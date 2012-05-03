/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewComputationDialog.java
 *
 * Created on Dec 15, 2011, 12:23:23 PM
 */
package de.tu_berlin.math.coga.batch.gui.dialog;

import de.tu_berlin.math.coga.batch.input.ProblemType;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author gross
 */
public class AddAlgorithmWizard extends javax.swing.JFrame {

    private boolean accepted;
    private boolean closed;

    /** Creates new form NewComputationDialog */
    public AddAlgorithmWizard(Component parent) {
        super("Preparing a new computation... ");
        initComponents();
        final File iconFile = new File("./icon.gif");
        try {
            setIconImage(ImageIO.read(iconFile));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        DefaultListModel model = new DefaultListModel();
        Map<Object, Icon> icons = new HashMap<>();
        for (ProblemType type : ProblemType.values()) {
            model.addElement(type);
            icons.put(type, new ImageIcon("./icons/max_flow.png"));
        }
        setSize(600, 500);
        setLocationRelativeTo(parent);
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isClosed() {
        return closed;
    }

    public ProblemType getProblemType() {
        return (ProblemType) jList1.getSelectedValue();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Choose the type of computation...");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Choose the type of computation you want to perform:"));

        jScrollPane1.setViewportView(jTree1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
        );

        jButton1.setText("Add algorithm");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Close");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        accepted = false;
        closed = true;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        accepted = true;
        closed = true;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                AddAlgorithmWizard frame = new AddAlgorithmWizard(null);
                frame.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables

    public class IconListRenderer extends DefaultListCellRenderer {

        private Map<Object, Icon> icons = null;

        public IconListRenderer() {
            this(new HashMap<Object, Icon>());
        }

        public IconListRenderer(Map<Object, Icon> icons) {
            this.icons = icons;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Icon icon = icons.get(value);
            label.setFont(label.getFont().deriveFont(16f));
            label.setIcon(icon);
            return label;
        }
    }
}
