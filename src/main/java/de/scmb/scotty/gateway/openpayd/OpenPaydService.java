package de.scmb.scotty.gateway.openpayd;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.DAYS;

import de.scmb.scotty.config.ApplicationProperties;
import de.scmb.scotty.domain.Payment;
import de.scmb.scotty.gateway.novalnet.NovalnetPayment;
import java.time.*;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.springframework.stereotype.Service;

@Service
public class OpenPaydService {

    private OpenPaydAccessToken openPaydAccessToken;

    private final ApplicationProperties applicationProperties;

    public OpenPaydService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public void execute(Payment payment) {
        if (openPaydAccessToken == null) {
            loadAccessToken();
        }

        OpenPaydDirectDebitRequest openPaydDirectDebitRequest = getOpenPaydDirectDebitRequest(payment);
    }

    private void loadAccessToken() {
        HttpResponse<OpenPaydAccessToken> response = Unirest
            .post(applicationProperties.getOpenPayd().getBaseUrl() + "/api/oauth/token?grant_type=client_credentials")
            .basicAuth(applicationProperties.getOpenPayd().getUsername(), applicationProperties.getOpenPayd().getPassword())
            .header("Content-Type", "application/json")
            .asObject(OpenPaydAccessToken.class);

        if (response.getStatus() == 200) {
            openPaydAccessToken = response.getBody();
        }
    }

    private OpenPaydDirectDebitRequest getOpenPaydDirectDebitRequest(Payment payment) {
        OpenPaydDirectDebitRequest openPaydDirectDebitRequest = new OpenPaydDirectDebitRequest();
        openPaydDirectDebitRequest.setAccountId(applicationProperties.getOpenPayd().getAccountId());
        openPaydDirectDebitRequest.setTransactionReference(payment.getPaymentId());
        openPaydDirectDebitRequest.setMandateId(payment.getMandateId());
        openPaydDirectDebitRequest.setFriendlyName(payment.getSoftDescriptor());
        openPaydDirectDebitRequest.setMandateDateOfSigning("2024-06-01");

        ZonedDateTime dueDate = getNextTarget2Day();
        openPaydDirectDebitRequest.setDueDate(
            String.format("%4d-%2d-%2d", dueDate.getYear(), dueDate.getMonthValue(), dueDate.getDayOfMonth())
        );

        OpenPaydDirectDebitRequestAmount openPaydDirectDebitRequestAmount = new OpenPaydDirectDebitRequestAmount();
        openPaydDirectDebitRequestAmount.setValue(payment.getAmount() / 100d);
        openPaydDirectDebitRequest.setAmount(openPaydDirectDebitRequestAmount);

        String bankAccountHolderName = payment.getFirstName() + " " + payment.getLastName();
        bankAccountHolderName = bankAccountHolderName.trim();

        OpenPaydDirectDebitRequestDebtor openPaydDirectDebitRequestDebtor = new OpenPaydDirectDebitRequestDebtor();
        openPaydDirectDebitRequestDebtor.setIban(payment.getIban());
        openPaydDirectDebitRequestDebtor.setBankAccountHolderName(bankAccountHolderName);
        openPaydDirectDebitRequest.setDebtor(openPaydDirectDebitRequestDebtor);

        return openPaydDirectDebitRequest;
    }

    public static ZonedDateTime getNextTarget2Day() {
        ZonedDateTime date = Instant.now().atZone(UTC).plusDays(1);
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
            (// 1st of January
                    date.getDayOfMonth() ==
                    1 &&
                date.getMonth() == Month.JANUARY) ||
            (// 1st of May
                    date.getDayOfMonth() ==
                    1 &&
                date.getMonth() == Month.MAY) ||
            (// 25th of December
                    date.getDayOfMonth() ==
                    25 &&
                date.getMonth() == Month.DECEMBER) ||
            (// 26th of December
                    date.getDayOfMonth() ==
                    26 &&
                date.getMonth() == Month.DECEMBER) ||
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
