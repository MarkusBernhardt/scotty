package de.scmb.scotty.web.rest;

import de.scmb.scotty.repository.PaymentRepository;
import de.scmb.scotty.service.ExcelService;
import de.scmb.scotty.service.criteria.PaymentCriteria;
import de.scmb.scotty.service.dto.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api/payments-download-payments")
public class PaymentsDownloadPayments {

    private final ExcelService excelService;

    private final PaymentRepository paymentRepository;

    private final Logger log = LoggerFactory.getLogger(PaymentsDownloadPayments.class);

    public PaymentsDownloadPayments(ExcelService excelService, PaymentRepository paymentRepository) {
        this.excelService = excelService;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<List<PaymentsDownloadPaymentsDto>> getAllGroupByFileName(
        PaymentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Payments by criteria: {}", criteria);

        Page<PaymentsDownloadPaymentsDto> page = paymentRepository.findAllGroupByFileName(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping(value = "/save")
    public ResponseEntity<StreamingResponseBody> save(@RequestParam(value = "fileName") String fileName)
        throws URISyntaxException, IOException {
        StreamingResponseBody stream = outputStream -> {
            excelService.writePaymentsToStream(outputStream, fileName);
        };
        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(stream);
    }
}
