/**
 * Statistic.java
 * Created: 12.05.2010 15:32:16
 */
package statistics;

import de.tu_berlin.math.coga.datastructure.Tuple;
import statistics.collectors.AgeCollector;
import statistics.collectors.SpecificFlowCollector;
import statistics.collectors.SpeedCollector;


/**
 * A general class that collects data. Which data is collected can be
 * enabled or disables. It also supports listener for the data.
 * @author Jan-Philipp Kappmeier
 */
public class Statistic {

	private int numberOfPersons;
	AgeCollector ageCollector;
	SpeedCollector speedCollector;
	SpecificFlowCollector specificFlowCollector;
	public static Statistic instance = new Statistic();
	private int ageTries = 0;

	/**
	 * Make singleton for the moment.
	 */
	private Statistic() {
		ageCollector = new AgeCollector();
		speedCollector = new SpeedCollector();
		specificFlowCollector = new SpecificFlowCollector();
	}

	/**
	 * Call this method if an individual with the given age was created.
	 * @param age
	 */
	public void collectAge( double age ) {
		ageCollector.add( age );
	}

	public void collectAge( double age, int tries ) {
		ageCollector.add( age );
		ageTries += tries;
	}

	public void collectAgeSpeed( double age, double speed ) {
		speedCollector.add( new Tuple( age, speed ) );
	}

	/**
	 * Returns the age collector
	 * @return
	 */
	public AgeCollector getAgeCollector() {
		return ageCollector;
	}

	public void addPerson() {
		numberOfPersons++;
	}

	/**
	 * Returns the number of persons stored in the statistic.
	 * @return the number of persons stored in the statistic
	 */
	public int getNumberOfPersons() {
		return numberOfPersons;
	}

	public int getAgeTries() {
		return ageTries;
	}

	public SpecificFlowCollector getSpecificFlowCollector() {
		return specificFlowCollector;
	}



	

}
