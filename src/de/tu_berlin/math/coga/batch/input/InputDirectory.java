package de.tu_berlin.math.coga.batch.input;

import de.tu_berlin.math.coga.batch.gui.dialog.AddDirectoryWizard;
import java.awt.Component;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 * @author Jan-Philipp Kappmeier
 */
public class InputDirectory extends InputFiles {

  public InputDirectory( Component parent ) {
    super( parent );
  }

  @Override
  public Iterator<File> iterator() {
    final AddDirectoryWizard wizard = new AddDirectoryWizard( getParent() );
    List<File> files = new LinkedList<>();
    wizard.setVisible( true );
    if( wizard.isAccepted() ) {
      files.addAll( addInputFiles( wizard.getSelectedFiles(), wizard.isRecursive(), wizard.isFollowingLinks() ) );
    }
    return files.iterator();    
  }
}
