package eu.beer.test.entity;

public class DistanceToFactory {
    private final double distanceInKm;
    private final BeerFactory factory;

    public DistanceToFactory(double distanceInKm, BeerFactory factory) {
        this.distanceInKm = distanceInKm;
        this.factory = factory;
    }

    public double getDistanceInKm() {
        return distanceInKm;
    }

    public BeerFactory getFactory() {
        return factory;
    }
}
