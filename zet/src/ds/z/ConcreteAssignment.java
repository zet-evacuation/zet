/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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