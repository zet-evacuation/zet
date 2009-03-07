/*
 * Created on 18.06.2008
 *
 */
package gui.visualization.draw.building;

import ds.z.PlanPoint;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.building.GLWallControl;
import gui.visualization.util.VisualizationConstants;
import io.visualization.BuildingResults.Wall;
import java.util.Vector;
import opengl.drawingutils.GLColor;
import opengl.drawingutils.GLVector;
import opengl.framework.abs.AbstractDrawable;
import opengl.helper.Frustum;
import util.vectormath.Vector3;

/**
 * @author Daniel Pluempe, Jan-Philipp Kappmeier
 *
 */
public class GLWall extends AbstractDrawable<GLWall, GLWallControl, GLWallControl> {

	private List<GLVector> basePoints;
	private static final double WALL_HEIGHT = VisualizationOptionManager.getWallHeight() * VisualizationConstants.SIZE_MULTIPLICATOR;
	private GLColor wallColor;

	/**
	 * @param control
	 */
	public GLWall( GLWallControl control ) {
		super( control );
		basePoints = control.getBasePoints();
		wallColor = VisualizationOptionManager.getCellWallColor();
	}

	@Override
	public void performDrawing( GLAutoDrawable drawable ) {
	}

	/**
	 * Draws the walls around rooms. The walls have some thickness and also an upper side.
	 * {@inheritDoc}
	 * @param drawable the OpenGL graphics context to be drawn on
	 */
	@Override
	public void performStaticDrawing( GLAutoDrawable drawable ) {
		final double pi = Math.PI;
		GL gl = drawable.getGL();

		wallColor.performGL( gl );

		gl.glBegin( GL.GL_QUADS );
		Iterator<GLVector> wallStart = basePoints.iterator();
		Iterator<GLVector> wallEnd = basePoints.iterator();
		
		Vector<Vector3> vectors = new Vector<Vector3>( basePoints.size() );
		Vector<Double> angles = new Vector<Double>( basePoints.size() );
		if( wallEnd.hasNext() )
			wallEnd.next();
		while( wallEnd.hasNext() && wallStart.hasNext() ) {
			GLVector start = wallStart.next();
			GLVector end = wallEnd.next();
			Vector3 newVector = start.subtraction( end );
			newVector.normalize();
			vectors.add(newVector);
		}
		for( int i = 0; i < vectors.size()-1; i++ ) {
			angles.add( calcAngle(vectors.get( i ), vectors.get( i+1 ) ) );
		}
		angles.add( calcAngle(vectors.get( vectors.size()-1 ), vectors.get( 0 ) ) );

		for( int i = 0; i < vectors.size(); i++ ) {
			GLVector innerStart = getStart( i, vectors.size() );
			GLVector innerEnd = getEnd( i, vectors.size() );
			GLVector innerStartHigh = getStartHigh( i, vectors.size() );
			GLVector innerEndHigh = getEndHigh( i, vectors.size() );
			GLVector outerStart = new GLVector( innerStart );
			GLVector outerEnd =new GLVector( innerEnd );
			GLVector outerStartHigh = new GLVector( innerStartHigh );
			GLVector outerEndHigh = new GLVector( innerEndHigh );
			
			if( control.getWallType( i ) != Wall.WallType.PASSABLE ) {	// Normal wall, no door
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
//					if( (i > 0 ? control.getWallType( i - 1) : control.getWallType( vectors.size()-1 ) ) == Wall.WallType.PASSABLE ) {
//						// Der vorherige abschnitt ist passierbar!
//						GLVector doorStart = getStart( i-1, vectors.size() );
//						GLVector doorEnd = getEnd( i-1, vectors.size() );
//						GLVector doorStartHigh = getStartHigh( i-1, vectors.size() );
//						GLVector doorEndHigh = getEndHigh( i-1, vectors.size() );
//						// Neue berechnung
//						innerStart = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, false, 1 );
//						innerStartHigh = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, false, 4 );
//					} else {
						innerStartVectorAddition.rotate( -(angleEnd/2)*Frustum.DEG2ANGLE, new Vector3( 0,0,1) );
						innerStartVectorAddition.scalarMultiplicate( getHypothenuseFactor( angleEnd / 2 ) );
						innerStart.add( innerStartVectorAddition );
						innerStartHigh.add( innerStartVectorAddition );
//					}
//					if( (i < vectors.size()-1 ? control.getWallType( i + 1) : control.getWallType( 0 )) == Wall.WallType.PASSABLE ) {
//						GLVector doorStart = getStart( i+1, vectors.size() );
//						GLVector doorEnd = getEnd( i+1, vectors.size() );
//						GLVector doorStartHigh = getStartHigh( i+1, vectors.size() );
//						GLVector doorEndHigh = getEndHigh( i+1, vectors.size() );
//						innerEnd = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, true, 1 );
//						innerEndHigh = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, true, 4 );
//					} else {
						innerEndVectorAddition.rotate( (angleStart/2)*Frustum.DEG2ANGLE, new Vector3( 0,0,1) );
						innerEndVectorAddition.scalarMultiplicate( getHypothenuseFactor( angleStart / 2 ) );
						innerEnd.add( innerEndVectorAddition );
						innerEndHigh.add( innerEndVectorAddition );
//					}
					gl.glVertex3d( innerEnd.x, innerEnd.y, innerEnd.z );
					gl.glVertex3d( innerStart.x, innerStart.y, innerStart.z );
					gl.glVertex3d( innerStartHigh.x, innerStartHigh.y, innerStartHigh.z );
					gl.glVertex3d( innerEndHigh.x, innerEndHigh.y, innerEndHigh.z );
					// Outer stuff
					normal.invert();
					normal.normal( gl );
					outerEndVectorAddition = new GLVector( normal );
					outerStartVectorAddition = new GLVector( normal );
//					if( (i < vectors.size()-1 ? control.getWallType( i + 1) : control.getWallType( 0 )) == Wall.WallType.PASSABLE ) {
//						// Der nächste abschnitt ist passierbar!
//						GLVector doorStart = getStart( i+1, vectors.size() );
//						GLVector doorEnd = getEnd( i+1, vectors.size() );
//						GLVector doorStartHigh = getStartHigh( i+1, vectors.size() );
//						GLVector doorEndHigh = getEndHigh( i+1, vectors.size() );
//						// Neue berechnung
//						outerEnd = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, true, 2 );
//						outerEndHigh = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, true, 3 );
//					} else {
						outerEndVectorAddition.rotate( (angleStart/2)*Frustum.DEG2ANGLE, new Vector3( 0,0,1) );
						outerEndVectorAddition.scalarMultiplicate( getHypothenuseFactor( angleStart / 2 ) );
						outerEnd.add( outerEndVectorAddition );
						outerEndHigh.add( outerEndVectorAddition );
//					}
//					if( (i > 0 ? control.getWallType( i - 1) : control.getWallType( vectors.size()-1 ) ) == Wall.WallType.PASSABLE ) {
//						// Der vorherige abschnitt ist passierbar!
//						GLVector doorStart = getStart( i-1, vectors.size() );
//						GLVector doorEnd = getEnd( i-1, vectors.size() );
//						GLVector doorStartHigh = getStartHigh( i-1, vectors.size() );
//						GLVector doorEndHigh = getEndHigh( i-1, vectors.size() );
//						// Neue berechnung
//						outerStart = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, false, 2 );
//						outerStartHigh = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, false, 3 );
//					} else {
						outerStartVectorAddition.rotate( -(angleEnd/2)*Frustum.DEG2ANGLE, new Vector3( 0,0,1) );
						outerStartVectorAddition.scalarMultiplicate( getHypothenuseFactor( angleEnd / 2 ) );
						outerStart.add( outerStartVectorAddition );
						outerStartHigh.add( outerStartVectorAddition );
//					}
					gl.glVertex3d( outerStart.x, outerStart.y, outerStart.z );
					gl.glVertex3d( outerEnd.x, outerEnd.y, outerEnd.z );
					gl.glVertex3d( outerEndHigh.x, outerEndHigh.y, outerEndHigh.z );
					gl.glVertex3d( outerStartHigh.x, outerStartHigh.y, outerStartHigh.z );
				} else { // Room is right
//					if( (i > 0 ? control.getWallType( i - 1) : control.getWallType( vectors.size()-1 ) ) == Wall.WallType.PASSABLE ) {
//						// Der vorherige abschnitt ist passierbar!
//						GLVector doorStart = getStart( i-1, vectors.size() );
//						GLVector doorEnd = getEnd( i-1, vectors.size() );
//						GLVector doorStartHigh = getStartHigh( i-1, vectors.size() );
//						GLVector doorEndHigh = getEndHigh( i-1, vectors.size() );
//						// Neue berechnung
//						innerStart = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, false, 1 );
//						innerStartHigh = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, false, 4 );
//					} else {
						innerStartVectorAddition.rotate( (angleEnd/2)*Frustum.DEG2ANGLE, new Vector3( 0,0,1) );
						innerStartVectorAddition.scalarMultiplicate( getHypothenuseFactor( angleEnd / 2 ) );
						innerStart.add( innerStartVectorAddition );
						innerStartHigh.add( innerStartVectorAddition );
//					}
//					if( (i < vectors.size()-1 ? control.getWallType( i + 1) : control.getWallType( 0 )) == Wall.WallType.PASSABLE ) {
//						// Der nächste Abschnitt ist passierbar!
//						GLVector doorStart = getStart( i+1, vectors.size() );
//						GLVector doorEnd = getEnd( i+1, vectors.size() );
//						GLVector doorStartHigh = getStartHigh( i+1, vectors.size() );
//						GLVector doorEndHigh = getEndHigh( i+1, vectors.size() );
//						// Neue berechnung
//						innerEnd = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, true, 1 );
//						innerEndHigh = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, true, 4 );
//					} else {
						innerEndVectorAddition.rotate( -(angleStart/2)*Frustum.DEG2ANGLE, new Vector3( 0,0,1) );
						innerEndVectorAddition.scalarMultiplicate( getHypothenuseFactor( angleStart / 2 ) );
						innerEnd.add( innerEndVectorAddition );
						innerEndHigh.add( innerEndVectorAddition );
//					}
					gl.glVertex3d( innerStart.x, innerStart.y, innerStart.z );
					gl.glVertex3d( innerEnd.x, innerEnd.y, innerEnd.z );
					gl.glVertex3d( innerEndHigh.x, innerEndHigh.y, innerEndHigh.z );
					gl.glVertex3d( innerStartHigh.x, innerStartHigh.y, innerStartHigh.z );
					// Outer stuff
					normal.invert();
					normal.normal( gl );
					outerEndVectorAddition = new GLVector( normal );
					outerStartVectorAddition = new GLVector( normal );
//					if( (i < vectors.size()-1 ? control.getWallType( i + 1) : control.getWallType( 0 )) == Wall.WallType.PASSABLE ) {
//						// Der nächste Abschnitt ist passierbar!
//						GLVector doorStart = getStart( i+1, vectors.size() );
//						GLVector doorEnd = getEnd( i+1, vectors.size() );
//						GLVector doorStartHigh = getStartHigh( i+1, vectors.size() );
//						GLVector doorEndHigh = getEndHigh( i+1, vectors.size() );
//						// Neue berechnung
//						outerEnd = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, true, 2 );
//						outerEndHigh = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, true, 3 );
//					} else {
						outerEndVectorAddition.rotate( -(angleStart/2)*Frustum.DEG2ANGLE, new Vector3( 0,0,1) );
						outerEndVectorAddition.scalarMultiplicate( getHypothenuseFactor( angleStart / 2 ) );
						outerEnd.add( outerEndVectorAddition );
						outerEndHigh.add( outerEndVectorAddition );
//					}
//					if( (i > 0 ? control.getWallType( i - 1) : control.getWallType( vectors.size()-1 ) ) == Wall.WallType.PASSABLE ) {
//						// Der vorherige Abschnitt ist passierbar!
//						GLVector doorStart = getStart( i-1, vectors.size() );
//						GLVector doorEnd = getEnd( i-1, vectors.size() );
//						GLVector doorStartHigh = getStartHigh( i-1, vectors.size() );
//						GLVector doorEndHigh = getEndHigh( i-1, vectors.size() );
//						// Neue berechnung
//						outerStart = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, false, 2 );
//						outerStartHigh = this.getDoorFrontCoordinate(doorStart, doorEnd, doorStartHigh, doorEndHigh, false, 3 );
//					} else {
						outerStartVectorAddition.rotate( +(angleEnd/2)*Frustum.DEG2ANGLE, new Vector3( 0,0,1) );
						outerStartVectorAddition.scalarMultiplicate( getHypothenuseFactor( angleEnd / 2 ) );
						outerStart.add( outerStartVectorAddition );
						outerStartHigh.add( outerStartVectorAddition );
//					}
					gl.glVertex3d( outerStart.x, outerStart.y, outerStart.z );
					gl.glVertex3d( outerEnd.x, outerEnd.y, outerEnd.z );
					gl.glVertex3d( outerEndHigh.x, outerEndHigh.y, outerEndHigh.z );
					gl.glVertex3d( outerStartHigh.x, outerStartHigh.y, outerStartHigh.z );
				} // end if room is left/right
				// zeichne oberseite
				gl.glNormal3d( 0, 0, 1);
				gl.glVertex3d( innerStartHigh.x, innerStartHigh.y, innerStartHigh.z );
				gl.glVertex3d( innerEndHigh.x, innerEndHigh.y, innerEndHigh.z );
				gl.glVertex3d( outerEndHigh.x, outerEndHigh.y, outerEndHigh.z );
				gl.glVertex3d( outerStartHigh.x, outerStartHigh.y, outerStartHigh.z );
			} else { // is passable
				// Zeichne für türen die innen liegenden Türseiten (also im Durchgang innen)
				GLVector doorVector = new GLVector( innerEnd.subtraction(innerStart) );
				doorVector.normalize();
				doorVector.normal( gl );
				GLVector dl = getDoorFrontCoordinate( innerStart, innerEnd, innerStartHigh, innerEndHigh, true, 1 );	// In normalenrichtung
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
			// Nutze dl und tl
			// addiere normale zum startpunkt hinzu
			GLVector dl2 = new GLVector( innerStart.addition( normal ) );
			GLVector tl2 = new GLVector( innerStartHigh.addition( normal ) );
			gl.glVertex3d( dl.x, dl.y, dl.z );
			gl.glVertex3d( dl2.x, dl2.y, dl2.z );
			gl.glVertex3d( tl2.x, tl2.y, tl2.z );
			gl.glVertex3d( tl.x, tl.y, tl.z );
			normal.invert();
			normal.normal( gl );
			GLVector dr2 = new GLVector( innerStart.addition( normal ) );
			GLVector tr2 = new GLVector( innerStartHigh.addition( normal ) );
			gl.glVertex3d( dr2.x, dr2.y, dr2.z );
			gl.glVertex3d( dr.x, dr.y, dr.z );
			gl.glVertex3d( tr.x, tr.y, tr.z );
			gl.glVertex3d( tr2.x, tr2.y, tr2.z );
			// zeichne oberseite
			gl.glNormal3d( 0, 0, 1);
			gl.glVertex3d( tr.x, tr.y, tr.z );
			gl.glVertex3d( tl.x, tl.y, tr.z );
			gl.glVertex3d( tl2.x, tl2.y, tl2.z );
			gl.glVertex3d( tr2.x, tr2.y, tr2.z );			
			// Endseite
			doorVector.invert();
			doorVector.normal( gl );
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
			dl2 = new GLVector( innerEnd.addition( normal ) );
			tl2 = new GLVector( innerEndHigh.addition( normal ) );
			gl.glVertex3d( dl.x, dl.y, dl.z );
			gl.glVertex3d( dl2.x, dl2.y, dl2.z );
			gl.glVertex3d( tl2.x, tl2.y, tl2.z );
			gl.glVertex3d( tl.x, tl.y, tl.z );
			normal.invert();
			normal.normal( gl );
			dr2 = new GLVector( innerEnd.addition( normal ) );
			tr2 = new GLVector( innerEndHigh.addition( normal ) );
			gl.glVertex3d( dr2.x, dr2.y, dr2.z );
			gl.glVertex3d( dr.x, dr.y, dr.z );
			gl.glVertex3d( tr.x, tr.y, tr.z );
			gl.glVertex3d( tr2.x, tr2.y, tr2.z );
				// zeichne oberseite
				gl.glNormal3d( 0, 0, 1);
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
		//PlanPoint p = new PlanPoint( v1.x, v1.y );
		//PlanPoint q = new PlanPoint( 0, 0 );
		//PlanPoint r = new PlanPoint( v2.x, v2.y);
		//int orientierung = PlanPoint.orientation(p,r,q);
		int orientierung = Vector3.orientation( v1, v2 );
		if( orientierung == 1 && control.isRoomRight() || orientierung == -1 && control.isRoomLeft() )
			angle = -angle;
		return angle;
	}
	
	// Hilfsmethoden die den startpunkt, endpunkt und höhenpunkte aus der base points-liste berechnen
	private static int mod( int val, int modulo ) {
		return val < 0 ? val % modulo + modulo : val % modulo;
	}
	private GLVector getStart( int segment, int segmentCount ) {
		GLVector start = new GLVector( basePoints.get( mod( segment, segmentCount ) ) );
		start.scalarMultiplicate( VisualizationConstants.SIZE_MULTIPLICATOR );
		return start;
	}
	private GLVector getEnd( int segment, int segmentCount ) {
			GLVector end = new GLVector( basePoints.get( mod( segment+1, segmentCount ) ) ); //segment < segmentCount ? new GLVector( basePoints.get( segment + 1) ) : new GLVector( basePoints.get( 0 ) );
			end.scalarMultiplicate( VisualizationConstants.SIZE_MULTIPLICATOR );
			return end;
	}
	private GLVector getStartHigh( int segment, int segmentCount ) {
			GLVector startHigh = new GLVector( basePoints.get( mod( segment, segmentCount ) ) );
			startHigh.scalarMultiplicate( VisualizationConstants.SIZE_MULTIPLICATOR );
			startHigh.z += WALL_HEIGHT;
			return startHigh;
	}
	// Cannot use this if the start vector is scalar multiplicated already!
	private GLVector getStartHigh( GLVector start ) {
			GLVector startHigh = new GLVector( start );
			startHigh.scalarMultiplicate( VisualizationConstants.SIZE_MULTIPLICATOR );
			startHigh.z += WALL_HEIGHT;
			return startHigh;
	}
	private GLVector getEndHigh( int segment, int segmentCount ) {
			GLVector endHigh = new GLVector( basePoints.get( mod( segment+1, segmentCount ) ) );
			endHigh.scalarMultiplicate( VisualizationConstants.SIZE_MULTIPLICATOR );
			endHigh.z += WALL_HEIGHT;
			return endHigh;
	}
	// Cannot use this if the end vector is scalar multiplicated already!
	private GLVector getEndHigh( GLVector end ) {
			GLVector endHigh = new GLVector( end );
			endHigh.scalarMultiplicate( VisualizationConstants.SIZE_MULTIPLICATOR );
			endHigh.z += WALL_HEIGHT;
			return endHigh;
	}
	private GLVector getDoorFrontCoordinate( GLVector startVec, GLVector endVec, GLVector startVecHigh, GLVector endVecHigh, boolean start, int vecNumber ) {		GLVector ret = new GLVector();
		GLVector doorVector = new GLVector( endVec.subtraction( startVec) );
		doorVector.normalize();
		//doorVector.normal( gl );
		GLVector normal = control.isRoomLeft() ? new GLVector( Vector3.normal( endVec, startVec, startVecHigh ) ) : new GLVector( Vector3.normal( startVec, endVec, endVecHigh ) );
		normal.normalize();
				//GLVector dl = new GLVector( innerStart.addition( doorVector ) );
				//dl.add( normal );
				//GLVector tl = new GLVector( innerStartHigh.addition( doorVector ) );
				//tl.add( normal );
				//normal.invert();
				//GLVector dr = new GLVector( innerStart.addition( doorVector ) );
				//dr.add( normal );
				//GLVector tr = new GLVector( innerStartHigh.addition( doorVector ) );
				//tr.add( normal );
				//gl.glVertex3d( dl.x, dl.y, dl.z );
				//gl.glVertex3d( dr.x, dr.y, dr.z );
				//gl.glVertex3d( tr.x, tr.y, tr.z );
				//gl.glVertex3d( tl.x, tl.y, tl.z );
		if( start ) {
			switch( vecNumber ) {
				case 1:
					ret = new GLVector( startVec.addition( doorVector ) );
					ret.add( normal );
					break;
				case 2:
					normal.invert();
					ret = new GLVector( startVec.addition( doorVector ) );
					ret.add( normal );
					break;
				case 3:
					normal.invert();
					ret = new GLVector( startVecHigh.addition( doorVector ) );
					ret.add( normal );
					break;
				case 4:
					ret = new GLVector( startVecHigh.addition( doorVector ) );
					ret.add( normal );
					break;
				default:
					throw new IllegalArgumentException( "Vector number must be withing the range from 1 to 4.");
			}
		} else { // end 
				doorVector.invert();
//				doorVector.normal( gl );
//				dl = new GLVector( innerEnd.addition( doorVector ) );
//				dl.add( normal );
//				tl = new GLVector( innerEndHigh.addition( doorVector ) );
//				tl.add( normal );
//				normal.invert();
//				dr = new GLVector( innerEnd.addition( doorVector ) );
//				dr.add( normal );
//				tr = new GLVector( innerEndHigh.addition( doorVector ) );
//				tr.add( normal );
			switch( vecNumber ) {
				case 1:
					ret = new GLVector( endVec.addition( doorVector ) );
					ret.add( normal );
					break;
				case 2:
					normal.invert();
					ret = new GLVector( endVec.addition( doorVector ) );
					ret.add( normal );
					break;
				case 3:
					normal.invert();
					ret = new GLVector( endVecHigh.addition( doorVector ) );
					ret.add( normal );
					break;
				case 4:
					ret = new GLVector( endVecHigh.addition( doorVector ) );
					ret.add( normal );
					break;
				default:
					throw new IllegalArgumentException( "Vector number must be withing the range from 1 to 4.");
			}
		}
		return ret;
	}
	
	private double getHypothenuseFactor( double angle ) {
		return 1/ Math.cos( angle );
	}
}
