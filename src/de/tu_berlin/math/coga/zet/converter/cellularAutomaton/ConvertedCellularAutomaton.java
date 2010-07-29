/**
 * ConvertedCellularAutomaton.java
 * Created: Jul 28, 2010,5:10:10 PM
 */
package de.tu_berlin.math.coga.zet.converter.cellularAutomaton;

import ds.ca.CellularAutomaton;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ConvertedCellularAutomaton {
	private CellularAutomaton cellularAutomaton;
	private ZToCAMapping mapping;
	private ZToCARasterContainer container;

	public ConvertedCellularAutomaton( CellularAutomaton cellularAutomaton, ZToCAMapping mapping, ZToCARasterContainer container ) {
		this.cellularAutomaton = cellularAutomaton;
		this.mapping = mapping;
		this.container = container;
	}

	public CellularAutomaton getCellularAutomaton() {
		return cellularAutomaton;
	}

	public ZToCARasterContainer getContainer() {
		return container;
	}

	public ZToCAMapping getMapping() {
		return mapping;
	}
}
