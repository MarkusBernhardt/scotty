package de.scmb.scotty.web.rest;

import de.scmb.scotty.repository.ReconciliationRepository;
import de.scmb.scotty.repository.ReconciliationRepositoryExtended;
import de.scmb.scotty.service.ExcelService;
import de.scmb.scotty.service.criteria.PaymentCriteria;
import de.scmb.scotty.service.dto.PaymentsDownloadReconciliationsDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api/payments-download-reconciliations")
public class PaymentsDownloadReconciliations {

    private final ExcelService excelService;

    private final ReconciliationRepositoryExtended reconciliationRepositoryExtended;

    private final Logger log = LoggerFactory.getLogger(PaymentsDownloadReconciliations.class);

    public PaymentsDownloadReconciliations(ExcelService excelService, ReconciliationRepositoryExtended reconciliationRepositoryExtended) {
        this.excelService = excelService;
        this.reconciliationRepositoryExtended = reconciliationRepositoryExtended;
    }

    @GetMapping("/list")
    public ResponseEntity<List<PaymentsDownloadReconciliationsDto>> getAllGroupByFileName(
        PaymentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Payments by criteria: {}", criteria);

        Page<PaymentsDownloadReconciliationsDto> page = reconciliationRepositoryExtended.findAllGroupByFileName(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping(value = "/save")
    public ResponseEntity<StreamingResponseBody> save(@RequestParam(value = "fileName") String fileName)
        throws URISyntaxException, IOException {
        StreamingResponseBody stream = outputStream -> excelService.writeReconciliationsToStream(outputStream, fileName);
        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(stream);
    }
}
