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
package de.zet_evakuierung.visualization.network;

import de.zet_evakuierung.visualization.VisHistoryTriple;

/**
 *
 * @author Moukarram Kabbash
 */
public class FlowHistroryTriple extends VisHistoryTriple<Integer, Double, Integer> {

    private int time;
    private double flow;
    private int duration;

    public FlowHistroryTriple(int time, double flow, int duration) {
        super(time, flow, duration);

        this.time = time;
        this.flow = flow;
        this.duration = duration;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
        super.setFirstValue(time);
    }

    public double getFlow() {
        return flow;
    }

    public void setFlow(double flow) {
        this.flow = flow;
        super.setSecondValue(flow);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        super.setThirdValue(duration);
    }

    /**
     * Sets the first value to vale of parameter v1
     *
     * @param v1
     */
    @Override
    public void setFirstValue(Integer v1) {
        super.setFirstValue(v1);
        time = v1;
    }

    /**
     * Sets the second value to vale of parameter v2
     *
     * @param v2
     */
    @Override
    public void setSecondValue(Double v2) {
        super.setSecondValue(v2);
        flow = v2;
    }

    /**
     * Sets the third value to vale of parameter v3
     *
     * @param v3
     */
    @Override
    public void setThirdValue(Integer v3) {
        super.setThirdValue(v3);
        duration = v3;
    }

}
