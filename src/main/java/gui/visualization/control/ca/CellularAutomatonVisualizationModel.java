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
package gui.visualization.control.ca;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gui.visualization.EvacuationVisualizationModel;
import gui.visualization.draw.ca.GLIndividual;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.InitialConfiguration;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationSpeed;
import org.zetool.math.Conversion;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonVisualizationModel extends EvacuationVisualizationModel {

    private int cellCount;
    private int cellsDone;

    private double realStep;
    private double secondsPerStep;
    private long nanoSecondsPerStep;
    private long step;
    private long time;
    /**
     * The status of the simulation, true if all is finished.
     */
    private boolean finished = false;
    private int maxStep;

    double speedFactor = 1;

    private ArrayList<GLIndividual> glIndividuals = new ArrayList<>();
    private ArrayList<GLIndividualControl> individuals = new ArrayList<>();
    private InitialConfiguration initialConfiguration;

    void init(int cellCount) {
        this.cellCount = cellCount;
        cellsDone = 0;

        // initialize timing
        secondsPerStep = Double.POSITIVE_INFINITY;
        nanoSecondsPerStep = Long.MAX_VALUE;
        step = 0;
        realStep = 0.0;
    }

    /**
     * <p>
     * This method increases the number of cells that are created and calculates a new progress. The progress will at
     * most reach 99% so that after all objects are created a final "Done" message can be submitted.</p>
     * <p>
     * Note that before this method can be used in the proper way the private variable {@code cellsDone} and
     * {@code cellCount} should be initialized correct. However, it is guaranteed to calculate a value from 0 to 99.
     */
    public void cellProgress() {
        cellsDone++;
        int progress = Math.max(0, Math.min((int) Math.round(((double) cellsDone / cellCount) * 100), 99));
        //AlgorithmTask.getInstance().setProgress( progress, "Erzeuge Zellen...", "Zelle " + cellsDone + " von " + cellCount + " erzeugt." );
    }

    @Override
    public void addTime(long timeNanoSeconds) {
        time += timeNanoSeconds * speedFactor;
        realStep = ((double) time / (double) nanoSecondsPerStep);
        step = (long) realStep;

        finished = step > maxStep;
    }

    @Override
    public void setTime(long timeNanoSeconds) {
        time = timeNanoSeconds;
        realStep = ((double) time / (double) nanoSecondsPerStep);
        step = (long) realStep;

        finished = step > maxStep;
    }

    @Override
    public void resetTime() {
        setTime(0);
    }

    public long getStepCount() {
        return maxStep;
    }

    /**
     * Sets the maximal time step for the cellular automaton.
     *
     * @param maxStep the last step
     */
    public void setMaxTime(int maxStep) {
        this.maxStep = maxStep;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    /**
     * Sets a factor that is multiplicated with the visualization speed. Use {@code 1.0} for normal (real-time) speed.
     *
     * @param speedFactor the speed factor
     */
    public void setSpeedFactor(double speedFactor) {
        //secondsPerStep = ca.getSecondsPerStep();
        //nanoSecondsPerStep = (long) (Math.round( secondsPerStep * Conversion.secToNanoSeconds ) / speedFactor);
        this.speedFactor = speedFactor;
    }

    public long getNanoSecondsPerStep() {
        return nanoSecondsPerStep;
    }

    @Override
    public double getStep() {
        return realStep;
    }

    public double getSecondsPerStep() {
        return secondsPerStep;
    }

    public double getTimeInSeconds() {
        return time * Conversion.NANO_SECONDS_TO_SEC;
    }

    public final List<GLIndividualControl> getIndividualControls() {
        return Collections.unmodifiableList(individuals);
    }

    public final GLIndividual getControlledGLIndividual(int number) {
        return glIndividuals.get(number - 1);
    }

    public List<GLIndividual> getIndividuals() {
        return Collections.unmodifiableList(glIndividuals);
    }

    public int floorFor(Individual i) {
        EvacCellInterface cell = cellFor(i);
        return cell.getRoom().getFloor();
    }

    public EvacCellInterface cellFor(Individual i) {
        return initialConfiguration.getIndividualStartPositions().get(i);
    }

    void initTiming(EvacuationSimulationSpeed esp, int maxStep) {
        secondsPerStep = esp.getSecondsPerStep();
        nanoSecondsPerStep = Math.round(secondsPerStep * Conversion.SEC_TO_NANO_SECONDS);
        step = 0;
        this.maxStep = maxStep;
    }

    void setIndividuals(ArrayList<GLIndividualControl> individuals, ArrayList<GLIndividual> glIndividuals) {
        if (individuals.size() != glIndividuals.size()) {
            throw new IllegalArgumentException("Individual lists must be of same size");
        }
        this.individuals = individuals;
        this.glIndividuals = glIndividuals;
    }

    void setInitialConfig(InitialConfiguration initialConfiguration) {
        this.initialConfiguration = initialConfiguration;
    }

}
