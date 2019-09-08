package eu.beer.test.solution;

import eu.beer.test.entity.BeerFactory;
import eu.beer.test.entity.Coordinates;
import eu.beer.test.entity.DistanceToFactory;
import eu.beer.test.util.DataPreparator;
import eu.beer.test.util.HaversineCalculator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class OptimalRoadCalculator {
    private static final int MAX_DISTANCE_IN_KM = 2000;
    private static final int CLOSEST_FACTORIES_TO_CHECK = 1;
    private List<DistanceToFactory> bestResult = emptyList();
    private final HaversineCalculator calculator;
    private final DataPreparator dataPreparator;
    private final Map<Integer, Double> toHome;

    public OptimalRoadCalculator(HaversineCalculator calculator, DataPreparator dataPreparator) {
        this.calculator = calculator;
        this.dataPreparator = dataPreparator;
        toHome = new HashMap<>();
    }

    public List<DistanceToFactory> find(Map<Integer, List<DistanceToFactory>> factoriesMap, Coordinates startingCoordinates) {
        DistanceToFactory home = new DistanceToFactory(0, new BeerFactory(-1, "HOME", startingCoordinates, emptyList()));
        factoriesMap.values().stream()
                .flatMap(List::stream)
                .map(DistanceToFactory::getFactory)
                .forEach(f -> toHome.put(f.getId(), calculator.distance(f.getCoordinates(), home.getFactory().getCoordinates())));
        count(factoriesMap, singletonList(home), 0);
        return Stream.concat(bestResult.stream(), Stream.of(home)).collect(toList());
    }

    private void count(Map<Integer, List<DistanceToFactory>> factoriesMap, List<DistanceToFactory> distances, double distance) {
        if ((distance + distanceToHomeFromLastFactory(distances)) > MAX_DISTANCE_IN_KM)
            return;
        if (distances.size() > bestResult.size())
            bestResult = distances;
        final int lastFactory = getLast(distances).getFactory().getId();
        final List<DistanceToFactory> closestFactories = ofNullable(factoriesMap.get(lastFactory))
                .orElse(countClosest(distances));
        closestFactories.stream()
                .limit(CLOSEST_FACTORIES_TO_CHECK)
                .forEach(closestFactory -> count(factoriesMap, concat(distances, closestFactory), distance + closestFactory.getDistanceInKm()));
    }

    private Double distanceToHomeFromLastFactory(List<DistanceToFactory> distances) {
        return toHome.getOrDefault(getLast(distances).getFactory().getId(), 0.0);
    }

    private List<DistanceToFactory> countClosest(List<DistanceToFactory> distances) {
        return dataPreparator.countDistances(getLast(distances).getFactory());
    }

    private List<DistanceToFactory> concat(List<DistanceToFactory> distances, DistanceToFactory closestFactory) {
        return Stream.concat(distances.stream(), Stream.of(closestFactory))
                .collect(toList());
    }

    private DistanceToFactory getLast(List<DistanceToFactory> distances) {
        return distances.get(distances.size() - 1);
    }
}
