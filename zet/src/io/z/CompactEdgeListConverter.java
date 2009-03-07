package io.z;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import ds.z.Edge;
import ds.z.PlanPoint;
import ds.z.event.ChangeListener;

import java.util.ArrayList;

/** A converter that converts edge lists without much recursion. Too much recursion
 * will lead to stack overflow errors in case of our linked-list edge structures
 * and this converter avoids there errors.
 *
 * @author Timon Kelter
 */
public class CompactEdgeListConverter extends ReflectionConverter {
	private Mapper mapper;

	public CompactEdgeListConverter (Mapper mapper, ReflectionProvider reflectionProvider) {
		super (mapper, reflectionProvider);
		
		this.mapper = mapper;
	}

	@Override
	public boolean canConvert (Class type) {
		return PlanPoint.class.isAssignableFrom (type);
	}

	@Override
	public void marshal (Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		PlanPoint start = (PlanPoint) source;
		PlanPoint currentPoint = start;
		
		// Seralialize this point
		writer.addAttribute ("x", Integer.toString (start.x));
		writer.addAttribute ("y", Integer.toString (start.y));

		// Write edges in compact list form
		writer.startNode ("edges");
		do {
			writer.startNode (mapper.serializedClass (PlanPoint.class));
			context.convertAnother (currentPoint);
			writer.endNode ();
			writer.startNode (mapper.serializedClass (currentPoint.getNextEdge ().getClass ()));
			context.convertAnother (currentPoint.getNextEdge ());
			writer.endNode ();
			
			currentPoint = currentPoint.getNextEdge ().getTarget ();
		} while (currentPoint.getNextEdge () != null && currentPoint != start);

		// Unclosed polygons are finished with the last planPoint
		if (currentPoint != start) {
			context.convertAnother (currentPoint);
		}

		writer.endNode ();
	}

	@Override
	public Object unmarshal (final HierarchicalStreamReader reader,
			final UnmarshallingContext context) {
		PlanPoint created = (PlanPoint) instantiateNewInstance (reader, context);

		// Early recreation of changeListener List neccessary
		reflectionProvider.writeField (created, "changeListeners",
				new ArrayList<ChangeListener> (), PlanPoint.class);

		created.x = Integer.parseInt (reader.getAttribute ("x"));
		created.y = Integer.parseInt (reader.getAttribute ("y"));
		
		// Initialize
		reader.moveDown ();

		// Get the edges one by one
		Edge currentEdge = null, lastEdge = null;
		PlanPoint currentPoint = null;
		boolean unclosedPolygon = false;
		boolean firstPoint = true;
		while (reader.hasMoreChildren ()) {
			reader.moveDown ();
			// The first point needs extra treatment (we are already converting it)
			if (firstPoint) {
				currentPoint = created;
				firstPoint = false;
			} else {
				currentPoint = (PlanPoint) context.convertAnother (null, PlanPoint.class);
			}
			reader.moveUp ();
			
			if (reader.hasMoreChildren ()) {
				reader.moveDown ();
				Class edgeType = mapper.realClass (reader.getNodeName ());
				currentEdge = (Edge) context.convertAnother (null, edgeType);
				reader.moveUp ();

				// Reinstall the connection between the objects
				if (lastEdge != null) {
					reflectionProvider.writeField (currentPoint, "previousEdge",
							lastEdge, PlanPoint.class);
					reflectionProvider.writeField (lastEdge, "target",
							currentPoint, Edge.class);
				}
				reflectionProvider.writeField (currentPoint, "nextEdge",
						currentEdge, PlanPoint.class);
				reflectionProvider.writeField (currentEdge, "source",
						currentPoint, Edge.class);

				lastEdge = currentEdge;
			} else {
				unclosedPolygon = true;
				currentPoint = (PlanPoint) context.convertAnother (null, PlanPoint.class);
			}
		}
		reader.moveUp ();

		// Finish the polygon
		if (unclosedPolygon) {
			reflectionProvider.writeField (currentEdge, "target",
					currentPoint, Edge.class);
			reflectionProvider.writeField (currentPoint, "previousEdge",
					currentEdge, PlanPoint.class);
		} else {
			reflectionProvider.writeField (created, "previousEdge",
					currentEdge, PlanPoint.class);
			reflectionProvider.writeField (currentEdge, "target",
					created, Edge.class);
		}

		// Iterate over edges - Recreate changeListener lists
		Edge start = created.getNextEdge ();
		currentEdge = start;
		do {
			currentEdge.getSource ().addChangeListener (currentEdge);
			currentEdge.getTarget ().addChangeListener (currentEdge);

			currentEdge = currentEdge.getTarget ().getNextEdge ();
		} while (currentEdge != null && currentEdge != start);

		return created;
	}
}
