/*
 * BspTree.java
 * Created: 14.11.2010, 18:39:11
 */
package opengl.bsp;

import de.tu_berlin.math.coga.math.vectormath.Plane;
import java.util.Queue;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BspTree {
	// pointer to plane that partitions this node
	private Plane partitionPlane;
	// pointer to list of triangles that are embedded in this node
	private Queue<Triangle> sameDir;
	private Queue<Triangle> oppDir;
	// pointer to the negative and positive subtree
	private BspTree negativeSide = null;
	private BspTree positiveSide = null;

	public BspTree( Plane partitionPlane, Queue<Triangle> sameDir, Queue<Triangle> oppDir ) {
		this.partitionPlane = partitionPlane;
		this.sameDir = sameDir;
		this.oppDir = oppDir;
	}

	void setNegativeSide( BspTree negativeSide ) {
		this.negativeSide = negativeSide;
	}

	void setPositiveSide( BspTree positiveSide ) {
		this.positiveSide = positiveSide;
	}

	public Queue<Triangle> getOppDir() {
		return oppDir;
	}

	public Plane getPartitionPlane() {
		return partitionPlane;
	}

	public Queue<Triangle> getSameDir() {
		return sameDir;
	}

	public BspTree getNegativeSide() {
		return negativeSide;
	}

	public BspTree getPositiveSide() {
		return positiveSide;
	}
}
