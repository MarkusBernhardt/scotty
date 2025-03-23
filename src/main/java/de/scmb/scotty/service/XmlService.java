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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
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

    private PaymentsUploadPayments.ValidationResult validatePain00800102FromStream(String xml) throws IOException, JAXBException {
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

    public List<Payment> readPaymentsFromStream(InputStream inputStream, @Valid String fileName) throws IOException {
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
                payments.add(buildPayment(directDebitTransactionInformation9, null, fileName));
            }
        }
        return payments;
    }

    private static Payment buildPayment(
        DirectDebitTransactionInformation9 directDebitTransactionInformation9,
        Gateway gateway,
        String fileName
    ) {
        Payment payment = new Payment();
        payment.setAmount(directDebitTransactionInformation9.getInstdAmt().getValue().multiply(new BigDecimal(100)).intValue());
        payment.setCurrencyCode(cutRight(directDebitTransactionInformation9.getInstdAmt().getCcy(), 3));

        String name = directDebitTransactionInformation9.getDbtr().getNm().trim();
        int index = name.lastIndexOf(' ');
        if (index == -1) {
            payment.setFirstName("");
            payment.setLastName(cutRight(name, 35));
        } else {
            payment.setFirstName(cutRight(name.substring(0, index), 35));
            payment.setLastName(cutRight(name.substring(index + 1), 35));
        }

        List<String> addrLines = directDebitTransactionInformation9.getDbtr().getPstlAdr().getAdrLines();
        if (addrLines != null && addrLines.size() == 2) {
            String addrLine = addrLines.getFirst();
            index = indexOfHouseNumber(addrLine);
            if (index == -1) {
                payment.setStreetName(cutRight(addrLine, 70));
                payment.setHouseNumber("");
            } else {
                payment.setStreetName(cutRight(addrLine.substring(0, index), 70));
                payment.setHouseNumber(cutRight(addrLine.substring(index + 1), 16));
            }

            addrLine = addrLines.get(1);
            index = addrLine.lastIndexOf(' ');
            if (index == -1) {
                payment.setPostalCode("");
                payment.setCity(cutRight(addrLine, 35));
            } else {
                payment.setPostalCode(cutRight(addrLine.substring(0, index), 16));
                payment.setCity(cutRight(addrLine.substring(index + 1), 35));
            }
        }

        payment.setCountryCode(cutRight(directDebitTransactionInformation9.getDbtr().getPstlAdr().getCtry(), 2));
        payment.setIban(cutRight(directDebitTransactionInformation9.getDbtrAcct().getId().getIBAN(), 34));
        payment.setBic(cutRight(directDebitTransactionInformation9.getDbtrAgt().getFinInstnId().getBIC(), 11));
        payment.setPaymentId(cutRight(directDebitTransactionInformation9.getPmtId().getEndToEndId(), 35));

        String verkett = "";
        StringBuilder ustrd = new StringBuilder();
        for (String ustrdLine : directDebitTransactionInformation9.getRmtInf().getUstrds()) {
            ustrd.append(verkett);
            ustrd.append(ustrdLine);
            verkett = " ";
        }
        payment.setSoftDescriptor(cutRight(ustrd.toString(), 140));

        payment.setRemoteIp("");
        payment.setEmailAddress("");
        payment.setGateway(gateway);
        payment.setMandateId(cutRight(directDebitTransactionInformation9.getDrctDbtTx().getMndtRltdInf().getMndtId(), 35));
        payment.setFileName(cutRight(fileName, 255));

        // <PmtInfId>PAYP20250123D2301-0000689730-000001</PmtInfId>
        // <ReqdColltnDt>2025-01-23</ReqdColltnDt>
        // <Cdtr>
        //   <Nm>Gruenwelt Waermestrom GmbH</Nm>
        // <CdtrAcct>
        //   <Id>
        //     <IBAN>DE96740201500001209949</IBAN>
        // <CdtrAgt>
        //   <FinInstnId>
        //     <BIC>RZOODE77050</BIC>
        // <DrctDbtTxInf>
        //   <DrctDbtTx>
        //     <MndtRltdInf>
        //       <DtOfSgntr>2025-01-20</DtOfSgntr>
        //     <CdtrSchmeId>
        //       <Id>
        //         <PrvtId>
        //           <Othr>
        //             <Id>DE84ZZZ00001776855</Id>

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

    private static String cutRight(String value, int length) {
        if (value == null) {
            return "";
        }

        value = value.trim();
        if (value.length() > length) {
            value = value.substring(0, length);
        }
        return value.trim();
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
