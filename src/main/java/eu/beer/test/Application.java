package eu.beer.test;

import eu.beer.test.gui.Communicator;
import eu.beer.test.gui.SystemInputStreamCommunicator;

public class Application {
    private final Communicator communicator;

    private Application() {
        communicator = new SystemInputStreamCommunicator();
    }

    public static void main(String[] args) {
        new Application().start();
    }

    private void start() {
        communicator.print("Please write latitude: ");
        Double latitude = communicator.read();
        System.out.println(latitude);
    }
}
