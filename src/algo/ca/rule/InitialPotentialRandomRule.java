package algo.ca.rule;

import ds.ca.Cell;
import ds.ca.Individual.DeathCause;
import ds.ca.StaticPotential;
import java.util.ArrayList;
import util.random.RandomUtils;

/**
 *
 */
public class InitialPotentialRandomRule extends AbstractInitialRule  {
	/**
	 * Checks, whether the rule is executable or not.
	 * @return Returns true, if an Individual is standing 
	 * on this cell, and moreover this Individual does 
	 * not already have a StaticPotential.
	 */
	@Override
	public boolean executableOn(Cell cell){
		if (cell.getIndividual() == null)
                    return false;
		else 
                    return true;
	}

	/**	 
	 * @param cell the cell
	 */
	@Override
	protected void onExecute(Cell cell) {            
            ArrayList<StaticPotential> exits = new ArrayList<StaticPotential>();
            exits.addAll(this.caController().getCA().getPotentialManager().getStaticPotentials());
            int numberOfExits = exits.size();
            RandomUtils random = RandomUtils.getInstance();
            int randomExitNumber = random.getRandomGenerator().nextInt(numberOfExits);            
            
            boolean exitFound = false;
            if (exits.get(randomExitNumber).getPotential(cell) < 0){
                for (StaticPotential exit : exits){
                    if (exit.getPotential(cell) >= 0){
                        cell.getIndividual().setStaticPotential(exit);
                        exitFound = true;
                        break;
                    }                    
                }
                if (!exitFound) this.caController().getCA().setIndividualDead( cell.getIndividual(), DeathCause.EXIT_UNREACHABLE );                
            } else {
                cell.getIndividual().setStaticPotential(exits.get(randomExitNumber));
            }
        }
        
}