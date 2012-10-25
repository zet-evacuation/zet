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
 * Created on 30.01.2008
 *
 */
package algo.ca.rule;

import de.tu_berlin.math.coga.rndutils.RandomUtils;
import ds.ca.evac.EvacCell;
import ds.ca.evac.Individual;
import ds.ca.results.IndividualStateChangeAction;
import ds.ca.results.VisualResultsRecorder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Daniel Plümpe
 */
public class WaitingMovementRule extends SimpleMovementRule2 {

	@Override
	protected void onExecute( ds.ca.evac.EvacCell cell ) {
		ind = cell.getIndividual();
		if( ind.isAlarmed() == true ) {
			if( canMove( ind ) ) {
				if( slack( ind ) ) {
					updateExhaustion( ind, cell );
					setMoveRuleCompleted( true );
					noMove();
				} else {
					if( isDirectExecute() ) {
						EvacCell targetCell = selectTargetCell( cell, computePossibleTargets( cell, true ) );
						setMoveRuleCompleted( true );
						move( targetCell );
					} else {
						computePossibleTargets( cell, false );
						setMoveRuleCompleted( true );
					}
				}
				this.updateSpeed( ind );
			} else {
				// Individual can't move, it is already moving
				setMoveRuleCompleted( false );
			}
		} else { // Individual is not alarmed, that means it remains standing on the cell			
			setMoveRuleCompleted( true );
			noMove();
		}
		VisualResultsRecorder.getInstance().recordAction( new IndividualStateChangeAction( ind ) );
	}

	@Override
	public void move( EvacCell targetCell ) {
		updatePanic( ind, targetCell );
		updateExhaustion( ind, targetCell );
		super.move( targetCell );
	}

	protected void updatePanic( Individual i, EvacCell targetCell ) {
		double oldPanic = i.getPanic();
		esp.parameterSet.updatePanic( i, targetCell, this.neighboursByPriority( i.getCell() ) );
		if( oldPanic != i.getPanic() )
			esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addPanicToStatistic( i, esp.eca.getTimeStep(), i.getPanic() );
	}

	protected void updateSpeed( Individual i ) {
		esp.parameterSet.updatePreferredSpeed( i );
	}

	/**
	 * Returns all reachable neighbours sorted according to their priority which
	 * is calculated by mergePotential(). The first element in the list is the
	 * most probable neighbour, the last element is the least probable neighbour.
	 * @param cell The cell whose neighbours are to be sorted
	 * @return A sorted list of the neighbour cells of {@code cell}, sorted
	 * in an increasing fashion according to their potential computed by 
	 * {@code mergePotential}.
	 */
	protected ArrayList<EvacCell> neighboursByPriority( EvacCell cell ) {
		class CellPrioritySorter implements Comparator<EvacCell> {
			final EvacCell referenceCell;

			CellPrioritySorter( EvacCell referenceCell ) {
				this.referenceCell = referenceCell;
			}

			@Override
			public int compare( EvacCell cell1, EvacCell cell2 ) {
				final double potential1 = esp.parameterSet.effectivePotential( referenceCell, cell1 );
				final double potential2 = esp.parameterSet.effectivePotential( referenceCell, cell2 );
				if( potential1 < potential2 ) {
					return -1;
				} else if( potential1 == potential2 ) {
					return 0;
				} else {
					return 1;
				}
			}
		}

		ArrayList<EvacCell> result = new ArrayList<>( cell.getNeighbours() );
		Collections.sort( result, new CellPrioritySorter( cell ) );
		return result;
	}

	/**
	 * Given a starting cell, this method picks one 
	 * of its reachable neighbors at random. The i-th neighbor is 
	 * chosen with probability {@code p(i) := N * exp[mergePotentials(i, cell)]}
	 * where N is a constant used for normalization. 
	 * 
	 * @param cell The starting cell
	 * @return A neighbor of {@code cell} chosen at random.
	 */
	@Override
	public EvacCell selectTargetCell( EvacCell cell, List<EvacCell> targets ) {
		if( targets.isEmpty() )
			return cell;

		double p[] = new double[targets.size()];

		double max = Integer.MIN_VALUE;
		int max_index = 0;

		for( int i = 0; i < targets.size(); i++ ) {
			p[i] = Math.exp( esp.parameterSet.effectivePotential( cell, targets.get( i ) ) );
			if( p[i] > max ) {
				max = p[i];
				max_index = i;
			}
		}

		// raising probablities only makes sense if the cell and all its neighbours are in the same room               
		boolean inSameRoom = true;
		for( int i = 0; i < targets.size(); i++ ) {
			if( !(cell.getRoom().equals( targets.get( i ).getRoom() )) ) {
				inSameRoom = false;
				break;
			}
		}
		if( inSameRoom ) {
			int startX = cell.getX();
			int startY = cell.getY();

			EvacCell mostProbableTarget = targets.get( max_index );
			int targetX = mostProbableTarget.getX();
			int targetY = mostProbableTarget.getY();

			boolean wayIsntDiagonal = (!(cell.equals( mostProbableTarget ))) && (startX == targetX) || (startY == targetY);

			if( wayIsntDiagonal ) {
				// raise the probability of the most probable cell:
				double c = 10.5;
				p[max_index] = max * c;
			} else {
				// find next probable cell (could be two next probable cells!):
				double max2 = 0;
				int max_index2 = 0;
				int nextProbable[] = { -1, -1 };
				for( int i = 0; i < targets.size(); i++ ) {
					int X = targets.get( i ).getX();
					int Y = targets.get( i ).getY();
					if( ((startX == X) && (targetY == Y)) || ((targetX == X) && (startY == Y)) ) {
						// if the cell is a (not-diagonal-)neighbour of the actual cell AND of the most probable cell
						if( p[i] > max2 ) {
							// if NEW max2 found
							max2 = p[i];
							nextProbable[0] = i;
							nextProbable[1] = -1;
						} else {
							if( p[i] == max2 ) {
								// if SECOND max2 found
								nextProbable[1] = i;
							}
						}
					}
				}//end for
				if( nextProbable[0] != -1 ) {
					// if at least one nextProbableCell found
					if( nextProbable[1] != -1 ) {
						// if exactly two nextProbableCells found
						// choose one of them randomly:
						if( RandomUtils.getInstance().binaryDecision( 0.5 ) ) {
							max_index2 = nextProbable[0];
						} else {
							max_index2 = nextProbable[1];
						}
					} else {
						// if exactly one nextProbableCell found
						max_index2 = nextProbable[0];
					}

					// raise the probability of the (chosen) nextProbableCell
					double c2 = 10.5;
					p[max_index2] = max2 * c2;
				}

			}//end else
		}// end if inSameRoom

		int number = RandomUtils.getInstance().chooseRandomlyAbsolute( p );
		return targets.get( number );
	}

	/**
	 * Updates the exhaustion for the individual and updates the statistic.
	 * @param i
	 * @param targetCell
	 */
	protected void updateExhaustion( Individual i, EvacCell targetCell ) {
		double oldExhaustion = i.getExhaustion();
		esp.parameterSet.updateExhaustion( i, targetCell );
		if( oldExhaustion != i.getExhaustion() ) {
			esp.caStatisticWriter.getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExhaustionToStatistic( i, esp.eca.getTimeStep(), i.getExhaustion() );
		}
	}

	/**
	 * Decides randomly if an individual idles.
	 * @param i An individual with a given slackness
	 * @return {@code true} with a probability of slackness or
	 * {@code false} otherwise. 
	 */
	protected boolean slack( Individual i ) {
		double randomNumber = RandomUtils.getInstance().getRandomGenerator().nextDouble();
		return (esp.parameterSet.idleThreshold( i ) > randomNumber);
	}
}
