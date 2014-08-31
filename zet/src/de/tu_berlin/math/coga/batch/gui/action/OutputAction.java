
package de.tu_berlin.math.coga.batch.gui.action;

import de.tu_berlin.math.coga.batch.gui.JBatch;
import de.tu_berlin.math.coga.batch.output.Output;
import java.awt.event.ActionEvent;
import java.util.Objects;
import javax.swing.Icon;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class OutputAction extends BatchAction {
  private Output output;

  public OutputAction( JBatch batch, Output output, String title, Icon icon ) {
    super( batch, title, icon );
    this.output = Objects.requireNonNull( output );
    setEnabled( false );
  }

  @Override
  public void actionPerformed( ActionEvent ae ) {
    batch.addOutput( output );
  }
}
