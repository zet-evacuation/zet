/*
 * Created on 23.01.2008
 */
package ds.ca;

import algo.ca.PotentialController;
import algo.ca.RuleSet;
import algo.ca.parameter.ParameterSet;
import algo.ca.rule.Rule;
import statistic.ca.CAStatisticWriter;

/**
 * <p> The <code>CAController</code> works as an controlle in the MVC design
 * pattern. It allows to access the cellular automaton and all the
 * datastructures connected with it to do both, run an ca algorithm and get
 * information for the visualization.</p>
 * @author Daniel Pluempe, Jan-Philipp Kappmeier
 */
public class CAController {

	CellularAutomaton ca;
	RuleSet ruleSet;
	// TODO ändern ;)
	public ParameterSet parameterSet;
	PotentialController potentialController;
	CAStatisticWriter caStatisticWriter;

	/**
	 * 
	 * @param ca
	 * @param ruleSet
	 * @param parameterSet
	 * @param potentialController
	 */
	public CAController( CellularAutomaton ca, RuleSet ruleSet, ParameterSet parameterSet, PotentialController potentialController, CAStatisticWriter caStatisticWriter ) {
		this.ca = ca;
		this.ruleSet = ruleSet;
		this.parameterSet = parameterSet;
		this.potentialController = potentialController;
		this.caStatisticWriter = caStatisticWriter;

		for( Rule rule : ruleSet ) {
			rule.setCAController( this );
		}
	}

	public CAStatisticWriter getCaStatisticWriter() {
		return caStatisticWriter;
	}

	/**
	 * 
	 * @return
	 */
	public CellularAutomaton getCA() {
		return ca;
	}

	/**
	 * 
	 * @return
	 */
	public RuleSet getRuleSet() {
		return ruleSet;
	}

	/**
	 * 
	 * @return
	 */
	public ParameterSet getParameterSet() {
		return parameterSet;
	}

	/**
	 * 
	 * @return
	 */
	public PotentialController getPotentialController() {
		return potentialController;
	}
}
