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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import util.IOTools;

public class SaveIndividualsRule extends AbstractSaveRule {
	static String nord = IOTools.getNextFreeNumberedFilename( "./", "results_nord", 3 );
	static String süd = IOTools.getNextFreeNumberedFilename( "./", "results_süd", 3 );
	// muss VOR der EvacuateIndividualsRule aufgerufen werden!
	public SaveIndividualsRule() {
	}

	@Override
	protected void onExecute( ds.ca.Cell cell ) {
		ds.ca.Individual savedIndividual = cell.getIndividual();
		if( !(savedIndividual.isSafe()) ) {
		try {
			//System.out.println( savedIndividual.getStaticPotential().getName() );
			// Write to file
			File f = null;
			if( savedIndividual.getStaticPotential().getName().equals( "Nordausgang" ) )
				f = new File( "./" + nord + ".txt" );
			else
				f = new File( "./" + süd + ".txt" );
			FileWriter w = new FileWriter( f, true );
			Double d = savedIndividual.getStepEndTime() * caController().getCA().getSecondsPerStep();
			Double d2 = caController().getCA().getTimeStep() * caController().getCA().getSecondsPerStep();
			w.append( Double.toString(  d  ) + '\n' );
			w.close();
		} catch( IOException ex ) {

		}

			//caController().getCA().decreaseNrOfLivingAndNotSafeIndividuals();
			//savedIndividual.setSafe();
			caController().getCA().setIndividualSave( savedIndividual );
			savedIndividual.setPanic( 0 );
			//savedIndividual.setSafetyTime(caController().getCA().getTimeStep());

			if( cell instanceof ds.ca.SaveCell ) {
				ds.ca.StaticPotential correspondingExitPotential = ((ds.ca.SaveCell) cell).getExitPotential();
				if( correspondingExitPotential == null ) {
					savedIndividual.setStaticPotential( caController().getCA().getPotentialManager().getSafePotential() );
				//throw new IllegalStateException("An individual is in a save area with no exit");
				} else {
					if( savedIndividual.getStaticPotential() != correspondingExitPotential ) {
						caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addChangedPotentialToStatistic( savedIndividual, caController().getCA().getTimeStep() );
						savedIndividual.setStaticPotential( correspondingExitPotential );
					}
					caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExitToStatistic( savedIndividual, correspondingExitPotential );
				}
			}

			caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addSafeIndividualToStatistic( savedIndividual );

		}
	// else: nothing!
	}

	@Override
	public boolean executableOn( ds.ca.Cell cell ) {
		// Regel NUR anwendbar, wenn auf der Zelle ein Individuum steht
		// und die Zelle eine Exit- Savecell oder  ist
		return (cell.getIndividual() != null) && ((cell instanceof ds.ca.ExitCell) || (cell instanceof ds.ca.SaveCell));
	}
}

