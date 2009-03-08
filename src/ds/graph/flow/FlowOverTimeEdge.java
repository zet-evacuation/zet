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
 * FlowOverTimeEdge.java
 *
 */

package ds.graph.flow;

import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;

/**
 *
 * @author Martin Groß
 */
public class FlowOverTimeEdge implements Cloneable {
    
    private int delay;
    private Edge edge;

    public FlowOverTimeEdge(Edge edge, int delay) {
        this.edge = edge;
        this.delay = delay;
    }
    
    public int length(IdentifiableIntegerMapping<Edge> transitTimes) {
        return delay + transitTimes.get(edge);
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public Edge getEdge() {
        return edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    public FlowOverTimeEdge clone() {
        return new FlowOverTimeEdge(edge, delay);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FlowOverTimeEdge other = (FlowOverTimeEdge) obj;
        if (this.delay != other.delay) {
            return false;
        }
        if (this.edge != other.edge && (this.edge == null || !this.edge.equals(other.edge))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + this.delay;
        hash = 31 * hash + (this.edge != null ? this.edge.hashCode() : 0);
        return hash;
    }   
    
    @Override
    public String toString() {
        return String.format("(%1$s, %2$s)", delay, edge);
    }
}
