/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package opengl.bsp;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.math.vectormath.Plane;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BinarySpacePartitioningTreeBuilder extends Algorithm<DynamicTriangleMesh,BspTree> {
	final static double eps = 0.000076;
	private Random rand = new Random();
	private Sign tempSign;
	private boolean contains;
	private Triangle newTriangle1;
	private Triangle newTriangle2;
	private int intersectionTemp;


	@Override
	protected BspTree runAlgorithm( DynamicTriangleMesh problem ) {
		// here we have a mesh and a triangle given
		// start the recursion with the root node
		return constructBspRec( problem.getQueue() );
	}

	private BspTree constructBspRec( Queue<Triangle> triangles ) {
		/** find the best partitioning plane among the list of triangles */
		final Plane plane = computePartitioningPlane( triangles );


		/** partition the triangles according to that plane. if no triangles
		have been put into some of the sets, the set will simply be empty. However,
		you must not forget to delete the TriangleList objects if they are not used.*/
		Queue<Triangle> neg = new LinkedList<Triangle>();
		Queue<Triangle> pos = new LinkedList<Triangle>();
		Queue<Triangle> same = new LinkedList<Triangle>();
		Queue<Triangle> opp = new LinkedList<Triangle>();

		partition( triangles, plane, neg, pos, same, opp );

		/** all the triangles need to have been parceled out to neg, pos, same, opp */
		assert (triangles.size() != 0);

		/** at least the triangle that defined the partitioning plane must be
		coincident with the partitioning plane */
		assert (same.size() != 0);

		BspTree node = new BspTree( plane, same, opp );

		/** now compute the negative subtree of the current treenode */
		if( !neg.isEmpty() )
			node.setNegativeSide( constructBspRec( neg ) );

		/** TODO: and the positive subtree */
		if( !pos.isEmpty() )
			node.setPositiveSide( constructBspRec( pos ) );

		return node;
	}

	/** Given a TriangleList computes the "optimal" partitioning plane */
	private Plane computePartitioningPlane( Queue<Triangle> triangles ) {
		// the best plane so far
		Plane best_plane = null;
		int best_value = -1;

		// algorithm tries to find a compromise between balance and splits

		for( Triangle t : triangles ) {
			// to speed things up and put some randomness into everything
			if( best_value >= 0 && rand.nextInt() % 2 > 0 )
				continue; //check every other triangle

			Plane plane = t.plane;

			// count the split up triangles and the triangles in front and behind the plane
			int splits = 0;
			int neg = 0;
			int pos = 0;

			for( Triangle tri : triangles ) {
				if( tri == t )	// skip current triangle
					continue;

				if( straddlesPlane( tri, plane ) )
					splits++; // the triangle is splitted
				else if( plane.isInPositiveSide( tri.v[0] ) ) //isInPositiveSide( plane, tri.v[0] ) )
					pos++;
				else
					neg++;
			}

			// calculate the "value" of the plane, splitting and balance
			int value = 2 * splits + Math.abs( neg - pos );

			// if the current plane is better then the current best, replace it
			if( best_value < 0 || value < best_value ) {
				best_plane = plane;
				best_value = value;
			}
		}

		return best_plane;
	}

	/** Given a partitioning plane p, partitions a TriangleList into four sets:
	1. A TriangleList in which all triangles lie on the negative side of p
	2. A TriangleList in which all triangles lie on the positive side of p
	3. A TriangleList of those triangles that lie in p, facing the same direction as p
	4. A TriangleList of those triangles that lie in p but face the other direction as p
	The contents of the original TriangleList (pointers to Triangles) will be parceled
	out to the four resulting TriangleList, s.t. that the passed TriangleList will be
	empty after this function has finished. */
	private void partition( Queue<Triangle> triangles,
					Plane p,
					Queue<Triangle> negativeSide,
					Queue<Triangle> positiveSide,
					Queue<Triangle> sameDir,
					Queue<Triangle> oppDir ) {
/** remove one triangle from the list in each step and distribute it
	to the four other list according to the partitioning plane p. */
	//while ( triangles.faceCount() > 0 ) {
	while ( triangles.size() > 0 ) {

		//Vector3 i1 = new Vector3();// vector könnte aßerhalb der schleife stehen, würde speicher sparen
		//Vector3 i2 = new Vector3();

		/** index of the vertex that is the first vertex of the intersecting
		edge. -1 if no intersection */
		//;
		//intersection1 = intersection2 = -1;

		/** sign of the starting point of the edge that causes the intersection */
		Sign signV1 = Sign.Negative;//(SIGN)-1;
		Sign signV2 = Sign.Negative;//(SIGN)-1;


		/** get the first triangle from the list and remove it since we
		are going to partition it into one of the four sets */
		Triangle t = triangles.remove();// .get( 0 );//->front();
		//triangles->pop_front();


		/** try if any of the triangles edges intersects with the plane */
		//intersection1 = findNextIntersection(0, t, p, &ix1, &iy1, &iz1, &signV1);
		Vector3 i1 = findNextIntersection( 0, t, p );
		int intersection1 = intersectionTemp;
		signV1 = tempSign; // set by findNextIntersection

		int intersection2 = -1;
		Vector3 i2 = null;
		/** found first intersection, check if we also have a second one */
		if( intersection1 != -1 ) {

			i2 = findNextIntersection( (intersection1 + 1) % 3, t, p );
			intersection2 = intersectionTemp;
			signV2 = tempSign; // set by findNextIntersection

			/** Due to numerical instabilities, both intersection points may
			have the same sign such as in the case when splitting very close
			to a vertex. This should not count as a split. */
			//Vertex* vtemp1 = new Vertex(ix1, iy1, iz1);
			//Vertex* vtemp2 = new Vertex(ix2, iy2, iz2);
			if( i2 != null && i1.equals( i2, eps ) ) // ISVERTEX_EQ( i1, i2 ) )
				intersection2 = -1;

			if( intersection2 != -1 && signV1 == signV2 )
				intersection2 = -1;
		}

		/** found two intersections, need to split the triangle
		and then split any possibly resulting trapezoids into
		another two triangles */
		if( intersection1 != -1 && intersection2 != -1 ) {

			//boolean containsV1;
			/** split the triangle into a triangle and a quadrangle */
			Quadrangle q = splitTriangle( t, intersection1, intersection2, i1.x, i1.y, i1.z, i2.x, i2.y, i2.z );
			//containsV1 = lastContains;

			splitQuadrangle( q );
			// newTriangle is set here


			/** copy normal and plane pointer */
			newTriangle1.faceNormal = t.faceNormal;
			newTriangle1.plane = t.plane;
			if( newTriangle2 != null ) {
				newTriangle2.faceNormal = t.faceNormal;
				newTriangle2.plane = t.plane;
			}

			if (contains) {
				if (signV1 == Sign.Positive) {
					positiveSide.add( t );//->push_back(t);
					negativeSide.add( newTriangle1 );//->push_back(t1);
					if (newTriangle2 != null)
						negativeSide.add( newTriangle2 );
				} else if (signV1 == Sign.Negative) {
					negativeSide.add( t ); //->push_back(t);
					positiveSide.add( newTriangle1 ); //->push_back(t1);
					if (newTriangle2 != null)
						positiveSide.add( newTriangle2 );//->push_back(t2);
				}
			}
			else {
				if (signV1 == Sign.Negative) {
					positiveSide.add( t );//->push_back(t);
					negativeSide.add( newTriangle1 );//->push_back(t1);
					if (newTriangle2 != null)
						negativeSide.add( newTriangle2 ); //->push_back(t2);
				}
				else if (signV1 == Sign.Positive) {
					negativeSide.add( t ); //->push_back(t);
					positiveSide.add( newTriangle1 ); //->push_back(t1);
					if (newTriangle2 != null)
						positiveSide.add( newTriangle2 );//->push_back(t2);
				}
			}
		}
 /** no intersection */
			else {
				Sign side = whichSideIsFaceWRTplane( t, p );
				if( side == Sign.Negative )
					negativeSide.add( t );//->push_back(t);
				else if( side == Sign.Positive )
					positiveSide.add( t );//->push_back(t);
				else {
					assert (side == Sign.Zero);

					if( t.plane.getNormal().equals( p.getNormal(), eps ) )// IS_EQ( t.plane.getA(), p.getA() ) && IS_EQ( t.plane.getB(), p.getB() ) && IS_EQ( t.plane.getC(), p.getC() ) )
						sameDir.add( t );//push_back(t);
					else
						oppDir.add( t );//->push_back(t);
				}
			}
		}

	}

	/** Returns true if t straddles p, false otherwise */
	private boolean straddlesPlane( Triangle t, Plane p ) {
		boolean anyPositive = false;
		boolean anyNegative = false;

		Vector3 v1 = t.v[0];
		Vector3 v2 = t.v[1];
		Vector3 v3 = t.v[2];

		double val;
		Sign sign;

		val = p.getA() * v1.x + p.getB() * v1.y + p.getC() * v1.z + p.getD();
		sign = val < -eps ? Sign.Negative : val > eps ? Sign.Positive : Sign.Zero;
		if( sign == Sign.Negative )
			anyNegative = true;
		else if( sign == Sign.Positive )
			anyPositive = true;

		val = p.getA() * v2.x + p.getB() * v2.y + p.getC() * v2.z + p.getD();
		sign = val < -eps ? Sign.Negative : val > eps ? Sign.Positive : Sign.Zero;
		if( sign == Sign.Negative )
			anyNegative = true;
		else if( sign == Sign.Positive )
			anyPositive = true;

		val = p.getA() * v3.x + p.getB() * v3.y + p.getC() * v3.z + p.getD();
		sign = val < -eps ? Sign.Negative : val > eps ? Sign.Positive : Sign.Zero;
		if( sign == Sign.Negative )
			anyNegative = true;
		else if( sign == Sign.Positive )
			anyPositive = true;


		/* if vertices on both sides of plane then face straddles else not */
		if( anyNegative && anyPositive )
			return true;
		return false;
	}

	private Vector3 findNextIntersection( int start, Triangle triangle, Plane plane ) {
		assert (start < 3 && start >= 0);
		for( int i = start; i < 3; i++ ) {
			Vector3 v = triangle.v[i];
			int ii = (i + 1) % 3;
			Vector3 vv = triangle.v[ii];
			Vector3 newIntersection = plane.intersectionPoint( v, vv );
			if( newIntersection != null ) {
				intersectionTemp = i;
				tempSign = plane.isInPositiveSide( v ) ? Sign.Positive : Sign.Negative;
				return newIntersection;
			}
			
			//tempSign = edgeIntersectsPlane( v, vv, plane );
			//if( tempSign != Sign.Zero )
			//	return i;
		}
		intersectionTemp = -1;
		tempSign = Sign.Zero;
		return null;
	}

		/** Split the triangle t into a triangle and a quadrangle. Note that the
	quadrangle may be degenerate, i.e. nearly a triangle itself
	containsIntersection1 ist true if the triangle contains the predecessor
	vertex of intersection point 1 */
	// containsV1 ist statisch als lastContains
	private Quadrangle splitTriangle( Triangle t, int intersection1, int intersection2,
					double ix1, double iy1, double iz1,
					double ix2, double iy2, double iz2 ) {

	/** Split a triangle into a triangle and a quadrangle.
		The resulting quadrangle will itself be split into two
		triangles again.
		Note that splitting the resulting quadrangle into two
		triangles may be numerically problematic if we have split
		the original triangle near a vertex.
		We detect this case (two vertices of the quadrangle below
		a certain distance threshold) and merge the problematic
		vertices into a single one thus generating a triangle */
		Quadrangle q = new Quadrangle();

		if (t.v[(intersection1+2)%3] == t.v[intersection2%3]) {
			contains = true;

			/** get a pointer to the vertices of the triangle that
			will be modified */
			Vector3 triOld1 = t.v[(intersection1+1) % 3];
			Vector3 triOld2 = t.v[(intersection1+2) % 3];


			/** the triangle resulting from the split keeps one old
			vertex from the original triangle and gets two new
			vertices (the intersection points) */
			Vector3 v;

			v = new Vector3(ix1, iy1, iz1);
			//v->normal = triOld1->normal;
			//vertices .push_back(v);
//			BspMain.vertices.add( v );

			t.v[(intersection1+1)%3] = v;

			v = new Vector3(ix2, iy2, iz2);
			//v->normal = triOld1->normal;
			//vertices.push_back(v);
//			BspMain.vertices.add( v );

			t.v[(intersection1+2)%3] = v;


			/** the quadrangle resulting from the split keeps two
			vertices from the original triangle and gets two new
			vertices (the intersection points) */
			v = new Vector3(ix1, iy1, iz1);
			//v->normal = triOld1->normal;
			//vertices.push_back(v);
//		BspMain.vertices.add( v );

			q.v[0] = v;
			q.v[1] = triOld1;
			q.v[2] = triOld2;
			v = new Vector3(ix2, iy2, iz2);
			//v->normal = triOld1->normal;
			//vertices.push_back(v);
//			BspMain.vertices.add( v );
			q.v[3] = v;
		} else {
			contains = false;

			Vector3 triOld1 = t.v[(intersection1) % 3];
			Vector3 triOld2 = t.v[(intersection2+1) % 3];

			Vector3 v;

			v = new Vector3(ix1, iy1, iz1);
			//v->normal = triOld1->normal;
			//vertices.push_back(v);
//			BspMain.vertices.add( v );
			t.v[intersection1] = v;
			v = new Vector3(ix2, iy2, iz2);
			//v->normal = triOld1->normal;
			//vertices.push_back(v);
//			BspMain.vertices.add( v );
			t.v[(intersection2+1) % 3] = v;

			v = new Vector3(ix2, iy2, iz2);
			//v->normal = triOld1->normal;
			//vertices.push_back(v);
//			BspMain.vertices.add( v );
			q.v[0] = v;
			q.v[1] = triOld2;
			q.v[2] = triOld1;
			v = new Vector3(ix1, iy1, iz1);
			//v->normal = triOld1->normal;
			//vertices.push_back(v);
//			BspMain.vertices.add( v );
			q.v[3] = v;
		}

		return q;
	}

	// Triangles are stored as lastT1, lastT2
	private void splitQuadrangle( Quadrangle q/*, Triangle t1, Triangle t2 */) {
		/** now check split the quadrangle into two triangles,
		but first check for numerical problems */

		int numEqual = 0;
		int deleteVertex = -1;
		for (int i = 0; i < 4; i++) {
			if ( q.v[i].equals( q.v[(i+1)%4], eps ) ) { //ISVERTEX_EQ(q.v[i], q.v[(i+1)%4])) {
				numEqual++;
				/** mark the duplicate for later deletion */
				deleteVertex = i;
			}
		}

		/** we only expect a maximum of one pair of vertices being equal */
		assert(numEqual <= 1);


		/** no numerical problems, can split the quadrangle into two triangles */
		if (numEqual == 0) {

			Vector3 v0dup = new Vector3(q.v[0].x, q.v[0].y, q.v[0].z);
			//v0dup->normal = q->v[0]->normal;
			//vertices.push_back(v0dup);
//			BspMain.vertices.add( v0dup );
			Vector3 v2dup = new Vector3(q.v[2].x, q.v[2].y, q.v[2].z);
			//v2dup->normal = q->v[0]->normal;
			//vertices.push_back(v2dup);
//			BspMain.vertices.add( v2dup );

			newTriangle1 = new Triangle(q.v[0], q.v[1], q.v[2]);
			newTriangle2 = new Triangle(v2dup, q.v[3], v0dup);
		} else {

			//newTriangle1;// = new Triangle();
			newTriangle2 = null;
			Vector3[] triangleVertices = new Vector3[3];

			int vIndex = 0;
			for (int i = 0; i < 4; i++) {

				if (i == deleteVertex) {
					// don't do anything we have the pointer in the vertices list
					// so we will be able to delete it
				} else {
					triangleVertices[vIndex] = q.v[i];
					//newTriangle1.v[vIndex] = q.v[i];
					vIndex++;
				}
			}
			newTriangle1 = new Triangle( triangleVertices[0], triangleVertices[1], triangleVertices[2] );
		}
	}

	/* Determines which side a face is with respect to a plane.
	 *
	 * However, due to numerical problems, when a face is very close to the plane,
	 * some vertices may be misclassified.
	 * There are several solutions, two of which are mentioned here:
	 *    1- classify the one vertex furthest away from the plane, (note that
	 *       one need not compute the actual distance) and use that side.
	 *    2- count how many vertices lie on either side and pick the side
	 *       with the maximum. (this is the one implemented).
	 */
	private Sign whichSideIsFaceWRTplane( Triangle t, Plane plane ) {
		Vector3 v = null;
		double value;
		boolean isNeg, isPos;

		isNeg = isPos = false;

		for( int i = 0; i < 3; i++ ) {
			v = t.v[i];
			value = plane.getA() * v.x + plane.getB() * v.y + plane.getC() * v.z + plane.getD();
			if( value < -eps )
				isNeg = true;
			else if( value > eps )
				isPos = true;
			else
				assert ( Math.abs( value ) < eps ); // IS_EQ( value, 0.0 ));
		}

		/** in the very rare case that some vertices slipped thru to other side of
		plane due to round-off errors, execute the above again but count the
		vertices on each side instead and pick the maximum. */
		if( isNeg && isPos ) {	/* yes so handle this numerical problem */
			int countNeg, countPos;

			/* count how many vertices are on either side */
			countNeg = countPos = 0;

			for( int i = 0; i < 3; i++ ) {
				value = plane.getA() * v.x + plane.getB() * v.y + plane.getC() * v.z + plane.getD();
				if( value < -eps )
					countNeg++;
				else if( value > eps )
					countPos++;
				else
					assert ( Math.abs( value ) < eps ); // IS_EQ( value, 0.0 ));
			} /* for */

			/* return the side corresponding to the maximum */
			if( countNeg > countPos )
				return (Sign.Negative);
			else if( countPos > countNeg )
				return (Sign.Positive);
		} else			/* this is the usual case */

			if( isNeg )
				return (Sign.Negative);
			else if( isPos )
				return (Sign.Positive);
		return (Sign.Zero);
	}



}
