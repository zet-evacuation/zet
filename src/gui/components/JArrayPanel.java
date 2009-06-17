/*
 * JArrayPanel.java
 * Created 16.06.2009, 22:04:07
 */

package gui.components;

import info.clearthought.layout.TableLayout;
import java.lang.reflect.Array;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * The class <code>JArrayPanel</code> represents an <code>Jpanel</code> that
 * contains <code>JComponents</code> arranged on a table. It bases on the
 * {@link TableLayout} layout manager but every array position has to be
 * filled with at most one component.
 * @author Jan-Philipp Kappmeier
 */
public class JArrayPanel extends JPanel {
	/** The array containing the components displayed in the panel. */
	private JComponent[][] components;
	/** The array describing the width of the columns. */
	private double sizeC[];
	/** The array describing the height of the rows. */
	private double sizeR[];
	/** The number of columns. */
	private int columns;
	/** The number of rows. */
	private int rows;
	
	/**
	 * Creates a new instance of <code>JArrayPanel</code>. Initializes it with
	 * the {@link TableLayout} with the specified number of columns and rows. The
	 * columns are initialized with the fill constant and the rows are initialized
	 * with the preferred constant.
	 * @param rows the number of component rows
	 * @param columns the number of component columns
	 * @see #setColumnWidth( int, double )
	 * @see #setRowHeight( int, double )
	 */
	public JArrayPanel( int columns, int rows ) {
		if( rows <= 0 || columns <= 0 )
			throw new IllegalArgumentException( "Rows and columns must be at least 1." );
		this.columns = columns;
		this.rows = rows;
		components = (JComponent[][]) Array.newInstance( JComponent.class, columns, rows );
		sizeC = (double[])Array.newInstance( double.class, columns ); // Columns
		sizeR = (double[])Array.newInstance( double.class, rows ); // Columns
		for( int i = 0; i < columns; i++ )
			sizeC[i] = TableLayout.FILL;
		for( int i = 0; i < rows; i++ )
			sizeR[i] = TableLayout.PREFERRED;
		setLayout( new TableLayout(sizeC,sizeR) );
	}

	/**
	 * Assigns an component to the specified position
	 * @param component the component
	 * @param x the column of the component
	 * @param y the row of the component
	 */
	public void set( JComponent component, int x, int y ) {
		this.add( component, x + ", " + y );
		components[x][y] = component;
	}

	/**
	 * Sets the height for the specified row. It is possible to use the
	 * constants defined by {@link TableLayout}. All rows are initialized
	 * with the preferred constant.
	 * @param index the index of the row
	 * @param width the width of the row
	 */
	public void setRowHeight( int index, double height ) {
		sizeR[index] = height;
	}

	/**
	 * Sets the width for the specified column. It is possible to use the
	 * constants defined by {@link TableLayout}. All columns are initialized
	 * with the filling constant.
	 * @param index the index of the column
	 * @param width the width of the column
	 */
	public void setColumnWidth( int index, double width ) {
		sizeC[index] = width;
	}

	/**
	 * Rebuilds the {@link TableLayout} for the panel with the
	 * components already added and the current settings for the width and height.
	 */
	public void rebuild() {
		this.setLayout( new TableLayout( sizeC, sizeR ) );
		for( int i = 0; i < columns; i++ )
			for( int j = 0; j < rows; j++ )
				if( components[i][j] != null )
					this.add( components[i][j], i + ", " + j );
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "JArrayPanel";
	}
}
