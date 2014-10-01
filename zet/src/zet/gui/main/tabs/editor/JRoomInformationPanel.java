/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zet.gui.main.tabs.editor;

import de.tu_berlin.coga.zet.model.Room;
import gui.ZETProperties;
import info.clearthought.layout.TableLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import zet.gui.main.JZetWindow;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JRoomInformationPanel extends JInformationPanel<Room> {
	private JLabel lblRoomName;
	private JTextField txtRoomName;
	private JLabel lblRoomSize;
	private JLabel lblRoomSizeDesc;
	private JButton deleteRoom;
	private JButton moveRoom;
	//private SelectedElements selection = new SelectedElements();

	public JRoomInformationPanel() {
		super( new double[] {TableLayout.FILL},
						new double[] {TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
							TableLayout.PREFERRED, TableLayout.PREFERRED,
							10,
							TableLayout.PREFERRED,
							10,
							TableLayout.PREFERRED,
							TableLayout.FILL,} );

		init();
	}

	private void init() {
		int row = 0;

		lblRoomName = new JLabel( loc.getString( "Room.Name" ) );
		this.add( lblRoomName, "0, " + row++ );
		txtRoomName = new JTextField();
		txtRoomName.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtRoomName.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
					//boolean success = projectControl.renameRoom( (Room)selection.getSelected().getPlanPolygon(), txtRoomName.getText() );
					boolean success = projectControl.renameRoom( current, txtRoomName.getText() );
					if( !success )
						guiControl.alertError( "Floor with that name already exists" );
//					else
//						updateFloorView();
				}
			}
		} );
		this.add( txtRoomName, "0, " + row++ );
		row++;

		// add size information
		lblRoomSizeDesc = new JLabel( loc.getString( "Room.Area" ) );
		lblRoomSize = new JLabel( "" );
		this.add( lblRoomSizeDesc, "0, " + row++ );
		this.add( lblRoomSize, "0, " + row++ );

		deleteRoom = new JButton( "Raum Löschen" );
		deleteRoom.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				//Room currentRoom = (Room)selection.getSelected().getPlanPolygon();
				projectControl.deletePolygon( current );
				//updateRoomList();
				//getLeftPanel().getMainComponent().displayFloor();
			}
		} );
		this.add( deleteRoom, "0, " + ++row );
		row++;


		moveRoom = new JButton( "Epsilon-Verschieben" );
		moveRoom.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				//projectControl.refineRoomCoordinates( (Room)selection.getSelected().getPlanPolygon(), ZETProperties.getRasterSizeSnap() );
				projectControl.refineRoomCoordinates( current.getPolygon(), ZETProperties.getRasterSizeSnap() );
				//getLeftPanel().getMainComponent().displayFloor();
			}
		} );
		this.add( moveRoom, "0, " + ++row );
		row++;

	}

//	public void setSelection( SelectedElements selection ) {
//		this.selection = selection;
//	}

	@Override
	public void update() {
		//txtRoomName.setText( ((Room)selection.getSelected().getPlanPolygon()).getName() );
		txtRoomName.setText( current.getName() );
		//double areaRoom = Math.round( selection.getSelected().getPlanPolygon().areaMeter() * 100 ) / 100.0;
		double areaRoom = Math.round( current.getPolygon().areaMeter() * 100 ) / 100.0;
		lblRoomSize.setText( nfFloat.format( areaRoom ) + " m²" );
	}

	@Override
	public void localize() {
		loc.setPrefix( "gui.EditPanel." );
		lblRoomName.setText( loc.getString( "Room.Name" ) );
		lblRoomSizeDesc.setText( loc.getString( "Room.Area" ) );
		loc.clearPrefix();
	}
}
