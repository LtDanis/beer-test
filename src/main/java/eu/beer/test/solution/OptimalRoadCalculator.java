package eu.beer.test.solution;

import eu.beer.test.entity.BeerFactory;
import eu.beer.test.entity.Coordinates;
import eu.beer.test.entity.DistanceToFactory;
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
    private final HaversineCalculator calculator;
    private final DataPreparator dataPreparator;
    private final Map<Integer, Double> toHome;
    private final int closestFactoriesToCheck;
    private final int timeToStopInSeconds;
    private long startTime;

    private List<DistanceToFactory> bestResult = emptyList();

    public OptimalRoadCalculator(HaversineCalculator calculator,
                                 DataPreparator dataPreparator,
                                 int closestFactoriesToCheck,
                                 int timeToStopInSeconds) {
        this.calculator = calculator;
        this.dataPreparator = dataPreparator;
        this.closestFactoriesToCheck = closestFactoriesToCheck;
        this.timeToStopInSeconds = timeToStopInSeconds;
        toHome = new HashMap<>();
    }

    public List<DistanceToFactory> find(Map<Integer, List<DistanceToFactory>> factoriesMap, Coordinates startingCoordinates) {
        startTime = System.currentTimeMillis();
        final BeerFactory home = new BeerFactory(-1, "HOME", startingCoordinates, emptyList());
        initDistanceToHome(factoriesMap, home);
        count(factoriesMap,
                singletonList(new DistanceToFactory(0, home)),
                singletonList(home.getId()),
                0);
        return Stream.concat(
                bestResult.stream(),
                Stream.of(new DistanceToFactory(toHome.get(getLastFactoryId(bestResult)), home)))
                .collect(toList());
    }

    private void initDistanceToHome(Map<Integer, List<DistanceToFactory>> factoriesMap, BeerFactory home) {
        factoriesMap.values().stream()
                .flatMap(List::stream)
                .map(DistanceToFactory::getFactory)
                .forEach(f -> toHome.put(f.getId(), calculator.distance(f.getCoordinates(), home.getCoordinates())));
    }

    private void count(Map<Integer, List<DistanceToFactory>> factoriesMap,
                       List<DistanceToFactory> distances,
                       List<Integer> visitedFactories,
                       double distance) {
        if ((distance + distanceToHomeFromLastFactory(distances)) > MAX_DISTANCE_IN_KM || shouldBreak())
            return;
        if (distances.size() > bestResult.size())
            bestResult = distances;
        final List<DistanceToFactory> closestFactories = ofNullable(factoriesMap.get(getLastFactoryId(distances)))
                .orElseGet(() -> countClosest(distances));
        closestFactories.stream()
                .filter(f -> !visitedFactories.contains(f.getFactory().getId()))
                .limit(closestFactoriesToCheck)
                .forEach(closestFactory -> count(factoriesMap,
                        concat(distances, closestFactory),
                        concat(visitedFactories, closestFactory.getFactory().getId()),
                        distance + closestFactory.getDistanceInKm()));
    }

    private int getLastFactoryId(List<DistanceToFactory> distances) {
        return getLast(distances).getFactory().getId();
    }

    private Double distanceToHomeFromLastFactory(List<DistanceToFactory> distances) {
        return toHome.getOrDefault(getLastFactoryId(distances), 0.0);
    }

    private List<DistanceToFactory> countClosest(List<DistanceToFactory> distances) {
        return dataPreparator.countDistances(getLast(distances).getFactory());
    }

    private <T> List<T> concat(List<T> distances, T closestFactory) {
        return Stream.concat(distances.stream(), Stream.of(closestFactory))
                .collect(toList());
    }

    private DistanceToFactory getLast(List<DistanceToFactory> distances) {
        return distances.get(distances.size() - 1);
    }

    private boolean shouldBreak() {
        return ((System.currentTimeMillis() - startTime) / 1000) > timeToStopInSeconds;
    }
}
