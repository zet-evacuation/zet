/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package statistic.ca.results;

/**
 *
 * @author Sylvie
 */
public class StoredCAStatisticResults {
    
    private StoredCAStatisticResultsForIndividuals storedCAStatisticResultsForIndividuals;
    private StoredCAStatisticResultsForCells storedCAStatisticResultsForCells;


    
    public StoredCAStatisticResults() {
        storedCAStatisticResultsForIndividuals=new StoredCAStatisticResultsForIndividuals();
        storedCAStatisticResultsForCells=new StoredCAStatisticResultsForCells();
    }
    
    
    public StoredCAStatisticResultsForCells getStoredCAStatisticResultsForCells() {
        return storedCAStatisticResultsForCells;
    }

    public StoredCAStatisticResultsForIndividuals getStoredCAStatisticResultsForIndividuals() {
        return storedCAStatisticResultsForIndividuals;
    }    
    
    

}
