/**
 * Class PotentialSelectionModel
 * Erstellt 05.07.2008, 01:42:38
 */

package gui.components;
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
