package eu.beer.test.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SystemInputStreamCommunicatorTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private Communicator communicator;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        communicator = new SystemInputStreamCommunicator();
    }

    @Test
    void testEmpty() {
        assertDoesNotThrow(() -> communicator.print());
    }

    @Test
    void testPrint() {
        communicator.print("A", "B", null, "C");
        assertThat(outContent.toString()).startsWith("A B C");
    }
}
