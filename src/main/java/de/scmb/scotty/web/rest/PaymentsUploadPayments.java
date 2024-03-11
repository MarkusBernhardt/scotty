package de.scmb.scotty.web.rest;

import static de.scmb.scotty.service.ExcelService.COLUMNS;

import com.emerchantpay.gateway.GenesisClient;
import com.emerchantpay.gateway.api.Request;
import com.emerchantpay.gateway.api.TransactionResult;
import com.emerchantpay.gateway.api.constants.Endpoints;
import com.emerchantpay.gateway.api.constants.Environments;
import com.emerchantpay.gateway.api.requests.financial.sdd.SDDInitRecurringSaleRequest;
import com.emerchantpay.gateway.api.requests.financial.sdd.SDDRecurringSaleRequest;
import com.emerchantpay.gateway.model.Transaction;
import com.emerchantpay.gateway.util.Configuration;
import de.scmb.scotty.config.ApplicationProperties;
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.domain.enumeration.Gateway;
import de.scmb.scotty.repository.PaymentRepository;
import de.scmb.scotty.service.ExcelService;
import de.scmb.scotty.service.ExcelService.ColumnDescription;
import de.scmb.scotty.service.ExcelService.ColumnLevel;
import de.scmb.scotty.service.dto.PaymentsUploadPaymentsExecuteResponseDTO;
import de.scmb.scotty.service.dto.PaymentsUploadPaymentsProgressResponseDTO;
import de.scmb.scotty.service.dto.PaymentsUploadPaymentsValidateResponseDTO;
import jakarta.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api/payments-upload-payments")
public class PaymentsUploadPayments {

    private final ApplicationProperties applicationProperties;

    private final ExcelService excelService;

    private final PaymentRepository paymentRepository;

    private final Logger log = LoggerFactory.getLogger(PaymentsUploadPayments.class);

    public PaymentsUploadPayments(
        ApplicationProperties applicationProperties,
        ExcelService excelService,
        PaymentRepository paymentRepository
    ) {
        this.applicationProperties = applicationProperties;
        this.excelService = excelService;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping("/validate")
    public ResponseEntity<PaymentsUploadPaymentsValidateResponseDTO> validate(@Valid @RequestPart MultipartFile file) throws IOException {
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheet("payments");
            Row firstRow = sheet.getRow(0);

            int columnAmount = -1;
            for (int i = 0; i < COLUMNS.length; i++) {
                ColumnDescription columnDescription = COLUMNS[i];
                if (columnDescription.level != ColumnLevel.mandatory && columnDescription.level != ColumnLevel.mandatoryOnInit) {
                    continue;
                }
                boolean found = false;
                for (Cell cell : firstRow) {
                    String value = cell.getStringCellValue();
                    if (value.equals(columnDescription.name)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new IllegalArgumentException(String.format("Mandatory column \"%s\" not found in file.", columnDescription.name));
                }
                if (columnDescription.name.equals("amount")) {
                    columnAmount = i;
                }
            }

            double amount = 0;
            int count = 0;
            boolean first = true;
            for (Row row : sheet) {
                if (first) {
                    first = false;
                    continue;
                }
                amount += getIntCellValue(row, columnAmount);
                count++;
            }
            return ResponseEntity.ok().body(new PaymentsUploadPaymentsValidateResponseDTO(true, count, amount, null));
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
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheet("payments");

            Environments environment = Environments.STAGING;
            if (applicationProperties.getEmerchantpay().getEnvironment().equals("production")) {
                environment = Environments.PRODUCTION;
            }

            Configuration configuration = new Configuration(environment, Endpoints.EMERCHANTPAY);
            configuration.setUsername(applicationProperties.getEmerchantpay().getUsername());
            configuration.setPassword(applicationProperties.getEmerchantpay().getPassword());
            configuration.setToken(applicationProperties.getEmerchantpay().getToken());

            Font headerFont = workbook.createFont();
            headerFont.setFontName("Arial");
            headerFont.setBold(true);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFont(headerFont);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true);

            Map<String, Integer> columnIndices = new HashMap<>();
            int count = 0;
            int countFailed = 0;
            boolean first = true;
            for (Row row : sheet) {
                if (first) {
                    first = false;
                    int index = 0;
                    for (Cell cell : row) {
                        columnIndices.put(cell.getStringCellValue(), index);
                        index++;
                    }
                    continue;
                }

                count++;
                switch (row.getCell(columnIndices.get("gateway")).getStringCellValue()) {
                    case "emerchantpay" -> {
                        if (executeEmerchantpay(columnIndices, configuration, row, fileName)) countFailed++;
                    }
                    case "ccbill" -> {
                        if (executeCcbill()) countFailed++;
                    }
                    default -> {
                        if (executeUnknown(columnIndices, row, fileName)) countFailed++;
                    }
                }
                // TODO
                // Show message on client
                // Progressbar
                // Download file
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

    private Payment buildPayment(Map<String, Integer> columnIndices, Row row, Gateway gateway, String executeFileName) {
        Payment payment = new Payment();
        payment.setAmount(getIntCellValue(row, columnIndices.get("amount")));
        payment.setCurrencyCode(cutRight(getStringCellValue(columnIndices, row, "currencyCode"), 3));
        payment.setFirstName(cutRight(getStringCellValue(columnIndices, row, "firstName"), 35));
        payment.setLastName(cutRight(getStringCellValue(columnIndices, row, "lastName"), 35));
        payment.setCity(cutRight(getStringCellValue(columnIndices, row, "city"), 35));
        payment.setPostalCode(cutRight(getStringCellValue(columnIndices, row, "postalCode"), 16));
        payment.setAddressLine1(cutRight(getStringCellValue(columnIndices, row, "addressLine1"), 70));
        payment.setAddressLine2(cutRight(getStringCellValue(columnIndices, row, "addressLine2"), 70));
        payment.setCountryCode(cutRight(getStringCellValue(columnIndices, row, "countryCode"), 2));
        payment.setIban(cutRight(getStringCellValue(columnIndices, row, "iban"), 34));
        payment.setBic(cutRight(getStringCellValue(columnIndices, row, "bic"), 11));
        payment.setPaymentId(cutRight(getStringCellValue(columnIndices, row, "paymentId"), 35));
        payment.setSoftDescriptor(cutRight(getStringCellValue(columnIndices, row, "softDescriptor"), 140));
        payment.setRemoteIp(cutRight(getStringCellValue(columnIndices, row, "remoteIp"), 39));
        payment.setGateway(gateway);
        payment.setMandateId(cutRight(getStringCellValue(columnIndices, row, "mandateId"), 35));
        payment.setFileName(executeFileName);
        return payment;
    }

    private boolean executeEmerchantpay(Map<String, Integer> columnIndices, Configuration configuration, Row row, String executeFileName) {
        Payment payment = buildPayment(columnIndices, row, Gateway.EMERCHANTPAY, executeFileName);
        payment.setFileName(cutRight(executeFileName, 255));
        try {
            Request request;
            Payment init = paymentRepository.findFirstByMandateIdAndGatewayIdNotNullAndGatewayIdNotOrderByIdAsc(
                getStringCellValue(columnIndices, row, "mandateId"),
                ""
            );
            if (init == null) {
                request = getSddInitRecurringSaleRequest(payment);
            } else {
                request = getSddRecurringSaleRequest(payment, init);
                payment.setFirstName(init.getFirstName());
                payment.setLastName(init.getLastName());
                payment.setCity(init.getCity());
                payment.setPostalCode(init.getPostalCode());
                payment.setAddressLine1(init.getAddressLine1());
                payment.setAddressLine2(init.getAddressLine2());
                payment.setCountryCode(init.getCountryCode());
                payment.setIban(init.getIban());
                payment.setBic(init.getBic());
            }

            Payment duplicate = paymentRepository.findFirstByPaymentIdOrderByIdAsc(getStringCellValue(columnIndices, row, "paymentId"));
            if (duplicate != null) {
                payment.setState("failed");
                payment.setMessage(cutRight("Duplicate payment with id: " + duplicate.getId(), 255));
                payment.setTimestamp(Instant.now());
                payment.setGatewayId("");
                payment.setMode("");
                return true;
            }

            GenesisClient client = new GenesisClient(configuration, request);
            client.debugMode(true);
            client.execute();

            // Parse Payment result
            TransactionResult<? extends Transaction> result = client.getTransaction().getRequest();
            log.debug("Transaction Id: " + result.getTransaction().getTransactionId());

            payment.setMessage(cutRight(result.getTransaction().getMessage(), 255));
            payment.setGatewayId(cutRight(result.getTransaction().getUniqueId(), 35));
            payment.setMode(cutRight(result.getTransaction().getMode(), 35));
            payment.setState(cutRight(mapStateEmerchantpay(result.getTransaction().getStatus()), 35));
            payment.setTimestamp(Instant.parse(result.getTransaction().getTimestamp()));
        } catch (Throwable t) {
            payment.setState("failed");
            payment.setMessage(t.getMessage());
            payment.setTimestamp(Instant.now());
            payment.setGatewayId("");
            payment.setMode("");
            return true;
        } finally {
            paymentRepository.save(payment);
        }
        return false;
    }

    private String mapStateEmerchantpay(String state) {
        return switch (state) {
            case "approved" -> "paid";
            case "pending_async" -> "submitted";
            case "pending_hold", "pending_review", "pending" -> "pending";
            case "refunded" -> "refunded";
            case "chargebacked" -> "chargedBack";
            default -> "failed";
        };
    }

    private boolean executeCcbill() {
        return true;
    }

    private boolean executeUnknown(Map<String, Integer> columnIndices, Row row, String executeFileName) {
        Payment payment = buildPayment(columnIndices, row, Gateway.UNKNOWN, executeFileName);
        payment.setFileName(executeFileName);
        try {
            payment.setState("failed");
            payment.setMessage(cutRight("Unknown gateway: " + row.getCell(columnIndices.get("gateway")).getStringCellValue(), 255));
            payment.setTimestamp(Instant.now());
            payment.setGatewayId("");
            payment.setMode("");
        } finally {
            paymentRepository.save(payment);
        }
        return true;
    }

    private SDDRecurringSaleRequest getSddRecurringSaleRequest(Payment payment, Payment init) {
        SDDRecurringSaleRequest sddRecurringSaleRequest = new SDDRecurringSaleRequest();
        sddRecurringSaleRequest.setAmount(BigDecimal.valueOf(payment.getAmount() / 100d));
        sddRecurringSaleRequest.setCurrency(payment.getCurrencyCode());
        sddRecurringSaleRequest.setTransactionId(payment.getPaymentId());
        sddRecurringSaleRequest.setUsage(payment.getSoftDescriptor());
        sddRecurringSaleRequest.setRemoteIp(payment.getRemoteIp());
        sddRecurringSaleRequest.setReferenceId(init.getGatewayId());

        return sddRecurringSaleRequest;
    }

    private SDDInitRecurringSaleRequest getSddInitRecurringSaleRequest(Payment payment) {
        SDDInitRecurringSaleRequest sddInitRecurringSaleRequest = new SDDInitRecurringSaleRequest();
        sddInitRecurringSaleRequest.setAmount(BigDecimal.valueOf(payment.getAmount() / 100d));
        sddInitRecurringSaleRequest.setCurrency(payment.getCurrencyCode());
        sddInitRecurringSaleRequest.setBillingFirstname(payment.getFirstName());
        sddInitRecurringSaleRequest.setBillingLastname(payment.getLastName());
        sddInitRecurringSaleRequest.setBillingCity(payment.getCity());
        sddInitRecurringSaleRequest.setBillingZipCode(payment.getPostalCode());
        sddInitRecurringSaleRequest.setBillingPrimaryAddress(payment.getAddressLine1());
        sddInitRecurringSaleRequest.setBillingSecondaryAddress(payment.getAddressLine2());
        sddInitRecurringSaleRequest.setBillingCountry(payment.getCountryCode());
        sddInitRecurringSaleRequest.setIban(payment.getIban());
        sddInitRecurringSaleRequest.setBic(payment.getBic());
        sddInitRecurringSaleRequest.setTransactionId(payment.getPaymentId());
        sddInitRecurringSaleRequest.setUsage(payment.getSoftDescriptor());
        sddInitRecurringSaleRequest.setRemoteIp(payment.getRemoteIp());

        return sddInitRecurringSaleRequest;
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

    private String cutRight(String value, int length) {
        if (value == null) {
            return "";
        }

        value = value.trim();
        if (value.length() > length) {
            value = value.substring(0, length);
        }
        return value;
    }

    private int getIntCellValue(Row row, int columnInddex) {
        try {
            Cell cell = row.getCell(columnInddex);
            return switch (cell.getCellType()) {
                case STRING -> Integer.parseInt(cell.getStringCellValue());
                case NUMERIC -> (int) cell.getNumericCellValue();
                default -> throw new IllegalArgumentException();
            };
        } catch (Throwable t) {
            return 0;
        }
    }

    private String getStringCellValue(Map<String, Integer> columnIndices, Row row, String columnName) {
        try {
            Cell cell = row.getCell(columnIndices.get(columnName));
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> Integer.toString((int) cell.getNumericCellValue());
                default -> throw new IllegalArgumentException();
            };
        } catch (Throwable t) {
            return "";
        }
    }
}
