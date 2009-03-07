/**
 * Class ApplyExitMappingRule
 * Erstellt 03.12.2008, 23:02:42
 */

package algo.ca.rule;

import ds.ca.Cell;
import ds.ca.Individual;
import ds.ca.StaticPotential;
import ds.ca.TargetCell;
import java.util.HashMap;

/**
 * This rule applies the exit mapping to the cellular automaton. It is explicitly
 * allowed to have individuals with no mapped exit.
 * @author Jan-Philipp Kappmeier
 */
public class ApplyExitMappingRule extends AbstractInitialRule {
	boolean initialized = false;
	HashMap<TargetCell, StaticPotential> potentialMapping;
	
	private void init() {
		//private void applyMapping(CellularAutomaton ca, IndividualToExitMapping mapping){
		potentialMapping = new HashMap<TargetCell, StaticPotential>();
		for( StaticPotential potential : caController().getCA().getPotentialManager().getStaticPotentials() ) {
			for( TargetCell target : potential.getAssociatedExitCells() ) {
				if( potentialMapping.put( target, potential ) != null ) {
					throw new IllegalArgumentException( "There were two potentials leading to the same exit. This method can currently not deal with this." );
				}
			}
		}
		initialized = true;
	}
	
	/**
	 * Checks, whether the rule is executable or not.
	 * @param cell the cell on which the rule should be executed
	 * @return Returns true, if an Individual is standing
	 * on this cell, and moreover this Individual does 
	 * not already have a StaticPotential.
	 */
	@Override
	public boolean executableOn( Cell cell ) {
		return cell.getIndividual() != null;
	}

	/**
	 * Assignes an exit (more precisely: the potential) for an individual.
	 * @param cell the cell on which the individual stands
	 */
	@Override
	protected void onExecute( Cell cell ) {
		if( !initialized )
			init();
		
		//for( Individual individual : getIndividuals() ) {
		Individual individual = cell.getIndividual();
			TargetCell target = caController().getCA().getIndividualToExitMapping().getExit( individual );
			if( target == null ) {
				if( !individual.isDead() )
					System.out.println( "Individual " + individual.getNumber() + " has no manual exit assigned." );
			} else {
				StaticPotential potential = potentialMapping.get( target );
				if( potential == null )
					throw new IllegalArgumentException( "The target cell (room id, x, y) " + target.getRoom().getID() + ", " + target.getX() + ", " + target.getY() + " does not correspond to a static potential." );
				individual.setStaticPotential( potential );
			}
		//}
	}

}
