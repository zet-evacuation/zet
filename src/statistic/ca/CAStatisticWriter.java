

package statistic.ca;

import statistic.ca.results.StoredCAStatisticResults;

/**
 *
 * @author Sylvie
 */
public class CAStatisticWriter {


    private StoredCAStatisticResults storedCAStatisticResults;
    
    public CAStatisticWriter() {
        storedCAStatisticResults = new StoredCAStatisticResults();
    }
    
    public StoredCAStatisticResults getStoredCAStatisticResults() {
        return storedCAStatisticResults;
    }
    
    public void saveStatistic() {
        // X-Stream-Kram zum speichern von storedCAStatisticResults in Datei
    }
        

}
