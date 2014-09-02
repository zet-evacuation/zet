///* zet evacuation tool copyright (c) 2007-14 zet evacuation team
// *
// * This program is free software; you can redistribute it and/or
// * as published by the Free Software Foundation; either version 2
// * of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
// */
///*
// * Created on 23.01.2008
// */
//package ds.ca.evac;
//
//import algo.ca.PotentialController;
//import algo.ca.RuleSet;
//import algo.ca.parameter.ParameterSet;
//import algo.ca.rule.Rule;
//import statistic.ca.CAStatisticWriter;
//
///**
// * <p> The {@code CAController} works as an controlle in the MVC design
// * pattern. It allows to access the cellular automaton and all the
// * datastructures connected with it to do both, run an ca algorithm and get
// * information for the visualization.</p>
// * @author Daniel R. Schmidt, Jan-Philipp Kappmeier
// */
//public class CAController {
//
//	EvacuationCellularAutomaton ca;
//	RuleSet ruleSet;
//	// TODO ändern ;)
//	public ParameterSet parameterSet;
//	PotentialController potentialController;
//	CAStatisticWriter caStatisticWriter;
//
//	/**
//	 * 
//	 * @param ca
//	 * @param ruleSet
//	 * @param parameterSet
//	 * @param potentialController
//	 */
//	public CAController( EvacuationCellularAutomaton ca, RuleSet ruleSet, ParameterSet parameterSet, PotentialController potentialController, CAStatisticWriter caStatisticWriter ) {
//		this.ca = ca;
//		this.ruleSet = ruleSet;
//		this.parameterSet = parameterSet;
//		this.potentialController = potentialController;
//		this.caStatisticWriter = caStatisticWriter;
//
//		for( Rule rule : ruleSet ) {
//			rule.setCAController( this );
//		}
//	}
//
//	public CAStatisticWriter getCaStatisticWriter() {
//		return caStatisticWriter;
//	}
//
//	/**
//	 * Returns the {@link ds.ca.EvacuationCellularAutomaton} that is controlled.
//	 * @return the cellular automaton
//	 */
//	public EvacuationCellularAutomaton getCA() {
//		return ca;
//	}
//
//	/**
//	 * Returns the {@link RuleSet}that is used during execution of the
//	 * {@link ds.ca.EvacuationCellularAutomaton}
//	 * @return the ruleset
//	 */
//	public RuleSet getRuleSet() {
//		return ruleSet;
//	}
//
//	/**
//	 * Returns the {@link ParameterSet} that is used during execution of the
//	 * {@link ds.ca.EvacuationCellularAutomaton}
//	 * @return the parameter set
//	 */
//	public ParameterSet getParameterSet() {
//		return parameterSet;
//	}
//
//	/**
//	 * Returns the {@link PotentialController} used during execution of the
//	 * {@link ds.ca.EvacuationCellularAutomaton}
//	 * @return the potential controller
//	 */
//	public PotentialController getPotentialController() {
//		return potentialController;
//	}
//}
