package algo.ca.rule;

import ds.ca.Individual;

// dieselbe wie die normale EvacuateIndividualsRule!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
public class PaperEvacuateIndividualsRule extends AbstractEvacuationRule {

	public PaperEvacuateIndividualsRule() {
	}

	@Override
	protected void onExecute( ds.ca.Cell cell ) {
		caController().getCA().markIndividualForRemoval( cell.getIndividual() );
		// Potential needed for statistics:
		ds.ca.StaticPotential exit = caController().getPotentialController().getNearestExitStaticPotential( cell );
		caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExitToStatistic( cell.getIndividual(), exit );
		// safetyTime etc will be set in the SaveIndividualsRule
	}

	@Override
	public boolean executableOn( ds.ca.Cell cell ) {
		// Regel NUR anwendbar, wenn auf der Zelle ein Individuum steht
		// und die Zelle eine Exitcell ist
		
		Individual i = cell.getIndividual();
		//return (i != null) && (cell instanceof ds.ca.ExitCell) && ( i.getStepEndTime() <= caController().getCA().getTimeStep() );
		boolean testval = false;
		if( (i != null) && (cell instanceof ds.ca.ExitCell)) {
			if( i.getStepEndTime() >= caController().getCA().getTimeStep()+1)
				testval = false;
			else
				testval = true;
		}
		return (i != null) && (cell instanceof ds.ca.ExitCell) && testval;
	}
}

