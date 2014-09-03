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
package statistic.ca;

import java.util.ArrayList;

import statistic.ca.exception.GroupOfIndividualsException;
import statistic.ca.exception.GroupOfIndsNoPotentialException;
import statistic.ca.exception.GroupOfIndsNotSafeException;
import statistic.ca.exception.IncorrectTimeException;
import statistic.ca.exception.OneIndNoPotentialException;
import statistic.ca.exception.OneIndNotSafeException;
import ds.ca.evac.ExitCell;
import ds.ca.evac.Individual;

public interface IndividualStatisticMethods {

        public abstract double getCurrentSpeed(Individual ind, int t) 
                throws OneIndNoPotentialException, IncorrectTimeException, IllegalArgumentException;
   	
        public abstract int getSafetyTime(Individual ind)
			throws OneIndNotSafeException;

	public abstract ArrayList<ExitCell> getPlannedExit(Individual ind, int t)
			throws OneIndNoPotentialException, IllegalArgumentException;
        
        public abstract ArrayList<ExitCell> getTakenExit(Individual ind)
                throws OneIndNoPotentialException, IllegalArgumentException;


	public abstract double getCoveredDistance(Individual ind, int from, int to)
			throws OneIndNoPotentialException, IncorrectTimeException,
			IllegalArgumentException;

	public abstract double getCoveredDistance(Individual ind, int t)
			throws OneIndNoPotentialException, IncorrectTimeException,
			IllegalArgumentException;

	public abstract int getWaitedTime(Individual ind, int t)
			throws OneIndNoPotentialException, IncorrectTimeException,
			IllegalArgumentException;

	public abstract int getWaitedTime(Individual ind, int from, int to)
			throws OneIndNoPotentialException, IncorrectTimeException,
			IllegalArgumentException;

	public abstract double minDistanceToNearestExit(Individual ind)
			throws OneIndNoPotentialException, IllegalArgumentException;

	public abstract double minDistanceToPlannedExit(Individual ind)
			throws OneIndNoPotentialException, IllegalArgumentException;

	public abstract double calculateAverageSpeed(Individual ind, int time)
			throws OneIndNoPotentialException, IncorrectTimeException;

	public abstract double calculateAverageSpeed(Individual ind, int from,
			int to) throws OneIndNoPotentialException, IncorrectTimeException;

	public abstract double calculateMaxSpeed(Individual ind, int from, int to)
			throws OneIndNoPotentialException, IncorrectTimeException;

	public abstract double calculateMaxSpeed(Individual ind, int time)
			throws OneIndNoPotentialException, IncorrectTimeException;

	public abstract double calculateDifferenceNearestAndPlannedExit(
			Individual ind) throws OneIndNoPotentialException;

	public abstract double calculateDifferenceMinAndRealDistanceToPlannedExit(
			Individual ind, int time) throws OneIndNoPotentialException,
			IncorrectTimeException;

	public abstract double calculateAverageAverageSpeedForGroup(
			ArrayList<Individual> indgroup, int time)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException;

	public abstract double calculateAverageAverageSpeedForGroup(
			ArrayList<Individual> indgroup, int from, int to)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException;

	public abstract double calculateAverageMaxSpeedForGroup(
			ArrayList<Individual> indgroup, int time)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException;

	public abstract double calculateAverageMaxSpeedForGroup(
			ArrayList<Individual> indgroup, int from, int to)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException;

	public abstract double calculateMaxMaxSpeedForGroup(
			ArrayList<Individual> indgroup, int time)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException;

	public abstract double calculateMaxMaxSpeedForGroup(
			ArrayList<Individual> indgroup, int from, int to)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException;

	public abstract double calculateMaxAverageSpeedForGroup(
			ArrayList<Individual> indgroup, int time)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException;

	public abstract double calculateMaxAverageSpeedForGroup(
			ArrayList<Individual> indgroup, int from, int to)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException;

	public abstract double calculateMinAverageSpeedForGroup(
			ArrayList<Individual> indgroup, int time)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException;

	public abstract double calculateMinAverageSpeedForGroup(
			ArrayList<Individual> indgroup, int from, int to)
			throws GroupOfIndsNoPotentialException, IncorrectTimeException;

	public abstract double calculateAverageCoveredDistanceForGroup(
			ArrayList<Individual> indgroup, int time)
			throws GroupOfIndividualsException, IncorrectTimeException;

	public abstract double calculateAverageCoveredDistanceForGroup(
			ArrayList<Individual> indgroup, int from, int to)
			throws GroupOfIndividualsException, IncorrectTimeException;

	public abstract int calculateMinWaitedTimeForGroup(
			ArrayList<Individual> indgroup, int from, int to)
			throws GroupOfIndividualsException, IncorrectTimeException;

	public abstract int calculateMinWaitedTimeForGroup(
			ArrayList<Individual> indgroup, int time)
			throws GroupOfIndividualsException, IncorrectTimeException;

	public abstract int calculateMaxWaitedTimeForGroup(
			ArrayList<Individual> indgroup, int from, int to)
			throws GroupOfIndividualsException, IncorrectTimeException;

	public abstract int calculateMaxWaitedTimeForGroup(
			ArrayList<Individual> indgroup, int time)
			throws GroupOfIndividualsException, IncorrectTimeException;

	public abstract double calculateAverageWaitedTimeForGroup(
			ArrayList<Individual> indgroup, int time)
			throws GroupOfIndividualsException, IncorrectTimeException;

	public abstract double calculateAverageWaitedTimeForGroup(
			ArrayList<Individual> indgroup, int from, int to)
			throws GroupOfIndividualsException, IncorrectTimeException;

	public abstract double calculateAverageSafetyTimeForGroup(
			ArrayList<Individual> indgroup) throws GroupOfIndsNotSafeException,
			IncorrectTimeException;

	public abstract int getNumberOfSafeIndividualForGroup(
			ArrayList<Individual> indgroup, int time)
			throws IncorrectTimeException;

	public abstract int calculateMinSafetyTimeForGroup(
			ArrayList<Individual> indgroup) throws GroupOfIndsNotSafeException,
			IncorrectTimeException;

	public abstract int calculateMaxSafetyTimeForGroup(
			ArrayList<Individual> indgroup) throws GroupOfIndsNotSafeException,
			IncorrectTimeException;

	public abstract double calculatePercentageOfSaveIndividuals(
			ArrayList<Individual> indgroup);


    public abstract double getPanicDifference
            (Individual ind, int from, int to) 
            throws OneIndNoPotentialException, IncorrectTimeException,IllegalArgumentException;

    public abstract double getPanic
            (Individual ind, int t)
            throws OneIndNoPotentialException, IncorrectTimeException, IllegalArgumentException;

    public abstract double getExhaustionDifference
            (Individual ind, int from, int to)
            throws OneIndNoPotentialException, IncorrectTimeException,IllegalArgumentException;

    public abstract double getExhaustion
            (Individual ind, int t)
            throws OneIndNoPotentialException, IncorrectTimeException, IllegalArgumentException;

    public abstract double calculateAverageExhaustion
            (Individual ind, int time)
            throws OneIndNoPotentialException, IncorrectTimeException ;

    public abstract double calculateAverageExhaustion 
            (Individual ind, int from, int to) 
            throws OneIndNoPotentialException, IncorrectTimeException ;

    public abstract double calculateAveragePanic
            (Individual ind, int time)
            throws OneIndNoPotentialException, IncorrectTimeException ;

    public abstract double calculateAveragePanic 
            (Individual ind, int from, int to) 
            throws OneIndNoPotentialException, IncorrectTimeException ;

    public abstract double calculateMaxExhaustion
            (Individual ind, int time)
            throws OneIndNoPotentialException, IncorrectTimeException ;

    public abstract double calculateMaxExhaustion 
            (Individual ind, int from, int to) 
            throws OneIndNoPotentialException, IncorrectTimeException ;

    public abstract double calculateMaxPanic
            (Individual ind, int time) 
            throws OneIndNoPotentialException, IncorrectTimeException ;

    public abstract double calculateMaxPanic
            (Individual ind, int from, int to) 
            throws OneIndNoPotentialException, IncorrectTimeException ;

    public abstract double calculateMinExhaustionExceptingStartExhaustion
            (Individual ind, int time) 
            throws OneIndNoPotentialException, IncorrectTimeException ;

    public abstract double calculateMinExhaustionExceptingStartExhaustion 
            (Individual ind, int from, int to) 
            throws OneIndNoPotentialException, IncorrectTimeException ;

    public abstract double calculateMinPanicExceptingStartPanic
            (Individual ind, int time) 
            throws OneIndNoPotentialException, IncorrectTimeException ;

    public abstract double calculateMinPanicExceptingStartPanic
            (Individual ind, int from, int to)
            throws OneIndNoPotentialException, IncorrectTimeException ;

    public abstract double calculateAverageAverageExhaustionForGroup
            (ArrayList<Individual> indgroup, int time)
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateAverageAverageExhaustionForGroup
            (ArrayList<Individual> indgroup, int from, int to)
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateAverageMaxExhaustionForGroup
            (ArrayList<Individual> indgroup, int time) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateAverageMaxExhaustionForGroup
            (ArrayList<Individual> indgroup, int from, int to)
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMaxMaxExhaustionForGroup
            (ArrayList<Individual> indgroup, int time)
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMaxMaxExhaustionForGroup
            (ArrayList<Individual> indgroup, int from, int to) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMaxAverageExhaustionForGroup
            (ArrayList<Individual> indgroup, int time) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMaxAverageExhaustionForGroup
            (ArrayList<Individual> indgroup, int from, int to) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMinAverageExhaustionForGroup
            (ArrayList<Individual> indgroup, int time) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMinAverageExhaustionForGroup
            (ArrayList<Individual> indgroup, int from, int to) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateAverageMinExhaustionForGroup
            (ArrayList<Individual> indgroup, int time) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateAverageMinExhaustionForGroup
            (ArrayList<Individual> indgroup, int from, int to) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMinMinExhaustionForGroup
            (ArrayList<Individual> indgroup, int time) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMinMinExhaustionForGroup
            (ArrayList<Individual> indgroup, int from, int to) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateAverageAveragePanicForGroup
            (ArrayList<Individual> indgroup, int time)
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateAverageAveragePanicForGroup
            (ArrayList<Individual> indgroup, int from, int to)
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateAverageMaxPanicForGroup
            (ArrayList<Individual> indgroup, int time) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateAverageMaxPanicForGroup
            (ArrayList<Individual> indgroup, int from, int to) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMaxMaxPanicForGroup
            (ArrayList<Individual> indgroup, int time) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMaxMaxPanicForGroup(ArrayList<Individual> indgroup, int from, int to) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMaxAveragePanicForGroup
            (ArrayList<Individual> indgroup, int time) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMaxAveragePanicForGroup
            (ArrayList<Individual> indgroup, int from, int to) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMinAveragePanicForGroup
            (ArrayList<Individual> indgroup, int time) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMinAveragePanicForGroup
            (ArrayList<Individual> indgroup, int from, int to) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateAverageMinPanicForGroup
            (ArrayList<Individual> indgroup, int time) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateAverageMinPanicForGroup
            (ArrayList<Individual> indgroup, int from, int to) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMinMinPanicForGroup
            (ArrayList<Individual> indgroup, int time) 
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;

    public abstract double calculateMinMinPanicForGroup
            (ArrayList<Individual> indgroup, int from, int to)
            throws GroupOfIndsNoPotentialException, IncorrectTimeException;
        

}