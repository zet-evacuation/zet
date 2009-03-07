/*
 * Algorithm.java
 *
 */
package sandbox;

import java.util.LinkedHashSet;
import java.util.Set;
import tasks.AlgorithmTask;
import util.NanosecondTimeFormatter;

/**
 *
 * @author
 */
public abstract class Algorithm<Problem, Solution> implements Runnable {

    private void fireEvent(AlgorithmStartedEvent algorithmStartedEvent) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private void fireEvent(AlgorithmStoppedEvent algorithmStoppedEvent) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public enum State { 
        WAITING_FOR_PROBLEM,
        READY_TO_SOLVE,
        SOLVING, 
        SOLVING_FAILED, 
        SOLVED; 
    }

    private Problem problem;
    private Set<MessageListener> messageListeners;
    private double progress;
    private Set<ProgressListener> progressListeners;
    private boolean running;
    private long runtime;
    private Solution solution;
    private long startTime;
    private State state;

    public final void addMessageListener(MessageListener listener) {
        if (isProblemSolved()) {
            throw new IllegalStateException("The problem has already been solved. There are no messages that could be listened to anymore.");
        } else {
            if (messageListeners == null) {
                messageListeners = new LinkedHashSet<MessageListener>();
            }
            messageListeners.add(listener);
        }
    }

    public final void removeMessageListener(MessageListener listener) {
        if (messageListeners != null) {
            messageListeners.remove(listener);
        }
    }

    protected final void fireEvent(String message) {
        if (!isRunning()) {
            throw new IllegalStateException("Message Events can only be dispatched while the algorithm is running.");
        }
        if (messageListeners != null) {
            MessageEvent event = new MessageEvent(this, startTime, System.nanoTime(), message);
            for (MessageListener listener : messageListeners) {
                listener.messageChanged(event);
            }
        }
    }

    protected final void fireEvent(String formatStr, Object... params) {
        fireEvent(String.format(formatStr, params));
    }

    public final void addProgressListener(ProgressListener listener) {
        if (isProblemSolved()) {
            throw new IllegalStateException("The problem has already been solved. There is no progress that could be listened to anymore.");
        } else {
            if (progressListeners == null) {
                progressListeners = new LinkedHashSet<ProgressListener>();
            }
            progressListeners.add(listener);
        }
    }

    public final void removeProgressListener(ProgressListener listener) {
        if (progressListeners != null) {
            progressListeners.remove(listener);
        }
    }

    protected final void fireProgressEvent(double progress) {
        //if (!util.ProgressBooleanFlags.ALGO_PROGRESS) {
        //    return;
        //}
        if (!isRunning()) {
            throw new IllegalStateException("Progress Events can only be dispatched while the algorithm is running.");
        } else if (progress < 0) {
            throw new IllegalArgumentException("The progress value must not be < 0.");
        } else if (progress > 100) {
            throw new IllegalArgumentException("The progress values must not not be > 100.");
        } else if (progress < this.progress) {
            throw new IllegalArgumentException("The progress values must be monotonically increasing.");
        }
        this.progress = progress;
				if( util.ProgressBooleanFlags.ALGO_PROGRESS )
					System.out.println( "Progress: " + progress );
				AlgorithmTask.getInstance().publish( (int)Math.round( progress*100 ) );
        if (progressListeners != null) {
            AlgorithmProgressEvent event = new AlgorithmProgressEvent(this, startTime, System.nanoTime(), progress);
            for (ProgressListener listener : progressListeners) {
                listener.progressChanged(event);
            }
        }
    }

    public final Problem getProblem() {
        return problem;
    }

    public final void setProblem(Problem problem) {
        this.problem = problem;
    }

    public final long getRuntime() {
        if (isProblemSolved()) {
            return runtime;
        } else {
            throw new IllegalStateException("The problem has not been solved yet. Please call run() first.");
        }
    }

    public final String getRuntimeAsString() {
        if (isProblemSolved()) {
            return NanosecondTimeFormatter.formatTime(runtime);
        } else {
            throw new IllegalStateException("The problem has not been solved yet. Please call run() first.");
        }
    }    
    
    public final Solution getSolution() {
        if (isProblemSolved()) {
            return solution;
        } else {
            throw new IllegalStateException("The problem has not been solved yet. Please call run() first.");
        }
    }
    
    public final long getStartTime() {
        if (isRunning() || isProblemSolved()) {
            return startTime;
        } else {
            throw new IllegalStateException("The problem has neither been solved nor is it currently been solved. Please call run() first.");
        }
    }

    public final boolean isProblemInitialized() {
        return problem != null;
    }

    public final boolean isProblemSolved() {
        return solution != null;
    }

    public final boolean isRunning() {
        return running;
    }

    public final void run() {
        if (!isProblemInitialized()) {
            throw new IllegalStateException("No problem has been specified yet. Please call setProblem() first.");
        } else {
            try {
                running = true;
                startTime = System.nanoTime();
                state = State.SOLVING;
                fireEvent(new AlgorithmStartedEvent(this));
                solution = runAlgorithm(problem);                
                state = State.SOLVED;                
                runtime = System.nanoTime() - startTime;
                //fireEvent(new AlgorithmStoppedEvent(this));
            } catch (RuntimeException ex) {
                state = State.SOLVING_FAILED;
                throw ex;
            } finally {
                running = false;
            }
        }
    }

    protected abstract Solution runAlgorithm(Problem problem);
}
