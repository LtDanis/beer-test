package eu.beer.test.entity;

import java.util.List;

public class BeerFactory {
    private final int id;
    private final String name;
    private final Coordinates coordinates;
    private final List<String> beerTypes;

    public BeerFactory(int id, String name, Coordinates coordinates, List<String> beerTypes) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.beerTypes = beerTypes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public List<String> getBeerTypes() {
        return beerTypes;
    }
}
