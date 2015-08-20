/**
 * SquareCell.java
 * Created: 25.10.2012, 14:25:49
 */
package de.tu_berlin.math.coga.datastructure.simulation.cellularautomaton;

import org.zetool.common.util.Direction8;
import java.util.Objects;


/**
 * @param <E>
 * @param <St> the status
 * @author Jan-Philipp Kappmeier
 */
public abstract class SquareCell<E extends SquareCell<E,St>,St> implements Cell<E,St> {
	/** x-coordinate of the cell in the room. */
	protected int x;
	/** y-coordinate of the cell in the room. */
	protected int y;
	/** The square matrix to which this cell belongs. */
	CellMatrix<E> matrix;
	/** The Status object for the cell. */
	private St status;

	public SquareCell( St status, int x, int y, CellMatrix<E> matrix ) {
		this.x = x;
		this.y = y;
		this.matrix = matrix;
		this.status = Objects.requireNonNull( status, "Cell status must not be null!" );
	}

	public St getStatus() {
		return status;
	}

	@Override
	public int getSides() {
		return 4;
	}

}
