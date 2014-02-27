/**
 * EarliestArrivalFlowPatternBuilder.java
 * Created: 27.01.2014, 11:58:17
 */
package de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.EATAPPROX;

import java.util.ArrayList;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EarliestArrivalFlowPatternBuilder {
	ArrayList<Integer> flowPattern;

	public EarliestArrivalFlowPatternBuilder() {
		flowPattern = new ArrayList<>();
	}

	public EarliestArrivalFlowPatternBuilder( int timeHorizon) {
		flowPattern = new ArrayList<>( timeHorizon );
	}

	public void addFlowValue( int value ) {
		flowPattern.add( value );
	}

	public EarliestArrivalFlowPattern build() {
		return new EarliestArrivalFlowPattern( flowPattern );
	}

	public void addFlowValue( int arrival, int amount ) {
		if( arrival >= flowPattern.size() ) {
			if( flowPattern.size() != 0 )
				for( int i = flowPattern.size(); i <= arrival; ++i ) {
					flowPattern.add( flowPattern.get( i - 1 ) );
			} else
				flowPattern.add( 0 );
		}

		for( int i = arrival; i < flowPattern.size(); ++i )
			flowPattern.set( i, flowPattern.get( i ) + amount );
	}
}
