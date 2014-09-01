
package de.tu_berlin.math.coga.batch.gui.action;

import de.tu_berlin.math.coga.batch.Computation;
import de.tu_berlin.math.coga.batch.ComputationList;
import de.tu_berlin.math.coga.batch.gui.JBatch;
import de.tu_berlin.math.coga.batch.input.InputFile;
import de.tu_berlin.math.coga.batch.input.InputList;
import de.tu_berlin.math.coga.batch.operations.Operation;
import de.tu_berlin.math.coga.batch.operations.OperationList;
import de.tu_berlin.math.coga.batch.output.Output;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

/**
 *
 * @author Martin Groß
 */
public class RunComputationAction extends BatchAction {

	public RunComputationAction( JBatch batch ) {
		super( batch, "Run computation", "play_24.png" );
	}

	@Override
	public void actionPerformed( ActionEvent ae ) {
		ComputationList clist = batch.getComputationList();

		for( Computation c : clist ) {
			System.out.println( c );
			InputList ilist = c.getInput();
			if( ilist.size() == 0 )
				return; // keine operationen zu tun

			OperationList oList = c.getOperations();
			if( oList.size() == 0 )
				return; // keine operation zu tun

			// prüfen, ob input für die operation geeignet ist
      LinkedList<Object> products = new LinkedList<>();
			for( InputFile i : ilist ) {
				System.out.println( "Checking input '" + i + "'" );
				for( Operation o : oList ) {
					System.out.println( "    Operation: " + o );
					if( o.consume( i.getReader() ) ) {
						System.out.println( "        Run " + o + " on input " + i );

						// we actually start an operation
						o.run();

            products.add( o.getProduced() );

					} else {
						System.out.println( "        Skip input" + i + "!" );
					}
				}
			}

      // Check outputs
      System.out.println( "Outputting results." );
      for( Output o : c.getOutputs() ) {
        for( Object prod : products ) {
          if( o.consumes( prod.getClass() ) ) {
            System.out.println( o + " consumes " + prod );
            o.consume( prod );
          }
        }
      }

		}
	}

}
