
package algo.ca.rule;

import java.util.ArrayList;
import java.util.Collections;
import ds.ca.Cell;
import ds.ca.StaticPotential;
import ds.ca.Individual;
/**
 *
 * @author Sylvie
 */
/**
 * This rule chooses an Individual's (the one standing on the current cell) initial
 * StaticPotential according to the attractivity value of those Exit-Cells, whose
 * distances to the Individual are acceptable according to the Individual's 
 * familarity value.
 * If the Individual standing on "cell" is caged (it cannot leave the building, 
 * because there is now passable way to an ExitCell), this Individual has to die
 * because it cannot be evacuated.
 * @author marcel
 *
 */
public class InitialPotentialShortestPathRule extends AbstractInitialRule {

	/**
	 * Checks, whether the rule is executable or not.
	 * @param cell the cell on which the rule should be executed
	 * @return Returns true, if an Individual is standing
	 * on this cell, and moreover this Individual does 
	 * not already have a StaticPotential.
	 */
	@Override
	public boolean executableOn( Cell cell ) {
            //System.err.println("wird getestet");
		if( cell.getIndividual() == null ) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * This rule chooses an Individual's (the one standing on "cell") initial
	 * StaticPotential. This Potential leads to the nearest exit.
	 * If the Individual standing on "cell" is caged (it cannot leave the building, 
	 * because there is now passable way to an ExitCell), this Individual has to die
	 * because it cannot be evacuated.
	 * @param cell the cell
	 */
	@Override
	protected void onExecute( Cell cell ) {
            //System.err.println("wird ausgeführt");
		Individual individual = cell.getIndividual();
		ArrayList<StaticPotential> staticPotentials = new ArrayList<StaticPotential>();
		staticPotentials.addAll( this.caController().getCA().getPotentialManager().getStaticPotentials() );
                StaticPotential initialPotential = new StaticPotential();
		double minDistanceToEvacArea = Double.MAX_VALUE;
		double distanceToEvacArea;
                //int i=0;
                //int j=0;
		for( StaticPotential sp : staticPotentials ) {
                    //i++;
			distanceToEvacArea = sp.getDistance( individual.getCell() );
                        //System.err.println("Distanz = "+distanceToEvacArea);
                        //System.err.println("aktMinDistanz = "+minDistanceToEvacArea);
			if( distanceToEvacArea >= 0 && distanceToEvacArea <= minDistanceToEvacArea ) {
				minDistanceToEvacArea = sp.getDistance( individual.getCell() );
                                initialPotential = sp;
                                //j=i;
                                //System.err.println("Potential übernommen");
			}
                }
                //System.err.println("--- "+j+". POTENTIAL ---");
                // Check whether the individual is caged and cannot leave the building -> it has to die
		if (minDistanceToEvacArea == Double.MAX_VALUE) {
                    this.caController().getCA().setIndividualDead( individual, Individual.DeathCause.EXIT_UNREACHABLE );
                }

			individual.setStaticPotential( initialPotential );
			caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addMinDistancesToStatistic( individual, minDistanceToEvacArea, initialPotential.getDistance( cell ) );
			caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addChangedPotentialToStatistic( individual, 0 );
			caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExhaustionToStatistic( individual, 0, individual.getExhaustion() );
			caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addPanicToStatistic( individual, 0, individual.getPanic() );
		// ToDo: Potential des Individuums im VisualResultsRecorder speichern
		}
	}

