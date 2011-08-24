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
 * ThinFlow.java
 *
 */

package ds.graph.flow;

import ds.graph.problem.ThinFlowProblem;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.DoubleMap;

/**
 *
 * @author Sebastian Schenker   
 */

public class ThinFlow {
    
    private ThinFlowProblem problem;
    
    private DoubleMap<Node> nodelabels;
    
    private DoubleMap<Edge> edgeflowvalues;
    
    public ThinFlow(ThinFlowProblem problem, DoubleMap<Edge> flows, DoubleMap<Node> labels) {
        this.problem = problem;
        nodelabels = labels;
        edgeflowvalues = flows;
    }

    public ThinFlowProblem getProblem() {
        return problem;
    }
    
    public DoubleMap<Node> getNodeLabels() {
        return nodelabels;
    }
        
    public DoubleMap<Edge> getEdgeFlowValues() {
        return edgeflowvalues;
    }
    
}