/**
 * OperationAction.java
 * Created: 27.03.2014, 16:52:48
 */
package de.tu_berlin.math.coga.batch.gui.action;

import de.tu_berlin.math.coga.batch.gui.JBatch;
import de.tu_berlin.math.coga.batch.operations.AbstractOperation;
import de.tu_berlin.math.coga.batch.operations.BasicOptimization;
import de.tu_berlin.math.coga.batch.operations.Operation;
import java.awt.event.ActionEvent;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class OperationAction extends BatchAction {

	  private Class<? extends Operation> operation;
    private String title;

    public OperationAction(JBatch batch, Operation operation, String title) {
        super(batch, title, "algo_24.png");
			System.out.println( "Creating operation action '" + operation + "' with title '" + title + "'" );
        setEnabled(false);
        //this.operation = operation;
        this.title = title;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
			System.out.println( "Adding operation '" + operation + "' with title '" + title + "'" );
        batch.addOperation( new BasicOptimization(), title);
    }

}
