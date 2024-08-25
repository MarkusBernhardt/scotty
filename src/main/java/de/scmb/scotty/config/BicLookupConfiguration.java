package de.scmb.scotty.config;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BicLookupConfiguration {

    private final Map<String, String> deBlz2BicUnmodifiableMap;

    public BicLookupConfiguration() throws IOException, CsvValidationException {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("data/bic/BLZ.CSV")) {
            assert in != null;
            try (InputStreamReader inReader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                CSVParser parser = new CSVParserBuilder().withSeparator(';').withQuoteChar('"').withIgnoreQuotations(true).build();

                Map<String, String> deBlz2BicMap = new HashMap<>();
                try (CSVReader csvReader = new CSVReaderBuilder(inReader).withSkipLines(0).withCSVParser(parser).withSkipLines(1).build()) {
                    String[] line;
                    while ((line = csvReader.readNext()) != null) {
                        if (line[7].trim().isEmpty()) {
                            continue;
                        }
                        deBlz2BicMap.put(line[0], line[7]);
                    }
                }
                deBlz2BicUnmodifiableMap = Collections.unmodifiableMap(deBlz2BicMap);
            }
        }
    }

    @Bean(name = "deBlz2Bic")
    public Map<String, String> deBlz2Bic() {
        return deBlz2BicUnmodifiableMap;
    }
}
