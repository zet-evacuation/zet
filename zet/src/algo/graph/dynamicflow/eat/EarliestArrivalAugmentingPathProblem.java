/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * EarliestArrivalAugmentingPathProblem.java
 *
 */

package algo.graph.dynamicflow.eat;

import ds.graph.DynamicResidualNetwork;
import ds.graph.Node;

/**
 *
 * @author Martin Groß
 */
public class EarliestArrivalAugmentingPathProblem {
    
    private DynamicResidualNetwork network;
    private Node source;
    private Node sink;
    private int timeHorizon;

    public EarliestArrivalAugmentingPathProblem(DynamicResidualNetwork network, Node source, Node sink, int timeHorizon) {
        this.network = network;
        this.source = source;
        this.sink = sink;
        this.timeHorizon = timeHorizon;
    }

    public DynamicResidualNetwork getNetwork() {
        return network;
    }

    public void setNetwork(DynamicResidualNetwork network) {
        this.network = network;
    }

    public Node getSink() {
        return sink;
    }

    public void setSink(Node sink) {
        this.sink = sink;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public int getTimeHorizon() {
        return timeHorizon;
    }

    public void setTimeHorizon(int timeHorizon) {
        this.timeHorizon = timeHorizon;
    }    
}
