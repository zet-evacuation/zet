/*
 * Person.java
 * Created on 3. Dezember 2007, 22:23
 */
package ds.z;

import java.util.UUID;

import localization.Localization;

/**
 * Represents a person that should be evacuated. The person has a specified position
 * and some parameter.
 * @author Jan-Philipp Kappmeier
 */
public class Person {
	/** The position of the person. */
	private PlanPoint position;
	/** The room containing the person. */
	private Room room;
	/** Unique ID of the assignment type this person is in */
	private UUID uid;
	/** The distribution of the persons' parameter "diameter". */
	private double diameter;
	/** The distribution of the persons' parameter "age". */
	private double age;
	/** The distribution of the persons' parameter "familiarity". */
	private double familiarity;
	/** The distribution of the persons' parameter "panic". */
	private double panic;
	/** The distribution of the persons' parameter "decisiveness". */
	private double decisiveness;
	/** The preferred exit area of the person. */
	private ds.z.SaveArea saveArea;

	/**
	 * Creates a new instance of <code>Person</code> at the specified position
	 * inside the specified room.
	 * @param room the room in which the person is located
	 * @param position the position
	 * @throws java.lang.IllegalArgumentException if the person's position is not inside the room
	 */
	public Person( PlanPoint position, Room room ) throws IllegalArgumentException {
		if( !room.contains( position ) ) {
			throw new IllegalArgumentException( Localization.getInstance().getString( "ds.z.NoPersonException" ) );
		}
		this.position = position;
		this.room = room;
		this.saveArea = null;
	}

	/**
	 * Returns the position of the person.
	 * @return the position of the person
	 */
	public PlanPoint getPosition() {
		return position;
	}

	/**
	 * Returns the room in which the person is located.
	 * @return the room in which the person is located
	 */
	public Room getRoom() {
		return room;
	}

	/**
	 * Returns the diameter of the person.
	 * @return the diameter of the person
	 */
	public double getDiameter() {
		return diameter;
	}

	/**
	 * Returns the age of the person.
	 * @return the age of the person
	 */
	public double getAge() {
		return age;
	}

	/**
	 * Returns the familiarity of the person.
	 * @return the familiarity of the person
	 */
	public double getFamiliarity() {
		return familiarity;
	}

	/**
	 * Returns the panic of the person.
	 * @return the panic of the person
	 */
	public double getPanic() {
		return panic;
	}

	/**
	 * Returns the decisiveness of the person.
	 * @return the decisiveness of the person
	 */
	public double getDecisiveness() {
		return decisiveness;
	}

	/**
	 * Sets the diameter of the person.
	 * @param diameter the diameter
	 * @throws java.lang.IllegalArgumentException if the diameter is less or equal to zero
	 */
	public void setDiameter( double diameter ) throws java.lang.IllegalArgumentException {
		if( diameter <= 0 ) {
			throw new java.lang.IllegalArgumentException( Localization.getInstance().getString( "ds.z.DiameterException" ) );
		}
		this.diameter = diameter;
	}

	/**
	 * Sets the age of the person.
	 * @param age the age
	 * @throws java.lang.IllegalArgumentException if the age is negative
	 */
	public void setAge( double age ) throws java.lang.IllegalArgumentException {
		if( age < 0 ) {
			throw new java.lang.IllegalArgumentException( Localization.getInstance().getString( "ds.z.AgeException" ) );
		}
		this.age = age;
	}

	/**
	 * Sets the familiarity of the person.
	 * @param familiarity the familiarity
	 * @throws java.lang.IllegalArgumentException If the familiarity is less or equal to zero
	 */
	public void setFamiliarity( double familiarity ) throws java.lang.IllegalArgumentException {
		if( familiarity <= 0 ) {
			throw new java.lang.IllegalArgumentException( Localization.getInstance().getString( "ds.z.FamilarityException" ) );
		}
		this.familiarity = familiarity;
	}

	/**
	 * Sets the panic of the person.
	 * @param panic the panic
	 * @throws java.lang.IllegalArgumentException if the panic is less than zero
	 */
	public void setPanic( double panic ) throws java.lang.IllegalArgumentException {
		if( panic < 0 ) {
			throw new java.lang.IllegalArgumentException( Localization.getInstance().getString( "ds.z.PanicException" ) );
		}
		this.panic = panic;
	}

	/**
	 * Sets the decisiveness of the person.
	 * @param decisiveness the decisiveness
	 * @throws java.lang.IllegalArgumentException if the decisiveness is less or equal to zero
	 */
	public void setDecisiveness( double decisiveness ) throws java.lang.IllegalArgumentException {
		if( decisiveness <= 0 )
			throw new java.lang.IllegalArgumentException( Localization.getInstance().getString( "ds.z.DecisivenessException" ) );
		this.decisiveness = decisiveness;
	}

	public UUID getUid() {
		return uid;
	}

	public void setUid( UUID uid ) {
		this.uid = uid;
	}

	/**
	 * Returns the save area assigned to this person. It is <code>null</code> if
	 * no explicit area is set.
	 * @return the save area assigned to this person.
	 */
	public SaveArea getSaveArea() {
		return saveArea;
	}

	/**
	 * Sets the preferred save area of this person. Use <code>null</code> if no
	 * explicit area is set.
	 * @param saveArea the preferred save area
	 */
	public void setSaveArea( SaveArea saveArea ) {
		this.saveArea = saveArea;
	}	
}
