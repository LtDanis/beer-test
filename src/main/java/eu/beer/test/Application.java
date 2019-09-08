package eu.beer.test;

import eu.beer.test.database.BeerFactoryDao;
import eu.beer.test.database.CsvToObject;
import eu.beer.test.database.DatabaseInitializer;
import eu.beer.test.entity.Coordinates;
import eu.beer.test.entity.DistanceToFactory;
import eu.beer.test.gui.Communicator;
import eu.beer.test.gui.RoadPrinter;
import eu.beer.test.gui.SystemInputStreamCommunicator;
import eu.beer.test.solution.OptimalRoadCalculator;
import eu.beer.test.solution.DataPreparator;
import eu.beer.test.util.HaversineCalculator;

import java.util.List;
import java.util.Map;

public class Application {
    private final Communicator communicator;
    private final DatabaseInitializer dbInitializer;
    private final DataPreparator dataPreparator;
    private final RoadPrinter roadPrinter;
    private final OptimalRoadCalculator roadCalculator;

    private Application() {
        final HaversineCalculator calculator = new HaversineCalculator();
        communicator = new SystemInputStreamCommunicator();
        dbInitializer = new DatabaseInitializer(new BeerFactoryDao(), new CsvToObject());
        dataPreparator = new DataPreparator(calculator, new BeerFactoryDao());
        roadPrinter = new RoadPrinter(communicator);
        roadCalculator = new OptimalRoadCalculator(calculator, dataPreparator);
    }

    public static void main(String[] args) {
        new Application().start();
    }

    private void start() {
        dbInitializer.init();
        Map<Integer, List<DistanceToFactory>> factoriesMap = dataPreparator.prepare();

        Coordinates startingCoordinates = getStartingCoordinates();
        long startTime = System.nanoTime();

        List<DistanceToFactory> road = roadCalculator.find(factoriesMap, startingCoordinates);

        roadPrinter.print(road);
        communicator.print(String.format("Program took %.3f ms", countTimeTaken(startTime)));
    }

    private Coordinates getStartingCoordinates() {
        communicator.print("Please write start latitude: ");
        double startLatitude = communicator.read()
                .orElse(51.355468);
        communicator.print("Please write start longitude: ");
        double startLongitude = communicator.read()
                .orElse(11.100790);
        communicator.print("Starting latitude", startLatitude, "Starting longitude", startLongitude);
        return new Coordinates(startLatitude, startLongitude);
    }

    private double countTimeTaken(long startTime) {
        return (System.nanoTime() - startTime) * 1.0 / 1000_000;
    }
}
