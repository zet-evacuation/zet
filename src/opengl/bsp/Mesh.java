/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package opengl.bsp;

import de.tu_berlin.math.coga.math.vectormath.Vector3;
import java.util.Collection;
import java.util.Iterator;

/**
 * A class containing a list of polygons that represents an object in
 * 3-dimensional space.
 * @author Jan-Philipp Kappmeier
 */
public abstract class Mesh<T extends Face> implements Iterable<T> {
	protected Collection<T> faces;
	protected Collection<Vector3> vertices;
	protected Collection edges; // not supported

	public abstract void prepareSize( int vertices, int edges, int faces );

	public Iterator<T> iterator() {
		return faces.iterator();
	}

	public Iterator<T> faceIterator() {
		return faces.iterator();
	}

	public Iterator<Vector3> vertexIterator() {
		return vertices.iterator();
	}

	public Iterator edgeIterator() {
		return edges.iterator();
	}

	public Vector3 addVertex( double x, double y, double z ) {
		final Vector3 newVertex = new Vector3(x,y,z);
		vertices.add( newVertex );
		return newVertex;
	}

	public int vertexCount() {
		return vertices.size();
	}

	public int faceCount() {
		return faces.size();
	}

	public void unitize( double size ) {
		double maxx, minx, maxy, miny, maxz, minz;

		//assert (BspMain.vertices.size() > 0);

		/* init min/max  */
		maxy = maxx = maxz = Double.MIN_VALUE;
		minx = miny = minz = Double.MAX_VALUE;

		for( Vector3 v : vertices ) {
			if( v.x > maxx )
				maxx = v.x;
			if( v.x < minx )
				minx = v.x;
			if( v.y > maxy )
				maxy = v.y;
			if( v.y < miny )
				miny = v.y;
			if( v.z > maxz )
				maxz = v.z;
			if( v.z < minz )
				minz = v.z;
		}

		/* calculate model width, height, and depth */
		final double w = Math.abs( maxx ) + Math.abs( minx );
		final double h = Math.abs( maxy ) + Math.abs( miny );
		final double d = Math.abs( maxz ) + Math.abs( minz );

		/* calculate center of the model */
		final double cx = (maxx + minx) / 2.0;
		final double cy = (maxy + miny) / 2.0;
		final double cz = (maxz + minz) / 2.0;

		/* calculate unitizing scale factor */
		final double scale = size / Math.max( Math.max( w, h ), d );

		for( Vector3 vFront : vertices ) {
			vFront.x -= cx;
			vFront.x *= scale;
			vFront.y -= cy;
			vFront.y *= scale;
			vFront.z -= cz;
			vFront.z *= scale;
		}
	}
}
