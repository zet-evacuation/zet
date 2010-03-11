/**
 * VisualizationEvent.java
 * Created: 10.03.2010, 12:39:48
 */
package event;

import batch.tasks.ProcessUpdateMessage;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class VisualizationEvent<S> extends ProgressEvent<S> {

	public VisualizationEvent( S source ) {
		super( source, new ProcessUpdateMessage( 0, "visualization", "playing", "" ) );
	}

}
