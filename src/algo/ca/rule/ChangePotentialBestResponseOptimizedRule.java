/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
import ds.ca.evac.EvacCell;
import ds.ca.evac.Individual;
import ds.ca.evac.StaticPotential;
import ds.ca.evac.Room;
import evacuationplan.BestResponseDynamics;

/**
 *
 * @author Jan-Philipp Kappmeier
 * @author Joscha Kulbatzki
 */
public class ChangePotentialBestResponseOptimizedRule extends AbstractPotentialChangeRule {

	private static final int TIME_STEP_LIMIT_FOR_NASH_EQUILIBRIUM = 25;

	/**
	 *
	 * @param cell
	 * @return true if the potential change rule can be used
	 */
	@Override
	public boolean executableOn( EvacCell cell ) {
		int timeStep = esp.eca.getTimeStep();
		return ((timeStep < TIME_STEP_LIMIT_FOR_NASH_EQUILIBRIUM) & (cell.getIndividual() != null)) ? true : false;

	}

  /**
 	 *
	 * @param cell
	 */
	@Override
	protected void onExecute( EvacCell cell ) {
		// perform initial best response dynamics exit selection
		BestResponseDynamics brd = new BestResponseDynamics();
    brd.computePotential( cell, esp.eca );
	}
}
