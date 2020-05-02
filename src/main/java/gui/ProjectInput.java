
package gui;

import org.zetool.container.util.SingleIterator;
import de.zet_evakuierung.model.Project;
import org.zetool.components.batch.input.Input;
import de.zet_evakuierung.model.ProjectLoader;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zetool.components.batch.gui.JBatch;

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
