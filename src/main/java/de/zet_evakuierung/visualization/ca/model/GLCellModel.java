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

import static java.util.stream.Collectors.toList;

import de.zet_evakuierung.visualization.AbstractVisualizationModel;
import de.zet_evakuierung.visualization.VisualizationNodeModel;
import de.zet_evakuierung.visualization.ca.draw.GLIndividual;
import de.zet_evakuierung.visualization.ca.model.DynamicCellularAutomatonInformation.CellInformationDisplay;
import gui.visualization.VisualizationOptionManager;
import io.visualization.CellularAutomatonVisualizationResults;
import org.zet.algo.ca.util.PotentialUtils;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.statistic.CAStatistic;
import org.zetool.common.util.Direction8;

public class GLCellModel extends AbstractVisualizationModel<CellularAutomatonVisualizationModel>
        implements VisualizationNodeModel {

    private final double xPosition;
    private final double yPosition;
    private static Potential mergedPotential = null;
    private static Potential activePotential = null;
    private static long MAX_DYNAMIC_POTENTIAL = -1;
    private CellInformationDisplay displayMode = CellInformationDisplay.STATIC_POTENTIAL;
    private final EvacCell controlled;
    // Initially unset
    private EvacuationState es;

    public static void invalidateMergedPotential() {
        mergedPotential = null;
    }

    CAStatistic statistic;

    public GLCellModel(CellularAutomatonVisualizationResults caVisResults, EvacCell cell,
            CellularAutomatonVisualizationModel visualizationModel) {
        super(visualizationModel);
        this.statistic = null;
        this.controlled = cell;
        xPosition = caVisResults.get(cell).x * visualizationModel.scaling;
        yPosition = caVisResults.get(cell).y * visualizationModel.scaling;

        if (mergedPotential == null) {
            mergedPotential = PotentialUtils.mergePotentials(caVisResults.getCa().getExits().stream()
                    .map(exit -> caVisResults.getCa().getPotentialFor(exit)).collect(toList()));
            activePotential = mergedPotential;
        }
        MAX_DYNAMIC_POTENTIAL = 0;

        visualizationModel.cellProgress();
    }

    public GLIndividual getDrawIndividual() {

        return visualizationModel.getControlledGLIndividual(controlled.getState().getIndividual().getNumber());
    }

    public EvacCellInterface getBackingCell() {
        return controlled;
    }

    public int getFloorID() {
        return controlled.getRoom().getFloor();
    }

    @Override
    public double getXPosition() {
        return xPosition;
    }

    @Override
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
     * Checks whether a new step has started and the current display mode is one that requires color updates on every
     * step.
     *
     * @return {@code true} if re-calculation of color values is required
     */
    public boolean isUpdateRequired() {
        return visualizationModel.isNewStep() && isDisplayMode();
    }

    private boolean isDisplayMode() {
        return displayMode == CellInformationDisplay.DYNAMIC_POTENTIAL
                || displayMode == CellInformationDisplay.UTILIZATION
                || displayMode == CellInformationDisplay.WAITING;
    }

    public static void setActivePotential(Potential activePotential) {
        GLCellModel.activePotential = activePotential;
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
            case DYNAMIC_POTENTIAL:
                return (long) es.getDynamicPotential(controlled);
            case STATIC_POTENTIAL:
                return activePotential.getPotential(controlled);
            case UTILIZATION:
                //return 0;
                // TODO statistic visualization
                //return mainControl.getCAStatistic().getCellStatistic().getCellUtilization( controlled, (int) mainControl.getStep() );
                return statistic.getCellStatistic().getCellUtilization(controlled, (int) visualizationModel.getStep());
            case WAITING:
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
            case DYNAMIC_POTENTIAL:
                return MAX_DYNAMIC_POTENTIAL;
            case STATIC_POTENTIAL:
                return activePotential.getMaxPotential();
            case UTILIZATION:
                return 0;
            // TODO opengL statistic visualization
            //return mainControl.getCAStatistic().getCellStatistic().getMaxUtilization();
            case WAITING:
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

    public double getWidth() {
        return visualizationModel.scaling * (VisualizationOptionManager.showSpaceBetweenCells() ? 390 : 400);
    }

    public double getOffset() {
        return VisualizationOptionManager.showSpaceBetweenCells() ? 10 * visualizationModel.scaling : 0;
    }

    public boolean isNeighborPresent(Direction8 currentNeighbor) {
        return controlled.getNeighbor(currentNeighbor) != null;
    }
}
