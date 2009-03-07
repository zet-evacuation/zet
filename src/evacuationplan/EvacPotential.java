package evacuationplan;

import ds.ca.Cell;
import ds.ca.Individual;
import ds.ca.StaticPotential;

public class EvacPotential extends StaticPotential {
	
	Individual ind;
	CAPathPassabilityChecker checker;
	
	public EvacPotential(Individual ind, CAPathPassabilityChecker checker){
		this.ind = ind;
		this.checker = checker;
	}
	
	@Override
	public int getPotential (Cell cell) throws IllegalArgumentException{
		if(cell != null){
			Double potential = cellToPotential.get(cell);
			if(potential != null){
				if(checker.canPass(ind, ind.getCell(), cell)) {
					// TODO Potential Long
					return (int)Math.round( potential );
				} else {
					return Integer.MAX_VALUE;
				}
			} else {
				return Integer.MAX_VALUE;
			}
		}
		else {
			return Integer.MAX_VALUE;
		}
    }
	
	@Override
	public int getTruePotential(Cell cell){
	    return super.getPotential(cell);
	}
	
}
