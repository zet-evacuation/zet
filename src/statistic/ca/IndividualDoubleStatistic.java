/*
 * Created on 03.07.2008
 *
 */
package statistic.ca;

import statistic.graph.IntegerDoubleMapping;
import statistic.graph.Statistic;
import statistic.graph.Statistics;
import ds.ca.Cell;
import ds.ca.Individual;

/**
 * @author Daniel Pluempe
 *
 */
public enum IndividualDoubleStatistic implements Statistic<Individual, Double, CAData>{
    
    TIME_TO_SAFETY("Zeit bis Sicherheit"){
        public Double calculate(Statistics<CAData> statistics, Individual individual) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }        
    },
    
    TIME_TO_EXIT("Zeit bis zur Evakuierung"){
        public Double calculate(Statistics<CAData> statistics, Individual individual) {
            throw new UnsupportedOperationException("This feature has not been implemented yet.");
        }        
    }
    ;

    private String description;
    
    private IndividualDoubleStatistic(String description){
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
    public Class<Double> range() {
        return Double.class;
    }

}
