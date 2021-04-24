/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
package de.zet_evakuierung.visualization.ca.model;

import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

import javax.media.opengl.GL2;

import de.zet_evakuierung.visualization.ca.CellularAutomatonVisualizationProperties;
import de.zet_evakuierung.visualization.ca.draw.GLCA;
import de.zet_evakuierung.visualization.ca.draw.GLCell;
import de.zet_evakuierung.visualization.ca.draw.GLCellularAutomatonViews;
import de.zet_evakuierung.visualization.ca.draw.GLIndividual;
import de.zet_evakuierung.visualization.ca.model.DynamicCellularAutomatonInformation.CellInformationDisplay;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.util.Tuple;
import io.visualization.EvacuationSimulationResults;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.results.DieAction;
import org.zet.cellularautomaton.results.EvacuationRecording;
import org.zet.cellularautomaton.results.MoveAction;
import org.zet.cellularautomaton.results.SwapAction;
import org.zetool.opengl.framework.abs.HierarchyNode;

/**
 * @author Jan-Philipp Kappmeier
 */
public class GLCellularAutomatonControl extends AbstractZETVisualizationControl<GLFloorModel, GLCA, CellularAutomatonVisualizationModel> implements HierarchyNode<GLFloorModel> {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getGlobal();

    /**
     * Access to properties of the visualization run.
     */
    private final CellularAutomatonVisualizationProperties properties;
    /**
     * Gives access to the model objects used by visualization views.
     */
    private final CellularAutomatonVisualizationModelContainer cellularAutomatonModel;
    /**
     * Gives access to all view objects drawing the OpenGL scene.
     */
    private final GLCellularAutomatonViews views;

    // Dynamic CA visualization
    private int recordingCount = 0;
    private int recordingDone = 0;
    boolean containsRecording = false;
    double scaling = 1;

    private EvacuationSimulationResults evacuationResults;

    public GLCellularAutomatonControl(CellularAutomatonVisualizationModel visualizationModel,
            CellularAutomatonVisualizationProperties properties,
            CellularAutomatonVisualizationModelContainer cellularAutomatonModel, GLCellularAutomatonViews views) {
        super(visualizationModel);
        this.properties = properties;
        this.cellularAutomatonModel = cellularAutomatonModel;
        this.views = views;
        setView(views.getView());
    }

    public void setEvacuationSimulationResults(EvacuationSimulationResults esr) {
        this.evacuationResults = esr;
        containsRecording = true;
        recordingCount = esr.getRecording().length();

        // Set up timing:
        visualizationModel.initTiming(evacuationResults.getEsp(), recordingCount);

        visualizationModel.setInitialConfig(esr.getRecording().getInitialConfig());

        // Load recording
        convertIndividualMovements();
        evacuationResults.getRecording().rewind();
        for (GLFloorModel floor : this) {
            views.getView(floor).setIndividuals(visualizationModel.getIndividuals());
        }
    }

    public void showOnlyFloor(Integer floorId) {
        childControls.clear();
        childControls.add(cellularAutomatonModel.getFloorModel(floorId)); // add the floor if possible, otherwise the first
        view.clear();
        for (GLFloorModel floor : this) {
            view.addChild(views.getView(floor));
        }
    }

    public void showAllFloors() {
        childControls.clear();
        cellularAutomatonModel.floors().forEach(childControls::add);
        view.clear();
        for (GLFloorModel floor : this) {
            view.addChild(views.getView(floor));
        }
    }

    @Override
    public void draw(GL2 gl) {
        getView().draw(gl);
    }

    @Override
    public void add(GLFloorModel childControl) {
        throw new IllegalStateException("Adding not supported any more");
    }

    @Override
    public void clear() {
        childControls.clear();
    }

    public void setPotentialDisplay(CellInformationDisplay potentialDisplay) {
        cellularAutomatonModel.cells().forEach(cell -> cell.setPotentialDisplay(potentialDisplay));
    }

    private void convertIndividualMovements() {
        LOG.info("Converting indivudal movements");
        ArrayList<GLIndividual> glIndividuals = new ArrayList<>();

        EvacuationRecording recording = evacuationResults.getRecording();
        EvacuationCellularAutomaton ca = recording.getInitialConfig().getCellularAutomaton();
        ArrayList<GLIndividualModel> individuals = new ArrayList<>(evacuationResults.getEs().getInitialIndividualCount());
        for (int k = 0; k < evacuationResults.getEs().getInitialIndividualCount(); k++) {
            individuals.add(null);
        }

        for (Individual individual : evacuationResults.getEs()) {
            GLIndividualModel control = new GLIndividualModel(individual, properties, visualizationModel, this::computeAbsoluteCellPosition);
            individuals.set(individual.getNumber(), control);
        }

        recording.rewind();

        while (recording.hasNext()) {
            recording.nextActions();
            Vector<MoveAction> movements = recording.filterActions(MoveAction.class);
            for (MoveAction movement : movements) {
                GLCellModel fromCell = cellularAutomatonModel.getCellModel(movement.from());
                GLCellModel endCell = cellularAutomatonModel.getCellModel(movement.to());
                double arrivalTime = movement.arrivalTime();
                double startTime = movement.startTime();
                individuals.get(movement.getIndividualNumber()).addHistoryTriple(fromCell, endCell, startTime, arrivalTime);
            }
            Vector<SwapAction> swaps = recording.filterActions(SwapAction.class);
            for (SwapAction swap : swaps) {
                GLCellModel cell1 = cellularAutomatonModel.getCellModel(swap.cell1());
                GLCellModel cell2 = cellularAutomatonModel.getCellModel(swap.cell2());
                double arrivalTime1 = swap.arrivalTime1();
                double startTime1 = swap.startTime1();
                double arrivalTime2 = swap.arrivalTime2();
                double startTime2 = swap.startTime2();
                individuals.get(swap.getIndividualNumber1()).addHistoryTriple(cell1, cell2, startTime1, arrivalTime1);
                individuals.get(swap.getIndividualNumber1()).addHistoryTriple(cell2, cell1, startTime2, arrivalTime2);
            }
            Vector<DieAction> deaths = recording.filterActions(DieAction.class);
            // Individuen, die schon von anfang an tot sind:
            for (DieAction death : deaths) {
                GLCellModel cell = cellularAutomatonModel.getCellModel(death.placeOfDeath());
                individuals.get(death.getIndividualNumber()).addHistoryTriple(cell, cell, 0, 0);
            }
            this.recordingProgress();
        }
        recording.rewind();
        for (int k = 0; k < evacuationResults.getEs().getInitialIndividualCount(); k++) {
            glIndividuals.add(individuals.get(k).getView());
        }

        visualizationModel.setIndividuals(individuals, glIndividuals);
    }

    /**
     * Returns the absolute position of the cell in the graphics world.
     *
     * @param cellVisualizationModel the cell for which the absolute position is computed
     * @return the absolute position. of the cell in the graphics world
     */
    private Tuple computeAbsoluteCellPosition(GLCellModel cellVisualizationModel) {
        double cellX = cellVisualizationModel.getXPosition();
        double cellY = cellVisualizationModel.getYPosition();
        EvacCellInterface cell = cellVisualizationModel.getBackingCell();
        double roomX = cellularAutomatonModel.getRoomModel(cell.getRoom()).getXPosition();
        double roomY = cellularAutomatonModel.getRoomModel(cell.getRoom()).getYPosition();
        int floorId = cell.getRoom().getFloor();
        double floorX = cellularAutomatonModel.getFloorModel(floorId).getXPosition();
        double floorY = cellularAutomatonModel.getFloorModel(floorId).getYPosition();
        return new Tuple(cellX + roomX + floorX, cellY + roomY + floorY);
    }

    /**
     * <p>
     * This method increases the number of individuals that are created and calculates a new progress. The progress will
     * at most reach 99% so that after all objects are created a final "Done" message can be submitted.</p>
     * <p>
     * Note that before this method can be used in the proper way the private variable {@code cellsDone} and
     * {@code cellCount} should be initialized correct. However, it is guaranteed to calculate a value from 0 to 99.
     */
    public void recordingProgress() {
        recordingDone++;
        int progress = Math.max(0, Math.min((int) Math.round(((double) recordingDone / recordingCount) * 100), 99));
        //AlgorithmTask.getInstance().setProgress( progress, "Erzeuge Individuen-Bewegungen...", "Recording-Schritt " + recordingDone + " von " + recordingCount + " abgearbeitet." );
    }

    @Override
    public void delete() {
        view.delete();
    }

    public void update() {
        for (GLCell cellView : views.cellViews()) {
            cellView.update();
        }
    }
}
