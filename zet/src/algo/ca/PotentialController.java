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

package algo.ca;

import java.util.ArrayList;

import ds.ca.evac.EvacCell;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.ca.evac.ExitCell;
import ds.ca.evac.PotentialManager;
import ds.ca.evac.StaticPotential;

/**
 * @author Daniel R. Schmidt
 *
 */
public interface PotentialController {

	public EvacuationCellularAutomaton getCA();

	public void setCA( EvacuationCellularAutomaton ca );

	public PotentialManager getPm();

	public void setPm( PotentialManager pm );

	public void updateDynamicPotential( double diffusion, double decay );

	public StaticPotential mergePotentials( ArrayList<StaticPotential> potentialsToMerge );

	public void increaseDynamicPotential( EvacCell cell );

	public void decreaseDynamicPotential( EvacCell cell );

	public StaticPotential createStaticPotential( ArrayList<ExitCell> exitBlock );

	public StaticPotential getRandomStaticPotential();

	public StaticPotential getNearestExitStaticPotential( EvacCell c );

	public String dynamicPotentialToString();

	public void generateSafePotential();
}
