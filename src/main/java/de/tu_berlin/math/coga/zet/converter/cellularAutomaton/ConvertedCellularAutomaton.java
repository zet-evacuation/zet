package de.tu_berlin.math.coga.zet.converter.cellularAutomaton;

import org.zet.cellularautomaton.EvacuationCellularAutomaton;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ConvertedCellularAutomaton {

    private EvacuationCellularAutomaton cellularAutomaton;
    private ZToCAMapping mapping;
    private ZToCARasterContainer container;

    public ConvertedCellularAutomaton(EvacuationCellularAutomaton cellularAutomaton, ZToCAMapping mapping, ZToCARasterContainer container) {
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
