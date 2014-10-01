
package zet.gui.main.tabs.editor;

import de.tu_berlin.math.coga.components.framework.Button;
import de.tu_berlin.coga.zet.model.Floor;
import de.tu_berlin.coga.zet.model.Room;
import gui.ZETLoader;
import info.clearthought.layout.TableLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import zet.gui.main.JZetWindow;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JFloorInformationPanel extends JInformationPanel<Floor> {
	private JLabel lblFloorName;
	private JButton btnFloorUp;
	private JButton btnFloorDown;
	private JTextField txtFloorName;
	private JLabel lblFloorxOffset;
	private JLabel lblFlooryOffset;
	private JLabel lblFloorWidth;
	private JLabel lblFloorHeight;
	private JTextField txtFloorxOffset;
	private JTextField txtFlooryOffset;
	private JTextField txtFloorWidth;
	private JTextField txtFloorHeight;
	private JLabel lblFloorSize;
	private JLabel lblFloorSizeDesc;

	public JFloorInformationPanel() {
		super( new double[]{
							TableLayout.FILL, 10, TableLayout.FILL
						},
						new double[]{
							TableLayout.PREFERRED,
							TableLayout.PREFERRED,
							20,
							TableLayout.PREFERRED,
							TableLayout.PREFERRED,
							20,
							TableLayout.PREFERRED,
							TableLayout.PREFERRED,
							TableLayout.PREFERRED,
							TableLayout.PREFERRED,
							TableLayout.PREFERRED,
							TableLayout.PREFERRED,
							20,
							TableLayout.PREFERRED,
							TableLayout.FILL
						} );
		init();
	}

	private void init() {
		lblFloorName = new JLabel( loc.getString( "Floor.Name" ) );
		this.add( lblFloorName, "0,0,2,0" );
		txtFloorName = new JTextField();
		txtFloorName.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtFloorName.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
					boolean success = projectControl.renameFloor( current, txtFloorName.getText() );
					//if( !success )
					//	guiControl.alertError( "Floor with that name already exists" );
					//else
					//	updateFloorView();
				}
			}
		} );
		this.add( txtFloorName, "0,1,2,1" );

		ActionListener aclFloor = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				switch( e.getActionCommand() ) {
					case "down":
						guiControl.moveFloorDown();
						break;
					case "up":
						guiControl.moveFloorUp();
						break;
					default:
						ZETLoader.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
						break;
				}
			}
		};
		btnFloorUp = Button.newButton( loc.getString( "Floor.Up" ), aclFloor, "up", loc.getString( "Floor.Up.ToolTip" ) );
		this.add( btnFloorUp, "0,3,0,3" );
		btnFloorDown = Button.newButton( loc.getString( "Floor.Down" ), aclFloor, "down", loc.getString( "Floor.Down.ToolTip" ) );
		this.add( btnFloorDown, "2,3" );

		// Additional Infos:
		lblFloorxOffset = new JLabel( loc.getString( "Floor.xOffset" ) );
		lblFlooryOffset = new JLabel( loc.getString( "Floor.yOffset" ) );
		lblFloorWidth = new JLabel( loc.getString( "Floor.Width" ) );
		lblFloorHeight = new JLabel( loc.getString( "Floor.Height" ) );
		txtFloorxOffset = new JTextField();
		txtFlooryOffset = new JTextField();
		txtFloorWidth = new JTextField();
		txtFloorHeight = new JTextField();
		this.add( lblFloorxOffset, "0,5" );
		this.add( lblFlooryOffset, "0,6" );
		this.add( lblFloorWidth, "0,7" );
		this.add( lblFloorHeight, "0,8" );
		this.add( txtFloorxOffset, "2,5" );
		this.add( txtFlooryOffset, "2,6" );
		this.add( txtFloorWidth, "2,7" );
		this.add( txtFloorHeight, "2,8" );

		ActionListener aclFloorSize = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent ae ) {
				try {
					final int xOffset = nfInteger.parse( txtFloorxOffset.getText() ).intValue();
					final int yOffset = nfInteger.parse( txtFlooryOffset.getText() ).intValue();
					final int width = nfInteger.parse( txtFloorWidth.getText() ).intValue();
					final int height = nfInteger.parse( txtFloorHeight.getText() ).intValue();
					current.setMinimumSize( xOffset, yOffset, width, height );
					//getLeftPanel().getMainComponent().displayFloor();
				} catch( ParseException ex ) {
					ZETLoader.sendError( "Parsing nicht möglich." ); // TODO loc
					return;
				}
				ZETLoader.sendMessage( "Floor-Size geändert." ); // TODO loc
			}
		};
		txtFloorxOffset.addActionListener( aclFloorSize );
		txtFlooryOffset.addActionListener( aclFloorSize );
		txtFloorWidth.addActionListener( aclFloorSize );
		txtFloorHeight.addActionListener( aclFloorSize );

		lblFloorSizeDesc = new JLabel( loc.getString( "Floor.Area" ) );
		lblFloorSize = new JLabel( "" );
		this.add( lblFloorSizeDesc, "0,10,2,10" );
		this.add( lblFloorSize, "0,11,2,11" );
    
    JButton cmdRasterizeBuilding = new JButton( "Rastern!" );
    this.add( cmdRasterizeBuilding, "0,13,2,13" );
    cmdRasterizeBuilding.addActionListener( new ActionListener() {

      @Override
      public void actionPerformed( ActionEvent e ) {
        projectControl.getProject().getBuildingPlan().rasterize();
      }
    });
	}


	@Override
	public void update() {
		txtFloorName.setText( this.current.getName() );
		txtFloorxOffset.setText( Integer.toString( current.getxOffset() ) );
		txtFlooryOffset.setText( Integer.toString( current.getyOffset() ) );
		txtFloorWidth.setText( Integer.toString( current.getWidth() ) );
		txtFloorHeight.setText( Integer.toString( current.getHeight() ) );

		double areaFloor = 0;
		for( Room r : current )
			areaFloor += r.getPolygon().areaMeter();

		lblFloorSize.setText( nfFloat.format( areaFloor ) + " m²" );
	}

	@Override
	public void localize() {
		// Floor properties
		loc.setPrefix( "gui.EditPanel." );
		lblFloorName.setText( loc.getString( "Floor.Name" ) );
		btnFloorUp.setText( loc.getString( "Floor.Up" ) );
		btnFloorUp.setToolTipText( loc.getString( "Floor.Up.ToolTip" ) );
		btnFloorDown.setText( loc.getString( "Floor.Down" ) );
		btnFloorDown.setToolTipText( loc.getString( "Floor.Down.ToolTip" ) );
		lblFloorxOffset.setText( loc.getString( "Floor.xOffset" ) );
		lblFlooryOffset.setText( loc.getString( "Floor.yOffset" ) );
		lblFloorWidth.setText( loc.getString( "Floor.Width" ) );
		lblFloorHeight.setText( loc.getString( "Floor.Height" ) );
		lblFloorSizeDesc.setText( loc.getString( "Floor.Area" ) );
		loc.clearPrefix();
	}
}
