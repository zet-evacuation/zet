/**
 * TimeReapeatedFlow.java
 * Created: 12.12.2011, 14:03:07
 */
package ds.graph.flow;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TimeReapeatedFlow extends PathBasedFlowOverTime {
	private int timeHorizon;
	private long value;

	public TimeReapeatedFlow( int timeHorizon ) {
		this.timeHorizon = timeHorizon;
	}

	@Override
	public void addPathFlow( FlowOverTimePath pathFlow ) {
		if( pathFlow.getFirst().getDelay() != 0 )
			throw new IllegalArgumentException( "Time Repeated Flow starts at time 0.");
		for( FlowOverTimeEdge e : pathFlow )
			if( e.getDelay() != 0 )
				throw new IllegalArgumentException( "No waiting allowed in Time Repeated Flow" );
		super.addPathFlow( pathFlow );
		value += pathFlow.getAmount();
	}

	@Override
	public boolean remove( FlowOverTimePath pathFlow ) {
		if( super.remove( pathFlow ) ) {
			value -= pathFlow.getAmount();
			return true;
		}
		return false;
	}

	public long getValue() {
		return value;
	}
}
