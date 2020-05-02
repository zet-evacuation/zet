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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package gui.visualization.draw.building;

import org.zetool.math.Conversion;
import org.zetool.math.vectormath.Vector3;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.building.GLWallControl;
import io.visualization.BuildingResults.Wall;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.zetool.opengl.drawingutils.GLColor;
import org.zetool.opengl.drawingutils.GLVector;
import org.zetool.opengl.framework.abs.AbstractDrawable;

/**
 * @author Daniel R. Schmidt
 * @author Jan-Philipp Kappmeier
 *
 */
public class GLWall extends AbstractDrawable<GLWall, GLWallControl> {

	private List<GLVector> basePoints;
	private static final double WALL_HEIGHT = VisualizationOptionManager.getWallHeight() * 0.01;
	private GLColor wallColor;
	private boolean barrier = false;

	/**
	 * @param control
	 */
	public GLWall( GLWallControl control ) {
		super( control );
		basePoints = control.getBasePoints();
    //wallColor = VisualizationOptionManager.getCellWallColor();
    wallColor = VisualizationOptionManager.getCellFloorColor();
    barrier = control.isBarrier();
	}

	@Override
	public void performDrawing( GL2 gl ) {
	}

	/**
	 * Draws the walls around rooms. The walls have some thickness and also an upper side.
	 * {@inheritDoc}
	 * @param gl
	 */
	@Override
	public void performStaticDrawing( GL2 gl ) {
		gl.glBegin( GL2.GL_POLYGON );
    //VisualizationOptionManager.getCellFloorColor().draw( gl );
    //VisualizationOptionManager.getCellWallColor().draw( gl );
    VisualizationOptionManager.getCellWallColor().draw( gl );
    final Vector3 perturbate = new Vector3( 0, 0, -0.5 ); // add a small offset to move the floor under the ca floor-level
		for( GLVector v : basePoints )
			v.add( perturbate ).draw( gl );
		gl.glEnd();

		final double pi = Math.PI;

		wallColor.draw( gl );

		gl.glBegin( GL2.GL_QUADS );
		Iterator<GLVector> wallStart = basePoints.iterator();
		Iterator<GLVector> wallEnd = basePoints.iterator();

		Vector<Vector3> vectors = new Vector<>( basePoints.size() );
		Vector<Double> angles = new Vector<>( basePoints.size() );
		if( wallEnd.hasNext() )
			wallEnd.next();
		while( wallEnd.hasNext() && wallStart.hasNext() ) {
			GLVector start = wallStart.next();
			GLVector end = wallEnd.next();
			Vector3 newVector = start.sub( end );
			newVector.normalize();
			vectors.add(newVector);
		}
		for( int i = 0; i < vectors.size()-1; i++ )
			angles.add( calcAngle(vectors.get( i ), vectors.get( i+1 ) ) );
		angles.add( calcAngle(vectors.get( vectors.size()-1 ), vectors.get( 0 ) ) );

		for( int i = 0; i < vectors.size(); i++ ) {
			GLVector innerStart = getStart( i, vectors.size() );
			GLVector innerEnd = getEnd( i, vectors.size() );
			GLVector innerStartHigh = getStartHigh( i, vectors.size() );
			GLVector innerEndHigh = getEndHigh( i, vectors.size() );
			GLVector outerStart = new GLVector( innerStart );
			GLVector outerEnd = new GLVector( innerEnd );
			GLVector outerStartHigh = new GLVector( innerStartHigh );
			GLVector outerEndHigh = new GLVector( innerEndHigh );

			if( control.getWallType( i ) != Wall.ElementType.PASSABLE ) {	// Normal wall, no door
				GLVector normal = control.isRoomLeft() ? new GLVector( Vector3.normal( innerEnd, innerStart, innerStartHigh ) ) : new GLVector( Vector3.normal( innerStart, innerEnd, innerEndHigh ) );
				normal.normalize();
				normal.normal( gl );
				double angleStart;
				double angleEnd;
				angleStart = angles.get(i);
				angleEnd = i != 0 ? angles.get( i-1 ) : angles.get( vectors.size()-1 );

				// Vektor, der zum Startpunkt hinzugefügt wird
				GLVector innerStartVectorAddition = new GLVector( normal );
				GLVector outerStartVectorAddition;
				// Vektor, der zum Endpunkt hinzugefügt wird
				GLVector innerEndVectorAddition = new GLVector( normal );
				GLVector outerEndVectorAddition;

				if( control.isRoomLeft() ) {
					innerStartVectorAddition.rotate( -(angleEnd/2)*Conversion.DEG2ANGLE, new Vector3( 0,0,1) );
					innerStartVectorAddition.scalarMultiplicateTo( getHypothenuseFactor( angleEnd / 2 )* 0.1 );
					innerStart.addTo( innerStartVectorAddition );
					innerStartHigh.addTo( innerStartVectorAddition );
					innerEndVectorAddition.rotate( (angleStart/2)*Conversion.DEG2ANGLE, new Vector3( 0,0,1) );
					innerEndVectorAddition.scalarMultiplicateTo( getHypothenuseFactor( angleStart / 2 )* 0.1 );
					innerEnd.addTo( innerEndVectorAddition );
					innerEndHigh.addTo( innerEndVectorAddition );
					gl.glVertex3d( innerEnd.x, innerEnd.y, innerEnd.z );
					gl.glVertex3d( innerStart.x, innerStart.y, innerStart.z );
					gl.glVertex3d( innerStartHigh.x, innerStartHigh.y, innerStartHigh.z );
					gl.glVertex3d( innerEndHigh.x, innerEndHigh.y, innerEndHigh.z );
					// Outer stuff
					normal.invert();
					normal.normal( gl );
					outerEndVectorAddition = new GLVector( normal );
					outerStartVectorAddition = new GLVector( normal );
					outerEndVectorAddition.rotate( (angleStart/2)*Conversion.DEG2ANGLE, new Vector3( 0,0,1) );
					outerEndVectorAddition.scalarMultiplicateTo( getHypothenuseFactor( angleStart / 2 ) * 0.1);
					outerEnd.addTo( outerEndVectorAddition );
					outerEndHigh.addTo( outerEndVectorAddition );
					outerStartVectorAddition.rotate( -(angleEnd/2)*Conversion.DEG2ANGLE, new Vector3( 0,0,1) );
					outerStartVectorAddition.scalarMultiplicateTo( getHypothenuseFactor( angleEnd / 2 ) * 0.1);
					outerStart.addTo( outerStartVectorAddition );
					outerStartHigh.addTo( outerStartVectorAddition );
					gl.glVertex3d( outerStart.x, outerStart.y, outerStart.z );
					gl.glVertex3d( outerEnd.x, outerEnd.y, outerEnd.z );
					gl.glVertex3d( outerEndHigh.x, outerEndHigh.y, outerEndHigh.z );
					gl.glVertex3d( outerStartHigh.x, outerStartHigh.y, outerStartHigh.z );
				} else { // Room is right
					if( Math.cos( angleEnd * 0.5 ) > 0.000001 ) { // added
						innerStartVectorAddition.rotate( (angleEnd/2)*Conversion.DEG2ANGLE, new Vector3( 0,0,1) );
						innerStartVectorAddition.scalarMultiplicateTo( getHypothenuseFactor( angleEnd / 2 )* 0.1 );
					} // added
					innerStart.addTo( innerStartVectorAddition );
					innerStartHigh.addTo( innerStartVectorAddition );

					if( Math.cos( angleStart * 0.5 ) > 0.00001 ) {
						innerEndVectorAddition.rotate( -(angleStart/2)*Conversion.DEG2ANGLE, new Vector3( 0,0,1) );
						innerEndVectorAddition.scalarMultiplicateTo( getHypothenuseFactor( angleStart / 2 )* 0.1 );
					}
					innerEnd.addTo( innerEndVectorAddition );
					innerEndHigh.addTo( innerEndVectorAddition );
					gl.glVertex3d( innerStart.x, innerStart.y, innerStart.z );
					gl.glVertex3d( innerEnd.x, innerEnd.y, innerEnd.z );
					gl.glVertex3d( innerEndHigh.x, innerEndHigh.y, innerEndHigh.z );
					gl.glVertex3d( innerStartHigh.x, innerStartHigh.y, innerStartHigh.z );
					// Outer stuff
					normal.invert();
					normal.normal( gl );
					outerEndVectorAddition = new GLVector( normal );
					outerStartVectorAddition = new GLVector( normal );
					if( Math.cos( angleStart * 0.5 ) > 0.000001 ) { // added
						outerEndVectorAddition.rotate( -(angleStart/2)*Conversion.DEG2ANGLE, new Vector3( 0,0,1) );
						outerEndVectorAddition.scalarMultiplicateTo( getHypothenuseFactor( angleStart / 2 ) * 0.1);
					}
					outerEnd.addTo( outerEndVectorAddition );
					outerEndHigh.addTo( outerEndVectorAddition );
					if( Math.cos( angleEnd * 0.5 ) > 0.000001 ) { // added
						outerStartVectorAddition.rotate( +(angleEnd/2)*Conversion.DEG2ANGLE, new Vector3( 0,0,1) );
						outerStartVectorAddition.scalarMultiplicateTo( getHypothenuseFactor( angleEnd / 2 ) * 0.1);
					}
					outerStart.addTo( outerStartVectorAddition );
					outerStartHigh.addTo( outerStartVectorAddition );
					gl.glVertex3d( outerStart.x, outerStart.y, outerStart.z );
					gl.glVertex3d( outerEnd.x, outerEnd.y, outerEnd.z );
					gl.glVertex3d( outerEndHigh.x, outerEndHigh.y, outerEndHigh.z );
					gl.glVertex3d( outerStartHigh.x, outerStartHigh.y, outerStartHigh.z );
				} // end if room is left/right
				// Special-Zeichnen falls angle = pi:
				// nur eine richtung, normale manchmal nicht korrekt, sondern 180° zu stark!
				if( angleEnd - pi < 0.000001 && i < ((vectors.size()+1) / 2) ) {
					Vector3 n = vectors.get( i );
					gl.glNormal3d( n.x, n.y, n.z );
					// innerstart, outer start
					gl.glVertex3d( innerStart.x, innerStart.y, innerStart.z );
					gl.glVertex3d( innerStartHigh.x, innerStartHigh.y, innerStartHigh.z );
					gl.glVertex3d( outerStartHigh.x, outerStartHigh.y, outerStartHigh.z );
					gl.glVertex3d( outerStart.x, outerStart.y, outerStart.z );
				}
				if( angleStart - pi < 0.000001 && i < ((vectors.size()+1) / 2) ) {
					Vector3 n = vectors.get( i );
					gl.glNormal3d( n.x, n.y, n.z );
					gl.glVertex3d( innerEnd.x, innerEnd.y, innerEnd.z );
					gl.glVertex3d( innerEndHigh.x, innerEndHigh.y, innerEndHigh.z );
					gl.glVertex3d( outerEndHigh.x, outerEndHigh.y, outerEndHigh.z );
					gl.glVertex3d( outerEnd.x, outerEnd.y, outerEnd.z );
				}
				// zeichne oberseite
				gl.glNormal3d( 0, 0, 1);
				gl.glVertex3d( innerStartHigh.x, innerStartHigh.y, innerStartHigh.z );
				gl.glVertex3d( innerEndHigh.x, innerEndHigh.y, innerEndHigh.z );
				gl.glVertex3d( outerEndHigh.x, outerEndHigh.y, outerEndHigh.z );
				gl.glVertex3d( outerStartHigh.x, outerStartHigh.y, outerStartHigh.z );
			} else {
				// is passable
				// Zeichne für türen die innen liegenden Türseiten (also im Durchgang innen)
				GLVector doorVector = new GLVector( innerEnd.sub( innerStart ) );
				doorVector.normalize();
				doorVector.normal( gl );
				GLVector dl = getDoorFrontCoordinate( innerStart, innerEnd, innerStartHigh, innerEndHigh, true, 1 ); // In normalenrichtung
				GLVector dr = getDoorFrontCoordinate( innerStart, innerEnd, innerStartHigh, innerEndHigh, true, 2 );
				GLVector tr = getDoorFrontCoordinate( innerStart, innerEnd, innerStartHigh, innerEndHigh, true, 3 );
				GLVector tl = getDoorFrontCoordinate( innerStart, innerEnd, innerStartHigh, innerEndHigh, true, 4 ); // In normalenrichtung
				gl.glVertex3d( dl.x, dl.y, dl.z );
				gl.glVertex3d( dr.x, dr.y, dr.z );
				gl.glVertex3d( tr.x, tr.y, tr.z );
				gl.glVertex3d( tl.x, tl.y, tl.z );
				// Startseite
				GLVector normal = control.isRoomLeft() ? new GLVector( Vector3.normal( innerEnd, innerStart, innerStartHigh ) ) : new GLVector( Vector3.normal( innerStart, innerEnd, innerEndHigh ) );
				normal.normalize();
				normal.normal( gl );
				normal.scalarMultiplicateTo( 0.1 );
				// Nutze dl und tl
				// addiere normale zum startpunkt hinzu
				GLVector dl2 = new GLVector( innerStart.add( normal ) );
				GLVector tl2 = new GLVector( innerStartHigh.add( normal ) );
				gl.glVertex3d( dl.x, dl.y, dl.z );
				gl.glVertex3d( dl2.x, dl2.y, dl2.z );
				gl.glVertex3d( tl2.x, tl2.y, tl2.z );
				gl.glVertex3d( tl.x, tl.y, tl.z );
				normal.invert();
				normal.normal( gl );
				GLVector dr2 = new GLVector( innerStart.add( normal ) );
				GLVector tr2 = new GLVector( innerStartHigh.add( normal ) );
				gl.glVertex3d( dr2.x, dr2.y, dr2.z );
				gl.glVertex3d( dr.x, dr.y, dr.z );
				gl.glVertex3d( tr.x, tr.y, tr.z );
				gl.glVertex3d( tr2.x, tr2.y, tr2.z );
				// zeichne oberseite
				gl.glNormal3d( 0, 0, 1 );
				gl.glVertex3d( tr.x, tr.y, tr.z );
				gl.glVertex3d( tl.x, tl.y, tr.z );
				gl.glVertex3d( tl2.x, tl2.y, tl2.z );
				gl.glVertex3d( tr2.x, tr2.y, tr2.z );
				// Endseite
				doorVector.invert();
				doorVector.normal( gl );
				doorVector.scalarMultiplicateTo( 0.1 );
				dl = getDoorFrontCoordinate( innerStart, innerEnd, innerStartHigh, innerEndHigh, false, 1 );
				dr = getDoorFrontCoordinate( innerStart, innerEnd, innerStartHigh, innerEndHigh, false, 2 );
				tr = getDoorFrontCoordinate( innerStart, innerEnd, innerStartHigh, innerEndHigh, false, 3 );
				tl = getDoorFrontCoordinate( innerStart, innerEnd, innerStartHigh, innerEndHigh, false, 4 );
				gl.glVertex3d( dl.x, dl.y, dl.z );
				gl.glVertex3d( dr.x, dr.y, dr.z );
				gl.glVertex3d( tr.x, tr.y, tr.z );
				gl.glVertex3d( tl.x, tl.y, tl.z );
				normal = control.isRoomLeft() ? new GLVector( Vector3.normal( innerEnd, innerStart, innerStartHigh ) ) : new GLVector( Vector3.normal( innerStart, innerEnd, innerEndHigh ) );
				normal.normalize();
				normal.normal( gl );
				// Nutze dl und tl
				// addiere normale zum startpunkt hinzu
				normal.scalarMultiplicateTo( 0.1 );
				dl2 = new GLVector( innerEnd.add( normal ) );
				tl2 = new GLVector( innerEndHigh.add( normal ) );
				gl.glVertex3d( dl.x, dl.y, dl.z );
				gl.glVertex3d( dl2.x, dl2.y, dl2.z );
				gl.glVertex3d( tl2.x, tl2.y, tl2.z );
				gl.glVertex3d( tl.x, tl.y, tl.z );
				normal.normalize();
				normal.normal( gl );
				normal.invert();
				normal.scalarMultiplicateTo( 0.1 );
				dr2 = new GLVector( innerEnd.add( normal ) );
				tr2 = new GLVector( innerEndHigh.add( normal ) );
				gl.glVertex3d( dr2.x, dr2.y, dr2.z );
				gl.glVertex3d( dr.x, dr.y, dr.z );
				gl.glVertex3d( tr.x, tr.y, tr.z );
				gl.glVertex3d( tr2.x, tr2.y, tr2.z );
				// zeichne oberseite
				gl.glNormal3d( 0, 0, 1 );
				gl.glVertex3d( tr.x, tr.y, tr.z );
				gl.glVertex3d( tl.x, tl.y, tr.z );
				gl.glVertex3d( tl2.x, tl2.y, tl2.z );
				gl.glVertex3d( tr2.x, tr2.y, tr2.z );
			}
		}
		gl.glEnd();
	}

	/** {@inheritDoc}
	 * @see opengl.framework.abs.AbstractDrawable#update()
	 */
	@Override
	public void update() {
	}

	/**
	 * Calculates the angle between two normalized vectors. The calculation includes the orientation, thus the
	 * returned angle is within the interval 0 and 2*PI
	 * @param v1 one vector
	 * @param v2 the other vector
	 * @return the angle
	 */
	private double calcAngle( Vector3 v1, Vector3 v2 ) {
		double angle = Math.acos( v1.dotProduct( v2 ) );
		int orientation = Vector3.orientation( v1, v2 );
		if( orientation == -1 && control.isRoomRight() || orientation == 1 && control.isRoomLeft() )
			angle = -angle;
		return angle;
	}

	// Hilfsmethoden die den startpunkt, endpunkt und höhenpunkte aus der base points-liste berechnen
	private static int mod( int val, int modulo ) {
		return val < 0 ? val % modulo + modulo : val % modulo;
	}
	private GLVector getStart( int segment, int segmentCount ) {
		return new GLVector( basePoints.get( mod( segment, segmentCount ) ) );
	}

	private GLVector getEnd( int segment, int segmentCount ) {
		return new GLVector( basePoints.get( mod( segment + 1, segmentCount ) ) ); //segment < segmentCount ? new GLVector( basePoints.get( segment + 1) ) : new GLVector( basePoints.get( 0 ) );
	}

	private GLVector getStartHigh( int segment, int segmentCount ) {
		GLVector startHigh = new GLVector( basePoints.get( mod( segment, segmentCount ) ) );
		startHigh.z += WALL_HEIGHT;
		return startHigh;
	}

	private GLVector getEndHigh( int segment, int segmentCount ) {
		GLVector endHigh = new GLVector( basePoints.get( mod( segment + 1, segmentCount ) ) );
		endHigh.z += WALL_HEIGHT;
		return endHigh;
	}

	private GLVector getDoorFrontCoordinate( GLVector startVec, GLVector endVec, GLVector startVecHigh, GLVector endVecHigh, boolean start, int vecNumber ) {		GLVector ret = new GLVector();
		GLVector doorVector = new GLVector( endVec.sub( startVec) );
		doorVector.normalize();
		doorVector.scalarMultiplicateTo( 0.1 ); // improvement!
		//doorVector.normal( gl );
		GLVector normal = control.isRoomLeft() ? new GLVector( Vector3.normal( endVec, startVec, startVecHigh ) ) : new GLVector( Vector3.normal( startVec, endVec, endVecHigh ) );
		normal.normalize();
		normal.scalarMultiplicateTo( 0.1 );

		if( start ) {
			switch( vecNumber ) {
				case 1:
					ret = new GLVector( startVec.add( doorVector ) );
					ret.addTo( normal );
					break;
				case 2:
					normal.invert();
					ret = new GLVector( startVec.add( doorVector ) );
					ret.addTo( normal );
					break;
				case 3:
					normal.invert();
					ret = new GLVector( startVecHigh.add( doorVector ) );
					ret.addTo( normal );
					break;
				case 4:
					ret = new GLVector( startVecHigh.add( doorVector ) );
					ret.addTo( normal );
					break;
				default:
					throw new IllegalArgumentException( "Vector number must be withing the range from 1 to 4.");
			}
		} else { // end
				doorVector.invert();

				switch( vecNumber ) {
				case 1:
					ret = new GLVector( endVec.add( doorVector ) );
					ret.addTo( normal );
					break;
				case 2:
					normal.invert();
					ret = new GLVector( endVec.add( doorVector ) );
					ret.addTo( normal );
					break;
				case 3:
					normal.invert();
					ret = new GLVector( endVecHigh.add( doorVector ) );
					ret.addTo( normal );
					break;
				case 4:
					ret = new GLVector( endVecHigh.add( doorVector ) );
					ret.addTo( normal );
					break;
				default:
					throw new IllegalArgumentException( "Vector number must be withing the range from 1 to 4.");
			}
		}
		return ret;
	}

	private double getHypothenuseFactor( double angle ) {
		if( Math.cos( angle ) < 0.000001 )
			return 0;
		return 1/ Math.cos( angle );
	}
}
