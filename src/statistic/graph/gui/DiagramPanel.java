/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * DiagramPanel.java
 *
 */
package statistic.graph.gui;

import statistic.graph.gui.event.DiagramListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import statistic.graph.gui.event.DiagramAddedEvent;
import statistic.graph.gui.event.DiagramEvent;
import statistic.graph.gui.event.DiagramRemovedEvent;
import statistic.graph.gui.event.DiagramSelectionChangedEvent;
import statistic.graph.gui.event.DiagramSequenceChangedEvent;
import statistic.graph.gui.event.DiagramTitleChangedEvent;
import statistic.graph.gui.event.DiagramTypeChangedEvent;
import statistic.graph.gui.event.DiagramXAxisLabelChangedEvent;
import statistic.graph.gui.event.DiagramYAxisLabelChangedEvent;
import statistic.graph.gui.event.ProfileEvent;
import statistic.graph.gui.event.ProfileListener;

/**
 *
 * @author Martin Groß
 */
public class DiagramPanel extends JPanel implements ProfileListener {

    private boolean active;
    private List<DiagramData> diagrams;
    private List<DiagramListener> listeners;

    public DiagramPanel() {
        diagrams = new LinkedList<DiagramData>();
        listeners = new LinkedList<DiagramListener>();
        initComponents();
        initListeners();
        jComboBox1.setModel(new DefaultComboBoxModel(DiagramType.values()));
    }

    public void addDiagramListener(DiagramListener listener) {
        listeners.add(listener);
    }

    public void removeDiagramListener(DiagramListener listener) {
        listeners.remove(listener);
    }
    
    private void fireDiagramEvent(DiagramEvent event) {
        for (DiagramListener listener : listeners) {
            listener.notify(event);
        }
    }    

    public void notify(ProfileEvent event) {
        DisplayProfile profile = event.getProfile();
        setActive(profile != null);
        setDiagrams((profile == null) ? new LinkedList<DiagramData>() : profile.getDiagrams());
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        jButton1.setEnabled(active);
    }

    private DiagramData getSelectedDiagram() {
        return (DiagramData) jList1.getSelectedValue();
    }

    public List<DiagramData> getDiagrams() {
        return diagrams;
    }

    public void setDiagrams(List<DiagramData> diagrams) {
        this.diagrams = diagrams;
        DefaultListModel model = new CustomListModel();
        for (DiagramData data : diagrams) {
            model.addElement(data);
        }
        jList1.setModel(model);
    }

    private void selectDiagram(int index) {
        jList1.getSelectionModel().setSelectionInterval(index, index);
        jList1.ensureIndexIsVisible(index);
    }

    private int getFirstFreeNumber(DiagramType type) {
        boolean[] occupied = new boolean[diagrams.size() + 2];
        for (DiagramData diagram : diagrams) {
            if (diagram.getType() == type && diagram.getTitle().matches(type.toString() + " [0-9]+:.*")) {
                String number = diagram.getTitle().substring(type.toString().length() + 1, diagram.getTitle().indexOf(":"));
                try {
                    int n = Integer.parseInt(number);
                    if (n < occupied.length) {
                        occupied[n] = true;
                    }
                } catch (NumberFormatException ex) {
                    continue;
                }
            }
        }
        for (int i = 1; i < occupied.length; i++) {
            if (!occupied[i]) {
                return i;
            }
        }
        throw new AssertionError("This should not happen.");
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
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Diagramme und Tabellen"));

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jList1);

        jButton1.setText("Neu");
        jButton1.setEnabled(false);

        jButton2.setText("Kopieren");
        jButton2.setEnabled(false);

        jButton3.setText("Löschen");
        jButton3.setEnabled(false);

        jButton4.setText("Hoch");
        jButton4.setEnabled(false);

        jButton5.setText("Runter");
        jButton5.setEnabled(false);

        jLabel1.setText("Titel:");
        jLabel1.setEnabled(false);

        jLabel2.setText("Typ:");
        jLabel2.setEnabled(false);

        jComboBox1.setEnabled(false);

        jTextField1.setEnabled(false);

        jLabel3.setText("X-Achse:");
        jLabel3.setEnabled(false);

        jTextField2.setEnabled(false);

        jLabel4.setText("Y-Achse:");
        jLabel4.setEnabled(false);

        jTextField3.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, 0, 329, Short.MAX_VALUE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents
    private void initListeners() {
        jButton1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DiagramData diagram = new DiagramData();
                diagram.setTitle("Tabelle " + getFirstFreeNumber(DiagramType.TABLE) + ": Titel");
                diagram.setType(DiagramType.TABLE);
                diagrams.add(diagram);
                ((DefaultListModel) jList1.getModel()).addElement(diagram);
                fireDiagramEvent(new DiagramAddedEvent(diagram));
                selectDiagram(jList1.getModel().getSize() - 1);
            }
        });
        jButton2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DiagramData original = (DiagramData) jList1.getSelectedValue();
                DiagramData clone = original.clone();
                clone.setTitle(clone.getType().toString() + " " + getFirstFreeNumber(clone.getType()) + ": Titel");
                diagrams.add(clone);
                ((DefaultListModel) jList1.getModel()).addElement(clone);
                fireDiagramEvent(new DiagramAddedEvent(clone));
                selectDiagram(jList1.getModel().getSize() - 1);
            }
        });
        jButton3.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int index = jList1.getSelectedIndex();
                DiagramData diagram = (DiagramData) jList1.getSelectedValue();
                ((DefaultListModel) jList1.getModel()).removeElement(diagram);
                diagrams.remove(diagram);
                fireDiagramEvent(new DiagramRemovedEvent(diagram));
                if (index < jList1.getModel().getSize()) {
                    selectDiagram(index);
                } else if (jList1.getModel().getSize() > 0) {
                    selectDiagram(jList1.getModel().getSize() - 1);
                }
            }
        });
        jButton4.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int index = jList1.getSelectedIndex();
                DiagramData previous = (DiagramData) jList1.getModel().getElementAt(index - 1);
                DiagramData selected = (DiagramData) jList1.getSelectedValue();
                ((DefaultListModel) jList1.getModel()).removeElement(selected);
                ((DefaultListModel) jList1.getModel()).add(index - 1, selected);
                diagrams.remove(selected);
                diagrams.add(index - 1, selected);
                fireDiagramEvent(new DiagramSequenceChangedEvent(previous, selected));
                selectDiagram(index - 1);
            }
        });
        jButton5.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int index = jList1.getSelectedIndex();
                DiagramData next = (DiagramData) jList1.getModel().getElementAt(index + 1);
                DiagramData selected = (DiagramData) jList1.getSelectedValue();
                ((DefaultListModel) jList1.getModel()).removeElement(selected);
                ((DefaultListModel) jList1.getModel()).add(index + 1, selected);
                diagrams.remove(selected);
                diagrams.add(index + 1, selected);
                fireDiagramEvent(new DiagramSequenceChangedEvent(selected, next));
                selectDiagram(index + 1);
            }
        });
        jList1.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                DiagramData data = (DiagramData) jList1.getSelectedValue();
                jButton1.setEnabled(active);
                jButton2.setEnabled(jList1.getSelectedIndex() >= 0);
                jButton3.setEnabled(jList1.getSelectedIndex() >= 0);
                jButton4.setEnabled(jList1.getSelectedIndex() > 0);
                jButton5.setEnabled(jList1.getSelectedIndex() >= 0 && jList1.getSelectedIndex() < jList1.getModel().getSize() - 1);
                jLabel1.setEnabled(jList1.getSelectedIndex() >= 0);
                jLabel2.setEnabled(jList1.getSelectedIndex() >= 0);
                jLabel3.setEnabled(jList1.getSelectedIndex() >= 0 && data.getType().hasXAxis());
                jLabel4.setEnabled(jList1.getSelectedIndex() >= 0 && data.getType().hasYAxis());
                jComboBox1.setEnabled(jList1.getSelectedIndex() >= 0);
                jTextField1.setEnabled(jList1.getSelectedIndex() >= 0);
                jTextField2.setEnabled(jList1.getSelectedIndex() >= 0 && data.getType().hasXAxis());
                jTextField3.setEnabled(jList1.getSelectedIndex() >= 0 && data.getType().hasYAxis());
                if (data != null) {
                    jTextField1.setText(data.getTitle());
                    jComboBox1.getModel().setSelectedItem(data.getType());
                    jTextField2.setText(data.getXAxisLabel());
                    jTextField3.setText(data.getYAxisLabel());
                } else {
                    jTextField1.setText("");
                    jComboBox1.getModel().setSelectedItem(null);
                    jTextField2.setText("");
                    jTextField3.setText("");
                }
                DiagramData diagram = (DiagramData) jList1.getSelectedValue();
                fireDiagramEvent(new DiagramSelectionChangedEvent(diagram));
            }
        });
        jComboBox1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (getSelectedDiagram() == null) {
                    jLabel3.setEnabled(false);
                    jLabel4.setEnabled(false);
                    jTextField2.setEnabled(false);
                    jTextField3.setEnabled(false);
                } else {
                    if (!getSelectedDiagram().getType().equals((DiagramType) jComboBox1.getSelectedItem())) {
                        getSelectedDiagram().setType((DiagramType) jComboBox1.getSelectedItem());
                        fireDiagramEvent(new DiagramTypeChangedEvent(getSelectedDiagram(), getSelectedDiagram().getType()));
                        for (DiagramType type : DiagramType.values()) {
                            if (type == (DiagramType) jComboBox1.getSelectedItem()) {
                                continue;
                            }
                            if (jTextField1.getText().matches(type.toString() + " [0-9]+:.*")) {
                                String text = jTextField1.getText();
                                text = getSelectedDiagram().getType().toString() + " " + getFirstFreeNumber(getSelectedDiagram().getType()) + text.substring(text.indexOf(":"));
                                jTextField1.setText(text);
                                getSelectedDiagram().setTitle(text);
                                break;
                            }
                        }
                    }
                    jLabel3.setEnabled(jList1.getSelectedIndex() >= 0 && getSelectedDiagram().getType().hasXAxis());
                    jLabel4.setEnabled(jList1.getSelectedIndex() >= 0 && getSelectedDiagram().getType().hasYAxis());
                    jTextField2.setEnabled(jList1.getSelectedIndex() >= 0 && getSelectedDiagram().getType().hasXAxis());
                    jTextField3.setEnabled(jList1.getSelectedIndex() >= 0 && getSelectedDiagram().getType().hasYAxis());
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
                if (getSelectedDiagram() != null && !getSelectedDiagram().getTitle().equals(jTextField1.getText())) {
                    String text = jTextField1.getText().trim();
                    if (text.equals("")) {
                        text = " ";
                    }
                    getSelectedDiagram().setTitle(text);
                    ((CustomListModel) jList1.getModel()).fireContentsChanged(jList1.getModel(), jList1.getSelectedIndex(), jList1.getSelectedIndex());
                    fireDiagramEvent(new DiagramTitleChangedEvent(getSelectedDiagram(), text));
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
                if (getSelectedDiagram() != null && getSelectedDiagram().getXAxisLabel() != null && !getSelectedDiagram().getXAxisLabel().equals(jTextField2.getText())) {
                    getSelectedDiagram().setXAxisLabel(jTextField2.getText());
                    fireDiagramEvent(new DiagramXAxisLabelChangedEvent(getSelectedDiagram(), jTextField2.getText()));
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
                if (getSelectedDiagram() != null && getSelectedDiagram().getYAxisLabel() != null && !getSelectedDiagram().getYAxisLabel().equals(jTextField3.getText())) {
                    getSelectedDiagram().setYAxisLabel(jTextField3.getText());
                    fireDiagramEvent(new DiagramYAxisLabelChangedEvent(getSelectedDiagram(), jTextField3.getText()));
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame test = new JFrame("Test");
            test.getContentPane().add(new DiagramPanel());
            test.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            test.pack();
            test.setLocationRelativeTo(null);
            test.setVisible(true);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StatisticPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(StatisticPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(StatisticPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(StatisticPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
