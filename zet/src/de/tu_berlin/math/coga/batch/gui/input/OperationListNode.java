/**
 * OperationListNode.java
 * Created: 27.03.2014, 17:17:51
 */
package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.operations.Operation;
import de.tu_berlin.math.coga.batch.operations.OperationList;
import javax.swing.ImageIcon;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class OperationListNode extends BatchTreeTableNode<OperationList> {
    public OperationListNode(OperationList data) {
        super(data, new String[0], new ImageIcon("./icons/gear_24.png"));
        for (Operation operation : data) {
            add( new OperationNode( operation ));
        }
    }

    @Override
    public String toString() {
        return "Operations";
    }

}
