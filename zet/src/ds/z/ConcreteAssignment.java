/*
 * ConcreteAssignment.java
 * Created on 3. Dezember 2007, 22:23
 */

package ds.z;

import exitdistributions.ZToExitMapping;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Represents a concrete assignment of an evacuation project. A concrete
 * assignment contains a list of evacuees and their positions in the building.</p>
 * <p>The evacuees are represented by {@link Person}-objects. These persons have
 * a position and are connected to the room in which they are. They have some
 * parameters that can be used by simulation and optimization algorithms.</p>
 * @author Jan-Philipp Kappmeier
 */
public class ConcreteAssignment {
  /** The list of persons. */
  ArrayList<Person> persons = new ArrayList<Person>();
	
  /** Creates a new instance of ConcreteAssignment */
  public ConcreteAssignment() { }
  
  /**
   * Adds a specified person to the concrete assignment.
   * @param p the person
   */
  public void addPerson( Person p ) {
    persons.add( p );
  }
  
  /**
   * Returns a view of a list of all persons in the concrete assignment.
   * @return the list
   */
  public List<Person> getPersons() {
    return Collections.unmodifiableList( persons );
  }
}