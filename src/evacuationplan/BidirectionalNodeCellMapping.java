/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

package evacuationplan;

import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCAMapping;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterSquare;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARoomRaster;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterContainer;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRasterSquare;
import de.tu_berlin.math.coga.zet.converter.cellularAutomaton.ZToCARasterContainer;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphRoomRaster;
import de.zet_evakuierung.model.FloorInterface;
import de.zet_evakuierung.model.Room;
import org.zet.cellularautomaton.EvacCell;
import java.util.ArrayList;
import java.util.HashMap;
import org.zetool.graph.Node;


/**
 * Contains a mapping from nodes to cells and a mapping from cells to nodes.
 */
public class BidirectionalNodeCellMapping {
	
	HashMap<Node,ArrayList<EvacCell>> nodeCellMapping;
	HashMap<EvacCell,Node> cellNodeMapping;
	
	public static class CAPartOfMapping{
		
		private ZToCARasterContainer raster;
		private ZToCAMapping rasterSquareToCell;
		
		public CAPartOfMapping(ZToCARasterContainer raster, ZToCAMapping rasterSquareToCell){
			this.raster=raster;
			this.rasterSquareToCell = rasterSquareToCell;
		}
	
	}
	
	public BidirectionalNodeCellMapping(ZToGraphRasterContainer graphRaster, CAPartOfMapping caPartOfMapping){
		ZToCARasterContainer caRaster = caPartOfMapping.raster;
		ZToCAMapping caSquaresToCells = caPartOfMapping.rasterSquareToCell;
		HashMap<Node, ArrayList<ZToGraphRasterSquare>> nodeToGraphSquareMapping = new HashMap<Node, ArrayList<ZToGraphRasterSquare>>();
		HashMap<ZToGraphRasterSquare,ZToCARasterSquare> graphToCARasterSquare = new HashMap<ZToGraphRasterSquare,ZToCARasterSquare>();
		
		for( FloorInterface floor:graphRaster.getFloors()){
			if (floor.getRooms().size() > 0){
				for (Room room:graphRaster.getRooms(floor)){
					ZToGraphRoomRaster graphRasteredRoom = graphRaster.getRasteredRoom(room);
					ZToCARoomRaster caRasteredRoom = caRaster.getRasteredRoom(room);
					for (int x = 0; x < graphRasteredRoom.getColumnCount(); x++){
						for (int y = 0; y < graphRasteredRoom.getRowCount(); y++){
							ZToGraphRasterSquare graphSquare = graphRasteredRoom.getSquare(x, y);
							ZToCARasterSquare caSquare = caRasteredRoom.getSquare(x, y);
							Node node = graphSquare.getNode();
							if (node != null){
								ArrayList<ZToGraphRasterSquare> squaresOfNode;
								if (! nodeToGraphSquareMapping.containsKey(node)){
									squaresOfNode = new ArrayList<ZToGraphRasterSquare>();
									nodeToGraphSquareMapping.put(node, squaresOfNode);
								} else {
									squaresOfNode = nodeToGraphSquareMapping.get(node);
								}
								squaresOfNode.add(graphSquare);
								graphToCARasterSquare.put(graphSquare,caSquare);
							}
						}
					}				
				}
			}
		}
		
		nodeCellMapping = new HashMap<Node,ArrayList<EvacCell>>();
		cellNodeMapping = new HashMap<EvacCell,Node>();
		for (Node node : nodeToGraphSquareMapping.keySet()){
			ArrayList<EvacCell> cellsOfThisNode = new ArrayList<EvacCell>();
			ArrayList<ZToGraphRasterSquare> squaresOfThisNode = nodeToGraphSquareMapping.get(node);
			for (ZToGraphRasterSquare graphSquare : squaresOfThisNode){
				ZToCARasterSquare caSquare = graphToCARasterSquare.get( graphSquare );
				EvacCell cell = caSquaresToCells.get(caSquare);
				cellsOfThisNode.add( cell );
				cellNodeMapping.put( cell, node );
			}
			nodeCellMapping.put( node, cellsOfThisNode );
		}
	}
	
	public ArrayList<EvacCell> getCells(Node node){
		return nodeCellMapping.get(node);
	}
	
	public Node getNode(EvacCell cell){
		return cellNodeMapping.get(cell);
	}

}
