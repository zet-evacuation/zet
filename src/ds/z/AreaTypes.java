/**
 * AreaTypes.java
 * Created: 21.01.2011, 14:26:48
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ds.z;

/**
 * An enumeration of all implemented areas in the z model.
 * @author Jan-Philipp Kappmeier
 */
public enum AreaTypes {
	Assignment("ds.z.AreaType.AssignmentArea"),
	Barrier("ds.z.AreaType.Barrier"),
	Delay("ds.z.AreaType.DelayArea"),
	Inaccessible("ds.z.AreaType.InaccessibleArea"),
	Evacuation("ds.z.AreaType.EvacuationArea"),
	Save("ds.z.AreaType.SaveArea"),
	Stair("ds.z.AreaType.StairArea"),
	Teleport("ds.z.AreaType.TeleportArea");
	
	/** The key needed for the localization class to get the area type name. */
	private String key;

	private AreaTypes( String key ) {
		this.key = key;
	}

	/**
	 * Returns a (localized) name for the area type.
	 * @return a name for the area type
	 */
	public String getTypeString() {
		return ZLocalization.getSingleton().getString( key );
	}


}
