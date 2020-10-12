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
package de.zet_evakuierung.visualization.ca.control;

import static gui.visualization.control.ZETGLControl.CellInformationDisplay.DynamicPotential;
import static java.util.stream.Collectors.toList;

import de.zet_evakuierung.visualization.ca.draw.GLCell;
import de.zet_evakuierung.visualization.ca.draw.GLDelayCell;
import de.zet_evakuierung.visualization.ca.draw.GLEvacuationCell;
import de.zet_evakuierung.visualization.ca.draw.GLIndividual;
import de.zet_evakuierung.visualization.ca.draw.GLSaveCell;
import de.zet_evakuierung.visualization.ca.draw.GLStairCell;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.control.StepUpdateListener;
import gui.visualization.control.ZETGLControl.CellInformationDisplay;
import gui.visualization.util.Tuple;
import io.visualization.CellularAutomatonVisualizationResults;
import org.zet.algo.ca.util.PotentialUtils;
import org.zet.cellularautomaton.DoorCell;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.SaveCell;
import org.zet.cellularautomaton.StairCell;
import org.zet.cellularautomaton.TeleportCell;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.statistic.CAStatistic;
import org.zetool.common.util.Direction8;
import org.zetool.opengl.drawingutils.GLColor;

public class GLCellControl extends AbstractZETVisualizationControl<GLCellControl, GLCell, CellularAutomatonVisualizationModel> implements StepUpdateListener {

    private final GLRoomControl glRoomControlObject;  // the corresponding GLRoomControl of this object
    private final double xPosition;
    private final double yPosition;
    private static Potential mergedPotential = null;
    private static Potential activePotential = null;
    private static long MAX_DYNAMIC_POTENTIAL = -1;
    private CellInformationDisplay displayMode = CellInformationDisplay.StaticPotential;
    private final EvacCell controlled;
    // Initially unset
    private EvacuationState es;
    
    public static void invalidateMergedPotential() {
        mergedPotential = null;
    }

    CAStatistic statistic;

    public GLCellControl(CellularAutomatonVisualizationResults caVisResults, EvacCell cell, GLRoomControl glRoomControl, CellularAutomatonVisualizationModel visualizationModel) {
        super(visualizationModel);
        this.statistic = null;
        this.controlled = cell;
        xPosition = caVisResults.get(cell).x * visualizationModel.scaling;
        yPosition = caVisResults.get(cell).y * visualizationModel.scaling;
        this.glRoomControlObject = glRoomControl;

        if (mergedPotential == null) {
            mergedPotential = PotentialUtils.mergePotentials(caVisResults.getCa().getExits().stream().map(exit -> caVisResults.getCa().getPotentialFor(exit)).collect(toList()));
            activePotential = mergedPotential;
        }
        MAX_DYNAMIC_POTENTIAL = 0;

        GLCell glCell = null;
        if (cell instanceof DoorCell || cell instanceof RoomCell) {
            if (cell.getSpeedFactor() == RoomCell.STANDARD_ROOMCELL_SPEEDFACTOR) {
                glCell = new GLCell(this);
            } else {
                glCell = new GLDelayCell(this);
            }
        } else if (cell instanceof ExitCell) {
            glCell = new GLEvacuationCell(this);
        } else if (cell instanceof SaveCell) {
            glCell = new GLSaveCell(this);
        } else if (cell instanceof StairCell) {
            glCell = new GLStairCell(this);
        } else if (cell instanceof TeleportCell) {
            glCell = new GLSaveCell(this);
        } else {
            throw new java.lang.IllegalStateException("Illegal Cell Type");
        }
        this.setView(glCell);
        visualizationModel.cellProgress();
    }

    public GLIndividual getDrawIndividual() {

        return visualizationModel.getControlledGLIndividual(controlled.getState().getIndividual().getNumber());
    }

    public int getFloorID() {
        return controlled.getRoom().getFloor();
    }

    public double getXPosition() {
        return xPosition;
    }

    public double getYPosition() {
        return -yPosition;
    }

    /**
     * Returns the x-coordinate of the cell relative to the room.
     *
     * @return the x-coordinate.
     */
    public int getX() {
        return controlled.getX();
    }

    /**
     * Returns the y-coordiante of the cell relative to the room.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return controlled.getY();
    }

    public double getSpeedFactor() {
        return controlled.getSpeedFactor();
    }

    /**
     * Returns the corresponding GLControlRoom-Object which created this GLCellControl Object.
     *
     * @return the corresponding GLControlRoom-Object which created this GLCellControl Object.
     */
    public GLRoomControl getGLControlRoom() {
        return this.glRoomControlObject;
    }

    @Override
    public void stepUpdate() {
        // Update the floor colors if in an mode that can change every step
        if (displayMode == CellInformationDisplay.DynamicPotential || displayMode == CellInformationDisplay.Utilization || displayMode == CellInformationDisplay.Waiting) {
            getView().update();
        }
    }

    /**
     * Returns the absolute position of the cell in the graphics world
     *
     * @return the absolute position. of the cell in the graphics world
     */
    public Tuple getAbsolutePosition() {
        return new Tuple(this.getXPosition() + this.getGLControlRoom().getXPosition() + this.getGLControlRoom().getGLCAFloorControl().getXPosition(), this.getYPosition() + this.getGLControlRoom().getYPosition() + this.getGLControlRoom().getGLCAFloorControl().getYPosition());
    }

    public static void setActivePotential(Potential activePotential) {
        GLCellControl.activePotential = activePotential;
    }

    public static Potential getMergedPotential() {
        return mergedPotential;
    }

    /**
     * Returns some values that can be used to display the status of the controlled cell, such as potentials or
     * utilization.
     *
     * @param cid the status type that is used
     * @return the value of t he status at the current time
     */
    public long getCellInformation(CellInformationDisplay cid) {
        switch (cid) {
            case DynamicPotential:
                return (long)es.getDynamicPotential(controlled);
            case StaticPotential:
                return activePotential.getPotential(controlled);
            case Utilization:
                //return 0;
                // TODO statistic visualization
                //return mainControl.getCAStatistic().getCellStatistic().getCellUtilization( controlled, (int) mainControl.getStep() );
                return statistic.getCellStatistic().getCellUtilization(controlled, (int) visualizationModel.getStep());
            case Waiting:
                //return 0;
                // TODO statistic visualization
                //return mainControl.getCAStatistic().getCellStatistic().getCellWaitingTime( controlled, (int) mainControl.getStep() );
                return statistic.getCellStatistic().getCellWaitingTime(controlled, (int) visualizationModel.getStep());
            default:
                return 0;
        }
    }

    /**
     * Returns the maximal values that are reached at the current time for some status properties of the controlled
     * cell, such as potentials or utilization.
     *
     * @param cid the status type that is used
     * @return the maximal value of the status at the current time
     */
    public long getMaxCellInformation(CellInformationDisplay cid) {
        switch (cid) {
            case DynamicPotential:
                return MAX_DYNAMIC_POTENTIAL;
            case StaticPotential:
                return activePotential.getMaxPotential();
            case Utilization:
                return 0;
            // TODO opengL statistic visualization
            //return mainControl.getCAStatistic().getCellStatistic().getMaxUtilization();
            case Waiting:
                return 0;
            // TODO opengL statistic visualization
            //return mainControl.getCAStatistic().getCellStatistic().getMaxWaiting();
            default:
                return 0;
        }
    }

    public CellInformationDisplay getDisplayMode() {
        return displayMode;
    }

    public boolean isPotentialValid() {
        return activePotential.hasValidPotential(controlled);
    }

    void setPotentialDisplay(CellInformationDisplay potentialDisplay) {
        displayMode = potentialDisplay;
    }

    /**
     * Creates a mixed colour for the cell. The direction indicates for which edge of the cell the colour is calculated.
     *
     * @param direction the edge of the cell
     * @return the mixed color for that edge
     */
    public GLColor mixColorWithNeighbours(Direction8 direction) {
        EvacCellInterface[] c = new EvacCell[3];
        GLCellControl cc;
        double r = getView().getColor().getRed();
        double g = getView().getColor().getGreen();
        double b = getView().getColor().getBlue();
        int count = 1;

        switch (direction) {
            case TopLeft:
                c[0] = controlled.getNeighbor(Direction8.Top);
                c[1] = controlled.getNeighbor(Direction8.TopLeft);
                c[2] = controlled.getNeighbor(Direction8.Left);
                break;
            case TopRight:
                c[0] = controlled.getNeighbor(Direction8.Top);
                c[1] = controlled.getNeighbor(Direction8.TopRight);
                c[2] = controlled.getNeighbor(Direction8.Right);
                break;
            case DownRight:
                c[0] = controlled.getNeighbor(Direction8.Down);
                c[1] = controlled.getNeighbor(Direction8.DownLeft);
                c[2] = controlled.getNeighbor(Direction8.Left);
                break;
            case DownLeft:
                c[0] = controlled.getNeighbor(Direction8.Down);
                c[1] = controlled.getNeighbor(Direction8.DownRight);
                c[2] = controlled.getNeighbor(Direction8.Right);
                break;
            default:
                return new GLColor(1, 1, 1);
        }
        for (int i = 0; i < 3; i++) {
            if (c[i] != null) {
                count++;
                cc = getGLControlRoom().getCellControl(c[i]);
                r += cc.getView().getColor().getRed();
                g += cc.getView().getColor().getGreen();
                b += cc.getView().getColor().getBlue();
            }
        }
        r /= count;
        g /= count;
        b /= count;
        return new GLColor(r, g, b, 1);
    }

    public double getWidth() {
        return visualizationModel.scaling * (VisualizationOptionManager.showSpaceBetweenCells() ? 390 : 400);
    }

    public double getOffset() {
        return VisualizationOptionManager.showSpaceBetweenCells() ? 10 * visualizationModel.scaling : 0;
    }
}
