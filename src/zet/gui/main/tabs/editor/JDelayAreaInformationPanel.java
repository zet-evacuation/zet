/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zet.gui.main.tabs.editor;

import de.tu_berlin.math.coga.components.framework.Button;
import ds.z.DelayArea;
import info.clearthought.layout.TableLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
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
public class JDelayAreaInformationPanel extends JInformationPanel<DelayArea> {
	private JButton btnDelaySetDefault;
	private JLabel lblDelayFactor;
	private JLabel lblDelayType;
	private JComboBox<DelayArea.DelayType> cbxDelayType;
	private JTextField txtDelayFactor;

		public JDelayAreaInformationPanel() {
		super( new double[] {TableLayout.FILL},
						new double[] {TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
							TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
							TableLayout.PREFERRED, 20, TableLayout.FILL
						} );
		init();
	}

	private void init() {
		int row = 0;

		// Delay-Selector
		lblDelayType = new JLabel( loc.getString( "Delay.Type" ) + ":" );
		this.add( lblDelayType, "0, " + row++ );
		cbxDelayType = new JComboBox<>( new DefaultComboBoxModel<>( DelayArea.DelayType.values() ) );
		cbxDelayType.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged( ItemEvent e ) {
				if( e.getStateChange() == ItemEvent.SELECTED )
					;
//					((DelayArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setDelayType( (DelayArea.DelayType)e.getItem() );
			}
		} );
		cbxDelayType.setRenderer( new ComboBoxRenderer<DelayArea.DelayType>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent( JList<? extends DelayArea.DelayType> list, DelayArea.DelayType value, int index, boolean isSelected, boolean cellHasFocus ) {
				//JLabel me = (JLabel)
				super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				if( value != null )
					setText( value.description );
				return this;
			}
			/** Prohibits serialization. */
			private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
				throw new UnsupportedOperationException( "Serialization not supported" );
			}
		} );
		this.add( cbxDelayType, "0, " + row++ );
		row++;

		// Delay-Factor
		lblDelayFactor = new JLabel( loc.getString( "Delay.Factor" ) );
		this.add( lblDelayFactor, "0, " + row++ );
		txtDelayFactor = new JTextField( " " );
		txtDelayFactor.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtDelayFactor.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
;
//					try {
//						((DelayArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setSpeedFactor( nfFloat.parse( txtDelayFactor.getText() ).doubleValue() );
//					} catch( ParseException ex ) {
//						ZETLoader.sendError( loc.getString( "gui.error.NonParsableFloatString" ) );
//					} catch( IllegalArgumentException ex ) {
//						ZETLoader.sendError( ex.getLocalizedMessage() );
//					}
			}
		} );
		this.add( txtDelayFactor, "0, " + row++ );
		row++;

		btnDelaySetDefault = Button.newButton( loc.getString( "Delay.TypeDefault" ), loc.getString( "Delay.TypeDefault.ToolTip" ) );
		btnDelaySetDefault.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
//				DelayArea a = (DelayArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon();
//				a.setSpeedFactor( a.getDelayType().defaultSpeedFactor );
//				txtDelayFactor.setText( nfFloat.format( a.getSpeedFactor() ) );
			}
		} );
		this.add( btnDelaySetDefault, "0, " + row++ );

	}

	@Override
	public void update() {
		txtDelayFactor.setText( nfFloat.format( current.getSpeedFactor() ) );
		cbxDelayType.setSelectedItem( current.getDelayType() );
	}

	@Override
	public void localize() {
		lblDelayType.setText( loc.getString( "Delay.Type" ) + ":" );
		lblDelayFactor.setText( loc.getString( "Delay.Factor" ) );
		btnDelaySetDefault.setText( loc.getString( "Delay.TypeDefault" ) );
		btnDelaySetDefault.setToolTipText( loc.getString( "Delay.TypeDefault.ToolTip" ) );
	}
}
