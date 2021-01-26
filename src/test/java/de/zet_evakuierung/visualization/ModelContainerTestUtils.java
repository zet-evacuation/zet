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
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.util.ArrayList;
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

    /**
     * Sets up one level in the object hierarchy. Creates mocks of the original cellular automaton model of type
     * {@link modelType}. For each created mock also a view model mock of type {@code V} is generated.
     * <p>
     * Sets up the parent mocks to return the respective list of created {@code M} mocks. Sets up the cellular automaton
     * model to return the {@code V} view model mocks for each created model mock.</p>
     * <p>
     * The number of {@code parentMocks} must be equal as the number of {@code childrenInParent}.</p>
     * <p>
     * Implementations are for example {@link org.zet.cellularautomaton.Room} as parent type and
     * {@link org.zet.cellularautomaton.EvacCell} as created model type, or
     * {@link de.zet_evakuierung.visualization.ca.model.GLFloorModel floors} as parent type and
     * {@link org.zet.cellularautomaton.Room} as created child models. Multiple such set ups create a hierarchy of
     * mocks. </p>
     *
     * @param <M> result model mock type
     * @param <V> visualization model mock type
     * @param parentMockCount a list of parent mocks for which model mocks are set up
     * @param modelType the type of mocks that is to be created, can be sub types of {@code M}
     * @param modelMockAccessor the function that returns the created list of mocks for a parent; to be mocked
     * @param viewModelMockType the type of the internal view model; used to create mocks
     * @param viewModelMockAccessor the function that returns the mock model for a given (created) view model mock; to
     * be mocked
     * @param childrenInParent the number of child mocks to be created for the respective parents
     * @return the list of created model mocks, ordered by their parents
     */
    public static <M, V> List<M> setUp(int parentMockCount, Class<? extends M> modelType,
            Function<Integer, List<M>> modelMockAccessor, Class<V> viewModelMockType,
            Function<M, V> viewModelMockAccessor, int... childrenInParent) {
        List<M> modelMocks = new ArrayList<>();

        for (int i = 0; i < parentMockCount; ++i) {
            int children = childrenInParent[i];
            List<M> modelMocksForParent = createMockList(modelType, children);
            when(modelMockAccessor.apply(i)).thenReturn(modelMocksForParent);

            for (int j = 0; j < children; ++j) {
                V viewModelMock = mock(viewModelMockType);
                modelMocks.add(modelMocksForParent.get(j));
                when(viewModelMockAccessor.apply(modelMocksForParent.get(j))).thenReturn(viewModelMock);
            }
        }

        return modelMocks;
    }

    public static <T> List<T> createMockList(Class<? extends T> mockType, int count) {
        return Stream.generate(() -> mock(mockType)).limit(count).collect(toList());
    }

}
