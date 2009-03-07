package statistic.ca;

import statistic.graph.IntegerDoubleMapping;
import statistic.graph.Statistic;
import statistic.graph.Statistics;
import ds.ca.Cell;

/**
 * @author Daniel Pluempe
 *
 */
public enum DynamicCellStatistic implements Statistic<Cell, IntegerDoubleMapping, CAData> {
    INCOMING_INDIVIDUAL_RATE("Ankommende Individuenrate") {
        public IntegerDoubleMapping calculate(Statistics<CAData> statistics, Cell cell) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    },
    
    OUTGOING_INDIVIDUAL_RATE("Ausgehende Individuenrate") {
        public IntegerDoubleMapping calculate(Statistics<CAData> statistics, Cell cell) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    },
    
    INCOMING_INDIVUDAL_AMOUNT("Anzahl angekommener Individuen"){
        public IntegerDoubleMapping calculate(Statistics<CAData> statistics, Cell cell) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    },
    
    OUTGOING_INDIVIDUAL_AMOUNT("Anzahl ausgegangener Individuen"){
        public IntegerDoubleMapping calculate(Statistics<CAData> statistics, Cell cell) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    },
        
    TIME_BLOCKED("Blockadezeit"){
        public IntegerDoubleMapping calculate(Statistics<CAData> statistics, Cell cell) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    },
    
    UTILISATION("Auslastung"){
        public IntegerDoubleMapping calculate(Statistics<CAData> statistics, Cell cell) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    },
    
    BLOCKED_TIMESTEPS("Blockadezeitpunkte"){
        public IntegerDoubleMapping calculate(Statistics<CAData> statistics, Cell cell) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    }   
    
    ;
    
    private String description;

    private DynamicCellStatistic(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see statistic.graph.Statistic#range()
     */
    @Override
    public Class<IntegerDoubleMapping> range() {
        return IntegerDoubleMapping.class;
    }
    
    @Override
    public String toString() {
        return description;
    }
}
