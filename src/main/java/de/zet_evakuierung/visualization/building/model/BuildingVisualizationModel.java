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
package de.zet_evakuierung.visualization.building.model;

import gui.visualization.EvacuationVisualizationModel;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BuildingVisualizationModel extends EvacuationVisualizationModel {

    private int wallCount;
    private int wallsDone;

    /**
     * This method increases the number of cells that are created and calculates a new progress. The progress will at
     * most reach 99% so that after all objects are created a final "Done" message can be submitted.
     * <p>
     * Note that before this method can be used in the proper way the private variable {@code wallsDone} and
     * {@code WallCount} should be initialized correct. However, it is guaranteed to calculate a value from 0 to 99.
     */
    public void wallProgress() {
        wallsDone++;
        int progress = Math.max(0, Math.min((int) Math.round(((double) wallsDone / wallCount) * 100), 99));
        //AlgorithmTask.getInstance().setProgress( progress, "Erzeuge Gebäude...", "Wand " + wallsDone + " von " + wallCount + " erzeugt." );
    }

    void init(int wallCount) {
        this.wallCount = wallCount;
        wallsDone = 0;
    }

    @Override
    public double getStep() {
        return 0;
    }

    @Override
    public void addTime(long timeNanoSeconds) {
        // Static model only
    }

    @Override
    public void setTime(long unused) {
        // Static model only
    }

    @Override
    public void resetTime() {
        // Static model only
    }

    @Override
    public boolean isFinished() {
        return true;
    }

}
