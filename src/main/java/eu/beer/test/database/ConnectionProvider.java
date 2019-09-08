package eu.beer.test.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class ConnectionProvider {
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    static ConnectionProvider getInstance() {
        return new ConnectionProvider();
    }

    private ConnectionProvider() {
        url = "jdbc:h2:~/beer_factory";
        user = "";
        password = "";
    }

    public Connection getDBConnection() {
        if (connection == null)
            createNewConnectionToDb();
        return connection;
    }

    private void createNewConnectionToDb() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
