/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
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
