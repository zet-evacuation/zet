/**
 * JVisualizationToolbar.java
 * Created: Jul 19, 2010,7:18:42 PM
 */
package zet.gui.components.toolbar;

import batch.BatchResult;
import batch.BatchResultEntry;
import ds.PropertyContainer;
import gui.GUIControl;
import gui.ZETMain;
import zet.gui.components.model.ComboBoxRenderer;
import de.tu_berlin.math.coga.datastructure.NamedIndex;
import gui.components.framework.Button;
import gui.components.framework.IconSet;
import gui.visualization.control.GLControl;
import gui.visualization.control.GLControl.CellInformationDisplay;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JToolBar;
import zet.gui.GUILocalization;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JVisualizationToolbar extends JToolBar implements ActionListener {
	private final GUIControl control;
	/** The localization class. */
	static final GUILocalization loc = GUILocalization.getSingleton();
	private JButton btnExit;
	private JLabel labelBatchName;
	private BatchResultEntryVisComboBoxModel entryModelVis;
	private JLabel labelBatchRun;
	private CycleComboBoxModel cycleModel;
	/** Allows switching between 3d (perspective) view and 2d (orthogonal/isometric) view. */
	private JButton btn2d3dSwitch;
	/** Allows switching orthogonal and isometric 2-dimensional view. */
	private JButton btn2dSwitch;
	private JButton btnVideo;
	private JButton btnPlayStart;
	private JButton btnPlay;
	private JButton btnPlayLoop;
	private JButton btnStop;
	private Icon playIcon;
	private Icon pauseIcon;
	//private JButton btnPlayEnd;
	private JButton btnShowWalls;
	private JButton btnShowGraph;
	private JButton btnShowGraphGrid;
	private JButton btnShowCellularAutomaton;
	private JButton btnShowAllFloors;
	private JButton btnShowPotential;
	private JButton btnShowDynamicPotential;
	private JButton btnShowUtilization;
	private JButton btnShowWaiting;

	public JVisualizationToolbar( GUIControl control ) {
		this.control = control;
		createVisualizationToolBar();
		control.setVisualizationToolbar( this );
	}

	/**
	 * Creates the {@code JToolBar} for the visualization mode.
	 */
	private void createVisualizationToolBar() {
		// todo loc
		loc.setPrefix( "gui.toolbar." );

		//toolBarVisualization = new JToolBar();
		btnExit = Button.newButton( IconSet.Exit, this, "", loc.getString( "Exit" ) );
		add( btnExit );
		addSeparator();
		labelBatchName = new JLabel( loc.getString( "Visualization.BatchName" ) );
		add( labelBatchName );
		entryModelVis = new BatchResultEntryVisComboBoxModel();
		JComboBox cbxBatchEntry = new JComboBox( entryModelVis );
		cbxBatchEntry.setLightWeightPopupEnabled( false );
		cbxBatchEntry.setMaximumRowCount( 10 );
		cbxBatchEntry.setMaximumSize( new Dimension( 250, cbxBatchEntry.getPreferredSize().height ) );
		cbxBatchEntry.setPreferredSize( new Dimension( 250, cbxBatchEntry.getPreferredSize().height ) );
		cbxBatchEntry.setAlignmentX( 0 );
		add( cbxBatchEntry );
		labelBatchRun = new JLabel( loc.getString( "Visualization.BatchRun" ) );
		add( labelBatchRun );
		cycleModel = new CycleComboBoxModel();
		JComboBox cbxBatchCycle = new JComboBox( cycleModel );
		cbxBatchCycle.setLightWeightPopupEnabled( false );
		cbxBatchCycle.setRenderer( new CycleComboBoxRenderer() );
		cbxBatchCycle.setMaximumRowCount( 20 );
		cbxBatchCycle.setMaximumSize( new Dimension( 120, cbxBatchCycle.getPreferredSize().height ) );
		cbxBatchCycle.setAlignmentX( 0 );
		add( cbxBatchCycle );

		add( new JLabel( " " ) );
		addSeparator();
		add( new JLabel( " " ) );

		btn2d3dSwitch = Button.newButton( IconSet.Toggle2D3D, this, "2d3dSwitch", loc.getString( "Visualization.Switch2d3d" ) );
		btn2d3dSwitch.setSelected( !PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.2d" ) );
		add( btn2d3dSwitch );
		btn2dSwitch = Button.newButton( IconSet.ToggleOrthogonalIsometric, this, "2dSwitch", loc.getString( "Visualization.SwitchIso" ) );
		btn2dSwitch.setSelected( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.isometric" ) );
		btn2dSwitch.setEnabled( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.2d" ) );
		add( btn2dSwitch );
		addSeparator();
		btnVideo = Button.newButton( IconSet.Video, this, "video", loc.getString( "Visualization.SaveVideo" ) );
		add( btnVideo );
		btnPlayStart = Button.newButton( IconSet.PlayStart, this, "start", loc.getString( "Visualization.PlayBackToStart" ) );
		add( btnPlayStart );
		btnPlay = Button.newButton( IconSet.Play, this, "play", loc.getString( "Visualization.PlayPause" ) );
		playIcon = gui.components.framework.Icon.newIcon( IconSet.Play );
		pauseIcon = gui.components.framework.Icon.newIcon( IconSet.PlayPause );
		add( btnPlay );
		btnStop = Button.newButton( IconSet.PlayStop, this, "stop", loc.getString( "Visualization.PlayStop" ) );
		add( btnStop );
		btnPlayLoop = Button.newButton( IconSet.PlayLoop, this, "loop", loc.getString( "Visualization.PlayLoop" ) );
		btnPlayLoop.setSelected( false );
		add( btnPlayLoop );
		//btnPlayEnd = Button.newButton( IconSet.PlayEnd, this, "end", loc.getString( "playToEnd" ) );
		//add( btnPlayEnd );
		addSeparator();
		btnShowWalls = Button.newButton( IconSet.ShowWalls, this, "walls", loc.getString( "Visualization.ShowWalls" ) );
		btnShowWalls.setSelected( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.walls" ) );
		add( btnShowWalls );
		btnShowGraph = Button.newButton( IconSet.ShowGraph, this, "graph", loc.getString( "Visualization.ShowGraph" ) );
		btnShowGraph.setSelected( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.graph" ) );
		add( btnShowGraph );
		btnShowGraphGrid = Button.newButton( IconSet.ShowGraphGrid, this, "graphgrid", loc.getString( "Visualization.ShowGridRectangles" ) );
		btnShowGraphGrid.setSelected( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.nodeArea" ) );
		add( btnShowGraphGrid );
		btnShowCellularAutomaton = Button.newButton( IconSet.ShowCellularAutomaton, this, "ca", loc.getString( "Visualization.ShowCellularAutomaton" ) );
		btnShowCellularAutomaton.setSelected( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.cellularAutomaton" ) );
		add( btnShowCellularAutomaton );
		addSeparator();
		btnShowAllFloors = Button.newButton( IconSet.ShowAllFloors, this, "floors", loc.getString( "Visualization.ShowAllFloors" ) );
		add( btnShowAllFloors );
		btnShowAllFloors.setSelected( PropertyContainer.getInstance().getAsBoolean( "settings.gui.visualization.floors" ) );
		addSeparator();

		btnShowPotential = Button.newButton( IconSet.ShowPotential, this, "potential", loc.getString( "Visualization.ShowPotential" ) );
		btnShowPotential.setSelected( PropertyContainer.getInstance().getAsInt( "settings.gui.visualization.floorInformation" ) == 1 );
		add( btnShowPotential );
		btnShowDynamicPotential = Button.newButton( IconSet.ShowDynamicPotential, this, "dynamic", loc.getString( "Visualization.ShowDynamicPotential" ) );
		btnShowDynamicPotential.setSelected( PropertyContainer.getInstance().getAsInt( "settings.gui.visualization.floorInformation" ) == 2 );
		add( btnShowDynamicPotential );
		btnShowUtilization = Button.newButton( IconSet.ShowUsage, this, "utilization", loc.getString( "Visualization.ShowUtilization" ) );
		btnShowUtilization.setSelected( PropertyContainer.getInstance().getAsInt( "settings.gui.visualization.floorInformation" ) == 3 );
		add( btnShowUtilization );
		btnShowWaiting = Button.newButton( IconSet.ShowWaiting, this, "waiting", loc.getString( "Visualization.ShowWaitingTime" ) );
		btnShowWaiting.setSelected( PropertyContainer.getInstance().getAsInt( "settings.gui.visualization.floorInformation" ) == 4 );
		add( btnShowWaiting );
		addSeparator();

		loc.setPrefix( "" );
	}

	public void actionPerformed( ActionEvent e ) {
		if( e.getActionCommand().equals( "exit" ) ) {
			// quits the program
			control.exit();
		} else if( e.getActionCommand().equals( "2d3dSwitch" ) ) {
			// toggles between 2d and 3d view
			control.visualizationToggle2D3D();
		} else if( e.getActionCommand().equals( "2dSwitch" ) ) {
			// toggels between the orthogonal and isometric 2d views (if in 2d mode)
			control.visualizationToggle2D();
		} else if( e.getActionCommand().equals( "screenshot" ) ) {
			// takes a screenshot
			control.takeScreenshot();
		} else if( e.getActionCommand().equals( "video" ) ) {
			// create a video out of the visualization content
			control.createVideo();
		} else if( e.getActionCommand().equals( "start" ) ) {
			control.visualizationTurnBackToStart();
		} else if( e.getActionCommand().equals( "play" ) ) {
			control.visualizationPlay();
		} else if( e.getActionCommand().equals( "loop" ) ) {
			control.visualizationLoop();
		} else if( e.getActionCommand().equals( "stop" ) ) {
			control.visualizationStop();
		} else if( e.getActionCommand().equals( "end" ) ) {
			ZETMain.sendError( "Go to end is not supported yet" );
		} else if( e.getActionCommand().equals( "walls" ) ) {
			control.visualizationShowWalls();
		} else if( e.getActionCommand().equals( "graph" ) ) {
			control.visualizationShowGraph();
		} else if( e.getActionCommand().equals( "graphgrid" ) ) {
			control.visualizationShowGraphGrid();
		} else if( e.getActionCommand().equals( "ca" ) ) {
			control.visualizationShowCellularAutomaton();
		} else if( e.getActionCommand().equals( "floors" ) ) {
			control.visualizationShowAllFloors();
		} else if( e.getActionCommand().equals( "potential" ) ) {
			control.visualizationShowCellInformation( CellInformationDisplay.StaticPotential );
		} else if( e.getActionCommand().equals( "dynamic" ) ) {
			control.visualizationShowCellInformation( CellInformationDisplay.DynamicPotential );
		} else if( e.getActionCommand().equals( "utilization" ) ) {
			control.visualizationShowCellInformation( CellInformationDisplay.Utilization );
		} else if( e.getActionCommand().equals( "waiting" ) ) {
			control.visualizationShowCellInformation( CellInformationDisplay.Waiting );
		} else
			ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
	}

	public void setSelected2d3d( boolean visualization3D ) {
		btn2d3dSwitch.setSelected( visualization3D );
	}

	public void setEnabled2d( boolean b ) {
		btn2dSwitch.setEnabled( b );
	}

	public void setSelected2d( boolean visualization2D ) {
		btn2dSwitch.setSelected( visualization2D );
	}

	public void setSelectedLoop( boolean loop ) {
		btnPlayLoop.setSelected( loop );
	}

	public void setSelectedShowWalls( boolean showWalls ) {
		btnShowWalls.setSelected( showWalls );
	}

	public void play() {
		btnPlay.setIcon( pauseIcon );
		btnPlay.setSelected( true );
	}

	public void pause() {
		btnPlay.setIcon( playIcon );
		btnPlay.setSelected( false );
	}

	public void setSelectedShowGraph( boolean showGraph ) {
		btnShowGraph.setSelected( showGraph );
	}

	public void setSelectedShowGraphGrid( boolean showGraphGrid ) {
		btnShowGraphGrid.setSelected( showGraphGrid );
	}

	public void setSelectedShowCellularAutomaton( boolean showCellularAutomaton ) {
		btnShowCellularAutomaton.setSelected( showCellularAutomaton );
	}

	public void setSelectedAllFloors( boolean showAllFloors ) {
		btnShowAllFloors.setSelected( showAllFloors );
	}

	public void setSelectedCellInformationDisplay( CellInformationDisplay cid ) {
		btnShowPotential.setSelected( cid == CellInformationDisplay.StaticPotential );
		btnShowDynamicPotential.setSelected( cid == CellInformationDisplay.DynamicPotential );
		btnShowUtilization.setSelected( cid == CellInformationDisplay.Utilization );
		btnShowWaiting.setSelected( cid == CellInformationDisplay.Waiting );
	}

	public void setEnabledVisibleElements( GLControl control ) {
		btnShowCellularAutomaton.setEnabled( control.hasCellularAutomaton() );
		btnShowGraph.setEnabled( control.hasGraph() );
		btnShowGraphGrid.setEnabled( control.hasGraph() );
	}

	/**
	 * Enables the controls for playback (video, play, back, forward etc.) or
	 * disables them.
	 * @param enabled decides if the controls should be enabled or disabled
	 */
	public void setEnabledPlayback( boolean enabled ) {
		btnPlay.setEnabled( enabled );
		//btnPlayEnd.setEnabled( enabled );
		btnPlayLoop.setEnabled( enabled );
		btnPlayStart.setEnabled( enabled );
		btnVideo.setEnabled( enabled );
		btnStop.setEnabled( enabled );
	}

	public void localize() {
		loc.setPrefix( "gui.toolbar." );
		btnExit.setToolTipText( loc.getString( "Exit" ) );
		labelBatchName.setText( loc.getString( "Visualization.BatchName" ) );
		labelBatchRun.setText( loc.getString( "Visualization.BatchRun" ) );
		btn2d3dSwitch.setToolTipText( loc.getString( "Visualization.Switch2d3d" ) );
		btn2dSwitch.setToolTipText( loc.getString( "Visualization.SwitchIso" ) );
		btnVideo.setToolTipText( loc.getString( "Visualization.SaveVideo" ) );
		btnPlayStart.setToolTipText( loc.getString( "Visualization.PlayBackToStart" ) );
		btnPlay.setToolTipText( loc.getString( "Visualization.PlayPause" ) );
		btnPlayLoop.setToolTipText( loc.getString( "Visualization.PlayLoop" ) );
		//btnPlayEnd.setToolTipText( loc.getString( "Visualization.PlayToEnd" ) );
		btnShowWalls.setToolTipText( loc.getString( "Visualization.ShowWalls" ) );
		btnShowGraph.setToolTipText( loc.getString( "Visualization.ShowGraph" ) );
		btnShowGraphGrid.setToolTipText( loc.getString( "Visualization.ShowGridRectangles" ) );
		btnShowCellularAutomaton.setToolTipText( loc.getString( "Visualization.ShowCellularAutomaton" ) );
		btnShowAllFloors.setToolTipText( loc.getString( "Visualization.ShowAllFloors" ) );
		btnShowPotential.setToolTipText( loc.getString( "Visualization.ShowPotential" ) );
		btnShowDynamicPotential.setToolTipText( loc.getString( "Visualization.ShowDynamicPotential" ) );
		btnShowUtilization.setToolTipText( loc.getString( "Visualization.ShowUtilization" ) );
		btnShowWaiting.setToolTipText( loc.getString( "Visualization.ShowWaitingTime" ) );
	}

	public void rebuild( BatchResult result ) {
		entryModelVis.rebuild( result );
	}

	/**
	 * This class serves as a model for the JComboBox that contains the
	 * BatchResultEntries for the Visualization Tab.
	 */
	private class BatchResultEntryVisComboBoxModel extends DefaultComboBoxModel {
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

			BatchResultEntry entry = (BatchResultEntry)getSelectedItem();
			control.visualizationStop();
			//if( visualizationView != null && visualizationView.getGLContainer().isAnimating() ) {
			//	btnPlay.setIcon( playIcon );
			//	visualizationView.getGLContainer().stopAnimation();
			//}
			if( cycleModel != null )
				cycleModel.rebuild( entry );
		}

		@Override
		public Object getSelectedItem() {
			try {
				if( result != null && super.getSelectedItem() != null )
					return result.getResult( ((NamedIndex)super.getSelectedItem()).getIndex() );
				else
					return null;
			} catch( IOException ex ) {
				ZETMain.sendError( "Error while loading temp file: " + ex.getLocalizedMessage() );
				return null;
			}
		}
	}

	/** This class serves as a model for the JComboBox that contains the Cycles. */
	private class CycleComboBoxModel extends DefaultComboBoxModel {
		public void rebuild( BatchResultEntry e ) {
			int oldSize = getSize();

			removeAllElements();
			if( e != null ) {
				if( e.getCaVis() != null )
					for( int i = 0; i < e.getCaVis().length; i++ )
						addElement( new Integer( i ) ); // else { the box stays empty }
				fireIntervalAdded( this, 0, getSize() );
			} else
				fireIntervalRemoved( this, 0, oldSize );
		}

		@Override
		public void setSelectedItem( Object object ) {
			super.setSelectedItem( object );

			control.visualizationStop();
//			if( visualizationView.getGLContainer().isAnimating() ) {
//				btnPlay.setIcon( playIcon );
//				visualizationView.getGLContainer().stopAnimation();
//			}
			control.buildVisualizationDataStructure( (BatchResultEntry)entryModelVis.getSelectedItem(), ((Integer)object).intValue() );
		}
	}

	/** This class can display EditMode Objects in a JComboBox. */
	private class CycleComboBoxRenderer extends ComboBoxRenderer {
		@Override
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected,
						boolean cellHasFocus ) {
			JLabel me = (JLabel)super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
			setHorizontalAlignment( RIGHT );

			if( value != null ) {
				BatchResultEntry currentEntry = (BatchResultEntry)entryModelVis.getSelectedItem();
				Integer number = (Integer)value;

				try {
					setText( Integer.toString( number + 1 ) );
				} catch( java.lang.ClassCastException e ) {
					setText( (String)value );
				}

				// Paint Medians with inverted colors
				if( currentEntry != null && (number == currentEntry.getMedianIndex()) ) {
					Color foreground = getForeground();
					Color background = getBackground();
					setBackground( foreground );
					setForeground( background );
				}
			}

			return this;
		}
	}
}