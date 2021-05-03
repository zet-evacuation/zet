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
package de.zet_evakuierung.visualization.ca.draw;

import static gui.visualization.VisualizationOptionManager.getInvalidPotentialColor;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.zetool.opengl.framework.util.GLContextAwareThread.createWithGLContext;

import java.awt.Color;
import java.util.function.Function;

import javax.media.opengl.GL2;

import mockit.Injectable;
import org.junit.Test;

import de.zet_evakuierung.visualization.ca.CellularAutomatonVisualizationProperties;
import de.zet_evakuierung.visualization.ca.model.DynamicCellularAutomatonInformation.CellInformationDisplay;
import de.zet_evakuierung.visualization.ca.model.GLCellModel;
import org.zetool.common.util.Direction8;
import org.zetool.opengl.drawingutils.GLColor;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLCellTest {

    private final CellularAutomatonVisualizationProperties DEFAULT_PROPERTIES
            = new CellularAutomatonVisualizationProperties() {
        @Override
        public GLColor getFloorColor() {
            return new GLColor(Color.GREEN);
        }

    };

    /**
     * Simple OpenGL mock to be injected in drawing methods.
     */
    @Injectable
    GL2 glMock;

    @Test
    public void colorUpdatedOnNewSteps() {
        // Init mocks and fixture
        GLCellModel model = mock(GLCellModel.class);
        Function<Direction8, GLColor> neighbourColorMock = mock(Function.class);
        GLCell fixture = createWithGLContext(() -> new GLCell(model, DEFAULT_PROPERTIES, neighbourColorMock));
        assertThat(fixture.color, is(equalTo(new GLColor(Color.GREEN))));

        // Set up test call: if drawn, an update is required and the potential is invalid
        when(model.isUpdateRequired()).thenReturn(true);
        when(model.getDisplayMode()).thenReturn(CellInformationDisplay.STATIC_POTENTIAL);
        when(model.isPotentialValid()).thenReturn(false);

        fixture.performDynamicDrawing(glMock);

        // Verify results: the new color should be set to the invalid color from the visualization properties
        assertThat(fixture.color, is(equalTo(getInvalidPotentialColor())));
    }
}
