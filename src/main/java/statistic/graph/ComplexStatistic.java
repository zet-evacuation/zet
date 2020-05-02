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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
/*
 * ComplexStatistic.java
 *
 */
package statistic.graph;

import org.zetool.statistic.Operation;
import org.zetool.statistic.Statistics;
import org.zetool.statistic.Statistic;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
public class ComplexStatistic<O, R, D> {

    private Operation<R> objectOperation;
    private Object[] objectParameters;
    private Operation<R> runOperation;
    private Object[] runParameters;
    @XStreamOmitField
    private StatisticsCollection<D> statisticsCollection;
    private Statistic<O, R, D> statistic;

    public ComplexStatistic() {
        objectParameters = new Object[0];
        runParameters = new Object[0];
    }

    public StatisticsCollection<D> getStatisticsCollection() {
        return statisticsCollection;
    }

    public void setStatisticsCollection(StatisticsCollection<D> statisticsCollection) {
        this.statisticsCollection = statisticsCollection;
    }

    public Statistic<O, R, D> getStatistic() {
        return statistic;
    }

    public void setStatistic(Statistic<O, R, D> statistic) {
        this.statistic = statistic;
    }

    public Operation<R> getObjectOperation() {
        return objectOperation;
    }

    public void setObjectOperation(Operation<R> objectOperation) {
        this.objectOperation = objectOperation;
    }

    public Object[] getObjectParameters() {
        return objectParameters;
    }

    public void setObjectParameters(Object... objectParameters) {
        this.objectParameters = objectParameters;
    }

    public Operation<R> getRunOperation() {
        return runOperation;
    }

    public void setRunOperation(Operation<R> runOperation) {
        this.runOperation = runOperation;
    }

    public Object[] getRunParameters() {
        return runParameters;
    }

    public void setRunParameters(Object... runParameters) {
        this.runParameters = runParameters;
    }

    public R get(O object) {
        if (statisticsCollection.isEmpty()) {
            throw new IllegalStateException("No runs have been specified.");
        } else if (statisticsCollection.size() == 1) {
            return statisticsCollection.getFirst().get(statistic, object);
        } else if (runOperation == null) {
            throw new IllegalStateException("No operation for compounding the runs has been specified.");
        } else {
            List<R> results = new LinkedList<R>();
            for (Statistics<D> statistics : statisticsCollection) {
                results.add(statistics.get(statistic, object));
            }
            return runOperation.execute(results, runParameters);
        }
    }

    public List<R> getList(O object) {
        if (statisticsCollection.isEmpty()) {
            throw new IllegalStateException("No runs have been specified.");
        } else {
            List<R> results = new LinkedList<R>();
            for (Statistics<D> statistics : statisticsCollection) {
                results.add(statistics.get(statistic, object));
            }
            return results;
        }
    }

    public R get(Iterable<O> objects) {
        if (statisticsCollection.isEmpty()) {
            throw new IllegalStateException("No runs have been specified.");
        } else if (objectOperation == null) {
            throw new IllegalStateException("No operation for compounding the objects has been specified.");
        } else if (statisticsCollection.size() > 1 && runOperation == null) {
            throw new IllegalStateException("No operation for compounding the runs has been specified.");
        }
        if (objects == null || !objects.iterator().hasNext()) {
            objects = new LinkedList();
            ((LinkedList) objects).add(null);
        }
        if (statisticsCollection.size() == 1) {
            List<R> results = new LinkedList<R>();
            for (O object : objects) {
                results.add(statisticsCollection.getFirst().get(statistic, object));
            }
            return objectOperation.execute(results, objectParameters);
        } else {
            List<R> results = new LinkedList<R>();
            for (Statistics<D> statistics : statisticsCollection) {
                List<R> runResults = new LinkedList<R>();
                for (O object : objects) {
                    runResults.add(statistics.get(statistic, object));
                }
                results.add(objectOperation.execute(runResults, objectParameters));
            }
            return runOperation.execute(results, runParameters);
        }
    }

    public List<R> getListPerRun(Iterable<O> objects) {
        if (statisticsCollection.isEmpty()) {
            throw new IllegalStateException("No runs have been specified.");
        } else if (objectOperation == null && objects != null && objects.iterator().hasNext()) {
            throw new IllegalStateException("No operation for compounding the objects has been specified.");
        }
        if (objects == null || !objects.iterator().hasNext()) {
            List<R> result = new LinkedList();
            for (Statistics<D> statistics : statisticsCollection) {
                result.add(statistics.get(statistic));
            }
            return result;            
        } else {
            List<R> result = new LinkedList();
            for (Statistics<D> statistics : statisticsCollection) {
                List<R> results = new LinkedList<R>();
                for (O object : objects) {
                    results.add(statistics.get(statistic, object));
                }
                result.add(objectOperation.execute(results, objectParameters));
            }
            return result;
        }
    }

    public List<R> getListPerObject(Iterable<O> objects) {
        if (statisticsCollection.isEmpty()) {
            throw new IllegalStateException("No runs have been specified.");
        } else if (statisticsCollection.size() > 1 && runOperation == null) {
            throw new IllegalStateException("No operation for compounding the runs has been specified.");
        }
        if (objects == null || !objects.iterator().hasNext()) {
            objects = new LinkedList();
            ((LinkedList) objects).add(null);
        }
        if (statisticsCollection.size() == 1) {
            List<R> results = new LinkedList<R>();
            for (O object : objects) {
                results.add(statisticsCollection.getFirst().get(statistic, object));
            }
            return results;
        } else {
            List<R>[] buffer = new List[statisticsCollection.size()];
            int index = 0;
            for (Statistics<D> statistics : statisticsCollection) {
                buffer[index] = new LinkedList<R>();
                for (O object : objects) {
                    buffer[index].add(statistics.get(statistic, object));
                }
                index++;
            }
            List<R>[] samples = new List[buffer[0].size()];
            for (int i = 0; i < buffer[0].size(); i++) {
                samples[i] = new LinkedList<R>();
            }
            for (int i = 0; i < buffer.length; i++) {
                int j = 0;
                for (R r : buffer[i]) {
                    samples[j].add(r);
                    j++;
                }
            }
            List<R> results = new LinkedList<R>();
            for (List<R> sample : samples) {
                results.add(runOperation.execute(sample, runParameters));
            }
            return results;
        }
    }

    public List<List<R>> getListOfLists(Iterable<O> objects) {
        if (statisticsCollection.isEmpty()) {
            throw new IllegalStateException("No runs have been specified.");
        }
        List<List<R>> result = new LinkedList<List<R>>();
        for (Statistics<D> statistics : statisticsCollection) {
            List<R> results = new LinkedList<R>();
            for (O object : objects) {
                results.add(statistics.get(statistic, object));
            }
            result.add(results);
        }
        return result;
    }

    @Override
    public ComplexStatistic<O, R, D> clone() {
        ComplexStatistic<O, R, D> clone = new ComplexStatistic<O, R, D>();
        clone.setObjectOperation(objectOperation);
        clone.setObjectParameters(Arrays.copyOf(objectParameters, objectParameters.length));
        clone.setRunOperation(runOperation);
        clone.setRunParameters(Arrays.copyOf(runParameters, runParameters.length));
        clone.setStatisticsCollection(statisticsCollection);
        clone.setStatistic(statistic);
        return clone;
    }
}
