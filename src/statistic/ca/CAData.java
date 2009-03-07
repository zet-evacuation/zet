package statistic.ca;

import statistic.common.Data;

/**
 * @author Daniel Pluempe
 *
 */
public class CAData extends Data {
    
    private CellStatistic cellStatistic;
    private IndividualStatistic individualStatistic;
    private CAStatistic caStatistic;
    
    public CAData(CellStatistic cellStatistic, 
            IndividualStatistic individualStatistic, 
            CAStatistic caStatistic
    ){
        this.cellStatistic = cellStatistic;
        this.individualStatistic = individualStatistic;
        this.caStatistic = caStatistic;
    }
    
    public CellStatistic getCellStatistic(){
        return cellStatistic;
    }
    
    public IndividualStatistic getIndividualStatistic(){
        return individualStatistic;
    }
    
    public CAStatistic getCAStatistic(){
        return caStatistic;
    }
}
