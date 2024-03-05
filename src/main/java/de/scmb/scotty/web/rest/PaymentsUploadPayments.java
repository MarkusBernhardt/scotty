package de.scmb.scotty.web.rest;

import com.emerchantpay.gateway.GenesisClient;
import com.emerchantpay.gateway.api.TransactionResult;
import com.emerchantpay.gateway.api.constants.Endpoints;
import com.emerchantpay.gateway.api.constants.Environments;
import com.emerchantpay.gateway.api.requests.financial.sdd.SDDInitRecurringSaleRequest;
import com.emerchantpay.gateway.api.requests.financial.sdd.SDDRecurringSaleRequest;
import com.emerchantpay.gateway.api.requests.financial.sdd.SDDSaleRequest;
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
import org.apache.poi.hpsf.GUID;
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

    private ApplicationProperties applicationProperties;

    public PaymentsUploadPayments(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    private final ColumnDescription[] COLUMNS = {
        new ColumnDescription(
            "idempotencyKey",
            ColumnLevel.MANDATORY,
            4711,
            "The unique idempotency key of this payment creation. It is checked whether a payment with the " +
            "specified idempotency key already exists, and the creation fails if a duplicate payment is found. " +
            "The Id of the conflicting payment can then be found in the error message. Max 35 characters."
        ),
        new ColumnDescription(
            "gateway",
            ColumnLevel.MANDATORY,
            "emerchantpay",
            "The name of the gateway this payment should be submitted to. Currently only \"emerchantpay\" " + "is supported."
        ),
        new ColumnDescription(
            "iban",
            ColumnLevel.MANDATORY,
            "DE02701500000000594937",
            "The customer's ISO 13616 international bank account number."
        ),
        new ColumnDescription("bic", ColumnLevel.MANDATORY, "SSKMDEMMXXX", "The customer's ISO 9362 business identifier code."),
        new ColumnDescription(
            "amount",
            ColumnLevel.MANDATORY,
            100,
            "The amount to be collected from the customer's bank account. Specified in the smallest " +
            "subunit of the used currency, e.g. cents for EUR."
        ),
        new ColumnDescription(
            "currencyCode",
            ColumnLevel.MANDATORY,
            "EUR",
            "The ISO 4217 currency code. Currently only \"EUR\" is supported."
        ),
        new ColumnDescription(
            "softDescriptor",
            ColumnLevel.MANDATORY,
            "Thank you for shopping at testmerchant. Your mandate is 78495098435.",
            "The soft descriptor for this payment. The text entered here will be printed on the bank " +
            "statements of the participating bank accounts, if supported in the used payment scheme. " +
            "Max 255 characters."
        ),
        new ColumnDescription("firstName", ColumnLevel.MANDATORY, "Max", "The customer's first name. Max 35 characters."),
        new ColumnDescription("lastName", ColumnLevel.MANDATORY, "Mustermann", "The customer's last name. Max 35 characters."),
        new ColumnDescription(
            "addressLine1",
            ColumnLevel.MANDATORY,
            "Karlsplatz 1",
            "The first line of the customer’s address. Max 70 characters."
        ),
        new ColumnDescription("addressLine2", ColumnLevel.OPTIONAL, "", "The second line of the customer’s address. Max 70 characters."),
        new ColumnDescription(
            "postalCode",
            ColumnLevel.MANDATORY,
            "80335",
            "The postal code of the customer’s address. Max 16 characters."
        ),
        new ColumnDescription("city", ColumnLevel.MANDATORY, "M\u00fcnchen", "The city of the customer’s address. Max 35 characters."),
        new ColumnDescription("countryCode", ColumnLevel.MANDATORY, "DE", "The ISO 3166-1 alpha-2 country code of the customer’s address."),
        new ColumnDescription("remoteIp", ColumnLevel.MANDATORY, "1.2.3.4", "IPv4 or IPv6 address of customer."),
        new ColumnDescription(
            "mandateReference",
            ColumnLevel.OPTIONAL,
            "",
            "The unique reference of this mandate. Length and format depend on the regulations defined " +
            "by the payment scheme.  If this field is not filled will a unique reference be " +
            "generated that meets the requirements of the payment scheme. Max 35 characters."
        ),
        new ColumnDescription("gatewayId", ColumnLevel.RETURN, "", "The unique id defined by the chosen gateway."),
        new ColumnDescription("status", ColumnLevel.RETURN, "", "The status of the payment."),
        new ColumnDescription("message", ColumnLevel.RETURN, "", "The human readable status message."),
        new ColumnDescription(
            "timestamp",
            ColumnLevel.RETURN,
            "",
            "The time when the transaction was processed in ISO 8601 combined date and time."
        ),
        new ColumnDescription("mode", ColumnLevel.RETURN, "", "The mode of the payment. Can be \"test\" of \"live\""),
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
                if (columnDescription.level != ColumnLevel.MANDATORY) {
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
                    throw new IllegalArgumentException();
                }
                if (columnDescription.name.equals("amount")) {
                    columnAmount = i;
                }
            }

            int amount = 0;
            int count = 0;
            boolean first = true;
            for (Row row : sheet) {
                if (first) {
                    first = false;
                    continue;
                }
                Cell cell = row.getCell(columnAmount);
                switch (cell.getCellType()) {
                    case STRING:
                        amount += Integer.parseInt(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        amount += (int) cell.getNumericCellValue();
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                count++;
            }
            return ResponseEntity.ok().body(new PaymentsUploadPaymentsValidateResponseDTO(true, count, amount));
        } catch (Throwable t) {
            return ResponseEntity.ok().body(new PaymentsUploadPaymentsValidateResponseDTO(false, 0, 0));
        }
    }

    @PostMapping("/execute")
    public ResponseEntity<PaymentsUploadPaymentsExecuteResponseDTO> execute(@Valid @RequestPart MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheet("payments");
        Row firstRow = sheet.getRow(0);

        Map<String, Integer> columnIndices = new HashMap<>();
        for (int i = 0; i < COLUMNS.length; i++) {
            ColumnDescription columnDescription = COLUMNS[i];
            columnIndices.put(columnDescription.name, i);
        }

        Configuration configuration = new Configuration(Environments.STAGING, Endpoints.EMERCHANTPAY);
        configuration.setUsername(applicationProperties.getEmerchantpay().getUsername());
        configuration.setPassword(applicationProperties.getEmerchantpay().getPassword());
        configuration.setToken(applicationProperties.getEmerchantpay().getToken());

        boolean first = true;
        for (Row row : sheet) {
            if (first) {
                first = false;
                continue;
            }
            // TODO
            // EMP testen
            // idempotencyKey testen
        }

        SDDInitRecurringSaleRequest sddInitRecurringSaleRequest = new SDDInitRecurringSaleRequest();
        sddInitRecurringSaleRequest.setAmount(new BigDecimal(100));
        sddInitRecurringSaleRequest.setCurrency("EUR");
        sddInitRecurringSaleRequest.setBillingFirstname("Markus");
        sddInitRecurringSaleRequest.setBillingLastname("Bernhardt");
        sddInitRecurringSaleRequest.setBillingCity("Riemerling");
        sddInitRecurringSaleRequest.setBillingZipCode("85521");
        sddInitRecurringSaleRequest.setBillingPrimaryAddress("Waldparkstr. 47a");
        sddInitRecurringSaleRequest.setBillingCountry("AT");
        sddInitRecurringSaleRequest.setIban("AT022050302101023600");
        sddInitRecurringSaleRequest.setBic("SPIHAT22XXX");
        sddInitRecurringSaleRequest.setTransactionId(new GUID().toString());
        sddInitRecurringSaleRequest.setUsage("Test mit EMP");
        sddInitRecurringSaleRequest.setRemoteIp("95.130.166.180");
        //sddInitRecurringSaleRequest.setBillingCountry("DE");
        //sddInitRecurringSaleRequest.setIban("DE02701500000115148348");
        //sddInitRecurringSaleRequest.setBic("SSKMDEMMXXX");

        GenesisClient client = new GenesisClient(configuration, sddInitRecurringSaleRequest);
        client.debugMode(true);
        client.execute();

        // Parse Payment result
        TransactionResult<? extends Transaction> result = client.getTransaction().getRequest();
        System.out.println("Transaction Id: " + result.getTransaction().getTransactionId());

        return ResponseEntity.ok().body(new PaymentsUploadPaymentsExecuteResponseDTO(2, ""));
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

        Row row = sheet.createRow(0);
        for (int i = 0; i < COLUMNS.length; i++) {
            ColumnDescription columnDescription = COLUMNS[i];
            if (columnDescription.level != ColumnLevel.MANDATORY) {
                continue;
            }

            Cell cell = row.createCell(i);
            cell.setCellValue(columnDescription.name);
            cell.setCellStyle(headerStyle);
        }

        int index = 0;
        row = sheet.createRow(1);
        for (ColumnDescription columnDescription : COLUMNS) {
            if (columnDescription.level != ColumnLevel.MANDATORY) {
                continue;
            }

            Cell cell = row.createCell(index);
            if (columnDescription.example instanceof Integer) {
                cell.setCellValue((Integer) columnDescription.example);
            } else {
                cell.setCellValue(columnDescription.example.toString());
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
        MANDATORY,
        OPTIONAL,
        RETURN,
    }

    private static class ColumnDescription {

        public String name;

        public ColumnLevel level;

        public Object example;

        public String description;

        public ColumnDescription(String name, ColumnLevel level, Object example, String description) {
            this.name = name;
            this.level = level;
            this.example = example;
            this.description = description;
        }
    }
}
