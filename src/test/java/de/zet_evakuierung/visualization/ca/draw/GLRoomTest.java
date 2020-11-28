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
package de.zet_evakuierung.visualization.ca.draw;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.zetool.opengl.framework.util.GLContextAwareThread.createWithGLContext;
import static org.zetool.test.math.geom.NDimensionalIsCloseTo.closeTo;

import de.zet_evakuierung.visualization.ca.control.CellularAutomatonVisualizationModel;
import ds.PropertyContainer;
import io.visualization.CellularAutomatonVisualizationResults;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zet.cellularautomaton.Room;
import org.zetool.math.vectormath.Vector3;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLRoomTest {

    private final static String GRID_PROPERTY = "options.visualization.view.grid";

    @BeforeClass
    public static void initializeProperty() {
        PropertyContainer.getGlobal().define(GRID_PROPERTY, Boolean.class, true);
    }

    @Test
    public void initialization() throws InterruptedException {
        Room model = mock(Room.class);
        CellularAutomatonVisualizationResults caVisResults = mock(CellularAutomatonVisualizationResults.class);
        when(caVisResults.get(model)).thenReturn(new Vector3());
        CellularAutomatonVisualizationModel visualizationModel = mock(CellularAutomatonVisualizationModel.class);

        GLRoom fixture = createWithGLContext(() -> new GLRoom(model, caVisResults, visualizationModel));

        assertThat(fixture.getModel(), is(sameInstance(model)));
    }

    /**
     * Asserts the coordinates of the bounding box ground floor are computed correct. The coordinates are only used (and
     * set) if the property to display a grid is enabled.
     *
     * @throws InterruptedException -
     */
    @Test
    public void coordinatesCorrect() throws InterruptedException {
        Room model = mock(Room.class);
        int width = 232;
        int height = 422;
        when(model.getWidth()).thenReturn(width);
        when(model.getHeight()).thenReturn(height);
        CellularAutomatonVisualizationResults caVisResults = mock(CellularAutomatonVisualizationResults.class);
        when(caVisResults.get(model)).thenReturn(new Vector3());
        CellularAutomatonVisualizationModel visualizationModel = mock(CellularAutomatonVisualizationModel.class);

        PropertyContainer.getGlobal().set(GRID_PROPERTY, true);
        GLRoom fixture = createWithGLContext(() -> new GLRoom(model, caVisResults, visualizationModel));

        assertThat(fixture.getTopLeft(), is(closeTo(new Vector3(0, 0, -0.1), 0.001)));
        assertThat(fixture.getTopRight(), is(closeTo(new Vector3(width, 0, -0.1), 0.001)));
        assertThat(fixture.getBottomLeft(), is(closeTo(new Vector3(0, -height, -0.1), 0.001)));
        assertThat(fixture.getBottomRight(), is(closeTo(new Vector3(width, -height, -0.1), 0.001)));
    }

    /**
     * Asserts that the ground floor coordinates are not computed if the grid property is disabled.
     *
     * @throws InterruptedException -
     */
    @Test
    public void coordinatesNotDefined() throws InterruptedException {
        Room model = mock(Room.class);
        CellularAutomatonVisualizationResults caVisResults = mock(CellularAutomatonVisualizationResults.class);
        when(caVisResults.get(model)).thenReturn(new Vector3());
        CellularAutomatonVisualizationModel visualizationModel = mock(CellularAutomatonVisualizationModel.class);

        PropertyContainer.getGlobal().set(GRID_PROPERTY, true);
        GLRoom fixture = createWithGLContext(() -> new GLRoom(model, caVisResults, visualizationModel));

        assertThat(fixture.getTopLeft(), is(nullValue()));
        assertThat(fixture.getTopRight(), is(nullValue()));
        assertThat(fixture.getBottomLeft(), is(nullValue()));
        assertThat(fixture.getBottomRight(), is(nullValue()));
    }

}
