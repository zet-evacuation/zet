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
 * This class pools several {@link AssignmentArea}s.
 * All the areas have the same distributions of the persons' parameters.
 * @author Sylvie Temme, Jan-Philipp Kappmeier
 */
@XStreamAlias("assignmentType")
@XMLConverter(AssignmentTypeConverter.class)
public class AssignmentType implements Serializable {
	/**
	 * A name for the assignment type such as "Old People" or "Pedestrians".
	 */
	@XStreamAsAttribute()
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
	/** The distribution of the persons' parameter "reaction". */
	private Distribution reaction;
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
	 * @param panic the {@link Distribution} for the panic of evacuees
	 * @param decisiveness the {@link Distribution} decisiveness.
	 * @param reaction the {@link Distribution } for the reaction time of evacuees
	 */
	public AssignmentType( String name, Distribution diameter, Distribution age, Distribution familiarity, Distribution panic, Distribution decisiveness, Distribution reaction ) {
		this( name, diameter, age, familiarity, panic, decisiveness, reaction, 0 );
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
	 * @param panic the {@link Distribution} for the panic of evacuees
	 * @param decisiveness the {@link Distribution} decisiveness.
	 * @param reaction the {@link Distribution } for the reaction time of evacuees
	 * @param standardEvacuees The standardnumber of evacuees.
	 * @throws java.lang.IllegalArgumentException If the standardnumber of evacuees is less than 0.
	 * (The standardnumber of evacuees is set to 0.)
	 */
	public AssignmentType( String name, Distribution diameter, Distribution age, Distribution familiarity, Distribution panic, Distribution decisiveness, Distribution reaction, int standardEvacuees ) throws IllegalArgumentException {
		this.name = name;
		this.diameter = diameter;
		this.age = age;
		this.familiarity = familiarity;
		this.panic = panic;
		this.decisiveness = decisiveness;
		this.reaction = reaction;
		assignmentAreas = new ArrayList<AssignmentArea>();
		setDefaultEvacuees( standardEvacuees );
		this.uid = UUID.randomUUID();
	}

	@Override
	public String toString() {
		return name;
	}

	public UUID getUid() {
		return uid;
	}

	public void setUid( UUID uid ) {
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
	public int getDefaultEvacuees() {
		return standardEvacuees;
	}

	/**
	 * Sets a new value for the standradnumber of evacuees of this AssignmentType.
	 * It has to be greater or equal 0. If it is less than 0, the number of
	 * evacuees is set to 0.
	 * @param evacuees the default number of evacuees of this <code>AssignmentType</code>.
	 * @throws java.lang.IllegalArgumentException if the standard number of evacuees is less than 0.
	 */
	public void setDefaultEvacuees( int evacuees ) throws IllegalArgumentException {
		if( evacuees < 0 )
			throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.AssignmentArea.NegativePersonValueException" ) );
		else
			standardEvacuees = evacuees;
	}

	/**
	 * Returns the currently set {@link Distribution} diameter.
	 * @return The distribution for the diameter.
	 */
	public Distribution getDiameter() {
		return diameter;
	}

	/**
	 * Sets the {@link Distribution} diameter.
	 * @param diameter the distribution for the evacuees diameter
	 */
	public void setDiameter( Distribution diameter ) {
		this.diameter = diameter;
	}

	/**
	 * Returns the currently set {@link Distribution} age.
	 * @return The distribution for the age.
	 */
	public Distribution getAge() {
		return age;
	}

	/**
	 * Sets the {@link Distribution} age.
	 * @param age the distribution for the evacuees age.
	 */
	public void setAge( Distribution age ) {
		this.age = age;
	}

	/**
	 * Returns the currently set {@link Distribution} familiarity.
	 * @return the distribution for the familiarity.
	 */
	public Distribution getFamiliarity() {
		return familiarity;
	}

	/**
	 * Sets the {@link Distribution} familiarity.
	 * @param familiarity the distribution for the evacuees familiarity
	 */
	public void setFamiliarity( Distribution familiarity ) {
		this.familiarity = familiarity;
	}

	/**
	 * Returns the currently set {@link Distribution} panic.
	 * @return The Distribution panic.
	 */
	public Distribution getPanic() {
		return panic;
	}

	/**
	 * Sets the {@link Distribution} panic.
	 * @param panic the distribution for the evacuees panic
	 */
	public void setPanic( Distribution panic ) {
		this.panic = panic;
	}

	/**
	 * Returns the currently set {@link Distribution} decisiveness.
	 * @return The Distribution decisiveness.
	 */
	public Distribution getDecisiveness() {
		return decisiveness;
	}

	/**
	 * Sets the {@link Distribution} decisiveness.
	 * @param decisiveness the distribution for the decisiveness
	 */
	public void setDecisiveness( Distribution decisiveness ) {
		this.decisiveness = decisiveness;
	}

	/**
	 * Returns the {@link Distribution} for the reaction time of evacuees.
	 * @return the {@link Distribution} for the reaction time of evacuees
	 */
	public Distribution getReaction() {
		return reaction;
	}

	/**
	 * Sets the {@link Distribution} for the reaction time of evacuees.
	 * @param reaction the distribution for the reaction times
	 */
	public void setReaction( Distribution reaction ) {
		this.reaction = reaction;
	}

	/**
	 * Returns the list assignmentAreas of this assignmentType.
	 * @return The list assignmentAreas of this assignmentType.
	 */
	public List<AssignmentArea> getAssignmentAreas() {
		return Collections.unmodifiableList( assignmentAreas );
	}

	/**
	 * Adds a new assignmentArea to the list of assignmentAreas of this assignmentType.
	 * @param familiarity The assignmentArea to be added.
	 * @throws java.lang.IllegalArgumentException If the area already is in the list.
	 */
	void addAssignmentArea( AssignmentArea val ) throws IllegalArgumentException {
		if( assignmentAreas.contains( val ) )
			throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.AssignmentType.DoubleAssignmentAreaException" ) );
		else
			assignmentAreas.add( val );
	}

	/**
	 * Removes an assignmentArea from the list of assignmentAreas of this assignmentType.
	 * @param familiarity The assignmentArea to be removed.
	 * @throws java.lang.IllegalArgumentException If the area is not in the list.
	 */
	void deleteAssignmentArea( AssignmentArea val ) throws IllegalArgumentException {
		if( !assignmentAreas.contains( val ) )
			throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.AssignmentType.AssignmentAreaNotFound" ) );
		else
			assignmentAreas.remove( val );
	}

	/**
	 * Deletes all references from this assignmentType.
	 * (Calls assignmentArea.delete() for all its assignmentAreas).
	 */
	public void delete() {
		for( int i = 0; i < assignmentAreas.size(); i++ )
			assignmentAreas.get( i ).delete();
	}

	/**
	 * Sets the number of evacuees of all its assignmentAreas
	 * to its standardnumber of evacuees.
	 */
	public void setEvacueesOfAllAreasToStandardEvacuees() {
		for( int i = 0; i < assignmentAreas.size(); i++ )
			assignmentAreas.get( i ).setEvacuees( standardEvacuees );
	}

	/**
	 * Two {@code AssignmentType}s are equal if their parameters match each others'.
	 * They can have different AssignmentAreas though.
	 * @param o the object that is compared to this assignment type
	 */
	@Override
	public boolean equals( Object o ) {
		if( o instanceof AssignmentType ) {
			AssignmentType p = (AssignmentType)o;
			return ((name == null) ? p.getName() == null
							: name.equals( p.getName() ))
							&& ((age == null) ? p.getAge() == null
							: age.equals( p.getAge() ))
							&& ((decisiveness == null) ? p.getDecisiveness() == null
							: decisiveness.equals( p.getDecisiveness() ))
							&& ((diameter == null) ? p.getDiameter() == null
							: diameter.equals( p.getDiameter() ))
							&& ((familiarity == null) ? p.getFamiliarity() == null
							: familiarity.equals( p.getFamiliarity() ))
							&& ((panic == null) ? p.getPanic() == null
							: panic.equals( p.getPanic() ));
		} else
			return false;
	}

	/**
	 * Returns the name of the assignment type.
	 * @return the name of the assignment type
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets a name for the assignment type.
	 * @param name the new name for the assignment type
	 * @throws IllegalArgumentException If the given name is null or the empty string.
	 */
	public void setName( String name ) throws IllegalArgumentException {
		if( name == null || name.equals( "" ) )
			throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.Assignment.NoNameException" ) );
		this.name = name;
	}
}
