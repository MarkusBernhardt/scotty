package de.scmb.scotty.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Payment getPaymentSample1() {
        return new Payment()
            .id(1L)
            .mandateId("mandateId1")
            .paymentId("paymentId1")
            .iban("iban1")
            .bic("bic1")
            .amount(1)
            .currencyCode("currencyCode1")
            .softDescriptor("softDescriptor1")
            .firstName("firstName1")
            .lastName("lastName1")
            .streetName("streetName1")
            .houseNumber("houseNumber1")
            .postalCode("postalCode1")
            .city("city1")
            .countryCode("countryCode1")
            .remoteIp("remoteIp1")
            .emailAddress("emailAddress1")
            .state("state1")
            .message("message1")
            .gatewayId("gatewayId1")
            .mode("mode1")
            .fileName("fileName1")
            .creditorName("creditorName1")
            .creditorIban("creditorIban1")
            .creditorBic("creditorBic1")
            .creditorId("creditorId1")
            .paymentInformationId("paymentInformationId1");
    }

    public static Payment getPaymentSample2() {
        return new Payment()
            .id(2L)
            .mandateId("mandateId2")
            .paymentId("paymentId2")
            .iban("iban2")
            .bic("bic2")
            .amount(2)
            .currencyCode("currencyCode2")
            .softDescriptor("softDescriptor2")
            .firstName("firstName2")
            .lastName("lastName2")
            .streetName("streetName2")
            .houseNumber("houseNumber2")
            .postalCode("postalCode2")
            .city("city2")
            .countryCode("countryCode2")
            .remoteIp("remoteIp2")
            .emailAddress("emailAddress2")
            .state("state2")
            .message("message2")
            .gatewayId("gatewayId2")
            .mode("mode2")
            .fileName("fileName2")
            .creditorName("creditorName2")
            .creditorIban("creditorIban2")
            .creditorBic("creditorBic2")
            .creditorId("creditorId2")
            .paymentInformationId("paymentInformationId2");
    }

    public static Payment getPaymentRandomSampleGenerator() {
        return new Payment()
            .id(longCount.incrementAndGet())
            .mandateId(UUID.randomUUID().toString())
            .paymentId(UUID.randomUUID().toString())
            .iban(UUID.randomUUID().toString())
            .bic(UUID.randomUUID().toString())
            .amount(intCount.incrementAndGet())
            .currencyCode(UUID.randomUUID().toString())
            .softDescriptor(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .streetName(UUID.randomUUID().toString())
            .houseNumber(UUID.randomUUID().toString())
            .postalCode(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .countryCode(UUID.randomUUID().toString())
            .remoteIp(UUID.randomUUID().toString())
            .emailAddress(UUID.randomUUID().toString())
            .state(UUID.randomUUID().toString())
            .message(UUID.randomUUID().toString())
            .gatewayId(UUID.randomUUID().toString())
            .mode(UUID.randomUUID().toString())
            .fileName(UUID.randomUUID().toString())
            .creditorName(UUID.randomUUID().toString())
            .creditorIban(UUID.randomUUID().toString())
            .creditorBic(UUID.randomUUID().toString())
            .creditorId(UUID.randomUUID().toString())
            .paymentInformationId(UUID.randomUUID().toString());
    }
}
