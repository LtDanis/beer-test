package eu.beer.test.database;

import eu.beer.test.entity.BeerFactory;
import eu.beer.test.entity.Coordinates;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class BeerFactoryDao {
    private final Connection connection;

    public BeerFactoryDao() {
        connection = ConnectionProvider.getInstance()
                .getDBConnection();
    }

    Statement statement(String sql) {
        return new Statement(sql, connection);
    }

    public List<BeerFactory> getFactories() {
        List<Integer> ids = new Statement("SELECT id FROM beer_factories", connection)
                .runForList(r -> r.getInt("id"));
        return ids.stream()
                .map(this::readById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    public Optional<BeerFactory> readById(int factoryId) {
        return new Statement("SELECT * FROM beer_factories " +
                "LEFT JOIN beers ON beer_factories.id=beers.beer_factory_id " +
                "WHERE beer_factories.id = ?", connection)
                .run(p -> p.integer(factoryId), this::toFactory);
    }

    private BeerFactory toFactory(ResultSet resultSet) throws SQLException {
        final int id = (int) resultSet.getObject("beer_factories.id");
        final String name = resultSet.getString("beer_factories.name");
        final Coordinates coordinates = new Coordinates(resultSet.getDouble("latitude"), resultSet.getDouble("longitude"));
        final List<String> beers = new ArrayList<>();
        beers.add(resultSet.getString("beer_name"));
        while (resultSet.next())
            beers.add(resultSet.getString("beer_name"));
        return new BeerFactory(id, name, coordinates, beers);
    }
}
