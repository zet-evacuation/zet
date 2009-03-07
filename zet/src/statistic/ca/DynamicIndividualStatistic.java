/*
 * Created on 03.07.2008
 *
 */
package statistic.ca;

import statistic.graph.IntegerDoubleMapping;
import statistic.graph.Statistic;
import statistic.graph.Statistics;
import ds.ca.Individual;

/**
 * @author Daniel Pluempe
 *
 */
public enum DynamicIndividualStatistic implements Statistic<Individual, IntegerDoubleMapping, CAData>{

    PANIC("Panik"){
        public IntegerDoubleMapping calculate(Statistics<CAData> statistics, Individual individual) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    },

    EXHAUSTION("Erschöpfung"){
        public IntegerDoubleMapping calculate(Statistics<CAData> statistics, Individual individual) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    },
    
    SPEED("Geschwindigkeit"){
        public IntegerDoubleMapping calculate(Statistics<CAData> statistics, Individual individual) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    },
    
    COVERED_DISTANCE("Zurückgelegte Distanz"){
        public IntegerDoubleMapping calculate(Statistics<CAData> statistics, Individual individual) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    },
    
    WAITED_TIME("Wartezeit"){
        public IntegerDoubleMapping calculate(Statistics<CAData> statistics, Individual individual) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    },
    
    DISTANCE_TO_NEAREST_EXIT("Abstand zum nächsten Ausgang"){
        public IntegerDoubleMapping calculate(Statistics<CAData> statistics, Individual individual) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    },
    
    DISTANCE_TO_PLANNED_EXIT("Abstand zum geplanten Ausgang"){
        public IntegerDoubleMapping calculate(Statistics<CAData> statistics, Individual individual) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }
    }
    ;
    
    private String description;
    
    private DynamicIndividualStatistic(String description){
        this.description = description;
    }

    @Override
    public String toString(){
        return description;
    }
    
    /* (non-Javadoc)
     * @see statistic.graph.Statistic#range()
     */
    @Override
    public Class<IntegerDoubleMapping> range() {
        return IntegerDoubleMapping.class;
    }
}
