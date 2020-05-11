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
package gui.editor.properties;

import de.tu_berlin.math.coga.zet.ZETLocalization2;
import ds.PropertyContainer;
import org.zetool.components.property.PropertyLoadException;
import gui.ZETProperties;
import gui.propertysheet.JOptionsDialog;
import gui.propertysheet.PropertyTreeModel;
import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.zetool.components.framework.Button;
import org.zetool.components.property.PropertyTreeModelLoader;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings("serial")
public class JPropertyDialog extends JOptionsDialog {
    private static final String PATH = "./icons/";
    private static final String NAME = "open.png";
    Icon icon = new ImageIcon(PATH + NAME);
    //ZETIconSet.Open.icon();
    
    public JPropertyDialog(PropertyTreeModel ptm) {
        super(ptm);
        
        int space = 10;
        JPanel buttonPanel = new JPanel();
        JButton btnOK = Button.newButton(ZETLocalization2.loc.getString("gui.OK"), getDefaultButtonsListener(), "ok");
        JButton btnCancel = Button.newButton(ZETLocalization2.loc.getString("gui.Cancel"), getDefaultButtonsListener(), "cancel");
        double size2[][] = {{TableLayout.FILL, TableLayout.PREFERRED, space, TableLayout.PREFERRED, space, TableLayout.PREFERRED, space}, {space, TableLayout.PREFERRED, space}};
        buttonPanel.setLayout(new TableLayout(size2));

        PropertyFilesSelectionModel pfsm = new PropertyFilesSelectionModel();
        pfsm.setSelectedItem(ZETProperties.getCurrentPropertyFile());
        final JPropertyComboBox jpc = new JPropertyComboBox(pfsm);
        jpc.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PropertyListEntry entry = (PropertyListEntry) jpc.getSelectedItem();
                try {
                    PropertyTreeModelLoader loader = new PropertyTreeModelLoader();
                    PropertyTreeModel ptm2 = loader.applyParameters(new FileReader(entry.getFile()), PropertyContainer.getGlobal());
                    loadProperties(ptm2);
                } catch (PropertyLoadException | FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
        btnOK.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ZETProperties.setCurrentProperty(jpc.getSelectedFile().toPath());
                } catch (PropertyLoadException | FileNotFoundException ex) {
                    System.out.println("Could not set the new Properties as current.");
                }
            }
        });

        buttonPanel.add(jpc, "1,1");
        buttonPanel.add(btnOK, "3,1");
        buttonPanel.add(btnCancel, "5,1");
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
