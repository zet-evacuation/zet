/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */


package statistic.ca.results;

/**
 *
 * @author Sylvie
 */

import java.util.HashMap;
import java.util.ArrayList;
import ds.ca.evac.Individual;
import ds.ca.evac.ExitCell;
import ds.ca.evac.StaticPotential;
import gui.ZETLoader;
import gui.ZETMain;

/**
 *
 * @author Sylvie
 */
public class StoredCAStatisticResultsForIndividuals {



    
    HashMap<Individual,Integer> safetyTimes;
    HashMap<Individual,ArrayList<Integer>> changePotentialTimes;
    private HashMap<Individual,ArrayList<ArrayList<ExitCell>>> potentials;
    HashMap<Individual,ArrayList<Integer>> coveredDistanceTimes;
    private HashMap<Individual,ArrayList<Double>> coveredDistance;
    HashMap<Individual,ArrayList<Integer>> waitedTimeTimes;
    private HashMap<Individual,ArrayList<Integer>> waitedTime;    
    HashMap<Individual,Double> minDistanceToNearestExit;
    HashMap<Individual,Double> minDistanceToPlannedExit;
    private HashMap<Individual,StaticPotential> takenExit;
    HashMap<Individual,ArrayList<Integer>> panicTimes;
    private HashMap<Individual,ArrayList<Double>> panic;
    HashMap<Individual,ArrayList<Integer>> exhaustionTimes;
    private HashMap<Individual,ArrayList<Double>> exhaustion; 
    HashMap<Individual,ArrayList<Integer>> currentSpeedTimes;
    private HashMap<Individual,ArrayList<Double>> currentSpeed;
    
    
    public StoredCAStatisticResultsForIndividuals(){
	safetyTimes = new HashMap<>();
        changePotentialTimes= new HashMap<Individual,ArrayList<Integer>>();
        potentials= new HashMap<Individual,ArrayList<ArrayList<ExitCell>>>();
        coveredDistanceTimes = new HashMap<Individual,ArrayList<Integer>>();
        coveredDistance= new HashMap<Individual,ArrayList<Double>>();
        waitedTimeTimes = new HashMap<Individual,ArrayList<Integer>>();
        waitedTime= new HashMap<Individual,ArrayList<Integer>>();        
        minDistanceToNearestExit= new HashMap<Individual,Double>();
        minDistanceToPlannedExit= new HashMap<Individual,Double>();
        takenExit= new HashMap<Individual,StaticPotential>();
        panicTimes = new HashMap<Individual,ArrayList<Integer>>();
        panic= new HashMap<Individual,ArrayList<Double>>();
        exhaustionTimes = new HashMap<Individual,ArrayList<Integer>>();
        exhaustion= new HashMap<Individual,ArrayList<Double>>();    
        currentSpeedTimes = new HashMap<Individual,ArrayList<Integer>>();
        currentSpeed= new HashMap<Individual,ArrayList<Double>>();
        
    }
    
    
    public void addSafeIndividualToStatistic(Individual ind) {
			
        if(!(safetyTimes.containsKey(ind))){
            safetyTimes.put(ind, ind.getSafetyTime());
        }
    }
    
    
    public void addChangedPotentialToStatistic(Individual ind, int t){
			if( !ZETLoader.useStatistic )
				return;
			
        if(!(changePotentialTimes.containsKey(ind))){
            changePotentialTimes.put(ind, new ArrayList<Integer>());
            potentials.put(ind, new ArrayList<ArrayList<ExitCell>>());
        }                 
            changePotentialTimes.get(ind).add(t);
            potentials.get(ind).add(ind.getStaticPotential().getAssociatedExitCells());
    }
    
    
    public void addCoveredDistanceToStatistic(Individual ind, int t, double distance){
			if( !ZETLoader.useStatistic )
				return;
			
        double lastCoveredDistance=0;
        if(!(coveredDistanceTimes.containsKey(ind))){
            coveredDistanceTimes.put(ind, new ArrayList<Integer>());
            coveredDistance.put(ind, new ArrayList<Double>());
        }
        else {
            lastCoveredDistance = coveredDistance.get(ind).get(coveredDistance.get(ind).size()-1);
        }
        coveredDistanceTimes.get(ind).add(t);
        coveredDistance.get(ind).add(distance+lastCoveredDistance);
    }
        
    
    public void addWaitedTimeToStatistic(Individual ind, int t) {
			if( !ZETLoader.useStatistic )
				return;
			
        int lastWaitedTime=0;
        if(!(waitedTimeTimes.containsKey(ind))){
            waitedTimeTimes.put(ind, new ArrayList<Integer>());
            waitedTime.put(ind, new ArrayList<Integer>());
        }
        else {
            lastWaitedTime = waitedTime.get(ind).get(waitedTime.get(ind).size()-1);
        }
        waitedTimeTimes.get(ind).add(t);
        waitedTime.get(ind).add(1+lastWaitedTime);
    }

 
    
    public void addMinDistancesToStatistic(Individual ind, double distNearest, double distPlanned) {
			if( !ZETLoader.useStatistic )
				return;
			
        if(!(minDistanceToNearestExit.containsKey(ind))){
            minDistanceToNearestExit.put(ind, new Double(distNearest));
        }
        if(!(minDistanceToPlannedExit.containsKey(ind))){
            minDistanceToPlannedExit.put(ind, new Double(distPlanned));
        }
    }
    
    public void addExitToStatistic (Individual ind, StaticPotential exit) {
        takenExit.put(ind, exit);
    }
    
    
    public void addExhaustionToStatistic(Individual ind, int t, double actualExhaustion){
			if( !ZETLoader.useStatistic )
				return;
			
        if(!(exhaustionTimes.containsKey(ind))){
            exhaustionTimes.put(ind, new ArrayList<Integer>());
            exhaustion.put(ind, new ArrayList<Double>());
        }
        exhaustionTimes.get(ind).add(t);
        exhaustion.get(ind).add(actualExhaustion);

    }
    
    public void addPanicToStatistic(Individual ind, int t, double actualPanic){
			if( !ZETLoader.useStatistic )
				return;
			
        if(!(panicTimes.containsKey(ind))){
            panicTimes.put(ind, new ArrayList<Integer>());
            panic.put(ind, new ArrayList<Double>());
        }
        panicTimes.get(ind).add(t);
        panic.get(ind).add(actualPanic);
    }    
    
    
    public void addCurrentSpeedToStatistic(Individual ind, int t, double speed){
			if( !ZETLoader.useStatistic )
				return;
			
        if(!(currentSpeedTimes.containsKey(ind))){
            currentSpeedTimes.put(ind, new ArrayList<Integer>());
            currentSpeed.put(ind, new ArrayList<Double>());
        }
        currentSpeedTimes.get(ind).add(t);
        currentSpeed.get(ind).add(speed);
    }    
    
    
    
    public HashMap<Individual, ArrayList<Integer>> getHashMapChangePotentialTimes() {
        return changePotentialTimes;
    }

    public HashMap<Individual, ArrayList<Double>> getHashMapCoveredDistance() {
        return coveredDistance;
    }

    public HashMap<Individual, ArrayList<Integer>> getHashMapCoveredDistanceTimes() {
        return coveredDistanceTimes;
    }

    public HashMap<Individual, Double> getHashMapMinDistanceToNearestExit() {
        return minDistanceToNearestExit;
    }

    public HashMap<Individual, Double> getHashMapMinDistanceToPlannedExit() {
        return minDistanceToPlannedExit;
    }

    public HashMap<Individual, ArrayList<ArrayList<ExitCell>>> getHashMapPotentials() {
        return potentials;
    }

    public HashMap<Individual, Integer> getHashMapSafetyTimes() {
        return safetyTimes;
    }

    public HashMap<Individual, ArrayList<Integer>> getHashMapWaitedTime() {
        return waitedTime;
    }

    public HashMap<Individual, ArrayList<Integer>> getHashMapWaitedTimeTimes() {
        return waitedTimeTimes;
    }

    
        public HashMap<Individual, StaticPotential> getHashMapTakenExit() {
        return takenExit;
    }
        

    public HashMap<Individual, ArrayList<Double>> getHashMapExhaustion() {
        return exhaustion;
    }

    public HashMap<Individual, ArrayList<Integer>> getHashMapExhaustionTimes() {
        return exhaustionTimes;
    }

    public HashMap<Individual, ArrayList<Double>> getHashMapPanic() {
        return panic;
    }

    public HashMap<Individual, ArrayList<Integer>> getHashMapPanicTimes() {
        return panicTimes;
    }
 
    
    public HashMap<Individual, ArrayList<Double>> getHashMapCurrentSpeed() {
        return currentSpeed;
    }

    public HashMap<Individual, ArrayList<Integer>> getHashMapCurrentSpeedTimes() {
        return currentSpeedTimes;
    }    
    
 
}//end class


