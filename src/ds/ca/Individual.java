package ds.ca;

import algo.ca.rule.PotentialValueTuple;
import ds.graph.Identifiable;
import java.util.UUID;

/**
 * A Individual represets a Person in the evacuationtool with the following
 * characteristics: familiarity, panic, slackness, maxSpeed. Also an
 * exhaustion factor exists, which simulates exhaustion after walking a long
 * way. An Individual is located in a {@link Cell} of the building and each
 * <code>Individual</code> has a {@link StaticPotential}, which guides the
 * person to an exit.
 */
public class Individual implements Identifiable {

	/**
	 * Describes the cause of death if an individual dies.
	 */
	public enum DeathCause {

		/** If no exit is reachable. Happens if a person is surrounded by barriers. */
		EXIT_UNREACHABLE,
		/** If the <code>Individual</code> is inside the building when the maximum evacuation time is over. */
		NOT_ENOUGH_TIME
	}
	private int age;
	private double familiarity;
	private double panic = 0.0001;
	private double panicFactor;
	private double slackness;
	private double exhaustion = 0;
	private double exhaustionFactor;
	private double currentSpeed;
	private double maxSpeed;
	private double absoluteMaxSpeed;
	private boolean alarmed;
	private int reactionTime;
	private Cell cell;
	private StaticPotential staticPotential;
	private DynamicPotential dynamicPotential;
	private DeathCause deathCause;
	/** The number of the individual. Each Individual of an CA should have a unique identifier. */
	private int individualNumber = 0;
	private double stepEndTime = 0;
	private double stepStartTime = 0;
	/** Unique ID of the assignment type this individual is in */
	private UUID uid;
	/**
	 * The time, when the individual has last entered an area, where it is safe
	 * ( = area of save- and exitcells)
	 */
	private int safetyTime;
	/** Indicates, if the individual is already safe; that means: on save- oder exitcells */
	private boolean safe;
	private boolean isEvacuated = false;
	private boolean isDead = false;
	private PotentialValueTuple potentialMemoryStart;
	private PotentialValueTuple potentialMemoryEnd;
	private int memoryIndex;
	int cellCountToChange;

	public Individual() {
	}

	public Individual( int age, double familiarity, double panicFactor, double slackness, double exhaustionFactor,
					double maxSpeed, int reactiontime, UUID uid ) {
		this.age = age;
		this.familiarity = familiarity;
		this.panicFactor = panicFactor;
		this.slackness = slackness;
		this.exhaustionFactor = exhaustionFactor;
		this.maxSpeed = maxSpeed;
		this.currentSpeed = maxSpeed;
		this.alarmed = false;
		this.cell = null;
		this.staticPotential = null;
		this.dynamicPotential = null;
		this.reactionTime = reactiontime;
		this.uid = uid;
		safe = false;
		safetyTime = -1;

		/**
		 * Calibratingfactor - 
		 * The bigger <code>cellCountToChange</code>, the longer an individual moves before a possible potential change
		 */
		cellCountToChange = (int) Math.round( currentSpeed * 15 / 0.4 );
		potentialMemoryStart = new PotentialValueTuple( -1, null );
		potentialMemoryEnd = new PotentialValueTuple( -1, null );
		memoryIndex = 0;
	}

	public PotentialValueTuple getPotentialMemoryStart() {
		return potentialMemoryStart;
	}

	public PotentialValueTuple getPotentialMemoryEnd() {
		return potentialMemoryEnd;
	}

	public void setPotentialMemoryStart( PotentialValueTuple start ) {
		potentialMemoryStart = start;
	}

	public void setPotentialMemoryEnd( PotentialValueTuple end ) {
		potentialMemoryEnd = end;
	}

	public int getCellCountToChange() {
		return cellCountToChange;
	}

	public int getMemoryIndex() {
		return memoryIndex;
	}

	public void setMemoryIndex( int index ) {
		memoryIndex = index;
	}

	public double getStepEndTime() {
		return stepEndTime;
	}

	public void setStepEndTime( double stepEndTime ) {
		this.stepEndTime = stepEndTime;
	}

	public double getStepStartTime() {
		return stepStartTime;
	}

	public void setStepStartTime( double stepStartTime ) {
		this.stepStartTime = stepStartTime;
	}

	/**
	 * Returns true, if the person is evacuated, false elsewise
	 * @return the evacuation status
	 */
	public boolean isEvacuated() {
		return this.isEvacuated;
	}

	/**
	 * Sets this <code>Individual</code> evacuated
	 */
	public void setEvacuated() {
		isEvacuated = true;
	}

	/**
	 * Returns the {@link DeathCause} of an individual.
	 * @return the cause
	 */
	public DeathCause getDeathCause() {
		return deathCause;
	}

	/**
	 * Returns the time when the individual is safe.
	 * @return The time when the individual is safe.
	 */
	public int getSafetyTime() {
		return safetyTime;
	}

	/**
	 * Sets the time when the individual is evacuated.
	 * @param time The time when the individual is evacuated.
	 */
	public void setSafetyTime( int time ) {
		safetyTime = time;
	}

	/**
	 * Get the age of the Individual
	 * @return The age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Returns, if the individual is already safe; that means: on save- oder exitcells
	 * @return if the individual is already safe
	 */
	public boolean isSafe() {
		return safe;
	}

	/**
	 * Sets the safe-status of the individual
	 * @param saveStatus indicates wheather the individual is save or not
	 */
	public void setSafe( boolean saveStatus ) {
		safe = saveStatus;
	}

	/**
	 * Get the left reaction time of the Individual
	 * @return The left reaction time 
	 */
	public int getReactionTime() {
		return reactionTime;
	}

	/**
	 * Sets a new reaction time
	 * @param reactionTime the reaction time.
	 * @throws IllegalArgumentException if <code>reactionTime</code> is negative.
	 */
	public void setReactionTime( int reactionTime ) throws IllegalArgumentException {
		if( reactionTime < 0 ) {
			throw new IllegalArgumentException( "Reaction time must not be negative." );
		}
		this.reactionTime = reactionTime;
	}

	/**
	 * Alarms the Individual and also alarms the room of the cell of the individual.     
	 * @param alarmed decides wheather the individual is alarmed, or if it is stopped being alarmed
	 */
	public void setAlarmed( boolean alarmed ) {
		this.alarmed = alarmed;
		//this.getCell().getRoom().setAlarmstatus( true );	// todo: test ;)
	}

	/**
	 * Get the setAlarmed status of the Individual
	 * @return true if the individual is alarmed, false otherwise
	 */
	public boolean isAlarmed() {
		return alarmed;
	}

	/**
	 * Get the exhaustion of the Individual
	 * @return The exhaustion 
	 */
	public double getExhaustion() {
		return exhaustion;
	}

	/**
	 * Set the exhaustion of the Individual to a specified value
	 * @param val the exhaustion
	 */
	public void setExhaustion( double val ) {
		this.exhaustion = val;
	}

	/**
	 * Returns the exchaustion factor of the <code>Individual</code>
	 * @return the exhaustion factor
	 */
	public double getExhaustionFactor() {
		return this.exhaustionFactor;
	}

	/**
	 * Sets the exhaustion factor of the <code>Individual</code> to a specified value
	 * @param val the exhaustion factor
	 */
	public void setExhaustionFactor( double val ) {
		this.exhaustionFactor = val;
	}

	/**
	 * Get the familiarity of the Individual
	 * @return The familiarity 
	 */
	public double getFamiliarity() {
		return familiarity;
	}

	/**
	 * Set the familiarity of the Individual
	 * @param val 
	 */
	public void setFamiliarity( double val ) {
		this.familiarity = val;
	}

	/**
	 * Returns the identifier of this individual.
	 * @return the number
	 */
	public int getNumber() {
		return individualNumber;
	}

	/** 
	 * Sets the identification Number of the <code>Individual</code>.
	 * @param i the number
	 */
	public void setNumber( int i ) {
		individualNumber = i;
	}

	/**
	 * Returns the identifier of this individual.
	 * @return the number
	 */
	public int id() {
		return individualNumber;
	}

	/**
	 * Get the panic of the Individual
	 * @return The panic
	 */
	public double getPanic() {
		return panic;
	}

	public void setPanic( double val ) {
		this.panic = val;
	}

	public double getPanicFactor() {
		return panicFactor;
	}

	/**
	 * Set the panic-factor of the Individual
	 * @param val 
	 */
	public void setPanicFactor( double val ) {
		this.panicFactor = val;
	}

	/**
	 * Get the slackness of the Individual
	 * @return The slackness 
	 */
	public double getSlackness() {
		return slackness;
	}

	/**
	 * Set the slackness of the Individual
	 * @param val 
	 */
	public void setSlackness( double val ) {
		this.slackness = val;
	}

	/**
	 * Set the maxSpeed of the Individual
	 * @param maxSpeed the maximal speed
	 */
	public void setMaxSpeed( double maxSpeed ) {
		this.maxSpeed = maxSpeed;
	}

	/**
	 * Get the maxSpeed of the Individual
	 * @return The maxSpeed 
	 */
	public double getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * Set the currentSpeed of the Individual
	 * @param currentSpeed 
	 */
	public void setCurrentSpeed( double currentSpeed ) {
		this.currentSpeed = currentSpeed;
	}

	/**
	 * Get the currentSpeed of the Individual
	 * @return The currentSpeed 
	 */
	public double getCurrentSpeed() {
		return currentSpeed;
	}

	/**
	 * Set the {@link ds.ca.Cell} on which the <code>Individual</code> stands.
	 * @param c the cell
	 */
	public void setCell( Cell c ) {
		this.cell = c;
	}

	/**
	 * Returns the {@link ds.ca.Cell} on which the <code>Individual</code> stands.
	 * @return The Cell 
	 */
	public Cell getCell() {
		return cell;
	}

	/**
	 * Set the dynamicPotential of the Individual
	 * @param dp 
	 */
	public void setDynamicPotential( DynamicPotential dp ) {
		this.dynamicPotential = dp;
	}

	/**
	 * Get the dynamicPotential of the Individual
	 * @return The dynamicPotential 
	 */
	public DynamicPotential getDynamicPotential() {
		return dynamicPotential;
	}

	/**
	 * Set the staticPotential of the Individual
	 * @param sp 
	 */
	public void setStaticPotential( StaticPotential sp ) {
		this.staticPotential = sp;
	}

	/**
	 * Get the staticPotential of the Individual
	 * @return The staticPotential 
	 */
	public StaticPotential getStaticPotential() {
		return staticPotential;
	}

	public void die( DeathCause cause ) {
		this.deathCause = cause;
		isDead = true;
	}

	public boolean isDead() {
		return isDead;
	}

	/**
	 * Returns a copy of itself as a new Object.
	 */
	@Override
	public Individual clone() {
		Individual aClone = new Individual();
		aClone.absoluteMaxSpeed = this.absoluteMaxSpeed;
		aClone.age = this.age;
		aClone.cell = this.cell;
		aClone.currentSpeed = this.currentSpeed;
		aClone.deathCause = this.deathCause;
		aClone.dynamicPotential = this.dynamicPotential;
		aClone.exhaustion = this.exhaustion;
		aClone.exhaustionFactor = this.exhaustionFactor;
		aClone.familiarity = this.familiarity;
		aClone.individualNumber = this.individualNumber;
		aClone.alarmed = this.alarmed;
		aClone.isEvacuated = this.isEvacuated;
		aClone.safe = this.safe;
		aClone.maxSpeed = this.maxSpeed;
		aClone.panic = this.panic;
		aClone.panicFactor = this.panicFactor;
		aClone.reactionTime = this.reactionTime;
		aClone.safetyTime = this.safetyTime;
		aClone.slackness = this.slackness;
		aClone.staticPotential = this.staticPotential;
		aClone.stepEndTime = this.stepEndTime;
		aClone.stepStartTime = this.stepStartTime;
		aClone.uid = this.uid;
		return aClone;
	}

	/**
	 * Returns a string "Individual" and the id number of the individual.
	 * @return the string representation
	 */
	@Override
	public String toString() {
		return "Individual " + id();
	}

	/**
	 * Returns a string containing all parameters of the individueal, such as
	 * familiarity, exhaustion etc.
	 * @return the property string
	 */
	public String toStringProperties() {
		return "Familiarity: " + familiarity + "\n" +
						"Panic: " + panic + "\n" +
						"Panic factor: " + panicFactor + "\n" +
						"Slackness: " + slackness + "\n" +
						"Exhaustion: " + exhaustion + "\n" +
						"Exhaustion factor: " + exhaustionFactor + "\n" +
						"MaxSpeed: " + maxSpeed + "\n" +
						"Absolute max speed: " + absoluteMaxSpeed;
	}

	/**
	 * The hashcode of individuals is their id numer.
	 * @return
	 */
	@Override
	public int hashCode() {
		return getNumber();
	}

	/**
	 * <p>Two individuals are equal, if they have both the same id.</p>
	 * @param o the reference object with which to compare.
	 * @return <code>true</code> if this object is the same as the obj
	 *          argument; <code>false</code> otherwise.
	 * @see     #hashCode()
	 */
	@Override
	public boolean equals( Object o ) {
		if( o instanceof Individual ) {
			super.equals( o );
			return (((Individual) o).id() == id());
		} else {
			return false;
		}
	}
	

	//////////////////////////////////////////////////////////////////////////////
	// alter stuff. todo ändern
	public UUID getUid() {
		return uid;
	}

	public void setUid( UUID uid ) {
		this.uid = uid;
	}
	/**
	 * Decreases the left reaction time of the Individual     
	 * @param x 
	 */
	//public void decreaseReactionstime( int x ) {
	//	this.reactionTime = this.reactionTime - x;
	//}



}
