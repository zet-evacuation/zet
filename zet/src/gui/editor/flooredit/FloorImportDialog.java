/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * You should have received aclButton copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package gui.editor.flooredit;

import de.tu_berlin.math.coga.zet.ZETLocalization2;
import de.tu_berlin.coga.zet.model.Project;
import de.tu_berlin.coga.zet.model.Floor;
import de.tu_berlin.math.coga.components.framework.Button;
import gui.GUIOptionManager;
import info.clearthought.layout.TableLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import org.zetool.common.localization.Localization;
import ds.ProjectLoader;
import gui.GUIControl;
import gui.ZETLoader;

/**
 * The class {@code FloorImportDialog} allows importing floors from other
 * evacuation projects.
 * @author Jan-Philipp Kappmeier
 */
public class FloorImportDialog extends JDialog {
	/** The localization class. */
	private static final Localization loc = ZETLocalization2.loc;
	/** The elements of the floor list. */
	private Vector<Floor> floors = new Vector<>();
	/** The list of floors available in the newly loaded project. */
	private JList list;
	/** The project loaded in the editor. */
	private Project project;
	private final JFrame owner;

	/**
	 * Creates aclButton new instance of {@code FloorImportDialog}.
	 * @param owner the parent window
	 * @param project the project to which the floor is added
	 * @param title the title of the floor import dialog
	 * @param width the width of the dialog window
	 * @param height the height of the dialog window
	 */
	public FloorImportDialog( JFrame owner, Project project, String title, int width, int height ) {
		super( owner, title, true );
		this.owner = owner;
		this.project = project;

		initComponents();

		pack();

		setSize( width, height );
		setLocation( owner.getX() + (owner.getWidth() - width) / 2, owner.getY() + (owner.getHeight() - height) / 2 );
	}

	/**
	 * Initializes the components in the dialog window and places them.
	 */
	private void initComponents() {
		final int space = 16;
		final double size[][] = // Columns
						{
			{space, TableLayout.FILL, space, TableLayout.PREFERRED, TableLayout.FILL, TableLayout.PREFERRED, space},
			//Rows
			{space,
				TableLayout.PREFERRED, // Load button
				space,
				TableLayout.PREFERRED, // Info text
				space,
				TableLayout.FILL, // Fill space for the list-box
				TableLayout.PREFERRED, // Import/Close buttons
				space
			}
		};

		this.setLayout( new TableLayout( size ) );

		list = new JList();
		final JButton btnLoad = Button.newButton( "Laden", aclButton, "load", "Ein ZET-Projekt zum Importieren öffnen" );
		final JButton btnImport = Button.newButton( "Importieren", aclButton, "import", "Ausgewählte Stockwerke importieren" );
		final JButton btnClose = Button.newButton( "Schließen", aclButton, "close", "Dialog schließen" );
		final JLabel lblInfo = new JLabel( "<html>Import-Funktion ist Beta! Hinzufügen funktioniert nur für einen Floor gleichzeitig. Hinzugefügte Etagen dürfen keine Stockwerkübergänge enthalten!</html>" );

		// Add Components:
		add( list, "1,1,1,6" );
		add( lblInfo, "3,3,5,3" );
		add( btnLoad, "3, 1" );
		add( btnImport, "3, 6" );
		add( btnClose, "5, 6" );
	}
	private ActionListener aclButton = new ActionListener() {
		private JFileChooser jfcProject;

		{
			jfcProject = new JFileChooser( GUIOptionManager.getImportPath() );
			jfcProject.setFileFilter( GUIControl.getProjectFilter() );
			jfcProject.setAcceptAllFileFilterUsed( false );
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			if( e.getActionCommand().equals( "load" ) ) {
				if( jfcProject.showOpenDialog( owner ) == JFileChooser.APPROVE_OPTION )
					GUIOptionManager.setImportPath( jfcProject.getCurrentDirectory().getPath() );
				try {
					//Project loaded = Project.load(jfcProject.getSelectedFile());
					Project loaded = ProjectLoader.load( jfcProject.getSelectedFile() );
					floors.clear();
					for( Floor floor : loaded.getBuildingPlan().getFloors() )
						floors.add( floor );
					list.setListData( floors );
				} catch( Exception ex ) {
					JOptionPane.showMessageDialog( null,
									loc.getString( "gui.editor.JEditor.error.loadError" ),
									loc.getString( "gui.editor.JEditor.error.loadErrorTitle" ),
									JOptionPane.ERROR_MESSAGE );
					ex.printStackTrace( System.err );
					ZETLoader.sendMessage( loc.getString( "gui.editor.JEditor.message.loadError" ) );
				}
			} else if( e.getActionCommand().equals( "import" ) ) {
				final Floor f = (Floor) list.getSelectedValue();
				final Floor fc = f.clone();
				final int max = project.getBuildingPlan().floorCount() + 1;
				int number = 0;
				while( !project.getBuildingPlan().addFloor( fc, f.getName() + "_" + number++ ) && number <= max );
			} else if( e.getActionCommand().equals( "close" ) )
				dispose();
		}
	};
}
