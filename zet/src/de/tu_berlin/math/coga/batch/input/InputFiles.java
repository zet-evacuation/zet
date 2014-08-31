
package de.tu_berlin.math.coga.batch.input;

import de.tu_berlin.math.coga.batch.gui.dialog.AddFileDialog;
import java.awt.Component;
import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFileChooser;

/**
 *
 * @author Martin Groß
 * @author Jan-Philipp Kappmeier
 */
public class InputFiles implements Input {
  private AddFileDialog dialog;
  private Component parent;

  public InputFiles( Component parent ) {
    this.parent = parent;
  }

  protected Component getParent() {
    return parent;
  }

  @Override
  public Iterator<File> iterator() {
    if( dialog == null ) {
      dialog = new AddFileDialog();//batch.getComputation().getType());
    }
    int decision = dialog.showOpenDialog( parent );
    if( decision == JFileChooser.APPROVE_OPTION ) {
      List<File> files = addInputFiles( dialog.getSelectedFiles(), false, false );
      return files.iterator();
    }
    return Collections.emptyIterator();
  }

  protected List<File> addInputFiles( File[] selectedFiles, boolean recursive, boolean followingLinks ) {
    FileCrawler crawler = new FileCrawler( recursive, followingLinks );
    List<String> extensions = FileFormat.getAllKnownExtensions();
    List<File> files = new LinkedList<>();
    for( File file : selectedFiles ) {
      if( file.isDirectory() ) {
        files.addAll( crawler.listFiles( file, extensions ) );
      } else if( file.isFile() ) {
        files.add( file );
      }
    }
    return files;
  }
}
