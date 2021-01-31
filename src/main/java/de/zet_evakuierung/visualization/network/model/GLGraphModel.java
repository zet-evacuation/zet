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
package de.zet_evakuierung.visualization.network.model;

import de.zet_evakuierung.visualization.network.draw.GLGraph;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Node;
import org.zetool.graph.visualization.NodePositionMapping;
import org.zetool.math.vectormath.Vector3;
import org.zetool.opengl.framework.abs.AbstractControl;
import org.zetool.opengl.framework.abs.Drawable;
import org.zetool.opengl.framework.abs.HierarchyNode;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLGraphModel extends AbstractControl<GLSimpleNodeModel, GLGraph> implements Drawable, HierarchyNode<GLSimpleNodeModel> {

    protected NodePositionMapping<Vector3> nodePositionMapping;
    protected DirectedGraph graph;

    public GLGraphModel(DirectedGraph graph, NodePositionMapping<Vector3> nodePositionMapping) {
        this(graph, nodePositionMapping, true);
    }

    public GLGraphModel(DirectedGraph graph, NodePositionMapping<Vector3> nodePositionMapping, boolean setUpNodes) {
        this.nodePositionMapping = nodePositionMapping;
        this.graph = graph;
        if (setUpNodes) {
            setUpNodes();
        }
    }

    protected void setUpNodes() {
        for (Node n : graph.nodes()) {
            GLSimpleNodeModel nodeControl = new GLSimpleNodeModel(graph, n, nodePositionMapping);
            add(nodeControl);
        }

        this.setView(new GLGraph(this));
        for (GLSimpleNodeModel nodeControl : this) {
            view.addChild(nodeControl.getView());
        }
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
