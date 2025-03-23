package de.scmb.scotty.web.rest;

import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.gateway.emerchantpay.EmerchantpayService;
import de.scmb.scotty.gateway.novalnet.NovalnetService;
import de.scmb.scotty.gateway.openpayd.OpenPaydService;
import de.scmb.scotty.repository.PaymentRepositoryExtended;
import de.scmb.scotty.service.ExcelService;
import de.scmb.scotty.service.XmlService;
import de.scmb.scotty.service.dto.PaymentsUploadPaymentsExecuteResponseDTO;
import de.scmb.scotty.service.dto.PaymentsUploadPaymentsProgressResponseDTO;
import de.scmb.scotty.service.dto.PaymentsUploadPaymentsValidateResponseDTO;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api/payments-upload-payments")
public class PaymentsUploadPayments {

    private final ExcelService excelService;

    private final EmerchantpayService emerchantpayService;

    private final NovalnetService novalnetService;

    private final OpenPaydService openPaydService;

    private final PaymentRepositoryExtended paymentRepositoryExtended;

    private final XmlService xmlService;

    private final TaskExecutor taskExecutor;

    private final Map<String, String> deBlz2Bic;

    private final Logger log = LoggerFactory.getLogger(PaymentsUploadPayments.class);

    private static final Set<String> executionTasks = ConcurrentHashMap.newKeySet();

    public PaymentsUploadPayments(
        ExcelService excelService,
        EmerchantpayService emerchantpayService,
        NovalnetService novalnetService,
        OpenPaydService openPaydService,
        PaymentRepositoryExtended paymentRepositoryExtended,
        XmlService xmlService,
        @Qualifier("taskExecutor") TaskExecutor taskExecutor,
        @Qualifier("deBlz2Bic") Map<String, String> deBlz2Bic
    ) {
        this.excelService = excelService;
        this.emerchantpayService = emerchantpayService;
        this.novalnetService = novalnetService;
        this.openPaydService = openPaydService;
        this.paymentRepositoryExtended = paymentRepositoryExtended;
        this.xmlService = xmlService;
        this.taskExecutor = taskExecutor;
        this.deBlz2Bic = deBlz2Bic;
    }

    @PostMapping("/validate")
    public ResponseEntity<PaymentsUploadPaymentsValidateResponseDTO> validate(@Valid @RequestPart MultipartFile file) throws IOException {
        try {
            ValidationResult validationResult = null;
            String contentType = file.getContentType();
            if (MediaType.TEXT_XML_VALUE.equals(contentType) || MediaType.APPLICATION_XML_VALUE.equals(contentType)) {
                validationResult = xmlService.validatePaymentsFromStream(file.getInputStream());
            } else {
                validationResult = excelService.validatePaymentsFromStream(file.getInputStream());
            }
            return ResponseEntity.ok()
                .body(new PaymentsUploadPaymentsValidateResponseDTO(true, validationResult.count, validationResult.amount, null));
        } catch (Throwable t) {
            return ResponseEntity.ok().body(new PaymentsUploadPaymentsValidateResponseDTO(false, 0, 0, t.getMessage()));
        }
    }

    @PostMapping("/execute")
    public ResponseEntity<PaymentsUploadPaymentsExecuteResponseDTO> execute(
        @Valid @RequestPart MultipartFile file,
        @Valid @RequestPart String fileName
    ) throws IOException {
        taskExecutor.execute(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        executionTasks.add(fileName);

                        List<Payment> payments = null;
                        String contentType = file.getContentType();
                        if (MediaType.TEXT_XML_VALUE.equals(contentType) || MediaType.APPLICATION_XML_VALUE.equals(contentType)) {
                            payments = xmlService.readPaymentsFromStream(file.getInputStream(), fileName);
                        } else {
                            payments = excelService.readPaymentsFromStream(file.getInputStream(), fileName);
                        }

                        for (Payment payment : payments) {
                            String iban = payment.getIban();
                            if (payment.getBic().trim().isEmpty() && iban.startsWith("DE") && iban.length() == 22) {
                                String bic = deBlz2Bic.get(iban.substring(4, 12));
                                if (bic == null) {
                                    bic = "";
                                }
                                payment.setBic(bic);
                            }

                            if (payment.getBic().trim().isEmpty()) {
                                payment.setBic("NOTFOUNDXXX");
                                executeUnknownBic(payment);
                                continue;
                            }

                            switch (payment.getGateway()) {
                                case EMERCHANTPAY -> {
                                    emerchantpayService.execute(payment);
                                }
                                case NOVALNET -> {
                                    novalnetService.execute(payment);
                                }
                                case OPENPAYD -> {
                                    openPaydService.execute(payment);
                                }
                                default -> {
                                    executeUnknowngateway(payment);
                                }
                            }
                        }
                    } catch (Throwable t) {
                        log.error(t.getMessage(), t);
                    } finally {
                        executionTasks.remove(fileName);
                    }
                }
            }
        );

        return ResponseEntity.ok().body(new PaymentsUploadPaymentsExecuteResponseDTO(true));
    }

    @GetMapping(value = "/progress")
    public ResponseEntity<PaymentsUploadPaymentsProgressResponseDTO> progress(@RequestParam(value = "fileName") String fileName)
        throws URISyntaxException, IOException {
        int countFailed = 0;
        List<Payment> payments = paymentRepositoryExtended.findAllByFileNameOrderByIdAsc(fileName);
        for (Payment payment : payments) {
            if (payment.getState().equals("failed")) {
                countFailed++;
            }
        }
        int success = 2;
        int count = payments.size();
        if (countFailed == 0) {
            success = 3;
        } else if (countFailed == count) {
            success = 1;
        }
        return ResponseEntity.ok().body(new PaymentsUploadPaymentsProgressResponseDTO(success, count, executionTasks.contains(fileName)));
    }

    private void executeUnknowngateway(Payment payment) {
        try {
            payment.setState("failed");
            payment.setMessage("Unknown gateway");
            payment.setTimestamp(Instant.now());
            payment.setGatewayId("");
            payment.setMode("");
        } finally {
            paymentRepositoryExtended.save(payment);
        }
    }

    private void executeUnknownBic(Payment payment) {
        try {
            payment.setState("failed");
            payment.setMessage("BIC not provided and not found by IBAN");
            payment.setTimestamp(Instant.now());
            payment.setGatewayId("");
            payment.setMode("");
        } finally {
            paymentRepositoryExtended.save(payment);
        }
    }

    @GetMapping(value = "/save")
    public ResponseEntity<StreamingResponseBody> save(@RequestParam(value = "fileName") String fileName)
        throws URISyntaxException, IOException {
        StreamingResponseBody stream = outputStream -> excelService.writePaymentsToStream(outputStream, fileName);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(stream);
    }

    @GetMapping(value = "/example")
    public ResponseEntity<StreamingResponseBody> example() throws URISyntaxException, IOException {
        StreamingResponseBody stream = excelService::writeExampleToStream;
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payments.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(stream);
    }

    public static class ValidationResult {

        public int count;

        public double amount;

        public ValidationResult(int count, double amount) {
            this.count = count;
            this.amount = amount;
        }
    }
}
