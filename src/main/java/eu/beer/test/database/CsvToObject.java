package eu.beer.test.database;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class CsvToObject {
    public <T> Map<Integer, T> fromCsv(String fileName, Function<CSVRecord, Entry<Integer, T>> mapper) {
        return fromFileToObject(fileName, bufferedReader -> toObjectList(bufferedReader, mapper));
    }

    private <T> T fromFileToObject(String fileName, Function<BufferedReader, T> mapper) {
        try (InputStream in = requireNonNull(getClass().getClassLoader().getResourceAsStream(fileName));
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return mapper.apply(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Map<Integer, T> toObjectList(BufferedReader bufferedReader, Function<CSVRecord, Entry<Integer, T>> mapper) {
        try {
            return StreamSupport.stream(CSVFormat.EXCEL.parse(bufferedReader).spliterator(), false)
                    .skip(1)
                    .map(mapper)
                    .collect(toMap(Entry::getKey, Entry::getValue, this::onDuplicateKey));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T onDuplicateKey(T value1, T value2) {
        if (value1 instanceof List && value2 instanceof List)
            return (T) Stream.concat(((List) value1).stream(), ((List) value2).stream())
                    .collect(toList());
        return value1;
    }
}
