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
package de.zet_evakuierung.visualization.ca.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationSpeed;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonVisualizationModelTest {

    @Test
    public void addTime() {
        // 100 nanos per step
        EvacuationSimulationSpeed esp = mock(EvacuationSimulationSpeed.class);
        when(esp.getSecondsPerStep()).thenReturn(10.0 / 100_000_000);

        CellularAutomatonVisualizationModel fixture = new CellularAutomatonVisualizationModel();

        // Init for time 0 and 1 as last step
        fixture.initTiming(esp, 1);

        checkTiming(fixture, fixture::addTime, Arrays.asList(99L, 1L, 100L));
    }

    @Test
    public void setTime() {
        // 100 nanos per step
        EvacuationSimulationSpeed esp = mock(EvacuationSimulationSpeed.class);
        when(esp.getSecondsPerStep()).thenReturn(10.0 / 100_000_000);

        CellularAutomatonVisualizationModel fixture = new CellularAutomatonVisualizationModel();

        // Init for time 0 and 1 as last step
        fixture.initTiming(esp, 1);

        checkTiming(fixture, fixture::setTime, Arrays.asList(99L, 100L, 200L));
    }

    /**
     * Asserts three points in time. Expects that the {@code fixture} is initialized at time 0.
     *
     * <ul>
     * <li>Time 0: 0.99 steps, same step, not finished</li>
     * <li>Time 1: 1.00 steps, new step, not finished</li>
     * <li>Time 2: 2.00 steps, new step, finished</li>
     * </ul>
     *
     * @param fixture the fixture under test
     * @param timeFunction function called to set new time
     * @param times values passed to the {@code timeFunction} (for indices 0, 1, 2)
     */
    private void checkTiming(CellularAutomatonVisualizationModel fixture, Consumer<Long> timeFunction, List<Long> times) {
        // Check parameters are valid
        assertThat(times, hasSize(3));

        // Init empty
        assertThat(Double.doubleToLongBits(fixture.getStep()), is(equalTo(Double.doubleToLongBits(0.0))));
        assertThat(fixture.isNewStep(), is(equalTo(true)));
        assertThat(fixture.isFinished(), is(equalTo(false)));

        // add time, but stay in initial step
        timeFunction.accept(times.get(0));
        assertThat(Double.doubleToLongBits(fixture.getStep()), is(equalTo(Double.doubleToLongBits(0.99))));
        assertThat(fixture.isNewStep(), is(equalTo(false)));
        assertThat(fixture.isFinished(), is(equalTo(false)));

        timeFunction.accept(times.get(1));
        assertThat(Double.doubleToLongBits(fixture.getStep()), is(equalTo(Double.doubleToLongBits(1.0))));
        assertThat(fixture.isNewStep(), is(equalTo(true)));
        assertThat(fixture.isFinished(), is(equalTo(false)));

        // Go to last step
        timeFunction.accept(times.get(2));
        assertThat(Double.doubleToLongBits(fixture.getStep()), is(equalTo(Double.doubleToLongBits(2.0))));
        assertThat(fixture.isNewStep(), is(equalTo(true)));
        assertThat(fixture.isFinished(), is(equalTo(true)));

    }
}
