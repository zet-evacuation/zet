/*
 * BspTree.java
 * Created: 14.11.2010, 18:39:11
 */
package opengl.bsp;

import de.tu_berlin.math.coga.math.vectormath.Plane;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BspTree {
	// pointer to plane that partitions this node

	Plane partitionPlane;
	// pointer to list of triangles that are embedded in this node
	Queue<Triangle> sameDir;
	Queue<Triangle> oppDir;
	// pointer to the negative and positive subtree
	BspTree negativeSide;
	BspTree positiveSide;
	static Random rand = new Random();

	/** traverses the bsp and calls the draw() function on each node */
	static void traverse( BspTree bsp ) {
		if( bsp == null )
			return;

		// TODO: traverse bsp and draw each node
		// Note: the current eye position is given
		// in the eye variable

		if( isInPositiveSide( bsp.partitionPlane, BspMain.eye ) ) {
			traverse( bsp.negativeSide );
			// the sign is ignored, so we can set zero (should not be like this, or?
			BspMain.instance.draw( bsp.oppDir, Sign.Zero );
			BspMain.instance.draw( bsp.sameDir, Sign.Zero );
			traverse( bsp.positiveSide );
		} else {
			traverse( bsp.positiveSide );
			// the sign is ignored, so we can set zero
			BspMain.instance.draw( bsp.oppDir, Sign.Zero );
			BspMain.instance.draw( bsp.sameDir, Sign.Zero );
			traverse( bsp.negativeSide );
		}
	}

	/** Given a TriangleList computes a BSP tree. As a result
	the passed TriangleList is automatically destructed since
	triangles have been parceled out to the bsp tree nodes */
	static BspTree constructBsp( Queue<Triangle> triangles ) {

		BspTree root = new BspTree();

		// start the recursion with the root node
		constructBspRec( triangles, root );

		return root;
	}

	static void constructBspRec(Queue<Triangle> triangles, BspTree node) {
		/** TODO: find the best partitioning plane among the list of triangles */
		Plane plane = computePartitioningPlane( triangles );


		/** TODO: partiton the triangles according to that plane. if no triangles
		have been put into some of the sets, the set will simply be empty. However,
		you must not forget to delete the TriangleList objects if they are not used.*/
		Queue<Triangle> neg = new LinkedList<Triangle>();
		Queue<Triangle> pos = new LinkedList<Triangle>();
		Queue<Triangle> same = new LinkedList<Triangle>();
		Queue<Triangle> opp = new LinkedList<Triangle>();

		partition( triangles, plane, neg, pos, same, opp );

		if( neg.isEmpty() ) {
			neg.clear();
			neg = null;
		}
		if( pos.isEmpty() ) {
			pos.clear();
			pos = null;
		}
		if( opp.isEmpty() ) {
			opp.clear();
			opp = null;
		}

		/** all the triangles need to have been parceled out to neg, pos, same, opp */
		assert (triangles.isEmpty());

		/** at least the triangle that defined the partitioning plane must be
		coincident with the partitioning plane */
		assert (!same.isEmpty());

		/** no more contents delete the list itself */
		//delete triangles;
		//triangles = NULL;
		/** TODO: create a new bsp tree node */
		// node must be allocated
		assert (node != null);

		node.partitionPlane = plane;
		node.oppDir = opp;
		node.sameDir = same;

		/** TODO: now compute the negative subtree of the current treenode */
		if( neg != null ) {
			node.negativeSide = new BspTree();
			constructBspRec( neg, node.negativeSide );
		}

		/** TODO: and the positive subtree */
		if( pos != null ) {
			node.positiveSide = new BspTree();
			constructBspRec( pos, node.positiveSide );
		}
	}

	/** Given a TriangleList computes the "optimal" partitioning plane */
	static Plane computePartitioningPlane( Queue<Triangle> triangles ) {
// the best plane so far
		Plane best_plane = null;
		int best_value = -1;

		// algorithm tries to find a compromise between balance and splits

		for( Triangle t : triangles ) {
			//for (std::list<Triangle*>::iterator it=triangles->begin(); it!=triangles->end(); it++) {
			// to speed things up and put some randomness into everything

			if( best_value >= 0 && rand.nextInt() % 2 > 0 )
				continue; //check every other triangle

			Plane plane = t.plane;//(*it)->plane;

			// count the split up triangles and the triangles in front and behind the plane
			int splits = 0;
			int neg = 0;
			int pos = 0;

			for( Triangle tri : triangles ) {
				//for (std::list<Triangle*>::iterator it2=triangles->begin(); it2!=triangles->end(); it2++) {

				//if (*it == *it2)
				if( tri == t )
					continue;

				//Triangle* tri = *it2;

				if( straddlesPlane( tri, plane ) )
					splits++; // the triangle is splitted
				else
					if( isInPositiveSide( plane, tri.v[0] ) )
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

		// return the best plane
		return best_plane;
	}

	static Sign lastSign = Sign.Negative;
	static boolean lastContains;
	static Triangle lastT1;
	static Triangle lastT2;



	/** Given a partitioning plane p, partitions a TriangleList into four sets:
	1. A TriangleList in which all triangles lie on the negative side of p
	2. A TriangleList in which all triangles lie on the positive side of p
	3. A TriangleList of those triangles that lie in p, facing the same direction as p
	4. A TriangleList of those triangles that lie in p but face the other direction as p
	The contents of the original TriangleList (pointers to Triangles) will be parceled
	out to the four resulting TriangleList, s.t. that the passed TriangleList will be
	empty after this function has finished. */
	static void partition( Queue<Triangle> triangles,
					Plane p,
					Queue<Triangle> negativeSide,
					Queue<Triangle> positiveSide,
					Queue<Triangle> sameDir,
					Queue<Triangle> oppDir ) {
/** remove one triangle from the list in each step and distribute it
	to the four other list according to the partitioning plane p. */
	while (!triangles.isEmpty() ) {

		/** coordinates of intersction points */
		double ix1, iy1, iz1;
		double ix2, iy2, iz2;

		Vector3 i1 = new Vector3();// vector könnte aßerhalb der schleife stehen, würde speicher sparen
		Vector3 i2 = new Vector3();

		/** index of the vertex that is the first vertex of the intersecting
		edge. -1 if no intersection */
		int intersection1, intersection2;
		intersection1 = intersection2 = -1;

		/** sign of the starting point of the edge that causes the intersection */
		Sign signV1 = Sign.Negative;//(SIGN)-1;
		Sign signV2 = Sign.Negative;//(SIGN)-1;


		/** get the first triangle from the list and remove it since we
		are going to partition it into one of the four sets */
		Triangle t = triangles.remove();// .get( 0 );//->front();
		//triangles->pop_front();


		/** try if any of the triangles edges intersects with the plane */
		//intersection1 = findNextIntersection(0, t, p, &ix1, &iy1, &iz1, &signV1);
		intersection1 = findNextIntersection( 0, t, p, i1 );
		signV1 = lastSign;

		/** found first intersection, check if we also have a second one */
		if( intersection1 != -1 ) {

			intersection2 = findNextIntersection( (intersection1 + 1) % 3, t, p, i2 );
			signV2 = lastSign;

			/** Due to numerical instabilities, both intersection points may
			have the same sign such as in the case when splitting very close
			to a vertex. This should not count as a split. */
			//Vertex* vtemp1 = new Vertex(ix1, iy1, iz1);
			//Vertex* vtemp2 = new Vertex(ix2, iy2, iz2);
			if( ISVERTEX_EQ( i1, i2 ) )
				intersection2 = -1;

			if( intersection2 != -1 && signV1 == signV2 )
				intersection2 = -1;
		}


		/** found two intersections, need to split the triangle
		and then split any possibly resulting trapezoids into
		another two triangles */
		if( intersection1 != -1 && intersection2 != -1 ) {

			boolean containsV1;
			/** split the triangle into a triangle and a quadrangle */
			Quadrangle q = splitTriangle( t, intersection1, intersection2, i1.x, i1.y, i1.z, i2.x, i2.y, i2.z );
			containsV1 = lastContains;

			splitQuadrangle( q );
			Triangle t1 = lastT1;
			Triangle t2 = lastT2;


			/** copy normal and plane pointer */
			t1.faceNormal = t.faceNormal;
			t1.plane = t.plane;
			if( t2 != null ) {
				t2.faceNormal = t.faceNormal;
				t2.plane = t.plane;
			}

			if (containsV1) {
				if (signV1 == Sign.Positive) {
					positiveSide.add( t );//->push_back(t);
					negativeSide.add( t1 );//->push_back(t1);
					if (t2 != null)
						negativeSide.add( t2 );
				} else if (signV1 == Sign.Negative) {
					negativeSide.add( t ); //->push_back(t);
					positiveSide.add( t1 ); //->push_back(t1);
					if (t2 != null)
						positiveSide.add( t2 );//->push_back(t2);
				}
			}
			else {
				if (signV1 == Sign.Negative) {
					positiveSide.add( t );//->push_back(t);
					negativeSide.add( t1 );//->push_back(t1);
					if (t2 != null)
						negativeSide.add( t2 ); //->push_back(t2);
				}
				else if (signV1 == Sign.Positive) {
					negativeSide.add( t ); //->push_back(t);
					positiveSide.add( t1 ); //->push_back(t1);
					if (t2 != null)
						positiveSide.add( t2 );//->push_back(t2);
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

					if( IS_EQ( t.plane.a, p.a ) && IS_EQ( t.plane.b, p.b ) && IS_EQ( t.plane.c, p.c ) )
						sameDir.add( t );//push_back(t);
					else
						oppDir.add( t );//->push_back(t);
				}
			}
		}

	}

	/** Returns true if t straddles p, false otherwise */
	static boolean straddlesPlane( Triangle t, Plane p ) {
		boolean anyPositive = false;
	boolean anyNegative = false;

	Vector3 v1 = t.v[0];
	Vector3 v2 = t.v[1];
	Vector3 v3 = t.v[2];

	double val;
	Sign sign;

	val = p.a*v1.x + p.b*v1.y + p.c*v1.z + p.d;
	sign= FSIGN( val );
	if (sign == Sign.Negative) anyNegative = true;
	else if (sign == Sign.Positive) anyPositive = true;

	val = p.a*v2.x + p.b*v2.y + p.c*v2.z + p.d;
	sign= FSIGN(val);
	if (sign == Sign.Negative) anyNegative = true;
	else if (sign == Sign.Positive) anyPositive = true;

	val = p.a*v3.x + p.b*v3.y + p.c*v3.z + p.d;
	sign= FSIGN(val);
	if (sign == Sign.Negative) anyNegative = true;
	else if (sign == Sign.Positive) anyPositive = true;


	/* if vertices on both sides of plane then face straddles else not */
	if (anyNegative && anyPositive) return true;
	return false;
	}

	/** Determines if an edge bounded by (x1,y1,z1)->(x2,y2,z2) intersects
	the plane.
	If there's an intersection, the sign of (x1,y1,z1), NEGATIVE or POSITIVE,
	w.r.t. the plane is returned with the intersection (ix,iy,iz) updated.
	Otherwise ZERO is returned. */
	static Sign edgeIntersectsPlane( double x1, double y1, double z1,
					double x2, double y2, double z2,
					Plane plane, Vector3 i ) {
		// Achtung: ix, iy, iz waren pointer hier!
		
		double temp1, temp2;
		int sign1, sign2;		/* must be int since gonna do a bitwise ^ */
		double a= plane.a;
		double b= plane.b;
		double c= plane.c;
		double d= plane.d;

		/* get signs */
		temp1= a*x1 + b*y1 + c*z1 + d;
		if (temp1 < -eps)
			sign1= -1;
		else if (temp1 > eps)
			sign1= 1;
		else {
			/* edges beginning with a 0 sign are not considered valid intersections
			* case 1 & 6 & 7, see Gems III.
			*/
			assert(IS_EQ(temp1,0.0));
			return(Sign.Zero);
		}

		temp2= (a*x2) + (b*y2) + (c*z2) + d;
		if (temp2 < -eps)
			sign2= -1;
		else if (temp2 > eps)
			sign2= 1;
		else {			/* case 8 & 9, see Gems III */
			assert(IS_EQ(temp2,0.0));
			i.x= x2;
			i.y= y2;
			i.z= z2;

			return( (sign1 == -1) ? Sign.Negative : Sign.Positive);
		}

		/* signs different?
		* recall: -1^1 == 1^-1 ==> 1    case 4 & 5, see Gems III
		*         -1^-1 == 1^1 ==> 0    case 2 & 3, see Gems III
		*/
		if( (sign1 ^ sign2) != 0) {
			double dx,dy,dz;
			double denom, tt;

			/* compute intersection point */
			dx= x2-x1;
			dy= y2-y1;
			dz= z2-z1;

			denom= (a*dx) + (b*dy) + (c*dz);
			tt= - ((a*x1) + (b*y1) + (c*z1) + d) / denom;

			i.x= x1 + (tt * dx);
			i.y= y1 + (tt * dy);
			i.z= z1 + (tt * dz);

			assert(sign1 == 1 || sign1 == -1);

			return( (sign1 == -1) ? Sign.Negative : Sign.Positive );
		} else
			return(Sign.Zero);
	}

	static int findNextIntersection( int start, Triangle triangle, Plane plane, Vector3 vi ) {
		assert (start < 3 && start >= 0);
		for( int i = start; i < 3; i++ ) {
			Vector3 v = triangle.v[i];
			int ii = (i + 1) % 3;
			Vector3 vv = triangle.v[ii];
			lastSign = edgeIntersectsPlane( v.x, v.y, v.z, vv.x, vv.y, vv.z, plane, vi );
			if( lastSign != Sign.Zero )
				return i;
		}
		return -1;
	}

	/** Split the triangle t into a triangle and a quadrangle. Note that the
	quadrangle may be degenerate, i.e. nearly a triangle itself
	containsIntersection1 ist true if the triangle contains the predecessor
	vertex of intersection point 1 */
	// containsV1 ist statisch als lastContains
	static Quadrangle splitTriangle( Triangle t, /*boolean containsV1,*/ int intersection1, int intersection2,
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

			lastContains = true;

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
			BspMain.vertices.add( v );

			t.v[(intersection1+1)%3] = v;

			v = new Vector3(ix2, iy2, iz2);
			//v->normal = triOld1->normal;
			//vertices.push_back(v);
			BspMain.vertices.add( v );

			t.v[(intersection1+2)%3] = v;


			/** the quadrangle resulting from the split keeps two
			vertices from the original triangle and gets two new
			vertices (the intersection points) */
			v = new Vector3(ix1, iy1, iz1);
			//v->normal = triOld1->normal;
			//vertices.push_back(v);
			BspMain.vertices.add( v );

			q.v[0] = v;
			q.v[1] = triOld1;
			q.v[2] = triOld2;
			v = new Vector3(ix2, iy2, iz2);
			//v->normal = triOld1->normal;
			//vertices.push_back(v);
			BspMain.vertices.add( v );
			q.v[3] = v;
		} else {

			lastContains = false;

			Vector3 triOld1 = t.v[(intersection1) % 3];
			Vector3 triOld2 = t.v[(intersection2+1) % 3];

			Vector3 v;

			v = new Vector3(ix1, iy1, iz1);
			//v->normal = triOld1->normal;
			//vertices.push_back(v);
			BspMain.vertices.add( v );
			t.v[intersection1] = v;
			v = new Vector3(ix2, iy2, iz2);
			//v->normal = triOld1->normal;
			//vertices.push_back(v);
			BspMain.vertices.add( v );
			t.v[(intersection2+1) % 3] = v;

			v = new Vector3(ix2, iy2, iz2);
			//v->normal = triOld1->normal;
			//vertices.push_back(v);
			BspMain.vertices.add( v );
			q.v[0] = v;
			q.v[1] = triOld2;
			q.v[2] = triOld1;
			v = new Vector3(ix1, iy1, iz1);
			//v->normal = triOld1->normal;
			//vertices.push_back(v);
			BspMain.vertices.add( v );
			q.v[3] = v;
		}

		return q;
	}

	// Triangles are stored as lastT1, lastT2
	static void splitQuadrangle( Quadrangle q/*, Triangle t1, Triangle t2 */) {
		/** now check split the quadrangle into two triangles,
		but first check for numerical problems */

		int numEqual = 0;
		int deleteVertex = -1;
		for (int i = 0; i < 4; i++) {
			if (ISVERTEX_EQ(q.v[i], q.v[(i+1)%4])) {
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
			BspMain.vertices.add( v0dup );
			Vector3 v2dup = new Vector3(q.v[2].x, q.v[2].y, q.v[2].z);
			//v2dup->normal = q->v[0]->normal;
			//vertices.push_back(v2dup);
			BspMain.vertices.add( v2dup );

			lastT1 = new Triangle(q.v[0], q.v[1], q.v[2]);
			lastT2 = new Triangle(v2dup, q.v[3], v0dup);
		} else {

			lastT1 = new Triangle();
			lastT2 = null;

			int vIndex = 0;
			for (int i = 0; i < 4; i++) {

				if (i == deleteVertex) {
					// don't do anything we have the pointer in the vertices list
					// so we will be able to delete it
					//delete[] q->v[i];
				} else {
					lastT1.v[vIndex] = q.v[i];
					vIndex++;
				}
			}
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
	static Sign whichSideIsFaceWRTplane( Triangle t, Plane plane ) {
	Vector3 v = null;
	double value;
	boolean isNeg, isPos;

	isNeg = isPos = false;

	for (int i = 0; i < 3; i++) {
		v = t.v[i];
		value = plane.a*v.x + plane.b*v.y + plane.c*v.z + plane.d;
		if (value < -eps)
			isNeg = true;
		else if (value > eps)
			isPos = true;
		else assert(IS_EQ(value,0.0));
	}

	/** in the very rare case that some vertices slipped thru to other side of
	plane due to round-off errors, execute the above again but count the
	vertices on each side instead and pick the maximum. */
	if (isNeg && isPos) {	/* yes so handle this numerical problem */
		int countNeg, countPos;

		/* count how many vertices are on either side */
		countNeg= countPos= 0;

		for (int i = 0; i < 3; i++) {
			value = plane.a*v.x + plane.b*v.y + plane.c*v.z + plane.d;
			if (value < -eps)
				countNeg++;
			else if (value > eps)
				countPos++;
			else assert(IS_EQ(value,0.0));
		} /* for */

		/* return the side corresponding to the maximum */
		if (countNeg > countPos)
			return(Sign.Negative);
		else if (countPos > countNeg)
			return(Sign.Positive);
	}	else {			/* this is the usual case */
		if (isNeg)
			return(Sign.Negative);
		else if (isPos)
			return(Sign.Positive);
	}
	return(Sign.Zero);
	}

	/** Given a triangle computes the equation of the plane this triangle spans
	and stores the plane coordinates in the triangle */
	void computePlane( Triangle t ) {
		// Done by the the plane constructor.
		// only adds the triangle to the plane
		// maybe move to the constructor of triangle...
		t.plane = new Plane( t.v[0], t.v[1], t.v[2] );
	}

	/** Determines whether pos is on the positive side of plane
	Returns true if pos is in the positive side of plane, false otherwise */
	static boolean isInPositiveSide( Plane plane, Vector3 pos ) {
		return (plane.a*pos.x + plane.b*pos.y + plane.c*pos.z + plane.d > 0);
	}


	final static double eps = 0.000076;
//#define IS_EQ(a,b) ((fabs((double)(a)-(b)) >= (double) TOLER) ? 0 : 1)
//typedef enum {NEGATIVE= -1, ZERO= 0, POSITIVE= 1} SIGN;
//#define FSIGN(f) (((f) < -TOLER) ? NEGATIVE : ((f) > TOLER ? POSITIVE : ZERO))

	static Sign FSIGN( double f ) {
		return f < -eps ? Sign.Negative : f > eps ? Sign.Positive : Sign.Zero;
	}

	static boolean IS_EQ( double a, double b ) {
		return Math.abs( a - b ) < eps;
	}

	static boolean ISVERTEX_EQ( Vector3 v1, Vector3 v2 ) {
		return IS_EQ( v1.x, v2.x ) && IS_EQ( v1.y, v2.y ) && IS_EQ( v1.z, v2.z );
	}
}
