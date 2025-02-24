package de.scmb.scotty.gateway.openpayd;

import static java.time.ZoneOffset.UTC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.scmb.scotty.config.ApplicationProperties;
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.repository.PaymentRepository;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.Base64;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.RequestBodyEntity;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OpenPaydService {

    private final Logger log = LoggerFactory.getLogger(OpenPaydService.class);

    private OpenPaydAccessToken openPaydAccessToken;

    private final ApplicationProperties applicationProperties;

    private final PaymentRepository paymentRepository;

    public OpenPaydService(ApplicationProperties applicationProperties, PaymentRepository paymentRepository) {
        this.applicationProperties = applicationProperties;
        this.paymentRepository = paymentRepository;
    }

    public void execute(Payment payment) {
        try {
            if (!applicationProperties.getOpenPayd().isEnabled()) {
                throw new IllegalArgumentException("Openpayd is not enabled");
            }

            if (openPaydAccessToken == null) {
                loadAccessToken();
            }

            if (payment.getAmount() >= 0) {
                executeSdd(payment);
            } else {
                executeSct(payment);
            }
        } catch (Throwable t) {
            payment.setState("failed");
            payment.setMessage(t.getMessage());
            payment.setTimestamp(Instant.now());
            payment.setGatewayId("");
            payment.setMode("");
        } finally {
            paymentRepository.save(payment);
        }
    }

    private void executeSct(Payment payment) {
        OpenPaydBeneficiaryPayout request = getOpenPaydBeneficiaryPayout(payment);

        RequestBodyEntity requestBodyEntity = Unirest
            .post(applicationProperties.getOpenPayd().getBaseUrl() + "/api/transactions/beneficiaryPayout")
            .header("Authorization", "Bearer " + openPaydAccessToken.getAccessToken())
            .header("Content-Type", "application/json")
            .header("Charset", "utf-8")
            .header("Accept", "application/json")
            .header("X-ACCOUNT-HOLDER-ID", applicationProperties.getOpenPayd().getAccountHolderId())
            .header("IDEMPOTENCY-KEY", payment.getPaymentId())
            .body(request);

        HttpResponse<String> response = requestBodyEntity.asString();

        payment.setTimestamp(Instant.now());
        payment.setMode("");
        payment.setGatewayId("");
        payment.setMessage("");

        if (response.getStatus() == 200) {
            payment.setState("submitted");

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                OpenPaydBeneficiaryPayoutResponse openPaydBeneficiaryPayoutResponse = objectMapper.readValue(
                    response.getBody(),
                    OpenPaydBeneficiaryPayoutResponse.class
                );
                String gatewayId = openPaydBeneficiaryPayoutResponse.transactionId;
                payment.setGatewayId(gatewayId);
            } catch (JsonProcessingException e) {
                payment.setMessage("Cannot parse response");
            }
        } else {
            payment.setState("failed");

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                OpenPaydErrors errors = objectMapper.readValue(response.getBody(), OpenPaydErrors.class);
                payment.setMessage(errors.getErrorCode());
                if (errors.getMessage() != null && !errors.getMessage().isEmpty()) {
                    payment.setMessage(errors.getMessage());
                }
                if (errors.getErrors() != null && !errors.getErrors().isEmpty()) {
                    payment.setMessage(errors.getErrors().get(0).getDefaultMessage());
                }
            } catch (JsonProcessingException e) {
                payment.setMessage("Cannot parse error");
            }
        }
    }

    private void executeSdd(Payment payment) {
        OpenPaydPayment request = getOpenPaydPayment(payment);

        RequestBodyEntity requestBodyEntity = Unirest
            .post(applicationProperties.getOpenPayd().getBaseUrl() + "/api/transactions/direct-debit")
            .header("Authorization", "Bearer " + openPaydAccessToken.getAccessToken())
            .header("Content-Type", "application/json")
            .header("Charset", "utf-8")
            .header("Accept", "application/json")
            .header("X-ACCOUNT-HOLDER-ID", applicationProperties.getOpenPayd().getAccountHolderId())
            .header("IDEMPOTENCY-KEY", payment.getPaymentId())
            .body(request);

        HttpResponse<String> response = requestBodyEntity.asString();

        payment.setTimestamp(Instant.now());
        payment.setMode("");
        payment.setGatewayId("");
        payment.setMessage("");

        if (response.getStatus() == 200) {
            payment.setState("submitted");

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                OpenPaydPayment openPaydPayment = objectMapper.readValue(response.getBody(), OpenPaydPayment.class);
                String gatewayId = openPaydPayment.getId();
                byte[] gatewayIdBuffer = Base64.getDecoder().decode(gatewayId);
                payment.setGatewayId(new String(gatewayIdBuffer, StandardCharsets.UTF_8));
            } catch (JsonProcessingException e) {
                payment.setMessage("Cannot parse response");
            }
        } else {
            payment.setState("failed");

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                OpenPaydErrors errors = objectMapper.readValue(response.getBody(), OpenPaydErrors.class);
                payment.setMessage(errors.getErrorCode());
                if (errors.getMessage() != null && !errors.getMessage().isEmpty()) {
                    payment.setMessage(errors.getMessage());
                }
                if (errors.getErrors() != null && !errors.getErrors().isEmpty()) {
                    payment.setMessage(errors.getErrors().get(0).getDefaultMessage());
                }
            } catch (JsonProcessingException e) {
                payment.setMessage("Cannot parse error");
            }
        }
    }

    public void handleWebhook(OpenPaydWebhook openPaydWebhook) {}

    private void loadAccessToken() {
        HttpResponse<OpenPaydAccessToken> response = Unirest
            .post(applicationProperties.getOpenPayd().getBaseUrl() + "/api/oauth/token?grant_type=client_credentials")
            .basicAuth(applicationProperties.getOpenPayd().getUsername(), applicationProperties.getOpenPayd().getPassword())
            .header("Content-Type", "application/json")
            .header("Charset", "utf-8")
            .header("Accept", "application/json")
            .asObject(OpenPaydAccessToken.class);

        if (response.getStatus() == 200) {
            openPaydAccessToken = response.getBody();
        }
    }

    private OpenPaydBeneficiaryPayout getOpenPaydBeneficiaryPayout(Payment payment) {
        OpenPaydBeneficiaryPayout openPaydBeneficiaryPayout = new OpenPaydBeneficiaryPayout();
        openPaydBeneficiaryPayout.setAccountId(applicationProperties.getOpenPayd().getAccountId());
        openPaydBeneficiaryPayout.setPaymentType("SEPA");
        openPaydBeneficiaryPayout.setReference(payment.getSoftDescriptor());
        openPaydBeneficiaryPayout.setExternalCustomerId(payment.getPaymentId());

        OpenPaydBeneficiaryPayoutAmount openPaydBeneficiaryPayoutAmount = new OpenPaydBeneficiaryPayoutAmount();
        openPaydBeneficiaryPayoutAmount.setValue(payment.getAmount() / -100d);
        openPaydBeneficiaryPayoutAmount.setCurrency("EUR");
        openPaydBeneficiaryPayout.setAmount(openPaydBeneficiaryPayoutAmount);

        OpenPaydBeneficiaryPayoutBeneficiary openPaydBeneficiaryPayoutBeneficiary = new OpenPaydBeneficiaryPayoutBeneficiary();
        openPaydBeneficiaryPayoutBeneficiary.setBankAccountCountry(payment.getIban().substring(0, 2));
        openPaydBeneficiaryPayoutBeneficiary.setCustomerType("RETAIL");
        openPaydBeneficiaryPayoutBeneficiary.setFirstName(payment.getFirstName());
        openPaydBeneficiaryPayoutBeneficiary.setLastName(payment.getLastName());
        openPaydBeneficiaryPayoutBeneficiary.setIban(payment.getIban());
        openPaydBeneficiaryPayout.setBeneficiary(openPaydBeneficiaryPayoutBeneficiary);

        return openPaydBeneficiaryPayout;
    }

    private OpenPaydPayment getOpenPaydPayment(Payment payment) {
        OpenPaydPayment openPaydPayment = new OpenPaydPayment();
        openPaydPayment.setAccountId(applicationProperties.getOpenPayd().getAccountId());
        openPaydPayment.setTransactionReference(payment.getPaymentId());
        openPaydPayment.setMandateId(payment.getMandateId());
        openPaydPayment.setFriendlyName(payment.getSoftDescriptor());
        openPaydPayment.setMandateDateOfSigning("2025-02-01");

        OpenPaydPaymentAmount openPaydPaymentAmount = new OpenPaydPaymentAmount();
        openPaydPaymentAmount.setValue(payment.getAmount() / 100d);
        openPaydPayment.setAmount(openPaydPaymentAmount);

        String bankAccountHolderName = payment.getFirstName() + " " + payment.getLastName();
        bankAccountHolderName = bankAccountHolderName.trim();

        OpenPaydPaymentDebtor openPaydPaymentDebtor = new OpenPaydPaymentDebtor();
        openPaydPaymentDebtor.setIban(payment.getIban());
        openPaydPaymentDebtor.setBankAccountHolderName(bankAccountHolderName);
        openPaydPayment.setDebtor(openPaydPaymentDebtor);

        return openPaydPayment;
    }

    public static ZonedDateTime getNextTarget2Day(ZonedDateTime date) {
        date = date.plusDays(1);
        while (!isTarget2Date(date)) {
            date = date.plusDays(1);
        }
        return date;
    }

    private static boolean isTarget2Date(ZonedDateTime date) {
        if ( // Saturday
            date.getDayOfWeek() == DayOfWeek.SATURDAY ||
            // Sunday
                date.getDayOfWeek() ==
                DayOfWeek.SUNDAY ||
            (date.getDayOfMonth() == 1 && date.getMonth() == Month.JANUARY) || // 1st of January
            (date.getDayOfMonth() == 1 && date.getMonth() == Month.MAY) || // 1st of May
            (date.getDayOfMonth() == 25 && date.getMonth() == Month.DECEMBER) || // 25th of December
            (date.getDayOfMonth() == 26 && date.getMonth() == Month.DECEMBER) || // 26th of December
            // Good Friday
            isSameDateAs(date, calcEasterSunday(date.getYear()).minusDays(2)) ||
            // Easter Monday
            isSameDateAs(date, calcEasterSunday(date.getYear()).plusDays(1))
        ) {
            return false;
        }
        return true;
    }

    private static ZonedDateTime calcEasterSunday(int year) {
        var g = year % 19;
        var c = year / 100;
        var h = (c - c / 4 - (8 * c + 13) / 25 + 19 * g + 15) % 30;
        var i = h - (h / 28) * (1 - (h / 28) * (29 / (h + 1)) * ((21 - g) / 11));

        var dayOfMonth = i - ((year + year / 4 + i + 2 - c + c / 4) % 7) + 28;
        var month = Month.MARCH;

        if (dayOfMonth > 31) {
            month = Month.APRIL;
            dayOfMonth -= 31;
        }

        return ZonedDateTime.of(year, month.getValue(), dayOfMonth, 0, 0, 0, 0, UTC);
    }

    public static boolean isSameDateAs(ZonedDateTime first, ZonedDateTime second) {
        return (
            first.getYear() == second.getYear() && first.getMonth() == second.getMonth() && first.getDayOfMonth() == second.getDayOfMonth()
        );
    }
}
