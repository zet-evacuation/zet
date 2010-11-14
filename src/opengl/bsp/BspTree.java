/*
 * BspTree.java
 * Created: 14.11.2010, 18:39:11
 */
package opengl.bsp;

import de.tu_berlin.math.coga.math.vectormath.Plane;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import java.util.List;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BspTree {
	// pointer to plane that partitions this node

	Plane partitionPlane;
	// pointer to list of triangles that are embedded in this node
	List<Triangle> sameDir;
	List<Triangle> oppDir;
	// pointer to the negative and positive subtree
	BspTree negativeSide;
	BspTree positiveSide;

	/** traverses the bsp and calls the draw() function on each node */
	static void traverse( BspTree bsp ) {
	}

	/** draws a list of triangles using OpenGL */
	static void draw( List<Triangle> triangles, Sign sign ) {
	}

	/** Given a TriangleList computes a BSP tree. As a result
	the passed TriangleList is automatically destructed since
	triangles have been parceled out to the bsp tree nodes */
	static BspTree constructBsp( List<Triangle> triangles ) {
		return null;
	}

	/** Given a TriangleList computes the "optimal" partitioning plane */
	static Plane computePartitioningPlane( List<Triangle> triangles ) {
		return null;
	}

	/** Given a partitioning plane p, partitions a TriangleList into four sets:
	1. A TriangleList in which all triangles lie on the negative side of p
	2. A TriangleList in which all triangles lie on the positive side of p
	3. A TriangleList of those triangles that lie in p, facing the same direction as p
	4. A TriangleList of those triangles that lie in p but face the other direction as p
	The contents of the original TriangleList (pointers to Triangles) will be parceled
	out to the four resulting TriangleList, s.t. that the passed TriangleList will be
	empty after this function has finished. */
	static void partition( List<Triangle> triangles,
					Plane p,
					List<Triangle> negativeSide,
					List<Triangle> positiveSide,
					List<Triangle> sameDir,
					List<Triangle> oppDir ) {
	}

	/** Returns true if t straddles p, false otherwise */
	static boolean straddlesPlane( Triangle t, Plane p ) {
		return false;
	}

	/** Determines if an edge bounded by (x1,y1,z1)->(x2,y2,z2) intersects
	the plane.
	If there's an intersection, the sign of (x1,y1,z1), NEGATIVE or POSITIVE,
	w.r.t. the plane is returned with the intersection (ix,iy,iz) updated.
	Otherwise ZERO is returned. */
	static Sign edgeIntersectsPlane( float x1, float y1, float z1,
					float x2, float y2, float z2,
					Plane plane,
					float ix, float iy, float iz ) {
		// Achtung: ix, iy, iz waren pointer hier!
		return Sign.Zero;
	}

	static int findNextIntersection( int start, Triangle triangle, Plane plane,
					float ix, float iy, float iz,
					Sign sign ) {
		return 0;
	}

	/** Split the triangle t into a triangle and a quadrangle. Note that the
	quadrangle may be degenerate, i.e. nearly a triangle itself
	containsIntersection1 ist true if the triangle contains the predecessor
	vertex of intersection point 1 */
	static Quadrangle splitTriangle( Triangle t, boolean containsV1, int intersection1, int intersection2,
					float ix1, float iy1, float iz1,
					float ix2, float iy2, float iz2 ) {
		return null;
	}

	static void splitQuadrangle( Quadrangle q, Triangle t1, Triangle t2 ) {
	}

	static Sign whichSideIsFaceWRTplane( Triangle t, Plane plane ) {
		return Sign.Zero;
	}

	/** Given a triangle computes the equation of the plane this triangle spans
	and stores the plane coordinates in the triangle */
	static void computePlane( Triangle t ) {
		// Done by the the plane constructor.
		// only adds the triangle to the plane
		// maybe move to the constructor of triangle...
		t.plane = new Plane( t.v[0], t.v[1], t.v[2] );
	}

	/** Determines whether pos is on the positive side of plane
	Returns true if pos is in the positive side of plane, false otherwise */
	static boolean isInPositiveSide( Plane plane, Vector3 pos ) {
		return false;
	}
}
