
package gui;

import org.zetool.container.util.SingleIterator;
import de.tu_berlin.coga.zet.model.Project;
import de.tu_berlin.math.coga.batch.gui.JBatch;
import de.tu_berlin.math.coga.batch.input.Input;
import ds.ProjectLoader;
import gui.GUIControl;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ProjectInput implements Input {

  private final GUIControl control;

  public ProjectInput( GUIControl control ) {
    this.control = control;
  }
  
  @Override
  public Iterator<File> iterator() {
		Project project = control.getZControl().getProject();
    try {
			ProjectLoader.save( project );
		} catch( IOException ex ) {
			Logger.getLogger( JBatch.class.getName() ).log( Level.SEVERE, null, ex );
		}
    return new SingleIterator<>( project.getProjectFile() );
  }
}
