/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
package io.z;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;
import de.tu_berlin.coga.zet.model.AssignmentArea;
import de.tu_berlin.coga.zet.model.Barrier;
import de.tu_berlin.coga.zet.model.DelayArea;
import de.tu_berlin.coga.zet.model.Edge;
import de.tu_berlin.coga.zet.model.EvacuationArea;
import de.tu_berlin.coga.zet.model.Floor;
import de.tu_berlin.coga.zet.model.InaccessibleArea;
import de.tu_berlin.coga.zet.model.PlanPoint;
import de.tu_berlin.coga.zet.model.PlanPolygon;
import de.tu_berlin.coga.zet.model.Room;
import de.tu_berlin.coga.zet.model.RoomEdgeA;
import de.tu_berlin.coga.zet.model.SaveArea;
import de.tu_berlin.coga.zet.model.StairArea;
import de.tu_berlin.coga.zet.model.TeleportArea;
import java.util.ArrayList;

/**
 * A converter that reads a polygon from the file. That means the edges and
 * points are read.
 * @author Timon Kelter, Jan-Philipp Kappmeier
 */
public class PlanPolygonConverter extends ReflectionConverter {
	public PlanPolygonConverter( Mapper mapper, ReflectionProvider reflectionProvider ) {
		super( mapper, reflectionProvider );
	}

	@Override
	public boolean canConvert( Class type ) {
		return type.equals( PlanPointConverter.class );
	}

	@Override
	public Object unmarshal( final HierarchicalStreamReader reader, final UnmarshallingContext context ) {
		Object created = instantiateNewInstance( reader, context );

//		PlanPolygon<Edge> t = (PlanPolygon<Edge>)created;
//		
////        // Traverse Tree
//					
//					        //Node supersink = (Node) context.convertAnother(null, Node.class);
//
//            reader.moveDown();
//						System.out.println( "Skip: " + reader.getNodeName() );
//						reflectionProvider.writeField( created, "edgeClassType", RoomEdgeA.class, PlanPolygon.class );
//            reader.moveUp();
////            if ("polygon".equals(reader.getNodeName()))
////            {
////                Polygon polygon = (Polygon)context.convertAnother(myClass, Polygon.class);
////                myClass.addPolygon(polygon);
////            } 
//            reader.moveDown();
//						System.out.println( "Marshal: " + reader.getNodeName() );
//						PlanPoint start = (PlanPoint) context.convertAnother(created, PlanPoint.class, new CompactEdgeListConverter( mapper, reflectionProvider ) );
//            reflectionProvider.writeField( created, "start", start, PlanPolygon.class );
//            reader.moveUp();
//            reader.moveDown();
//						System.out.println( "Marshal: " + reader.getNodeName() );
//						PlanPoint end = (PlanPoint) context.convertAnother(created, PlanPoint.class, new CompactEdgeListConverter( mapper, reflectionProvider ) );
//            reflectionProvider.writeField( created, "end", end, PlanPolygon.class );
//						reader.moveUp();
//
//            reader.moveDown();
//						System.out.println( "Marshal: " + reader.getNodeName() );
//						reflectionProvider.writeField( created, "size", Integer.parseInt( reader.getValue() ), PlanPolygon.class );
//            reader.moveUp();
//
//            reader.moveDown();
//						System.out.println( "Marshal: " + reader.getNodeName() );
//						//PlanPoint intersectionPoint = (PlanPoint) context.convertAnother(created, PlanPoint.class, new CompactEdgeListConverter( mapper, reflectionProvider ) );
//            //reflectionProvider.writeField( created, "intersectionPoint", intersectionPoint, PlanPolygon.class );
//						reader.moveUp();
//					
//            reader.moveDown();
//						System.out.println( "Marshal: " + reader.getNodeName() );
//						Floor associatedFloor = (Floor) context.convertAnother(created, Floor.class );
//            reflectionProvider.writeField( created, "associatedFloor", associatedFloor, Room.class );
//						reader.moveUp();
//						
//            reader.moveDown();
//						System.out.println( "Marshal: " + reader.getNodeName() );
//						ArrayList<AssignmentArea> assignmentAreas = (ArrayList<AssignmentArea>) context.convertAnother(created, ArrayList.class );
//            reflectionProvider.writeField( created, "assignmentAreas", assignmentAreas, Room.class );
//						reader.moveUp();
//
//						reader.moveDown();
//						System.out.println( "Marshal: " + reader.getNodeName() );
//						ArrayList<Barrier> barriers = (ArrayList<Barrier>) context.convertAnother(created, ArrayList.class );
//            reflectionProvider.writeField( created, "barriers", barriers, Room.class );
//						reader.moveUp();
//
//						reader.moveDown();
//						System.out.println( "Marshal: " + reader.getNodeName() );
//						ArrayList<DelayArea> delayAreas = (ArrayList<DelayArea>) context.convertAnother(created, ArrayList.class );
//            reflectionProvider.writeField( created, "delayAreas", delayAreas, Room.class );
//						reader.moveUp();
//
//						reader.moveDown();
//						System.out.println( "Marshal: " + reader.getNodeName() );
//						ArrayList<EvacuationArea> evacuationAreas = (ArrayList<EvacuationArea>) context.convertAnother(created, ArrayList.class );
//            reflectionProvider.writeField( created, "evacuationAreas", evacuationAreas, Room.class );
//						reader.moveUp();
//
//						reader.moveDown();
//						System.out.println( "Marshal: " + reader.getNodeName() );
//						ArrayList<InaccessibleArea> inaccessibleAreas = (ArrayList<InaccessibleArea>) context.convertAnother(created, ArrayList.class );
//            reflectionProvider.writeField( created, "inaccessibleAreas", inaccessibleAreas, Room.class );
//						reader.moveUp();
//
//						reader.moveDown();
//						System.out.println( "Marshal: " + reader.getNodeName() );
//						ArrayList<SaveArea> saveAreas = (ArrayList<SaveArea>) context.convertAnother(created, ArrayList.class );
//            reflectionProvider.writeField( created, "saveAreas", saveAreas, Room.class );
//						reader.moveUp();
//
//						reader.moveDown();
//						System.out.println( "Marshal: " + reader.getNodeName() );
//						ArrayList<StairArea> stairAreas = (ArrayList<StairArea>) context.convertAnother(created, ArrayList.class );
//            reflectionProvider.writeField( created, "stairAreas", stairAreas, Room.class );
//						reader.moveUp();
//
//						reader.moveDown();
//						System.out.println( "Marshal: " + reader.getNodeName() );
//						ArrayList<TeleportArea> teleportAreas = (ArrayList<TeleportArea>) context.convertAnother(created, ArrayList.class );
//            reflectionProvider.writeField( created, "teleportAreas", teleportAreas, Room.class );
//						reader.moveUp();
//
//						
//						
//						
//						while (reader.hasMoreChildren())  {
//            reader.moveDown();
//						System.out.println( "Skip in polygon: " + reader.getNodeName() );
//            reader.moveUp();
//		       }
//		
		
		try {
			created = doUnmarshal( created, reader, context );
		} catch( com.thoughtworks.xstream.converters.ConversionException ex ) {
			System.out.println( "Exception beim Speichern eines PlanPolygons!" );
			ex.printStackTrace( System.err );
		}
		PlanPolygon<Edge> result = (PlanPolygon<Edge>) serializationMethodInvoker.callReadResolve( created );
		
		result.recomputeBounds();
		
		return result;
	}
}
