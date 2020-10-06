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
package io.visualization;

import java.util.Objects;
import org.zet.cellularautomaton.results.EvacuationRecording;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationSpeed;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.statistic.CAStatistic;

/**
 * A data structure containing all information about a run of the cellular automaton, including the (real z-format)
 * positions of the cells and the visual recorder.
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationSimulationResults {

    /** The recording of a simulation. */
    private final EvacuationRecording visRecording;

    //TODO correct
    public CAStatistic statistic;
    private final EvacuationState es; // Should not be referenced here directly.
    private final EvacuationSimulationSpeed esp;

    /**
     * Creates the visualization results. Takes a ca data structure, a visual recording object, a ZToCAMapping and
     * creates all necessary objects.
     *
     * @param visRecording
     * @param caMapping
     * @param ca
     */
    public EvacuationSimulationResults(EvacuationState es, EvacuationSimulationSpeed esp, EvacuationRecording visRecording) {
        this.visRecording = Objects.requireNonNull(visRecording);
        this.esp = Objects.requireNonNull(esp);
        this.es = Objects.requireNonNull(es);
    }

    public EvacuationRecording getRecording() {
        return this.visRecording;
    }

    public EvacuationState getEs() {
        return es;
    }

    public EvacuationSimulationSpeed getEsp() {
        return esp;
    }
}
