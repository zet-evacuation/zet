package de.tu_berlin.math.coga.batch.gui.input;

import de.tu_berlin.math.coga.batch.output.Output;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class OutputListNode extends BatchTreeTableNode<List<Output>> {
  public OutputListNode( List<Output> data ) {
    super( data, new String[0], new ImageIcon( "./icons/dropbox-icon-24.png" ) );
    for( Output operation : data ) {
      add( new OutputNode( operation ) );
    }
  }

  @Override
  public String toString() {
    return "Outputs";
  }

}
