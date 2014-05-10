/**
 * NodePositionMapping.java
 * Created: 17.08.2010, 17:17:20
 */
package de.tu_berlin.math.coga.zet.viewer;

import de.tu_berlin.math.coga.math.vectormath.Vector3;
import de.tu_berlin.coga.container.mapping.IdentifiableObjectMapping;
import ds.graph.Node;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class NodePositionMapping extends IdentifiableObjectMapping<Node, Vector3> {

	public NodePositionMapping() {
		super( 0 );
	}

	public NodePositionMapping( int domainSize ) {
		super( domainSize );
	}
}
