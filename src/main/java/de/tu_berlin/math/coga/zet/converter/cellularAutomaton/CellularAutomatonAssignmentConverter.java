/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */
package de.tu_berlin.math.coga.zet.converter.cellularAutomaton;

import org.zet.cellularautomaton.algorithm.parameter.AbstractParameterSet;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zetool.common.algorithm.AbstractAlgorithm;
import de.tu_berlin.math.coga.zet.ZETLocalization2;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAConverter.ConversionNotSupportedException;
import ds.PropertyContainer;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.TargetCell;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.model.Person;
import de.zet_evakuierung.model.PlanPoint;
import exitdistributions.ZToExitMapping;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.IndividualBuilder;
import org.zetool.common.datastructure.SimpleTuple;
import statistics.Statistic;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CellularAutomatonAssignmentConverter extends AbstractAlgorithm<AssignmentApplicationInstance, SimpleTuple<Map<Individual, EvacCellInterface>, ZToExitMapping>> {
    private final Map<Individual, EvacCellInterface> individualStartPositions = new HashMap<>();
    private ZToExitMapping mapping;
    
    @Override
    protected SimpleTuple<Map<Individual, EvacCellInterface>, ZToExitMapping> runAlgorithm(AssignmentApplicationInstance problem) {
        try {
            applyConcreteAssignment(problem.getV(), problem.getU());
        } catch (IllegalArgumentException | ConversionNotSupportedException ex) {
            Logger.getLogger(CellularAutomatonAssignmentConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new SimpleTuple<>(individualStartPositions, mapping);
    }

    /**
     * Puts persons which are specified in an concrete assignment into the right cells of the cellular automaton. This
     * is done by first get the rastered square version of the room specified in the person object. Afterwards the cell,
     * in which the person has to be located, is calculated in the following way: (coordinate-200)/400
     *
     * @param concreteAssignment the concrete assignment containing the individuals
     * @throws java.lang.IllegalArgumentException if the calculated cell is not in the room of the cellular automaton
     */
    private void applyConcreteAssignment(ConcreteAssignment concreteAssignment, ConvertedCellularAutomaton cca) throws IllegalArgumentException, ConversionNotSupportedException {
        IndividualBuilder builder = new IndividualBuilder();

        ZToCARoomRaster room;
        // Create ZToExitMapping
        HashMap<Individual, TargetCell> individualExitMapping = new HashMap<>();
        EvacCellInterface c;
        int x, y;
        for (Person p : concreteAssignment.getPersons()) {
            room = cca.getContainer().getRasteredRoom(p.getRoom());
            x = Math.round((p.getPosition().getXInt() - 200 - p.getRoom().getPolygon().getxOffset()) / 400);
            y = Math.round((p.getPosition().getYInt() - 200 - p.getRoom().getPolygon().getyOffset()) / 400);
            if (!room.isValid(x, y)) {
                throw new IllegalArgumentException(ZETLocalization2.loc.getString("converter.IndividualOutsideException"));
            }
            c = cca.getMapping().get(room.getSquare(x, y));
            if (c == null) {
                //JEditor.showErrorMessage( "Fehler", "Individuen konnten nicht erzeugt werden in Raum '" + room.getRoom().getName() + "'. Eventuell keine freien Plätze durch unbetretbare Bereiche?" );
                //throw new ConversionNotSupportedException();
                // Individual mapped to inaccessible area
                // skip
            } else {
                Individual i = generateIndividual(builder, p);

                Collection<Exit> exits = cca.getCellularAutomaton().getExits();
                for (Exit exit : exits) {
                    Collection<ExitCell> ecs = exit.getExitCluster();
                    for (ExitCell e : ecs) {
                        // Calculate absolute position
                        // TODO 400 hardcoded hier!
                        int cellCenterX = (e.getX() + e.getRoom().getXOffset()) * 400 + 200;
                        int cellCenterY = (e.getY() + e.getRoom().getYOffset()) * 400 + 200;
                        if (p.getSaveArea() != null && p.getSaveArea().contains(new PlanPoint(cellCenterX, cellCenterY))) {
                            individualExitMapping.put(i, e);
                            break;
                        }
                    }
                }

                //cca.getCellularAutomaton().addIndividual(c, i);
                individualStartPositions.put(i, c);
            }
        }
        
        
        mapping = new ZToExitMapping(individualExitMapping);
        //cca.getCellularAutomaton().setIndividualToExitMapping(mapping);        
    }

    /**
     * This method returns an individual whose attributes are based upon die assignment specified by the Z-Assignment
     * group.<br>
     * To do this the different assignments could be extracted form the passed assignment object. With these values the
     * specific random attributes for the generated individual can be calculated.
     *
     * @param assignment The specific Assignment for the new created individual
     * @return Returns an individual of type Individual
     */
    private static Individual generateIndividual(IndividualBuilder builder, Person p) {
        String parameterName = PropertyContainer.getGlobal().getAsString("algo.ca.parameterSet");
        AbstractParameterSet.createParameterSet("DefaultParameterSet");
        ParameterSet ps = AbstractParameterSet.createParameterSet(parameterName);

        double pDecisiveness = p.getDecisiveness();
        double pFamiliarity = p.getFamiliarity();
        double pAge = p.getAge();
        //double pDiameter = p.getDiameter(); // not used in current implementation

        // here is specified how the different properties of an individual are calculated
        double familiarity = pFamiliarity;
        double panicFactor = p.getPanic();
        double slackness = ps.getSlacknessFromDecisiveness(pDecisiveness);
        double exhaustionFactor = ps.getExhaustionFromAge(pAge);
        double maxSpeed = ps.getSpeedFromAge(pAge);
        double reactiontime = p.getReaction();

        // Collect statistic
        Statistic.instance.collectAgeSpeed(pAge, maxSpeed);

        double relativeMaxSpeed = maxSpeed / ps.getAbsoluteMaxSpeed();
        return builder.withAge((int) pAge).withFamiliarity(familiarity).withPanicFactor(panicFactor)
                .withSlackness(slackness).withExhaustionFactor(exhaustionFactor).withRelativeMaxSpeed(relativeMaxSpeed)
                .withReactionTime(reactiontime).build();
    }
}
