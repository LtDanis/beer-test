package eu.beer.test.database;

import eu.beer.test.entity.Coordinates;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

public class DatabaseInitializer {
    private final BeerFactoryDao beerFactoryDao;
    private final CsvToObject csvToObject;

    public DatabaseInitializer(BeerFactoryDao beerFactoryDao, CsvToObject csvToObject) {
        this.beerFactoryDao = beerFactoryDao;
        this.csvToObject = csvToObject;
    }

    public void init() {
        beerFactoryDao.statement("DROP TABLE IF EXISTS beer_factories;").run();
        beerFactoryDao.statement("CREATE TABLE beer_factories(" +
                "id INT NOT NULL, " +
                "name VARCHAR(360) NOT NULL, " +
                "latitude FLOAT NOT NULL, " +
                "longitude FLOAT NOT NULL" +
                ");").run();
        beerFactoryDao.statement("DROP TABLE IF EXISTS beers;").run();
        beerFactoryDao.statement("CREATE TABLE beers(" +
                "id INT NOT NULL, " +
                "beer_name VARCHAR(360) NOT NULL, " +
                "beer_factory_id INT NOT NULL" +
                ");").run();
        insertData();
    }

    private void insertData() {
        final Map<Integer, String> beerFactoryNames = csvToObject.fromCsv("breweries.csv",
                r -> new SimpleEntry<>(Integer.parseInt(r.get(0)), r.get(1)));
        final Map<Integer, Coordinates> beerFactoryCoordinates = csvToObject.fromCsv("geocodes.csv",
                r -> new SimpleEntry<>(Integer.parseInt(r.get(1)), new Coordinates(Double.parseDouble(r.get(2)), Double.parseDouble(r.get(3)))));
        beerFactoryNames.entrySet().stream()
                .filter(entry -> nonNull(beerFactoryCoordinates.get(entry.getKey())))
                .forEach(entry ->
                        beerFactoryDao.statement("INSERT INTO beer_factories (id, name, latitude, longitude) VALUES (?, ?, ?, ?)")
                                .run(p -> p
                                        .integer(entry.getKey())
                                        .string(entry.getValue())
                                        .doubleVal(beerFactoryCoordinates.get(entry.getKey()).getLatitude())
                                        .doubleVal(beerFactoryCoordinates.get(entry.getKey()).getLongitude())));

        final Map<Integer, List<String>> beerNames = csvToObject.fromCsv("beers.csv",
                r -> new SimpleEntry<>(Integer.parseInt(r.get(1)), singletonList(r.get(2))));

        AtomicInteger index = new AtomicInteger();
        beerNames.forEach((key, values) ->
                values.forEach(value -> beerFactoryDao.statement("INSERT INTO beers (id, beer_name, beer_factory_id) VALUES (?, ?, ?)")
                        .run(p -> p
                                .integer(index.getAndIncrement())
                                .string(value)
                                .integer(key))));
    }
}
