package de.tu_berlin.math.coga.batch.gui.action;

import de.tu_berlin.math.coga.batch.gui.JBatch;
import de.tu_berlin.math.coga.batch.input.Input;
import java.awt.event.ActionEvent;
import java.util.Objects;
import javax.swing.Icon;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class InputAction extends BatchAction {
  private Input input;

  public InputAction( JBatch batch, Input input, String title, Icon icon ) {
    super( batch, title, icon );
    this.input = Objects.requireNonNull( input );
    setEnabled( false );
  }

  @Override
  public void actionPerformed( ActionEvent ae ) {
    batch.addInput( input );
  }
}
