/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
package zet.gui.main.tabs.visualization;

import org.zetool.common.localization.Localization;
import org.zetool.common.localization.LocalizationManager;
import org.zetool.common.util.Formatter;
import org.zetool.common.util.units.TimeUnits;
import ds.PropertyContainer;
import de.zet_evakuierung.model.ZControl;
import gui.GUIControl;
import gui.ZETProperties;
import gui.visualization.Visualization;
import gui.visualization.control.ZETGLControl;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import org.zetool.opengl.drawingutils.GLColor;
import org.zetool.opengl.helper.ProjectionHelper;
import zet.gui.GUILocalization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings("serial")
public class ZETVisualization extends Visualization<ZETGLControl> {

    /** The GUI localization class. */
    Localization loc = GUILocalization.loc;
    /** The minimal number of frames that needs to be captured in movie rendering mode for the cellular automaton. */
    private int minimalFrameCountCellularAutomaton;
    /** The minimal number of frames that needs to be captured in movie rendering mode for the graph. */
    private int minimalFrameCountGraph;

    private boolean showTimestepGraph = ZETProperties.isShowTimestepGraph();
    private boolean showTimestepCellularAutomaton = ZETProperties.isShowTimestepCellularAutomaton();
    private final GUIControl guiControl;
    private ZControl zcontrol;

    public ZETVisualization(GLCapabilities capabilities, GUIControl guiControl) {
        super(capabilities);

        //setBackground( Color.WHITE ); // for report
        this.guiControl = guiControl;
        showEye = ZETProperties.isShowEye();
        showFPS = ZETProperties.isShowFPS();

        noRotate = !PropertyContainer.getGlobal().getAsBoolean("editor.options.visualization.allowRotateIn2D");
        mouseInvert = PropertyContainer.getGlobal().getAsBoolean("editor.options.visualization.invertMouse") ? -1 : 1;
        scrollInvert = PropertyContainer.getGlobal().getAsBoolean("editor.options.visualization.invertScroll") ? 1 : -1;

        // this will create errors!
//		if( PropertyContainer.getGlobal().getAsBoolean( "settings.gui.visualization.2d" ) )
//			set2DView();
//		else
//			set3DView();
//		if( PropertyContainer.getGlobal().getAsBoolean( "settings.gui.visualization.isometric" ) )
//			this.setParallelViewMode( ParallelViewMode.Isometric );
//		else
//			this.setParallelViewMode( ParallelViewMode.Orthogonal );
        setControl(new ZETGLControl());
    }

    public void setZcontrol(ZControl zcontrol) {
        this.zcontrol = zcontrol;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // TODO improve option handling
        //showEye = PropertyContainer.getGlobal().getAsBoolean( "options.visualization.elements.eye" );
        //showFPS = PropertyContainer.getGlobal().getAsBoolean( "options.visualization.elements.fps" );
        //showTimestepGraph = PropertyContainer.getGlobal().getAsBoolean( "options.visualization.elements.timestepGraph" );
        //showTimestepCellularAutomaton = PropertyContainer.getGlobal().getAsBoolean( "options.visualization.elements.timestepCA" );
        //System.out.println( "display" );
        super.display(drawable);
    }

    /**
     * Draws the current frame rate on the lower left edge of the screen and the current time of the cellular automaton
     * and graph, if used.
     */
    @Override
    final protected void drawFPS() {
        super.drawFPS();
        ProjectionHelper.setPrintScreenProjection(gl, getViewportWidth(), getViewportHeight());
        GLColor.white.draw(gl);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);// Copy Image 2 Color To The Screen
        gl.glEnable(GL.GL_TEXTURE_2D);
        fontTex.bind();

        boolean finished = true;

        // TODO gehört hier nicht rein!
        int row = 1;
        if (control.hasCellularAutomaton()) {
            if (control.isCaFinshed()) {
                minimalFrameCountCellularAutomaton--;
                if (showTimestepCellularAutomaton) {
                    // TODO updateinformationen anzeigen
                    font.print(0, this.getHeight() - (row++) * fontSize,
                            loc.getString("gui.VisualizationPanel.FPS.Simulation.Finished"));
                    font.print(0, this.getHeight() - (row++) * fontSize, loc.getString(
                            "gui.VisualizationPanel.FPS.Simulation.Needed") + " "
                            + Formatter.formatUnit(control.getCaStep() * control.getCaSecondsPerStep(),
                                    TimeUnits.SECOND));
                }
            } else {
                finished = false;
                minimalFrameCountCellularAutomaton = 2;
                if (showTimestepCellularAutomaton) {
                    font.print(0, this.getHeight() - (row++) * fontSize, loc.getString(
                            "gui.VisualizationPanel.FPS.Simulation.Step") + " "
                            + LocalizationManager.getManager().getFloatConverter().format(control.getCaStep()));
                    font.print(0, this.getHeight() - (row++) * fontSize, loc.getString(
                            "gui.VisualizationPanel.FPS.Simulation.Time") + " "
                            + Formatter.formatUnit(control.getCaStep() * control.getCaSecondsPerStep(),
                                    TimeUnits.SECOND));
                }
            }
            row++;
        }
        if (control.hasGraph()) {
            if (control.isGraphFinished()) {
                minimalFrameCountGraph--;
                if (showTimestepGraph) {
                    font.print(0, this.getHeight() - (row++) * fontSize,
                            loc.getString("gui.VisualizationPanel.FPS.Graph.Finished"));
                    font.print(0, this.getHeight() - (row++) * fontSize, loc.getString(
                            "gui.VisualizationPanel.FPS.Graph.Needed") + " "
                            + Formatter.formatUnit(control.getGraphStep() * control.getGraphSecondsPerStep(),
                                    TimeUnits.SECOND));
                }
            } else {
                finished = false;
                minimalFrameCountGraph = 2;
                if (showTimestepGraph) {
                    font.print(0, this.getHeight() - (row++) * fontSize, loc.getString(
                            "gui.VisualizationPanel.FPS.Graph.Step") + " "
                            + LocalizationManager.getManager().getFloatConverter().format(control.getGraphStep()));
                    font.print(0, this.getHeight() - (row++) * fontSize, loc.getString(
                            "gui.VisualizationPanel.FPS.Graph.Time") + " "
                            + Formatter.formatUnit(control.getGraphStep() * control.getGraphSecondsPerStep(),
                                    TimeUnits.SECOND));
                }
            }
            row++;
        }

        if (finished && isAnimating()) {
            guiControl.animationFinished();
        }

        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_BLEND);
        ProjectionHelper.resetProjection(gl);
    }

    // TODO: wenn eine Belegung keine Typen enthält, gibts eine Exception anstelle einer Warnung
    @Override
    @SuppressWarnings("fallthrough")
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_C:
                System.out.println(loc.getStringWithoutPrefix("gui.VisualizationPanel.Camera.Information"));
                System.out.println(getCamera());
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                guiControl.updateCameraInformation();
                zcontrol.getProject().getVisualProperties().getCameraPosition().pos = getCamera().getPos();
                zcontrol.getProject().getVisualProperties().getCameraPosition().view = getCamera().getView();
                zcontrol.getProject().getVisualProperties().getCameraPosition().up = getCamera().getUp();
                zcontrol.getProject().getVisualProperties().setCurrentWidth(getViewWidth());
                zcontrol.getProject().getVisualProperties().setCurrentHeight(getViewHeight());
            default:
                super.keyPressed(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        guiControl.updateCameraInformation();
        zcontrol.getProject().getVisualProperties().getCameraPosition().pos = getCamera().getPos();
        zcontrol.getProject().getVisualProperties().getCameraPosition().view = getCamera().getView();
        zcontrol.getProject().getVisualProperties().getCameraPosition().up = getCamera().getUp();
        zcontrol.getProject().getVisualProperties().setCurrentWidth(getViewWidth());
        zcontrol.getProject().getVisualProperties().setCurrentHeight(getViewHeight());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        guiControl.updateCameraInformation();
        zcontrol.getProject().getVisualProperties().getCameraPosition().pos = getCamera().getPos();
        zcontrol.getProject().getVisualProperties().getCameraPosition().view = getCamera().getView();
        zcontrol.getProject().getVisualProperties().getCameraPosition().up = getCamera().getUp();
        zcontrol.getProject().getVisualProperties().setCurrentWidth(getViewWidth());
        zcontrol.getProject().getVisualProperties().setCurrentHeight(getViewHeight());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);
        guiControl.updateCameraInformation();
        zcontrol.getProject().getVisualProperties().getCameraPosition().pos = getCamera().getPos();
        zcontrol.getProject().getVisualProperties().getCameraPosition().view = getCamera().getView();
        zcontrol.getProject().getVisualProperties().getCameraPosition().up = getCamera().getUp();
        zcontrol.getProject().getVisualProperties().setCurrentWidth(getViewWidth());
        zcontrol.getProject().getVisualProperties().setCurrentHeight(getViewHeight());
    }
}
