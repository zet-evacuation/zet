/**
 * JEdgeInformationPanel.java
 * Created: 23.11.2012, 13:34:13
 */
package zet.gui.main.tabs.editor;

import ds.z.DefaultEvacuationFloor;
import ds.z.Edge;
import ds.z.EvacuationArea;
import ds.z.Room;
import ds.z.RoomEdge;
import ds.z.TeleportEdge;
import info.clearthought.layout.TableLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import zet.gui.main.JZetWindow;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JEdgeInformationPanel extends JInformationPanel<Edge> {
	private JLabel lblEdgeType;
	private JLabel lblEdgeLength;
	private JLabel lblEdgeExitName;
	private JTextField txtEdgeExitName;

	public JEdgeInformationPanel() {
		super( new double[]{TableLayout.FILL},
						new double[]{TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
							TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
							TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
							TableLayout.PREFERRED, 20, TableLayout.FILL
						} );
		init();
	}

	private void init() {
		int row = 0;

		lblEdgeType = new JLabel( "Edge type" );
		this.add( lblEdgeType, "0, " + row++ );
		row++;

		lblEdgeLength = new JLabel( "Länge:" );
		this.add( lblEdgeLength, "0, " + row++ );
		row++;

		lblEdgeExitName = new JLabel( loc.getString( "Evacuation.Name" ) );
		this.add( lblEdgeExitName, "0, " + row++ );
		txtEdgeExitName = new JTextField();
		txtEdgeExitName.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtEdgeExitName.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
					// Find the attached exit and set the name
//					if( getLeftPanel().getMainComponent().getSelectedEdge() instanceof TeleportEdge ) {
//						TeleportEdge te = (TeleportEdge)getLeftPanel().getMainComponent().getSelectedEdge();
//						if( ((Room)te.getLinkTarget().getAssociatedPolygon()).getAssociatedFloor() instanceof DefaultEvacuationFloor ) {
//							// we have an evacuation exit
//							Room r = (Room)te.getLinkTarget().getAssociatedPolygon();
//							EvacuationArea ea = r.getEvacuationAreas().get( 0 );
//							ea.setName( txtEdgeExitName.getText() );
//						}
//					}
				}
					//((EvacuationArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setName( txtEdgeExitName.getText() );
			}
		} );
		this.add( txtEdgeExitName, "0, " + row++ );
		row++;
	}

	@Override
	public void update() {
		txtEdgeExitName.setEnabled( false );
		lblEdgeExitName.setText( "" );
		if( current instanceof RoomEdge ) {
			if( current instanceof TeleportEdge ) {
				TeleportEdge te = (TeleportEdge)current;
				if( ((Room)te.getLinkTarget().getAssociatedPolygon()).getAssociatedFloor() instanceof DefaultEvacuationFloor ) {
					lblEdgeType.setText( "Ausgang" );
					txtEdgeExitName.setEnabled( true );
					// we have an evacuation exit
					Room r = (Room)te.getLinkTarget().getAssociatedPolygon();
					EvacuationArea ea = r.getEvacuationAreas().get( 0 );
					lblEdgeExitName.setText( "Ausgang" );
					txtEdgeExitName.setText( ea.getName() );
				}
				else
					lblEdgeType.setText( "Stockwerkübergang" );
			} else {
				if( ((RoomEdge)current).isPassable() ) {
					lblEdgeType.setText( "Durchgang" );
				} else {
					lblEdgeType.setText( "Wand" );
				}
			}
		} else {
			// easy peasy, this is an area boundry
			lblEdgeType.setText( "Area-Begrenzung" );
		}

		lblEdgeLength.setText( "Breite: " + nfFloat.format(( current.length())*0.001) + "m"  );
	}

	@Override
	public void localize() {
		loc.setPrefix( "gui.EditPanel." );

		loc.clearPrefix();
	}

}
