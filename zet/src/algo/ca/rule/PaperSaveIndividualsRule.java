package algo.ca.rule;

import ds.ca.Cell;

/* Veränderungen zur normalen SaveIndividualsRule:
- Panik wird nicht auf 0 gesetzt
- kein neues Potential, wenn man eine SaveArea betritt!!!!!
*/
public class PaperSaveIndividualsRule extends AbstractSaveRule {
	// muss VOR der EvacuateIndividualsRule aufgerufen werden!
	public PaperSaveIndividualsRule() {
	}

	@Override
	protected void onExecute( ds.ca.Cell cell ) {
		ds.ca.Individual savedIndividual = cell.getIndividual();
		if( !(savedIndividual.isSafe()) ) {
			caController().getCA().setIndividualSave( savedIndividual );
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

