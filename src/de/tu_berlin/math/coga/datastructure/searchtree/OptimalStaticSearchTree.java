/**
 * OptimalStaticSearchTree.java
 * Created: 05.07.2012, 11:24:11
 */
package de.tu_berlin.math.coga.datastructure.searchtree;

import de.tu_berlin.math.coga.algorithm.shortestpath.searchtree.OptimalStaticSearchTreeInstance;
import de.tu_berlin.math.coga.common.util.BinaryTreeToString;
import ds.graph.Edge;
import ds.graph.Node;
import ds.mapping.IdentifiableDoubleMapping;


/**
 *
 * @param <T> 
 * @author Jan-Philipp Kappmeier
 */
public class OptimalStaticSearchTree<T extends Comparable<T>> {

	BinaryTree bt;
	IdentifiableDoubleMapping<Node> nodeData;
	OptimalStaticSearchTreeInstance<T> instance;
	
	public OptimalStaticSearchTree( OptimalStaticSearchTreeInstance<T> i ) {
		bt = new BinaryTree( i.size()+1 );
		nodeData = new IdentifiableDoubleMapping<>( i.size() );
		this.instance = i;
	}

	public void setRoot( int i, double cost ) {
		if( bt.getParent( bt.getNode( i ) ) != null )
			throw new IllegalArgumentException( "New root already has a parent node!" );
		bt.setRoot( i );
		nodeData.set( bt.getNode( i), cost );
	}

	public Node getNode( int mink ) {
		return bt.getNode( mink );
	}

	public Edge setLeft( Node tempRoot, Node leftChild, double childCost ) {
		nodeData.set( leftChild, childCost );
		return bt.setLeft( tempRoot, leftChild );
		
	}

	public Edge setRight( Node tempRoot, Node rightChild, double childCost ) {
		nodeData.set( rightChild, childCost );
		return bt.setRight( tempRoot, rightChild );
	}

	@Override
	public String toString() {
		return BinaryTreeToString.format( bt, nodeData );
	}
}
