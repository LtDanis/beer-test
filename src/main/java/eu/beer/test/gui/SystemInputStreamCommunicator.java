package eu.beer.test.gui;

import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.of;

public class SystemInputStreamCommunicator implements Communicator {
    private final Scanner scanner;

    public SystemInputStreamCommunicator() {
        this.scanner = new Scanner(System.in);
    }

    public void print(Object... text) {
        System.out.println(of(text)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(joining(" ")));
    }

    public Optional<Double> read() {
        try {
            return Optional.ofNullable(scanner.next())
                    .map(Double::parseDouble);
        } catch (Exception e) {
            System.out.println("Failed to read double. Returning default data.");
            return Optional.empty();
        }
    }
}
