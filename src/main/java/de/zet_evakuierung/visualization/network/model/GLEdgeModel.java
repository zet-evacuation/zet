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
package de.zet_evakuierung.visualization.network.model;

import de.zet_evakuierung.visualization.network.GraphVisualizationData;
import org.zetool.graph.Edge;
import org.zetool.math.Conversion;
import org.zetool.math.vectormath.Vector3;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLEdgeModel {

    /**
     * Decides whether this edge is the one from the node with lower id to the one with higher id of the two edges
     * between two nodes.
     */
    protected boolean isFirst = false;
    private double length = 0;
    private double length3d = 0;
    private Vector3 differenceVectorInOpenGLScaling;
    private Vector3 startPoint;
    private Vector3 endPoint;
    double scaling = 1;

    public GLEdgeModel(GraphVisualizationData visualizationData, Edge edge) {
        super();

        // checks weather this edge is the first one of the two representing one undirected edge
        if (edge.start().id() < edge.end().id()) {
            isFirst = true;
        }

        init(visualizationData.getPosition(edge.start()), visualizationData.getPosition(edge.end()));
    }

    private void init(Vector3 startPos, Vector3 endPos) {
        // calculate differences between the points
        final double dx = (startPos.x - endPos.x) * scaling;
        final double dy = (startPos.y - endPos.y) * scaling;
        final double dz = (startPos.z - endPos.z) * scaling;
        differenceVectorInOpenGLScaling = new Vector3(dx, dy, dz);

        // calculate length and 3d length
        length = Math.sqrt(dx * dx + dy * dy);
        length3d = Math.sqrt(dz * dz + length * length);

        startPoint = startPos;
        endPoint = endPos;
    }

    public void setScaling(double scaling) {
        this.scaling = scaling;
        init(startPoint, endPoint);
    }

    final public Vector3 getStartPosition() {
        return startPoint.scalarMultiplicate(scaling);
    }

    final public Vector3 getEndPosition() {
        return endPoint.scalarMultiplicate(scaling);
    }

    /**
     * Returns whether this edge is the one going from lower ID to higher ID (of the two edges between two nodes).
     *
     * @return whether this edge is the one going from lower ID to higher ID
     */
    final public boolean isFirstEdge() {
        return isFirst;
    }

    /**
     * Returns the model-length of the edge, NOT taking the z-coordinate into account.
     *
     * @return the model-length of the edge, NOT taking the z-coordinate into account
     */
    final public double getLength() {
        return length;
    }

    /**
     * Returns the model length of the edge, taking the z-coordinate into account.
     *
     * @return the model length of the edge, taking the z-coordinate into account
     */
    final public double get3DLength() {
        return length3d;
    }

    /**
     * Returns the difference vector between the start node and the end node of the controlled edge.
     *
     * @return the difference vector between the start node and the end node of the controlled edge, i.e. start-end in
     * each component.
     */
    final public Vector3 getDifferenceVectorInOpenGlScaling() {
        return differenceVectorInOpenGLScaling;
    }

    /**
     * Calculates the angle between to vectors a and b, going from a to b. Lies always between 0 and 180 ! Uses the dot
     * product to calculate the cosine, the angle is then calculated with the arcus cosine.
     *
     * @param a a vector.
     * @param b another vector.
     * @return the angle between the two vectors.
     */
    final public double getAngleBetween(Vector3 a, Vector3 b) {
        final double cosine = (a.dotProduct(b) / a.length()) / b.length();
        final double angle = Math.acos(cosine) / Conversion.ANGLE2DEG;
        return 180 - angle;
    }

    /**
     * Returns the cross product of a and b (a being the first vector).
     *
     * @param a a vector.
     * @param b another vector.
     * @return the cross product of a and b (a being the first vector).
     */
    final public Vector3 getRotationAxis(Vector3 a, Vector3 b) {
        return a.crossProduct(b);
    }

    public double getThickness() {
        return 5 * 0.1;
    }

}
