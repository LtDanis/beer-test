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
//        initDb();
//        initData();
        communicator.print();
        communicator.print("Please write start latitude: ");
        double startLatitude = communicator.read()
                .orElse(51.355468);
        communicator.print("Please write start longitude: ");
        double startLongitude = communicator.read()
                .orElse(11.100790);
        long currentTime = System.currentTimeMillis();
        communicator.print("Starting latitude", startLatitude, "Starting longitude", startLongitude);

//        List<BeerFactory> road = countRoad();

//        printRoad(road, startLatitude, startLongitude);
        communicator.print(String.format("System took %.2f s", countTimeTaken(currentTime)));
    }

    private double countTimeTaken(long startTime) {
        return (System.currentTimeMillis() - startTime) * 1.0 / 1000;
    }
}
