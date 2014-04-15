
package zet.gui.main.tabs.editor;

import de.tu_berlin.coga.common.localization.LocalizationManager;
import de.tu_berlin.coga.zet.model.StairArea;
import de.tu_berlin.coga.zet.model.StairPreset;
import de.tu_berlin.coga.zet.model.ZLocalization;
import info.clearthought.layout.TableLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import zet.gui.main.JZetWindow;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JStairAreaInformationPanel extends JInformationPanel<StairArea> {
	private JTextField txtStairFactorUp;
	private JTextField txtStairFactorDown;

	private JLabel lblStairFactorUp;
	private JLabel lblStairFactorDown;
	/** A label for the stair speed preset. */
	private JLabel lblStairPreset;
	/** A selection box for the stair speed presets. */
	private JComboBox<StairPreset> cbxStairPresets;
	/** A label describing the current preset. */
	private JLabel lblStairPresetDescription;

	public JStairAreaInformationPanel() {
		super( new double[]{TableLayout.FILL},
						new double[]			{TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, 20,
				TableLayout.FILL
			}
 );
		init();
	}

	private void init() {
		int row = 0;

		// DelayFactor for going upwards
		lblStairFactorUp = new JLabel( loc.getString( "Stair.FactorUp" ) + ":" );
		this.add( lblStairFactorUp, "0, " + row++ );
		txtStairFactorUp = new JTextField( " " );
		txtStairFactorUp.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtStairFactorUp.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
					;
//					try {
//						((StairArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setSpeedFactorUp( nfFloat.parse( txtStairFactorUp.getText() ).doubleValue() );
//					} catch( ParseException ex ) {
//						ZETLoader.sendError( loc.getString( "gui.error.NonParsableFloatString" ) );
//					} catch( IllegalArgumentException ex ) {
//						ZETLoader.sendError( ex.getLocalizedMessage() );
//					}
			}
		} );
		this.add( txtStairFactorUp, "0, " + row++ );
		row++;

		// DelayFactor for going downwards
		lblStairFactorDown = new JLabel( loc.getString( "Stair.FactorDown" ) + ":" );
		this.add( lblStairFactorDown, "0, " + row++ );
		txtStairFactorDown = new JTextField( " " );
		txtStairFactorDown.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) {
				JZetWindow.setEditing( true );
			}

			@Override
			public void focusLost( FocusEvent e ) {
				JZetWindow.setEditing( false );
			}
		} );
		txtStairFactorDown.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed( KeyEvent e ) {
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
					;
//					try {
//						((StairArea)getLeftPanel().getMainComponent().getSelectedPolygons().get( 0 ).getPlanPolygon()).setSpeedFactorDown( nfFloat.parse( txtStairFactorDown.getText() ).doubleValue() );
//					} catch( ParseException ex ) {
//						ZETLoader.sendError( loc.getString( "gui.error.NonParsableFloatString" ) );
//					} catch( IllegalArgumentException ex ) {
//						ZETLoader.sendError( ex.getLocalizedMessage() );
//					}
			}
		} );
		this.add( txtStairFactorDown, "0, " + row++ );
		row++;

		// Add combo box with presets
		lblStairPreset = new JLabel( loc.getString( "Stair.Preset" ) + ":" );
		cbxStairPresets = new JComboBox<>();

		cbxStairPresets.setRenderer( new ListCellRenderer<StairPreset>() {
			protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
			@Override
			public Component getListCellRendererComponent( JList<? extends StairPreset> list, StairPreset value, int index, boolean isSelected, boolean cellHasFocus ) {
				JLabel presetLabel = (JLabel)defaultRenderer.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				presetLabel.setText( ZLocalization.loc.getString( presetLabel.getText() ) );
				return presetLabel;
			}

		});

		cbxStairPresets.addItem( StairPreset.Indoor );
		cbxStairPresets.addItem( StairPreset.Outdoor );
		this.add( lblStairPreset, "0, " + row++ );
		this.add( cbxStairPresets, "0, " + row++ );
		cbxStairPresets.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent arg0 ) {
				StairPreset sp = (StairPreset)cbxStairPresets.getSelectedItem();
				NumberFormat nf = LocalizationManager.getManager().getFloatConverter();
				txtStairFactorUp.setText( nf.format( sp.getSpeedFactorUp() ) );
				txtStairFactorDown.setText( nf.format( sp.getSpeedFactorDown() ) );
			}
		});
		lblStairPresetDescription = new JLabel( ZLocalization.loc.getString( ((StairPreset)cbxStairPresets.getSelectedItem()).getText() ) ) ;
		this.add( lblStairPresetDescription, "0, " + row++ );
		row++;

	}

	@Override
	public void update() {
		txtStairFactorUp.setText( nfFloat.format( current.getSpeedFactorUp() ) );
		txtStairFactorDown.setText( nfFloat.format( current.getSpeedFactorDown() ) );
	}


	@Override
	public void localize() {
		loc.setPrefix( "gui.EditPanel." );
		lblStairFactorUp.setText( loc.getString( "Stair.FactorUp" ) + ":" );
		lblStairFactorDown.setText( loc.getString( "Stair.FactorDown" ) + ":" );
		lblStairPreset.setText( loc.getString( "Stair.Preset" ) + ":" );
		lblStairPresetDescription.setText( ZLocalization.loc.getString( ((StairPreset)cbxStairPresets.getSelectedItem() ).getText() ) );
		loc.clearPrefix();
	}
}
