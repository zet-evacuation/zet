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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import de.zet_evakuierung.visualization.network.GraphVisualizationData;
import org.zetool.graph.Node;

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
     * Sets up a {@link GraphVisualizationData graph visualization data} mock to return a valid node mapping. Calls to
     * retrieve {@link GraphVisualizationData#getNodesOnLayer(int) nodes by floor} and to get the
     * {@link GraphVisualizationData#getLayer(org.zetool.graph.Node) floor} are mocked.
     *
     * @param visualizationResults the mock object
     * @param nodes the existing nodes, can be mocks or real objects
     * @param nodesOnFloor counts for mocks on which floor
     */
    public static void mockNodeFloorMapping(GraphVisualizationData visualizationResults, List<Node> nodes,
            int... nodesOnFloor) {
        List<List<Node>> nodesByFloor = new ArrayList<>(nodesOnFloor.length);

        int start = 0;
        for (int floor = 0; floor < nodesOnFloor.length; ++floor) {
            List<Node> onCurrentFloor = nodes.subList(start, start + nodesOnFloor[floor]);
            nodesByFloor.add(onCurrentFloor);
            for (Node n : onCurrentFloor) {
                when(visualizationResults.getLayer(n)).thenReturn(floor);
            }
            start += nodesOnFloor[floor];
        }
        for (int i = 0; i < nodesOnFloor.length; ++i) {
            when(visualizationResults.getNodesOnLayer(i)).thenReturn(nodesByFloor.get(i));
        }

    }

    /**
     * Factory class that sets up one level in the object hierarchy. Creates mocks of the model of type
     * {@link modelType}. For each created mock a corresponding internal view model mock of type {@code V} is generated.
     * <p>
     * Sets up the parent mocks to return the respective list of created {@code M} mocks. Sets up the cellular automaton
     * model to return the {@code V} view model mocks for each created model mock.</p>
     * <p>
     * Implementations are for example {@link org.zet.cellularautomaton.Room} as parent type and
     * {@link org.zet.cellularautomaton.EvacCell} as created model type, or
     * {@link de.zet_evakuierung.visualization.ca.model.GLFloorModel floors} as parent type and
     * {@link org.zet.cellularautomaton.Room} as created child models. Multiple such set ups create a hierarchy of
     * mocks.</p>
     *
     * @param <M> type of created model mocks
     * @param <V> type of created internal view model mocks
     */
    public static class MockHierarchyBuilder<M, V> {

        /**
         * The type of model mocks that is to be created, can be a subclass of actual model.
         */
        private final Class<? extends M> modelType;
        /**
         * The type of view model mocks that is to be created, can be a subclass of the actual model.
         */
        private final Class<? extends V> viewModelMockType;
        private int parentCount;
        private Function<Integer, ? extends Iterable<M>> modelMockAccessor;
        private BiFunction<Class<? extends M>, Integer, Iterable<M>> mockListFactory
                = ModelContainerTestUtils::createMockList;
        private Function<M, V> viewModelMockAccessor;
        private int[] childrenInParent;

        private MockHierarchyBuilder(Class<? extends M> modelType, Class<V> viewModelMockType) {
            this.modelType = Objects.requireNonNull(modelType);
            this.viewModelMockType = Objects.requireNonNull(viewModelMockType);
        }

        /**
         *
         * @param <M> type of created model mocks
         * @param <V> type of created internal view model mocks
         * @param modelType the type of mocks that is to be created, can be sub types of {@code M}
         * @param viewModelMockType the type of the internal view model; used to create mocks
         * @return
         */
        public static <M, V> MockHierarchyBuilder<M, V> hierarchyMocks(
                Class<? extends M> modelType, Class<V> viewModelMockType) {
            return new MockHierarchyBuilder<>(modelType, viewModelMockType);
        }

        /**
         *
         * @param parentCount a list of parent mocks for which model mocks are set up
         * @return
         */
        public MockHierarchyBuilder<M, V> forParentCount(int parentCount) {
            assertThat(parentCount, is(greaterThanOrEqualTo(0)));
            this.parentCount = parentCount;
            return this;
        }

        /**
         * Sets a function that provides a mocked method returning a list of model mocks. The input values are the
         * indices of {@link #forParentCount(int) parents}, and the function returns a mocked method call. The method
         * call is set up such, that it returns
         * {@link #withMockListFactory(java.util.function.BiFunction) created model mocks} for the respective parents.
         *
         * @param modelMockAccessor the function that returns the created list of mocks for a parent; to be mocked
         * @return the builder instance
         */
        public MockHierarchyBuilder<M, V> withModelMockAccessor(
                Function<Integer, ? extends Iterable<M>> modelMockAccessor) {
            this.modelMockAccessor = Objects.requireNonNull(modelMockAccessor);
            return this;
        }

        /**
         * Creates a number of mocks of a subtype of the model mock type. Will be called for each
         * {@link #forParentCount(int) parent} to create the respective
         * {@link #withChildrenInParent(int[]) number of children}.
         * <p>
         * Not required to be set, by default {@link #createMockList(java.lang.Class, int) } a list of mocks will be
         * created.</p>
         *
         * @param mockListFactory
         * @return the builder instance
         */
        public MockHierarchyBuilder<M, V> withMockListFactory(
                BiFunction<Class<? extends M>, Integer, Iterable<M>> mockListFactory) {
            this.mockListFactory = Objects.requireNonNull(mockListFactory);
            return this;
        }

        /**
         * Sets a function that provides a mocked method returning a list of internal view model mocks. The input values
         * are the {@link #withMockListFactory(java.util.function.BiFunction) created mock instances}, and the function
         * returns a mocked method call. The method call is set up such, that it returns the corresponding internal view
         * model mock.
         *
         * @param viewModelMockAccessor the function that returns the mock model for a given (created) view model mock;
         * to be mocked
         * @return the builder instance
         */
        public MockHierarchyBuilder<M, V> withViewModelMockAccessor(Function<M, V> viewModelMockAccessor) {
            this.viewModelMockAccessor = Objects.requireNonNull(viewModelMockAccessor);
            return this;
        }

        /**
         * Specifies the number of child model and view mock pairs that should be created for each parent. The length
         * must be equal to the {@link #forParentCount(int) number of parents}.
         *
         * @param childrenInParent the number of child mocks to be created for the respective parents
         * @return the builder instance
         */
        public MockHierarchyBuilder<M, V> withChildrenInParent(int[] childrenInParent) {
            this.childrenInParent = Objects.requireNonNull(childrenInParent);
            return this;
        }

        /**
         * Creates the hierarchy mocks.
         *
         * @return the list of created model mocks, ordered by their parents
         */
        public List<M> build() {
            assertThat(childrenInParent.length, is(equalTo(parentCount)));

            List<M> modelMocks = new ArrayList<>();

            for (int i = 0; i < parentCount; ++i) {
                appendToParent(modelMocks, i);
            }

            return modelMocks;
        }

        private void appendToParent(List<M> modelMocks, int parent) {
            int children = childrenInParent[parent];

            Iterable<M> modelMocksForParent = createModelMocks(parent, children);
            for (M modelMock : modelMocksForParent) {
                modelMocks.add(modelMock);
                createviewModelMock(modelMock);
            }
        }

        private Iterable<M> createModelMocks(int parent, int children) {
            Iterable<M> modelMocksForParent = mockListFactory.apply(modelType, children);
            when(modelMockAccessor.apply(parent)).thenReturn(modelMocksForParent);
            return modelMocksForParent;
        }

        private void createviewModelMock(M modelMock) {
            V viewModelMock = mock(viewModelMockType);
            when(viewModelMockAccessor.apply(modelMock)).thenReturn(viewModelMock);
        }

    }

    /**
     * Creates a list of simple {@link org.mockito.Mockito.mock Mockito mocks} of a certain type.
     *
     * @param <T> the type of the created mocks
     * @param mockType the class of the mocked type
     * @param count the number of objects to be created
     * @return a list of mocks
     */
    public static <T> List<T> createMockList(Class<? extends T> mockType, int count) {
        return Stream.generate(() -> mock(mockType)).limit(count).collect(toList());
    }

}
