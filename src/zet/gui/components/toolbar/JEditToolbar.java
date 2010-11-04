/*
 * JEditToolbar.java
 * Created 16.07.2010, 10:24:23
 */
package zet.gui.components.toolbar;

import de.tu_berlin.math.coga.common.localization.Localization;
import gui.GUIControl;
import gui.ZETMain;
import zet.gui.components.model.ComboBoxRenderer;
import gui.components.framework.Button;
import gui.components.framework.IconSet;
import gui.editor.AreaVisibility;
import gui.editor.CoordinateTools;
import zet.gui.components.tabs.editor.EditMode;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * The class <code>JEditToolbar</code> ...
 * @author Jan-Philipp Kappmeier
 */
public class JEditToolbar extends JToolBar implements ActionListener, PopupMenuListener, KeyListener {
	/** The localization class. */
	static final Localization loc = Localization.getInstance();
	private JButton btnExit;
	private JButton btnOpen;
	private JButton btnSave;
	private JButton btnEditSelect;
	private JButton btnEditPointwise;
	private JButton btnEditRectangled;
	private JLabel lblAreaType;
	private JComboBox cbxEdit;
	private JButton btnZoomIn;
	private JButton btnZoomOut;
	private JTextField txtZoomFactor;
	private JButton btnRasterize;
	/** Model for the edit-mode combo box. */
	private EditComboBoxModel editSelector;
	/** The number format used to display the zoom factor in the text field. */
	private NumberFormat nfZoom = NumberFormat.getPercentInstance();	// Main window components
	private final GUIControl control;
	private EditMode.Type creationType = EditMode.Type.CreationPointwise;

	/**
	 * Creates a new instance of <code>JEditToolbar</code>.
	 *
	 * @param control
	 */
	public JEditToolbar( GUIControl control ) {
		this.control = control;
		createEditToolBar();
		control.setEditToolbar( this );
	}

	/**
	 * Creates the <code>JToolBar</code> for the edit mode.
	 */
	private void createEditToolBar() {
		loc.setPrefix( "gui.editor.JEditor." );

		btnExit = Button.newButton( IconSet.Exit, this, "exit", loc.getString( "toolbarTooltipExit" ) );
		add( btnExit );
		addSeparator();

		btnOpen = Button.newButton( IconSet.Open, this, "loadProject", loc.getString( "toolbarTooltipOpen" ) );
		add( btnOpen );
		btnSave = Button.newButton( IconSet.Save, this, "saveProject", loc.getString( "toolbarTooltipSave" ) );
		add( btnSave );
		addSeparator();

		btnEditSelect = Button.newButton( IconSet.EditSelect, this , "editSelect", loc.getString( "toolbarTooltipSelectionMode" ) );
		add( btnEditSelect );
		btnEditSelect.setSelected( true );
		btnEditPointwise = Button.newButton( IconSet.EditDrawPointwise, this, "editPointwise", loc.getString( "toolbarTooltipPointSequence" ) );
		add( btnEditPointwise );
		btnEditRectangled = Button.newButton( IconSet.EditDrawRectangled, this, "editRectangled", loc.getString( "toolbarTooltipDragCreate" ) );
		add( btnEditRectangled );

		add( new JLabel( " " ) ); //Spacer
		lblAreaType = new JLabel( loc.getString( "labelAreaType" ) );
		add( lblAreaType );
		editSelector = new EditComboBoxModel();
		cbxEdit = new JComboBox();
		cbxEdit.setMaximumRowCount( 25 );
		cbxEdit.setMaximumSize( new Dimension( 250, cbxEdit.getPreferredSize().height ) );
		cbxEdit.setPreferredSize( new Dimension( 250, cbxEdit.getPreferredSize().height ) );
		cbxEdit.setAlignmentX( 0 );
		loc.setPrefix( "gui.editor.JEditor." );
		cbxEdit.setToolTipText( loc.getString( "toolbarTooltipAreaType" ) );
		cbxEdit.setModel( editSelector );
		cbxEdit.setRenderer( new EditComboBoxRenderer() );
		// Don't use an item/change listener here, because then we can't capture the event
		// that the user re-selects the same entry as before
		cbxEdit.addPopupMenuListener( this );
		add( cbxEdit );
		addSeparator();

		btnZoomIn = Button.newButton( IconSet.ZoomIn, this, "zoomIn", loc.getString( "toolbarTooltipZoomIn" ) );
		add( btnZoomIn );
		btnZoomOut = Button.newButton( IconSet.ZoomOut, this, "zoomOut", loc.getString( "toolbarTooltipZoomOut" ) );
		add( btnZoomOut );
		add( new JLabel( " " ) );
		txtZoomFactor = new JTextField( nfZoom.format( CoordinateTools.getZoomFactor() ) );
		txtZoomFactor.setToolTipText( loc.getString( "toolbarTooltipZoomTextBox" ) );
		txtZoomFactor.setMaximumSize( new Dimension( 40, txtZoomFactor.getPreferredSize().height ) );
		txtZoomFactor.addKeyListener( this );
		txtZoomFactor.addFocusListener( new java.awt.event.FocusAdapter() {
			@Override
			public void focusLost( java.awt.event.FocusEvent evt ) {
				updateZoomFactor();
			}
		} );
		add( txtZoomFactor );
		addSeparator();

		btnRasterize = Button.newButton( IconSet.Rasterize, this, "rasterize", loc.getString( "toolbarTooltipRasterize" ) );
		add( btnRasterize );

		loc.setPrefix( "" );
	}

	public void actionPerformed( ActionEvent e ) {
		if( e.getActionCommand().equals( "exit" ) ) {
			control.exit();
		} else if( e.getActionCommand().equals( "loadProject" ) ) {
			control.loadProject();
		} else if( e.getActionCommand().equals( "saveProject" ) ) {
			control.saveProject();
		} else if( e.getActionCommand().equals( "newProject" ) ) {
			control.newProject();
		} else if( e.getActionCommand().equals( "editSelect" ) ) {
			control.setEditMode( EditMode.Selection );
						//				btnEditSelect.setSelected( true );
						//				btnEditPointwise.setSelected( false );
						//				btnEditRectangled.setSelected( false );
						//				editView.setEditMode( EditMode.Selection );
						//				sendReady();
		} else if( e.getActionCommand().equals( "editPointwise" ) ) {

						//				btnEditSelect.setSelected( false );
						//				btnEditPointwise.setSelected( true );
						//				btnEditRectangled.setSelected( false );
										creationType = EditMode.Type.CreationPointwise;
										editSelector.rebuild();
										control.setEditMode( (EditMode)editSelector.getSelectedItem() );
						//				editView.setEditMode( (EditMode)editSelector.getSelectedItem() );
						//				ZETMain.sendMessage( "Wählen sie die Koordinaten." ); // TODO loc
		} else if( e.getActionCommand().equals( "editRectangled" ) ) {
						//				btnEditSelect.setSelected( false );
						//				btnEditPointwise.setSelected( false );
						//				btnEditRectangled.setSelected( true );
										creationType = EditMode.Type.CreationRectangled;
										editSelector.rebuild();
										control.setEditMode( (EditMode)editSelector.getSelectedItem() );
						//				ZETMain.sendMessage( "Wählen sie die Koordinaten." ); // TODO loc
		} else if( e.getActionCommand().equals( "zoomIn" ) ) {
			control.setZoomFactor( Math.min( 0.4, CoordinateTools.getZoomFactor() * 2 ) );
		} else if( e.getActionCommand().equals( "zoomOut" ) ) {
			control.setZoomFactor( Math.max( 0.00004, CoordinateTools.getZoomFactor() / 2 ) );
		} else
			ZETMain.sendError( loc.getString( "gui.UnknownCommand" ) + " '" + e.getActionCommand() + "'. " + loc.getString( "gui.ContactDeveloper" ) );
	}



	public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
	}

	public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
		EditMode currentEditMode = (EditMode)editSelector.getSelectedItem();


		// Einblenden der gewählten Area, falls ausgeblendet
		switch( currentEditMode ) {
			case AssignmentAreaCreation:
			case AssignmentAreaCreationPointwise:
				control.showArea( AreaVisibility.Assignment );
				break;
			case DelayAreaCreation:
			case DelayAreaCreationPointwise:
				control.showArea( AreaVisibility.Delay );
				break;
			case StairAreaCreation:
			case StairAreaCreationPointwise:
				control.showArea( AreaVisibility.Stair );
				break;
			case EvacuationAreaCreation:
			case EvacuationAreaCreationPointwise:
				control.showArea( AreaVisibility.Evacuation );
				break;
			case InaccessibleAreaCreation:
			case InaccessibleAreaCreationPointwise:
				control.showArea( AreaVisibility.Inaccessible );
				break;
			case SaveAreaCreation:
			case SaveAreaCreationPointwise:
				control.showArea( AreaVisibility.Save );
				break;
			case TeleportAreaCreation:
			case TeleportAreaCreationPointwise:
				control.showArea( AreaVisibility.Teleport );
				break;
			default:
				break;
		}
//		updateAreaVisiblity();
//		if( editView != null && lastEditMode == currentEditMode ) {
//			editView.setEditMode( currentEditMode );
//			btnEditSelect.setSelected( false );
//			btnEditPointwise.setSelected( creationType == EditMode.Type.CREATION_POINTWISE );
//			btnEditRectangled.setSelected( creationType == EditMode.Type.CREATION_RECTANGLED );
//
//		}
//		lastEditMode = currentEditMode;
	}

	public void popupMenuCanceled( PopupMenuEvent e ) {
	}

	public void keyTyped( KeyEvent e ) {
	}

	public void keyPressed( KeyEvent e ) {
	}

	public void keyReleased( KeyEvent e ) {
		if( e.getKeyCode() != KeyEvent.VK_ENTER )
			return;
		updateZoomFactor();
	}

	/**
	 * Reads the current value of the zoom factor text field and sets the
	 * zoom factor. If the last character is '%' it is removed. It is possible to
	 * insert real and integer values.
	 */
	private void updateZoomFactor() {
		NumberFormat nf = NumberFormat.getNumberInstance( loc.getLocale() );
		String text = txtZoomFactor.getText();
		char c = text.charAt( text.length() - 1 );
		boolean percent = false;
		if( c == '%' ) {
			StringBuffer sb = new StringBuffer( text ).delete( text.length() - 1, text.length() );
			text = sb.toString();
			percent = true;
		}
		try {
			double val = nf.parse( text ).doubleValue();
			if( val < 1 && percent == false )
				val = val * 100;
			val = val / 2.5d;
			control.setZoomFactor( val / 100 );
		} catch( ParseException ex2 ) {
			ZETMain.sendError( loc.getString( "gui.error.NonParsableNumber" ) );
		} catch( IllegalArgumentException ex ) {
			ZETMain.sendError( loc.getString( ex.getLocalizedMessage() ) );
		}
	}

	public void setZoomFactorText( double zoomFactor ) {
		// Pretend a smaller Zoom factor, so that the user is actually only zomming
		// in the range [0,0.25]. This is still close enough for the user.
		txtZoomFactor.setText( nfZoom.format( zoomFactor * 2.5d ) );
	}

	public void setEditSelectionSelected( boolean selected ) {
		btnEditSelect.setSelected( selected );
	}

	public void setEditPointwiseSelected( boolean selected ) {
		btnEditPointwise.setSelected( selected );
	}

	public void setEditRectangledSelected( boolean selected ) {
		btnEditRectangled.setSelected( selected );
	}

/**
	 * Changes the appearance of the GUI to the selected language.
	 * @see de.tu_berlin.math.coga.common.localization.Localization
	 */
	public void localize() {

		cbxEdit.setToolTipText( loc.getString( "toolbarTooltipAreaType" ) );
		btnExit.setToolTipText( loc.getString( "toolbarTooltipExit" ) );
		btnOpen.setToolTipText( loc.getString( "toolbarTooltipOpen" ) );
		btnSave.setToolTipText( loc.getString( "toolbarTooltipSave" ) );

		btnOpen.setToolTipText( loc.getString( "toolbarTooltipOpen" ) );
		btnSave.setToolTipText( loc.getString( "toolbarTooltipSave" ) );
		btnEditSelect.setToolTipText( loc.getString( "toolbarTooltipSelectionMode" ) );
		btnEditPointwise.setToolTipText( loc.getString( "toolbarTooltipPointSequence" ) );
		btnEditRectangled.setToolTipText( loc.getString( "toolbarTooltipDragCreate" ) );
		lblAreaType.setText( loc.getString( "labelAreaType" ) );
		btnZoomIn.setToolTipText( loc.getString( "toolbarTooltipZoomIn" ) );
		btnZoomOut.setToolTipText( loc.getString( "toolbarTooltipZoomOut" ) );
		txtZoomFactor.setToolTipText( loc.getString( "toolbarTooltipZoomTextBox" ) );
		btnRasterize.setToolTipText( loc.getString( "toolbarTooltipRasterize" ) );

	}
		/**
	 * This class serves as a model for the JComboBox that contains the EditModes.
	 */
	private class EditComboBoxModel extends DefaultComboBoxModel {
		/**
		 * Creates a new combo box model containing edit types that are of a
		 * specified type.
		 * @param type the type of the displayed edit modes
		 */
		public EditComboBoxModel() {
			rebuild();
		}

		final public void rebuild() {
			// In case that the creationType really changed we must restore the partner edit mode.
			// If we change to the same creation type as before, we must restore the old selection itself.
//			boolean restore_partner = getSelectedItem() != null
//							&& creationType != ((EditMode)getSelectedItem()).getType();
//			EditMode next_selection = restore_partner ? ((EditMode)getSelectedItem()).getPartnerMode() : (EditMode)getSelectedItem();

			// Build new edit mode list
			this.removeAllElements();
			for( EditMode e : EditMode.getCreationModes( creationType ) )
				addElement( e );

			// Restore the selection with the associated partner editmode if neccessary
//			if( next_selection != null )
//				setSelectedItem( next_selection );
		}

		// This was moved to a change listener too, to be able to capture selections that
		// dont change the selected value, but only re-select it. In any other case this part
		// of the code is used (that is also needed, because the popuplistener can't deal with
		// other selection types)
		@Override
		public void setSelectedItem( Object object ) {
			super.setSelectedItem( object );
			control.setEditMode( (EditMode) object );
		}
	}

	/**
	 * This class can display EditMode Objects in a JComboBox.
	 */
	private class EditComboBoxRenderer extends ComboBoxRenderer {
		@Override
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
			JLabel me = (JLabel)super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
			setHorizontalAlignment( LEFT );

			if( value != null ) {
				EditMode e = (EditMode)value;
				if( e.getEditorColor() != null && !e.getEditorColor().equals( Color.BLACK ) )
					setBackground( e.getEditorColor() );
				setText( e.toString() );
			}
			return this;
		}
	}
}
