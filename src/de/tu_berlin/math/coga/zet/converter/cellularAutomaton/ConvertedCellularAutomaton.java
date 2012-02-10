/**
 * ConvertedCellularAutomaton.java
 * Created: Jul 28, 2010,5:10:10 PM
 */
package de.tu_berlin.math.coga.zet.converter.cellularAutomaton;

import ds.ca.evac.EvacuationCellularAutomaton;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ConvertedCellularAutomaton {
	private EvacuationCellularAutomaton cellularAutomaton;
	private ZToCAMapping mapping;
	private ZToCARasterContainer container;

	public ConvertedCellularAutomaton( EvacuationCellularAutomaton cellularAutomaton, ZToCAMapping mapping, ZToCARasterContainer container ) {
		this.cellularAutomaton = cellularAutomaton;
		this.mapping = mapping;
		this.container = container;
	}

	public EvacuationCellularAutomaton getCellularAutomaton() {
		return cellularAutomaton;
	}

	public ZToCARasterContainer getContainer() {
		return container;
	}

	public ZToCAMapping getMapping() {
		return mapping;
	}
}
