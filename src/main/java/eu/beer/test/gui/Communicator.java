package eu.beer.test.gui;

import java.util.Optional;

public interface Communicator {
    void print(Object... text);

    Optional<Double> read();
}
