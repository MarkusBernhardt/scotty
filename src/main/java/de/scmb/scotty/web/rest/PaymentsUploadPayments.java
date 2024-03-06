package de.scmb.scotty.web.rest;

import com.emerchantpay.gateway.GenesisClient;
import com.emerchantpay.gateway.api.TransactionResult;
import com.emerchantpay.gateway.api.constants.Endpoints;
import com.emerchantpay.gateway.api.constants.Environments;
import com.emerchantpay.gateway.api.requests.financial.sdd.SDDInitRecurringSaleRequest;
import com.emerchantpay.gateway.api.requests.financial.sdd.SDDRecurringSaleRequest;
import com.emerchantpay.gateway.model.Transaction;
import com.emerchantpay.gateway.util.Configuration;
import de.scmb.scotty.config.ApplicationProperties;
import de.scmb.scotty.service.dto.PaymentsUploadPaymentsExecuteResponseDTO;
import de.scmb.scotty.service.dto.PaymentsUploadPaymentsValidateResponseDTO;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    public PaymentsUploadPayments(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    private final ColumnDescription[] COLUMNS = {
        new ColumnDescription(
            "mandateId",
            ColumnLevel.mandatory,
            "The unique id of the mandate of this payment. Max 35 characters.",
            "mandate-00000002",
            "mandate-00000002"
        ),
        new ColumnDescription(
            "paymentId",
            ColumnLevel.mandatory,
            "The unique id of this payment creation. It is checked whether a payment with the " +
            "specified id already exists, and the creation fails, if a duplicate payment is found. " +
            "The Id of the conflicting payment can then be found in the error message. Max 35 characters.",
            "payment-00000201",
            "payment-00000202"
        ),
        new ColumnDescription(
            "gateway",
            ColumnLevel.mandatory,
            "The name of the gateway this payment should be submitted to. Currently only \"emerchantpay\" " + "is supported.",
            "emerchantpay",
            "emerchantpay"
        ),
        new ColumnDescription(
            "iban",
            ColumnLevel.mandatoryOnInit,
            "The customer's ISO 13616 international bank account number.",
            "DE91100000001234400020",
            ""
        ),
        new ColumnDescription("bic", ColumnLevel.mandatoryOnInit, "The customer's ISO 9362 business identifier code.", "SSKMDEMMXXX", ""),
        new ColumnDescription(
            "amount",
            ColumnLevel.mandatory,
            "The amount to be collected from the customer's bank account. Specified in the smallest " +
            "subunit of the used currency, e.g. cents for EUR.",
            111,
            123
        ),
        new ColumnDescription(
            "currencyCode",
            ColumnLevel.mandatory,
            "The ISO 4217 currency code. Currently only \"EUR\" is supported.",
            "EUR",
            "EUR"
        ),
        new ColumnDescription(
            "softDescriptor",
            ColumnLevel.mandatory,
            "The soft descriptor for this payment. The text entered here will be printed on the bank " +
            "statements of the participating bank accounts, if supported in the used payment scheme. " +
            "Max 140 characters.",
            "Thank you for shopping at testmerchant. Your mandate is 00000001.",
            "Thank you for shopping at testmerchant. Your mandate is 00000001."
        ),
        new ColumnDescription("firstName", ColumnLevel.mandatoryOnInit, "The customer's first name. Max 35 characters.", "Max", ""),
        new ColumnDescription("lastName", ColumnLevel.mandatoryOnInit, "The customer's last name. Max 35 characters.", "Mustermann", ""),
        new ColumnDescription(
            "addressLine1",
            ColumnLevel.mandatoryOnInit,
            "The first line of the customer’s address. Max 70 characters.",
            "Karlsplatz 1",
            ""
        ),
        new ColumnDescription(
            "addressLine2",
            ColumnLevel.optionalOnInit,
            "The second line of the customer’s address. Max 70 characters.",
            "",
            ""
        ),
        new ColumnDescription(
            "postalCode",
            ColumnLevel.mandatoryOnInit,
            "The postal code of the customer’s address. Max 16 characters.",
            "80335",
            ""
        ),
        new ColumnDescription(
            "city",
            ColumnLevel.mandatoryOnInit,
            "The city of the customer’s address. Max 35 characters.",
            "M\u00fcnchen",
            ""
        ),
        new ColumnDescription(
            "countryCode",
            ColumnLevel.mandatoryOnInit,
            "The ISO 3166-1 alpha-2 country code of the customer’s address.",
            "DE",
            ""
        ),
        new ColumnDescription("remoteIp", ColumnLevel.mandatory, "IPv4 or IPv6 address of customer.", "1.2.3.4", "1.2.3.4"),
        new ColumnDescription("scottyId", ColumnLevel.response, "The unique id defined by the Scotty.", "", ""),
        new ColumnDescription(
            "timestamp",
            ColumnLevel.response,
            "The time when the transaction was processed in ISO 8601 combined date and time.",
            "",
            ""
        ),
        new ColumnDescription("status", ColumnLevel.response, "The status of the payment.", "", ""),
        new ColumnDescription("message", ColumnLevel.response, "The human readable status message.", "", ""),
        new ColumnDescription("gatewayId", ColumnLevel.response, "The unique id defined by the chosen gateway.", "", ""),
        new ColumnDescription("gatewayCode", ColumnLevel.response, "The error code defined by the chosen gateway.", "", ""),
        new ColumnDescription("mode", ColumnLevel.response, "The mode of the payment. Can be \"test\" of \"live\"", "", ""),
    };

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
                amount += getAmount(row, columnAmount);
                count++;
            }
            return ResponseEntity.ok().body(new PaymentsUploadPaymentsValidateResponseDTO(true, count, amount, null));
        } catch (Throwable t) {
            return ResponseEntity.ok().body(new PaymentsUploadPaymentsValidateResponseDTO(false, 0, 0, t.getMessage()));
        }
    }

    @PostMapping("/execute")
    public ResponseEntity<PaymentsUploadPaymentsExecuteResponseDTO> execute(@Valid @RequestPart MultipartFile file) throws IOException {
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheet("payments");
            Row firstRow = sheet.getRow(0);

            Configuration configuration = new Configuration(Environments.STAGING, Endpoints.EMERCHANTPAY);
            configuration.setUsername(applicationProperties.getEmerchantpay().getUsername());
            configuration.setPassword(applicationProperties.getEmerchantpay().getPassword());
            configuration.setToken(applicationProperties.getEmerchantpay().getToken());

            Map<String, Integer> columnIndices = new HashMap<>();
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

                switch (row.getCell(columnIndices.get("gateway")).getStringCellValue()) {
                    case "emerchantpay":
                        executeEmerchantpay(columnIndices, configuration, row);
                        break;
                    default:
                        throw new IllegalArgumentException(
                            String.format("Unknown gateway \"%s\"", row.getCell(columnIndices.get("gateway")).getStringCellValue())
                        );
                }
                // TODO
                // idempotencyKey testen
                // Show message on client
                // Progressbar
                // Id
                // Add columns
                // Download file
            }

            return ResponseEntity.ok().body(new PaymentsUploadPaymentsExecuteResponseDTO(3, "", null));
        } catch (Throwable t) {
            return ResponseEntity.ok().body(new PaymentsUploadPaymentsExecuteResponseDTO(1, "", t.getMessage()));
        }
    }

    private void executeEmerchantpay(Map<String, Integer> columnIndices, Configuration configuration, Row row) {
        SDDInitRecurringSaleRequest sddInitRecurringSaleRequest = new SDDInitRecurringSaleRequest();
        sddInitRecurringSaleRequest.setAmount(BigDecimal.valueOf(getAmount(row, columnIndices.get("amount"))));
        sddInitRecurringSaleRequest.setCurrency(row.getCell(columnIndices.get("currencyCode")).getStringCellValue());
        sddInitRecurringSaleRequest.setBillingFirstname(row.getCell(columnIndices.get("firstName")).getStringCellValue());
        sddInitRecurringSaleRequest.setBillingLastname(row.getCell(columnIndices.get("lastName")).getStringCellValue());
        sddInitRecurringSaleRequest.setBillingCity(row.getCell(columnIndices.get("city")).getStringCellValue());
        sddInitRecurringSaleRequest.setBillingZipCode(row.getCell(columnIndices.get("postalCode")).getStringCellValue());
        sddInitRecurringSaleRequest.setBillingPrimaryAddress(row.getCell(columnIndices.get("addressLine1")).getStringCellValue());
        sddInitRecurringSaleRequest.setBillingCountry(row.getCell(columnIndices.get("countryCode")).getStringCellValue());
        sddInitRecurringSaleRequest.setIban(row.getCell(columnIndices.get("iban")).getStringCellValue());
        sddInitRecurringSaleRequest.setBic(row.getCell(columnIndices.get("bic")).getStringCellValue());
        sddInitRecurringSaleRequest.setTransactionId(row.getCell(columnIndices.get("paymentId")).getStringCellValue() + "61");
        sddInitRecurringSaleRequest.setUsage(row.getCell(columnIndices.get("softDescriptor")).getStringCellValue());
        sddInitRecurringSaleRequest.setRemoteIp(row.getCell(columnIndices.get("remoteIp")).getStringCellValue());

        GenesisClient client = new GenesisClient(configuration, sddInitRecurringSaleRequest);
        client.debugMode(true);
        client.execute();

        // Parse Payment result
        TransactionResult<? extends Transaction> result = client.getTransaction().getRequest();
        System.out.println("Transaction Id: " + result.getTransaction().getTransactionId());

        SDDRecurringSaleRequest sddRecurringSaleRequest = new SDDRecurringSaleRequest();
        sddRecurringSaleRequest.setAmount(BigDecimal.valueOf(getAmount(row, columnIndices.get("amount")) * 2));
        sddRecurringSaleRequest.setCurrency(row.getCell(columnIndices.get("currencyCode")).getStringCellValue());
        sddRecurringSaleRequest.setTransactionId(row.getCell(columnIndices.get("paymentId")).getStringCellValue() + "62");
        sddRecurringSaleRequest.setUsage(row.getCell(columnIndices.get("softDescriptor")).getStringCellValue());
        sddRecurringSaleRequest.setRemoteIp(row.getCell(columnIndices.get("remoteIp")).getStringCellValue());
        sddRecurringSaleRequest.setReferenceId(result.getTransaction().getUniqueId());

        client = new GenesisClient(configuration, sddRecurringSaleRequest);
        client.debugMode(true);
        client.execute();

        result = client.getTransaction().getRequest();
        System.out.println("Transaction Id: " + result.getTransaction().getTransactionId());

        sddRecurringSaleRequest = new SDDRecurringSaleRequest();
        sddRecurringSaleRequest.setAmount(BigDecimal.valueOf(getAmount(row, columnIndices.get("amount")) * 3));
        sddRecurringSaleRequest.setCurrency(row.getCell(columnIndices.get("currencyCode")).getStringCellValue());
        sddRecurringSaleRequest.setTransactionId(row.getCell(columnIndices.get("paymentId")).getStringCellValue() + "63");
        sddRecurringSaleRequest.setUsage(row.getCell(columnIndices.get("softDescriptor")).getStringCellValue());
        sddRecurringSaleRequest.setRemoteIp(row.getCell(columnIndices.get("remoteIp")).getStringCellValue());
        sddRecurringSaleRequest.setReferenceId(result.getTransaction().getUniqueId());

        client = new GenesisClient(configuration, sddRecurringSaleRequest);
        client.debugMode(true);
        client.execute();

        result = client.getTransaction().getRequest();
        System.out.println("Transaction Id: " + result.getTransaction().getTransactionId());
    }

    private double getAmount(Row row, int columnAmount) {
        Cell cell = row.getCell(columnAmount);
        return switch (cell.getCellType()) {
            case STRING -> Double.parseDouble(cell.getStringCellValue()) / 100d;
            case NUMERIC -> cell.getNumericCellValue() / 100d;
            default -> throw new IllegalArgumentException();
        };
    }

    @GetMapping(value = "/example")
    public ResponseEntity<StreamingResponseBody> example() throws URISyntaxException, IOException {
        StreamingResponseBody stream = this::writeExampleToStream;
        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payments.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(stream);
    }

    private void writeExampleToStream(OutputStream outputStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontName("Arial");
        headerFont.setBold(true);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(headerFont);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);

        Sheet sheet = workbook.createSheet("payments");

        int index = 0;
        Row row = sheet.createRow(0);
        for (ColumnDescription columnDescription : COLUMNS) {
            if (columnDescription.level != ColumnLevel.mandatory && columnDescription.level != ColumnLevel.mandatoryOnInit) {
                continue;
            }

            Cell cell = row.createCell(index);
            cell.setCellValue(columnDescription.name);
            cell.setCellStyle(headerStyle);
            index++;
        }

        index = 0;
        Row row1 = sheet.createRow(1);
        Row row2 = sheet.createRow(2);
        for (ColumnDescription columnDescription : COLUMNS) {
            if (columnDescription.level != ColumnLevel.mandatory && columnDescription.level != ColumnLevel.mandatoryOnInit) {
                continue;
            }

            Cell cell = row1.createCell(index);
            if (columnDescription.example[0] instanceof Integer) {
                cell.setCellValue((Integer) columnDescription.example[0]);
            } else {
                cell.setCellValue(columnDescription.example[0].toString());
            }
            cell.setCellStyle(cellStyle);

            cell = row2.createCell(index);
            if (columnDescription.example[1] instanceof Integer) {
                cell.setCellValue((Integer) columnDescription.example[1]);
            } else {
                cell.setCellValue(columnDescription.example[1].toString());
            }
            cell.setCellStyle(cellStyle);

            sheet.autoSizeColumn(index);
            index++;
        }

        sheet = workbook.createSheet("description");

        row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("name");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(1);
        cell.setCellValue("type");
        cell.setCellStyle(headerStyle);

        cell = row.createCell(2);
        cell.setCellValue("description");
        cell.setCellStyle(headerStyle);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.setColumnWidth(2, 40000);

        for (int i = 0; i < COLUMNS.length; i++) {
            ColumnDescription columnDescription = COLUMNS[i];

            row = sheet.createRow(i + 1);
            cell = row.createCell(0);
            cell.setCellValue(columnDescription.name);
            cell.setCellStyle(cellStyle);

            cell = row.createCell(1);
            cell.setCellValue(columnDescription.level.toString());
            cell.setCellStyle(cellStyle);

            cell = row.createCell(2);
            cell.setCellValue(columnDescription.description);
            cell.setCellStyle(cellStyle);
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.setColumnWidth(2, 20000);

        workbook.write(outputStream);
        workbook.close();
    }

    private static enum ColumnLevel {
        mandatory,
        optional,
        mandatoryOnInit,
        optionalOnInit,
        response,
    }

    private static class ColumnDescription {

        public String name;

        public ColumnLevel level;

        public Object[] example;

        public String description;

        public ColumnDescription(String name, ColumnLevel level, String description, Object... example) {
            this.name = name;
            this.level = level;
            this.description = description;
            this.example = example;
        }
    }
}
