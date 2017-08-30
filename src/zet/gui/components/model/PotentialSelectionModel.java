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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package zet.gui.components.model;

import javax.swing.DefaultComboBoxModel;
import org.zet.cellularautomaton.potential.Potential;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PotentialSelectionModel extends DefaultComboBoxModel {

    public class PotentialEntry {

        public String name;
        public Potential potential;

        PotentialEntry(String name, Potential potential) {
            this.name = name;
            this.potential = potential;
        }

        @Override
        public String toString() {
            return name;
        }

        public Potential getPotential() {
            return potential;
        }
    }

    public PotentialSelectionModel(Iterable<Potential> ca) {
        super();
        if (ca == null) {
            return;
        }

        Integer a = 0;
        for (Potential potential : ca) {
            a++;
            addElement(new PotentialEntry("Potenzial " + a.toString(), potential));
        }
    }
}
