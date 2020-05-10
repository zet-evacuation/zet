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
package zet.gui.main.toolbar;

import batch.BatchResult;
import batch.BatchResultEntry;
import org.zetool.common.datastructure.NamedIndex;
import org.zetool.common.localization.Localization;
import org.zetool.common.localization.Localized;
import gui.GUIControl;
import gui.ZETLoader;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import org.zetool.components.framework.Button;
import statistic.graph.Controller;
import zet.gui.GUILocalization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JStatisticGraphToolBar extends JToolBar implements ActionListener, Localized {

	private final GUIControl control;
	/** The localization class. */
	static final Localization loc = GUILocalization.loc;
	private JButton btnExit;
	private JButton btnOpenResults;
	private JButton btnSaveResults;
	private JLabel labelBatchName;
	private BatchResultEntryGRSComboBoxModel entryModelGraph;

	public JStatisticGraphToolBar( GUIControl control ) {
		this.control = control;
		createStatisticsToolBar();
		control.setGraphStatisticToolBar( this );
	}

	/**
	 * Creates the {@code JToolBar} for the statistic view.
	 */
	private void createStatisticsToolBar() {
		loc.setPrefix( "gui.toolbar." );

		btnExit = Button.newButton( ZETIconSet.Exit.icon(), this, "exit", loc.getString( "Exit" ) );
		add( btnExit );
		addSeparator();

		btnOpenResults = Button.newButton( ZETIconSet.Open.icon(), null, "loadBatchResult", loc.getString( "Open" ) );
		add( btnOpenResults );
		btnSaveResults = Button.newButton( ZETIconSet.Save.icon(), null, "saveResultAs", loc.getString( "Save" ) );
		add( btnSaveResults );
		addSeparator();

		labelBatchName = new JLabel( loc.getString( "Visualization.BatchName" ) );
		add( labelBatchName );
		entryModelGraph = new BatchResultEntryGRSComboBoxModel();
		JComboBox cbxBatchEntry = new JComboBox( entryModelGraph );
		cbxBatchEntry.setMaximumRowCount( 10 );
		cbxBatchEntry.setMaximumSize( new Dimension( 250, cbxBatchEntry.getPreferredSize().height ) );
		cbxBatchEntry.setPreferredSize( new Dimension( 250, cbxBatchEntry.getPreferredSize().height ) );
		cbxBatchEntry.setAlignmentX( 0 );
		add( cbxBatchEntry );
		loc.setPrefix( "" );
	}

	public void actionPerformed( ActionEvent e ) {
		if( e.getActionCommand().equals( "exit" ) )
			// quits the program
			control.exit();
		else if( e.getActionCommand().equals( "loadBatchResult" ) ) {
			// nothing. see source code in JZETMenuBar
		} else if( e.getActionCommand().equals( "saveResultsAs" ) ) {
			// nothing. see source code in JZETMenuBar
		} else
			ZETLoader.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
	}

	public void localize() {
		loc.setPrefix( "gui.toolbar." );
		labelBatchName.setText( loc.getString( "Visualization.BatchName" ) );
		btnExit.setToolTipText( loc.getString( "Exit" ) );
		btnSaveResults.setToolTipText( loc.getString( "Save" ) );
		btnOpenResults.setToolTipText( loc.getString( "Open" ) );
		loc.clearPrefix();
	}

	public void rebuild( BatchResult result ) {
		entryModelGraph.rebuild( result );
	}

	/**
	 * This class serves as a model for the JComboBox that contains the
	 * BatchResultEntries for the Graph statistics Tab.
	 */
	private class BatchResultEntryGRSComboBoxModel extends DefaultComboBoxModel {

		BatchResult result;

		public void rebuild( BatchResult result ) {
			this.result = result;

			removeAllElements();
			int index = 0;
			for( String e : result.getEntryNames() )
				super.addElement( new NamedIndex( e, index++ ) );
		}

		@Override
		public void setSelectedItem( Object object ) {
			super.setSelectedItem( object );

			BatchResultEntry entry = (BatchResultEntry) getSelectedItem();
			Controller.getInstance().setFlow( entry.getGraph(), entry.getFlow() );
		}

		@Override
		public Object getSelectedItem() {
			try {
				if( result != null && super.getSelectedItem() != null )
					return result.getResult( ((NamedIndex) super.getSelectedItem()).getIndex() );
				else
					return null;
			} catch( IOException ex ) {
				ZETLoader.sendError( "Error while loading temp file: " + ex.getLocalizedMessage() );
				return null;
			}
		}
	}
}
