/**
 * JTeleportAreaInformationPanel.java
 * Created: 22.11.2012, 17:31:16
 */
package zet.gui.main.tabs.editor;


import de.zet_evakuierung.model.AbstractFloor;
import de.zet_evakuierung.model.EvacuationArea;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.model.TeleportArea;
import info.clearthought.layout.TableLayout;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import zet.gui.components.model.ComboBoxRenderer;
import zet.gui.main.JZetWindow;
/**
 *
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class JTeleportAreaInformationPanel extends JInformationPanel<TeleportArea> {
	/** Describes the teleportation area name field. */
	private JLabel lblTeleportAreaName;
	/** The name filed for a teleportation area. */
	private JTextField txtTeleportAreaName;

	/** Describes the target area combo box.  */
	private JLabel lblTargetArea;
	/** A combo box selecting the target area of a target area. */
	private JComboBox<TeleportArea> cbxTargetArea;

	/** A combo box selecting the possible exits for a teleportation area. */
	private JComboBox<EvacuationArea> cbxTargetExit;
	/** Describes the target exit combo box. */
	private JLabel lblTargetExit;

	public JTeleportAreaInformationPanel() {
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

		// The area name
		lblTeleportAreaName = new JLabel( loc.getString( "Teleportation.Name") );
		this.add( lblTeleportAreaName, "0, " + row++ );
		txtTeleportAreaName = new JTextField();
		txtTeleportAreaName.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtTeleportAreaName.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
;//					((TeleportArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setName( txtTeleportAreaName.getText() );
			}
		} );
		this.add( txtTeleportAreaName, "0, " + row++ );
		row++;

		// Target-Aea-Selector
		lblTargetArea = new JLabel( loc.getString( "Teleportation.TargetArea") );
		this.add( lblTargetArea, "0, " + row++ );
		cbxTargetArea = new JComboBox<>();
		cbxTargetArea.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged( ItemEvent e ) {
				if( cbxTargetArea.getSelectedIndex() == -1 )
					return;
//				if( getLeftPanel().getMainComponent().getSelectedPolygons().size() > 0 ) {
//					final TeleportArea a = (TeleportArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
//					a.setTargetArea( (TeleportArea)cbxTargetArea.getSelectedItem() );
//				}
			}
		} );

		cbxTargetArea.setRenderer( new ComboBoxRenderer<TeleportArea>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent( JList<? extends TeleportArea> list, TeleportArea value, int index, boolean isSelected, boolean cellHasFocus ) {

				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				setText( value != null ? value.getName() : "" );
				return this;
			}
			/** Prohibits serialization. */
			private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
				throw new UnsupportedOperationException( "Serialization not supported" );
			}
		} );
		this.add( cbxTargetArea, "0, " + row++ );
		row++;

		// Target-Exit-Selector
		lblTargetExit = new JLabel( loc.getString( "Teleportation.TargetExit" ) );
		this.add( lblTargetExit, "0, " + row++ );
		cbxTargetExit = new JComboBox<>();
		cbxTargetExit.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged( ItemEvent e ) {
				if( cbxTargetExit.getSelectedIndex() == -1 )
					return;
//				if( getLeftPanel().getMainComponent().getSelectedPolygons().size() > 0 ) {
//					final TeleportArea a = (TeleportArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
//					a.setExitArea( (EvacuationArea)cbxTargetExit.getSelectedItem() );
//				}
			}
		} );
		cbxTargetExit.setRenderer( new ComboBoxRenderer<EvacuationArea>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent( JList<? extends EvacuationArea> list, EvacuationArea value, int index, boolean isSelected, boolean cellHasFocus ) {
				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				setText( value != null ? value.getName() : "" );
				return this;
			}
			/** Prohibits serialization. */
			private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
				throw new UnsupportedOperationException( "Serialization not supported" );
			}
		} );
		this.add( cbxTargetExit, "0, " + row++ );
		row++;
	}

	@Override
	public void update() {
		txtTeleportAreaName.setText( current.getName() );
		if( current.getExitArea() == null )
			cbxTargetExit.setSelectedIndex( -1 );
		else
			cbxTargetExit.setSelectedItem( current.getExitArea() );

		if( current.getTargetArea() == null )
			cbxTargetArea.setSelectedIndex( -1 );
		else
			cbxTargetArea.setSelectedItem( current.getTargetArea() );

		for( AbstractFloor f : projectControl.getProject().getBuildingPlan().getFloors() )
			for( Room r : f.getRooms() ) {
				for( EvacuationArea e : r.getEvacuationAreas() ) {
		//			cbxPreferredExit.addItem( e );
					cbxTargetExit.addItem( e );
				}
				for( TeleportArea e : r.getTeleportAreas() )
					cbxTargetArea.addItem( e );
			}
	}

	@Override
	public void localize() {
		loc.setPrefix( "gui.EditPanel." );
		lblTeleportAreaName.setText( loc.getString( "Teleportation.Name" ) );
		lblTargetArea.setText( loc.getString( "Teleportation.TargetArea" ) );
		lblTargetExit.setText( loc.getString( "Teleportation.TargetExit" ) );
		loc.clearPrefix();
	}
	/** Prohibits serialization. */
	private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
		throw new UnsupportedOperationException( "Serialization not supported" );
	}
}
