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
package zet.gui.main.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.function.Function;

import javax.media.opengl.GLCapabilities;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import info.clearthought.layout.TableLayout;

import de.zet_evakuierung.visualization.ca.model.DynamicCellularAutomatonInformation;
import ds.PropertyContainer;
import gui.GUIControl;
import gui.ZETLoader;
import gui.visualization.AbstractVisualizationView;
import gui.visualization.VisualizationPanel;
import gui.visualization.control.ZETGLControl;
import org.zet.components.model.editor.editview.FloorComboBoxModel;
import org.zet.components.model.editor.floor.FloorViewModel;
import org.zet.components.model.editor.selectors.NamedComboBox;
import org.zetool.common.localization.Localization;
import org.zetool.common.localization.LocalizationManager;
import org.zetool.components.JArrayPanel;
import zet.gui.GUILocalization;
import zet.gui.components.model.PotentialSelectionModel;
import zet.gui.main.tabs.visualization.ZETVisualization;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JVisualizationView extends AbstractVisualizationView<ZETVisualization> {
    private final GUIControl guiControl;
    private Localization loc = GUILocalization.loc;
    /** The visualization panel. */
    private ZETVisualization visualization;
    /** A combo box that allows selecting the visible floor (if not all are visible). */
    private NamedComboBox<FloorViewModel> floorSelector;
    /** A combo box selecting the currently visible potential */
    private JComboBox potentialSelector;
    /** A combo box for selecting the information displayed on the head of the individuals. */
    private JComboBox<DynamicCellularAutomatonInformation.HeadInformation> headColorSelector;
    /** The label for the floor combo box. */
    private JLabel lblFloorSelector;
    /** The label for the potential combo box. */
    private JLabel lblPotentialSelector;
    /** The label for the combo box for the information on the heads. */
    private JLabel lblHeadSelector;
    /** The currently selected floor */
    private int selectedFloor = 0;
    /** A text field containing the position of the camera. */
    private JTextField txtCameraPosition;
    /** A text field containing the view vector of the camera. */
    private JTextField txtCameraView;
    /** A text field containing the up vector of the camera. */
    private JTextField txtCameraUp;
    /** The label for the camera position. */
    private JLabel lblCameraPosition;
    /** The label for the view vector of the camera. */
    private JLabel lblCameraView;
    /** The label for the up vector of the camera. */
    private JLabel lblCameraUp;
    /** A button that sets a new camera position. */
    private JButton setCameraPosition;
    /** A button that resets the camera position to a fixed coordinate. */
    private JButton resetCameraPosition;

    /**
     * Map of head information types to visualization localization tags.
     */
    private final static EnumMap<DynamicCellularAutomatonInformation.HeadInformation, String> HEAD_INFORMATION_LOCALIZATION
            = new EnumMap<DynamicCellularAutomatonInformation.HeadInformation, String>(DynamicCellularAutomatonInformation.HeadInformation.class) {
        {
            put(DynamicCellularAutomatonInformation.HeadInformation.NOTHING, "gui.VisualizationPanel.HeadInformation.Nothing");
            put(DynamicCellularAutomatonInformation.HeadInformation.PANIC, "gui.VisualizationPanel.HeadInformation.Panic");
            put(DynamicCellularAutomatonInformation.HeadInformation.SPEED, "gui.VisualizationPanel.HeadInformation.Speed");
            put(DynamicCellularAutomatonInformation.HeadInformation.EXHAUSTION, "gui.VisualizationPanel.HeadInformation.Exhaustion");
            put(DynamicCellularAutomatonInformation.HeadInformation.ALARMED, "gui.VisualizationPanel.HeadInformation.Alarmed");
            put(DynamicCellularAutomatonInformation.HeadInformation.CHOSEN_EXIT, "gui.VisualizationPanel.HeadInformation.ChosenExit");
            put(DynamicCellularAutomatonInformation.HeadInformation.REACTION_TIME, "gui.VisualizationPanel.HeadInformation.ReactionTime");
        }
    };

    private static GLCapabilities getCaps() {
        GLCapabilities caps = new GLCapabilities(null);
        caps.setSampleBuffers(true);
        caps.setNumSamples(4); // enable anti-antialiasing

        System.out.println(caps);
        return caps;
    }

    /**
     *
     * @param guiControl
     */
    public JVisualizationView(GUIControl guiControl) {
        super(new VisualizationPanel<>(new ZETVisualization(getCaps(), guiControl)));
        this.guiControl = guiControl;
        visualization = getGLContainer();
        addComponents();

        final JSlider slider = new JSlider();
        slider.setMinimum(-90);
        slider.setMaximum(90);
        slider.setValue(0);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ZETGLControl control = visualization.getControl();
                if (control == null) {
                    return;
                }
                if (slider.getValue() == 0) {
                    control.setSpeedFactor(1);
                } else if (slider.getValue() < 0) {
                    control.setSpeedFactor((10 - (-slider.getValue() * 0.1)) * 0.1);
                } else {
                    control.setSpeedFactor((slider.getValue() + 10) * 0.1);
                }
            }
        });
        @SuppressWarnings("UseOfObsoleteCollectionType") // hashtable has to be used here due to slider
        Hashtable<Integer, JComponent> table = new Hashtable<>();
        for (int i = 1; i <= 10; i++) {
            table.put(-i * 10, new JLabel(LocalizationManager.getManager().getFloatConverter().format((10 - i) * 0.1)));
        }
        table.put(0, new JLabel("1"));
        for (int i = 1; i < 10; i++) {
            table.put(i * 10, new JLabel("" + (i + 1)));
        }
        slider.setLabelTable(table);
        this.getLeftPanel().add(slider, BorderLayout.SOUTH);
        setFloorSelectorEnabled(!PropertyContainer.getGlobal().getAsBoolean("settings.gui.visualization.floors"));
    }

    @Override
    protected JPanel createEastBar() {
        double size[][]
                = // Columns
                {
                    {10, TableLayout.FILL, 10},
                    //Rows
                    {10,
                        TableLayout.PREFERRED, TableLayout.PREFERRED, 16,
                        TableLayout.PREFERRED, TableLayout.PREFERRED, 16,
                        TableLayout.PREFERRED, TableLayout.PREFERRED, 16,
                        TableLayout.PREFERRED, TableLayout.PREFERRED, 16,
                        TableLayout.PREFERRED, TableLayout.PREFERRED, 16,
                        TableLayout.FILL
                    }
                };
        final JPanel eastPanel = new JPanel(new TableLayout(size));

        floorSelector = new NamedComboBox<>(new FloorComboBoxModel(guiControl.getViewModel()));

        floorSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (PropertyContainer.getGlobal().getAsBoolean("settings.gui.visualization.floors")) {
                    return;
                }
                if (floorSelector.getSelectedIndex() >= 0) {
                    System.out.println("Ausgewählter Floor:" + floorSelector.getSelectedItem());
                    selectedFloor = floorSelector.getSelectedIndex();
                } else {
                    return;
                }
                if (visualization.getControl() != null) {
                    visualization.getControl().showFloor(selectedFloor);
                    getLeftPanel().getGLContainer().repaint();
                }
            }
        });
        int row = 1;

        if (loc == null) {
            loc = GUILocalization.loc;
        }

        lblFloorSelector = new JLabel(loc.getString("gui.EditPanel.Default.Floors") + ":");
        eastPanel.add(lblFloorSelector, "1, " + row++);
        eastPanel.add(floorSelector, "1, " + row++);
        row++;

        loc.setPrefix("gui.VisualizationPanel.");
        potentialSelector = new JComboBox();
        potentialSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getItem() == null || e.getStateChange() == ItemEvent.DESELECTED) {
                    return;
                }
                PotentialSelectionModel.PotentialEntry potentialEntry = (PotentialSelectionModel.PotentialEntry) e.getItem();
                guiControl.visualizationShowCellInformation(DynamicCellularAutomatonInformation.CellInformationDisplay.NO_POTENTIAL);
                visualization.getControl().activatePotential(potentialEntry.getPotential());
                visualization.getControl().showPotential(DynamicCellularAutomatonInformation.CellInformationDisplay.STATIC_POTENTIAL);
                getGLContainer().repaint();
            }
        });
        lblPotentialSelector = new JLabel(loc.getString("Potentials") + ":");
        eastPanel.add(lblPotentialSelector, "1, " + row++);
        eastPanel.add(potentialSelector, "1, " + row++);
        row++;

        headColorSelector = new JComboBox<>();
        CustomDefaultListCellRenderer<DynamicCellularAutomatonInformation.HeadInformation> llcr = new CustomDefaultListCellRenderer<>(
                headColorSelector.getRenderer(), headInformation -> HEAD_INFORMATION_LOCALIZATION.get(headInformation));
        headColorSelector.setRenderer(llcr);
        for (DynamicCellularAutomatonInformation.HeadInformation hi : DynamicCellularAutomatonInformation.HeadInformation.values()) {
            headColorSelector.addItem(hi);
        }
        headColorSelector.setSelectedIndex(1);
        headColorSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualization.getControl().showIndividualInformation((DynamicCellularAutomatonInformation.HeadInformation) (headColorSelector.getSelectedItem()));
                getLeftPanel().getGLContainer().repaint();
            }
        });
        lblHeadSelector = new JLabel(loc.getString("HeadInformation") + ":");
        eastPanel.add(lblHeadSelector, "1, " + row++);
        eastPanel.add(headColorSelector, "1, " + row++);
        row++;

        JArrayPanel cameraPanel = new JArrayPanel(1, 10);
        lblCameraPosition = new JLabel(loc.getString("Camera.Position"));
        cameraPanel.set(lblCameraPosition, 0, 0);
        txtCameraPosition = new JTextField("(0;0;0)");
        cameraPanel.set(txtCameraPosition, 0, 1);
        lblCameraView = new JLabel(loc.getString("Camera.View"));
        cameraPanel.set(lblCameraView, 0, 2);
        txtCameraView = new JTextField("(0;0;0)");
        cameraPanel.set(txtCameraView, 0, 3);
        lblCameraUp = new JLabel(loc.getString("Camera.Up"));
        cameraPanel.set(lblCameraUp, 0, 4);
        txtCameraUp = new JTextField("(0;0;0)");
        cameraPanel.set(txtCameraUp, 0, 5);

        cameraPanel.setRowHeight(6, 16);

        setCameraPosition = new JButton(loc.getString("Camera.SetPosition"));
        setCameraPosition.setToolTipText(loc.getString("Camera.SetPosition.ToolTip"));
        setCameraPosition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    visualization.getCamera().getPos().parse(txtCameraPosition.getText());
                    visualization.getCamera().getView().parse(txtCameraView.getText());
                    visualization.getCamera().getUp().parse(txtCameraUp.getText());
                    visualization.repaint();
                } catch (ParseException ex) {
                    ZETLoader.sendError(loc.getStringWithoutPrefix("gui.error.CameraParseError"));
                }
            }
        });
        cameraPanel.set(setCameraPosition, 0, 7);

        cameraPanel.setRowHeight(8, 16);

        resetCameraPosition = new JButton(loc.getString("Camera.ResetPosition"));
        resetCameraPosition.setToolTipText(loc.getString("Camera.ResetPosition.ToolTip"));
        resetCameraPosition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ZETLoader.sendError("Not supported yet.");
            }
        });
        cameraPanel.set(resetCameraPosition, 0, 9);

        cameraPanel.rebuild();
        eastPanel.add(cameraPanel, "1, " + row++);

        loc.clearPrefix();
        return eastPanel;
    }

    /**
     * Localizes the components on the east bar of the visualization view.
     */
    @Override
    public void localize() {
        lblFloorSelector.setText(loc.getString("gui.EditPanel.Default.Floors") + ":");
        loc.setPrefix("gui.VisualizationPanel.");
        lblPotentialSelector.setText(loc.getString("Potentials") + ":");
        lblHeadSelector.setText(loc.getString("HeadInformation") + ":");
        lblCameraPosition.setText(loc.getString("Camera.Position"));
        lblCameraView.setText(loc.getString("Camera.View"));
        lblCameraUp.setText(loc.getString("Camera.Up"));
        setCameraPosition.setText(loc.getString("Camera.SetPosition"));
        setCameraPosition.setToolTipText(loc.getString("Camera.SetPosition.ToolTip"));
        resetCameraPosition.setText(loc.getString("Camera.ResetPosition"));
        resetCameraPosition.setToolTipText(loc.getString("Camera.ResetPosition.ToolTip"));
        loc.clearPrefix();
    }

    /**
     * Updates the camera position information in the text fields.
     */
    public void updateCameraInformation() {
        System.err.println("TODO: correctly initialize visualization view!");
        if (txtCameraPosition == null) {
            return;
        }
        this.txtCameraPosition.setText(visualization.getCamera().getPos().toString());
        this.txtCameraView.setText(visualization.getCamera().getView().toString());
        this.txtCameraUp.setText(visualization.getCamera().getUp().toString());
    }

    /**
     * Enables and disables the floor selector on the right part of the view.
     *
     * @param val decides whether the floor selector is enabled or disabled
     */
    public final void setFloorSelectorEnabled(boolean val) {
        if (floorSelector != null) {
            floorSelector.setEnabled(val);
        }
    }

    /**
     * Updates the floor selection combo box on the right panel.
     */
    public void updateFloorSelector() {
        updateFloorSelector(floorSelector.getItemCount() > 0 ? 0 : -1);
    }

    public void updateFloorSelector(int floor) {
        //floorSelector.displayFloors( visualization.getControl().getFloorNames(), PropertyContainer.getGlobal().getAsBoolean( "editor.options.view.hideDefaultFloor" ) );
        if (floor > -1) {
            floorSelector.setSelectedIndex(floor);
        }
        selectedFloor = floorSelector.getSelectedIndex();
    }

    /**
     * Updates the potential selection combo box on the right panel.
     */
    public void updatePotentialSelector() {
        potentialSelector.setModel(new PotentialSelectionModel(visualization.getControl().getPotentials()));
    }

    /**
     * Unselects all elements of the potential selection box.
     */
    public void unselectPotentialSelector() {
        potentialSelector.setSelectedIndex(-1);
    }

    /**
     * Adds another {@code ItemListener} to the potential selection box. This can be used to access external GUI
     * elements.
     *
     * @param listener
     */
    public void addPotentialItemListener(ItemListener listener) {
        potentialSelector.addItemListener(listener);
    }

    /**
     * Returns the currently selected floor.
     *
     * @return the currently selected floor.
     */
    public int getSelectedFloorID() {
        return selectedFloor;
    }

    /**
     * Implementation of a cell renderer that uses the default cell renderer and replaces the displaced text from simple
     * {@link Object#toString() toString} to custom texts.
     * <p>
     * If the type of {@link Component} generated by the original renderer is not of type {@link JLabel}, the renderer
     * does nothing.</p>
     *
     * @param <T> the type of items that is rendered
     */
    public final class CustomDefaultListCellRenderer<T> implements ListCellRenderer<T> {

        private final ListCellRenderer<? super T> originalRenderer;
        private final Function<T, String> itemText;

        /**
         * Initializes the custom text renderer with an existing renderer. To work properly the {@code originalRenderer}
         * should be the default {@link javax.swing.JComboBox#getRenderer() combo box renderer}. More precise, the
         * displayed text is replaced if the
         * {@link javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean) component}
         * used to render is an instance of {@link JList}.
         *
         * @param originalRenderer the original renderer, should be a default list cell renderer
         * @param itemText function providing texts for inputs
         */
        public CustomDefaultListCellRenderer(ListCellRenderer<? super T> originalRenderer,
                Function<T, String> itemText) {
            this.originalRenderer = originalRenderer;
            this.itemText = itemText;
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends T> jList, T value, int index, boolean isSelected,
                boolean cellHasFocus) {
            Component c = originalRenderer.getListCellRendererComponent(jList, value, index, isSelected, cellHasFocus);
            if (c instanceof JLabel) {
                ((JLabel) c).setText(itemText.apply(value));
            }
            return c;
        }
    }
}
