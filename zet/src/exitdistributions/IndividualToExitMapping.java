package exitdistributions;

import ds.ca.Individual;
import ds.ca.TargetCell;

public abstract class IndividualToExitMapping {
	
	public abstract TargetCell getExit(Individual individual);

}
