package de.scmb.scotty.web.rest;

import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.repository.PaymentRepository;
import de.scmb.scotty.service.EmerchantpayService;
import de.scmb.scotty.service.ExcelService;
import de.scmb.scotty.service.dto.PaymentsUploadPaymentsExecuteResponseDTO;
import de.scmb.scotty.service.dto.PaymentsUploadPaymentsProgressResponseDTO;
import de.scmb.scotty.service.dto.PaymentsUploadPaymentsValidateResponseDTO;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
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

    private final PaymentRepository paymentRepository;

    public PaymentsUploadPayments(ExcelService excelService, EmerchantpayService emerchantpayService, PaymentRepository paymentRepository) {
        this.excelService = excelService;
        this.emerchantpayService = emerchantpayService;
        this.paymentRepository = paymentRepository;
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
        try {
            List<Payment> payments = excelService.readPaymentsFromStream(file.getInputStream(), fileName);
            int count = 0;
            int countFailed = 0;
            for (Payment payment : payments) {
                count++;
                switch (payment.getGateway()) {
                    case EMERCHANTPAY -> {
                        if (emerchantpayService.execute(payment)) countFailed++;
                    }
                    case CCBILL -> {
                        if (executeCcbill()) countFailed++;
                    }
                    default -> {
                        if (executeUnknown(payment)) countFailed++;
                    }
                }
            }

            int success = 2;
            if (countFailed == 0) {
                success = 3;
            } else if (countFailed == count) {
                success = 1;
            }

            return ResponseEntity.ok().body(new PaymentsUploadPaymentsExecuteResponseDTO(success, null));
        } catch (Throwable t) {
            return ResponseEntity.ok().body(new PaymentsUploadPaymentsExecuteResponseDTO(1, t.getMessage()));
        }
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
        return ResponseEntity.ok().body(new PaymentsUploadPaymentsProgressResponseDTO(success, count));
    }

    private boolean executeCcbill() {
        return true;
    }

    private boolean executeUnknown(Payment payment) {
        try {
            payment.setState("failed");
            payment.setMessage("Unknown gateway");
            payment.setTimestamp(Instant.now());
            payment.setGatewayId("");
            payment.setMode("");
        } finally {
            paymentRepository.save(payment);
        }
        return true;
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
