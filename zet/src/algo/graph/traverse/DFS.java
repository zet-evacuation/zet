/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * DFS.java
 *
 */

package algo.graph.traverse;

import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.IdentifiableCollection;
import ds.graph.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ds.graph.Localization;

/**
 *
 * @author Martin Groß
 */
public class DFS {
    
    public enum State { NORMAL, VISITED, DONE; }
    
    private Graph graph;
    
    private List<Edge> backEdges;
    private List<Edge> crossEdges;
    private List<Edge> forwardEdges;
    private List<Edge> treeEdges;
    private Map<Node,Integer> numbering;
    private Map<Node,State> states;
    private int currentNumber;
    
    public DFS() {        
        this(null);
    }
    
    public DFS(Graph graph) {
        this.graph = graph;
        backEdges = new ArrayList<Edge>(graph.numberOfEdges());
        crossEdges = new ArrayList<Edge>(graph.numberOfEdges());
        forwardEdges = new ArrayList<Edge>(graph.numberOfEdges());
        treeEdges = new ArrayList<Edge>(graph.numberOfEdges());
        numbering = new HashMap<Node,Integer>(graph.numberOfNodes());
        states = new HashMap<Node,State>(graph.numberOfNodes());
        currentNumber = 0;        
        for (int i=0; i<graph.numberOfNodes(); i++) {
            numbering.put(graph.getNode(i),0);
            states.put(graph.getNode(i),State.NORMAL);
        }        
    }

    public List<Edge> getBackEdges() {
        if (backEdges == null) throw new IllegalStateException(Localization.getInstance (
		).getString ("algo.graph.traverse.NotCalledYetException"));
        return backEdges;
    }    
    
    public List<Edge> getCrossEdges() {
        if (crossEdges == null) throw new IllegalStateException(Localization.getInstance (
		).getString ("algo.graph.traverse.NotCalledYetException"));
        return crossEdges;
    }    
    
    public List<Edge> getForwardEdges() {
        if (forwardEdges == null) throw new IllegalStateException(Localization.getInstance (
		).getString ("algo.graph.traverse.NotCalledYetException"));
        return forwardEdges;
    }    
    
    public List<Edge> getTreeEdges() {
        if (treeEdges == null) throw new IllegalStateException(Localization.getInstance (
		).getString ("algo.graph.traverse.NotCalledYetException"));
        return treeEdges;
    }
    
    public void run() {
        run(false);
    }
    
    public void run(boolean reverse) {
        if (graph == null) throw new IllegalStateException(Localization.getInstance (
		).getString ("algo.graph.traverse.NotCalledYetException"));
        if (backEdges != null) return;
        for (int i=0; i<graph.numberOfNodes(); i++) {
            if (numbering.get(graph.getNode(i)) == 0) run(graph.getNode(i),reverse);
        }        
    }
    
    public void run(Node node) {
        run(node,false);
    }
    
    public void run(Node node, boolean reverse) {
        if (states.get(node) != State.NORMAL) return;
        states.put(node,State.VISITED);
        currentNumber++;
        numbering.put(node,currentNumber);
        IdentifiableCollection<Edge> edges;
        if (reverse) {
            edges = graph.incomingEdges(node);
        } else {
            edges = graph.outgoingEdges(node);
        }
        for (Edge edge : edges) {
            Node oppositeNode = edge.opposite(node);
            if (numbering.get(oppositeNode) == 0) {
                treeEdges.add(edge);
                run(oppositeNode,reverse);
            } else if (numbering.get(oppositeNode) > numbering.get(node)) {
                forwardEdges.add(edge);
            } else if (numbering.get(oppositeNode) < numbering.get(node) && states.get(oppositeNode) == State.VISITED) {
                backEdges.add(edge);
            } else {
                crossEdges.add(edge);
            }
        }     
        states.put(node,State.DONE);
    }
    
    public State state(Node node) {
        return states.get(node);
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        backEdges = null;
        crossEdges = null;
        forwardEdges = null;
        treeEdges = null;
    }    
}
