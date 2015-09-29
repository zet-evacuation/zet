/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package zet.gui.main.toolbar;

import java.awt.MediaTracker;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 * Stores a list of default Icons with filename.
 *
 * @author Jan-Philipp Kappmeier
 */
public enum ZETIconSet {

    EditDrawPointwise("draw_polygon_pointwise.png"),
    EditDrawRectangled("draw_polygon_rectangled.png"),
    EditSelect("selection.png"),
    Exit("exit.png"),
    Open("open.png"),
    Play("play.png"),
    PlayEnd("end.png"),
    PlayPause("pause.png"),
    PlayStart("start.png"),
    PlayLoop("loop.png"),
    PlayStop("stop.png"),
    Rasterize("rasterize.png"),
    Run("run_algorithms.png"),
    Save("save.png"),
    ShowAllFloors("all_floors.png"),
    ShowCellularAutomaton("show_ca.png"),
    ShowDynamicPotential("show_potential_dyn.png"),
    ShowGraph("show_graph.png"),
    ShowGraphGrid("show_graph_grid.png"),
    ShowPotential("show_potential.png"),
    ShowUsage("show_usage.png"),
    ShowWaiting("show_waiting.png"),
    ShowWalls("show_walls.png"),
    Toggle2D3D("viewmode_toggle.png"),
    ToggleOrthogonalIsometric("orthogonal_isometric.png"),
    Video("video.png"),
    ZoomIn("zoom_in.png"),
    ZoomOut("zoom_out.png"),
    // Icons used in the options dialog
    OptionsEditor("draw_polygon_rectangled.png"),
    OptionsFileHandling("document_24.png"),
    OptionsQuickVisualization("show_potential.png"),
    OptionsVisualization("dropbox-icon-24.png"),
    OptionsStatistic("video.png");

    private final static String path = "./icons/";
    private final Icon icon;

    ZETIconSet(String name) {
        ImageIcon imageIcon = new ImageIcon(path + name);
        if( imageIcon.getImageLoadStatus() == MediaTracker.ERRORED) {
            Logger.getGlobal().log(Level.WARNING, "Loading of {0} failed. File not found? Fall back to error icon.", name);
            this.icon = UIManager.getIcon("OptionPane.errorIcon");
        } else {
            this.icon = imageIcon;
        }
    }

    public Icon icon() {
        return icon;
    }
}
