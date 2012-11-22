/**
 * StairAreaBoundaryException.java
 * Created: 22.11.2012, 12:46:24
 */
package ds.z.exception;

import ds.z.Edge;
import ds.z.StairArea;
import java.io.IOException;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
@SuppressWarnings( "serial" )
public class StairAreaBoundaryException extends ZModelErrorException {
	private StairArea sa;
	private Edge causing;
	public StairAreaBoundaryException( StairArea sa, Edge causing ) {
		super( "Edge " + causing.toString() + " could not be set as upper or lower edge, because it overlaps with another lower/upper edge in stair " + sa  );
		this.sa = sa;
	}

	public StairAreaBoundaryException( String message, StairArea sa, Edge causing ) {
		super( message );
		this.sa = sa;
	}

	public StairArea getStairArea() {
		return sa;
	}

	public Edge getCausing() {
		return causing;
	}
	
	/** Prohibits serialization. */
	private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException {
		throw new UnsupportedOperationException( "Serialization not supported" );
	}
}
