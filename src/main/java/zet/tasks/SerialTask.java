/**
 * SerialTask.java
 * Created: Jul 28, 2010,5:45:06 PM
 */
package zet.tasks;

import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.common.algorithm.AbstractAlgorithmEvent;
import org.zetool.common.algorithm.AlgorithmListener;
import org.zetool.common.algorithm.AlgorithmStartedEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.zetool.common.algorithm.Algorithm;
import org.zetool.common.algorithm.AlgorithmTerminatedEvent;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SerialTask extends SwingWorker<Void, AbstractAlgorithmEvent> implements AlgorithmListener {

    private static Logger log = Logger.getGlobal();

    ArrayList<AbstractAlgorithm<?, ?>> algorithms;

    public SerialTask() {
        algorithms = new ArrayList<>();
    }
    private RuntimeException error = null;

    /**
     *
     * @return
     */
    public RuntimeException getError() {
        return error;
    }

    public boolean isError() {
        return error != null;
    }

    public SerialTask(AbstractAlgorithm<?, ?> algorithm) {
        algorithms = new ArrayList<>();
        algorithms.add(algorithm);
    }

    public void add(AbstractAlgorithm<?, ?> algorithm) {
        algorithms.add(algorithm);
    }

    @Override
    protected Void doInBackground() throws Exception {
        log.info("Starting serial task " + this.toString() + " execution.");
        try {
            for (AbstractAlgorithm<?, ?> algorithm : algorithms) {
                log.info("[" + this.toString() + "] " + " start " + algorithm.toString());
                algorithm.addAlgorithmListener(this);
                algorithm.run();
            }
        } catch (RuntimeException ex) {
            log.info("[" + this.toString() + "] " + " error: " + ex.toString());
            this.error = ex;
        }
        log.info("[" + this.toString() + "] " + " completed.");

        return null;
    }

    public void test() throws Exception {
        doInBackground();
    }

    /**
     * Takes events thrown by the algorithms and forwards it to the swing workers publish method. Thus, a listener to
     * the swing worker can also get progress.
     *
     * @param event
     */
    @Override
    public void eventOccurred(AbstractAlgorithmEvent event) {
        publish(event);
    }

    @Override
    protected void process(List<AbstractAlgorithmEvent> chunks) {
        for (AbstractAlgorithmEvent event : chunks) {
            if (event instanceof AlgorithmStartedEvent) {
                Logger.getGlobal().log(Level.FINE, "Gestartet: {0}", ((AlgorithmStartedEvent) event).getAlgorithm());
            } else if (event instanceof AlgorithmTerminatedEvent) {
                Logger.getGlobal().log(Level.FINE, "Gestoppt: {0}", ((AlgorithmTerminatedEvent) event).getAlgorithm());
            }
        }
    }

    public static class Lubricant<T, U> extends AbstractAlgorithm<Algorithm<?, T>, Void> {

        private final Algorithm<?, T> producer;
        private final Algorithm<U, ?> taker;
        private final Function<T, U> converter;

        public Lubricant(Algorithm<?, T> producer, Algorithm<U, ?> taker, Function<T, U> converter) {
            this.producer = producer;
            this.taker = taker;
            this.converter = converter;
            setProblem(producer);
        }

        @Override
        protected Void runAlgorithm(Algorithm<?, T> problem) {
            log.info("Transferring solution " + problem.getSolution() + " to " + taker);
            taker.setProblem(converter.apply(producer.getSolution()));
            return null;
        }
    };
    
    public static class SimpleLubricant<T> extends Lubricant<T, T> {
    
        public SimpleLubricant(Algorithm<?, T> producer, Algorithm<T, ?> taker) {
            super(producer, taker, x -> x);
        }
    
    }
}
