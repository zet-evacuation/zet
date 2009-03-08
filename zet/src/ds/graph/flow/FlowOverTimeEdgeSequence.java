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
 * FlowOverTimePath.java
 *
 */
package ds.graph.flow;

import ds.graph.DynamicPath;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Path;
import java.util.LinkedList;

/**
 * The <code>@link FlowOverTimePath</code> class represents the flow on one 
 * {@link Path} in a network. The delay time in the first node of the path
 * implies the time when the represented flow would start to leave the first node.
 * The flow is send with a constant rate of <code>rate</code>. It sends a
 * total quantity of <code>amount<code> units of flow. Together with the rate
 * and the starting time this implies the point in time where the flow
 * will stop sending.
 * <code>DynamicPathFlows</code> are needed to represent dynamic flows path based.
 */
public class FlowOverTimeEdgeSequence extends LinkedList<FlowOverTimeEdge> {

    private int rate;

    public FlowOverTimeEdgeSequence() {
        super();
        rate = 1;
    }

    public FlowOverTimeEdgeSequence(FlowOverTimePath path) {
        rate = path.getRate();
        for (Edge edge : path) {
            addLast(new FlowOverTimeEdge(edge, path.delay(edge)));
        }
    }

    public FlowOverTimeEdgeSequence(FlowOverTimeEdgeSequence edgeSequence) {
        rate = edgeSequence.getRate();
        for (FlowOverTimeEdge edge : edgeSequence) {
            addLast(new FlowOverTimeEdge(edge.getEdge(), edge.getDelay()));
        }
    }

    public void append(FlowOverTimeEdgeSequence path) {
        for (FlowOverTimeEdge edge : path) {
            addLast(new FlowOverTimeEdge(edge.getEdge(), edge.getDelay()));
        }
    //addAll(path);
    }

    public void append(FlowOverTimePath path) {
        for (Edge edge : path) {
            addLast(new FlowOverTimeEdge(edge, path.delay(edge)));
        }
    }

    public void append(FlowOverTimeEdgeSequence edgeSequence, int time) {
        boolean first = true;
        for (FlowOverTimeEdge edge : edgeSequence) {
            if (first) {
                addLast(new FlowOverTimeEdge(edge.getEdge(), time));
                first = false;
            } else {
                addLast(new FlowOverTimeEdge(edge.getEdge(), edge.getDelay()));
            }
        }
    }

    public void append(FlowOverTimePath path, int time) {
        boolean first = true;
        for (Edge edge : path) {
            if (first) {
                addLast(new FlowOverTimeEdge(edge, time));
                first = false;
            } else {
                addLast(new FlowOverTimeEdge(edge, path.delay(edge)));
            }
        }
    }

    public FlowOverTimeEdge getFirstEdge() {
        return getFirst();
    }

    public FlowOverTimeEdge getLastEdge() {
        return getLast();
    }

    public int delay(FlowOverTimeEdge edge) {
        return edge.getDelay();
    }
    
    public int length(IdentifiableIntegerMapping<Edge> transitTimes) {
        int result = 0;
        for (FlowOverTimeEdge edge : this) {
            result += edge.getDelay();
            result += transitTimes.get(edge.getEdge());
        }
        return result;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String toText(IdentifiableIntegerMapping transitTimes) {
        StringBuilder result = new StringBuilder();
        result.append(toString() + "\n");
        int time = 0;
        for (FlowOverTimeEdge edge : this) {
            result.append(" Reaching node " + edge.getEdge().start() + " at time " + time + ".\n");
            if (delay(edge) > 0) {
                result.append(" Waiting for " + delay(edge) + ".\n");
            }
            time += delay(edge);
            result.append(" Entering edge " + edge.getEdge().id() + " at " + time + ".\n");
            time += transitTimes.get(edge.getEdge());
        }
        return result.toString();
    }

    /**
     * Returns a String consisting of the rate, the amount and a description
     * of the path that is described in {@link DynamicPath}.
     * @return a String consisting of the rate, the amount and a description
     * of the path that is described in {@link DynamicPath}.
     */
    @Override
    public String toString() {
        return String.format("{%1$s, %2$s}", rate, super.toString());
    }
}
