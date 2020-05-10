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
package algo.graph.nashflow;

import java.util.ArrayList;
import org.zetool.graph.Node;
import org.zetool.container.mapping.IdentifiableDoubleMapping;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import org.zetool.container.collection.ListSequence;

/**
 *
 * @author Sebastian Schenker
 */
public class NodePartition {

    private List<Node>[] partitionArray;
    private HashMap<Node,Integer> nodeToArrayPosition;



    public NodePartition(ListSequence<Node> nodeList, IdentifiableDoubleMapping<Node> thinflowNodelabels ) {
        HashMap< Double, List<Node> > helperMap = new HashMap(nodeList.size());
        ArrayList<Double> helperList;

        //einfuegen in hashmap mit numerischen problemen?

        for(Node n: nodeList) {
            helperMap.put(thinflowNodelabels.get(n),new ArrayList());

        }


        helperList = new ArrayList(helperMap.keySet());

        Collections.sort(helperList);

        for(Node n: nodeList) {
            (helperMap.get(thinflowNodelabels.get(n))).add(n);
        }

        partitionArray = new List[helperList.size()];
        nodeToArrayPosition = new HashMap(nodeList.size());
        int i=0;
        for(Double d: helperList) {
            partitionArray[i] = helperMap.get(d);

            for(Node n: partitionArray[i])
                nodeToArrayPosition.put(n, i);

            i++;

        }

    }

    public List<Node>[] getPartitionArray() {
        return partitionArray;
    }

    public HashMap<Node,Integer> getNodeToArrayPosition() {
        return nodeToArrayPosition;
    }

    public int getNodePosition(Node n) {
        return nodeToArrayPosition.get(n);
    }

    public void printArray(){
        for(int i=0;i<partitionArray.length;i++) {
            for(Node n : partitionArray[i])
                System.out.print(n + " ");
            System.out.println();
        }
    }
}
