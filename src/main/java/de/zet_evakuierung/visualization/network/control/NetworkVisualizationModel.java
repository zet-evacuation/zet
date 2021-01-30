/*
 * zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.zet_evakuierung.visualization.network.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import gui.visualization.EvacuationVisualizationModel;
import org.zetool.math.Conversion;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class NetworkVisualizationModel extends EvacuationVisualizationModel {

    private static final Logger LOG = Logger.getGlobal();

    private int nodeCount;
    private int nodesDone;
    double speedFactor = 1;
    // Timing variables
    private long time;
    private double realStep;
//	private long timeSinceLastStep = 0;
    private long nanoSecondsPerStep = Conversion.SEC_TO_NANO_SECONDS;
    private double secondsPerStep = 1;
    private long step;
    /**
     * The maximal time step used for the graph
     */
    private int stepCount = 0;
    /**
     * The status of the simulation, true if all is finished
     */
    private boolean finished = false;
    private int superSinkID = 0;

    /**
     * <p>
     * This method increases the number of nodes that are already created and calculates a new progress. The progress
     * will at most reach 99% so that after all objects are created a final "Done" message can be submitted.</p>
     * <p>
     * Note that before this method can be used in the proper way the private variable {@code nodesDone} and
     * {@code nodeCount} should be initialized correct. However, it is guaranteed to calculate a value from 0 to 99.
     */
    public void nodeProgress() {
        nodesDone++;
        int progress = Math.max(0, Math.min((int) Math.round(((double) nodesDone / nodeCount) * 100), 99));
        //AlgorithmTask.getInstance().setProgress( progress, "Erzeuge Graph...", "Knoten " + nodesDone + " von " + nodeCount + " erzeugt." );
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public void init(int nodeCount, int superSinkId) {
        this.superSinkID = superSinkId;
        this.nodeCount = nodeCount;
        nodesDone = 0;

        // initialize timing
        secondsPerStep = Double.POSITIVE_INFINITY;
        nanoSecondsPerStep = Long.MAX_VALUE;
        step = 0;
        realStep = 0.0;

        if (nanoSecondsPerStep == 0) {
            finished = true;
        }
    }

    public int superSinkID() {
        return superSinkID;
    }

    /**
     * Returns the current step of the graph. The step counter is stopped if the graph is finished.
     *
     * @return the current step of the graph
     */
    @Override
    public double getStep() {
        return realStep;
    }

    @Override
    public void addTime(long timeNanoSeconds) {
        time += timeNanoSeconds * speedFactor;
        realStep = ((double) time / (double) nanoSecondsPerStep);
        step = (long) realStep;

        if (step > stepCount) {
            finished = true;
        }
    }

    @Override
    public void setTime(long time) {
        this.time = time;
        realStep = time == 0 ? 0 : ((double) time / (double) nanoSecondsPerStep);
        step = (long) realStep;
        finished = stepCount == 0 || step > stepCount;
    }

    @Override
    public void resetTime() {
        setTime(0);
    }

    /**
     * Sets the maximal time used by the graph
     *
     * @param maxT
     */
    public void setMaxTime(int maxT) {
        this.stepCount = Math.max(stepCount, maxT);
    }

    /**
     * Checks wheather graph visualization is finished, or not.
     *
     * @return true if the simulation is finished, false otherwise
     */
    @Override
    public final boolean isFinished() {
        return finished;
    }

    /**
     * Sets the time needed for one step of the graph in nano seconds. A graph step equals a time unit of the network
     * flow.
     *
     * @param nanoSecondsPerStep the nano seconds needed for one graph step
     * @see #setSecondsPerStep(double)
     */
    public void setNanoSecondsPerStep(long nanoSecondsPerStep) {
        if (nanoSecondsPerStep < 0) {
            throw new IllegalArgumentException("Nano seconds have to be nonnegative!");
        }
        if (nanoSecondsPerStep == 0) {
            finished = true;
        }
        this.nanoSecondsPerStep = nanoSecondsPerStep;
        secondsPerStep = nanoSecondsPerStep * Conversion.NANO_SECONDS_TO_SEC;

        LOG.log(Level.FINE, "Berechnete Graph-Geschwindigkeit: {0}", nanoSecondsPerStep);
    }

    /**
     * Sets the time needed for one step of the graph in seconds. A graph step equals a time unit of the network flow.
     *
     * @param secondsPerStepGraph the seconds needed for one graph step
     * @see #setNanoSecondsPerStep(long)
     */
    public void setSecondsPerStep(double secondsPerStepGraph) {
        this.secondsPerStep = secondsPerStepGraph;
        nanoSecondsPerStep = Math.round(secondsPerStepGraph * Conversion.SEC_TO_NANO_SECONDS);
        if (nanoSecondsPerStep < 0) {
            throw new IllegalArgumentException("Nano seconds have to be nonnegative!");
        }
        if (nanoSecondsPerStep == 0) {
            finished = true;
        }

        LOG.log(Level.FINE, "Berechnete Graph-Geschwindigkeit: {0}", nanoSecondsPerStep);
    }

    public void setSpeedFactor(double speedFactor) {
        this.speedFactor = speedFactor;
    }

    public double getSecondsPerStep() {
        return secondsPerStep;
    }

    public long getNanoSecondsPerStep() {
        return this.nanoSecondsPerStep;
    }

    public long getStepCount() {
        return stepCount;
    }

}
