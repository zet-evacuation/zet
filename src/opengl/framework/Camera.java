/*
 * Camera.java
 * Created on 30.01.2008, 22:07:07
 */

package opengl.framework;

import util.vectormath.Vector3;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Camera {
	Vector3 view = new Vector3( 0, 0, 1 );			// direction of view (z-axis)
	Vector3 up = new Vector3( 0, 1, 0 );				// direction of up   (y-axis)
	Vector3 pos = new Vector3( 0, 0, 0 );			// position
	double speed = 20;
	double speedStep = 1;
	double minSpeed = 0;
	double maxSpeed = 100;

	public Camera() {
		pos = new Vector3();
	}
	
	public Camera( Vector3 pos ) {
		setPos( pos );
	}
	
	public Camera( double x, double y, double z ) {
		setPos( x, y, z );
	}

	public Vector3 getUp() {
		return up;
	}

	public void setUp( Vector3 up ) {
		this.up = up;
	}
	
	public void setUp( double x, double y, double z ) {
		this.up.set( x, y, z );
	}

	public Vector3 getView() {
		return view;
	}

	public void setView( Vector3 view ) {
		this.view = view;
		view.normalize();
	}
	
	public void setView( double x, double y, double z ) {
		this.view.set( x, y, z );
	}
	
	public Vector3 getPos() {
		return pos;
	}
	
	public void setPos( Vector3 pos ) {
		this.pos = pos;
	}
	
	public void setPos( double x, double y ) {
		pos = new Vector3( x, y );
	}
	
	public void setPos( double x, double y, double z ) {
		pos = new Vector3( x, y, z );
	}
	
	public void pitch( double angle ) {
	  Vector3 xa;

		xa = view.crossProduct( up ); // view * up;
		view = Vector3.rotateVector( angle, xa, view );
		up = Vector3.rotateVector( angle, xa, up );
	}

	/**
	 * <p>This method pitches but not around the {@link up}-vector but around a
	 * given vector indicating the up direction. </p>
	 * <p>This can be used to allow using the mouse for viewing and calculate the
	 * current view (while mouse motion is in progress) on the base of the
	 * original up-vector.</p>
	 * @param angle the angle
	 * @param initUp the original up-vector
	 */
	public void pitch( double angle, Vector3 initUp ) {
	  Vector3 xa;

		xa = view.crossProduct( view ); // view * up;
		view = Vector3.rotateVector( angle, xa, view );
		up = Vector3.rotateVector( angle, xa, initUp );
	}

	public void yaw( double angle ) {
		view = Vector3.rotateVector( angle, up, view );
	}
	
	public void rotate( double angle, Vector3 rotateAxis ) {
		view = Vector3.rotateVector( angle, rotateAxis, view );
		up = Vector3.rotateVector( angle, rotateAxis, up );
	}
	
	public void roll( double angle ) {
		up = Vector3.rotateVector( angle, view, up );
	}

	public void stepRight() {
		pos.add( view.crossProduct( up ).scalaryMultiplication( -speed ) );
	}
	
	public void stepLeft() {
		pos.add( view.crossProduct( up ).scalaryMultiplication( speed ) );
	}
	
	public void stepForward() {
		pos.add( view.scalaryMultiplication( speed ) );
	}
	
	public void stepBackward() {
		pos.add( view.scalaryMultiplication( -speed ) );
	}
	
	public void stepUp() {
		pos.add( up.scalaryMultiplication( speed ) );
	}

	public void stepDown() {
		pos.add( up.scalaryMultiplication( -speed ) );
	}
	
	public void strafeLeft() {
		pos.x += speed;
	}
	
	public void strafeRight() {
		pos.x -= speed;
	}
	
	public void strafeForward() {
		pos.y += speed;
	}
	
	public void strafeBackward() {
		pos.y -= speed;
	}
	
	public void strafeUp() {
		pos.z += speed;
	}
	
	public void strafeDown() {
		pos.z -= speed;
	}
	
	public void accelerate() {
		speed = Math.min( speed + speedStep, maxSpeed );
	}
	
	public void decelerate() {
		speed = Math.max( speed - speedStep, minSpeed );
	}
	
	public double getSpeed() {
		return speed;
	}

	public void setSpeed( double speed ) {
		this.speed = speed;
	}

	public double getSpeedStep() {
		return speedStep;
	}

	public void setSpeedStep( double speedStep ) {
		if( speedStep < minSpeed )
			speedStep = minSpeed;
		else if( speedStep > maxSpeed )
			speedStep = maxSpeed;
		else
			this.speedStep = speedStep;
	}
}
