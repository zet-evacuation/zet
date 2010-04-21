/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * Camera.java
 * Created on 30.01.2008, 22:07:07
 */

package opengl.framework;

import de.tu_berlin.math.coga.math.vectormath.Vector3;
import java.text.NumberFormat;

/**
 * A camera used for movement in a 3-dimensional world.
 * @author Jan-Philipp Kappmeier
 */
public class Camera {
	/** Direction of view. Initialized as {@code z}-axis. */
	Vector3 view = new Vector3( 0, 0, 1 );			
	/** Upwards direction. Initialized as {@code y}-axis. */
	Vector3 up = new Vector3( 0, 1, 0 );
		/** Position. Initialized in the origin. */
	Vector3 pos = new Vector3( 0, 0, 0 );
	double speed = 20;
	double speedStep = 1;
	double minSpeed = 0;
	double maxSpeed = 100;

	/**
	 * Initializes a camera with position in the origin. The up vector is in
	 * direction of the {@code y}-axis and forward vector is along the
	 * {@code z}-axis.
	 */
	public Camera() {
	}

	/**
	 * Initializes a camera at a specific position. The up vector is in direction
	 * of the {@code y}-axis and forward vector is along the {@code z}-axis. The
	 * passed vector is used as position vector.
	 * @param pos the position of the camera
	 */
	public Camera( Vector3 pos ) {
		setPos( pos );
	}
	
	/**
	 * Initializes a camera at a specific position. The up vector is in direction
	 * of the {@code y}-axis and forward vector is along the {@code z}-axis. The
	 * current position vector is changed to the new coordinates.
	 * @param x the {@code x}-coordinate of the camera position
	 * @param y the {@code y}-coordinate of the camera position
	 * @param z the {@code z}-coordinate of the camera position
	 */
	public Camera( double x, double y, double z ) {
		setPos( x, y, z );
	}

	/**
	 * Returns the current upwards direction vector.
	 * @return the current upwards direction vector
	 */
	public Vector3 getUp() {
		return up;
	}

	/**
	 * The upwards direction of the camera. The current up-vector is replaced by
	 * the passed one.
	 * @param up the new up vector
	 */
	public void setUp( Vector3 up ) {
		this.up = up;
	}

	/**
	 * Sets the upwards direction of the camera. The current up-vector is changed
	 * to the given values.
	 * @param x the {@code x}-coordinate of the up vector
	 * @param y the {@code y}-coordinate of the up vector
	 * @param z the {@code z}-coordinate of the up vector
	 */
	public void setUp( double x, double y, double z ) {
		this.up.set( x, y, z );
	}

	/**
	 * Returns the current view direction vector.
	 * @return the current view direction vector
	 */
	public Vector3 getView() {
		return view;
	}

	/**
	 * Sets a new direction of view for the camera. The current view vector is
	 * replaced by the new one and normalized.
	 * @param view the new view direction vector
	 */
	public void setView( Vector3 view ) {
		this.view = view;
		view.normalize();
	}

	/**
	 * Sets a new direction of view for the camera. The current view vector is
	 * changed to the new coordinates.
	 * @param x the {@code x}-coordinate of the view vector
	 * @param y the {@code y}-coordinate of the view vector
	 * @param z the {@code z}-coordinate of the view vector
	 */
	public void setView( double x, double y, double z ) {
		this.view.set( x, y, z );
	}

	/**
	 * Returns the current position of the camera.
	 * @return the current position of the camera
	 */
	public Vector3 getPos() {
		return pos;
	}

	/**
	 * Sets the current position of the camera. The current position vector is
	 * replaced by the passed one.
	 * @param pos the new position vector
	 */
	public void setPos( Vector3 pos ) {
		this.pos = pos;
	}

	/**
	 * Sets a new position in the 2-dimensional space. A new position vector with
	 * the passed coordinates and zero {@code z}-coordinate is created.
	 * @param x the {@code x}-coordinate of the position
	 * @param y the {@code y}-coordinate of the position
	 */
	public void setPos( double x, double y ) {
		pos = new Vector3( x, y );
	}
	
	/**
	 * Sets a new position for the camera. The current position vector is changed
	 * to the new position.
	 * @param x the {@code x}-coordinate of the position
	 * @param y the {@code y}-coordinate of the position
	 * @param z the {@code z}-coordinate of the position
	 */
	public void setPos( double x, double y, double z ) {
		pos.set( x, y, z );
	}
	
	public void pitch( double angle ) {
	  Vector3 xa;

		xa = view.crossProduct( up ); // view * up;
		view = Vector3.rotateVector( angle, xa, view );
		up = Vector3.rotateVector( angle, xa, up );
	}

	/**
	 * <p>This method pitches but not around the {@code up}-vector but around a
	 * given vector indicating the up direction. </p>
	 * <p>This can be used to allow using the mouse for viewing and calculate the
	 * current view (while mouse motion is in progress) on the base of the
	 * original up-vector.</p>
	 * @param angle the angle
	 * @param initUp the original up-vector
	 */
	public void pitch( double angle, Vector3 initUp ) {
	  final Vector3 xa = view.crossProduct( view ); // view * up;
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
		pos.addTo( view.crossProduct( up ).scalarMultiplicate( -speed ) );
	}
	
	public void stepLeft() {
		pos.addTo( view.crossProduct( up ).scalarMultiplicate( speed ) );
	}
	
	public void stepForward() {
		pos.addTo( view.scalarMultiplicate( speed ) );
	}
	
	public void stepBackward() {
		pos.addTo( view.scalarMultiplicate( -speed ) );
	}
	
	public void stepUp() {
		pos.addTo( up.scalarMultiplicate( speed ) );
	}

	public void stepDown() {
		pos.addTo( up.scalarMultiplicate( -speed ) );
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
	
	/**
	 * Increases the speed, by {@link #getSpeedStep()} but at most to {@link #getMaxSpeed}.
	 */
	public void accelerate() {
		speed = Math.min( speed + speedStep, maxSpeed );
	}
	
	/**
	 * Decreases the speed, by {@link #getSpeedStep()} but at most to {@link #getMinSpeed}.
	 */
	public void decelerate() {
		speed = Math.max( speed - speedStep, minSpeed );
	}

	/**
	 * Returns the current speed of the camera.
	 * @return the current speed of the camera
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Sets the current speed of the camera to a given value. The maximal value
	 * that is set is given by {@link #getMaxSpeed()}. Also negative speeds are
	 * ignored.
	 * @param speed the new speed
	 */
	public void setSpeed( double speed ) {
		this.speed = speed > maxSpeed ? maxSpeed : speed < minSpeed ? minSpeed : speed;
	}

	/**
	 * Returns the current speed step. The step is used to increase or decrease
	 * speed in {@link #accelerate} and {@link #decelerate}
	 * @return
	 */
	public double getSpeedStep() {
		return speedStep;
	}

	/**
	 * Sets a new speed step. The speed step can not be smaller than the current
	 * minimal speed nor higher than the current maximal speed.
	 * @param speedStep the new speed step
	 */
	public void setSpeedStep( double speedStep ) {
		this.speedStep = speedStep < minSpeed ? minSpeed : speedStep > maxSpeed ? maxSpeed : speedStep;
	}

	/**
	 * Returns the current maximum speed.
	 * @return the current maximum speed
	 */
	public double getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * Sets the current maximum speed. The speed is only set if it is larger than
	 * the minimum speed.
	 * @param maxSpeed the new maximum speed
	 */
	public void setMaxSpeed( double maxSpeed ) {
		if( maxSpeed > minSpeed )
			this.maxSpeed = maxSpeed;
	}

	/**
	 * Returns the current minimum speed.
	 * @return the current minimum speed
	 */
	public double getMinSpeed() {
		return minSpeed;
	}

	/**
	 * Sets a new minimum speed. The speed is only set, if it is smaller than the
	 * maximum speed.
	 * @param minSpeed the new minimum speed
	 */
	public void setMinSpeed( double minSpeed ) {
		if( minSpeed < maxSpeed )
			this.minSpeed = minSpeed;
	}

	/**
	 * <p>Returns a string representation of the camera that contains the position
	 * and the vectors for view direction and upwards direction.</p>
	 * <p>A representation looks as follows: <br><br>
	 * (pos_x, pos_y, pos_z)
	 * (view_x, view_y, view_z)
	 * (up_x, up_y, up_z)
	 * </p>
	 * <p>The numbers are printed in the number format of the current locale.</p>
	 * @return String representation of the camera
	 */
	@Override
	public String toString() {
		return pos.toString() + "\n" + view.toString() + "\n" + up.toString();
	}

	/**
	 * <p>Returns a string representation of the camera that contains the position
	 * and the vectors for view direction and upwards direction.</p>
	 * <p>A representation looks as follows: <br><br>
	 * (pos_x, pos_y, pos_z)
	 * (view_x, view_y, view_z)
	 * (up_x, up_y, up_z)
	 * </p>
	 * <p>The numbers are printed in the passed locale.</p>
	 * @param nf the number format used to print the coordinates
	 * @return String representation of the camera
	 */
	public String toString( NumberFormat nf ) {
		return pos.toString( nf ) + "\n" + view.toString( nf ) + "\n" + up.toString( nf );
	}
}
