package ua.nupp.defects;

import org.junit.jupiter.api.Test;
import ua.nupp.defects.service.IndexCalculator;

import static org.junit.jupiter.api.Assertions.*;

public class IndexCalculatorTest {

    @Test
    void severityFiveGivesHundred() {
        assertEquals(100.0, IndexCalculator.severityFactor(5));
    }

    @Test
    void severityOutOfRangeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IndexCalculator.severityFactor(6));
    }

    @Test
    void bigAreaIsLimitedTo100() {
        assertEquals(100.0, IndexCalculator.areaFactor(60));
    }

    @Test
    void indexForExampleDefect() {
        assertEquals(64.0, IndexCalculator.calculate(75, 40, 80));
    }
}
