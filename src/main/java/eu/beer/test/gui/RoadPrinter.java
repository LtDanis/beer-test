package eu.beer.test.gui;

import eu.beer.test.entity.BeerFactory;
import eu.beer.test.entity.Coordinates;
import eu.beer.test.entity.DistanceToFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class RoadPrinter {
    private final Communicator communicator;

    public RoadPrinter(Communicator communicator) {
        this.communicator = communicator;
    }

    public void print(List<DistanceToFactory> road) {
        communicator.print("Found", road.size(), "factories:");
        printRoad(road);
        communicator.print();
        communicator.print("Total distance traveled", getTotalDistance(road));
        communicator.print();
        final List<String> beers = collectedBeers(road);
        communicator.print("Collected beer types", beers.size(), "-", String.join(", ", beers));
    }

    private void printRoad(List<DistanceToFactory> road) {
        for (DistanceToFactory next : road) {
            final BeerFactory factory = next.getFactory();
            final Coordinates coordinates = factory.getCoordinates();
            communicator.print(String.format("[%s] %s [%s %s] distance %s",
                    factory.getId(), factory.getName(), coordinates.getLatitude(), coordinates.getLongitude(), next.getDistanceInKm()));
        }
    }

    private double getTotalDistance(List<DistanceToFactory> road) {
        return road.stream()
                .mapToDouble(DistanceToFactory::getDistanceInKm)
                .sum();
    }

    private List<String> collectedBeers(List<DistanceToFactory> road) {
        return road.stream()
                .map(DistanceToFactory::getFactory)
                .map(BeerFactory::getBeerTypes)
                .flatMap(List::stream)
                .collect(toList());
    }
}
