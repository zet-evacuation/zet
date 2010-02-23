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
 * AssignmentType.java
 * Created on 26. November 2007, 21:32
 */

package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import de.tu_berlin.math.coga.rndutils.distribution.Distribution;
import io.z.AssignmentTypeConverter;
import io.z.XMLConverter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import de.tu_berlin.math.coga.common.localization.Localization;

/**
 * This class pools several assignmentAreas.
 * All the areas have the same distributions of the persons' parameters.
 * @author Sylvie Temme
 */
@XStreamAlias ("assignmentType")
@XMLConverter(AssignmentTypeConverter.class)
public class AssignmentType implements Serializable {
//	@XStreamOmitField ()
//	private transient ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener> ();
	
	
	/**
	 * A name for the assignment type such as "Old People" or "Pedestrians".
	 */
	@XStreamAsAttribute ()
	private String name;
	/** The distribution of the persons' parameter "diameter". */
	private Distribution diameter;
	/** The distribution of the persons' parameter "age". */
	private Distribution age;
	/** The distribution of the persons' parameter "familiarity". */
	private Distribution familiarity;
	/** The distribution of the persons' parameter "panic". */
	private Distribution panic;
	/** The distribution of the persons' parameter "decisiveness". */
	private Distribution decisiveness;
	/** The default number of evacuees of the assignmentAreas, which belong this type. */
	private int standardEvacuees;
	/** The list of the assignmentAreas, which belong to this assignmentType. */
	private ArrayList<AssignmentArea> assignmentAreas;
	/** Unique ID of this AssignmentType */
	private UUID uid;


	/**
	 * Creates a new instance of {@link AssignmentType}.
	 * Sets Distributions diameter, age, familiarity, panic, decisiveness.
	 * Sets the standardnumber of evacuees to 0.
	 * @param name The name of the type
	 * @param diameter The {@link Distribution} diameter.
	 * @param age The {@link Distribution} age.
	 * @param familiarity The {@link Distribution} familiarity.
	 * @param panic The {@link Distribution} panic.
	 * @param decisiveness The {@link Distribution} decisiveness.
	 */
	public AssignmentType (String name, Distribution diameter, Distribution age, Distribution familiarity, Distribution panic, Distribution decisiveness) {
		this (name, diameter, age, familiarity, panic, decisiveness, 0);
	}
	
	/**
	 * Creates a new instance of {@link AssignmentType}.
	 * Sets Distributions diameter, age, familiarity, panic, decisiveness.
	 * Sets the standardnumber of evacuees.
	 * This number has to be greater or equal 0.
	 * If it is less than 0, the number of evacuees is set to 0.
	 * @param name The name of the type
	 * @param diameter The {@link Distribution} diameter.
	 * @param age The {@link Distribution} age.
	 * @param familiarity The {@link Distribution} familiarity.
	 * @param panic The {@link Distribution} panic.
	 * @param decisiveness The {@link Distribution} decisiveness.
	 * @param standardEvacuees The standardnumber of evacuees.
	 * @throws java.lang.IllegalArgumentException If the standardnumber of evacuees is less than 0.
	 * (The standardnumber of evacuees is set to 0.)
	 */
	public AssignmentType (String name, Distribution diameter, Distribution age, Distribution familiarity, Distribution panic, Distribution decisiveness, int standardEvacuees) throws IllegalArgumentException {
		this.name = name;
		this.diameter=diameter;
		this.age=age;
		this.familiarity=familiarity;
		this.panic=panic;
		this.decisiveness=decisiveness;
		assignmentAreas=new ArrayList<AssignmentArea>();
		setDefaultEvacuees (standardEvacuees);
		this.uid = UUID.randomUUID();
	}
	
	@Override
	public String toString () {
		return name;
	}
	
	
	public UUID getUid() {
		return uid;
	}

	public void setUid(UUID uid) {
		this.uid = uid;
	}
	
	/** See {@link ds.z.event.ChangeReporter#throwChangeEvent (ChangeEvent)} for details. */
//	public void throwChangeEvent (ChangeEvent e) {
//		for (ChangeListener c : changeListeners) {
//			c.stateChanged (e);
//		}
//	}
//	/** {@inheritDoc} */
//	public void addChangeListener (ChangeListener c) {
//		if (!changeListeners.contains (c)) {
//			changeListeners.add (c);
//		}
//	}
//	/** {@inheritDoc} */
//	public void removeChangeListener (ChangeListener c) {
//		changeListeners.remove (c);
//	}
//	/** See {@link ds.z.event.ChangeListener#stateChanged (ChangeEvent)} for details. */
//	public void stateChanged (ChangeEvent e) {
//		// Simply forward the event
//		throwChangeEvent (e);
//	}
	
	/**
	 * Returns the currently set value for the standardnumber of evacuees.
	 * @return The standardnumber of evacuees in this area.
	 */
	public int getDefaultEvacuees () {
		return standardEvacuees;
	}
	
	/**
	 * Sets a new value for the standradnumber of evacuees of this AssignmentType.
	 * It has to be greater or equal 0.
	 * If it is less than 0, the number of evacuees is set to 0.
	 * @param val The default number of evacuees of this <code>AssignmentType</code>.
	 * @throws java.lang.IllegalArgumentException if the standardnumber of evacuees is less than 0.
	 */
	public void setDefaultEvacuees (int val) throws IllegalArgumentException {
		if (val<0) {
			throw new IllegalArgumentException ( Localization.getInstance().getString("ds.z.AssignmentArea.NegativePersonValueException") );
		} else {
			standardEvacuees=val;
//			throwChangeEvent (new ChangeEvent (this));
		}
	}
	
	/**
	 * Returns the currently set {@link Distribution} diameter.
	 * @return The Distribution diameter.
	 */
	public Distribution getDiameter () {
		return diameter;
	}
	
	/**
	 * Sets the {@link Distribution} diameter.
	 * @param val The Distribution diameter.
	 */
	public void setDiameter (Distribution val) {
		if (diameter != null) {
//			diameter.removeChangeListener (this);
		}
		diameter=val;
//		val.addChangeListener (this);
//		throwChangeEvent (new ChangeEvent (this));
	}
	
	/**
	 * Returns the currently set {@link Distribution} age.
	 * @return The Distribution age.
	 */
	public Distribution getAge () {
		return age;
	}
	
	/**
	 * Sets the {@link Distribution} age.
	 * @param val The Distribution age.
	 */
	public void setAge (Distribution val) {
		if (age != null) {
//			age.removeChangeListener (this);
		}
		age=val;
//		val.addChangeListener (this);
//		throwChangeEvent (new ChangeEvent (this));
	}
	
	/**
	 * Returns the currently set {@link Distribution} familiarity.
	 * @return The Distribution familiarity.
	 */
	public Distribution getFamiliarity () {
		return familiarity;
	}
	
	/**
	 * Sets the {@link Distribution} familiarity.
	 * @param val The Distribution familiarity.
	 */
	public void setFamiliarity (Distribution val) {
		if (familiarity != null) {
	//		familiarity.removeChangeListener (this);
		}
		familiarity=val;
		//val.addChangeListener (this);
		//throwChangeEvent (new ChangeEvent (this));
	}
	
	/**
	 * Returns the currently set {@link Distribution} panic.
	 * @return The Distribution panic.
	 */
	public Distribution getPanic () {
		return panic;
	}
	
	/**
	 * Sets the {@link Distribution} panic.
	 * @param val The Distribution panic.
	 */
	public void setPanic (Distribution val) {
		if (panic != null) {
//			panic.removeChangeListener (this);
		}
		panic=val;
	//	val.addChangeListener (this);
		//throwChangeEvent (new ChangeEvent (this));
	}
	
	/**
	 * Returns the currently set {@link Distribution} decisiveness.
	 * @return The Distribution decisiveness.
	 */
	public Distribution getDecisiveness () {
		return decisiveness;
	}
	
	/**
	 * Sets the {@link Distribution} decisiveness.
	 * @param val The Distribution decisiveness.
	 */
	public void setDecisiveness (Distribution val) {
		if (decisiveness != null) {
//			decisiveness.removeChangeListener (this);
		}
		decisiveness=val;
	//	val.addChangeListener (this);
		//throwChangeEvent (new ChangeEvent (this));
	}
	
	/**
	 * Returns the list assignmentAreas of this assignmentType.
	 * @return The list assignmentAreas of this assignmentType.
	 */
	public List<AssignmentArea> getAssignmentAreas () {
		return Collections.unmodifiableList (assignmentAreas);
	}
	
	/**
	 * Adds a new assignmentArea to the list of assignmentAreas of this assignmentType.
	 * @param val The assignmentArea to be added.
	 * @throws java.lang.IllegalArgumentException If the area already is in the list.
	 */
	void addAssignmentArea (AssignmentArea val) throws IllegalArgumentException {
		if (assignmentAreas.contains (val)) {
			throw new IllegalArgumentException (Localization.getInstance().getString("ds.z.AssignmentType.DoubleAssignmentAreaException"));
		} else {
			assignmentAreas.add (val);
//			val.addChangeListener (this);
//			throwChangeEvent (new ChangeEvent (this));
		}
	}
	
	/**
	 * Removes an assignmentArea from the list of assignmentAreas of this assignmentType.
	 * @param val The assignmentArea to be removed.
	 * @throws java.lang.IllegalArgumentException If the area is not in the list.
	 */
	void deleteAssignmentArea (AssignmentArea val) throws IllegalArgumentException {
		if (!assignmentAreas.contains (val)) {
			throw new IllegalArgumentException (Localization.getInstance().getString("ds.z.AssignmentType.AssignmentAreaNotFound"));
		} else {
			assignmentAreas.remove (val);
//			val.removeChangeListener (this);
//			throwChangeEvent (new ChangeEvent (this));
		}
	}
	
	
	/**
	 * Deletes all references from this assignmentType.
	 * (Calls assignmentArea.delete() for all its assignmentAreas).
	 */
	public void delete () {
		for (int i=0; i< assignmentAreas.size (); i++) {
			assignmentAreas.get (i).delete ();
		}
	}
	
	
	/**
	 * Sets the number of evacuees of all its assignmentAreas
	 * to its standardnumber of evacuees.
	 */
	public void setEvacueesOfAllAreasToStandardEvacuees () {
		for (int i=0; i<assignmentAreas.size (); i++) {
			assignmentAreas.get (i).setEvacuees (standardEvacuees);
		}
	}
	
	/** Two AssignmentTypes are equal if their parameters match each others'.
	 * They can have different AssignmentAreas though. */
	public boolean equals (Object o) {
		if (o instanceof AssignmentType) {
			AssignmentType p = (AssignmentType)o;
			return ((name == null) ? p.getName () == null :
				name.equals (p.getName ())) &&
				((age == null) ? p.getAge () == null :
				age.equals (p.getAge ())) &&
				((decisiveness == null) ? p.getDecisiveness () == null :
					decisiveness.equals (p.getDecisiveness ())) &&
				((diameter == null) ? p.getDiameter () == null :
					diameter.equals (p.getDiameter ())) &&
				((familiarity == null) ? p.getFamiliarity () == null :
					familiarity.equals (p.getFamiliarity ())) &&
				((panic == null) ? p.getPanic () == null :
					panic.equals (p.getPanic ()));
		} else {
			return false;
		}
	}

	public String getName () {
		return name;
	}
	/** @throws IllegalArgumentException If the given name is nul or "".
	 */
	public void setName( String val ) throws IllegalArgumentException {
		if (val == null || val.equals ("")) {
			throw new IllegalArgumentException (Localization.getInstance (
			).getString ("ds.z.Assignment.NoNameException"));
		}
		this.name = val;
//		throwChangeEvent (new ChangeEvent (this));
	}
	
}
