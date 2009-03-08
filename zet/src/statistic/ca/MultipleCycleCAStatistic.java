/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
package statistic.ca;

import java.util.ArrayList;
import java.util.HashMap;

import statistic.ca.exception.*;
import ds.ca.Cell;
import ds.ca.ExitCell;
import ds.ca.Individual;
import ds.ca.Room;
import ds.ca.StaticPotential;


/**
 * This class calculates statistics for several cycles of an cellular automaton. The values are obtained 
 * by getting the mean average over all cycles.
 * 
 * @author Matthias Woste
 *
 */

public class MultipleCycleCAStatistic implements CellStatisticMethods {
	
	/** number of cycles to run */
	private double numberOfCycles;
	
	/** ArrayList storing all cycles */
	private ArrayList<CAStatistic> cycles;
	
	public MultipleCycleCAStatistic(int c){
		numberOfCycles = c;
		cycles = new ArrayList<CAStatistic>(c);
	}
	
	public MultipleCycleCAStatistic(ArrayList<CAStatistic> cycles){
		this.cycles = new ArrayList<CAStatistic>(cycles.size());
		this.cycles.addAll(cycles);
		numberOfCycles = cycles.size();
	}
	
	public MultipleCycleCAStatistic(CAStatistic[] cycles){
		this.cycles = new ArrayList<CAStatistic>(cycles.length);
		for(int i = 0; i < cycles.length ; i++){
			this.cycles.add(cycles[i]);
		}
		numberOfCycles = cycles.length;
	}

	/**
	 * Adds a cycle of a cellular automaton to the list
	 * @param cas cycle of ca
	 */
	public void addCycle(CAStatistic cas){
		cycles.add(cas);
		numberOfCycles++;
	}
	
	/**
	 * Returns the cycle of a cellular automaton stored at the given index
	 * @param i index of the cycle
	 * @return CAStatistic object
	 */
	public CAStatistic getCycle(int i){
		return cycles.get(i);
	}
	
	@Override
	public double calculatedOverallSingleRoomUtilization(Room r, int o) {
		double accumulatedValue = 0.0;
		for(CAStatistic cas : cycles){
			accumulatedValue += cas.getCellStatistic().calculatedOverallSingleRoomUtilization(r, o);
		}
		return accumulatedValue / numberOfCycles;
	}

	@Override
	public double calculatedOverallSingleRoomWaitingTime(Room r, int o) {
		double accumulatedValue = 0.0;
		for(CAStatistic cas : cycles){
			accumulatedValue += cas.getCellStatistic().calculatedOverallSingleRoomWaitingTime(r, o);
		}
		return accumulatedValue / numberOfCycles;
	}

	@Override
	public double calculatedSingleRoomUtilization(Room r, int t) {
		double accumulatedValue = 0.0;
		for(CAStatistic cas : cycles){
			accumulatedValue += cas.getCellStatistic().calculatedSingleRoomUtilization(r, t);
		}
		return accumulatedValue / numberOfCycles;
	}

	@Override
	public double calculatedSingleRoomWaitingTime(Room r, int t) {
		double accumulatedValue = 0.0;
		for(CAStatistic cas : cycles){
			accumulatedValue += cas.getCellStatistic().calculatedSingleRoomWaitingTime(r, t);
		}
		return accumulatedValue / numberOfCycles;
	}

	@Override
	public HashMap<Room, Double> calculateOverallRoomUtilization(
			ArrayList<Room> rooms, int o) {
		HashMap<Room, Double> accumulatedValue = new HashMap<Room, Double>();
		HashMap<Room, Double> tmpValue = new HashMap<Room, Double>();
		HashMap<Room, Double> returnValue = new HashMap<Room, Double>();
		Double oldValue;
		for(CAStatistic cas : cycles){
			tmpValue = cas.getCellStatistic().calculateOverallRoomUtilization(rooms, o);
			for(Room r : tmpValue.keySet()){
				if(accumulatedValue.containsKey(r)){
					oldValue = accumulatedValue.get(r);
					oldValue += tmpValue.get(r);
					accumulatedValue.remove(r);
					accumulatedValue.put(r, oldValue);
				}
			}
		}
		for(Room r : accumulatedValue.keySet()){
			returnValue.put(r,accumulatedValue.get(r) / numberOfCycles);
		}
		
		return returnValue;
	}

	@Override
	public HashMap<Room, Double> calculateOverallRoomWaitingTime(
			ArrayList<Room> rooms, int o) {
		HashMap<Room, Double> accumulatedValue = new HashMap<Room, Double>();
		HashMap<Room, Double> tmpValue = new HashMap<Room, Double>();
		HashMap<Room, Double> returnValue = new HashMap<Room, Double>();
		Double oldValue;
		for(CAStatistic cas : cycles){
			tmpValue = cas.getCellStatistic().calculateOverallRoomWaitingTime(rooms, o);
			for(Room r : tmpValue.keySet()){
				if(accumulatedValue.containsKey(r)){
					oldValue = accumulatedValue.get(r);
					oldValue += tmpValue.get(r);
					accumulatedValue.remove(r);
					accumulatedValue.put(r, oldValue);
				}
			}
		}
		for(Room r : accumulatedValue.keySet()){
			returnValue.put(r,accumulatedValue.get(r) / numberOfCycles);
		}
		
		return returnValue;
	}

	@Override
	public int getCellUtilization(Cell c, int t)
			throws IllegalArgumentException {
		int accumulatedValue = 0;
		for(CAStatistic cas : cycles){
			accumulatedValue += cas.getCellStatistic().getCellUtilization(c, t);
		}
		return accumulatedValue / (int)numberOfCycles;
	}

	@Override
	public int getCellWaitingTime(Cell c, int t)
			throws IllegalArgumentException {
		int accumulatedValue = 0;
		for(CAStatistic cas : cycles){
			accumulatedValue += cas.getCellStatistic().getCellWaitingTime(c, t);
		}
		return accumulatedValue / (int)numberOfCycles;
	}

	@Override
	public double getOverallCellUtilization(Cell c, int o) {
		double accumulatedValue = 0.0;
		for(CAStatistic cas : cycles){
			accumulatedValue += cas.getCellStatistic().getOverallCellUtilization(c, o);
		}
		return accumulatedValue / numberOfCycles;
	}

	@Override
	public double getOverallWaitingTime(Cell c, int o) {
		double accumulatedValue = 0.0;
		for(CAStatistic cas : cycles){
			accumulatedValue += cas.getCellStatistic().getOverallWaitingTime(c, o);
		}
		return accumulatedValue / numberOfCycles;
	}

	//needed for "durchschnittliche Geschwindigkeit
        public double calculateAverageAverageSpeedForGroup(
			ArrayList<ArrayList<Individual>> indgroup, int time)
			throws MissingStoredValueException, IncorrectTimeException, AllCyclesNoValueBecauseAlreadySafeException {
		double accumulatedValue = 0.0;
                double noOfNoValueExc=0;
		for(int i = 0; i < cycles.size(); i++){
                    try {
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateAverageAverageSpeedForGroup(indgroup.get(i), time);
                    }
                    catch (GroupOfIndsNoValueBecauseAlreadySafeException e) {noOfNoValueExc++;}
                }
                if (noOfNoValueExc==numberOfCycles) {
                    throw new AllCyclesNoValueBecauseAlreadySafeException();
                }
		return accumulatedValue / (numberOfCycles-noOfNoValueExc);
	}

	
	// needed for "durschnittliche Geschwindigkeit über Zeit"
        public double calculateAverageSpeedForGroupInOneTimestep(
			ArrayList<ArrayList<Individual>> indgroup, int timestep)
			throws MissingStoredValueException, IncorrectTimeException, AllCyclesNoValueBecauseAlreadySafeException {
		double accumulatedValue = 0.0;
                double noOfNoValueExc=0;
		for(int i = 0; i < cycles.size(); i++){
                    try {
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateAverageSpeedForGroupInOneTimestep(indgroup.get(i), timestep);
                    }
                    catch (GroupOfIndsNoValueBecauseAlreadySafeException e) {noOfNoValueExc++;}
                }
                if (noOfNoValueExc==numberOfCycles) {
                    throw new AllCyclesNoValueBecauseAlreadySafeException();
                }
		return accumulatedValue / (numberOfCycles-noOfNoValueExc);
	}
        
        
// needed for "Maximale Geschwindigkeit über Zeit"
	public double calculateMaxSpeedForGroupInOneTimestep(
			ArrayList<ArrayList<Individual>> indgroup, int timestep)
			throws MissingStoredValueException, IncorrectTimeException, AllCyclesNoValueBecauseAlreadySafeException {
		double accumulatedValue = 0.0;
                double noOfNoValueExc=0;
		for(int i = 0; i < cycles.size(); i++){
                    try {
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMaxSpeedForGroupInOneTimestep(indgroup.get(i), timestep);
                    }
                    catch (GroupOfIndsNoValueBecauseAlreadySafeException e) {noOfNoValueExc++;}
                }
                if (noOfNoValueExc==numberOfCycles) {
                    throw new AllCyclesNoValueBecauseAlreadySafeException();
                }
		return accumulatedValue / (numberOfCycles-noOfNoValueExc);
	}

	//needed for "zurückgelegte Distanz"
	public double calculateAverageCoveredDistanceForGroup(
			ArrayList<ArrayList<Individual>> indgroup, int time)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateAverageCoveredDistanceForGroup(indgroup.get(i), time);
		}
		return accumulatedValue / numberOfCycles;
	}

	//needed for "Distanz über Zeit"
	public ArrayList<Double> calculateAverageCoveredDistanceForGroupInTimeSteps(
			ArrayList<ArrayList<Individual>> indgroup, int from, int to)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException {
		ArrayList<Double> accumulatedValues = new ArrayList<Double>();
                ArrayList<Double> valuesForOneCycle = new ArrayList<Double>();
                ArrayList<Double> averageAboutCyclesValues = new ArrayList<Double>();
                for (int timeStep= from; timeStep<=to; timeStep++) {
                    accumulatedValues.add(new Double(0));
                }
		for(int i = 0; i < cycles.size(); i++){

			valuesForOneCycle = cycles.get(i).getIndividualStatistic().calculateAverageCoveredDistanceForGroupInTimeSteps(indgroup.get(i), from, to);

                        for (int j= 0; j<=to-from; j++) {
                            accumulatedValues.set(j, accumulatedValues.get(j)+valuesForOneCycle.get(j));
                        }
                }
                for (int j= 0; j<=to-from; j++) {
                    averageAboutCyclesValues.add(accumulatedValues.get(j)/numberOfCycles);
                }
                return averageAboutCyclesValues;
	}

	
	//needed for "Maximale Geschwindigkeit"
        public double calculateAverageMaxSpeedForGroup(
			ArrayList<ArrayList<Individual>> indgroup, int time)
			throws MissingStoredValueException, IncorrectTimeException, AllCyclesNoValueBecauseAlreadySafeException {
		double accumulatedValue = 0.0;
                double noOfNoValueExc=0;
		for(int i = 0; i < cycles.size(); i++){
                    try {
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateAverageMaxSpeedForGroup(indgroup.get(i), time);
                    }
                    catch (GroupOfIndsNoValueBecauseAlreadySafeException e) {noOfNoValueExc++;}
                }
                if (noOfNoValueExc==numberOfCycles) {
                    throw new AllCyclesNoValueBecauseAlreadySafeException();
                }
		return accumulatedValue / (numberOfCycles-noOfNoValueExc);
	}
	
	public double calculateAverageMaxSpeedForGroup(
			ArrayList<ArrayList<Individual>> indgroup, int from, int to)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateAverageMaxSpeedForGroup(indgroup.get(i), from, to);
		}
		return accumulatedValue / numberOfCycles;
	}
        

	//needed for "durchschnittliche Zeit bis Safe"
	public double calculateAverageSafetyTimeForGroup(
			ArrayList<ArrayList<Individual>> indgroup) throws GroupOfIndsNotSafeException,
			AllCyclesNoValueBecauseNotSafeException {
                double noOfNotSafeExc=0;
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
                    try {
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateAverageSafetyTimeForGroup(indgroup.get(i));
                    }
                    catch (GroupOfIndsNotSafeException e) {noOfNotSafeExc++;}
		}
                if (noOfNotSafeExc==numberOfCycles) {
                    throw new AllCyclesNoValueBecauseNotSafeException();
                }
		return accumulatedValue / (numberOfCycles-noOfNotSafeExc);                
	}

	
	public double calculateAverageSpeed(ArrayList<Individual> ind, int time)
			throws OneIndNoPotentialException, IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateAverageSpeed(ind.get(i), time);		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double calculateAverageSpeed(ArrayList<Individual> ind, int from, int to)
			throws OneIndNoPotentialException, IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateAverageSpeed(ind.get(i), from, to);
		}
		return accumulatedValue / numberOfCycles;
	}

	//needed for "durchschnittliche Blockadezeit"
	public double calculateAverageWaitedTimeForGroup(
			ArrayList<ArrayList<Individual>> indgroup, int time)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateAverageWaitedTimeForGroup(indgroup.get(i), time);
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double calculateAverageWaitedTimeForGroup(
			ArrayList<ArrayList<Individual>> indgroup, int from, int to)
			throws GroupOfIndividualsException, IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateAverageWaitedTimeForGroup(indgroup.get(i), from, to);
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double calculateDifferenceMinAndRealDistanceToPlannedExit(
			ArrayList<Individual> ind, int time) throws OneIndNoPotentialException,
			IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateDifferenceMinAndRealDistanceToPlannedExit(ind.get(i), time);
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double calculateDifferenceNearestAndPlannedExit(ArrayList<Individual> ind)
			throws OneIndNoPotentialException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateDifferenceNearestAndPlannedExit(ind.get(i));
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double calculateMaxAverageSpeedForGroup(
			ArrayList<ArrayList<Individual>> indgroup, int time)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMaxAverageSpeedForGroup(indgroup.get(i), time);
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double calculateMaxAverageSpeedForGroup(
			ArrayList<ArrayList<Individual>> indgroup, int from, int to)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMaxAverageSpeedForGroup(indgroup.get(i), from, to);
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double calculateMaxMaxSpeedForGroup(ArrayList<ArrayList<Individual>> indgroup,
			int time) throws GroupOfIndsNoPotentialException,
			IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMaxMaxSpeedForGroup(indgroup.get(i), time);
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double calculateMaxMaxSpeedForGroup(ArrayList<ArrayList<Individual>> indgroup,
			int from, int to) throws GroupOfIndsNoPotentialException,
			IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMaxMaxSpeedForGroup(indgroup.get(i), from, to);
		}
		return accumulatedValue / numberOfCycles;
	}

	// needed for "maximale Zeit bis Safe"
	public double calculateMaxSafetyTimeForGroup(ArrayList<ArrayList<Individual>> indgroup)
			throws AllCyclesNoValueBecauseNotSafeException {
                double noOfNotSafeExc=0;
		double accumulatedValue = 0;
		for(int i = 0; i < cycles.size(); i++){
                    try {
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMaxSafetyTimeForGroup(indgroup.get(i));
                    }
                    catch (GroupOfIndsNotSafeException e) {noOfNotSafeExc++;}
		}
                if (noOfNotSafeExc==numberOfCycles) {
                    throw new AllCyclesNoValueBecauseNotSafeException();
                }
		return accumulatedValue / (numberOfCycles-noOfNotSafeExc);                
	}

	
	public double calculateMaxSpeed(ArrayList<Individual> ind, int from, int to)
			throws OneIndNoPotentialException, IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMaxSpeed(ind.get(i), from, to);
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double calculateMaxSpeed(ArrayList<Individual> ind, int time)
			throws OneIndNoPotentialException, IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMaxSpeed(ind.get(i), time);
		}
		return accumulatedValue / numberOfCycles;
	}
        
        
	//needed for "Panik über Zeit"
	public double getPanicForGroup(ArrayList<ArrayList<Individual>> indgroup, int timestep)
                throws IncorrectTimeException, AllCyclesNoValueBecauseAlreadySafeException {
		double accumulatedValue = 0.0;
                double noOfNoValueExc=0;
		for(int i = 0; i < cycles.size(); i++){
                    try {
			accumulatedValue += cycles.get(i).getIndividualStatistic().getPanicForGroup(indgroup.get(i), timestep);
                    }
                    catch (GroupOfIndsNoValueBecauseAlreadySafeException e) {noOfNoValueExc++;}
                }
                if (noOfNoValueExc==numberOfCycles) {
                    throw new AllCyclesNoValueBecauseAlreadySafeException();
                }
		return accumulatedValue / (numberOfCycles-noOfNoValueExc);
	}
        	
            
            
            

	//needed for "Erschöpfung über Zeit"
	public double getExhaustionForGroup(ArrayList<ArrayList<Individual>> indgroup, int timestep)
                throws IncorrectTimeException, AllCyclesNoValueBecauseAlreadySafeException {
		double accumulatedValue = 0.0;
                double noOfNoValueExc=0;
		for(int i = 0; i < cycles.size(); i++){
                    try {
			accumulatedValue += cycles.get(i).getIndividualStatistic().getExhaustionForGroup(indgroup.get(i), timestep);
                    }
                    catch (GroupOfIndsNoValueBecauseAlreadySafeException e) {noOfNoValueExc++;}
                }
                if (noOfNoValueExc==numberOfCycles) {
                    throw new AllCyclesNoValueBecauseAlreadySafeException();
                }
		return accumulatedValue / (numberOfCycles-noOfNoValueExc);
	}
 

	
	public double calculateMaxWaitedTimeForGroup(ArrayList<ArrayList<Individual>> indgroup,
			int from, int to) throws GroupOfIndividualsException,
			IncorrectTimeException {
		double accumulatedValue = 0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMaxWaitedTimeForGroup(indgroup.get(i), from, to);
		}
		return accumulatedValue / numberOfCycles;
	}

	// needed for "maximale Blockadezeit"
	public double calculateMaxWaitedTimeForGroup(ArrayList<ArrayList<Individual>> indgroup,
			int time) throws GroupOfIndsNoPotentialException,IncorrectTimeException {
		double accumulatedValue = 0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMaxWaitedTimeForGroup(indgroup.get(i), time);
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double calculateMinAverageSpeedForGroup(
			ArrayList<ArrayList<Individual>> indgroup, int time)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMinAverageSpeedForGroup(indgroup.get(i), time);
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double calculateMinAverageSpeedForGroup(
			ArrayList<ArrayList<Individual>> indgroup, int from, int to)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMinAverageSpeedForGroup(indgroup.get(i), from, to);
		}
		return accumulatedValue / numberOfCycles;
	}

	// needed for "minimale Zeit bis Safe"
	public double calculateMinSafetyTimeForGroup(ArrayList<ArrayList<Individual>> indgroup)
			throws AllCyclesNoValueBecauseNotSafeException{
		double accumulatedValue = 0;
                double noOfNotSafeExc=0;
		for(int i = 0; i < cycles.size(); i++){
                    try {
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMinSafetyTimeForGroup(indgroup.get(i));
                    }
                    catch (GroupOfIndsNotSafeException e) {noOfNotSafeExc++;}
		}
                if (noOfNotSafeExc==numberOfCycles) {
                    throw new AllCyclesNoValueBecauseNotSafeException();
                }
		return accumulatedValue / (numberOfCycles-noOfNotSafeExc);                
	}

	
	public double calculateMinWaitedTimeForGroup(ArrayList<ArrayList<Individual>> indgroup,
			int from, int to) throws GroupOfIndividualsException,
			IncorrectTimeException {
		double accumulatedValue = 0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMinWaitedTimeForGroup(indgroup.get(i), from, to);
		}
                return accumulatedValue / numberOfCycles;
	}

	//needed for "minimale Blockadezeit"
	public double calculateMinWaitedTimeForGroup(ArrayList<ArrayList<Individual>> indgroup,
			int time) throws GroupOfIndsNoPotentialException,IncorrectTimeException {
		double accumulatedValue = 0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculateMinWaitedTimeForGroup(indgroup.get(i), time);
		}
                return accumulatedValue / numberOfCycles;
	}

	//needed for "evakuierte Individuen in Prozent"
	public double calculatePercentageOfSaveIndividuals(
			ArrayList<ArrayList<Individual>> indgroup) {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().calculatePercentageOfSaveIndividuals(indgroup.get(i));
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double getCoveredDistance(ArrayList<Individual> ind, int from, int to)
			throws OneIndNoPotentialException, IncorrectTimeException,
			IllegalArgumentException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().getCoveredDistance(ind.get(i), from, to);
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double getCoveredDistance(ArrayList<Individual> ind, int t)
			throws OneIndNoPotentialException, IncorrectTimeException,
			IllegalArgumentException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().getCoveredDistance(ind.get(i), t);
		}
		return accumulatedValue / numberOfCycles;
	}

	//needed for "Ankunftskurve"
	public double getNumberOfSafeIndividualForGroup(
			ArrayList<ArrayList<Individual>> indgroup, int time)
			throws IncorrectTimeException {
		double accumulatedValue = 0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().getNumberOfSafeIndividualForGroup(indgroup.get(i), time);
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public ArrayList<ExitCell> getPlannedExit(ArrayList<Individual> ind, int t)
			throws OneIndNoPotentialException, IllegalArgumentException {
		ArrayList<ExitCell> accumulatedValue = new ArrayList<ExitCell>((int)numberOfCycles);
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue.addAll(cycles.get(i).getIndividualStatistic().getPlannedExit(ind.get(i), t));
		}
		return accumulatedValue;
	}
        
        //needed for "Ausgangsverteilung"
	public HashMap<String, Double> getTakenExit(ArrayList<ArrayList<Individual>> ind)
			{
		
		HashMap<String, Double> exitUtilization = new HashMap<String, Double>();
                StaticPotential exit;
		for(int i = 0; i < cycles.size(); i++){
			for (Individual indi : ind.get(i)) {
                            try {
                                exit=cycles.get(i).getIndividualStatistic().getTakenExit(indi);
                            }
                            catch (OneIndNoPotentialException e) {continue;}
                            
                            if (!exitUtilization.containsKey(exit.getName())) {
                                exitUtilization.put(exit.getName(),1.0);                                              
                            }
                            else {
                                exitUtilization.put(exit.getName(),new Double(exitUtilization.get(exit.getName()) + 1));
                            }
                        }
                }
  
		return exitUtilization;
	}

	
	public double getSafetyTime(ArrayList<Individual> ind) throws OneIndNotSafeException {
		double accumulatedValue = 0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().getSafetyTime(ind.get(i));
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double getWaitedTime(ArrayList<Individual> ind, int t)
			throws OneIndNoPotentialException, IncorrectTimeException,
			IllegalArgumentException {
		double accumulatedValue = 0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().getWaitedTime(ind.get(i), t);
		}
		return accumulatedValue / numberOfCycles;
	}

	
	public double getWaitedTime(ArrayList<Individual> ind, int from, int to)
			throws OneIndNoPotentialException, IncorrectTimeException,
			IllegalArgumentException {
		double accumulatedValue = 0;
		for(int i = 0; i < cycles.size(); i++){
			accumulatedValue += cycles.get(i).getIndividualStatistic().getWaitedTime(ind.get(i), from, to);
		}
		return accumulatedValue / numberOfCycles;
	}

	// Needed for "Minimale Distanz zum nächsten Ausgang"
	public double minDistanceToNearestExit(ArrayList<ArrayList<Individual>> ind)
			throws GroupOfIndsNoPotentialException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
                            accumulatedValue += cycles.get(i).getIndividualStatistic().minDistanceToNearestExitForGroup(ind.get(i));
                }
		return accumulatedValue / numberOfCycles;
	}

	// Needed for "Minimale Distanz zum initialen Ausgang"
	public double minDistanceToPlannedExit(ArrayList<ArrayList<Individual>> ind)
			throws GroupOfIndsNoPotentialException {
		double accumulatedValue = 0.0;
		for(int i = 0; i < cycles.size(); i++){
                            accumulatedValue += cycles.get(i).getIndividualStatistic().minDistanceToPlannedExitForGroup(ind.get(i));
                }
		return accumulatedValue / numberOfCycles;
	}

	public int getMaxUtilization() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public int getMaxWaiting() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

}
