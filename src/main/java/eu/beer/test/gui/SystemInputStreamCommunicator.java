package eu.beer.test.gui;

import java.util.Scanner;

public class SystemInputStreamCommunicator implements Communicator {
    private final Scanner scanner;

    public SystemInputStreamCommunicator() {
        this.scanner = new Scanner(System.in);
    }

    public void print(String text) {
        System.out.println(text);
    }

    public double read() {
        return scanner.nextDouble();
    }

    private class InvalidDataException extends RuntimeException {
        public InvalidDataException(String message) {
            super(message);
        }
    }
}
