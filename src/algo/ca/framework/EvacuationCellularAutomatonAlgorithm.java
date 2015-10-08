package algo.ca.framework;

import algo.ca.algorithm.evac.EvacuationSimulationProblem;
import algo.ca.algorithm.evac.EvacuationSimulationResult;
import algo.ca.rule.Rule;
import de.tu_berlin.math.coga.algorithm.simulation.cellularautomaton.AbstractCellularAutomatonSimulationAlgorithm;
import ds.ca.evac.DeathCause;
import ds.ca.evac.EvacCell;
import ds.ca.evac.Individual;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 * An implementation of a general cellular automaton algorithm specialized for evacuation simulation. The cells of the
 * cellular automaton are populized by {@link Individual}s and the simulation is rulebased performed only on these
 * populated cells. The algorithm is itself abstract and implementations have to specify the order in which the rules
 * are executed for the populating individuals.
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class EvacuationCellularAutomatonAlgorithm
        extends AbstractCellularAutomatonSimulationAlgorithm<EvacCell,
        EvacuationSimulationProblem, EvacuationSimulationResult> {

  public EvacuationCellularAutomatonAlgorithm() {
    System.out.println( "Instance created!" );
  }
  
  
  
  /** The simulation result. */
  private EvacuationSimulationResult evacuationSimulationResult;

  /**
   * Define the maximum allowed time for evacuation (that is simulated) in seconds.
   * @param time the time limit
   */
  public final void setMaxTimeInSeconds( double time ) {
    int maxTimeInSteps = (int)Math.ceil( time * getProblem().eca.getStepsPerSecond() );
    setMaxSteps( maxTimeInSteps );
  }

  @Override
  protected void initialize() {
    log.log( Level.INFO, "{0} wird ausgef\u00fchrt. ", toString() );
    evacuationSimulationResult = new EvacuationSimulationResult();

    getProblem().eca.start();
    Individual[] individualsCopy = getProblem().eca.getIndividuals().toArray(
            new Individual[getProblem().eca.getIndividuals().size()] );
    for( Individual i : individualsCopy ) {
      Iterator<Rule> primary = getProblem().ruleSet.primaryIterator();
      EvacCell c = i.getCell();
      while( primary.hasNext() ) {
        Rule r = primary.next();
        r.execute( c );
      }
    }
    getProblem().eca.removeMarkedIndividuals();
  }

  @Override
  protected void performStep() {
    super.performStep();

    super.increaseStep();

    getProblem().eca.removeMarkedIndividuals();
    getProblem().potentialController.updateDynamicPotential(
            getProblem().parameterSet.probabilityDynamicIncrease(),
            getProblem().parameterSet.probabilityDynamicDecrease() );
//		caController.getPotentialController().updateDynamicPotential(
//            caController.parameterSet.probabilityDynamicIncrease(),
//            caController.parameterSet.probabilityDynamicDecrease() );
    getProblem().eca.nextTimeStep();

    fireProgressEvent( getProgress(), String.format( "%1$s von %2$s Personen evakuiert.",
            getProblem().eca.getInitialIndividualCount() - getProblem().eca.getIndividualCount(),
            getProblem().eca.getInitialIndividualCount() ) );
  }

  @Override
  protected final void execute( EvacCell cell ) {

    Individual i = Objects.requireNonNull( cell.getIndividual(),
            "Execute called on EvacCell that does not contain an individual!" );
    //System.out.println( "Executing rules for individual " + i );
    Iterator<Rule> loop = getProblem().ruleSet.loopIterator();
    while( loop.hasNext() ) { // Execute all rules
      Rule r = loop.next();
      r.execute( i.getCell() );
    }
  }

  @Override
  protected EvacuationSimulationResult terminate() {
    // let die all individuals which are not already dead and not safe
    if( getProblem().eca.getNotSafeIndividualsCount() != 0 ) {
      Individual[] individualsCopy = getProblem().eca.getIndividuals().toArray(
              new Individual[getProblem().eca.getIndividuals().size()] );
      for( Individual i : individualsCopy ) {
        if( !i.getCell().getIndividual().isSafe() ) {
          getProblem().eca.setIndividualDead( i, DeathCause.NotEnoughTime );
        }
      }
    }
    fireProgressEvent( 1, "Simulation abgeschlossen" );

    getProblem().eca.stop();
    System.out.println( "Time steps: " + getProblem().eca.getTimeStep() );
    return evacuationSimulationResult;
  }

  @Override
  protected boolean isFinished() {
    boolean continueCondition = ((getProblem().eca.getNotSafeIndividualsCount() > 0
            || getProblem().eca.getTimeStep() <= getProblem().eca.getNeededTime()) /*&& !isCancelled()*/);
    return super.isFinished() || !continueCondition;
  }

  /**
   * Sends a progress event. The progress is defined as the maximum of the percentage of already evacuated individuals
   * and the fraction of time steps of the maximum amount of time steps already simulated.
   * @return the current progress as percentage of safe individuals
   */
  @Override
  protected final double getProgress() {
    double timeProgress = super.getProgress();
    double individualProgress = 1.0 - ((double)getProblem().eca.getIndividualCount()
            / getProblem().eca.getInitialIndividualCount());
    double progress = Math.max( individualProgress, timeProgress );
    return progress;
  }

  /**
   * An iterator that iterates over all cells of the cellular automaton that contains an individual. The rules of the
   * simulation algorithm are being executed on each of the occupied cells.
   * @return iterator of all occupied cells
   */
  @Override
  public final Iterator<EvacCell> iterator() {
    return new CellIterator( getIndividuals() );

  }

  /**
   * Returns all individuals currently contained in the simulation in an unspecified order. Individuals are being
   * removed from simulation when they are either dead or reach exit cells. The order can be specified by overwriting
   * implementations.
   * @return all individuals in the simulation
   */
  protected abstract List<Individual> getIndividuals();

  /**
   * A simple iterator that iterates over all cells of the cellular automaton that contain an individual. The iteration
   * order equals the order of the individuals given.
   */
  private static class CellIterator implements Iterator<EvacCell> {
    private final Iterator<Individual> individuals;

    /**
     * Initializes the object with a list of individuals whose cells are iterated over.
     * @param individuals the individuals
     */
    private CellIterator( List<Individual> individuals ) {
      this.individuals = Objects.requireNonNull( individuals, "Individuals list must not be null." ).iterator();
    }

    @Override
    public boolean hasNext() {
      return individuals.hasNext();
    }

    @Override
    public EvacCell next() {
      return individuals.next().getCell();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException( "Removal of cells is not supported." );
    }
  }
}
