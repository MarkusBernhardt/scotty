package de.scmb.scotty.service;

import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.domain.enumeration.Gateway;
import de.scmb.scotty.repository.PaymentRepository;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelService {

    private final PaymentRepository paymentRepository;

    public static final ColumnDescription[] COLUMNS = {
        new ColumnDescription(
            "mandateId",
            ColumnLevel.mandatory,
            "The unique id of the mandate of this payment. Max 35 characters.",
            "",
            ""
        ),
        new ColumnDescription(
            "paymentId",
            ColumnLevel.mandatory,
            "The unique id of this payment creation. It is checked whether a payment with the " +
            "specified id already exists, and the creation fails, if a duplicate payment is found. " +
            "The Id of the conflicting payment can then be found in the error message. Max 35 characters.",
            "",
            ""
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
        new ColumnDescription("bic", ColumnLevel.mandatoryOnInit, "The customer's ISO 9362 business identifier code.", "MARKDEF1100", ""),
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
        new ColumnDescription("city", ColumnLevel.mandatoryOnInit, "The city of the customer’s address. Max 35 characters.", "München", ""),
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
        new ColumnDescription(
            "state",
            ColumnLevel.response,
            "The state of the payment. Currently are the following states possible: \"created\", " +
            "\"pending\", \"submitted\", \"paid\", \"chargedBack\", \"refunded\" and \"failed\"",
            "",
            ""
        ),
        new ColumnDescription("message", ColumnLevel.response, "The human readable message.", "", ""),
        new ColumnDescription("gatewayId", ColumnLevel.response, "The unique id defined by the chosen gateway.", "", ""),
        new ColumnDescription("mode", ColumnLevel.response, "The mode of the payment. Can be \"test\" of \"live\"", "", ""),
    };

    public ExcelService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public ValidationResult validatePaymentsFromStream(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
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

        return new ValidationResult(count, amount);
    }

    public List<Payment> readPaymentsFromStream(InputStream inputStream, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheet("payments");

        boolean first = true;
        List<Payment> payments = new ArrayList<>();
        Map<String, Integer> columnIndices = new HashMap<>();
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
                case "emerchantpay" -> {
                    payments.add(buildPayment(columnIndices, row, Gateway.EMERCHANTPAY, fileName));
                }
                case "ccbill" -> {
                    payments.add(buildPayment(columnIndices, row, Gateway.CCBILL, fileName));
                }
                default -> {
                    payments.add(buildPayment(columnIndices, row, Gateway.UNKNOWN, fileName));
                }
            }
        }

        return payments;
    }

    public void writePaymentsToStream(OutputStream outputStream, String fileName) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
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
            Map<String, Integer> columnIndices = new HashMap<>();
            for (ColumnDescription columnDescription : COLUMNS) {
                Cell cell = row.createCell(index);
                cell.setCellValue(columnDescription.name);
                cell.setCellStyle(headerStyle);
                columnIndices.put(columnDescription.name, index);
                index++;
            }

            index = 1;
            List<Payment> payments = paymentRepository.findAllByFileNameOrderByIdAsc(fileName);
            for (Payment payment : payments) {
                row = sheet.createRow(index);

                Cell cell = row.createCell(columnIndices.get("mandateId"));
                try {
                    cell.setCellValue(Integer.parseInt(payment.getMandateId()));
                } catch (Throwable t) {
                    cell.setCellValue(payment.getMandateId());
                }
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("paymentId"));
                try {
                    cell.setCellValue(Integer.parseInt(payment.getPaymentId()));
                } catch (Throwable t) {
                    cell.setCellValue(payment.getPaymentId());
                }
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("gateway"));
                cell.setCellValue(payment.getGateway().toString());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("iban"));
                cell.setCellValue(payment.getIban());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("bic"));
                cell.setCellValue(payment.getBic());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("amount"));
                cell.setCellValue(payment.getAmount());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("currencyCode"));
                cell.setCellValue(payment.getCurrencyCode());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("softDescriptor"));
                cell.setCellValue(payment.getSoftDescriptor());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("firstName"));
                cell.setCellValue(payment.getFirstName());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("lastName"));
                cell.setCellValue(payment.getLastName());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("addressLine1"));
                cell.setCellValue(payment.getAddressLine1());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("addressLine2"));
                cell.setCellValue(payment.getAddressLine2());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("postalCode"));
                try {
                    cell.setCellValue(Integer.parseInt(payment.getPostalCode()));
                } catch (Throwable t) {
                    cell.setCellValue(payment.getPostalCode());
                }
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("city"));
                cell.setCellValue(payment.getCity());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("countryCode"));
                cell.setCellValue(payment.getCountryCode());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("remoteIp"));
                cell.setCellValue(payment.getRemoteIp());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("scottyId"));
                cell.setCellValue(payment.getId());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("state"));
                cell.setCellValue(payment.getState());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("timestamp"));
                cell.setCellValue(payment.getTimestamp().toString());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("gatewayId"));
                cell.setCellValue(payment.getGatewayId());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("message"));
                cell.setCellValue(payment.getMessage());
                cell.setCellStyle(cellStyle);

                cell = row.createCell(columnIndices.get("mode"));
                cell.setCellValue(payment.getMode());
                cell.setCellStyle(cellStyle);

                index++;
            }

            index = 0;
            for (ColumnDescription ignored : COLUMNS) {
                sheet.autoSizeColumn(index);
                index++;
            }

            workbook.write(outputStream);
        }
    }

    public void writeExampleToStream(OutputStream outputStream) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
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

            for (int i = 0; i < 10; i++) {
                index = 0;
                row = sheet.createRow(i + 1);
                for (ColumnDescription columnDescription : COLUMNS) {
                    if (columnDescription.level != ColumnLevel.mandatory && columnDescription.level != ColumnLevel.mandatoryOnInit) {
                        continue;
                    }

                    Cell cell = row.createCell(index);
                    if (columnDescription.name.equals("mandateId") || columnDescription.name.equals("paymentId")) {
                        cell.setCellValue(UUID.randomUUID().toString().replace("-", ""));
                    } else if (columnDescription.example[0] instanceof Integer) {
                        cell.setCellValue((Integer) columnDescription.example[0]);
                    } else {
                        cell.setCellValue(columnDescription.example[0].toString());
                    }
                    cell.setCellStyle(cellStyle);

                    index++;
                }
            }

            for (int i = 0; i < 10; i++) {
                index = 0;
                row = sheet.createRow(i + 11);
                for (ColumnDescription columnDescription : COLUMNS) {
                    if (columnDescription.level != ColumnLevel.mandatory && columnDescription.level != ColumnLevel.mandatoryOnInit) {
                        continue;
                    }

                    Cell cell = row.createCell(index);
                    if (columnDescription.name.equals("mandateId") || columnDescription.name.equals("paymentId")) {
                        cell.setCellValue(sheet.getRow(i + 1).getCell(index).getStringCellValue());
                    } else if (columnDescription.example[1] instanceof Integer) {
                        cell.setCellValue((Integer) columnDescription.example[1]);
                    } else {
                        cell.setCellValue(columnDescription.example[1].toString());
                    }
                    cell.setCellStyle(cellStyle);

                    index++;
                }
            }

            index = 0;
            for (ColumnDescription ignored : COLUMNS) {
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
        }
    }

    public static Payment buildPayment(Map<String, Integer> columnIndices, Row row, Gateway gateway, String fileName) {
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
        payment.setFileName(cutRight(fileName, 255));
        return payment;
    }

    public static int getIntCellValue(Row row, int columnInddex) {
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

    public static String getStringCellValue(Map<String, Integer> columnIndices, Row row, String columnName) {
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

    public static String cutRight(String value, int length) {
        if (value == null) {
            return "";
        }

        value = value.trim();
        if (value.length() > length) {
            value = value.substring(0, length);
        }
        return value;
    }

    public enum ColumnLevel {
        mandatory,
        optional,
        mandatoryOnInit,
        optionalOnInit,
        response,
    }

    public static class ColumnDescription {

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

    public static class ValidationResult {

        public int count;

        public double amount;

        public ValidationResult(int count, double amount) {
            this.count = count;
            this.amount = amount;
        }
    }
}
