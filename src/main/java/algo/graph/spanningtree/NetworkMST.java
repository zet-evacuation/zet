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
package algo.graph.spanningtree;

import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.graph.Edge;

/**
 *
 * @author schwengf
 */
public class NetworkMST {
    
    
    private NetworkMSTProblem networkprob;
    private IdentifiableCollection<Edge> Edges ;
    private int overalldist;
    
    public NetworkMST(NetworkMSTProblem networkprob,IdentifiableCollection<Edge> Edges, int overalldist)
    {
        this.networkprob = networkprob;
        this.Edges = Edges;
        this.overalldist = overalldist;
    }
    public NetworkMST()
    {
        
    }    
    public NetworkMSTProblem getProblem()
    {
        return networkprob;
    }
    public IdentifiableCollection<Edge> getEdges()
    {
        return Edges;
    }
    


    
}
