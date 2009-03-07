/*
 * Created on 23.01.2008
 *
 */
package algo.ca.rule;

/**
 * @author Daniel Pluempe
 *
 */
public abstract class AbstractReactionRule extends AbstractRule {
	@Override
	public boolean executableOn( ds.ca.Cell cell ) {
		return cell.getIndividual() == null ? false : !cell.getIndividual().isAlarmed();
	}
}
