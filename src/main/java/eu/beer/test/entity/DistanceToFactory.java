package eu.beer.test.entity;

public class DistanceToFactory {
    private final double distanceInKm;
    private final int factoryId;

    public DistanceToFactory(double distanceInKm, int factoryId) {
        this.distanceInKm = distanceInKm;
        this.factoryId = factoryId;
    }

    public double getDistanceInKm() {
        return distanceInKm;
    }

    public int getFactoryId() {
        return factoryId;
    }
}
