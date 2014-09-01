
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.operations.AtomicOperation;
import de.tu_berlin.math.coga.batch.operations.Operation;
import javax.swing.ImageIcon;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class OperationNode extends BatchTreeTableNode<Operation<?,?>> {
    public OperationNode(Operation<?,?> operation) {
      super( operation, new String[0], new ImageIcon("./icons/algo_24.png") );
			for( AtomicOperation<?,?> ao : operation.getAtomicOperations() ) {
				add(new OperationAlgorithmSelectNode( ao ) );
			}
    }
}
