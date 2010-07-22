/**
 * JStatisticGraphToolBar.java
 * Created: 22.07.2010 11:41:46
 */
package zet.gui.components.toolbar;

import batch.BatchResult;
import batch.BatchResultEntry;
import de.tu_berlin.math.coga.common.localization.Localization;
import de.tu_berlin.math.coga.common.localization.Localized;
import gui.Control;
import gui.ZETMain;
import gui.components.NamedIndex;
import gui.components.framework.Button;
import gui.components.framework.IconSet;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import statistic.graph.Controller;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JStatisticGraphToolBar extends JToolBar implements ActionListener, Localized {

	private final Control control;
	/** The localization class. */
	static final Localization loc = Localization.getInstance();
	private JButton btnExit;
	private JButton btnOpenResults;
	private JButton btnSaveResults;
	private JLabel labelBatchName;
	private BatchResultEntryGRSComboBoxModel entryModelGraph;

	public JStatisticGraphToolBar( Control control ) {
		this.control = control;
		createStatisticsToolBar();
		control.setGraphStatisticToolBar( this );
	}

	/**
	 * Creates the <code>JToolBar</code> for the statistic view.
	 */
	private void createStatisticsToolBar() {
		loc.setPrefix( "gui.editor.JEditor." );

		btnExit = Button.newButton( IconSet.Exit, this, "exit", loc.getString( "toolbarTooltipExit" ) );
		add( btnExit );
		addSeparator();

		btnOpenResults = Button.newButton( IconSet.Open, null, "loadBatchResult", loc.getString( "toolbarTooltipOpen" ) );
		add( btnOpenResults );
		btnSaveResults = Button.newButton( IconSet.Save, null, "saveResultAs", loc.getString( "toolbarTooltipSave" ) );
		add( btnSaveResults );
		addSeparator();

		labelBatchName = new JLabel( loc.getString( "batchName" ) );
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
			ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
	}

	public void localize() {
		labelBatchName.setText( loc.getString( "batchName" ) );
		btnExit.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnSaveResults.setToolTipText( loc.getString( "toolbarTooltipSave" ) );
		btnOpenResults.setToolTipText( loc.getString( "toolbarTooltipOpen" ) );
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
				ZETMain.sendError( "Error while loading temp file: " + ex.getLocalizedMessage() );
				return null;
			}
		}
	}
}
