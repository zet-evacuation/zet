/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
package statistic.graph.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import statistic.graph.gui.event.ProfileSelectionChangedEvent;
import statistic.graph.gui.event.ProfileSelectionChangedEvent;
import statistic.graph.gui.event.ProfileListener;

/**
 *
 * @author Martin Groß
 */
public class ProfilePanel extends javax.swing.JPanel {

    private List<DisplayProfile> profiles;
    private List<ProfileListener> listeners;

    public ProfilePanel() {
        initComponents();
        initListeners();
        listeners = new LinkedList<ProfileListener>();
        profiles = new LinkedList<DisplayProfile>();
        DefaultComboBoxModel model = new CustomComboBoxModel(ProfileType.values());
        jComboBox1.setModel(model);
        updateProfileList();
    }
    
    public void addProfileListener(ProfileListener listener) {
        listeners.add(listener);
    }

    public List<DisplayProfile> getProfiles() {
        return profiles;
    }
    
    public void setProfiles(List<DisplayProfile> profiles) {
        this.profiles = profiles;
        updateProfileList();
    }
    
    public DisplayProfile getSelectedProfile() {
        return (isProfileSelected()) ? ((DisplayProfile) jList1.getSelectedValue()) : null;
    }

    public ProfileType getSelectedProfileType() {
        return (ProfileType) jComboBox1.getSelectedItem();
    }

    public boolean isProfileSelected() {
        return jList1.getSelectedIndex() >= 0;
    }

    public void setSelectedProfiles(List<DisplayProfile> profiles) {
        this.profiles = profiles;
        updateProfileList();
    }

    private void initListeners() {
        jComboBox1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                updateProfileList();
            }
        });
        jList1.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                jButton2.setEnabled(isProfileSelected());
                jButton3.setEnabled(isProfileSelected());
                jLabel2.setEnabled(isProfileSelected());
                jTextField1.setEnabled(isProfileSelected());
                if (isProfileSelected()) {
                    for (ProfileListener listener : listeners) {
                        listener.notify(new ProfileSelectionChangedEvent(getSelectedProfile()));
                    }
                    jTextField1.setText(getSelectedProfile().getName());
                } else {
                    for (ProfileListener listener : listeners) {
                        listener.notify(new ProfileSelectionChangedEvent(null));
                    }
                    jTextField1.setText("");
                }
            }
        });
        jTextField1.addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent e) {
                getSelectedProfile().setName(jTextField1.getText());
                ((CustomListModel) jList1.getModel()).fireContentsChanged(jList1.getModel(), jList1.getSelectedIndex(), jList1.getSelectedIndex());
            }

            public void keyPressed(KeyEvent e) {
                getSelectedProfile().setName(jTextField1.getText());
                ((CustomListModel) jList1.getModel()).fireContentsChanged(jList1.getModel(), jList1.getSelectedIndex(), jList1.getSelectedIndex());
            }

            public void keyReleased(KeyEvent e) {
                getSelectedProfile().setName(jTextField1.getText());
                ((CustomListModel) jList1.getModel()).fireContentsChanged(jList1.getModel(), jList1.getSelectedIndex(), jList1.getSelectedIndex());
            }
        });
        jButton1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DisplayProfile profile = new DisplayProfile();
                profile.setName("Neues Profil");
                profile.setType(getSelectedProfileType());
                profiles.add(profile);
                updateProfileList();
                jList1.getSelectionModel().setSelectionInterval(jList1.getModel().getSize() - 1, jList1.getModel().getSize() - 1);
            }
        });
        jButton2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DisplayProfile profile = getSelectedProfile().clone();
                profile.setName("Kopie von " + getSelectedProfile().getName());
                profiles.add(profile);
                updateProfileList();
                jList1.getSelectionModel().setSelectionInterval(jList1.getModel().getSize() - 1, jList1.getModel().getSize() - 1);
            }
        });
        jButton3.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int index = jList1.getSelectedIndex();
                profiles.remove(getSelectedProfile());
                updateProfileList();
                if (index < jList1.getModel().getSize()) {
                    jList1.getSelectionModel().setSelectionInterval(index, index);
                } else if (jList1.getModel().getSize() > 0) {
                    jList1.getSelectionModel().setSelectionInterval(jList1.getModel().getSize() - 1, jList1.getModel().getSize() - 1);
                }
            }
        });
    }

    private void updateProfileList() {
        CustomListModel model = new CustomListModel();
        for (DisplayProfile profile : profiles) {
            if (getSelectedProfileType() != profile.getType()) {
                continue;
            }
            model.addElement(profile);
        }
        jList1.setModel(model);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Statistik - Profile"));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jScrollPane1.setViewportView(jList1);

        jLabel2.setText("Beschreibung:");
        jLabel2.setEnabled(false);

        jTextField1.setEnabled(false);

        jButton1.setText("Neu");

        jButton2.setText("Kopieren");
        jButton2.setEnabled(false);

        jButton3.setText("Löschen");
        jButton3.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(378, Short.MAX_VALUE))
            .addComponent(jComboBox1, 0, 378, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3)))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
