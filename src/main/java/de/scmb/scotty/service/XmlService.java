package de.scmb.scotty.service;

import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.domain.enumeration.Gateway;
import de.scmb.scotty.web.rest.PaymentsUploadPayments;
import iso.std.iso._20022.tech.xsd.pain_008_001.*;
import jakarta.validation.Valid;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class XmlService {

    public PaymentsUploadPayments.ValidationResult validatePaymentsFromStream(InputStream inputStream) throws IOException, JAXBException {
        String xml = readInputStreamToString(inputStream);
        if (xml.indexOf("pain.008.001.02", 0, Math.min(xml.length(), 1000)) != -1) {
            return validatePain00800102FromStream(xml);
        }

        throw new RuntimeException("Unknown XML format");
    }

    private PaymentsUploadPayments.ValidationResult validatePain00800102FromStream(String xml) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Document document = (Document) unmarshaller.unmarshal(new StringReader(xml));
        CustomerDirectDebitInitiationV02 customerDirectDebitInitiationV02 = document.getCstmrDrctDbtInitn();

        GroupHeader39 groupHeader39 = customerDirectDebitInitiationV02.getGrpHdr();
        int nbOfTxs = Integer.parseInt(groupHeader39.getNbOfTxs());
        BigDecimal ctrlSum = groupHeader39.getCtrlSum();

        int nbOfTxsTotal = 0;
        BigDecimal ctrlSumTotal = BigDecimal.ZERO;
        for (PaymentInstructionInformation4 paymentInstructionInformation4 : customerDirectDebitInitiationV02.getPmtInves()) {
            if (!paymentInstructionInformation4.getPmtMtd().value().equals("DD")) {
                throw new RuntimeException("Payment method is not supported");
            }

            int nbOfTxsPmtInf = Integer.parseInt(paymentInstructionInformation4.getNbOfTxs());
            BigDecimal ctrlSumPmtInf = paymentInstructionInformation4.getCtrlSum();

            int nbOfTxsTotalPmtInf = 0;
            BigDecimal ctrlSumTotalPmtInf = BigDecimal.ZERO;

            for (DirectDebitTransactionInformation9 directDebitTransactionInformation9 : paymentInstructionInformation4.getDrctDbtTxInves()) {
                nbOfTxsTotalPmtInf++;
                ctrlSumTotalPmtInf = ctrlSumTotalPmtInf.add(directDebitTransactionInformation9.getInstdAmt().getValue());
            }

            if (nbOfTxsPmtInf != nbOfTxsTotalPmtInf) {
                throw new RuntimeException("Number of payments did not match");
            }

            if (!ctrlSumPmtInf.equals(ctrlSumTotalPmtInf)) {
                throw new RuntimeException("Control sum of payments did not match");
            }

            nbOfTxsTotal += nbOfTxsPmtInf;
            ctrlSumTotal = ctrlSumTotal.add(ctrlSumPmtInf);
        }

        if (nbOfTxs != nbOfTxsTotal) {
            throw new RuntimeException("Number of payments did not match");
        }

        if (!ctrlSum.equals(ctrlSumTotal)) {
            throw new RuntimeException("Control sum of payments did not match");
        }

        return new PaymentsUploadPayments.ValidationResult(nbOfTxs, ctrlSum.multiply(new BigDecimal(100)).doubleValue());
    }

    public List<Payment> readPaymentsFromStream(InputStream inputStream, @Valid String fileName) throws IOException, JAXBException {
        String xml = readInputStreamToString(inputStream);
        if (xml.indexOf("pain.008.001.02", 0, Math.min(xml.length(), 1000)) != -1) {
            return readPain00800102PaymentsFromStream(xml, fileName);
        }

        throw new RuntimeException("Unknown XML format");
    }

    private List<Payment> readPain00800102PaymentsFromStream(String xml, @Valid String fileName) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Document document = (Document) unmarshaller.unmarshal(new StringReader(xml));
        CustomerDirectDebitInitiationV02 customerDirectDebitInitiationV02 = document.getCstmrDrctDbtInitn();

        List<Payment> payments = new ArrayList<>();
        for (PaymentInstructionInformation4 paymentInstructionInformation4 : customerDirectDebitInitiationV02.getPmtInves()) {
            if (!paymentInstructionInformation4.getPmtMtd().value().equals("DD")) {
                throw new RuntimeException("Payment method is not supported");
            }

            for (DirectDebitTransactionInformation9 directDebitTransactionInformation9 : paymentInstructionInformation4.getDrctDbtTxInves()) {
                payments.add(buildPayment(paymentInstructionInformation4, directDebitTransactionInformation9, null, fileName));
            }
        }
        return payments;
    }

    private static Payment buildPayment(
        PaymentInstructionInformation4 paymentInstructionInformation4,
        DirectDebitTransactionInformation9 directDebitTransactionInformation9,
        Gateway gateway,
        String fileName
    ) {
        Payment payment = new Payment();
        payment.setAmount(directDebitTransactionInformation9.getInstdAmt().getValue().multiply(new BigDecimal(100)).intValue());
        payment.setCurrencyCode(directDebitTransactionInformation9.getInstdAmt().getCcy());

        String name = directDebitTransactionInformation9.getDbtr().getNm().trim();
        int index = name.lastIndexOf(' ');
        if (index == -1) {
            payment.setFirstName("");
            payment.setLastName(name);
        } else {
            payment.setFirstName(name.substring(0, index));
            payment.setLastName(name.substring(index + 1));
        }

        List<String> addrLines = directDebitTransactionInformation9.getDbtr().getPstlAdr().getAdrLines();
        if (addrLines != null && addrLines.size() == 2) {
            String addrLine = addrLines.getFirst();
            index = indexOfHouseNumber(addrLine);
            if (index == -1) {
                payment.setStreetName(addrLine);
                payment.setHouseNumber("");
            } else {
                payment.setStreetName(addrLine.substring(0, index));
                payment.setHouseNumber(addrLine.substring(index + 1));
            }

            addrLine = addrLines.get(1);
            index = addrLine.lastIndexOf(' ');
            if (index == -1) {
                payment.setPostalCode("");
                payment.setCity(addrLine);
            } else {
                payment.setPostalCode(addrLine.substring(0, index));
                payment.setCity(addrLine.substring(index + 1));
            }
        }

        payment.setCountryCode(directDebitTransactionInformation9.getDbtr().getPstlAdr().getCtry());
        payment.setIban(directDebitTransactionInformation9.getDbtrAcct().getId().getIBAN());
        payment.setBic(directDebitTransactionInformation9.getDbtrAgt().getFinInstnId().getBIC());
        payment.setPaymentId(directDebitTransactionInformation9.getPmtId().getEndToEndId());

        String verkett = "";
        StringBuilder ustrd = new StringBuilder();
        for (String ustrdLine : directDebitTransactionInformation9.getRmtInf().getUstrds()) {
            ustrd.append(verkett);
            ustrd.append(ustrdLine);
            verkett = " ";
        }
        payment.setSoftDescriptor(ustrd.toString());

        payment.setRemoteIp("");
        payment.setEmailAddress("");
        payment.setGateway(gateway);
        payment.setMandateId(directDebitTransactionInformation9.getDrctDbtTx().getMndtRltdInf().getMndtId());
        payment.setFileName(fileName);

        payment.setCreditorName(paymentInstructionInformation4.getCdtr().getNm());
        payment.setCreditorIban(paymentInstructionInformation4.getCdtrAcct().getId().getIBAN());
        payment.setCreditorBic(paymentInstructionInformation4.getCdtrAgt().getFinInstnId().getBIC());
        payment.setCreditorId(
            directDebitTransactionInformation9.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthrs().getFirst().getId()
        );

        payment.setPaymentInformationId(paymentInstructionInformation4.getPmtInfId());

        payment.setMandateDate(
            directDebitTransactionInformation9
                .getDrctDbtTx()
                .getMndtRltdInf()
                .getDtOfSgntr()
                .toGregorianCalendar()
                .toZonedDateTime()
                .toLocalDate()
        );
        payment.setExecutionDate(paymentInstructionInformation4.getReqdColltnDt().toGregorianCalendar().toZonedDateTime().toLocalDate());

        return payment;
    }

    private static int indexOfHouseNumber(String line) {
        for (int index = Math.max(0, line.length() - 16); index < line.length(); index++) {
            if (Character.isDigit(line.charAt(index))) {
                return index;
            }
        }
        return -1;
    }

    private static String readInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                stringBuilder.append((char) c);
            }
        }

        return stringBuilder.toString();
    }
}
