/**
 * EvacuationSimulationProblem.java
 * Created: 10.02.2012, 17:33:02
 */
package algo.ca.algorithm.evac;

import algo.ca.PotentialController;
import algo.ca.RuleSet;
import algo.ca.SPPotentialController;
import algo.ca.algorithm.CellularAutomatonSimulationProblem;
import algo.ca.parameter.AbstractDefaultParameterSet;
import algo.ca.parameter.ParameterSet;
import algo.ca.rule.Rule;
import ds.PropertyContainer;
import ds.ca.evac.EvacuationCellularAutomaton;
import ds.z.Project;
import statistic.ca.CAStatisticWriter;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationSimulationProblem extends CellularAutomatonSimulationProblem {
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

		parameterSet = AbstractDefaultParameterSet.createParameterSet( props.getAsString( "algo.ca.parameterSet" ) );

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
