/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * Algorithm.java
 *
 */
package de.tu_berlin.math.coga.common.algorithm;

import algo.graph.util.MillisecondTimeFormatter;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The basic framework class for algorithms. It allows to define input and
 * output of an algorithm by using generics and provides the framework to run
 * an algorithm in its own thread by implementing <code>Runnable</code>.
 * Furthermore, it keeps track of the current state of the algorithm and
 * provides an generic exception handling that can be extended by overwriting
 * <code>handleException</code>. It also keeps track of the algorithms runtime
 * and offers simple logging features that should be preferred to System.out
 * logging. Finally it offers the possibility to dispatch information about the
 * algorithm's state and progress to listeners, that can register and unregister
 * themselves for the algorithms events.
 * 
 * @author Martin Groß
 */
public abstract class Algorithm<Problem, Solution> implements Runnable {

    /**
     * An enumeration type that specifies the current state of the algorithm.
     */
    public enum State {

        WAITING,
        SOLVING,
        SOLVING_FAILED,
        SOLVED;
    }
    /**
     * The set of listeners that recieves events from this algorithm.
     */
    private Set<AlgorithmListener> algorithmListeners;
    /**
     * Whether messages are logged by the log-methods.
     */
    private boolean logging;
    /**
     * Whether events are also logged to the console.
     */
    private boolean loggingEvents;
    /**
     * The instance of the problem.
     */
    private Problem problem;
    /**
     * The current progress of the algorithm. The progress begins with 0.0 and
     * ends with 1.0.
     */
    private double progress;
    /**
     * The runtime of the algorithm in milliseconds.
     */
    private long runtime;
    /**
     * The solution to the problem instance, once available.
     */
    private Solution solution;
    /**
     * The point of time at which the execution of the algorithm started.
     */
    private long startTime;
    /**
     * The state of execution of the algorithm.
     */
    private State state;

    /**
     * Adds the specified listener to the set of listeners recieving events from
     * this algorithm. If the specified listener is already part of this list,
     * nothing happens.
     * @param listener the listener to be added to the notification list.
     * @throws IllegalArgumentException if the algorithm has already terminated.
     */
    public final void addAlgorithmListener(AlgorithmListener listener) {
        if (isProblemSolved()) {
            throw new IllegalStateException("The problem has already been solved. There will be no more events that could be listened to anymore.");
        } else {
            if (algorithmListeners == null) {
                algorithmListeners = new LinkedHashSet<AlgorithmListener>();
            }
            algorithmListeners.add(listener);
        }
    }

    /**
     * Removes the specified listener from the set of listeners recieving events
     * from this algorithm.
     * @param listener the listener to be removed from the notification list.
     */
    public final void removeAlgorithmListener(AlgorithmListener listener) {
        if (algorithmListeners != null) {
            algorithmListeners.remove(listener);
        }
    }

    /**
     * Dispatches the specified event to all registered listeners.
     * @param event the event to be dispatched to the listeners.
     */
    protected final void fireEvent(AlgorithmEvent event) {
        if (algorithmListeners != null) {
            for (AlgorithmListener listener : algorithmListeners) {
                listener.eventOccurred(event);
            }
        }
    }

    /**
     * Dispatches an algorithm progress event with the specified message and
     * current progress value to all listeners.
     * @param message the message to be dispatched.
     */
    protected final void fireEvent(String message) {
        fireEvent(new AlgorithmDetailedProgressEvent(this, progress, message));
    }

    /**
     * Dispatches an algorithm progress event with the specified message and
     * current progress value to all listeners. The method is a shortcut for
     * fireEvent(String.format(formatStr, params)).
     * @param formatStr the format string part of the message to be dispatched.
     * @param params the parameters used by the format string.
     */
    protected final void fireEvent(String formatStr, Object... params) {
        fireEvent(String.format(formatStr, params));
    }

    /**
     * Updates the progress value to broadcasts the new value to all listeners.
     * @param progress the new progress value.
     * @throws IllegalArgumentException if the progress value is less than the
     * previous one.
     */
    protected final void fireProgressEvent(double progress) {
        if (progress < this.progress) {
            throw new IllegalArgumentException("The progress values must be monotonically increasing.");
        }
        this.progress = progress;
        fireEvent(new AlgorithmProgressEvent(this, progress));
    }

    /**
     * Updates the progress value to broadcasts the new value together with a
     * message to all listeners.
     * @param progress the current progress value.
     * @param message a message describing the current task and progress of the
     * algorithm.
     * @throws IllegalArgumentException if the progress value is less than the
     * previous one.
     */
    protected final void fireProgressEvent(double progress, String message) {
        if (progress < this.progress) {
            throw new IllegalArgumentException("The progress values must be monotonically increasing.");
        }
        this.progress = progress;
        fireEvent(new AlgorithmDetailedProgressEvent(this, progress, message));
    }

    /**
     * Returns the instance of the problem that is to be solved.
     * @return the instance of the problem that is to be solved.
     */
    public final Problem getProblem() {
        return problem;
    }

    /**
     * Specifies the instance of the problem this algorithm is going to solve.
     * @param problem the instance of the problem that is to be solved.
     */
    public final void setProblem(Problem problem) {
        if (state == State.SOLVING) {
            throw new IllegalStateException("The algorithm is currently running! Changing the underlying instance could lead to undefined behaviour!");
        }
        if (this.problem != problem) {
            this.problem = problem;
            runtime = 0;
            solution = null;
            startTime = 0;
            state = State.WAITING;
        }
    }

    /**
     * Returns the time between the start of the algorithm and its termination
     * in milliseconds.
     * @return the runtime of the algorithm in milliseconds.
     * @throws IllegalStateException if the algorithm has not terminated yet.
     */
    public final long getRuntime() {
        if (state == State.SOLVED || state == State.SOLVING_FAILED) {
            return runtime;
        } else {
            throw new IllegalStateException("The algorithm has not terminated yet. Please call run() first and wait for its termination.");
        }
    }

    /**
     * Returns the runtime of the algorithm as a string formatted with regard to
     * human readability. The formatting is done according to
     * <code>MillisecondTimeFormatter</code>.
     * @return the runtime of the algorithm formatted as a string.
     * @throws IllegalStateException if the algorithm has not terminated yet.
     */
    public final String getRuntimeAsString() {
        if (state == State.SOLVED || state == State.SOLVING_FAILED) {
            return MillisecondTimeFormatter.formatTime(runtime);
        } else {
            throw new IllegalStateException("The algorithm has not terminated yet. Please call run() first and wait for its termination.");
        }
    }

    /**
     * Returns the solution computed by the algorithm.
     * @return the solution to the algorithm.
     * @throws IllegalStateException if the problem has not been solved yet.
     */
    public final Solution getSolution() {
        if (isProblemSolved()) {
            return solution;
        } else {
            throw new IllegalStateException("The problem has not been solved yet. Please call run() first and wait for its termination.");
        }
    }

    /**
     * Returns the start time of the algorithm. The start time is measured in
     * the number of milliseconds elapsed since midnight, January 1, 1970 UTC.
     * @return the start time of the algorithm.
     * @throws IllegalStateException if the execution of the algorithm has not
     * yet begun.
     */
    public final long getStartTime() {
        if (state != State.WAITING) {
            return startTime;
        } else {
            throw new IllegalStateException("The execution of the algorithm has not started yet. Please call run() first.");
        }
    }

    /**
     * Returns the current state of the algorithm.
     * @return the current state of the algorithm.
     */
    public final State getState() {
        return state;
    }

    /**
     * Returns whether log messages of this algorithm are written to System.out 
     * or not.
     * @return <code>true</code>, if log messages are written to System.out, 
     * <code>false</code> if otherwise.
     */
    public final boolean isLogging() {
        return logging;
    }

    /**
     * Sets whether messages logged via the log() methods are written to
     * System.out or not.
     * @param value whether messages logged via the log() methods are written to
     * System.out or not.
     */
    public final void setLogging(boolean logging) {
        this.logging = logging;
    }

    /**
     * Returns whether events are also treated as log messages or not.
     * @return <code>true</code>, if events are also treated as log messages,
     * <code>false</code> if otherwise.
     */
    public final boolean isLoggingEvents() {
        return loggingEvents;
    }

    /**
     * Sets whether events are also treated as log messages.
     * @param loggingEvents whether events are also treated as log messages.
     */
    public final void setLoggingEvents(boolean loggingEvents) {
        if (this.loggingEvents != loggingEvents) {
            this.loggingEvents = loggingEvents;
            if (loggingEvents) {
                addAlgorithmListener(new EventLogger());
            } else {
                List<AlgorithmListener> logger = new LinkedList<AlgorithmListener>();
                for (AlgorithmListener listener : algorithmListeners) {
                    if (listener instanceof Algorithm.EventLogger) {
                        logger.add(listener);
                    }
                }
                algorithmListeners.removeAll(logger);
            }
        }
    }

    /**
     * Returns whether a problem instance has been specified for the algorithm.
     * This is the prerequisite for beginning the execution of the algorithm.
     * @return <code>true</code> if a problem instance has been specified,
     * <code>false</code> otherwise.
     */
    public final boolean isProblemInitialized() {
        return problem != null;
    }

    /**
     * Returns whether this algorithm has successfully run and solved the
     * instance of the problem given to it. If this is <code>true</code>, then
     * the solution to the instance of the problem can be obtained by <code>
     * getSolution</code>.
     * @return <code>true</code> if the algorithm's instance of the problem has
     * been solved successfully and <code>false</code> otherwise.
     */
    public final boolean isProblemSolved() {
        return state == State.SOLVED;
    }

    /**
     * Returns whether the algorithm is currently begin executed.
     * @return <code>true</code> if this algorithm is currently running and
     * <code>false</code> otherwise.
     */
    public final boolean isRunning() {
        return state == State.SOLVING;
    }

    /**
     * Writes the specified message to System.out, if logging is enabled. Does
     * nothing otherwise.
     * @param message the message that it to be logged.
     */
    protected final void log(String message) {
        if (logging) {
            System.out.println(message);
        }
    }

    /**
     * Formats the specified message and params using String.format() and logs
     * it.
     * @param message the format string of the message.
     * @param params the parameters for formatting the message.
     */
    protected final void log(String message, Object... params) {
        log(String.format(message, params));
    }

    /**
     * The framework method for executing the algorithm. It is responsible for
     * recording the runtime of the actual algorithm in addition to handling
     * exceptions and recording the solution to the problem instance.
     * @throws IllegalStateException if the instance of the problem has not been
     * specified yet.
     */
    public final void run() {
        if (!isProblemInitialized()) {
            throw new IllegalStateException("The instance of the problem has been specified yet. Please call setProblem() first.");
        } else {
            try {
                startTime = System.currentTimeMillis();
                state = State.SOLVING;
                fireEvent(new AlgorithmStartedEvent(this));
                solution = runAlgorithm(problem);
                state = State.SOLVED;
            } catch (RuntimeException ex) {
                state = State.SOLVING_FAILED;
                handleException(ex);
            } finally {
                runtime = System.currentTimeMillis() - startTime;
                fireEvent(new AlgorithmTerminatedEvent(this));
            }
        }
    }

    /**
     * The default exception handling method. It records that the algorithm 
     * failed to solve the instance and rethrows the runtime exception that 
     * caused the premature termination of the algorithm. Subclasses can
     * override this method to change this behaviour.
     * @param the exception that caused the termination of the algorithm.
     */
    protected void handleException(RuntimeException exception) {
        throw exception;
    }

    /**
     * The abstract method that needs to be implemented by sub-classes in order
     * to implement the actual algorithm.
     * @param problem an instance of the problem.
     * @return a solution to the specified problem.
     */
    protected abstract Solution runAlgorithm(Problem problem);

    /**
     * A private listener class for recieving events and logging them.
     */
    private class EventLogger implements AlgorithmListener {

        /**
         * This method is called when an event occurred in an algorithm that is
         * being listened to.
         * @param event the event which occurred.
         */
        public void eventOccurred(AlgorithmEvent event) {
            String message = "";
            if (event instanceof AlgorithmStartedEvent) {
                message = String.format("%1$s: %2$s gestartet...", event.getFormattedEventTime(), Algorithm.this.getClass().getSimpleName());
            } else if (event instanceof AlgorithmDetailedProgressEvent) {
                message = String.format("%1$s: %2$s running... %3$s%", event.getFormattedEventTime(), Algorithm.this.getClass().getSimpleName(), ((AlgorithmProgressEvent) event).getProgressAsInteger());
            } else if (event instanceof AlgorithmProgressEvent) {
                message = String.format("%1$s: %2$s running... %3$s%", event.getFormattedEventTime(), Algorithm.this.getClass().getSimpleName(), ((AlgorithmProgressEvent) event).getProgressAsInteger());
            } else if (event instanceof AlgorithmTerminatedEvent) {
                message = String.format("%1$s: %2$s beendet nach %3$s.", event.getFormattedEventTime(), Algorithm.this.getClass().getSimpleName(), getRuntimeAsString());
            }
            if (!message.equals("")) {
                log(message);
            }
        }
    }
}
