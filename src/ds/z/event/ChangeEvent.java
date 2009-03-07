package ds.z.event;

/** This event is thrown whenever a change to the plan objects that are organised
 * under the {@link ds.Project} class / {@link ds.z.BuildingPlan} class / {@link ds.z.Assignment}
 * class occurs. It is used to notify the converter objects of the fact that they will
 * have to regenerate the graph / cellular automaton.
 *
 * @author Timon Kelter
 */
public class ChangeEvent {
	/** The object at which the event was originally created. 
	 * Usually an object of type ds.z.XXX (f.e. ds.z.Room) */
	protected Object source;
	/** Optional: The concerned field in the modified source object. */
	private String field;
	/** Optional: A message that may be added to describe the event. */
	protected String message;
	
	public ChangeEvent (Object source) {
		this (source, null, null);
	}

	public ChangeEvent (Object source, String message) {
		this (source, message, null);
	}
	
	public ChangeEvent (Object source, String message, String field) {
		this.source = source;
		this.message = message;
		this.field = field;
	}
	
	/** The object at which the event was originally created. 
	 * Usually an object of type ds.z.XXX (f.e. ds.z.Room) */
	public Object getSource () {
		return source;
	}

	/** Optional: A message that may be added to describe the event. */
	public String getMessage () {
		return message;
	}

	/** Optional: The concerned field in the modified source object. */
	public String getField () {
		return field;
	}
}