package de.scmb.scotty.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class KeyValueTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static KeyValue getKeyValueSample1() {
        return new KeyValue().id(1L).kvKey("kvKey1").kvValue("kvValue1");
    }

    public static KeyValue getKeyValueSample2() {
        return new KeyValue().id(2L).kvKey("kvKey2").kvValue("kvValue2");
    }

    public static KeyValue getKeyValueRandomSampleGenerator() {
        return new KeyValue().id(longCount.incrementAndGet()).kvKey(UUID.randomUUID().toString()).kvValue(UUID.randomUUID().toString());
    }
}
