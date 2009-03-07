/**
 * Class EvacuationAreaCreatedEvent
 * Erstellt 22.11.2008, 01:01:57
 */

package ds.z.event;

/**
 *
 * @author Jan-Philipp Kapmeier
 */
public class EvacuationAreaCreatedEvent extends ChangeEvent {
	public static final int CREATED = 1;
	public static final int DELETED = 2;

	public EvacuationAreaCreatedEvent( Object source, int status ) {
		super( source );
		switch( status ) {
			case CREATED:
				message = "created";
				break;
			case DELETED:
				message = "deleted";
				break;
			default:
				throw new IllegalArgumentException( "Status has wrong type." );
		}
	}
}
