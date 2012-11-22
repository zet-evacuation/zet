/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zet.gui.main.tabs.editor;

import ds.z.EvacuationArea;
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
public class JEvacuationAreaInformationPanel extends JInformationPanel<EvacuationArea> {
	private JLabel lblEvacuationAreaName;
	private JTextField txtEvacuationAreaName;
	private JLabel lblEvacuationAttractivity;
	private JTextField txtEvacuationAttractivity;

	public JEvacuationAreaInformationPanel() {
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
		lblEvacuationAreaName = new JLabel( loc.getString( "Evacuation.Name" ) );
		this.add( lblEvacuationAreaName, "0, " + row++ );
		txtEvacuationAreaName = new JTextField();
		txtEvacuationAreaName.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtEvacuationAreaName.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
;
//					((EvacuationArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setName( txtEvacuationAreaName.getText() );
			}
		} );
		this.add( txtEvacuationAreaName, "0, " + row++ );
		row++;

		// Attractivity
		lblEvacuationAttractivity = new JLabel( loc.getString( "Evacuation.Attractivity" ) );
		this.add( lblEvacuationAttractivity, "0, " + row++ );

		txtEvacuationAttractivity = new JTextField( " " );
		txtEvacuationAttractivity.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtEvacuationAttractivity.addKeyListener( new KeyAdapter() {
			@Override
			public void keyReleased( KeyEvent e ) {
//				try {
//					((EvacuationArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setAttractivity( nfInteger.parse(
//									txtEvacuationAttractivity.getText() ).intValue() );
//				} catch( ParseException ex ) {
//					ZETLoader.sendError( loc.getString( "gui.error.NonParsableNumberString" ) );
//				} catch( IllegalArgumentException ex ) {
//					ZETLoader.sendError( ex.getLocalizedMessage() );
//				}
			}
		} );
		this.add( txtEvacuationAttractivity, "0, " + row++ );
		row++;

	}

	@Override
	public void update() {
		txtEvacuationAreaName.setText( current.getName() );
		txtEvacuationAttractivity.setText( nfInteger.format( current.getAttractivity() ) );
	}

	@Override
	public void localize() {
		lblEvacuationAreaName.setText( loc.getString( "Evacuation.Name" ) );
		lblEvacuationAttractivity.setText( loc.getString( "Evacuation.Attractivity" ) );
	}
}
