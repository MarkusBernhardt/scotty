package de.scmb.scotty.web.rest;

import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.repository.PaymentRepository;
import de.scmb.scotty.service.EmerchantpayService;
import de.scmb.scotty.service.ExcelService;
import de.scmb.scotty.service.NovalnetService;
import de.scmb.scotty.service.dto.PaymentsUploadPaymentsExecuteResponseDTO;
import de.scmb.scotty.service.dto.PaymentsUploadPaymentsProgressResponseDTO;
import de.scmb.scotty.service.dto.PaymentsUploadPaymentsValidateResponseDTO;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
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
    private final PaymentRepository paymentRepository;

    private final TaskExecutor taskExecutor;

    private final Logger log = LoggerFactory.getLogger(PaymentsUploadPayments.class);

    private static final Set<String> executionTasks = ConcurrentHashMap.newKeySet();

    public PaymentsUploadPayments(
        ExcelService excelService,
        EmerchantpayService emerchantpayService,
        NovalnetService novalnetService,
        PaymentRepository paymentRepository,
        @Qualifier("taskExecutor") TaskExecutor taskExecutor
    ) {
        this.excelService = excelService;
        this.emerchantpayService = emerchantpayService;
        this.novalnetService = novalnetService;
        this.paymentRepository = paymentRepository;
        this.taskExecutor = taskExecutor;
    }

    @PostMapping("/validate")
    public ResponseEntity<PaymentsUploadPaymentsValidateResponseDTO> validate(@Valid @RequestPart MultipartFile file) throws IOException {
        try {
            ExcelService.ValidationResult validationResult = excelService.validatePaymentsFromStream(file.getInputStream());
            return ResponseEntity
                .ok()
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

                        List<Payment> payments = excelService.readPaymentsFromStream(file.getInputStream(), fileName);

                        for (Payment payment : payments) {
                            switch (payment.getGateway()) {
                                case CCBILL -> {
                                    executeCcbill();
                                }
                                case EMERCHANTPAY -> {
                                    emerchantpayService.execute(payment);
                                }
                                case NOVALNET -> {
                                    novalnetService.execute(payment);
                                }
                                default -> {
                                    executeUnknown(payment);
                                }
                            }
                        }
                    } catch (Throwable t) {
                        log.warn(t.getMessage());
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
        List<Payment> payments = paymentRepository.findAllByFileNameOrderByIdAsc(fileName);
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

    private void executeCcbill() {}

    private void executeUnknown(Payment payment) {
        try {
            payment.setState("failed");
            payment.setMessage("Unknown gateway");
            payment.setTimestamp(Instant.now());
            payment.setGatewayId("");
            payment.setMode("");
        } finally {
            paymentRepository.save(payment);
        }
    }

    @GetMapping(value = "/save")
    public ResponseEntity<StreamingResponseBody> save(@RequestParam(value = "fileName") String fileName)
        throws URISyntaxException, IOException {
        StreamingResponseBody stream = outputStream -> excelService.writePaymentsToStream(outputStream, fileName);
        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(stream);
    }

    @GetMapping(value = "/example")
    public ResponseEntity<StreamingResponseBody> example() throws URISyntaxException, IOException {
        StreamingResponseBody stream = excelService::writeExampleToStream;
        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payments.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(stream);
    }
}
