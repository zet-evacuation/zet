/*
 * IconSet.java
 * Created on 19.12.2007, 00:29:35
 */

package gui.components.framework;

/**
 * Stores a list of default Icons with filename.
 * @author Jan-Philipp Kappmeier
 */
public enum IconSet {
	EditDrawPointwise( "draw_polygon_pointwise.png" ),
	EditDrawRectangled( "draw_polygon_rectangled.png" ),
	EditSelect( "selection.png" ),
	Exit( "exit.png" ),
	Open( "open.png" ),
	Play( "play.png" ),
	PlayEnd( "end.png" ),
	PlayPause( "pause.png" ),
	PlayStart( "start.png" ),
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
	
	private String name;
	
	IconSet( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
