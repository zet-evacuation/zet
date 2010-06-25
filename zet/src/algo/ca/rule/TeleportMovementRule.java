/**
 * TeleportMovementRule.java
 * Created: Jun 17, 2010,4:36:48 PM
 */
package algo.ca.rule;

import ds.ca.Cell;
import ds.ca.TeleportCell;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TeleportMovementRule extends WaitingMovementRule {

	@Override
	public boolean executableOn( Cell cell ) {
		if( cell instanceof TeleportCell ) {
			return super.executableOn( cell ) && !((TeleportCell)cell).isTeleportFailed();
		} else
			return super.executableOn( cell );
	}

}
