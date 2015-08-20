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

/*
 * IconSet.java
 * Created on 19.12.2007, 00:29:35
 */

package zet.gui.main.toolbar;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Stores a list of default Icons with filename.
 * @author Jan-Philipp Kappmeier
 */
public enum ZETIconSet {
	EditDrawPointwise( "draw_polygon_pointwise.png" ),
	EditDrawRectangled( "draw_polygon_rectangled.png" ),
	EditSelect( "selection.png" ),
	Exit( "exit.png" ),
	Open( "open.png" ),
	Play( "play.png" ),
	PlayEnd( "end.png" ),
	PlayPause( "pause.png" ),
	PlayStart( "start.png" ),
	PlayLoop( "loop.png" ),
	PlayStop( "stop.png" ),
	Rasterize( "rasterize.png" ),
	Run( "run_algorithms.png" ),
	Save( "save.png" ),
	ShowAllFloors( "all_floors.png" ),
	ShowCellularAutomaton( "show_ca.png" ),
	ShowDynamicPotential( "show_potential_dyn.png" ),
	ShowGraph( "show_graph.png" ),
	ShowGraphGrid( "show_graph_grid.png" ),
	ShowPotential( "show_potential.png" ),
	ShowUsage( "show_usage.png" ),
	ShowWaiting( "show_waiting.png" ),
	ShowWalls( "show_walls.png" ),
	Toggle2D3D( "viewmode_toggle.png" ),
	ToggleOrthogonalIsometric( "orthogonal_isometric.png" ),
	Video( "video.png" ),
	ZoomIn( "zoom_in.png" ),
	ZoomOut( "zoom_out.png" );
	
	private final static String path = "./icons/";
	private Icon icon;
	
	ZETIconSet( String name ) {
		this.icon = new ImageIcon( path + name );
	}
	
	public Icon icon() {
		return icon;
	}
}
