package eu.beer.test.util;

import eu.beer.test.entity.Coordinates;

import static java.lang.Math.*;

public class HaversineCalculator {
    private static final int EARTH_RADIUS_IN_KM = 6371;

    public double distance(Coordinates coordinates1, Coordinates coordinates2) {
        return distance(coordinates1.getLatitude(), coordinates1.getLongitude(),
                coordinates2.getLatitude(), coordinates2.getLongitude());
    }

    public double distance(double startLat, double startLong,
                           double endLat, double endLong) {
        double dLat = toRadians(endLat - startLat);
        double dLong = toRadians(endLong - startLong);

        double a = haversine(dLat) + cos(toRadians(startLat)) * cos(toRadians(endLat)) * haversine(dLong);
        double c = 2 * Math.atan2(sqrt(a), sqrt(1 - a));

        return EARTH_RADIUS_IN_KM * c;
    }

    private double haversine(double val) {
        return pow(sin(val / 2), 2);
    }
}
