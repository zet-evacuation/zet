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
package gui.visualization.control.ca;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.control.ZETGLControl.CellInformationDisplay;
import gui.visualization.draw.ca.GLCA;
import gui.visualization.draw.ca.GLIndividual;
import io.visualization.CellularAutomatonVisualizationResults;
import io.visualization.EvacuationSimulationResults;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.MultiFloorEvacuationCellularAutomaton;
import org.zet.cellularautomaton.results.DieAction;
import org.zet.cellularautomaton.results.EvacuationRecording;
import org.zet.cellularautomaton.results.MoveAction;
import org.zet.cellularautomaton.results.SwapAction;
import org.zetool.opengl.framework.abs.HierarchyNode;

/**
 * @author Jan-Philipp Kappmeier
 */
public class GLCellularAutomatonControl extends AbstractZETVisualizationControl<GLCAFloorControl, GLCA, CellularAutomatonVisualizationModel> implements HierarchyNode {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getGlobal();

    // general control stuff
    private HashMap<Integer, GLCAFloorControl> allFloorsByID;
    // Dynamic CA visualization
    private int recordingCount;
    private int recordingDone;
    boolean containsRecording = false;
    double scaling = 1;

    private CellularAutomatonVisualizationResults caVisResults;

    private EvacuationSimulationResults evacuationResults;

    public GLCellularAutomatonControl(CellularAutomatonVisualizationResults caVisResults, CellularAutomatonVisualizationModel visualizationModel) {
        super(visualizationModel);
        this.caVisResults = caVisResults;
    }

    public void build() {
        if (!(caVisResults.getCa() instanceof MultiFloorEvacuationCellularAutomaton)) {
            throw new IllegalStateException("Only multi floor automaton supported");
        }
        MultiFloorEvacuationCellularAutomaton mfca = (MultiFloorEvacuationCellularAutomaton) caVisResults.getCa();
        containsRecording = false;
        visualizationModel.init(MultiFloorEvacuationCellularAutomaton.getCellCount(caVisResults.getCa()));
        //AlgorithmTask.getInstance().setProgress( 0, DefaultLoc.getSingleton().getStringWithoutPrefix( "batch.tasks.progress.createCellularAutomatonVisualizationDatastructure" ), "" );

        recordingCount = 0;
        recordingDone = 0;

        allFloorsByID = new HashMap<>();

        Collection<String> floors = mfca.getFloors();
        for (int i = 0; i < floors.size(); ++i) {
            add(new GLCAFloorControl(caVisResults, mfca.getRoomsOnFloor(i), i, visualizationModel));
        }

        this.setView(new GLCA(this, visualizationModel));
        for (GLCAFloorControl floor : this) {
            view.addChild(floor.getView());
        }

        showAllFloors();
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
        for (GLCAFloorControl floor : this) {
            floor.getView().setIndividuals(visualizationModel.getIndividualControls());
        }
    }

    public Collection<GLCAFloorControl> getAllFloors() {
        return allFloorsByID.values();
    }

    public void showOnlyFloor(Integer floorID) {
        childControls.clear();
        //childControls.add( allFloorsByID.get( floorID == 0 && allFloorsByID.get( floorID ) == null ? 1 : floorID ) ); // add the floor if possible, otherwise the first
        childControls.add(allFloorsByID.get(floorID)); // add the floor if possible, otherwise the first
        view.clear();
        for (GLCAFloorControl floor : this) {
            view.addChild(floor.getView());
        }
    }

    public void showAllFloors() {
        childControls.clear();
        childControls.addAll(allFloorsByID.values());
        view.clear();
        for (GLCAFloorControl floor : this) {
            view.addChild(floor.getView());
        }
    }

    @Override
    public void add(GLCAFloorControl childControl) {
        super.add(childControl);
        System.out.println("FLoor hinzugefügt");
        allFloorsByID.put(childControl.getFloorNumber(), childControl);
    }

    @Override
    public void clear() {
        allFloorsByID.clear();
        childControls.clear();
    }

    @Override
    public Iterator<GLCAFloorControl> fullIterator() {
        return allFloorsByID.values().iterator();
    }

    public void setPotentialDisplay(CellInformationDisplay potentialDisplay) {
        for (GLCAFloorControl floorControl : allFloorsByID.values()) {
            floorControl.setPotentialDisplay(potentialDisplay);
        }
    }

    GLCAFloorControl getFloorControl(Integer floorID) {
        return this.allFloorsByID.get(floorID);
    }

    private GLCellControl getCellControl(EvacCellInterface cell) {
        GLCAFloorControl floor = getFloorControl(cell.getRoom().getFloor());
        GLRoomControl room = floor.getRoomControl(cell.getRoom());
        return room.getCellControl(cell);
    }

    private void convertIndividualMovements() {
        LOG.info("Converting indivudal movements");
        ArrayList<GLIndividual> glIndividuals = new ArrayList<>();
        ArrayList<GLIndividualControl> individuals = new ArrayList<>();

        EvacuationRecording recording = evacuationResults.getRecording();
        EvacuationCellularAutomaton ca = recording.getInitialConfig().getCellularAutomaton();
        individuals = new ArrayList<>(evacuationResults.getEs().getInitialIndividualCount());
        for (int k = 0; k < evacuationResults.getEs().getInitialIndividualCount(); k++) {
            individuals.add(null);
        }
        for (Individual individual : evacuationResults.getEs()) {
            GLIndividualControl control = new GLIndividualControl(individual, visualizationModel);
            individuals.set(individual.getNumber(), control);
        }

        recording.rewind();

        while (recording.hasNext()) {
            recording.nextActions();
            Vector<MoveAction> movements = recording.filterActions(MoveAction.class);
            for (MoveAction movement : movements) {
                GLCellControl fromCell = getCellControl(movement.from());
                GLCellControl endCell = getCellControl(movement.to());
                double arrivalTime = movement.arrivalTime();
                double startTime = movement.startTime();
                individuals.get(movement.getIndividualNumber()).addHistoryTriple(fromCell, endCell, startTime, arrivalTime);
            }
            Vector<SwapAction> swaps = recording.filterActions(SwapAction.class);
            for (SwapAction swap : swaps) {
                GLCellControl cell1 = getCellControl(swap.cell1());
                GLCellControl cell2 = getCellControl(swap.cell2());
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
                GLCellControl cell = getCellControl(death.placeOfDeath());
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
        for (GLCAFloorControl floor : this) {
            //floor.delete();
        }
        view.delete();
        caVisResults = null;
    }

    public void stepUpdate() {
        for (GLCAFloorControl floor : this) {
            for (GLRoomControl room : floor) {
                for (GLCellControl cell : room) {
                    cell.stepUpdate();
                }
            }
        }
    }
}
