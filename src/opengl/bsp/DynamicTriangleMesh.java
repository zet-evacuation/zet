/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package opengl.bsp;

import de.tu_berlin.math.coga.math.vectormath.Vector3;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A mesh consisting of triangles. The mesh faces are stored in a queue. The
 * vertices can be fast accessed via indices, they are stored in an
 * {@link java.util.ArrayList}.
 * @author Jan-Philipp Kappmeier
 */
public class DynamicTriangleMesh extends Mesh<Triangle> {
	Queue<Triangle> facesQueue;
	ArrayList<Vector3> verticesArray;
	public DynamicTriangleMesh() {
		verticesArray = new ArrayList<Vector3>();
		facesQueue = new LinkedList<Triangle>();
		faces = facesQueue;
		vertices = verticesArray;
	}

	/**
	 * Creates a triangle consisting of the specified vertices.
	 * @param i1 the index of the first vertex
	 * @param i2 the index of the second vertex
	 * @param i3 the index of the third vertex
	 * @return
	 */
	public Triangle addTriangle( int i1, int i2, int i3 ) {
		final Triangle t = new Triangle( verticesArray.get(i1), verticesArray.get(i2), verticesArray.get(i3) );
		faces.add(t);
		return t;
	}

	public boolean offer(Triangle e) {
		return facesQueue.offer( e );
	}

	public Triangle remove() {
		return facesQueue.remove();
	}

	public Triangle poll() {
		return facesQueue.poll();
	}

	public Triangle element() {
		return facesQueue.element();
	}

	public Triangle peek() {
		return facesQueue.peek();
	}

	@Override
	public void prepareSize(int vertices, int edges, int faces) {
		verticesArray.ensureCapacity( vertices );
	}

	Queue<Triangle> getQueue() {
		return facesQueue;
	}

}
