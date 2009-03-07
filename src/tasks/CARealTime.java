/*
 * CARealTime.java
 * Created on 28.01.2008, 03:16:13
 */

package tasks;

import algo.ca.CellularAutomatonInOrderExecution;
import ds.ca.CellularAutomaton;
import ds.ca.CellularAutomaton.State;
import ds.ca.Individual;
import util.Helper;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CARealTime extends CellularAutomatonInOrderExecution {

	private int stepTime;

	public CARealTime( CellularAutomaton ca ) {
		super( ca );
		stepTime = 100;
	}

	@Override
	public void run() {
		if( isCancelled() ) {
			return;
		}
		if( isStepByStep() ) {
			super.run();
			AlgorithmTask.getInstance().publish( 0, ca.evacuatedIndividualsCount() + " Individuen evakuiert", "Führe " + (ca.getTimeStep() + 1) + ". Schritt durch..." );
			return;
		} else {
			// Realtime simulation
			long start = System.currentTimeMillis();
			long end;
			if( !isInitialized() | isFinished() ) {
				initialize();
				executeInitialization();
				end = System.currentTimeMillis();
			} else
				end = start;
			int individuals = ca.individualCount();
			if( ca.individualCount() > 0 ) {
				AlgorithmTask.getInstance().publish( 0, ca.evacuatedIndividualsCount() + " Individuen sicher", "Führe " + (ca.getTimeStep() + 1) + ". Schritt durch..." );
			}
			long wait = stepTime - (end - start);
			if( wait > 0 ) {
				Helper.pause( wait );
			}
			// Execute loop rules
			while( ca.getNotSafeIndividualsCount() > 0 && ca.getTimeStep() < getMaxTimeInSteps() && !isCancelled() ) {
				if( isPaused() ) {
					try {
						Thread.sleep( 500 );
					} catch( InterruptedException ignore ) { }
					continue;
				}
				start = System.currentTimeMillis();
				executeStep();
				end = System.currentTimeMillis();
				AlgorithmTask.getInstance().publish( 100 - (int) ((ca.individualCount() / (float) individuals) * 100), ca.getInitialIndividualCount() - ca.getNotSafeIndividualsCount() - ca.deadIndividualsCount() + " Individuen sicher, " + ca.deadIndividualsCount() + " Individuen nicht sicher", "Führe " + ca.getTimeStep() + ". Schritt durch..." );
				wait = stepTime - (end - start);
				if( wait > 0 ) {
					try {
						Thread.sleep( wait );
					} catch( InterruptedException ignore ) { }
				}
			}
			// let die all individuals which are not already dead and not safe
			if( ca.getNotSafeIndividualsCount() != 0 ) {
				Individual[] individualsCopy = ca.getIndividuals().toArray( new Individual[ca.getIndividuals().size()] );
				for( Individual i : individualsCopy ) {
					if( !i.getCell().getIndividual().isSafe() ) {
						ca.setIndividualDead( i, Individual.DeathCause.NOT_ENOUGH_TIME );
						//ca.decreaseNrOfLivingAndNotSafeIndividuals();
					}
				}
			}
			setFinished( true );
			if( !isCancelled() )
				AlgorithmTask.getInstance().publish( 100, "Ende: " + (ca.getInitialIndividualCount() - ca.getNotSafeIndividualsCount() - ca.deadIndividualsCount()) + " Individuen sicher, " + ca.deadIndividualsCount() + " Individuen nicht sicher", ca.getTimeStep() + " Schritte durchgeführt" );
			ca.setState( State.finish );
		}
	}

	public int getStepTime() {
		return stepTime;
	}
	
	public void setStepTime( int stepTime ) {
		this.stepTime = stepTime;
	}
}
