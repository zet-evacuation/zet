/*
 * zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.zetool.test.math.geom;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.joining;

import java.util.Objects;
import java.util.stream.IntStream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import org.zetool.math.geom.NDimensional;

/**
 * Checks that the value of an {@code n} dimensional is equal to an {@code n} dimensional within some range of
 * acceptable error for every entry.
 *
 * @author Jan-Philipp Kappmeier
 */
public class NDimensionalIsCloseTo extends TypeSafeMatcher<NDimensional<Double>> {

    /**
     * The expected value.
     */
    private final NDimensional<Double> expected;
    /**
     * The maximal allowed error delta for every entry.
     */
    private final double elementErrorDelta;

    private final NDimensional<Double> deltaDim = new MatchingNDimensional() {
        @Override
        public Double get(int i) {
            return elementErrorDelta;
        }
    };

    /**
     * Instantiates a matcher for {@code n} dimensional with a given allowed {@code errorDelta} for each entry.
     *
     * @param expected the expected value
     * @param errorDelta the maximal error for and asserted value
     */
    public NDimensionalIsCloseTo(@NonNull NDimensional<Double> expected, double errorDelta) {
        this.expected = Objects.requireNonNull(expected);
        this.elementErrorDelta = errorDelta;
    }

    @Override
    public boolean matchesSafely(@NonNull NDimensional<Double> item) {
        boolean result = true;
        for (int i = 0; i < item.getDimension(); ++i) {
            result &= entryDelta(item, i) <= 0.0;
        }
        return result;
    }

    @Override
    public void describeMismatchSafely(@NonNull NDimensional<Double> item, Description mismatchDescription) {
        NDimensional<Double> difference = new MatchingNDimensional() {
            @Override
            public Double get(int i) {
                return Math.max(0, entryDelta(item, i));
            }
        };

        mismatchDescription.appendValue(item)
                .appendText(" differed by ")
                .appendValue(difference)
                .appendText(" more than delta ")
                .appendValue(deltaDim);
    }

    private double entryDelta(@NonNull NDimensional<Double> item, int index) {
        return abs(item.get(index) - expected.get(index)) - elementErrorDelta;
    }

    @Override
    public void describeTo(@NonNull Description description) {
        description.appendText("a " + expected.getDimension() + " dimensional numeric within ")
                .appendValue(deltaDim)
                .appendText(" of ")
                .appendValue(expected);
    }

    /**
     * Creates a matcher of {@link NDimensional n-dimensionals} that matches when each examined double entry of the the
     * specified {@code operand} is equal to the respective entry, within a range of +/- {@code error}.
     *
     * @param operand the expected value of matching {@code n}-dimensional
     * @param error the delta (+/-) within which matches for each entry will be allowed
     * @return the {@link Matcher} instance for {@code n}-dimensional
     */
    public static Matcher<NDimensional<Double>> closeTo(@NonNull NDimensional<Double> operand, double error) {
        return new NDimensionalIsCloseTo(operand, error);
    }

    /**
     * Static helper class to compare the operand with the {@link NDimensionalIsCloseTo#expected} value. The dimension
     * is fixed and output is formatted as {@code (a1; a2; ... an)}.
     */
    private abstract class MatchingNDimensional implements NDimensional<Double> {

        @Override
        public int getDimension() {
            return expected.getDimension();
        }

        @Override
        public String toString() {
            return "(" + IntStream.range(0, getDimension()).mapToObj(i -> Double.toString(get(i))).collect(joining("; ")) + ')';
        }
    }

}
