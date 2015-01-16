/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * StatisticPanel.java
 *
 *
 */
package statistic.graph.gui;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import javax.swing.JList;
import statistic.graph.gui.event.StatisticDiagramChangedEvent;
import statistic.graph.gui.event.StatisticSelectionChangedEvent;
import statistic.graph.gui.event.DiagramEvent;
import statistic.graph.gui.event.DiagramListener;
import org.zetool.graph.Node;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import statistic.graph.DynamicOperation;
import statistic.graph.IntegerDoubleMapping;
import statistic.graph.Operation;
import statistic.graph.DoubleOperation;
import statistic.graph.Statistic;
import statistic.graph.gui.event.DiagramAddedEvent;
import statistic.graph.gui.event.DiagramRemovedEvent;
import statistic.graph.gui.event.DiagramSequenceChangedEvent;
import statistic.graph.gui.event.DiagramTitleChangedEvent;
import statistic.graph.gui.event.ProfileEvent;
import statistic.graph.gui.event.ProfileListener;
import statistic.graph.gui.event.ProfileSelectionChangedEvent;
import statistic.graph.gui.event.StatisticAddedEvent;
import statistic.graph.gui.event.StatisticBaseValueChangedEvent;
import statistic.graph.gui.event.StatisticColorChangedEvent;
import statistic.graph.gui.event.StatisticDescriptionChangedEvent;
import statistic.graph.gui.event.StatisticEvent;
import statistic.graph.gui.event.StatisticListener;
import statistic.graph.gui.event.StatisticObjectOperationChangedEvent;
import statistic.graph.gui.event.StatisticObjectParameterChangedEvent;
import statistic.graph.gui.event.StatisticRemovedEvent;
import statistic.graph.gui.event.StatisticRunOperationChangedEvent;
import statistic.graph.gui.event.StatisticRunParameterChangedEvent;
import statistic.graph.gui.event.StatisticSequenceChangedEvent;

/**
 *
 * @author Martin Groß
 */
public class StatisticPanel extends JPanel implements DiagramListener, ProfileListener {

    private List<StatisticListener> listeners;
    private DisplayProfile profile;
    private List<DisplayableStatistic> statistics;

    public StatisticPanel() {
        initComponents();
        initListeners();
        listeners = new LinkedList<StatisticListener>();
        statistics = new LinkedList<DisplayableStatistic>();
        updateStatisticList();
        updateValueList();
        updateObjectOperationList();
        updateRunOperationList();
        updateAttributes();
    }

    public void addStatisticListener(StatisticListener listener) {
        listeners.add(listener);
    }

    public void removeStatisticListener(StatisticListener listener) {
        listeners.remove(listener);
    }

    private void fireStatisticEvent(StatisticEvent event) {
        for (StatisticListener listener : listeners) {
            listener.notify(event);
        }
    }

    public DisplayProfile getProfile() {
        return profile;
    }

    public void setProfile(DisplayProfile profile) {
        this.profile = profile;
        this.statistics = (profile != null) ? profile.getStatistics() : null;
        if (profile != null) {
            ((TitledBorder) getBorder()).setTitle("Statistiken");
        } else {
            ((TitledBorder) getBorder()).setTitle("Statistiken");
        }
        jButton1.setEnabled(profile != null);
        updateStatisticList();
        updateValueList();
        updateObjectOperationList();
        updateRunOperationList();
        updateAttributes();
    }

    public ProfileType getProfileType() {
        return profile.getType();
    }

    public Class<?> getValueType() {
        Statistic statistic = (Statistic) jComboBox1.getModel().getSelectedItem();
        return (statistic == null) ? null : statistic.range();
    }

    public DisplayableStatistic getSelectedStatistic() {
        return (DisplayableStatistic) jList1.getSelectedValue();
    }

    public boolean isStatisticSelected() {
        return jList1.getSelectedIndex() >= 0;
    }

    public void notify(DiagramEvent event) {
        DiagramData data = event.getDiagram();
        if (event instanceof DiagramAddedEvent) {
            ((DefaultComboBoxModel) jComboBox4.getModel()).addElement(data);
        } else if (event instanceof DiagramRemovedEvent) {
            ((DefaultComboBoxModel) jComboBox4.getModel()).removeElement(data);
        } else if (event instanceof DiagramSequenceChangedEvent) {
            DiagramData data2 = ((DiagramSequenceChangedEvent) event).getDiagram2();
            int index = ((DefaultComboBoxModel) jComboBox4.getModel()).getIndexOf(data);
            ((DefaultComboBoxModel) jComboBox4.getModel()).removeElement(data2);
            ((DefaultComboBoxModel) jComboBox4.getModel()).insertElementAt(data2, index);
            ((CustomComboBoxModel) jComboBox4.getModel()).fireContentsChanged(event, index, index + 1);
        } else if (event instanceof DiagramTitleChangedEvent) {
            int index = ((DefaultComboBoxModel) jComboBox4.getModel()).getIndexOf(data);
            ((CustomComboBoxModel) jComboBox4.getModel()).fireContentsChanged(event, index, index);
        }
    }

    public void notify(ProfileEvent event) {
        if (event instanceof ProfileSelectionChangedEvent) {
            setProfile(event.getProfile());
        }
    }

    private void initListeners() {
        jButton1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DisplayableStatistic statistic = new DisplayableStatistic();
                statistic.getAttributes().setName("Neue Statistik");
                statistics.add(statistic);
                updateStatisticList();
                jList1.getSelectionModel().setSelectionInterval(statistics.indexOf(statistic), statistics.indexOf(statistic));
                fireStatisticEvent(new StatisticAddedEvent(statistic));
            }
        });
        jButton2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DisplayableStatistic statistic = (DisplayableStatistic) jList1.getSelectedValue();
                DisplayableStatistic newStatistic = statistic.clone();
                newStatistic.getAttributes().setName("Kopie von " + statistic.getAttributes().getName());
                statistics.add(newStatistic);
                updateStatisticList();
                jList1.getSelectionModel().setSelectionInterval(statistics.indexOf(newStatistic), statistics.indexOf(newStatistic));
                fireStatisticEvent(new StatisticAddedEvent(newStatistic));
            }
        });
        jButton3.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int index = jList1.getSelectedIndex();
                DisplayableStatistic statistic = (DisplayableStatistic) jList1.getSelectedValue();
                statistics.remove(statistic);
                updateStatisticList();
                if (index < jList1.getModel().getSize()) {
                    jList1.getSelectionModel().setSelectionInterval(index, index);
                } else if (jList1.getModel().getSize() > 0) {
                    jList1.getSelectionModel().setSelectionInterval(jList1.getModel().getSize() - 1, jList1.getModel().getSize() - 1);
                }
                fireStatisticEvent(new StatisticRemovedEvent(statistic));
            }
        });
        jButton4.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int index = statistics.indexOf(jList1.getSelectedValue());
                DisplayableStatistic statistic = (DisplayableStatistic) jList1.getSelectedValue();
                DisplayableStatistic previous = (DisplayableStatistic) jList1.getModel().getElementAt(index - 1);
                statistics.remove(statistic);
                statistics.add(index - 1, statistic);
                updateStatisticList();
                jList1.getSelectionModel().setSelectionInterval(index - 1, index - 1);
                fireStatisticEvent(new StatisticSequenceChangedEvent(previous, statistic));
            }
        });
        jButton5.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int index = statistics.indexOf(jList1.getSelectedValue());
                DisplayableStatistic statistic = (DisplayableStatistic) jList1.getSelectedValue();
                DisplayableStatistic next = (DisplayableStatistic) jList1.getModel().getElementAt(index + 1);
                statistics.remove(statistic);
                statistics.add(index + 1, statistic);
                updateStatisticList();
                jList1.getSelectionModel().setSelectionInterval(index + 1, index + 1);
                fireStatisticEvent(new StatisticSequenceChangedEvent(statistic, next));
            }
        });
        jList1.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                jButton2.setEnabled(jList1.getSelectedIndex() >= 0);
                jButton3.setEnabled(jList1.getSelectedIndex() >= 0);
                jButton4.setEnabled(jList1.getSelectedIndex() > 0);
                jButton5.setEnabled(jList1.getSelectedIndex() >= 0 && jList1.getSelectedIndex() < jList1.getModel().getSize() - 1);
                updateValueList();
                updateObjectOperationList();
                updateRunOperationList();
                updateAttributes();
                if (isStatisticSelected()) {
                    jTextField4.setBackground(getSelectedStatistic().getAttributes().getColor());
                } else {
                    jTextField4.setBackground(null);
                }
                fireStatisticEvent(new StatisticSelectionChangedEvent((DisplayableStatistic) jList1.getSelectedValue()));
            }
        });
        jList1.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                DisplayableStatistic statistic = (DisplayableStatistic) value;
                if (!statistic.isInitialized()) {
                    setText(getText() + " (" + statistic.getReason() + ")");
                    if (isSelected) {
                        component.setBackground(new Color(255, 128, 128));
                    } else {
                        component.setBackground(new Color(255, 0, 0));
                    }
                }
                return component;
            }
        });
        jComboBox1.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                Statistic statistic = (Statistic) jComboBox1.getModel().getSelectedItem();
                Statistic oldStatistic = getSelectedStatistic().getStatistic().getStatistic();
                if (jTextField3.getText().equals("Neue Statistik") && statistic != null
                        || oldStatistic != null && jTextField3.getText().equals(oldStatistic.toString()) && statistic != null) {
                    jTextField3.setText(statistic.toString());
                    getSelectedStatistic().getAttributes().setName(jTextField3.getText());
                    ((CustomListModel) jList1.getModel()).fireContentsChanged(jList1.getModel(), jList1.getSelectedIndex(), jList1.getSelectedIndex());
                }
                if (statistic != oldStatistic) {
                    getSelectedStatistic().getStatistic().setStatistic(statistic);
                    updateObjectOperationList();
                    updateRunOperationList();
                    if (oldStatistic != null && !statistic.range().equals(oldStatistic.range())) {                        
                        getSelectedStatistic().getStatistic().setObjectOperation(null);
                        getSelectedStatistic().getStatistic().setObjectParameters(null);                        
                        getSelectedStatistic().getStatistic().setRunOperation(null);
                        getSelectedStatistic().getStatistic().setRunParameters(null);                        
                        jLabel3.setEnabled(false);
                        jLabel3.setText("Parameter");
                        jTextField1.setEnabled(false);
                        jTextField1.setText("");                        
                        jLabel5.setEnabled(false);
                        jLabel5.setText("Parameter");
                        jTextField2.setEnabled(false);
                        jTextField2.setText("");                        
                    }                    
                    fireStatisticEvent(new StatisticBaseValueChangedEvent(getSelectedStatistic()));
                }
            }
        });
        jComboBox2.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                Operation op = (Operation) jComboBox2.getModel().getSelectedItem();
                Operation oldOp = getSelectedStatistic().getStatistic().getObjectOperation();
                if (op != null && op.parameters().length == 1) {
                    jLabel3.setEnabled(true);
                    jLabel3.setText(op.parameters()[0].getName());
                    jTextField1.setEnabled(true);
                    jTextField1.setText("" + op.parameters()[0].getDef());
                    if (oldOp != op) {
                        getSelectedStatistic().getStatistic().setObjectOperation(op);
                        getSelectedStatistic().getStatistic().setObjectParameters(op.parameters()[0].getDef());
                        fireStatisticEvent(new StatisticObjectOperationChangedEvent(getSelectedStatistic()));
                    }
                } else {
                    jLabel3.setEnabled(false);
                    jLabel3.setText("Parameter");
                    jTextField1.setEnabled(false);
                    jTextField1.setText("");
                    if (oldOp != op) {
                        getSelectedStatistic().getStatistic().setObjectOperation(op);
                        fireStatisticEvent(new StatisticObjectOperationChangedEvent(getSelectedStatistic()));
                    }
                }
            }
        });
        jComboBox3.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                Operation op = (Operation) jComboBox3.getModel().getSelectedItem();
                Operation oldOp = getSelectedStatistic().getStatistic().getRunOperation();
                if (op != null && op.parameters().length == 1) {
                    jLabel5.setEnabled(true);
                    jLabel5.setText(op.parameters()[0].getName());
                    jTextField2.setEnabled(true);
                    jTextField2.setText("" + op.parameters()[0].getDef());
                    if (oldOp != op) {
                        getSelectedStatistic().getStatistic().setRunOperation(op);
                        getSelectedStatistic().getStatistic().setRunParameters(op.parameters()[0].getDef());
                        fireStatisticEvent(new StatisticRunOperationChangedEvent(getSelectedStatistic()));
                    }
                } else {
                    jLabel5.setEnabled(false);
                    jLabel5.setText("Parameter");
                    jTextField2.setEnabled(false);
                    jTextField2.setText("");
                    if (oldOp != op) {
                        getSelectedStatistic().getStatistic().setRunOperation(op);
                        fireStatisticEvent(new StatisticRunOperationChangedEvent(getSelectedStatistic()));
                    }
                }
            }
        });
        jComboBox4.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                DiagramData diagram = (DiagramData) jComboBox4.getSelectedItem();
                DiagramData oldDiagram = null;
                if (isStatisticSelected()) {
                    oldDiagram = getSelectedStatistic().getAttributes().getDiagram();
                }
                if (isStatisticSelected()) {
                    jLabel8.setEnabled(diagram != null && (diagram.getType() == DiagramType.AREA_CHART || diagram.getType() == DiagramType.LINE_CHART || diagram.getType() == DiagramType.STEP_CHART || diagram.getType() == DiagramType.STEP_AREA_CHART));
                }
                if (isStatisticSelected() && oldDiagram != diagram) {
                    getSelectedStatistic().getAttributes().setDiagram(diagram);
                    fireStatisticEvent(new StatisticDiagramChangedEvent(getSelectedStatistic(), oldDiagram, diagram));
                }
            }
        });
        jTextField1.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void changedUpdate(DocumentEvent e) {
                update();
            }

            public void update() {
                try {
                    double newValue = Double.parseDouble(jTextField1.getText());
                    Object[] values = getSelectedStatistic().getStatistic().getObjectParameters();
                    if (values == null || values.length != 1 || !values[0].equals(newValue)) {
                        getSelectedStatistic().getStatistic().setObjectParameters(newValue);
                        fireStatisticEvent(new StatisticObjectParameterChangedEvent(getSelectedStatistic()));
                    }
                } catch (NumberFormatException ex) {
                }
            }
        });
        jTextField2.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void changedUpdate(DocumentEvent e) {
                update();
            }

            public void update() {
                try {
                    double newValue = Double.parseDouble(jTextField2.getText());
                    Object[] values = getSelectedStatistic().getStatistic().getRunParameters();
                    if (values == null || values.length != 1 || !values[0].equals(newValue)) {
                        getSelectedStatistic().getStatistic().setRunParameters(newValue);
                        fireStatisticEvent(new StatisticRunParameterChangedEvent(getSelectedStatistic()));
                    }
                } catch (NumberFormatException ex) {
                }
            }
        });
        jTextField3.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void changedUpdate(DocumentEvent e) {
                update();
            }

            public void update() {
                String oldName = getSelectedStatistic().getAttributes().getName();
                if (!oldName.equals(jTextField3.getText())) {
                    getSelectedStatistic().getAttributes().setName(jTextField3.getText());
                    ((CustomListModel) jList1.getModel()).fireContentsChanged(jList1.getModel(), jList1.getSelectedIndex(), jList1.getSelectedIndex());
                    fireStatisticEvent(new StatisticDescriptionChangedEvent(getSelectedStatistic()));
                }
            }
        });
        jTextField4.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (isStatisticSelected()) {
                    DiagramData diagram = getSelectedStatistic().getAttributes().getDiagram();
                    if (diagram != null && (diagram.getType() == DiagramType.AREA_CHART || diagram.getType() == DiagramType.LINE_CHART || diagram.getType() == DiagramType.STEP_CHART || diagram.getType() == DiagramType.STEP_AREA_CHART))  {
                        Color color = JColorChooser.showDialog(StatisticPanel.this, "Farbe wählen", getSelectedStatistic().getAttributes().getColor());
                        if (getSelectedStatistic().getAttributes().getColor() == null && color != null || getSelectedStatistic().getAttributes().getColor() != null && !getSelectedStatistic().getAttributes().getColor().equals(color)) {
                            getSelectedStatistic().getAttributes().setColor(color);
                            jTextField4.setBackground(color);
                            fireStatisticEvent(new StatisticColorChangedEvent(getSelectedStatistic()));
                        }
                    }
                }
            }
        });
    }

    private void updateStatisticList() {
        DefaultListModel model = new CustomListModel();
        if (statistics != null) {
            for (DisplayableStatistic statistic : statistics) {
                model.addElement(statistic);
            }
        }
        jList1.setModel(model);
    }

    private void updateValueList() {
        jLabel1.setEnabled(jList1.getSelectedIndex() >= 0);
        jComboBox1.setEnabled(jList1.getSelectedIndex() >= 0);
        Vector items = new Vector();
        if (jList1.getSelectedIndex() >= 0) {
            for (Class<? extends Enum> statisticClass : getProfileType().getStatistics()) {
                Enum[] f = statisticClass.getEnumConstants();
                for (Enum e : f) {
                    items.add(e);
                }
            }
        }
        ComboBoxModel model = new DefaultComboBoxModel(items);
        jComboBox1.setModel(model);
        if (isStatisticSelected()) {
            jComboBox1.getModel().setSelectedItem(getSelectedStatistic().getStatistic().getStatistic());
        }
    }

    private void updateObjectOperationList() {
        jLabel2.setEnabled(isStatisticSelected() && getValueType() != null && getProfileType() != ProfileType.GLOBAL);
        jLabel3.setEnabled(isStatisticSelected() && getValueType() != null && getProfileType() != ProfileType.GLOBAL);
        jComboBox2.setEnabled(isStatisticSelected() && getValueType() != null && getProfileType() != ProfileType.GLOBAL);
        jTextField1.setEnabled(isStatisticSelected() && getValueType() != null && getProfileType() != ProfileType.GLOBAL);
        jLabel3.setText("Parameter");
        jTextField1.setText("");
        Vector items = new Vector();
        if (isStatisticSelected() && getValueType() != null && getProfileType() != ProfileType.GLOBAL) {
            if (getValueType().equals(Double.class)) {
                for (DoubleOperation operation : DoubleOperation.values()) {
                    items.add(operation);
                }
                Collections.sort(items, new ObjectToStringComparator());
            } else if (getValueType().equals(IntegerDoubleMapping.class)) {
                for (DynamicOperation operation : DynamicOperation.values()) {
                    items.add(operation);
                }
                Collections.sort(items, new ObjectToStringComparator());
            } else if (getValueType().equals(Node.class)) {
                items.add("Vergleich");
                Collections.sort(items);
            } else {
                throw new AssertionError("This should not happen.");
            }
        }
        jComboBox2.setModel(new DefaultComboBoxModel(items));
        if (isStatisticSelected()) {
            jComboBox2.setSelectedItem(getSelectedStatistic().getStatistic().getObjectOperation());
            Operation objectOperation = getSelectedStatistic().getStatistic().getObjectOperation();
            Object[] objectParameters = getSelectedStatistic().getStatistic().getObjectParameters();
            if (objectOperation != null && objectOperation.parameters().length == 1) {
                jLabel3.setEnabled(true);
                jLabel3.setText(objectOperation.parameters()[0].getName());
                jTextField1.setEnabled(true);
                jTextField1.setText((objectParameters[0] == null) ? Double.toString(objectOperation.parameters()[0].getDef()) : objectParameters[0].toString());
                if (objectParameters[0] == null) {
                    getSelectedStatistic().getStatistic().setObjectParameters(objectOperation.parameters()[0].getDef());
                }
            }
        }
    }

    private void updateRunOperationList() {
        jLabel4.setEnabled(isStatisticSelected() && getValueType() != null);
        jLabel5.setEnabled(isStatisticSelected() && getValueType() != null);
        jComboBox3.setEnabled(isStatisticSelected() && getValueType() != null);
        jTextField2.setEnabled(isStatisticSelected() && getValueType() != null);
        jLabel5.setText("Parameter");
        jTextField2.setText("");
        Vector items = new Vector();
        if (isStatisticSelected() && getValueType() != null) {
            if (getValueType().equals(Double.class)) {
                for (DoubleOperation operation : DoubleOperation.values()) {
                    items.add(operation);
                }
                Collections.sort(items, new ObjectToStringComparator());
            } else if (getValueType().equals(IntegerDoubleMapping.class)) {
                for (DynamicOperation operation : DynamicOperation.values()) {
                    items.add(operation);
                }
                Collections.sort(items, new ObjectToStringComparator());
            } else if (getValueType().equals(Node.class)) {
                items.add("Vergleich");
                Collections.sort(items);
            } else {
                throw new AssertionError("This should not happen.");
            }
        }
        jComboBox3.setModel(new DefaultComboBoxModel(items));
        if (isStatisticSelected()) {
            jComboBox3.setSelectedItem(getSelectedStatistic().getStatistic().getRunOperation());
            Operation runOperation = getSelectedStatistic().getStatistic().getRunOperation();
            Object[] runParameters = getSelectedStatistic().getStatistic().getRunParameters();
            if (runOperation != null && runOperation.parameters().length == 1) {
                jLabel5.setEnabled(true);
                jLabel5.setText(runOperation.parameters()[0].getName());
                jTextField2.setEnabled(true);
                jTextField2.setText((runParameters[0] == null) ? Double.toString(runOperation.parameters()[0].getDef()) : runParameters[0].toString());
                if (runParameters[0] == null) {
                    getSelectedStatistic().getStatistic().setRunParameters(runOperation.parameters()[0].getDef());
                }
            }
        }
    }

    private void updateAttributes() {
        jLabel6.setEnabled(isStatisticSelected());
        jComboBox4.setEnabled(isStatisticSelected());
        Vector items = new Vector();
        if (isStatisticSelected()) {
            for (DiagramData diagram : getProfile().getDiagrams()) {
                items.add(diagram);
            }
        }
        Collections.sort(items, new ObjectToStringComparator());
        ComboBoxModel model = new CustomComboBoxModel(items);
        jComboBox4.setModel(model);
        if (isStatisticSelected() && jComboBox4.getSelectedItem() != getSelectedStatistic().getAttributes().getDiagram()) {
            jComboBox4.setSelectedItem(getSelectedStatistic().getAttributes().getDiagram());
        }
        jLabel7.setEnabled(isStatisticSelected());
        jTextField3.setEnabled(isStatisticSelected());
        if (isStatisticSelected() && !jTextField3.getText().equals(getSelectedStatistic().getAttributes().getName())) {
            jTextField3.setText(getSelectedStatistic().getAttributes().getName());
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jTextField1 = new javax.swing.JTextField();
        jComboBox3 = new javax.swing.JComboBox();
        jTextField2 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Statistiken für Profil "));

        jScrollPane1.setViewportView(jList1);

        jButton1.setText("Neu");
        jButton1.setEnabled(false);

        jButton2.setText("Kopieren");
        jButton2.setEnabled(false);

        jButton3.setText("Löschen");
        jButton3.setEnabled(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Statistik"));

        jLabel1.setText("Betrachtete Modell-Größe:");
        jLabel1.setEnabled(false);

        jLabel2.setText("Operation für Objekt-Mengen:");
        jLabel2.setEnabled(false);

        jLabel3.setText("Parameter:");
        jLabel3.setEnabled(false);

        jLabel4.setText("Operation für multiple Durchläufe:");
        jLabel4.setEnabled(false);

        jLabel5.setText("Parameter:");
        jLabel5.setEnabled(false);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setEnabled(false);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.setEnabled(false);

        jTextField1.setEnabled(false);

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox3.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField2)
                    .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField1)
                    .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox1, 0, 191, Short.MAX_VALUE))
                .addContainerGap(2, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jButton4.setText("Hoch");
        jButton4.setEnabled(false);

        jButton5.setText("Runter");
        jButton5.setEnabled(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Darstellung"));

        jLabel7.setText("Darstellungsart:");
        jLabel7.setEnabled(false);

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox4.setEnabled(false);

        jLabel6.setText("Beschreibung:");
        jLabel6.setEnabled(false);

        jLabel8.setText("Farbe:");
        jLabel8.setEnabled(false);

        jTextField4.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8))
                .addGap(90, 90, 90)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                    .addComponent(jComboBox4, 0, 193, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, 0, 376, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
}
