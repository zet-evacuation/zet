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

package algo.ca.algorithm.evac;

import algo.ca.PotentialController;
import algo.ca.RuleSet;
import algo.ca.SPPotentialController;
import algo.ca.parameter.AbstractParameterSet;
import algo.ca.parameter.ParameterSet;
import algo.ca.rule.Rule;
import de.tu_berlin.math.coga.algorithm.simulation.cellularautomaton.CellularAutomatonSimulationProblem;
import ds.PropertyContainer;
import ds.ca.evac.EvacCell;
import ds.ca.evac.EvacuationCellularAutomaton;
import de.zet_evakuierung.model.Project;
import statistic.ca.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationSimulationProblem extends CellularAutomatonSimulationProblem<EvacCell> {
	public EvacuationCellularAutomaton eca;
	public RuleSet ruleSet;
	public ParameterSet parameterSet;
	public PotentialController potentialController;
	public CAStatisticWriter caStatisticWriter;
	private Project project;

	public EvacuationSimulationProblem( EvacuationCellularAutomaton ca ) {
		super( ca );
		eca = ca;

			PropertyContainer props = PropertyContainer.getInstance();

		ruleSet = RuleSet.createRuleSet( props.getAsString( "algo.ca.ruleSet" ) );
		for( Rule rule : ruleSet ) {
			rule.setEvacuationSimulationProblem( this );
		}

		parameterSet = AbstractParameterSet.createParameterSet( props.getAsString( "algo.ca.parameterSet" ) );

		potentialController = new SPPotentialController( ca );
		caStatisticWriter = new CAStatisticWriter();
		potentialController.setCA( ca );
		potentialController.setPm( ca.getPotentialManager() );
		ca.setAbsoluteMaxSpeed( parameterSet.getAbsoluteMaxSpeed() );

	}

	public void setProject( Project project ) {
		this.project = project;
	}

	public Project getProject() {
		return project;
	}

}
