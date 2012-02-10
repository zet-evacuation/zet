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
/**
 * Class SwapCellularAutomaton
 * Created 07.07.2008, 22:46:48
 */
package algo.ca.algorithm.evac;

import algo.ca.rule.AbstractMovementRule;
import algo.ca.rule.Rule;
import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import ds.ca.evac.Cell;
import ds.ca.evac.Individual;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import util.ProgressBooleanFlags;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SwapCellularAutomaton extends EvacuationCellularAutomatonRandom {

	public SwapCellularAutomaton() {
	}

	/**
	 * 
	 */
	@Override
	protected void executeStep() {
		if( !isInitialized() ) {
			throw new IllegalArgumentException( DefaultLoc.getSingleton().getString( "algo.ca.NotInitializedException" ) );
		}
		getProblem().eca.nextTimeStep();
		if( ProgressBooleanFlags.CA_PROGRESS ) {
			if( getProblem().eca.getTimeStep() % stepsBetweenProgressOutputs == 1 )
				System.out.println("Progress: Starting step " + getProblem().eca.getTimeStep() + "." );
		}

		// Suche movement rule und setze auf not direct action
		setDirectExecute( false );

		AbstractMovementRule movement = null;
		// erster Lauf: bis zur movement-rule und dann anmelden lassen.
		ArrayList<Individual> unfinished = new ArrayList<Individual>();
		HashMap<Individual, ArrayList<Cell>> individualPossibleMapping = new HashMap<Individual, ArrayList<Cell>>();
		HashSet<Individual> individualSwapped = new HashSet<Individual>();

		for( Individual i : getIndividuals() ) {
			Iterator<Rule> loop = getProblem().ruleSet.loopIterator();
			while( loop.hasNext() ) {
				Rule r = loop.next();
				r.execute( i.getCell() );
				if( r instanceof AbstractMovementRule ) {
					movement = (AbstractMovementRule) r;
					break;
				}
			}

			// hier ist movementrule die aktuelle movement rule.
			if( movement.performMove() ) {
				unfinished.add( i );
				individualPossibleMapping.put( i, movement.getPossibleTargets() );
			//movement.move( i, movement.selectTargetCell( i.getCell(), movement.getPossibleTargets() ) );
			} else {
				// perform the other rules because this individual has finished its movement for this round
				while( loop.hasNext() ) {
					Rule r = loop.next();
					r.execute( i.getCell() );
				}
			}
		}

		setDirectExecute( true );

		ArrayList<Individual> unfinished2 = new ArrayList<Individual>();

		// erster Lauf ist beendet. Versuche nun ob welche swappen können
		for( Individual i : unfinished ) {
			if( individualSwapped.contains( i ) ) {
				if( util.DebugFlags.CA_SWAP )
					System.out.println( "Individuum " + i.id() + " hat schon geswappt." );
				continue;
			}
			ArrayList<Cell> possibleTargets = individualPossibleMapping.get( i );
			Cell target = movement.selectTargetCell( i.getCell(), possibleTargets );
			if( target.getIndividual() == null ) {
				// Klappt alles
				movement.move( i, target );
				//individualSwapped.add( i );
				unfinished2.add( i );
			} else {
				if( target.equals( i.getCell() ) ) {
					unfinished2.add( i );
				} else {
					// steht ein individual drauf.
					Individual i2 = target.getIndividual();
					if( individualSwapped.contains( i2 ) ) {
						if( util.DebugFlags.CA_SWAP )
							System.out.println( "Individuum " + i2.id() + " hat schon geswappt." );
						unfinished2.add( i );
					} else {
						ArrayList<Cell> possibleTargets2 = individualPossibleMapping.get( i2 );
						if( possibleTargets2 == null ) {
							// das andere individual hat wohl seinen weg ausgeführt!
							unfinished2.add( i );
						} else {
							Cell target2 = movement.selectTargetCell( i2.getCell(), possibleTargets2 );
							if( i.getCell().equals( target2 ) && i2.getCell().equals( target ) ) {
								if( util.DebugFlags.CA_SWAP )
									System.out.println( "SWAP Individual " + i.id() + " und Individual " + i2.id() );
								movement.swap( i.getCell(), i2.getCell() );
								individualSwapped.add( i2 );
								individualSwapped.add( i );
								if( unfinished2.contains( i2 ) ) {
									unfinished2.remove( i2 );								// perform last rules for them
								}
								Iterator<Rule> loop = getProblem().ruleSet.loopIterator();
								boolean movementFound = false;
								while( loop.hasNext() ) {
									Rule r = loop.next();
									if( r instanceof AbstractMovementRule ) {
										movementFound = true;
									} else if( movementFound ) {
										r.execute( i.getCell() );
									}
								}
								// perform last rules for them
								loop = getProblem().ruleSet.loopIterator();
								movementFound = false;
								while( loop.hasNext() ) {
									Rule r = loop.next();
									if( r instanceof AbstractMovementRule ) {
										movementFound = true;
									} else if( movementFound ) {
										r.execute( i2.getCell() );
									}
								}
							} else {
								// perform movement rule a second time with direct execute on
								unfinished2.add( i );
							}
						}
					}
				}
			}
		}

		// Führe alle übrigen Individuals aus (Individuen, die nicht geswappt haben
		for( Individual i : unfinished2 ) {
			Iterator<Rule> loop = getProblem().ruleSet.loopIterator();
			boolean movementFound = false;
			while( loop.hasNext() ) {
				Rule r = loop.next();
				if( r instanceof AbstractMovementRule ) {
					r.execute( i.getCell() );
					movementFound = true;
				} else if( movementFound ) {
					r.execute( i.getCell() );
				}
			}
		}


		//setDirectExecute( true );

		getProblem().eca.removeMarkedIndividuals();
		getProblem().potentialController.updateDynamicPotential( getProblem().parameterSet.probabilityDynamicIncrease(), getProblem().parameterSet.probabilityDynamicDecrease() );
	}

	private void setDirectExecute( boolean val ) {
		Iterator<Rule> loop = getProblem().ruleSet.loopIterator();
		while( loop.hasNext() ) {
			Rule r = loop.next();
			if( r instanceof AbstractMovementRule ) {
				((AbstractMovementRule) r).setDirectExecute( val );
			}
		}
	}

	@Override
	public String toString() {
		return "SwapCellularAutomaton";
	}
}
