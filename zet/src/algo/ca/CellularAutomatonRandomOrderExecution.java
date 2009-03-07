/**
 * Class CellularAutomatonRandomOrderExecution
 * Erstellt 04.07.2008, 14:50:49
 */

package algo.ca;

import ds.ca.CellularAutomaton;
import ds.ca.Individual;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import util.random.RandomUtils;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonRandomOrderExecution extends CellularAutomatonInOrderExecution {
	public CellularAutomatonRandomOrderExecution( CellularAutomaton ca ) {
		super( ca );
	}
	
	@Override
	public List<Individual> getIndividuals() {
		Individual[] indArray = super.getIndividuals().toArray( new Individual[0] );
		// Permutieren
		for( int i = indArray.length-1; i >= 0; i-- ) {
			int randomNumber = (RandomUtils.getInstance()).getRandomGenerator().nextInt( i+1 );
			Individual t = indArray[i];	// Save position i
			indArray[i] = indArray[randomNumber];	// Store randomNumber at i
			indArray[randomNumber] = t;	// Set Individual from i to randomNumber
		}
		return Collections.unmodifiableList( Arrays.asList( indArray ) );
	}

	@Override
	public String toString() {
		return "CellularAutomatonRandomOrderExecution";
	}

}
