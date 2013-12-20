package de.tu_berlin.math.coga.math.matrix;

import de.tu_berlin.math.coga.datastructure.Triple;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SparseMatrix {
	MatrixElement[] rows;
	MatrixElement[] columns;

	public SparseMatrix( int rows, int columns ) {
		this.rows = new MatrixElement[rows];
		this.columns = new MatrixElement[columns];

		for( int i = 0; i < rows; ++i )
			this.rows[i] = new MatrixElement( new Triple<>( i+1,0,0) );
		for( int j = 0; j < columns; ++j )
			this.columns[j] = new MatrixElement( new Triple<>( 0,j+1,0) );
	}

	@Override
	public String toString() {
		String s = "SparseMatrix{" + "rows=" + rows.length + ", columns=" + columns.length + "}\n";

		for( int i = 0; i < rows.length; ++i ) {
			MatrixElement e = rows[i].xNext();
			for( int j = 0; j < columns.length; ++j ) {
				if( e == null )
					s += "0";
				else if( e.col() == j+1 ) {
					s += e.val();
					e = e.xNext();
				} else
					s += "0";
			}
			s += "\n";
		}
		return s;
	}

	public String toString2() {
		String s = "Reversed SparseMatrix{" + "rows=" + rows.length + ", columns=" + columns.length + "}\n";

		for( int j = 0; j < columns.length; ++j ) {
			MatrixElement e = columns[j].yNext();
			for( int i = 0; i < rows.length; ++i ) {
				if( e == null )
					s += "0";
				else if( e.row() == i+1 ) {
					s += e.val();
					e = e.yNext();
				} else
					s += "0";
			}
			s += "\n";
		}
		return s;
	}

	public SparseMatrix add( SparseMatrix A, SparseMatrix B ) {
		SparseMatrix C = new SparseMatrix( A.rows.length, A.columns.length );

		for( int i = 0; i < C.rows.length; ++i ) {
			MatrixElement a = A.rows[i].xNext();
			MatrixElement b = B.rows[i].xNext();
			MatrixElement c = C.rows[i];
			while( !(a == null && b == null ) ) {
				MatrixElement newC;
				if( a == null ) {
					newC = new MatrixElement( new Triple<>( b.row(), b.col(), b.val() ) );
					b = b.xNext();
				} else if( b == null ) {
					newC = new MatrixElement( new Triple<>( a.row(), a.col(), a.val() ) );
					a = a.xNext();
				} else if( a.col() == b.col() ) {
					newC = new MatrixElement( new Triple<>( a.row(), a.col(), a.val() + b.val() ) );
					a = a.xNext();
					b = b.xNext();
				} else if( a.col() < b.col() ) {
					newC = new MatrixElement( new Triple<>( a.row(), a.col(), a.val() ) );
					a = a.xNext();
				} else {
					newC = new MatrixElement( new Triple<>( b.row(), b.col(), b.val() ) );
					b = b.xNext();
				}
				c.xAdd( newC );
				MatrixElement colLast = C.columns[newC.col()-1].yLast();
				colLast.yAdd( newC );
				c = newC;
			}
		}

		return C;
	}

	public int value( SparseMatrix A, SparseMatrix B, int row, int column ) {
		int sum = 0;

		MatrixElement a = A.rows[row].xNext();
		MatrixElement b = B.columns[column].yNext();

		while( a != null && b != null ) {
			if( a.col() == b.row() ) {
				sum += a.val()*b.val();
				a = a.xNext();
				b = b.yNext();
			} else if( a.col() < b.row() )
					a = a.xNext();
			else
				b = b.yNext();
		}
		return sum;
	}

	public SparseMatrix mult( SparseMatrix A, SparseMatrix B ) {
		SparseMatrix C = new SparseMatrix( A.rows.length, A.columns.length );

		for( int i = 0; i < A.rows.length; ++i ) {
			MatrixElement c = C.rows[i];

			for( int j = 0; j < B.columns.length; ++j ) {
				int val = value( A, B, i, j );
				if( val != 0 ) {
					MatrixElement newC = new MatrixElement( new Triple<>(i+1,j+1,val) );
					c.xAdd( newC );
					MatrixElement colLast = C.columns[newC.col()-1].yLast();
					colLast.yAdd( newC );
					c = newC;
				}
			}
		}
		return C;
	}


	public static void main( String args[] ) {
		SparseMatrix A = new SparseMatrix( 3,3 );

		MatrixElement a11 = new MatrixElement( new Triple<>(1,1,1) );
		MatrixElement a21 = new MatrixElement( new Triple<>(2,1,1) );
		MatrixElement a32 = new MatrixElement( new Triple<>(3,2,1) );
		MatrixElement a33 = new MatrixElement( new Triple<>(3,3,2) );
		A.rows[0].xAdd( a11 );
		A.rows[1].xAdd( a21 );
		A.rows[2].xAdd( a32 );
		a32.xAdd( a33 );

		A.columns[0].yAdd( a11 );
		a11.yAdd( a21 );
		A.columns[1].yAdd( a32 );
		A.columns[2].yAdd( a33 );

		System.out.println( A.toString() );
		System.out.println( A.toString2() );

		SparseMatrix B = new SparseMatrix( 3,3 );

		MatrixElement b11 = new MatrixElement( new Triple<>(1,1,1) );
		MatrixElement b21 = new MatrixElement( new Triple<>(2,1,1) );
		MatrixElement b32 = new MatrixElement( new Triple<>(3,2,1) );
		MatrixElement b33 = new MatrixElement( new Triple<>(3,3,2) );
		B.rows[0].xAdd( b11 );
		B.rows[1].xAdd( b21 );
		B.rows[2].xAdd( b32 );
		b32.xAdd( b33 );

		B.columns[0].yAdd( b11 );
		b11.yAdd( b21 );
		B.columns[1].yAdd( b32 );
		B.columns[2].yAdd( b33 );

		System.out.println( B.toString() );

		System.out.println( "Addition" );
		SparseMatrix C = A.add( A, B);
		System.out.println( C.toString() );
		System.out.println( C.toString2() );

		System.out.println( "Multiplication" );
		System.out.println( A.value( A, B, 0, 0 ) );

		SparseMatrix C2 = A.mult( A, B );
		System.out.println( C2.toString());
		System.out.println( C2.toString2());
	}
}
