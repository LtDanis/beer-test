package eu.beer.test.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Statement {
    private final String query;
    private final Connection connection;

    public Statement(String query, Connection connection) {
        this.query = query;
        this.connection = connection;
    }

    <R> List<R> runForList(SqlFunction<R> resultsConsumer) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            return executeForList(resultsConsumer, preparedStatement);
        } catch (SQLException e) {
            throw new StorageException(e);
        }
    }

    private <R> List<R> executeForList(SqlFunction<R> resultsConsumer, PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            List<R> results = new ArrayList<>();
            while (resultSet.next())
                results.add(resultsConsumer.apply(resultSet));
            return results;
        }
    }

    public <R> Optional<R> run(SqlConsumer<Parameter> parameter, SqlFunction<R> resultsConsumer) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            parameter.accept(new Parameter(preparedStatement));
            return executeForSingle(resultsConsumer, preparedStatement);
        } catch (SQLException e) {
            throw new StorageException(e);
        }
    }

    private <R> Optional<R> executeForSingle(SqlFunction<R> resultsConsumer, PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSet.next()
                    ? Optional.ofNullable(resultsConsumer.apply(resultSet))
                    : Optional.empty();
        }
    }

    public void run() {
        run(p -> {
        });
    }

    public void run(SqlConsumer<Parameter> parameter) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            parameter.accept(new Parameter(preparedStatement));
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new StorageException(e);
        }
    }

    public static class Parameter {
        private final PreparedStatement preparedStatement;
        private final AtomicInteger index;

        private Parameter(PreparedStatement preparedStatement) {
            this.preparedStatement = preparedStatement;
            index = new AtomicInteger(1);
        }

        Parameter integer(int param) {
            try {
                preparedStatement.setInt(index.getAndIncrement(), param);
            } catch (SQLException e) {
                throw new StorageException(e);
            }
            return this;
        }

        Parameter string(String param) {
            try {
                preparedStatement.setString(index.getAndIncrement(), param);
            } catch (SQLException e) {
                throw new StorageException(e);
            }
            return this;
        }

        Parameter doubleVal(Double param) {
            try {
                preparedStatement.setDouble(index.getAndIncrement(), param);
            } catch (SQLException e) {
                throw new StorageException(e);
            }
            return this;
        }
    }

    @FunctionalInterface
    public interface SqlConsumer<T> {
        void accept(T result) throws SQLException;
    }

    @FunctionalInterface
    public interface SqlFunction<R> {
        R apply(ResultSet result) throws SQLException;
    }

    private static class StorageException extends RuntimeException {
        StorageException(SQLException e) {
            super(e);
        }
    }
}
