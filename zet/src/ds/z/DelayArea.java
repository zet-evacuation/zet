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
 * DelayArea.java
 * Created on 26. November 2007, 21:32
 */

package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Represents a delayArea.
 * This type of area has an speedFactor, which has an influence on the speed of
 * the evacuees in this area. Every delayArea is associated to exactly one
 * {@link Room} at every time.
 */
@XStreamAlias ("delayArea")
public class DelayArea extends Area<Edge> {
	/** Gives the GUI an indication what the source of the delay is, so that the
	 * GUI can create an appropriate graphical representation. */
	public enum DelayType {
		OBSTACLE (0.6d, ZLocalization.getSingleton ().getString ("ds.z.DelayArea.DelayType.OBSTACLE.description")),
		OTHER (1.0d, ZLocalization.getSingleton ().getString ("ds.z.DelayArea.DelayType.OTHER.description"));
		
		DelayType (double defaultSpeedFactor, String description) {
			this.defaultSpeedFactor = defaultSpeedFactor;
			this.description = description;
		}
		
		public final double defaultSpeedFactor;
		public final String description;
	}
	
	@XStreamAsAttribute ()
	private double speedFactor;
	@XStreamAsAttribute ()
	private DelayType delayType;
	
	/**
	 * Constucts a new <code>DelayArea</code> with the default <code>speedFactor</code>
	 * provided by the specified {@link DelayType}.
	 * 
	 * @param room to which the area belongs
	 * @param type the source/type of the delay
	 */
	public DelayArea (Room room, DelayType type) {
		this (room, type, type.defaultSpeedFactor);
	}
	/** Constucts a new DelayArea with the given parameters.
	 *
	 * @param room to which the area belongs
	 * @param type the source/type of the delay
	 * @param speedFactor affects the speed of the evacuees.
	 */
	public DelayArea (Room room, DelayType type, double speedFactor) {
		super ( Edge.class, room);
		setDelayType (type);
		setSpeedFactor (speedFactor);
	}
		
	/** This method copies the current polygon without it's edges. Every other setting, as f.e. the floor
	 * for Rooms or the associated Room for Areas is kept as in the original polygon. */
	@Override
	protected PlanPolygon<Edge> createPlainCopy () {
		return new DelayArea (getAssociatedRoom (), getDelayType (), getSpeedFactor ());
	}
	
	/**
	 * Returns the currently set value for the speedFactor. The speedfactor is
         * the percentage of the original speed that can be achieved on this area.
         * A speedfactor of 1 would be normal speed and speedfactor of 0 would mean
         * total halt.
	 * @return speedFactor
	 */
	public double getSpeedFactor () {
		return speedFactor;
	}
	
	/**
	 * <p>Sets a new value for the speedFactor in the area. The speedfactor is
   * the percentage of the original speed that can be achieved on this area.
   * A speedfactor of 1 would be normal speed and speedfactor of 0 would mean
   * total halt.</p>
   * <p>The speedfactor has to be greater than 0 and less or equal than 1.</p>
	 * @throws java.lang.IllegalArgumentException the speedFactor must be greater than 0.
	 * @param val ist the speedFactor of the delayArea.
	 */
	public void setSpeedFactor (double val) throws IllegalArgumentException {
		if ( val <= 0 ) {
			throw new IllegalArgumentException ( ZLocalization.getSingleton().getString("ds.z.DelayArea.SpeedFactorNegativeException") );
		} else if ( val > 1 )
                  throw new IllegalArgumentException( ZLocalization.getSingleton().getString("ds.z.DelayArea.SpeedFactorToHighException") );
                else {
			this.speedFactor = val;
//			throwChangeEvent (new ChangeEvent (this));
		}
	}

	public DelayType getDelayType () {
		return delayType;
	}

	public void setDelayType (DelayType delayType) {
		this.delayType = delayType;
//		throwChangeEvent (new ChangeEvent (this));
	}
	
	@Override
	public boolean equals (Object o) {
		if (o instanceof DelayArea) {
			DelayArea p = (DelayArea)o;
			return super.equals (p) && ((delayType == null) ? p.getDelayType () == null : delayType.equals (p.getDelayType ())) && (speedFactor == p.getSpeedFactor ());
		} else {
			return false;
		}
	}
}
