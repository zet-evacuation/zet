package gui.components;

/** A string that is connected to an integer.
 *
 * @author Timon
 */
public class NamedIndex {
	private String name;
	private int index;
	
	public NamedIndex (String name, int index) {
		this.name = name;
		this.index = index;
	}

	public String getName () {
		return name;
	}

	public int getIndex () {
		return index;
	}
	
	@Override
	public String toString () {
		return name;
	}
}