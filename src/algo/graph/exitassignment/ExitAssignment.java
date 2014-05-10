/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * ExitAssignment.java
 *
 */

package algo.graph.exitassignment;

import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.container.mapping.IdentifiableObjectMapping;
import ds.graph.Node;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
public class ExitAssignment extends IdentifiableObjectMapping<Node, List> {

    protected Iterable<Node> domain;
    protected IdentifiableIntegerMapping<Node> evacuees;
    protected int numberOfEvacuees;
    
    public ExitAssignment(Iterable<Node> domain) {
        super(domain );
        this.domain = domain;
        evacuees = new IdentifiableIntegerMapping<Node>(domain);
        numberOfEvacuees = 0;
    }

    public void assignIndividualToExit(Node node, Node exit) {
        if (!isDefinedFor(node)) {
            set(node, new LinkedList<Node>());
        }
        get(node).add(exit);
        evacuees.increase(exit, 1);
        numberOfEvacuees++;
    }        
    
    @Override
    public List<Node> get(Node node) {
        return super.get(node);
    }
    
    public ExitAssignment difference(ExitAssignment exitAssignment) {
        ExitAssignment result = new ExitAssignment(domain);
        for (Node node : domain) {
            if (isDefinedFor(node)) {
                for (Node exit : get(node)) {
                    result.assignIndividualToExit(node, exit);
                }
                if (exitAssignment.isDefinedFor(node)) {
                    result.get(node).removeAll(exitAssignment.get(node));
                }
            }
        }
        return result;
    }
    
    public IdentifiableIntegerMapping<Node> evacuees() {
        return evacuees;
    }
    
    @Override
    public String toString() {
        return numberOfEvacuees + " Individuen evakuiert:\n" + evacuees().toString() + "\n" + super.toString();
    }
}
