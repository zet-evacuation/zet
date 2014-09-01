package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.output.Output;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class OutputNode extends BatchTreeTableNode<Output> {

  public OutputNode( Output output ) {
    super( output, output.getIcon() );
  }

  @Override
  public String getToolTipText() {
    return getUserObject().getDescription();
  }
}
