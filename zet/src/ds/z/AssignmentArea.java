/*
 * AssignmentArea.java
 * Created on 26. November 2007, 21:32
 */

package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import ds.z.event.ChangeEvent;
import ds.z.exception.PolygonNotClosedException;
import ds.z.exception.TooManyPeopleException;
import localization.Localization;


/**
 * Represents an area that can contain several persons that should be evacuated.
 * @author Sylvie Temme
 */
@XStreamAlias ("assignmentArea")
public class AssignmentArea extends Area<Edge> {
	@XStreamAsAttribute()
	/** The number of evacuees of this area. */
	private int evacuees;
	/** The assignmentType of this area. */
	private AssignmentType assignmentType;
	/** The evacuation area representing the exit that the pearsons this rooms should use. */
	private EvacuationArea exit = null;

	/**
	 * Returns the exit assigned to this <code>AssignmentArea</code>
	 * @return the assigned exit
	 */
	public EvacuationArea getExitArea() {
		return exit;
	}

	/**
	 * Sets the exit assigned to this <code>AssignmentArea</code>.
	 * @param exit the {@link EvacuationArea} representing the exit.
	 */
	public void setExitArea( EvacuationArea exit ) {
		this.exit = exit;
		if( exit != null )
			System.out.println( "Setze Ausgang " + exit.getName() );
	}

	
	
	/**
	 * Creates a new instance of {@link AssignmentArea}.
	 * Sets room and assignmentType of the assignmentArea.
	 * Sets the number of evacuees to the standardnumber of evacuees of the assignmentType.
	 * @param room Room, to which the area belongs
	 * @param type Type, to which the area belongs.
	 */
	public AssignmentArea ( Room room, AssignmentType type ) {
		this (room, type, type.getDefaultEvacuees() );
		if( type.getDefaultEvacuees() > getMaxEvacuees() )
			setEvacuees( getMaxEvacuees() );
	}
	
	/**
	 * Creates a new instance of {@link AssignmentArea}.
	 * Sets room, assignmentType and number of evacuees of the assignmentArea.
	 * @param room Room, to which the area belongs
	 * @param type Type, to which the area belongs.
	 * @param evacuees Number of evacuees.
	 * @throws java.lang.IllegalArgumentException When number of evacuees is less than 0.
	 * (Number is set to 0.)
	 */
	public AssignmentArea (Room room, AssignmentType type, int evacuees) throws IllegalArgumentException {
		super ( Edge.class, room );
		setAssignmentType (type);
		setEvacuees (evacuees);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>The number of evacuees for each resulting AssignmentArea is distributed
	 * proportionally to the area of the resulting AssignmentAreas.</p>
	 * <p>It is guaranteed that none of the new areas contains more evacuees than
	 * are allowed. This holds even if it contained to many evacuees before the
	 * split.</p>
	 * @throws java.lang.IllegalArgumentException 
	 */
	@Override
	public PlanPolygon<Edge> splitClosedPolygon (Edge edge1, Edge edge2) throws IllegalArgumentException {
		int originalEvacuees = getEvacuees ();
		AssignmentArea newPolygon = (AssignmentArea)super.splitClosedPolygon(edge1, edge2);
		double areaNew = newPolygon.area ();
		double areaTotal = areaNew + area();
		newPolygon.setEvacuees( (int)(originalEvacuees * areaNew/areaTotal) );
		int exess = this.getMaxEvacuees() - ( originalEvacuees - newPolygon.getEvacuees() );
		if( exess > 0) {
			this.setEvacuees( getMaxEvacuees() );
			newPolygon.setEvacuees( Math.min( originalEvacuees - getMaxEvacuees(), newPolygon.getMaxEvacuees() ) );
		} else
			this.setEvacuees( originalEvacuees - newPolygon.getEvacuees() );
		return newPolygon;
	}
	
	/**
	 * This method copies the current polygon without it's edges. Every other setting, as f.e. the floor
	 * for Rooms or the associated Room for Areas is kept as in the original polygon.
	 * @return the copy
	 */
	@Override
	protected PlanPolygon<Edge> createPlainCopy () {
		return new AssignmentArea (getAssociatedRoom (), getAssignmentType ());
	}
	
	/**
	 * Returns the currently set value for the number of evacuees.
	 * @return Number of evacuees in this area.
	 */
	public int getEvacuees () {
		return evacuees;
	}
	
	/**
	 * Sets a new value for the number of evacuees in this area.
	 * It has to be greater or equal 0.
	 * If it is less than 0, the number of evacuees is set to 0.
	 * @param val The number of evacuees of this area.
	 * @throws java.lang.IllegalArgumentException If number of evacuees is less than 0.
	 * (Number is set to 0.)
	 */
	public void setEvacuees (int val) throws IllegalArgumentException {
		if (val<0) {
			throw new IllegalArgumentException ( Localization.getInstance().getString("ds.z.AssignmentArea.NegativePersonValueException") );
		} else {
			evacuees=val;
			throwChangeEvent (new ChangeEvent (this));
		}
	}
	
	/**
	 * Returns the currently set assignmentType of this area.
	 * @return The assignmentType of this area.
	 */
	public AssignmentType getAssignmentType() {
		return assignmentType;
	}
	
	/**
	 * Sets a new assignmentType.
	 * Gives notice of departure to the previous assignmentType of the area.
	 * @param val The new assignmentType.
	 * @throws java.lang.NullPointerException If the new assignmentType is null.
	 * (Doesn`t give notice of departure to the previous assignmentType of the area.)
	 */
	public void setAssignmentType (AssignmentType val) throws NullPointerException {
		if( val == null ) throw new NullPointerException ( Localization.getInstance().getString("ds.z.AssignmentArea.NoAssignmentTypeException") );
		if( val.equals( assignmentType ) )
			return; // no change
		
		if (assignmentType != null) {
			assignmentType.deleteAssignmentArea (this);
		}
		assignmentType = val;
		assignmentType.addAssignmentArea (this);
		//throwChangeEvent (new ChangeEvent (this)); - is thrown by add/delete methods
	}

	/**
	 * Deletes all references to and from this area.
	 * (Calls {@link Area#delete()}
	 * and gives notice of departure to the assignmentType of the area
	 * and sets its assignmentType to null. )
	 * @throws java.lang.IllegalArgumentException If this area is not in the list of assignmentAreas of its assignmentType.
	 * (assignmentType is set to null.)
	 */
	@Override
	public void delete () throws IllegalArgumentException{
		try {
			assignmentType.deleteAssignmentArea( this );
		} finally {
			assignmentType=null;
		}
		super.delete ();
	}
	
	/**
	 * See @return
	 * @return True, if the area has the same number of evacuees
	 * as the default number of evacuees of its <code>AssignmentType</code>.
	 * False else.
	 */
	public boolean hasStandardEvacuees () {
		if( evacuees == assignmentType.getDefaultEvacuees() )
			return true;
		else return false;
	}
        
	/**
	 * Signs in in the list of assignmentAreas of the associated room of this assignmentArea.
	 */
	public void setActive() {
		getAssociatedRoom().addArea( this );
	}

	/**
	 * Gives notice of departure to the associated room of this assignmentArea.
	 */
	public void setInactive() {
		getAssociatedRoom().deleteArea( this );
	}

	/**
	 * Calls {@link PlanPolygon#check( boolean )}.
	 * Checks additional, if this assignmentArea contains too many people.
	 * @throws ds.z.exception.PolygonNotClosedException If the area is not closed.
	 * @throws ds.z.exception.TooManyPeopleException If the assignmentArea contains too many persons.
	 * @param rasterized Indicates, if the BuildingPlan should be rasterized.
	 */
	@Override
	public void check( boolean rasterized ) throws PolygonNotClosedException, TooManyPeopleException {
		super.check( rasterized );
		checkTooManyPersonsInArea();
	}

	/**
	 * Checks, if this assignmentArea contains too many people.
	 * @throws ds.z.exception.TooManyPeopleException If this assignmentArea contains too many people.
	 */
	private void checkTooManyPersonsInArea() throws TooManyPeopleException {
		if (getEvacuees()!=0) {
			if( area() / getEvacuees() < Assignment.spacePerPerson )
				throw new TooManyPeopleException( this, Localization.getInstance().getString( "ds.z.AssignmentArea.ContainsToManyPersonsException" ) );
		}
  }
	
	@Override
	public boolean equals (Object o) {
		if (o instanceof AssignmentArea) {
			AssignmentArea p = (AssignmentArea)o;
			return super.equals (p) && evacuees == p.getEvacuees () &&
				((assignmentType == null) ? p.getAssignmentType () == null : 
					assignmentType.equals (p.getAssignmentType ()));
		} else {
			return false;
		}
	}
}