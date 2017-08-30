package org.zet.cellularautomaton.algorithm;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
final class SlowAlgorithm extends AbstractEvacuationCellularAutomatonWrapper {

    SlowAlgorithm(EvacuationCellularAutomatonAlgorithm eca) {
        super(eca);
    }

    @Override
    protected void perform() {
        while (!wrapped.isFinished()) {
            try { // wait anyway
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
                //setPaused( false );
            }
            while (isPaused()) // wait longer if paused
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                    //setPaused( false );
                }
            }
            wrapped.performStep();
        }
    }
}
