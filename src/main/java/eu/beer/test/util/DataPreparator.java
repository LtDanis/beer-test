package eu.beer.test.util;

import eu.beer.test.entity.DistanceToFactory;

import java.util.List;
import java.util.Map;

public class DataPreparator {
    private final HaversineCalculator calculator;

    public DataPreparator(HaversineCalculator calculator) {
        this.calculator = calculator;
    }

    public Map<Integer, List<DistanceToFactory>> prepare() {
        throw new UnsupportedOperationException();
    }
}
