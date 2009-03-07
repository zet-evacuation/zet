package algo.ca.rule;

import ds.ca.Cell;

public class SaveIndividualsRule extends AbstractSaveRule {
	// muss VOR der EvacuateIndividualsRule aufgerufen werden!
	public SaveIndividualsRule() {
	}

	@Override
	protected void onExecute( ds.ca.Cell cell ) {
		ds.ca.Individual savedIndividual = cell.getIndividual();
		if( !(savedIndividual.isSafe()) ) {
			//caController().getCA().decreaseNrOfLivingAndNotSafeIndividuals();
			//savedIndividual.setSafe();
			caController().getCA().setIndividualSave( savedIndividual );
			savedIndividual.setPanic( 0 );
			//savedIndividual.setSafetyTime(caController().getCA().getTimeStep());

			if( cell instanceof ds.ca.SaveCell ) {
				ds.ca.StaticPotential correspondingExitPotential = ((ds.ca.SaveCell) cell).getExitPotential();
				if( correspondingExitPotential == null ) {
					savedIndividual.setStaticPotential( caController().getCA().getPotentialManager().getSafePotential() );
				//throw new IllegalStateException("An individual is in a save area with no exit");
				} else {
					if( savedIndividual.getStaticPotential() != correspondingExitPotential ) {
						caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addChangedPotentialToStatistic( savedIndividual, caController().getCA().getTimeStep() );
						savedIndividual.setStaticPotential( correspondingExitPotential );
					}
					caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addExitToStatistic( savedIndividual, correspondingExitPotential );
				}
			}

			caController().getCaStatisticWriter().getStoredCAStatisticResults().getStoredCAStatisticResultsForIndividuals().addSafeIndividualToStatistic( savedIndividual );

		}
	// else: nothing!
	}

	@Override
	public boolean executableOn( ds.ca.Cell cell ) {
		// Regel NUR anwendbar, wenn auf der Zelle ein Individuum steht
		// und die Zelle eine Exit- Savecell oder  ist
		return (cell.getIndividual() != null) && ((cell instanceof ds.ca.ExitCell) || (cell instanceof ds.ca.SaveCell));
	}
}

