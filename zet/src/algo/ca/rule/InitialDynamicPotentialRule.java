/**
 * Class InitialDynamicPotentialRule
 * Erstellt 28.04.2008, 21:45:37
 */

package algo.ca.rule;

import ds.ca.Cell;

/**
 * An initialization rule that assings the dynamic potential to individuals.
 * @author Jan-Philipp Kappmeier
 */
public class InitialDynamicPotentialRule extends AbstractInitialRule {

	/**
	 * Checks if an {@link Individual} is assigned to the cell.
	 * @param cell the cell
	 * @return true if an individual is on the cell.
	 */
	@Override
	public boolean executableOn( Cell cell ) {
		if(cell.getIndividual() == null)
			return false;    		
		else
			return true;
	}

	/**
	 * Assigns the dynamic potential to the cell's individual. It is found in the
	 * {@link PotentialController}.
	 * @param cell
	 */
	@Override
	protected void onExecute( Cell cell ) {
		cell.getIndividual().setDynamicPotential(this.caController().getPotentialController().getPm().getDynamicPotential());
	}
	
}
