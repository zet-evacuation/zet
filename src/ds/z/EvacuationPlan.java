/*
 * EvacuationPlan.java
 * Created on 26. November 2007, 21:32
 */

package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** A class for representing evacuation plans. This is only a dummy until now. */
@XStreamAlias("evacuationPlan")
public class EvacuationPlan {
	
	public EvacuationPlan () {
	}
	
	public void delete () {
	}
	
	public boolean equals (Object o) {
		if (o instanceof EvacuationPlan) {
			return true;
		} else {
			return false;
		}
	}
}
