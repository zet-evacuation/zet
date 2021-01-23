/*
 * zet evacuation tool copyright © 2007-21 zet evacuation team
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
package de.zet_evakuierung.visualization;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ModelContainerTestUtils {

    /**
     * Asserts that a map of objects and a list of objects contains exactly the same data.
     *
     * @param <I> the input model object type, used as key for the map
     * @param <O> the created model type, in the list and as map values
     * @param iterable iterable of all generated output objects
     * @param inputObjects list of all input obects
     * @param fromMap extracts output objects for input objects from map
     */
    public static <I, O> void assertObjectsFromIteratorAndMapAreEqual(Iterable<O> iterable, List<I> inputObjects,
            Function<I, O> fromMap) {
        List<O> objectsFromIterator = StreamSupport.stream(iterable.spliterator(), false).collect(toList());
        List<O> objectsFromMap = inputObjects.stream().map(fromMap).collect(toList());
        assertThat(objectsFromIterator, hasSize(objectsFromMap.size()));
        assertThat(objectsFromIterator, containsInAnyOrder(objectsFromMap.toArray()));
    }

    /**
     * Asserts that objects have positions as specified in {@link #X_POSITIONS} and {@link #Y_POSITIONS}.
     *
     * @param <T> the input model object type
     * @param <R> the created model type that is asserted
     * @param accessor retrieves a created model object for an input model object
     * @param objects the input model objects
     * @param positions the positions that are asserted
     */
    public static <T, R extends VisualizationNodeModel> void assertPositions(Function<T, R> accessor, List<T> objects,
            List<Point2D> positions) {
        for (int i = 0; i < objects.size(); ++i) {
            assertThat(accessor.apply(objects.get(i)).getXPosition(), is(closeTo(positions.get(i).getX(), 0.1)));
            assertThat(accessor.apply(objects.get(i)).getYPosition(), is(closeTo(-positions.get(i).getY(), 0.1)));
        }
    }

    public static <T> List<T> createMockList(Class<? extends T> mockType, int count) {
        return Stream.generate(() -> mock(mockType)).limit(count).collect(toList());
    }

}
