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

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import ds.ca.evac.Individual;
import ds.ca.evac.ExitCell;
import statistic.ca.results.StoredCAStatisticResultsForIndividuals;
import ds.ca.evac.StaticPotential;
import statistic.ca.exception.GroupOfIndividualsException;
import statistic.ca.exception.GroupOfIndsNoPotentialException;
import statistic.ca.exception.GroupOfIndsNoValueBecauseAlreadySafeException;
import statistic.ca.exception.GroupOfIndsNotSafeException;
import statistic.ca.exception.IncorrectTimeException;
import statistic.ca.exception.MissingStoredValueException;
import statistic.ca.exception.OneIndNoPotentialException;
import statistic.ca.exception.OneIndNoValueBecauseAlreadySafeException;
import statistic.ca.exception.OneIndNotSafeException;

/**
 *
 * @author Sylvie Temme
 */
public class IndividualStatistic {

  HashMap<Individual, Integer> safetyTimes;
  HashMap<Individual, ArrayList<Integer>> changePotentialTimes;
  private HashMap<Individual, ArrayList<ArrayList<ExitCell>>> potentials;
  HashMap<Individual, ArrayList<Integer>> coveredDistanceTimes;
  private HashMap<Individual, ArrayList<Double>> coveredDistance;
  HashMap<Individual, ArrayList<Integer>> waitedTimeTimes;
  private HashMap<Individual, ArrayList<Integer>> waitedTime;
  HashMap<Individual, Double> minDistanceToNearestExit;
  HashMap<Individual, Double> minDistanceToPlannedExit;
  private HashMap<Individual, StaticPotential> takenExit;
  HashMap<Individual, ArrayList<Integer>> panicTimes;
  private HashMap<Individual, ArrayList<Double>> panic;
  HashMap<Individual, ArrayList<Integer>> exhaustionTimes;
  private HashMap<Individual, ArrayList<Double>> exhaustion;
  HashMap<Individual, ArrayList<Integer>> currentSpeedTimes;
  private HashMap<Individual, ArrayList<Double>> currentSpeed;

  public IndividualStatistic( StoredCAStatisticResultsForIndividuals stored ) {
    safetyTimes = stored.getHashMapSafetyTimes();
    changePotentialTimes = stored.getHashMapChangePotentialTimes();
    potentials = stored.getHashMapPotentials();
    coveredDistanceTimes = stored.getHashMapCoveredDistanceTimes();
    coveredDistance = stored.getHashMapCoveredDistance();
    waitedTimeTimes = stored.getHashMapWaitedTimeTimes();
    waitedTime = stored.getHashMapWaitedTime();
    minDistanceToNearestExit = stored.getHashMapMinDistanceToNearestExit();
    minDistanceToPlannedExit = stored.getHashMapMinDistanceToPlannedExit();
    takenExit = stored.getHashMapTakenExit();
    panicTimes = stored.getHashMapPanicTimes();
    panic = stored.getHashMapPanic();
    exhaustionTimes = stored.getHashMapExhaustionTimes();
    exhaustion = stored.getHashMapExhaustion();
    currentSpeedTimes = stored.getHashMapCurrentSpeedTimes();
    currentSpeed = stored.getHashMapCurrentSpeed();
  }

//coveredDistance
  /**
   *
   * @param ind an individual
   * @param t a TimeStep
   * @return the distance which the individual has covered from the beginning of
   * the simulation to TimeStep "t"
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if "t" is less than 0
   */
  public double getCoveredDistance( Individual ind, int t ) throws OneIndNoPotentialException, IncorrectTimeException {

    if( ind.getDeathCause() != null && ind.getDeathCause().compareTo( ds.ca.evac.DeathCause.ExitUnreachable ) == 0 ) {
      // ind tot weil eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }

    if( !coveredDistanceTimes.containsKey( ind ) ) {
      // ind hat sich nie bewegt und ist nie stehengeblieben--> ind ist eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }

    if( t < 0 ) {
      // Zeit ist falsch
      throw new IncorrectTimeException();
    }

    if( t == 0 ) {
      return 0;
    }

    int index = Collections.binarySearch( coveredDistanceTimes.get( ind ), t );
    if( index < 0 ) {
      index = -index - 2;
    }

    if( index < 0 ) {
      // ind hat sich zum Zeitpunkt t noch nicht bewegt
      return 0;
    }

    return coveredDistance.get( ind ).get( index );

  }

  /**
   *
   * @param ind an individual
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the distance which the individual has covered from TimeStep "from"
   * to TimeStep "to"
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if "from" is bigger
   * than "to" OR "from" is less than 0 OR "to" is less than 0
   */
  public double getCoveredDistance( Individual ind, int from, int to ) throws OneIndNoPotentialException, IncorrectTimeException {
    if( from > to ) {
      // Zeiten falschrum
      throw new IncorrectTimeException();
    }
    return getCoveredDistance( ind, to ) - getCoveredDistance( ind, from );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the average of the individual's covered distances from the
   * beginning of the simulation to TimeStep "time" (this value is calculated
   * only over those individuals which have a potential)
   * @see #getCoveredDistance
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no potential
   * @throws statistic.ca.exception.IncorrectTimeException if "time" is less
   * than 0
   */
  public double calculateAverageCoveredDistanceForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateAverageCoveredDistanceForGroup( indgroup, 0, time );

  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the average of the individual's covered distances from the TimeStep
   * "from" to the TimeStep "to" (this value is calculated only over those
   * individuals which have a potential)
   * @see #getCoveredDistance
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no potential
   * @throws statistic.ca.exception.IncorrectTimeException if "from" is bigger
   * than "to" OR "from" is less than 0 OR "to" is less than 0
   */
  public double calculateAverageCoveredDistanceForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double averageCoveredDistanceSum = 0;
    double coveredDistanceOfInd;
    double numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        coveredDistanceOfInd = getCoveredDistance( ind, from, to );
        averageCoveredDistanceSum += coveredDistanceOfInd;
        numberOfInds += 1;
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( numberOfInds != 0 ) {
      return averageCoveredDistanceSum / numberOfInds;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return a list of averages of the individual's covered distances from the
   * TimeStep 0 to the TimeStep ("from"+i), whereas i is the index of the value
   * in the list and i runs from 0 to (to-from) (this values are calculated only
   * over those individuals which have a potential)
   * @see #getCoveredDistance
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no potential
   * @throws statistic.ca.exception.IncorrectTimeException if "from" is bigger
   * than "to" OR "from" is less than 0 OR "to" is less than 0
   */
  public ArrayList<Double> calculateAverageCoveredDistanceForGroupInTimeSteps( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    if( from > to ) {
      throw new IncorrectTimeException();
    }

    ArrayList<Double> result = new ArrayList<Double>();

    double averageTotalCoveredDistance = calculateAverageCoveredDistanceForGroup( indgroup, from );
    result.add( averageTotalCoveredDistance );

    double averageCoveredDistanceInTimeStep = 0;

    for( int timeStep = from + 1; timeStep <= to; timeStep++ ) {
      averageCoveredDistanceInTimeStep = calculateAverageCoveredDistanceForGroup( indgroup, timeStep - 1, timeStep );
      averageTotalCoveredDistance += averageCoveredDistanceInTimeStep;
      result.add( averageTotalCoveredDistance );
    }
    return result;
  }

    // speed
  /**
   *
   * @param ind an individual
   * @param t a TimeStep
   * @return the current speed the individual has in timestep t
   * @throws statistic.ca.exception.OneIndNoValueBecauseAlreadySafeException if
   * "t" is bigger than the individual's safetyTime
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if "t" is less than 0
   * @throws statistic.ca.exception.MissingStoredValueException if the
   * individual had no speedvalue in timestep 1
   */
  public double getCurrentSpeed( Individual ind, int t ) throws OneIndNoValueBecauseAlreadySafeException, OneIndNoPotentialException, IncorrectTimeException, MissingStoredValueException {
    if( ind.isSafe() ) {
      if( t > ind.getSafetyTime() ) {
        throw new OneIndNoValueBecauseAlreadySafeException( ind );
      }
    }

    if( ind.getDeathCause() != null && ind.getDeathCause().compareTo( ds.ca.evac.DeathCause.ExitUnreachable ) == 0 ) {
      // ind tot weil eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }

    if( !currentSpeedTimes.containsKey( ind ) ) {
      // ind hat sich nie bewegt und ist nie stehengeblieben--> ind ist eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }

    if( t < 0 ) {
      // Zeit ist falsch
      throw new IncorrectTimeException();
    }

    if( t == 0 ) {
      return 0;
    }

    int index = Collections.binarySearch( currentSpeedTimes.get( ind ), t );
    if( index < 0 ) {
      index = -index - 2;
    }

    if( index < 0 ) {
      // Fall sollte nicht eintreten
      throw new MissingStoredValueException( "Individual hat zum Zeitpunkt 1 keinen Speed-Eintrag!" );
    }

    return currentSpeed.get( ind ).get( index );

  }

  /**
   *
   * @param ind an individual
   * @param time a TimeStep
   * @return the average speed of the individual from the beginning of the
   * simulation to TimeStep "time" (if the TimeStep "time" is later than the
   * individual's SafetyTime, this time is taken instead of "time")
   * @throws statistic.ca.exception.OneIndNoValueBecauseAlreadySafeException if
   * the individual is already safe in timestep 1
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if "time" is less or
   * equal 0
   * @throws statistic.ca.exception.MissingStoredValueException if the
   * individual had no speedvalue in timestep 1
   */
  public double calculateAverageSpeed( Individual ind, int time ) throws OneIndNoValueBecauseAlreadySafeException, OneIndNoPotentialException, IncorrectTimeException {
    return calculateAverageSpeed( ind, 1, time );
  }

  /**
   *
   * @param ind an individual
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the average speed of the individual from the TimeStep "from" to the
   * TimeStep "to" (if the TimeStep "to" is later than the individual's
   * SafetyTime, this time is taken instead of "to")
   * @throws statistic.ca.exception.OneIndNoValueBecauseAlreadySafeException if
   * the individual is already safe in timestep "from"
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if "from" is less or
   * equal 0 OR "to" is less or equal 0 OR "from" is bigger than the original
   * "to"
   * @throws statistic.ca.exception.MissingStoredValueException if the
   * individual had no speedvalue in timestep 1
   */
  public double calculateAverageSpeed( Individual ind, int from, int to ) throws MissingStoredValueException, OneIndNoValueBecauseAlreadySafeException, OneIndNoPotentialException, IncorrectTimeException {
    if( ind.isSafe() ) {
      if( from >= ind.getSafetyTime() ) {
        throw new OneIndNoValueBecauseAlreadySafeException( ind );
      }
    }

    if( ind.getDeathCause() != null && ind.getDeathCause().compareTo( ds.ca.evac.DeathCause.ExitUnreachable ) == 0 ) {
      // ind tot, weil eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }

    if( !currentSpeedTimes.containsKey( ind ) ) {
      // ind hat sich nie bewegt und ist nie stehengeblieben--> ind ist eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }

    if( from <= 0 || to <= 0 ) {
      throw new IncorrectTimeException();
    }

    if( ind.isSafe() ) {
      int safetyTime = ind.getSafetyTime();
      if( to > safetyTime ) {
        to = safetyTime;
      }
    }
    if( from > to ) {
      throw new IncorrectTimeException();
    }

    if( from == to ) {
      return getCurrentSpeed( ind, from );
    }

    int indexOfFrom = (Collections.binarySearch( currentSpeedTimes.get( ind ), from ));
    if( indexOfFrom < 0 ) {
      indexOfFrom = -indexOfFrom - 2;
    }
    if( indexOfFrom < 0 ) {
      // Fall sollte nicht eintreten
      throw new MissingStoredValueException( "Individual hat zum Zeitpunkt 1 keinen Speed-Eintrag!" );
    }
    int indexOfTo = (Collections.binarySearch( currentSpeedTimes.get( ind ), to ));
    if( indexOfTo < 0 ) {
      indexOfTo = -indexOfTo - 2;
    }
    if( indexOfTo < 0 ) {
      // Fall sollte nicht eintreten
      throw new MissingStoredValueException( "Individual hat zum Zeitpunkt 1 keinen Speed-Eintrag!" );
    }

    if( indexOfFrom == indexOfTo ) {
      return currentSpeed.get( ind ).get( indexOfFrom );
    }

    double lastSpeed = currentSpeed.get( ind ).get( indexOfFrom );
    double weightedSpeedSum = 0;
    int stepTo;
    int stepFrom = from;

    for( int i = indexOfFrom + 1; i < indexOfTo; i++ ) {
      stepTo = currentSpeedTimes.get( ind ).get( i );
      weightedSpeedSum += lastSpeed * (stepTo - stepFrom);
      lastSpeed = currentSpeed.get( ind ).get( i );
      stepFrom = stepTo;
    }
    //last step:
    stepTo = to;
    weightedSpeedSum += lastSpeed * (stepTo - stepFrom);

    return weightedSpeedSum / (to - from);
  }

  /**
   * alter Code: if (ind.isSafe()) { int safetyTime=ind.getSafetyTime(); if
   * (to>safetyTime) { to=safetyTime; } } int time=to-from; if (!(
   * (ind.getDeathCause() != null) &&
   * ind.getDeathCause().compareTo(ds.ca.Individual.DeathCause.ExitUnreachable)==0)
   * ) { if (time>0) { double startCoveredDistance = getCoveredDistance(ind,
   * from); double endCoveredDistance = getCoveredDistance(ind, to); return
   * ((endCoveredDistance-startCoveredDistance)/time); } else { if (time==0) {
   * return 0; } else // time < 0 Zeiten falsch throw new
   * IncorrectTimeException(); } } // Ind tot weil eingeschlossen throw new
   * OneIndNoPotentialException(ind);
       *
   */
  /**
   *
   * @param ind an individual
   * @param time a TimeStep
   * @return the maximum speed of the individual from the beginning of the
   * simulation to TimeStep "time" (if the TimeStep "time" is later than the
   * individual's SafetyTime, this time is taken instead of "time")
   * @throws statistic.ca.exception.OneIndNoValueBecauseAlreadySafeException if
   * the individual is already safe in timestep 1
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if "time" is less or
   * equal 0
   * @throws statistic.ca.exception.MissingStoredValueException if the
   * individual had no speedvalue in timestep 1
   */
  public double calculateMaxSpeed( Individual ind, int time ) throws MissingStoredValueException, OneIndNoValueBecauseAlreadySafeException, OneIndNoPotentialException, IncorrectTimeException {
    return calculateMaxSpeed( ind, 1, time );
  }

  /**
   *
   * @param ind an individual
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the maximum speed of the individual from the TimeStep "from" to the
   * TimeStep "to" (if the TimeStep "to" is later than the individual's
   * SafetyTime, this time is taken instead of "to")
   * @throws statistic.ca.exception.OneIndNoValueBecauseAlreadySafeException if
   * the individual is already safe in timestep "from"
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if "from" is less or
   * equal 0 OR "to" is less or equal 0 OR "from" is bigger than the original
   * "to"
   * @throws statistic.ca.exception.MissingStoredValueException if the
   * individual had no speedvalue in timestep 1
   */
  public double calculateMaxSpeed( Individual ind, int from, int to ) throws MissingStoredValueException, OneIndNoValueBecauseAlreadySafeException, OneIndNoPotentialException, IncorrectTimeException {

    if( ind.isSafe() ) {
      if( from >= ind.getSafetyTime() ) {
        throw new OneIndNoValueBecauseAlreadySafeException( ind );
      }
    }

    if( ind.getDeathCause() != null && ind.getDeathCause().compareTo( ds.ca.evac.DeathCause.ExitUnreachable ) == 0 ) {
      // ind tot, weil eingeschlossen

      throw new OneIndNoPotentialException( ind );
    }

    if( !currentSpeedTimes.containsKey( ind ) ) {
      // ind hat sich nie bewegt und ist nie stehengeblieben--> ind ist eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }

    if( from <= 0 || to <= 0 ) {
      throw new IncorrectTimeException();
    }

    if( ind.isSafe() ) {
      int safetyTime = ind.getSafetyTime();
      if( to > safetyTime ) {
        to = safetyTime;
      }
    }
    if( from > to ) {
      throw new IncorrectTimeException();
    }

    if( from == to ) {
      return getCurrentSpeed( ind, from );
    }

    int indexOfFrom = (Collections.binarySearch( currentSpeedTimes.get( ind ), from ));
    if( indexOfFrom < 0 ) {
      indexOfFrom = -indexOfFrom - 2;
    }
    if( indexOfFrom < 0 ) {
      // Fall sollte nicht eintreten
      throw new MissingStoredValueException( "Individual hat zum Zeitpunkt 1 keinen Speed-Eintrag!" );
    }

    int indexOfTo = (Collections.binarySearch( currentSpeedTimes.get( ind ), to ));
    if( indexOfTo < 0 ) {
      indexOfTo = -indexOfTo - 2;
    }
    if( indexOfTo < 0 ) {
      // Fall sollte nicht eintreten
      throw new MissingStoredValueException( "Individual hat zum Zeitpunkt 1 keinen Speed-Eintrag!" );
    }

    if( indexOfFrom == indexOfTo ) {
      return currentSpeed.get( ind ).get( indexOfFrom );
    }
    double maxSpeed = 0;
    double speed;

    for( int i = indexOfFrom; i < indexOfTo; i++ ) {
      speed = currentSpeed.get( ind ).get( i );
      if( speed > maxSpeed ) {
        maxSpeed = speed;
      }
    }

    return maxSpeed;
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the average of the individual's average-speeds from the beginning
   * of the simulation to TimeStep "time" (this value is calculated only over
   * those individuals which have a potential and are not safe in timestep from)
   * @return 0 if all individuals have no static potential (there`s no exit
   * reachable from the individual's positions)
   * @see #calculateAverageSpeed
   * @throws statistic.ca.exception.IncorrectTimeException if "time" is less or
   * equal 0
   * @throws statistic.ca.exception.MissingStoredValueException if at least one
   * individual had no speedvalue in timestep 1
   * @throws
   * statistic.ca.exception.GroupOfIndsNoValueBecauseAlreadySafeException if at
   * least one individual is already safe in timestep 1 AND all other
   * individuals have no static potential or are already safe in timestep 1
   */
  public double calculateAverageAverageSpeedForGroup( ArrayList<Individual> indgroup, int time ) throws MissingStoredValueException, GroupOfIndsNoValueBecauseAlreadySafeException, IncorrectTimeException {
    return calculateAverageAverageSpeedForGroup( indgroup, 1, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the average of the individual's average-speeds from the TimeStep
   * "from" to the TimeStep "to" (this value is calculated only over those
   * individuals which have a potential and are not safe in timestep from)
   * @return 0 if all individuals have no static potential (there`s no exit
   * reachable from the individual's positions)
   * @see #calculateAverageSpeed
   * @throws statistic.ca.exception.IncorrectTimeException if "from" is less or
   * equal 0 OR "to" is less or equal 0 OR "from" is bigger than the original
   * "to"
   * @throws statistic.ca.exception.MissingStoredValueException if at least one
   * individual had no speedvalue in timestep 1
   * @throws
   * statistic.ca.exception.GroupOfIndsNoValueBecauseAlreadySafeException if at
   * least one individual is already safe in timestep "from" AND all other
   * individuals have no static potential or are already safe in timestep from
   */
  public double calculateAverageAverageSpeedForGroup( ArrayList<Individual> indgroup, int from, int to ) throws MissingStoredValueException, GroupOfIndsNoValueBecauseAlreadySafeException, IncorrectTimeException {

    int noOfNoPotExc = 0;
    double averageSpeedSum = 0;
    double averageSpeedOfInd;
    double numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        averageSpeedOfInd = calculateAverageSpeed( ind, from, to );
        averageSpeedSum += averageSpeedOfInd;
        numberOfInds += 1;
      } catch( OneIndNoPotentialException ex ) {
        noOfNoPotExc++;
      } catch( OneIndNoValueBecauseAlreadySafeException e ) {
      }
    }

    if( numberOfInds != 0 ) {
      return averageSpeedSum / numberOfInds;
    } else {
      if( noOfNoPotExc == indgroup.size() ) {
        // alle inds haben sich nicht bewegt (weil sie tot oder safe waren)
        return 0;
      } else {
        //mind ein ind ist nicht eingeschlossen, liefert aber keinen Wert
        throw new GroupOfIndsNoValueBecauseAlreadySafeException( indgroup );
      }
    }

  }

  /**
   *
   * @param indgroup a list of individuals
   * @param timestep a TimeStep
   * @return the average of the individual's speeds in timeStep "timestep" (this
   * value is calculated only over those individuals which have a potential and
   * are not safe in timestep "timestep")
   * @return 0 if all individuals have no static potential (there`s no exit
   * reachable from the individual's positions)
   * @see #getCurrentSpeed
   * @throws statistic.ca.exception.IncorrectTimeException if "timestep" is less
   * than 0
   * @throws statistic.ca.exception.MissingStoredValueException if at least one
   * individual had no speedvalue in timestep 1
   * @throws
   * statistic.ca.exception.GroupOfIndsNoValueBecauseAlreadySafeException if at
   * least one individual is already safe in timestep "timestep" AND all other
   * individuals have no static potential or are already safe in timestep
   * "timestep"
   */
  public double calculateAverageSpeedForGroupInOneTimestep( ArrayList<Individual> indgroup, int timestep ) throws MissingStoredValueException, GroupOfIndsNoValueBecauseAlreadySafeException, IncorrectTimeException {
    int noOfNoPotExc = 0;
    double averageSpeedSum = 0;
    double speedOfInd;
    double numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        speedOfInd = getCurrentSpeed( ind, timestep );
        averageSpeedSum += speedOfInd;
        numberOfInds += 1;
      } catch( OneIndNoPotentialException ex ) {
        noOfNoPotExc++;
      } catch( OneIndNoValueBecauseAlreadySafeException e ) {
      }
    }

    if( numberOfInds != 0 ) {
      return averageSpeedSum / numberOfInds;
    } else {
      if( noOfNoPotExc == indgroup.size() ) {
        // alle inds haben sich nicht bewegt (weil sie tot oder safe waren)
        return 0;
      } else {
        //mind ein ind ist nicht eingeschlossen, liefert aber keinen Wert
        throw new GroupOfIndsNoValueBecauseAlreadySafeException( indgroup );
      }
    }

  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the average of the individual's maximum-speeds from the beginning
   * of the simulation to TimeStep "time" (this value is calculated only over
   * those individuals which have a potential and are not safe in timestep from)
   * @return 0 if all individuals have no static potential (there`s no exit
   * reachable from the individual's positions)
   * @see #calculateMaxSpeed
   * @throws statistic.ca.exception.IncorrectTimeException if "time" is less or
   * equal 0
   * @throws statistic.ca.exception.MissingStoredValueException if at least one
   * individual had no speedvalue in timestep 1
   * @throws
   * statistic.ca.exception.GroupOfIndsNoValueBecauseAlreadySafeException if at
   * least one individual is already safe in timestep 1 AND all other
   * individuals have no static potential or are already safe in timestep 1
   */
  public double calculateAverageMaxSpeedForGroup( ArrayList<Individual> indgroup, int time ) throws IncorrectTimeException, MissingStoredValueException, GroupOfIndsNoValueBecauseAlreadySafeException {
    return calculateAverageMaxSpeedForGroup( indgroup, 1, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the average of the individual's maximum-speeds from the TimeStep
   * "from" to the TimeStep "to" (this value is calculated only over those
   * individuals which have a potential and are not safe in timestep from)
   * @return 0 if all individuals have no static potential (there`s no exit
   * reachable from the individual's positions)
   * @see #calculateMaxSpeed
   * @throws statistic.ca.exception.IncorrectTimeException if "from" is less or
   * equal 0 OR "to" is less or equal 0 OR "from" is bigger than the original
   * "to"
   * @throws statistic.ca.exception.MissingStoredValueException if at least one
   * individual had no speedvalue in timestep 1
   * @throws
   * statistic.ca.exception.GroupOfIndsNoValueBecauseAlreadySafeException if at
   * least one individual is already safe in timestep "from" AND all other
   * individuals have no static potential or are already safe in timestep from
   */
  public double calculateAverageMaxSpeedForGroup( ArrayList<Individual> indgroup, int from, int to ) throws IncorrectTimeException, MissingStoredValueException, GroupOfIndsNoValueBecauseAlreadySafeException {
    int noOfNoPotExc = 0;
    double maxSpeedSum = 0;
    double maxSpeedOfInd;
    double numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        maxSpeedOfInd = calculateMaxSpeed( ind, from, to );
        maxSpeedSum += maxSpeedOfInd;
        numberOfInds += 1;
      } catch( OneIndNoPotentialException ex ) {
        noOfNoPotExc++;
      } catch( OneIndNoValueBecauseAlreadySafeException ex ) {
      }
    }
    if( numberOfInds != 0 ) {
      return maxSpeedSum / numberOfInds;
    } else {
      if( noOfNoPotExc == indgroup.size() ) {
        // alle inds haben sich nicht bewegt (weil sie tot oder safe waren)
        return 0;
      } else {
        //mind ein ind ist nicht eingeschlossen, liefert aber keinen Wert
        throw new GroupOfIndsNoValueBecauseAlreadySafeException( indgroup );
      }
    }

  }

  /**
   *
   * @param indgroup a list of individuals
   * @param timestep a TimeStep
   * @return the maximum of the individual's speeds in timeStep "timestep" (this
   * value is calculated only over those individuals which have a potential and
   * are not safe in timestep "timestep")
   * @return 0 if all individuals have no static potential (there`s no exit
   * reachable from the individual's positions)
   * @see #getCurrentSpeed
   * @throws statistic.ca.exception.IncorrectTimeException if "timestep" is less
   * than 0
   * @throws statistic.ca.exception.MissingStoredValueException if at least one
   * individual had no speedvalue in timestep 1
   * @throws
   * statistic.ca.exception.GroupOfIndsNoValueBecauseAlreadySafeException if at
   * least one individual is already safe in timestep "timestep" AND all other
   * individuals have no static potential or are already safe in timestep
   * "timestep"
   */
  public double calculateMaxSpeedForGroupInOneTimestep( ArrayList<Individual> indgroup, int timestep ) throws MissingStoredValueException, GroupOfIndsNoValueBecauseAlreadySafeException, IncorrectTimeException {
    int noOfNoPotExc = 0;
    double maxSpeed = 0;
    double speedOfInd;
    double numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        speedOfInd = getCurrentSpeed( ind, timestep );
        numberOfInds++;
        if( speedOfInd > maxSpeed ) {
          maxSpeed = speedOfInd;
        }
      } catch( OneIndNoPotentialException ex ) {
        noOfNoPotExc++;
      } catch( OneIndNoValueBecauseAlreadySafeException e ) {
      }
    }

    if( numberOfInds != 0 ) {
      return maxSpeed;
    } else {
      if( noOfNoPotExc == indgroup.size() ) {
        // alle inds haben sich nicht bewegt (weil sie tot oder safe waren)
        return 0;
      } else {
        //mind ein ind ist nicht eingeschlossen, liefert aber keinen Wert
        throw new GroupOfIndsNoValueBecauseAlreadySafeException( indgroup );
      }
    }

  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the maximum of the individual's maximum-speeds from the beginning
   * of the simulation to TimeStep "time" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateMaxSpeed
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxMaxSpeedForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateMaxMaxSpeedForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the maximum of the individual's maximum-speeds from the TimeStep
   * "from" to the TimeStep "to" (this value is calculated only over those
   * individuals which have a potential)
   * @see #calculateMaxSpeed
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxMaxSpeedForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double maxSpeed = 0;
    double maxSpeedOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        maxSpeedOfInd = calculateMaxSpeed( ind, from, to );
        nrOfInds++;
        if( maxSpeedOfInd > maxSpeed ) {
          maxSpeed = maxSpeedOfInd;
        }
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return maxSpeed;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the maximum of the individual's average-speeds from the beginning
   * of the simulation to TimeStep "time" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateAverageSpeed
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxAverageSpeedForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateMaxAverageSpeedForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the maximum of the individual's average-speeds from the TimeStep
   * "from" to the TimeStep "to" (this value is calculated only over those
   * individuals which have a potential)
   * @see #calculateAverageSpeed
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxAverageSpeedForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double maxSpeed = 0;
    double averageSpeedOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        averageSpeedOfInd = calculateAverageSpeed( ind, from, to );
        nrOfInds++;
        if( averageSpeedOfInd > maxSpeed ) {
          maxSpeed = averageSpeedOfInd;
        }
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return maxSpeed;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the minimum of the individual's average-speeds from the beginning
   * of the simulation to TimeStep "time" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateAverageSpeed
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMinAverageSpeedForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateMinAverageSpeedForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the minimum of the individual's average-speeds from the TimeStep
   * "from" to the TimeStep "to" (this value is calculated only over those
   * individuals which have a potential)
   * @see #calculateAverageSpeed
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMinAverageSpeedForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double minSpeed = Double.MAX_VALUE;
    double averageSpeedOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        averageSpeedOfInd = calculateAverageSpeed( ind, from, to );
        nrOfInds++;
        if( averageSpeedOfInd < minSpeed ) {
          minSpeed = averageSpeedOfInd;
        }
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return minSpeed;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

    //exhaustion
  /**
   *
   * @param ind an individual
   * @param t a TimeStep
   * @return the individuals exhaustion in TimeStep "t"
   * @return 0 if the first stored value for the individual lies after the
   * timestep "t"
   * @throws statistic.ca.exception.OneIndNoValueBecauseAlreadySafeException if
   * "t" is bigger than the individual's safetyTime
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if "t" is less than 0
     *
   */
  public double getExhaustion( Individual ind, int t ) throws OneIndNoValueBecauseAlreadySafeException, OneIndNoPotentialException, IncorrectTimeException {
    if( ind.isSafe() ) {
      if( t > ind.getSafetyTime() ) {
        throw new OneIndNoValueBecauseAlreadySafeException( ind );
      }
    }

    if( ind.getDeathCause() != null && ind.getDeathCause().compareTo( ds.ca.evac.DeathCause.ExitUnreachable ) == 0 ) {
      // ind tot weil eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }

    if( !exhaustionTimes.containsKey( ind ) ) {
      // ind hat sich nie bewegt und ist nie stehengeblieben--> ind ist eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }

    if( t < 0 ) {
      // Zeit ist falsch
      throw new IncorrectTimeException();
    }

    if( t == 0 ) {
      return 0;
    }

    int index = Collections.binarySearch( exhaustionTimes.get( ind ), t );
    if( index < 0 ) {
      index = -index - 2;
    }

    if( index < 0 ) {
      return 0;
    }

    return exhaustion.get( ind ).get( index );

  }

  /**
   *
   * @param ind an individual
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the difference between the individual's exhaustion in TimeStep "to"
   * and in TimeStep "from"
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   * @throws java.lang.IllegalArgumentException
   */
  public double getExhaustionDifference( Individual ind, int from, int to ) throws OneIndNoPotentialException, IncorrectTimeException, IllegalArgumentException {
    if( to - from >= 0 ) {
      return getExhaustion( ind, to ) - getExhaustion( ind, from );
    }
    // Zeiten falschrum
    throw new IncorrectTimeException();
  }

  /**
   *
   * @param ind an individual
   * @param time a TimeStep
   * @return the average exhaustion of the individual from the beginning of the
   * simulation to TimeStep "time" (if the TimeStep "to" is later than the
   * individual's SafetyTime, this time is taken instead of "to")
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateAverageExhaustion( Individual ind, int time ) throws OneIndNoPotentialException, IncorrectTimeException {
    return calculateAverageExhaustion( ind, 1, time );
  }

  /**
   *
   * @param ind an individual
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the average exhaustion of the individual from the TimeStep "from"
   * to the TimeStep "to" (if the TimeStep "to" is later than the individual's
   * SafetyTime, this time is taken instead of "to")
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 OR are in wrong order OR are both greater
   * than the individual's safetyTime
   */
  public double calculateAverageExhaustion( Individual ind, int from, int to ) throws OneIndNoPotentialException, IncorrectTimeException {
    if( ind.isSafe() ) {
      int safetyTime = ind.getSafetyTime();
      if( to > safetyTime ) {
        to = safetyTime;
      }
      if( from > safetyTime ) {
        throw new IncorrectTimeException();
      }
    }

    if( from < to ) {
      if( !(ind.getDeathCause() != null && ind.getDeathCause().compareTo( ds.ca.evac.DeathCause.ExitUnreachable ) == 0) ) {
        if( exhaustionTimes.containsKey( ind ) ) {
          if( from == 0 ) {
            from = 1;
          }
          int indexOfFrom = (Collections.binarySearch( exhaustionTimes.get( ind ), from ));
          if( indexOfFrom < 0 ) {
            indexOfFrom = -indexOfFrom - 2;
          }
          if( indexOfFrom < 0 ) {
            throw new IncorrectTimeException();
          }

          int indexOfTo = (Collections.binarySearch( exhaustionTimes.get( ind ), to ));
          if( indexOfTo < 0 ) {
            indexOfTo = -indexOfTo - 2;
          }
          if( indexOfTo < 0 ) {
            throw new IncorrectTimeException();
          }

          double lastExhaustion = exhaustion.get( ind ).get( indexOfFrom );
          double weightedExhaustionSum = 0;
          int stepTo;
          int stepFrom = from;

          if( indexOfFrom == indexOfTo ) {
            return currentSpeed.get( ind ).get( indexOfFrom );
          }

          for( int i = indexOfFrom + 1; i < indexOfTo; i++ ) {
            stepTo = exhaustionTimes.get( ind ).get( i );
            weightedExhaustionSum += lastExhaustion * (stepTo - stepFrom);
            lastExhaustion = exhaustion.get( ind ).get( i );
            stepFrom = stepTo;
          }
          //last step:
          stepTo = to;
          weightedExhaustionSum += lastExhaustion * (stepTo - stepFrom);

          return weightedExhaustionSum / (to - from);
        }
        //duerfte nicht vorkommen
        throw new IllegalArgumentException();
      }
      // ind tot, weil eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }
    if( from == to ) {
      return getExhaustion( ind, from );
    } else // Zeiten falsch
    {
      throw new IncorrectTimeException();
    }
  }

  /**
   *
   * @param ind an individual
   * @param time a TimeStep
   * @return the maximum exhaustion of the individual from the beginning of the
   * simulation to TimeStep "time" (if the TimeStep "to" is later than the
   * individual's SafetyTime, this time is taken instead of "to")
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxExhaustion( Individual ind, int time ) throws OneIndNoPotentialException, IncorrectTimeException {
    return calculateMaxExhaustion( ind, 0, time );
  }

  /**
   *
   * @param ind an individual
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the maximum exhaustion of the individual from the TimeStep "from"
   * to the TimeStep "to" (if the TimeStep "to" is later than the individual's
   * SafetyTime, this time is taken instead of "to")
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxExhaustion( Individual ind, int from, int to ) throws OneIndNoPotentialException, IncorrectTimeException {
    if( from < to ) {
      if( !(ind.getDeathCause() != null && ind.getDeathCause().compareTo( ds.ca.evac.DeathCause.ExitUnreachable ) == 0) ) {
        if( exhaustionTimes.containsKey( ind ) ) {
          if( ind.isSafe() ) {
            int safetyTime = ind.getSafetyTime();
            if( to > safetyTime ) {
              to = safetyTime;
            }
          }
          int indexOfFrom = (Collections.binarySearch( exhaustionTimes.get( ind ), from ));
          if( indexOfFrom < 0 ) {
            indexOfFrom = -indexOfFrom - 2;
          }
          if( indexOfFrom < 0 ) {
            throw new IllegalArgumentException();
          }

          int indexOfTo = (Collections.binarySearch( exhaustionTimes.get( ind ), to ));
          if( indexOfTo < 0 ) {
            indexOfTo = -indexOfTo - 2;
          }
          if( indexOfTo < 0 ) {
            throw new IllegalArgumentException();
          }

          double actualExhaustion;
          double maxExhaustion = 0;

          for( int i = indexOfFrom; i <= indexOfTo; i++ ) {
            actualExhaustion = exhaustion.get( ind ).get( i );
            if( actualExhaustion > maxExhaustion ) {
              maxExhaustion = actualExhaustion;
            }
          }
          return maxExhaustion;
        }
        //duerfte nicht vorkommen
        throw new IllegalArgumentException();
      }
      // ind tot, weil eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }
    if( from == to ) {
      return getExhaustion( ind, from );
    } else // Zeiten falsch
    {
      throw new IncorrectTimeException();
    }
  }

  /**
   *
   * @param ind an individual
   * @param time a TimeStep
   * @return the minimum exhaustion the individual had from the beginning of the
   * simulation to TimeStep "time" except for the exhaustion at the beginning of
   * the simulation (if the individual never had a higher exhaustion than that
   * at the beginning of the simulation, this value is returned) (if the
   * TimeStep "to" is later than the individual's SafetyTime, this time is taken
   * instead of "to")
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMinExhaustionExceptingStartExhaustion( Individual ind, int time ) throws OneIndNoPotentialException, IncorrectTimeException {
    return calculateMinExhaustionExceptingStartExhaustion( ind, 0, time );
  }

  /**
   *
   * @param ind an individual
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the minimum exhaustion the individual had from the TimeStep "from"
   * to the TimeStep "to" except for the exhaustion at the beginning of the
   * simulation (if the individual never had a higher exhaustion than that at
   * the beginning of the simulation, this value is returned) (if the TimeStep
   * "to" is later than the individual's SafetyTime, this time is taken instead
   * of "to")
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMinExhaustionExceptingStartExhaustion( Individual ind, int from, int to ) throws OneIndNoPotentialException, IncorrectTimeException {
    if( from < to ) {
      if( !(ind.getDeathCause() != null && ind.getDeathCause().compareTo( ds.ca.evac.DeathCause.ExitUnreachable ) == 0) ) {
        if( exhaustionTimes.containsKey( ind ) ) {
          if( ind.isSafe() ) {
            int safetyTime = ind.getSafetyTime();
            if( to > safetyTime ) {
              to = safetyTime;
            }
          }
          if( from == 0 ) {
            try {
              from = exhaustionTimes.get( ind ).get( 1 );
            } catch( Exception e ) {
              return exhaustion.get( ind ).get( 0 );
            }
            if( to < from ) {
              return exhaustion.get( ind ).get( 0 );
            }
          }
          int indexOfFrom = (Collections.binarySearch( exhaustionTimes.get( ind ), from ));
          if( indexOfFrom < 0 ) {
            indexOfFrom = -indexOfFrom - 2;
          }
          if( indexOfFrom < 0 ) {
            throw new IllegalArgumentException();
          }

          int indexOfTo = (Collections.binarySearch( exhaustionTimes.get( ind ), to ));
          if( indexOfTo < 0 ) {
            indexOfTo = -indexOfTo - 2;
          }
          if( indexOfTo < 0 ) {
            throw new IllegalArgumentException();
          }

          double actualExhaustion;
          double minExhaustion = Double.MAX_VALUE;

          for( int i = indexOfFrom; i <= indexOfTo; i++ ) {
            actualExhaustion = exhaustion.get( ind ).get( i );
            if( actualExhaustion < minExhaustion ) {
              minExhaustion = actualExhaustion;
            }
          }
          return minExhaustion;
        }
        //duerfte nicht vorkommen
        throw new IllegalArgumentException();
      }
      // ind tot, weil eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }
    if( from == to ) {
      return getExhaustion( ind, from );
    } else // Zeiten falsch
    {
      throw new IncorrectTimeException();
    }
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param t a TimeStep
   * @return the average of the individual's exhasutionvalues in TimeStep "t"
   * (this value is calculated only over those individuals which have a
   * potential and are not safe in timestep "timestep")
   * @return 0 if all individuals have no static potential (there`s no exit
   * reachable from the individual's positions) OR if all individual's first
   * stored values lie after the timestep "t"
   * @see #getExhaustion
   * @throws statistic.ca.exception.IncorrectTimeException if "timestep" is less
   * than 0
   * @throws statistic.ca.exception.MissingStoredValueException if at least one
   * individual had no speedvalue in timestep 1
   * @throws
   * statistic.ca.exception.GroupOfIndsNoValueBecauseAlreadySafeException if at
   * least one individual is already safe in timestep "timestep" AND all other
   * individuals have no static potential or are already safe in timestep
   * "timestep"
   */
  public double getExhaustionForGroup( ArrayList<Individual> indgroup, int t ) throws GroupOfIndsNoPotentialException, IncorrectTimeException, IllegalArgumentException {
    int noOfNoPotExc = 0;
    double exhaustionSum = 0;
    double speedOfInd;
    double numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        exhaustionSum += getExhaustion( ind, t );
        numberOfInds++;
      } catch( OneIndNoPotentialException ex ) {
        noOfNoPotExc++;
      } catch( OneIndNoValueBecauseAlreadySafeException e ) {
      }
    }

    if( numberOfInds != 0 ) {
      return exhaustionSum / numberOfInds;
    } else {
      if( noOfNoPotExc == indgroup.size() ) {
        // alle inds haben sich nicht bewegt (weil sie tot oder safe waren)
        return 0;
      } else {
        //mind ein ind ist nicht eingeschlossen, liefert aber keinen Wert
        throw new GroupOfIndsNoValueBecauseAlreadySafeException( indgroup );
      }
    }

  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the average of the individual's average-exhaustions from the
   * beginning of the simulation to TimeStep "time" (this value is calculated
   * only over those individuals which have a potential)
   * @see #calculateAverageExhaustion
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateAverageAverageExhaustionForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateAverageAverageExhaustionForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the average of the individual's average-exhaustions from the
   * TimeStep "from" to the TimeStep "to" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateAverageExhaustion
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateAverageAverageExhaustionForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double averageExhaustionSum = 0;
    double averageExhaustionOfInd;
    int numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        averageExhaustionOfInd = calculateAverageExhaustion( ind, from, to );
        averageExhaustionSum += averageExhaustionOfInd;
        numberOfInds += 1;
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( numberOfInds != 0 ) {
      return averageExhaustionSum / numberOfInds;
    } else // alle ind tot weil eingeschlossen
    {
      throw new GroupOfIndsNoPotentialException( indgroup );
    }
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the average of the individual's maximum-exhaustions from the
   * beginning of the simulation to TimeStep "time" (this value is calculated
   * only over those individuals which have a potential)
   * @see #calculateMaxExhaustion
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateAverageMaxExhaustionForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateAverageMaxExhaustionForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the average of the individual's maximum-exhaustions from the
   * TimeStep "from" to the TimeStep "to" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateMaxExhaustion
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateAverageMaxExhaustionForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double maxExhaustionSum = 0;
    double maxExhaustionOfInd;
    int numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        maxExhaustionOfInd = calculateMaxExhaustion( ind, from, to );
        maxExhaustionSum += maxExhaustionOfInd;
        numberOfInds += 1;
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( numberOfInds != 0 ) {
      return maxExhaustionSum / numberOfInds;
    } else {
      throw new GroupOfIndsNoPotentialException( indgroup );
    }
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the average of the individual's minimum-exhaustions from the
   * beginning of the simulation to TimeStep "time" (this value is calculated
   * only over those individuals which have a potential)
   * @see #calculateMinExhaustionExceptingStartExhaustion
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateAverageMinExhaustionForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateAverageMinExhaustionForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the average of the individual's minimum-exhaustions from the
   * TimeStep "from" to the TimeStep "to" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateMinExhaustionExceptingStartExhaustion
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateAverageMinExhaustionForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double minExhaustionSum = 0;
    double minExhaustionOfInd;
    int numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        minExhaustionOfInd = calculateMinExhaustionExceptingStartExhaustion( ind, from, to );
        minExhaustionSum += minExhaustionOfInd;
        numberOfInds += 1;
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( numberOfInds != 0 ) {
      return minExhaustionSum / numberOfInds;
    } else {
      throw new GroupOfIndsNoPotentialException( indgroup );
    }
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the maximum of the individual's average-exhaustions from the
   * beginning of the simulation to TimeStep "time" (this value is calculated
   * only over those individuals which have a potential)
   * @see #calculateAverageExhaustion
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxAverageExhaustionForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateMaxAverageExhaustionForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the maximum of the individual's average-exhaustions from the
   * TimeStep "from" to the TimeStep "to" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateAverageExhaustion
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException
   */
  public double calculateMaxAverageExhaustionForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double maxExhaustion = 0;
    double averageExhaustionOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        averageExhaustionOfInd = calculateAverageExhaustion( ind, from, to );
        nrOfInds++;
        if( averageExhaustionOfInd > maxExhaustion ) {
          maxExhaustion = averageExhaustionOfInd;
        }
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return maxExhaustion;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the maximum of the individual's maximum-exhaustions from the
   * beginning of the simulation to TimeStep "time" (this value is calculated
   * only over those individuals which have a potential)
   * @see #calculateMaxExhaustion
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxMaxExhaustionForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateMaxMaxExhaustionForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the maximum of the individual's maximum-exhaustions from the
   * TimeStep "from" to the TimeStep "to" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateMaxExhaustion
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxMaxExhaustionForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double maxExhaustion = 0;
    double maxExhaustionOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        maxExhaustionOfInd = calculateMaxExhaustion( ind, from, to );
        nrOfInds++;
        if( maxExhaustionOfInd > maxExhaustion ) {
          maxExhaustion = maxExhaustionOfInd;
        }
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return maxExhaustion;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the minimum of the individual's average-exhaustions from the
   * beginning of the simulation to TimeStep "time" (this value is calculated
   * only over those individuals which have a potential)
   * @see #calculateAverageExhaustion
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMinAverageExhaustionForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateMinAverageExhaustionForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the minimum of the individual's average-exhaustions from the
   * TimeStep "from" to the TimeStep "to" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateAverageExhaustion
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMinAverageExhaustionForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double minExhaustion = Double.MAX_VALUE;
    double averageExhaustionOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        averageExhaustionOfInd = calculateAverageExhaustion( ind, from, to );
        nrOfInds++;
        if( averageExhaustionOfInd < minExhaustion ) {
          minExhaustion = averageExhaustionOfInd;
        }
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return minExhaustion;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the minimum of the individual's minimum-exhaustions from the
   * beginning of the simulation to TimeStep "time" (this value is calculated
   * only over those individuals which have a potential)
   * @see #calculateMinExhaustionExceptingStartExhaustion
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMinMinExhaustionForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateMinMinExhaustionForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the minimum of the individual's minimum-exhaustions from the
   * TimeStep "from" to the TimeStep "to" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateMinExhaustionExceptingStartExhaustion
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMinMinExhaustionForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double minExhaustion = 0;
    double minExhaustionOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        minExhaustionOfInd = calculateMinExhaustionExceptingStartExhaustion( ind, from, to );
        nrOfInds++;
        if( minExhaustionOfInd > minExhaustion ) {
          minExhaustion = minExhaustionOfInd;
        }
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return minExhaustion;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

    //panic
  /**
   *
   * @param ind an individual
   * @param t a TimeStep
   * @return the individuals panic in TimeStep "t"
   * @return 0 if the first stored value for the individual lies after the
   * timestep "t"
   * @throws statistic.ca.exception.OneIndNoValueBecauseAlreadySafeException if
   * "t" is bigger than the individual's safetyTime
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if "t" is less than 0
     *
   */
  public double getPanic( Individual ind, int t ) throws OneIndNoValueBecauseAlreadySafeException, OneIndNoPotentialException, IncorrectTimeException {
    if( ind.isSafe() ) {
      if( t > ind.getSafetyTime() ) {
        throw new OneIndNoValueBecauseAlreadySafeException( ind );
      }
    }

    if( ind.getDeathCause() != null && ind.getDeathCause().compareTo( ds.ca.evac.DeathCause.ExitUnreachable ) == 0 ) {
      // ind tot weil eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }

    if( !panicTimes.containsKey( ind ) ) {
      // ind hat sich nie bewegt und ist nie stehengeblieben--> ind ist eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }

    if( t < 0 ) {
      // Zeit ist falsch
      throw new IncorrectTimeException();
    }

    if( t == 0 ) {
      return 0;
    }

    int index = Collections.binarySearch( panicTimes.get( ind ), t );
    if( index < 0 ) {
      index = -index - 2;
    }

    if( index < 0 ) {
      return 0;
    }

    return panic.get( ind ).get( index );

  }

  /**
   *
   * @param ind an individual
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the difference between the individual's panic in TimeStep "to" and
   * in TimeStep "from"
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   * @throws java.lang.IllegalArgumentException
   */
  public double getPanicDifference( Individual ind, int from, int to ) throws OneIndNoPotentialException, IncorrectTimeException, IllegalArgumentException {
    if( to - from >= 0 ) {
      return getPanic( ind, to ) - getPanic( ind, from );
    }
    // Zeiten falschrum
    throw new IncorrectTimeException();
  }

  /**
   *
   * @param ind an individual
   * @param time a TimeStep
   * @return the average panic of the individual from the beginning of the
   * simulation to TimeStep "time" (if the TimeStep "to" is later than the
   * individual's SafetyTime, this time is taken instead of "to")
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateAveragePanic( Individual ind, int time ) throws OneIndNoPotentialException, IncorrectTimeException {
    return calculateAveragePanic( ind, 1, time );
  }

  /**
   *
   * @param ind an individual
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the average panic of the individual from the TimeStep "from" to the
   * TimeStep "to" (if the TimeStep "to" is later than the individual's
   * SafetyTime, this time is taken instead of "to")
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 OR are in wrong order OR are both greater
   * than the individual's safetyTime
   */
  public double calculateAveragePanic( Individual ind, int from, int to ) throws OneIndNoPotentialException, IncorrectTimeException {
    if( ind.isSafe() ) {
      int safetyTime = ind.getSafetyTime();
      if( to > safetyTime ) {
        to = safetyTime;
      }
      if( from > safetyTime ) {
        throw new IncorrectTimeException();
      }
    }

    if( from < to ) {
      if( !(ind.getDeathCause() != null && ind.getDeathCause().compareTo( ds.ca.evac.DeathCause.ExitUnreachable ) == 0) ) {
        if( panicTimes.containsKey( ind ) ) {
          if( from == 0 ) {
            from = 1;
          }
          int indexOfFrom = (Collections.binarySearch( panicTimes.get( ind ), from ));
          if( indexOfFrom < 0 ) {
            indexOfFrom = -indexOfFrom - 2;
          }
          if( indexOfFrom < 0 ) {
            throw new IncorrectTimeException();
          }

          int indexOfTo = (Collections.binarySearch( panicTimes.get( ind ), to ));
          if( indexOfTo < 0 ) {
            indexOfTo = -indexOfTo - 2;
          }
          if( indexOfTo < 0 ) {
            throw new IncorrectTimeException();
          }

          double lastPanic = panic.get( ind ).get( indexOfFrom );
          double weightedPanicSum = 0;
          int stepTo;
          int stepFrom = from;

          if( indexOfFrom == indexOfTo ) {
            return currentSpeed.get( ind ).get( indexOfFrom );
          }

          for( int i = indexOfFrom + 1; i < indexOfTo; i++ ) {
            stepTo = panicTimes.get( ind ).get( i );
            weightedPanicSum += lastPanic * (stepTo - stepFrom);
            lastPanic = panic.get( ind ).get( i );
            stepFrom = stepTo;
          }
          //last step:
          stepTo = to;
          weightedPanicSum += lastPanic * (stepTo - stepFrom);

          return weightedPanicSum / (to - from);
        }
        //duerfte nicht vorkommen
        throw new IllegalArgumentException();
      }
      // ind tot, weil eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }
    if( from == to ) {
      return getPanic( ind, from );
    } else // Zeiten falsch
    {
      throw new IncorrectTimeException();
    }
  }

  /**
   *
   * @param ind an individual
   * @param time a TimeStep
   * @return the maximum panic of the individual from the beginning of the
   * simulation to TimeStep "time" (if the TimeStep "to" is later than the
   * individual's SafetyTime, this time is taken instead of "to")
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxPanic( Individual ind, int time ) throws OneIndNoPotentialException, IncorrectTimeException {
    return calculateMaxPanic( ind, 0, time );
  }

  /**
   *
   * @param ind an individual
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the maximum panic of the individual from the TimeStep "from" to the
   * TimeStep "to" (if the TimeStep "to" is later than the individual's
   * SafetyTime, this time is taken instead of "to")
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxPanic( Individual ind, int from, int to ) throws OneIndNoPotentialException, IncorrectTimeException {
    if( from < to ) {
      if( !(ind.getDeathCause() != null && ind.getDeathCause().compareTo( ds.ca.evac.DeathCause.ExitUnreachable ) == 0) ) {
        if( panicTimes.containsKey( ind ) ) {
          if( ind.isSafe() ) {
            int safetyTime = ind.getSafetyTime();
            if( to > safetyTime ) {
              to = safetyTime;
            }
          }
          int indexOfFrom = (Collections.binarySearch( panicTimes.get( ind ), from ));
          if( indexOfFrom < 0 ) {
            indexOfFrom = -indexOfFrom - 2;
          }
          if( indexOfFrom < 0 ) {
            throw new IllegalArgumentException();
          }

          int indexOfTo = (Collections.binarySearch( panicTimes.get( ind ), to ));
          if( indexOfTo < 0 ) {
            indexOfTo = -indexOfTo - 2;
          }
          if( indexOfTo < 0 ) {
            throw new IllegalArgumentException();
          }

          double actualPanic;
          double maxPanic = 0;

          for( int i = indexOfFrom; i <= indexOfTo; i++ ) {
            actualPanic = panic.get( ind ).get( i );
            if( actualPanic > maxPanic ) {
              maxPanic = actualPanic;
            }
          }
          return maxPanic;
        }
        //duerfte nicht vorkommen
        throw new IllegalArgumentException();
      }
      // ind tot, weil eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }
    if( from == to ) {
      return getPanic( ind, from );
    } else // Zeiten falsch
    {
      throw new IncorrectTimeException();
    }
  }

  /**
   *
   * @param ind an individual
   * @param time a TimeStep
   * @return the minimum panic the individual had from the beginning of the
   * simulation to TimeStep "time" except for the panic at the beginning of the
   * simulation (if the individual never had a higher panic than that at the
   * beginning of the simulation, this value is returned) (if the TimeStep "to"
   * is later than the individual's SafetyTime, this time is taken instead of
   * "to")
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMinPanicExceptingStartPanic( Individual ind, int time ) throws OneIndNoPotentialException, IncorrectTimeException {
    return calculateMinPanicExceptingStartPanic( ind, 0, time );
  }

  /**
   *
   * @param ind an individual
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the minimum panic the individual had from the TimeStep "from" to
   * the TimeStep "to" except for the panic at the beginning of the simulation
   * (if the individual never had a higher panic than that at the beginning of
   * the simulation, this value is returned) (if the TimeStep "to" is later than
   * the individual's SafetyTime, this time is taken instead of "to")
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMinPanicExceptingStartPanic( Individual ind, int from, int to ) throws OneIndNoPotentialException, IncorrectTimeException {
    if( from < to ) {
      if( !(ind.getDeathCause() != null && ind.getDeathCause().compareTo( ds.ca.evac.DeathCause.ExitUnreachable ) == 0) ) {
        if( panicTimes.containsKey( ind ) ) {
          if( ind.isSafe() ) {
            int safetyTime = ind.getSafetyTime();
            if( to > safetyTime ) {
              to = safetyTime;
            }
          }
          if( from == 0 ) {
            try {
              from = panicTimes.get( ind ).get( 1 );
            } catch( Exception e ) {
              return panic.get( ind ).get( 0 );
            }
            if( to < from ) {
              return panic.get( ind ).get( 0 );
            }
          }
          int indexOfFrom = (Collections.binarySearch( panicTimes.get( ind ), from ));
          if( indexOfFrom < 0 ) {
            indexOfFrom = -indexOfFrom - 2;
          }
          if( indexOfFrom < 0 ) {
            throw new IllegalArgumentException();
          }

          int indexOfTo = (Collections.binarySearch( panicTimes.get( ind ), to ));
          if( indexOfTo < 0 ) {
            indexOfTo = -indexOfTo - 2;
          }
          if( indexOfTo < 0 ) {
            throw new IllegalArgumentException();
          }

          double actualPanic;
          double minPanic = Double.MAX_VALUE;

          for( int i = indexOfFrom; i <= indexOfTo; i++ ) {
            actualPanic = panic.get( ind ).get( i );
            if( actualPanic < minPanic ) {
              minPanic = actualPanic;
            }
          }
          return minPanic;
        }
        //duerfte nicht vorkommen
        throw new IllegalArgumentException();
      }
      // ind tot, weil eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }
    if( from == to ) {
      return getPanic( ind, from );
    } else // Zeiten falsch
    {
      throw new IncorrectTimeException();
    }
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param t a TimeStep
   * @return the average of the individual's panicvalues in TimeStep "t" (this
   * value is calculated only over those individuals which have a potential and
   * are not safe in timestep "timestep")
   * @return 0 if all individuals have no static potential (there`s no exit
   * reachable from the individual's positions) OR if all individual's first
   * stored values lie after the timestep "t"
   * @see #getPanic
   * @throws statistic.ca.exception.IncorrectTimeException if "timestep" is less
   * than 0
   * @throws statistic.ca.exception.MissingStoredValueException if at least one
   * individual had no speedvalue in timestep 1
   * @throws
   * statistic.ca.exception.GroupOfIndsNoValueBecauseAlreadySafeException if at
   * least one individual is already safe in timestep "timestep" AND all other
   * individuals have no static potential or are already safe in timestep
   * "timestep"
   */
  public double getPanicForGroup( ArrayList<Individual> indgroup, int t ) throws GroupOfIndsNoPotentialException, IncorrectTimeException, IllegalArgumentException {
    int noOfNoPotExc = 0;
    double panicSum = 0;
    double speedOfInd;
    double numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        panicSum += getPanic( ind, t );
        numberOfInds++;
      } catch( OneIndNoPotentialException ex ) {
        noOfNoPotExc++;
      } catch( OneIndNoValueBecauseAlreadySafeException e ) {
      }
    }

    if( numberOfInds != 0 ) {
      return panicSum / numberOfInds;
    } else {
      if( noOfNoPotExc == indgroup.size() ) {
        // alle inds haben sich nicht bewegt (weil sie tot oder safe waren)
        return 0;
      } else {
        //mind ein ind ist nicht eingeschlossen, liefert aber keinen Wert
        throw new GroupOfIndsNoValueBecauseAlreadySafeException( indgroup );
      }
    }

  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the average of the individual's average-panics from the beginning
   * of the simulation to TimeStep "time" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateAveragePanic
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateAverageAveragePanicForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateAverageAveragePanicForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the average of the individual's average-panics from the TimeStep
   * "from" to the TimeStep "to" (this value is calculated only over those
   * individuals which have a potential)
   * @see #calculateAveragePanic
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateAverageAveragePanicForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double averagePanicSum = 0;
    double averagePanicOfInd;
    int numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        averagePanicOfInd = calculateAveragePanic( ind, from, to );
        averagePanicSum += averagePanicOfInd;
        numberOfInds += 1;
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( numberOfInds != 0 ) {
      return averagePanicSum / numberOfInds;
    } else // alle ind tot weil eingeschlossen
    {
      throw new GroupOfIndsNoPotentialException( indgroup );
    }
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the average of the individual's maximum-panics from the beginning
   * of the simulation to TimeStep "time" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateMaxPanic
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateAverageMaxPanicForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateAverageMaxPanicForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the average of the individual's maximum-panics from the TimeStep
   * "from" to the TimeStep "to" (this value is calculated only over those
   * individuals which have a potential)
   * @see #calculateMaxPanic
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateAverageMaxPanicForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double maxPanicSum = 0;
    double maxPanicOfInd;
    int numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        maxPanicOfInd = calculateMaxPanic( ind, from, to );
        maxPanicSum += maxPanicOfInd;
        numberOfInds += 1;
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( numberOfInds != 0 ) {
      return maxPanicSum / numberOfInds;
    } else {
      throw new GroupOfIndsNoPotentialException( indgroup );
    }
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the average of the individual's minimum-panics from the beginning
   * of the simulation to TimeStep "time" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateMinPanicExceptingStartPanic
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateAverageMinPanicForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateAverageMinPanicForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the average of the individual's minimum-panics from the TimeStep
   * "from" to the TimeStep "to" (this value is calculated only over those
   * individuals which have a potential)
   * @see #calculateMinPanicExceptingStartPanic
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateAverageMinPanicForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double minPanicSum = 0;
    double minPanicOfInd;
    int numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        minPanicOfInd = calculateMinPanicExceptingStartPanic( ind, from, to );
        minPanicSum += minPanicOfInd;
        numberOfInds += 1;
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( numberOfInds != 0 ) {
      return minPanicSum / numberOfInds;
    } else {
      throw new GroupOfIndsNoPotentialException( indgroup );
    }
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the maximum of the individual's average-panics from the beginning
   * of the simulation to TimeStep "time" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateAveragePanic
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxAveragePanicForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateMaxAveragePanicForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the maximum of the individual's average-panics from the TimeStep
   * "from" to the TimeStep "to" (this value is calculated only over those
   * individuals which have a potential)
   * @see #calculateAveragePanic
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxAveragePanicForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double maxPanic = 0;
    double averagePanicOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        averagePanicOfInd = calculateAveragePanic( ind, from, to );
        nrOfInds++;
        if( averagePanicOfInd > maxPanic ) {
          maxPanic = averagePanicOfInd;
        }
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return maxPanic;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the maximum of the individual's maximum-panics from the beginning
   * of the simulation to TimeStep "time" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateMaxPanic
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxMaxPanicForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateMaxMaxPanicForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the maximum of the individual's maximum-panics from the TimeStep
   * "from" to the TimeStep "to" (this value is calculated only over those
   * individuals which have a potential)
   * @see #calculateMaxPanic
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMaxMaxPanicForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double maxPanic = 0;
    double maxPanicOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        maxPanicOfInd = calculateMaxPanic( ind, from, to );
        nrOfInds++;
        if( maxPanicOfInd > maxPanic ) {
          maxPanic = maxPanicOfInd;
        }
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return maxPanic;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the minimum of the individual's average-panics from the beginning
   * of the simulation to TimeStep "time" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateAveragePanic
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMinAveragePanicForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateMinAveragePanicForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the minimum of the individual's average-panics from the TimeStep
   * "from" to the TimeStep "to" (this value is calculated only over those
   * individuals which have a potential)
   * @see #calculateAveragePanic
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMinAveragePanicForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double minPanic = Double.MAX_VALUE;
    double averagePanicOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        averagePanicOfInd = calculateAveragePanic( ind, from, to );
        nrOfInds++;
        if( averagePanicOfInd < minPanic ) {
          minPanic = averagePanicOfInd;
        }
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return minPanic;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the minimum of the individual's minimum-panics from the beginning
   * of the simulation to TimeStep "time" (this value is calculated only over
   * those individuals which have a potential)
   * @see #calculateMinPanicExceptingStartPanic
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMinMinPanicForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateMinMinPanicForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the minimum of the individual's minimum-panics from the TimeStep
   * "from" to the TimeStep "to" (this value is calculated only over those
   * individuals which have a potential)
   * @see #calculateMinPanicExceptingStartPanic
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateMinMinPanicForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    double minPanic = 0;
    double minPanicOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        minPanicOfInd = calculateMinPanicExceptingStartPanic( ind, from, to );
        nrOfInds++;
        if( minPanicOfInd > minPanic ) {
          minPanic = minPanicOfInd;
        }
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return minPanic;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

   // exits and distances to exits
  /**
   *
   * @param ind an individual; an individual
   * @param t a TimeStep
   * @return the list of exitcells, over which the individual actually (in
   * TimeStep t) plannes to leave the building
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws java.lang.IllegalArgumentException
   */
  //eventuell auf StaticPotential umstellen?
  public ArrayList<ExitCell> getPlannedExit( Individual ind, int t ) throws OneIndNoPotentialException, IllegalArgumentException {
    if( changePotentialTimes.containsKey( ind ) ) {
      int index = (Collections.binarySearch( changePotentialTimes.get( ind ), t ));
      if( index < 0 ) {
        index = -index - 2;
      }
      if( index >= 0 ) {
        return potentials.get( ind ).get( index );
      } else // ind hat zum Zeitpunkt t noch kein Potential
      {
        throw new OneIndNoPotentialException( ind );
      }

    }
    //ind hat überhaupt nie ein Potential gehabt
    throw new OneIndNoPotentialException( ind );
  }

  /**
   *
   * @param ind an individual
   * @return the StaticPotential to which`s exit the individual has left the
   * building
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   */
  public StaticPotential getTakenExit( Individual ind ) throws OneIndNoPotentialException {
    if( !takenExit.containsKey( ind ) ) {
      // ind hat keinen Ausgang genommen (ist gestorben)
      throw new OneIndNoPotentialException( ind );
    }
    return takenExit.get( ind );
  }

  /**
   *
   * @param ind an individual
   * @return the minimal distance from the starting position of the individual
   * to the nearest exit
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   */
  public double minDistanceToNearestExit( Individual ind ) throws OneIndNoPotentialException {
    if( !minDistanceToNearestExit.containsKey( ind ) ) {
      // Ind tot weil eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }
    return minDistanceToNearestExit.get( ind );
  }

  /**
   *
   * @param ind an individual
   * @return the minimal distance from the starting position of the individual
   * to the FIRST planned exit
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   */
  public double minDistanceToPlannedExit( Individual ind ) throws OneIndNoPotentialException {
    if( !minDistanceToPlannedExit.containsKey( ind ) ) {
      // Ind tot weil eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }
    return minDistanceToPlannedExit.get( ind );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @return the average of the individual's minDistanceToNearestExit (this
   * value is calculated only over those individuals which have a potential)
   * @see #minDistanceToNearestExit
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there's no exit reachable from the
   * individual's positions)
   */
  public double minDistanceToNearestExitForGroup( ArrayList<Individual> indgroup ) throws GroupOfIndsNoPotentialException {
    double distanceSum = 0.0;
    double noOfIndividuals = 0;
    for( Individual i : indgroup ) {
      try {
        distanceSum += minDistanceToNearestExit( i );
        noOfIndividuals++;
      } catch( OneIndNoPotentialException e ) {
      }
    }
    if( noOfIndividuals == 0 ) {
      throw new GroupOfIndsNoPotentialException( indgroup );
    }
    return distanceSum / noOfIndividuals;
  }

  /**
   *
   * @param indgroup a list of individuals
   * @return the average of the individual's minDistanceToPlannedExit (this
   * value is calculated only over those individuals which have a potential)
   * @see #minDistanceToPlannedExit
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no static potential (there`s no exit reachable from the
   * individual's positions)
   * @throws java.lang.IllegalArgumentException
   */
  public double minDistanceToPlannedExitForGroup( ArrayList<Individual> indgroup ) throws GroupOfIndsNoPotentialException, IllegalArgumentException {
    double distanceSum = 0.0;
    int noOfIndividuals = 0;
    for( Individual i : indgroup ) {
      try {
        distanceSum += minDistanceToPlannedExit( i );
        noOfIndividuals++;
      } catch( OneIndNoPotentialException e ) {
      }
    }
    if( noOfIndividuals == 0 ) {
      throw new GroupOfIndsNoPotentialException( indgroup );
    }
    return distanceSum / noOfIndividuals;
  }

  /**
   *
   * @param ind an individual
   * @param time a TimeStep
   * @return the difference between the individual's minDistanceToPlannedExit
   * and the distance which the individual had covered until TimeStep "time"
   * @see #minDistanceToPlannedExit
   * @see #getCoveredDistance
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if the given
   * TimeStep(s) is/are less than 0 or are in wrong order
   */
  public double calculateDifferenceMinAndRealDistanceToPlannedExit( Individual ind, int time ) throws OneIndNoPotentialException, IncorrectTimeException {
    double toNearestExit = minDistanceToNearestExit( ind );
    return getCoveredDistance( ind, time ) - toNearestExit;
  }

  /**
   *
   * @param ind an individual
   * @return the difference between the individual's minDistanceToPlannedExit
   * and minDistanceToNearestExit
   * @see #minDistanceToPlannedExit
   * @see #minDistanceToNearestExit
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   */
  public double calculateDifferenceNearestAndPlannedExit( Individual ind ) throws OneIndNoPotentialException {
    double toPlannedExit = minDistanceToPlannedExit( ind );
    return toPlannedExit - minDistanceToNearestExit( ind );
  }

    // safetyTime and savedIndividuals
  /**
   *
   * @param ind an individual; an individual
   * @return the TimeStep, in which the individual has first entered a save- or
   * exitcell
   * @throws statistic.ca.exception.OneIndNotSafeException if the individual has
   * never entered a save- or exitcell
   */
  public int getSafetyTime( Individual ind ) throws OneIndNotSafeException {
    if( safetyTimes.containsKey( ind ) ) {
      return safetyTimes.get( ind );
    }
    // ind nicht safe
    throw new OneIndNotSafeException( ind );
  }

  /**
   *
   * @param ind an individual
   * @param time a TimeStep
   * @return true, if the individual is already safe in TimeStep "time"; false
   * otherwise
   * @see #getSafetyTime
   * @throws statistic.ca.exception.IncorrectTimeException if "time" is less
   * than 0
   */
  public boolean isIndividualSafe( Individual ind, int time ) throws IncorrectTimeException {
    if( time < 0 ) {
      throw new IncorrectTimeException();
    }
    if( ind.isSafe() ) {
      return (ind.getSafetyTime() <= time);
    } else {
      return false;
    }
  }

  /**
   *
   * @param indgroup a list of individuals
   * @return the average of the numbers of TimeSteps in which the individuals
   * first entered a save- or exitcell (this value is calculated only over those
   * individuals which are safe at the end of the simulation)
   * @see #getSafetyTime
   * @throws statistic.ca.exception.GroupOfIndsNotSafeException if all
   * individuals have no safetyTime
   */
  public double calculateAverageSafetyTimeForGroup( ArrayList<Individual> indgroup ) throws GroupOfIndsNotSafeException {
    double safetyTimeSum = 0;
    int safetyTimeOfInd;
    double numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        safetyTimeOfInd = getSafetyTime( ind );
        safetyTimeSum += safetyTimeOfInd;
        numberOfInds += 1;
      } catch( OneIndNotSafeException ex ) {
      }
    }
    if( numberOfInds != 0 ) {
      return safetyTimeSum / numberOfInds;
    }
    throw new GroupOfIndsNotSafeException( indgroup );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @return the maximum of the individual's SafetyTimes
   * @see #getSafetyTime
   * @throws statistic.ca.exception.GroupOfIndsNotSafeException if all
   * individuals have no safetyTime
   */
  public int calculateMaxSafetyTimeForGroup( ArrayList<Individual> indgroup ) throws GroupOfIndsNotSafeException {
    int maxSafetyTime = 0;
    int safetyTimeOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        safetyTimeOfInd = getSafetyTime( ind );
        nrOfInds++;
        if( safetyTimeOfInd > maxSafetyTime ) {
          maxSafetyTime = safetyTimeOfInd;
        }
      } catch( OneIndNotSafeException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return maxSafetyTime;
    }
    throw new GroupOfIndsNotSafeException( indgroup );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @return the minimum of the individual's SafetyTimes
   * @see #getSafetyTime
   * @throws statistic.ca.exception.GroupOfIndsNotSafeException if all
   * individuals have no safetyTime
   */
  public int calculateMinSafetyTimeForGroup( ArrayList<Individual> indgroup ) throws GroupOfIndsNotSafeException {
    int minSafetyTime = Integer.MAX_VALUE;
    int safetyTimeOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        safetyTimeOfInd = getSafetyTime( ind );
        nrOfInds++;
        if( safetyTimeOfInd < minSafetyTime ) {
          minSafetyTime = safetyTimeOfInd;
        }
      } catch( OneIndNotSafeException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return minSafetyTime;
    }
    throw new GroupOfIndsNotSafeException( indgroup );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the number of individuals which are already safe in TimeStep "time"
   * @see #isIndividualSafe
   * @throws statistic.ca.exception.IncorrectTimeException if "time" is less
   * than 0
   */
  public int getNumberOfSafeIndividualForGroup( ArrayList<Individual> indgroup, int time ) throws IncorrectTimeException {
    int result = 0;
    for( Individual i : indgroup ) {
      if( isIndividualSafe( i, time ) ) {
        result++;
      }
    }
    return result;
  }

  /**
   *
   * @param indgroup a list of individuals
   * @return the percentage of individuals which are safe at the end of the
   * simulation from the given individuals
   */
  public double calculatePercentageOfSaveIndividuals( ArrayList<Individual> indgroup ) {
    double numberOfInds = 0;
    double numberofSafeInds = 0;
    for( Individual ind : indgroup ) {
      numberOfInds++;
      if( ind.isSafe() ) {
        numberofSafeInds++;
      }
    }
    if( numberOfInds != 0 ) {
      return numberofSafeInds / numberOfInds * 100;
    } else {
      throw new IllegalArgumentException( "Indgroup with no individuals" );
    }
  }

    // waitedTime
  /**
   *
   * @param ind an individual
   * @param t a TimeStep
   * @return the number of TimeSteps in which the individual waited (=all
   * neighbourcells were occupied by other individuals) from the beginning of
   * the simulation to TimeStep "time"
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if "t" is less than 0
   */
  public int getWaitedTime( Individual ind, int t ) throws OneIndNoPotentialException, IncorrectTimeException {

    if( ind.getDeathCause() != null && ind.getDeathCause().compareTo( ds.ca.evac.DeathCause.ExitUnreachable ) == 0 ) {
      // ind tot weil eingeschlossen
      throw new OneIndNoPotentialException( ind );
    }

    if( !waitedTimeTimes.containsKey( ind ) ) {
      //ind hat nie gewartet
      return 0;
    }

    if( t < 0 ) {
      // Zeit ist falsch
      throw new IncorrectTimeException();
    }

    if( t == 0 ) {
      return 0;
    }

    int index = Collections.binarySearch( waitedTimeTimes.get( ind ), t );
    if( index < 0 ) {
      index = -index - 2;
    }

    if( index < 0 ) {
      // ind hat zum Zeitpunkt t noch nicht gewartet
      return 0;
    }

    return waitedTime.get( ind ).get( index );

  }

  /**
   *
   * @param ind an individual
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the number of TimeSteps in which the individual waited (=all
   * neighbourcells were occupied by other individuals) from the TimeStep "from"
   * to the TimeStep "to"
   *
   * @throws statistic.ca.exception.OneIndNoPotentialException if the individual
   * has no static potential (there`s no exit reachable from the individual's
   * position)
   * @throws statistic.ca.exception.IncorrectTimeException if "from" is bigger
   * than "to" OR "from" is less than 0 OR "to" is less than 0
   */
  public int getWaitedTime( Individual ind, int from, int to ) throws OneIndNoPotentialException, IncorrectTimeException {
    if( from > to ) {
      // Zeiten falschrum
      throw new IncorrectTimeException();
    }
    return getWaitedTime( ind, to ) - getWaitedTime( ind, from );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the average of the numbers of TimeSteps in which the individuals
   * waited from the beginning of the simulation to TimeStep "time" (this value
   * is calculated only over those individuals which have a potential)
   * @see #getWaitedTime
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no potential
   * @throws statistic.ca.exception.IncorrectTimeException if "time" is less
   * than 0
   */
  public double calculateAverageWaitedTimeForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndividualsException, IncorrectTimeException {
    return calculateAverageWaitedTimeForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the average of the numbers of TimeSteps in which the individuals
   * waited from the TimeStep "from" to the TimeStep "to" (this value is
   * calculated only over those individuals which have a potential)
   * @see #getWaitedTime
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no potential
   * @throws statistic.ca.exception.IncorrectTimeException if "from" is bigger
   * than "to" OR "from" is less than 0 OR "to" is less than 0
   */
  public double calculateAverageWaitedTimeForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndividualsException, IncorrectTimeException {
    double waitedTimeSum = 0;
    int waitedTimeOfInd;
    double numberOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        waitedTimeOfInd = getWaitedTime( ind, from, to );
        waitedTimeSum += waitedTimeOfInd;
        numberOfInds += 1;
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( numberOfInds != 0 ) {
      return waitedTimeSum / numberOfInds;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the maximum of the numbers of TimeSteps in which the individuals
   * waited from the beginning of the simulation to TimeStep "time" (this value
   * is calculated only over those individuals which have a potential)
   * @see #getWaitedTime
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no potential
   * @throws statistic.ca.exception.IncorrectTimeException if "time" is less
   * than 0
   */
  public int calculateMaxWaitedTimeForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndividualsException, IncorrectTimeException {
    return calculateMaxWaitedTimeForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the maximum of the numbers of TimeSteps in which the individuals
   * waited from the TimeStep "from" to the TimeStep "to" (this value is
   * calculated only over those individuals which have a potential)
   * @see #getWaitedTime
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no potential
   * @throws statistic.ca.exception.IncorrectTimeException if "from" is bigger
   * than "to" OR "from" is less than 0 OR "to" is less than 0
   */
  public int calculateMaxWaitedTimeForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndividualsException, IncorrectTimeException {
    int maxWaitedTime = 0;
    int waitedTimeOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        waitedTimeOfInd = getWaitedTime( ind, from, to );
        nrOfInds++;
        if( waitedTimeOfInd > maxWaitedTime ) {
          maxWaitedTime = waitedTimeOfInd;
        }
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return maxWaitedTime;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param time a TimeStep
   * @return the minimum of the numbers of TimeSteps in which the individuals
   * waited from the beginning of the simulation to TimeStep "time" (this value
   * is calculated only over those individuals which have a potential)
   * @see #getWaitedTime
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no potential
   * @throws statistic.ca.exception.IncorrectTimeException if "time" is less
   * than 0
   */
  public int calculateMinWaitedTimeForGroup( ArrayList<Individual> indgroup, int time ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    return calculateMinWaitedTimeForGroup( indgroup, 0, time );
  }

  /**
   *
   * @param indgroup a list of individuals
   * @param from a TimeStep
   * @param to a TimeStep
   * @return the minimum of the numbers of TimeSteps in which the individuals
   * waited from the TimeStep "from" to the TimeStep "to" (this value is
   * calculated only over those individuals which have a potential)
   * @see #getWaitedTime
   * @throws statistic.ca.exception.GroupOfIndsNoPotentialException if all
   * individuals have no potential
   * @throws statistic.ca.exception.IncorrectTimeException if "from" is bigger
   * than "to" OR "from" is less than 0 OR "to" is less than 0
   */
  public int calculateMinWaitedTimeForGroup( ArrayList<Individual> indgroup, int from, int to ) throws GroupOfIndsNoPotentialException, IncorrectTimeException {
    int minWaitedTime = Integer.MAX_VALUE;
    int waitedTimeOfInd;
    int nrOfInds = 0;
    for( Individual ind : indgroup ) {
      try {
        waitedTimeOfInd = getWaitedTime( ind, from, to );
        nrOfInds++;
        if( waitedTimeOfInd < minWaitedTime ) {
          minWaitedTime = waitedTimeOfInd;
        }
      } catch( OneIndNoPotentialException ex ) {
      }
    }
    if( nrOfInds != 0 ) {
      return minWaitedTime;
    }
    throw new GroupOfIndsNoPotentialException( indgroup );
  }

}//end class
