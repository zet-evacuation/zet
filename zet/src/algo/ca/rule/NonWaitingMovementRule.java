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
package algo.ca.rule;

import java.util.ArrayList;
import java.util.Comparator;

import ds.ca.Cell;
import ds.ca.Individual;
import ds.ca.StairCell;
import ds.ca.results.VisualResultsRecorder;
import ds.ca.results.IndividualStateChangeAction;
import java.util.Collections;
import de.tu_berlin.math.coga.rndutils.RandomUtils;
import util.DebugFlags;
import de.tu_berlin.math.coga.common.util.Direction;
import de.tu_berlin.math.coga.common.util.Level;

/**
 * 
 * @author Jan-Philipp Kappmeier
 */
public class NonWaitingMovementRule extends AbstractMovementRule {

	static double timeCounter = 0;
	static double distCounter = 0;

	public NonWaitingMovementRule() {
	}

	@Override
	protected void onExecute( ds.ca.Cell cell ) {
		if( DebugFlags.EVAPLANCHECKER ) {
			System.out.print( "Move individual " + cell.getIndividual().id() + " " );
		}
		Individual actor = cell.getIndividual();
		if( actor.isAlarmed() == true ) {
			if( canMove( actor ) ) {
				if( slack( actor ) ) {
					this.updateExhaustion( actor, cell );
					this.setPerformMove( true );
					doMove( actor, cell );
				} else {
					if( this.isDirectExecute() ) {
						//Cell targetCell = this.selectTargetCell( cell, selectPossibleTargets( cell, true ) );
						Cell targetCell = getTargetCell( cell );
						setPerformMove( true );
						move( actor, targetCell );
					} else {
						this.setPossibleTargets( selectPossibleTargets( cell, false ) );
						setPerformMove( true );
					}
				}
				this.updateSpeed( actor );
			} else {
				// Individual can't move, it is already moving
				setPerformMove( false );
			}
		} else {
			// Individual is not alarmed, that means it remains standing on the cell
			setPerformMove( true );
			doMove( actor, cell );
		}
		VisualResultsRecorder.getInstance().recordAction( new IndividualStateChangeAction( actor ) );
	}

	private Cell getTargetCell( Cell cell ) {
		Cell targetCell = this.selectTargetCell( cell, selectPossibleTargets( cell, true ) );
		int xd = targetCell.getX() - cell.getX();
		int yd = targetCell.getY() - cell.getY();

		boolean diagonal = ((xd == -1) || (xd == 1)) && ((yd == -1) || (yd == 1));
		if( diagonal ) {
			boolean indVary = cell.getRoom().existsCellAt( cell.getX(), cell.getY()+yd ) && cell.getRoom().getCell( cell.getX(), cell.getY()+yd ).getIndividual() == null;
			boolean indVarx = cell.getRoom().existsCellAt( cell.getX()+xd, cell.getY() ) && cell.getRoom().getCell( cell.getX()+xd, cell.getY() ).getIndividual() == null;

			if( indVary && indVarx )
				return targetCell;
			else if( !indVary && !indVarx )
				return cell;
			else if( indVary && !indVarx) {
				final Cell possibleCell = cell.getRoom().getCell( cell.getX(), cell.getY()+yd );
				return possibleCell;
			} else
				return cell.getRoom().getCell( cell.getX()+xd, cell.getY() );
		} else
			return targetCell;
	}

	private void doMove( Individual i, Cell targetCell ) {
		if( i.getCell().equals( targetCell ) ) {
			i.setStepStartTime( i.getStepEndTime() );
			setStepEndTime( i, i.getStepEndTime() + 1 );
			//i.setStepEndTime( i.getStepEndTime() + 1 );
			caController().getCA().moveIndividual( targetCell, targetCell );
			//i.getCell().getRoom().moveIndividual( targetCell, targetCell );
			setPerformMove( false );
			caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCurrentSpeedToStatistic( i, this.caController().getCA().getTimeStep(), 0 );
			return;
		}

		doMoveWithDecision( i, targetCell, true );
		setPerformMove( false );
	}

	public void move( Individual i, Cell targetCell ) {
		if( i.isSafe() && !((targetCell instanceof ds.ca.SaveCell) || (targetCell instanceof ds.ca.ExitCell)) ) {
			// Rauslaufen aus sicheren Bereichen ist nicht erlaubt
			targetCell = i.getCell();
		} else {
			this.updatePanic( i, targetCell );
		}
		this.updateExhaustion( i, targetCell );

		if( i.getCell().equals( targetCell ) ) {
			caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addWaitedTimeToStatistic( i, caController().getCA().getTimeStep() );
			caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForCells().addCellToWaitingStatistic( targetCell, this.caController().getCA().getTimeStep() );
		}
		caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForCells().addCellToUtilizationStatistic( targetCell, this.caController().getCA().getTimeStep() );
		this.doMove( i, targetCell );
		setPerformMove( false );
	}

	protected void updatePanic( Individual i, Cell targetCell ) {
		double oldPanic = i.getPanic();
		this.parameters.updatePanic( i, targetCell, this.neighboursByPriority( i.getCell() ) );
		if( oldPanic != i.getPanic() ) {
			caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addPanicToStatistic( i, caController().getCA().getTimeStep(), i.getPanic() );
		}
	}

	protected void updateSpeed( Individual i ) {
		this.parameters.updatePreferredSpeed( i );

	}

	/**
	 * Returns all reachable neighbours sorted according to their priority which
	 * is calculated by mergePotential(). The first element in the list is the
	 * most probable neighbour, the last element is the least probable neighbour.
	 * @param cell The cell whose neighbours are to be sorted
	 * @return A sorted list of the neighbour cells of <code>cell</code>, sorted
	 * in an increasing fashion according to their potential computed by 
	 * <code>mergePotential</code>.
	 */
	protected ArrayList<Cell> neighboursByPriority( Cell cell ) {
		class CellPrioritySorter implements Comparator<Cell> {

			final Cell referenceCell;

			public CellPrioritySorter( Cell referenceCell ) {
				this.referenceCell = referenceCell;
			}

			@Override
			public int compare( Cell cell1, Cell cell2 ) {
				final double potential1 = parameters.effectivePotential( referenceCell, cell1 );
				final double potential2 = parameters.effectivePotential( referenceCell, cell2 );
				if( potential1 < potential2 ) {
					return -1;
				} else if( potential1 == potential2 ) {
					return 0;
				} else {
					return 1;
				}
			}
		}

		ArrayList<Cell> result = new ArrayList<Cell>( cell.getNeighbours() );
		Collections.sort( result, new CellPrioritySorter( cell ) );
		return result;
	}

	/**
	 * Given a starting cell, this method picks one 
	 * of its reachable neighbours at random. The i-th neighbour is 
	 * chosen with probability <code>p(i) := N * exp[mergePotentials(i, cell)]</code>
	 * where N is a constant used for normalisation. 
	 * 
	 * @param cell The starting cell
	 * @return A neighbour of <code>cell</code> chosen at random.
	 */
	@Override
	public Cell selectTargetCell( Cell cell, ArrayList<Cell> targets ) {
		if( targets.size() == 0 )
			return cell;

		double p[] = new double[targets.size()];

		double max = Integer.MIN_VALUE;
		int max_index = 0;

		for( int i = 0; i < targets.size(); i++ ) {
			p[i] = Math.exp( parameters.effectivePotential( cell, targets.get( i ) ) );
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

			Cell mostProbableTarget = targets.get( max_index );
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
	protected void updateExhaustion( Individual i, Cell targetCell ) {
		//throw new java.lang.IllegalStateException( "update exhaustion aufgerufen" );
		double oldExhaustion = i.getExhaustion();
		this.parameters.updateExhaustion( i, targetCell );
		if( oldExhaustion != i.getExhaustion() ) {
			caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExhaustionToStatistic( i, caController().getCA().getTimeStep(), i.getExhaustion() );
		}
	}

	/**
	 * Decides randomly if an individual moves. (falsch)
	 * @param i An individual with a given parameters
	 * @return <code>true</code> if the individual moves or
	 * <code>false</code> otherwise. 
	 */
	//gibt true wieder, wenn geschwindigkeit von zelle und individuel (wkeit darueber) bewegung bedeuten
	protected boolean canMove( Individual i ) {
		if( this.caController().getCA().getTimeStep() >= i.getStepEndTime() ) {
			return true;
		}
		return false;
	}

	/**
	 * Decides randomly if an individual idles.
	 * @param i An individual with a given slackness
	 * @return <code>true</code> with a probability of slackness or
	 * <code>false</code> otherwise. 
	 */
	protected boolean slack( Individual i ) {
		double randomNumber = RandomUtils.getInstance().getRandomGenerator().nextDouble();
		return (this.parameters.idleThreshold( i ) > randomNumber);
	}

	/**
	 * Decides whether the rule can be applied to the current cell. 
	 * Returns <code>true</code> if the cell is occupied by an individual
	 * or <code>false</code> otherwise.
	 * @param cell
	 * @return true if the rule can be executed
	 */
	@Override
	public boolean executableOn( ds.ca.Cell cell ) {
		// Regel nicht anwendbar, wenn auf der Zelle kein Individuum steht.
		return cell.getIndividual() != null;
	}

	@Override
	public void swap( Cell cell1, Cell cell2 ) {
		if( cell1.getIndividual() == null ) {
			throw new IllegalArgumentException( "No Individual standing on cell #1!" );
		}
		if( cell2.getIndividual() == null ) {
			throw new IllegalArgumentException( "No Individual standing on cell #2!" );
		}
		if( cell1.equals( cell2 ) ) {
			throw new IllegalArgumentException( "The cells are equal. Can't swap on equal cells." );
		}
		doMoveWithDecision( cell1.getIndividual(), cell2, false );
		doMoveWithDecision( cell2.getIndividual(), cell1, false );
		caController().getCA().swapIndividuals( cell1, cell2 );
		//cell1.getRoom().swapIndividuals( cell1, cell2 );
	}

	private void doMoveWithDecision( Individual i, Cell targetCell, boolean performMove ) {
		this.caController().getPotentialController().increaseDynamicPotential( targetCell );
		// Calculate a factor that is later multiplied with the speed,
		// this factor is only != 1 for stair cells to 
		// give different velocities for going a stair up or down.
		double stairSpeedFactor = 1;
		if( targetCell instanceof StairCell ) {

			StairCell stairCell = (StairCell) targetCell;
			int x = targetCell.getX() - i.getCell().getX();
			int y = targetCell.getY() - i.getCell().getY();
			Direction direction = Direction.getDirection( x, y );
			Level lvl = stairCell.getLevel( direction );
			if( lvl == Level.Higher ) {
				stairSpeedFactor = stairCell.getSpeedFactorUp();
			} else if( lvl == Level.Lower ) {
				stairSpeedFactor = stairCell.getSpeedFactorDown();
			}
		}

		// TODO check if this big stuff is really necessery! maybe easier!
		// calculate distance
		double dist;
		final double sqrt2 = Math.sqrt( 2 ) * 0.4;
		if( !targetCell.getRoom().equals( i.getCell().getRoom() ) ) {
			if( i.getCell().getX() + i.getCell().getRoom().getXOffset() == targetCell.getX() + targetCell.getRoom().getXOffset() && i.getCell().getY() + i.getCell().getRoom().getYOffset() == targetCell.getY() + targetCell.getRoom().getYOffset() ) {
				dist = 0.4;
			} else if( i.getCell().getX() + i.getCell().getRoom().getXOffset() == targetCell.getX() + targetCell.getRoom().getXOffset() | i.getCell().getY() + i.getCell().getRoom().getYOffset() == targetCell.getY() + targetCell.getRoom().getYOffset() ) {
				dist = 0.4;
			} else {
				dist = sqrt2;
			}
		} else {
			if( i.getCell().getX() == targetCell.getX() && i.getCell().getY() == targetCell.getY() ) {
				dist = 0;
			} else if( i.getCell().getX() == targetCell.getX() | i.getCell().getY() == targetCell.getY() ) {
				dist = 0.4;
			} else {
				dist = sqrt2;
			}
		}


		// Perform Movement if the individual changes the room!
		//if( i.getCell().getRoom() != targetCell.getRoom() )
		//	i.getCell().getRoom().moveIndividual( i.getCell(), targetCell );

		// update times
		if( this.caController().getCA().absoluteSpeed( i.getCurrentSpeed() ) >= 0.0001 ) {
			double speed = this.caController().getCA().absoluteSpeed( i.getCurrentSpeed() );
			speed *= targetCell.getSpeedFactor() * stairSpeedFactor;
			//System.out.println( "Speed ist " + speed );
			// zu diesem zeitpunkt ist die StepEndtime aktualisiert, falls ein individual vorher geslackt hat
			// oder sich nicht bewegen konnte.
			timeCounter += (dist / speed);
			distCounter += dist;
			i.setStepStartTime( i.getStepEndTime() );
			setStepEndTime( i, i.getStepEndTime() + (dist / speed) * this.caController().getCA().getStepsPerSecond() );
			if( performMove ) {
				//i.getCell().getRoom().moveIndividual( i.getCell(), targetCell );
				caController().getCA().moveIndividual( i.getCell(), targetCell );
				caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCurrentSpeedToStatistic( i, this.caController().getCA().getTimeStep(), speed * this.caController().getCA().getSecondsPerStep() );
				caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addCoveredDistanceToStatistic( i, (int) Math.ceil( i.getStepEndTime() ), dist );
			} else {
				if( util.DebugFlags.CA_SWAP_USED_OUTPUT )
					System.err.println( "Individuum Läuft doch nicht!!" );
			}
		} else {
			throw new IllegalStateException( "Individuum has no speed." );
		}
	}

	/**
	 * Sets the time when the current movement is over for an individual and
	 * actualizates the needed time in the cellular automaton.
	 * @param i the individual
	 * @param d the (real) time when the movement is over
	 */
	private void setStepEndTime( Individual i, double d ) {
		i.setStepEndTime( d );
		caController().getCA().setNeededTime( (int) Math.ceil( d ) );
	}
}