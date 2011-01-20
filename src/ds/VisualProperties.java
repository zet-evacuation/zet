/*
 * VisualProperties.java
 * Created 30.09.2009, 10:50:19
 */

package ds;

import java.util.ArrayList;
import opengl.helper.TextureFontStrings;

/**
 * The class {@code VisualProperties} stores visualization information
 * that is project specific. That could be current camera positions that allow
 * a good view and so forth.
 * @author Jan-Philipp Kappmeier
 */
public class VisualProperties {

	private ArrayList<TextureFontStrings> tfs;
	private ArrayList<CameraPosition> cameraPositions;

	// Used for 2d-view
	private double currentWidth;
	private double currentHeight;

	/**
	 * Creates a new instance of {@code VisualProperties} with default
	 * camera position and empty texts for video intro.
	 */
	public VisualProperties() {
		tfs = new ArrayList<TextureFontStrings>( 1 );
		cameraPositions = new ArrayList<CameraPosition>( 1 );
		tfs.add( new TextureFontStrings( true ) );
		cameraPositions.add( new CameraPosition() );
		currentHeight = 1000;
		currentWidth = 1000;
	}

	public CameraPosition getCameraPosition() {
		return cameraPositions.get( 0 );
	}

	public void setCameraPosition( CameraPosition cameraPosition ) {
		cameraPositions.set(  0, cameraPosition );
	}

	public double getCurrentHeight() {
		return currentHeight;
	}

	public void setCurrentHeight( double currentHeight ) {
		this.currentHeight = currentHeight;
	}

	public double getCurrentWidth() {
		return currentWidth;
	}

	public void setCurrentWidth( double currentWidth ) {
		this.currentWidth = currentWidth;
	}

	public ArrayList<TextureFontStrings> getTextureFontStrings() {
		return tfs;
	}

	public void setTextureFontStrings( ArrayList<TextureFontStrings> textureFontStrings ) {
		tfs = textureFontStrings;
	}
}
