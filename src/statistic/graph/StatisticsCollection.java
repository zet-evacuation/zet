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
/*
 * StatisticsCollection.java
 *
 */
package statistic.graph;

import java.util.LinkedList;
import java.util.List;

import statistic.common.Data;

/**
 *
 * @author Martin Groß
 */
public class StatisticsCollection<D extends Data> extends LinkedList<Statistics<D>> {

    private List<String> names;

    public StatisticsCollection() {
        super();
        names = new LinkedList<String>();
    }

    @Override
    public boolean add(Statistics e) {
        names.add("Durchlauf " + (size() + 1));
        return super.add(e);
    }
    
    public List<String> getNames() {
        return names;
    }

    /*
    public boolean add(Statistics statistic) {
        statisticsList.add(statistic);
    }
/*
    public <O, R> List<R> get(Statistic<O, R> statistic) {
        List<R> result = new LinkedList<R>();
        for (Statistics statistics : statisticsList) {
            result.add(statistics.get(statistic));
        }
        return result;
    }

    public <O, R> List<R> get(Statistic<O, R> statistic, O object) {
        List<R> result = new LinkedList<R>();
        for (Statistics statistics : statisticsList) {
            result.add(statistics.get(statistic, object));
        }
        return result;
    }

    public <O, R> R get(Statistic<O, R> statistic, Operation<R> operation, Object... parameters) {
        return operation.execute(get(statistic), parameters);
    }

    public <O, R> R get(Statistic<O, R> statistic, O object, Operation<R> operation, Object... parameters) {
        return operation.execute(get(statistic, object), parameters);
    }*/
}
