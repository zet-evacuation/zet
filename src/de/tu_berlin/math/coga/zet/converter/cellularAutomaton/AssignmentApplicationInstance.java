/**
 * AssignmentApplicationInstance.java
 * Created: Jul 28, 2010,5:30:04 PM
 */
package de.tu_berlin.math.coga.zet.converter.cellularAutomaton;

import de.tu_berlin.coga.common.datastructure.Tuple;
import de.tu_berlin.coga.zet.model.ConcreteAssignment;


/**
 *
 * @author Jan-Philipp Kappmeier
 */

	public class AssignmentApplicationInstance extends Tuple<ConvertedCellularAutomaton, ConcreteAssignment> {

		public AssignmentApplicationInstance( ConvertedCellularAutomaton u, ConcreteAssignment v ) {
			super( u, v );
		}
	}
