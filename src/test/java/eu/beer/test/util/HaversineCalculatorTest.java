package eu.beer.test.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HaversineCalculatorTest {
    private HaversineCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new HaversineCalculator();
    }

    @Test
    void testZeroDistance() {
        double distance = calculator.distance(0, 0, 0, 0);
        assertThat(distance).isZero();
    }

    @Test
    void testWithLatitudeChangedBy1() {
        double distance = calculator.distance(0, 0, 1, 0);
        assertThat(distance).isEqualTo(111);
    }

    @Test
    void testFromHomeToSatalia() {
        double distance = calculator.distance(54.8983385,23.954133, 54.9004929,23.893662);
        assertThat(distance).isEqualTo(7);
    }
}