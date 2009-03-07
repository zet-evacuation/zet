/*
 * Created on 09.06.2008
 *
 */
package algo.ca.util;

import ds.ca.Cell;
import ds.ca.ExitCell;
import ds.ca.StaticPotential;
import java.util.Collection;

/**
 * @author Daniel Pluempe
 *
 */
public class PotentialUtils {

    /**
     * This method merges StaticPotentials into a new one. The new potential
     * is calculated for each cell by taking the minimum over all given static potentials.
     * The attractivity of the new static potential is the average over all attractivity values given by the
     * specified static potentials to merge.
     * @param potentialsToMerge Contains an ArrayList with the StaticPotential object to merge
     * @return the new potential
     */

    public static StaticPotential mergePotentials(Collection<StaticPotential> potentialsToMerge) {
        StaticPotential newSP = new StaticPotential();
        //stores the minimum potential for one cell over all potentials in potentialsToMerge
        int minPot;
        //stores the sum of all attractivity values
        int totalAttractivity = 0;
        // iterate over all mapped cells
        for(StaticPotential sp : potentialsToMerge) {
            totalAttractivity += sp.getAttractivity();
            for(Cell c : sp.getMappedCells()) {
                //for each cell get the minimum of all potentials in potentialsToMerge and put in into newSP
                minPot = Integer.MAX_VALUE;
                for(StaticPotential localSP : potentialsToMerge){    
                    if(localSP.getPotential(c) >= 0){ // if the potential is negative, it is invalid for the cell
                        minPot = Math.min(minPot,localSP.getPotential(c));
                    }
                }
                //put the current cell along with minPot in newSP
                newSP.setPotential(c, minPot);
            }
            for(ExitCell c : sp.getAssociatedExitCells())
            newSP.getAssociatedExitCells().add(c);
        }
        if( potentialsToMerge.size() > 0 )  // catch error if no exit is set!
            newSP.setAttractivity(totalAttractivity / potentialsToMerge.size());
        return newSP;
    }
}
