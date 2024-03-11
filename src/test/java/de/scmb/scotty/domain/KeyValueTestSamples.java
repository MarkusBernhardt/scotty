package de.scmb.scotty.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class KeyValueTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static KeyValue getKeyValueSample1() {
        return new KeyValue().id(1L).key("key1").value("value1");
    }

    public static KeyValue getKeyValueSample2() {
        return new KeyValue().id(2L).key("key2").value("value2");
    }

    public static KeyValue getKeyValueRandomSampleGenerator() {
        return new KeyValue().id(longCount.incrementAndGet()).key(UUID.randomUUID().toString()).value(UUID.randomUUID().toString());
    }
}
