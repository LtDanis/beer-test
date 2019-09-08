package eu.beer.test.util;

import eu.beer.test.database.BeerFactoryDao;
import eu.beer.test.entity.BeerFactory;
import eu.beer.test.entity.DistanceToFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class DataPreparator {
    private final HaversineCalculator calculator;
    private BeerFactoryDao beerFactoryDao;

    public DataPreparator(HaversineCalculator calculator, BeerFactoryDao beerFactoryDao) {
        this.calculator = calculator;
        this.beerFactoryDao = beerFactoryDao;
    }

    public List<DistanceToFactory> countDistances(BeerFactory factory) {
        final List<BeerFactory> factories = beerFactoryDao.getFactories();
        return sortByDistance(factory, factories);
    }

    public Map<Integer, List<DistanceToFactory>> prepare() {
        final List<BeerFactory> factories = beerFactoryDao.getFactories();
        return factories.stream()
                .collect(toMap(BeerFactory::getId, f -> sortByDistance(f, factories)));
    }

    private List<DistanceToFactory> sortByDistance(BeerFactory countDistanceFrom, List<BeerFactory> factories) {
        return factories.stream()
                .filter(f -> countDistanceFrom.getId() != f.getId())
                .map(f -> new DistanceToFactory(calculator.distance(countDistanceFrom.getCoordinates(), f.getCoordinates()), f))
                .sorted(Comparator.comparing(DistanceToFactory::getDistanceInKm))
                .collect(toList());
    }

}
