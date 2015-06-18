package zz_old_de.tu_berlin.math.coga.math.matrix;

import org.zetool.common.datastructure.Triple;


/**
 *
 * @author Jan-Philipp
 */
public class MatrixElement {
	private MatrixElement xNext;
	private MatrixElement yNext;
	private Triple<Integer,Integer,Integer> data;

	public MatrixElement( Triple<Integer, Integer, Integer> data ) {
		this.data = data;
	}

	public void xAdd( MatrixElement xNext ) {
		this.xNext = xNext;
	}

	public void yAdd( MatrixElement yNext ) {
		this.yNext = yNext;
	}

	public MatrixElement xNext() {
		return xNext;
	}

	public MatrixElement yNext() {
		return yNext;
	}

	public int row() {
		return data.u();
	}

	public int col() {
		return data.v();
	}

	public int val() {
		return data.w();
	}

	public String toString() {
		return "(" + data.u() + "," + data.v() + "," + data.w() + ")";
	}

	public MatrixElement xLast() {
		MatrixElement e = this;
		while( e.xNext != null )
			e = e.xNext;
		return e;
	}

	public MatrixElement yLast() {
		MatrixElement e = this;
		while( e.yNext != null )
			e = e.yNext;
		return e;
	}

}
