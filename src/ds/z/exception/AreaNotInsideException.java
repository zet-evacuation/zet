
package ds.z.exception;

import ds.z.Area;

/** Is thrown ehen an are ais not located insid the room which is is associated to.
 * @author Joscha Kulbatzki
 */
public class AreaNotInsideException extends ValidationException {
	
	public AreaNotInsideException ( Area area ) {
		super (area);
	}
	
	public AreaNotInsideException ( Area area, String s ) {
		super (area, s );
	}
	
	public Area getArea () {
		return (Area)getSource ();
	}
}