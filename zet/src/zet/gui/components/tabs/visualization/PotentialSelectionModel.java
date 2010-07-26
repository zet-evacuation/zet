/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
/**
 * Class PotentialSelectionModel
 * Erstellt 05.07.2008, 01:42:38
 */

package zet.gui.components.tabs.visualization;
import ds.ca.PotentialManager;
import ds.ca.StaticPotential;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PotentialSelectionModel extends DefaultComboBoxModel {
	public class PotentialEntry {
		public String name;
		public StaticPotential potential;
		PotentialEntry ( String name, StaticPotential potential ) {
			this.name = name;
			this.potential = potential;
		}
		
		@Override
		public String toString() {
			return name;
		}
	
		public StaticPotential getPotential() {
			return potential;
		}
	}
	
	public PotentialSelectionModel( PotentialManager pm ) {
		super();
		if( pm == null )
			return;
		
		Integer a = 0;
		for( StaticPotential potential : pm.getStaticPotentials() ) {
			a++;
			addElement( new PotentialEntry( "Potenzial " + a.toString(), potential ) );
		}
	}
}
