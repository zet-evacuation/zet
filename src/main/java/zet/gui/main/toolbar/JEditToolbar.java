/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package zet.gui.main.toolbar;

import org.zetool.common.localization.Localization;
import org.zetool.common.localization.LocalizationManager;
import org.zetool.common.localization.Localized;
import gui.GUIControl;
import gui.ZETLoader;
import org.zet.components.model.editor.CoordinateTools;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.zetool.components.framework.Button;
import zet.gui.GUILocalization;
import org.zet.components.model.editor.selectors.ComboBoxRenderer;
import org.zet.components.model.editor.floor.ZetObjectTypes;
import org.zet.components.model.editor.panel.AbstractChangeEvent;
import org.zet.components.model.editor.panel.ChangeListener;
import org.zet.components.model.editor.style.GraphicsStyle;

/**
 * The class {@code JEditToolbar} ...
 *
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings("serial")
public class JEditToolbar extends JToolBar implements ActionListener, PopupMenuListener, KeyListener, Localized {

    public enum ToolbarAction {
        Selection, CreatePointwise, CreateRectangle, SelectZetObjectType
    }
    
    public static class ToolbarEvent extends AbstractChangeEvent<ToolbarAction> {

        public ToolbarEvent(Object source, ToolbarAction type) {
            super(source, type);
        }
    }
    

    public void addChangeListener(ChangeListener<ToolbarEvent> l) {
        listenerList.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener<ToolbarEvent> l) {
        listenerList.remove(ChangeListener.class, l);
    }

    protected void fireChangeEvent(ToolbarEvent c) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (c == null) {
                    throw new IllegalArgumentException("ChangeEvent null!");
                }
                ((ChangeListener<ToolbarEvent>) listeners[i + 1]).changed(c);
            }
        }
    }    
    
    /**
     * The localization class.
     */
    static final Localization loc = GUILocalization.loc;
    private JButton btnExit;
    private JButton btnOpen;
    private JButton btnSave;
    private JToggleButton btnEditSelect;
    private JToggleButton btnEditPointwise;
    private JToggleButton btnEditRectangled;
    private JLabel lblAreaType;
    private JComboBox<ZetObjectTypes> cbxEdit;
    private JButton btnZoomIn;
    private JButton btnZoomOut;
    private JTextField txtZoomFactor;
    private JButton btnRasterize;
    private ZetObjectTypes zetObjectType;
    /**
     * Model for the edit-mode combo box.
     */
    ///private EditComboBoxModel1 editSelector1;
    private EditComboBoxModel editSelector;
    /**
     * The number format used to display the zoom factor in the text field.
     */
    private NumberFormat nfZoom = NumberFormat.getPercentInstance();    // Main window components
    private final GUIControl control;

    /**
     * Creates a new instance of {@code JEditToolbar}.
     *
     * @param control
     * @param editStatus the object storing the current status of the editing
     */
    public JEditToolbar(GUIControl control) {
        this.control = control;

        createEditToolBar();
    }

    /**
     * Creates the {@code JToolBar} for the edit mode.
     */
    private void createEditToolBar() {
        loc.setPrefix("gui.toolbar.");
        ButtonGroup editModeGroup = new ButtonGroup();

        btnExit = Button.newButton(ZETIconSet.Exit.icon(), this, "exit", loc.getString("Exit"));
        add(btnExit);
        addSeparator();

        btnOpen = Button.newButton(ZETIconSet.Open.icon(), this, "loadProject", loc.getString("Open"));
        add(btnOpen);
        btnSave = Button.newButton(ZETIconSet.Save.icon(), this, "saveProject", loc.getString("Save"));
        add(btnSave);
        addSeparator();

        btnEditSelect = Button.newButton(ZETIconSet.EditSelect.icon(), this, "editSelect", loc.getString("Edit.SelectionMode"), true); //@// editStatus.getEditMode() == EditMode.Selection );
        add(btnEditSelect);
        editModeGroup.add(btnEditSelect);
        btnEditSelect.setSelected(true);
        btnEditPointwise = Button.newButton(ZETIconSet.EditDrawPointwise.icon(), this, "editPointwise", loc.getString("Edit.PointSequence"), false); //@// editStatus.getEditMode() == EditMode.CreationPointWise );
        add(btnEditPointwise);
        editModeGroup.add(btnEditPointwise);
        btnEditRectangled = Button.newButton(ZETIconSet.EditDrawRectangled.icon(), this, "editRectangled", loc.getString("Edit.DragCreate"), false); //@// editStatus.getEditMode() == EditMode.CreationRectangle );
        add(btnEditRectangled);
        editModeGroup.add(btnEditRectangled);

        add(new JLabel(" ")); //Spacer
        lblAreaType = new JLabel(loc.getString("Edit.AreaTypeLabel"));
        add(lblAreaType);

        editSelector = new EditComboBoxModel();
        cbxEdit = new JComboBox<>();
        cbxEdit.setToolTipText(loc.getString("Edit.AreaType"));
        cbxEdit.setModel( editSelector );
        cbxEdit.setMaximumSize(new Dimension(250, cbxEdit.getPreferredSize().height));
        cbxEdit.setPreferredSize(new Dimension(250, cbxEdit.getPreferredSize().height));
        cbxEdit.setMaximumRowCount(25);
        cbxEdit.setRenderer(new EditComboBoxRenderer());
        cbxEdit.addPopupMenuListener(this);
        cbxEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                zetObjectType = (ZetObjectTypes)cbxEdit.getSelectedItem();
                fireChangeEvent(new ToolbarEvent(this, ToolbarAction.SelectZetObjectType));
            }
        });
        add(cbxEdit);

//        editSelector1 = new EditComboBoxModel1();
//        cbxEdit1 = new JComboBox<>();
//        cbxEdit1.setMaximumRowCount( 25 );
//        cbxEdit1.setMaximumSize( new Dimension( 250, cbxEdit1.getPreferredSize().height ) );
//        cbxEdit1.setPreferredSize( new Dimension( 250, cbxEdit1.getPreferredSize().height ) );
//        cbxEdit1.setAlignmentX( 0 );
//        cbxEdit1.setToolTipText( loc.getString( "Edit.AreaType" ) );
//        cbxEdit1.setModel( editSelector1 );
//        cbxEdit1.setRenderer( new EditComboBoxRenderer1() );
        // Don't use an item/change listener here, because then we can't capture the event
        // that the user re-selects the same entry as before
//        cbxEdit1.addPopupMenuListener( this );
//        add( cbxEdit1 );
        addSeparator();

        btnZoomIn = Button.newButton(ZETIconSet.ZoomIn.icon(), this, "zoomIn", loc.getString("Edit.ZoomIn"));
        add(btnZoomIn);
        btnZoomOut = Button.newButton(ZETIconSet.ZoomOut.icon(), this, "zoomOut", loc.getString("Edit.ZoomOut"));
        add(btnZoomOut);
        add(new JLabel(" "));
        txtZoomFactor = new JTextField(nfZoom.format(CoordinateTools.getZoomFactor()));
        txtZoomFactor.setToolTipText(loc.getString("Edit.ZoomTextBox"));
        txtZoomFactor.setMaximumSize(new Dimension(40, txtZoomFactor.getPreferredSize().height));
        txtZoomFactor.addKeyListener(this);
        txtZoomFactor.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                updateZoomFactor();
            }
        });
        add(txtZoomFactor);
        addSeparator();

        btnRasterize = Button.newButton(ZETIconSet.Rasterize.icon(), this, "rasterize", loc.getString("Edit.Rasterize"));
        add(btnRasterize);

        loc.setPrefix("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "exit":
                control.exit();
                break;
            case "loadProject":
                control.loadProject();
                break;
            case "saveProject":
                control.saveProject();
                break;
            case "newProject":
                control.newProject();
                break;
            case "editSelect":
                fireChangeEvent(new ToolbarEvent(this, ToolbarAction.Selection));
                //editStatus.setEditMode( EditMode.Selection );
                break;
            case "editPointwise":
                fireChangeEvent(new ToolbarEvent(this, ToolbarAction.CreatePointwise));
                //editStatus.setEditMode( EditMode.CreationPointWise );
                break;
            case "editRectangled":
                fireChangeEvent(new ToolbarEvent(this, ToolbarAction.CreateRectangle));
                //editStatus.setEditMode( EditMode.CreationRectangle );
                break;
            case "zoomIn":
                control.setZoomFactor(Math.min(0.4, CoordinateTools.getZoomFactor() * 2));
                break;
            case "zoomOut":
                control.setZoomFactor(Math.max(0.00004, CoordinateTools.getZoomFactor() / 2));
                break;
            case "rasterize":
                control.rasterize();
                break;
            default:
                ZETLoader.sendError(loc.getString("gui.UnknownCommand") + " '" + e.getActionCommand() + "'. " + loc.getString("gui.ContactDeveloper"));
                break;
        }
    }

    public ZetObjectTypes getZetObjectType() {
        return zetObjectType;
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        ZetObjectTypes currentEditMode = (ZetObjectTypes) editSelector.getSelectedItem();

        //control.showArea( currentEditMode.);
        // Einblenden der gewählten Area, falls ausgeblendet
        switch (currentEditMode) {
//            case AssignmentAreaCreation:
//            case AssignmentAreaCreationPointwise:
//                control.showArea( Areas.Assignment );
//                break;
//            case DelayAreaCreation:
//            case DelayAreaCreationPointwise:
//                control.showArea( Areas.Delay );
//                break;
//            case StairAreaCreation:
//            case StairAreaCreationPointwise:
//                control.showArea( Areas.Stair );
//                break;
//            case EvacuationAreaCreation:
//            case EvacuationAreaCreationPointwise:
//                control.showArea( Areas.Evacuation );
//                break;
//            case InaccessibleAreaCreation:
//            case InaccessibleAreaCreationPointwise:
//                control.showArea( Areas.Inaccessible );
//                break;
//            case SaveAreaCreation:
//            case SaveAreaCreationPointwise:
//                control.showArea( Areas.Save );
//                break;
//            case TeleportAreaCreation:
//            case TeleportAreaCreationPointwise:
//                control.showArea( Areas.Teleportation );
//                break;
//            default:
//                break;
        }
//        updateAreaVisiblity();
//        if( editView != null && lastEditMode == currentEditMode ) {
//            editView.setEditMode( currentEditMode );
//            btnEditSelect.setSelected( false );
//            btnEditPointwise.setSelected( creationType == EditModeOld.Type.CREATION_POINTWISE );
//            btnEditRectangled.setSelected( creationType == EditModeOld.Type.CREATION_RECTANGLED );
//
//        }
//        lastEditMode = currentEditMode;
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_ENTER) {
            return;
        }
        updateZoomFactor();
    }

    /**
     * Reads the current value of the zoom factor text field and sets the zoom factor. If the last character is '%' it
     * is removed. It is possible to insert real and integer values.
     */
    private void updateZoomFactor() {
        NumberFormat nf = LocalizationManager.getManager().getIntegerConverter();
        String text = txtZoomFactor.getText();
        char c = text.charAt(text.length() - 1);
        boolean percent = false;
        if (c == '%') {
            StringBuffer sb = new StringBuffer(text).delete(text.length() - 1, text.length());
            text = sb.toString();
            percent = true;
        }
        try {
            double val = nf.parse(text).doubleValue();
            if (val < 1 && percent == false) {
                val *= 100;
            }
            val /= 2.5d;
            control.setZoomFactor(val / 100);
        } catch (ParseException ex2) {
            ZETLoader.sendError(loc.getString("gui.error.NonParsableNumber"));
        } catch (IllegalArgumentException ex) {
            ZETLoader.sendError(loc.getString(ex.getLocalizedMessage()));
        }
    }

    public void setZoomFactorText(double zoomFactor) {
        // Pretend a smaller Zoom factor, so that the user is actually only zomming
        // in the range [0,0.25]. This is still close enough for the user.
        txtZoomFactor.setText(nfZoom.format(zoomFactor * 2.5d));
    }

    public void setEditSelectionSelected(boolean selected) {
        btnEditSelect.setSelected(selected);
    }

    public void setEditPointwiseSelected(boolean selected) {
        btnEditPointwise.setSelected(selected);
    }

    public void setEditRectangledSelected(boolean selected) {
        btnEditRectangled.setSelected(selected);
    }

    /**
     * Changes the appearance of the GUI to the selected language.
     *
     * @see de.tu_berlin.math.coga.common.localization.Localization
     */
    @Override
    public void localize() {
        loc.setPrefix("gui.toolbar.");
        btnExit.setToolTipText(loc.getString("Exit"));
        btnOpen.setToolTipText(loc.getString("Open"));
        btnSave.setToolTipText(loc.getString("Save"));
        btnEditSelect.setToolTipText(loc.getString("Edit.SelectionMode"));
        btnEditPointwise.setToolTipText(loc.getString("Edit.PointSequence"));
        btnEditRectangled.setToolTipText(loc.getString("Edit.DragCreate"));
        lblAreaType.setText(loc.getString("Edit.AreaTypeLabel"));
        cbxEdit.setToolTipText(loc.getString("Edit.AreaType"));
        btnZoomIn.setToolTipText(loc.getString("Edit.ZoomIn"));
        btnZoomOut.setToolTipText(loc.getString("Edit.ZoomOut"));
        txtZoomFactor.setToolTipText(loc.getString("Edit.ZoomTextBox"));
        btnRasterize.setToolTipText(loc.getString("Edit.Rasterize"));
        loc.clearPrefix();
    }

    private static class EditComboBoxModel extends DefaultComboBoxModel<ZetObjectTypes> {
    //private EditStatus editStatus;
        private EditComboBoxModel() {
        //    this.editStatus = editStatus;
            for( ZetObjectTypes e : ZetObjectTypes.values() )
                addElement( e );
        }

        @Override
        public void setSelectedItem(Object object) {
            super.setSelectedItem(object);
            //    editStatus.setZetObjectType( (ZetObjectTypes)object );
        }

    }

    /**
     * This class can display EditModeOld Objects in a JComboBox.
     */
    @SuppressWarnings("serial")
    private class EditComboBoxRenderer extends ComboBoxRenderer<ZetObjectTypes> {
        private final GraphicsStyle graphicsStyle = new GraphicsStyle() {
        };
        @Override
        public Component getListCellRendererComponent(JList<? extends ZetObjectTypes> list, ZetObjectTypes value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel me = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setHorizontalAlignment(LEFT);

            if (value != null) {
                Color drawColor = value.isArea() ? graphicsStyle.getColorForArea(value.area) : graphicsStyle.getWallColor();
                Color background = drawColor.equals(Color.BLACK) ? getBackground() : drawColor;
                Color foreground = getForeground();
                if (!isSelected) {
                    setBackground(background);
                    setForeground(foreground);
                } else {
                    setBackground(foreground);
                    setForeground(background);
                }
                setText(" " + value.toString());
            }
            return this;
        }
    }
}
