package com.github.kiro;

import org.junit.Test;

import static com.github.kiro.Point.point;
import static org.junit.Assert.assertTrue;

/**
 * Tests point
 */
public class PointTest {
    @Test
    public void testDistance() {
        Point waterloo = point("Waterloo", 51.502973, -0.114723);
        Point kingsCross = point("Kings Cross", 51.529999,-0.124481);

        assertTrue((waterloo.distance(kingsCross).meters - 3.08 * 1000) < 0.1);
    }
}
