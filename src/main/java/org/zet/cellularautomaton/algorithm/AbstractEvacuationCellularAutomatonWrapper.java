/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.zet.cellularautomaton.algorithm;

import java.util.logging.Logger;
import org.zet.cellularautomaton.algorithm.EvacuationCellularAutomatonAlgorithm;
import org.zet.cellularautomaton.algorithm.EvacuationSimulationResult;
import org.zetool.common.datastructure.parameter.ParameterSet;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
abstract class AbstractEvacuationCellularAutomatonWrapper extends EvacuationCellularAutomatonAlgorithm {
    
    protected EvacuationCellularAutomatonAlgorithm wrapped;
    private EvacuationSimulationResult result = null;

    AbstractEvacuationCellularAutomatonWrapper(EvacuationCellularAutomatonAlgorithm wrapped) {
        this.wrapped = wrapped;
    }

    //@Override
    //protected List<Individual> getIndividuals() {
    //    return wrapped.getIndividuals();
    //}

    @Override
    protected void initialize() {
        wrapped.addAlgorithmListener(this);
        wrapped.setProblem(getProblem());
    }

    protected abstract void perform();

    @Override
    protected void performStep() {
        wrapped.initialize();

        perform();

        result = wrapped.terminate();
    }

    @Override
    protected EvacuationSimulationResult terminate() {
        return result;
    }

    @Override
    protected boolean isFinished() {
        return result != null;
    }

    @Override
    public int getMaxSteps() {
        return wrapped.getMaxSteps();
    }

    @Override
    public void setMaxSteps(int maxSteps) {
        wrapped.setMaxSteps(maxSteps);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription();
    }

    @Override
    public void setDescription(String description) {
        wrapped.setDescription(description);
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public void setName(String name) {
        wrapped.setName(name);
    }

    @Override
    public ParameterSet getParameterSet() {
        return wrapped.getParameterSet();
    }

    @Override
    public void setParameterSet(ParameterSet parameterSet) {
        wrapped.setParameterSet(parameterSet);
    }

    @Override
    public double getAccuracy() {
        return wrapped.getAccuracy();
    }

    @Override
    public void setAccuracy(double accuracy) throws IllegalArgumentException {
        wrapped.setAccuracy(accuracy);
    }

    @Override
    public void setAccuracy(int possibleChanges) {
        wrapped.setAccuracy(possibleChanges);
    }

    @Override
    public Logger getLogger() {
        return wrapped.getLogger();
    }

    @Override
    public void setLogger() {
        wrapped.setLogger();
    }

    @Override
    public void setLogger(Logger log) {
        wrapped.setLogger(log);
    }

    @Override
    public String toString() {
        return wrapped.toString();
    }
}
