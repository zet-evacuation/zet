package io.z;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;
import ds.z.Edge;

import ds.z.PlanPolygon;
import ds.z.event.ChangeListener;

import java.util.ArrayList;

/** A converter that behaves just like a normal converter would do, he only adds
 * the functionality of recreating the changeListeners.
 *
 * @author Timon Kelter
 */
public class PlanPolygonConverter extends ReflectionConverter {

	private Class myClass = PlanPolygon.class;

	public PlanPolygonConverter (Mapper mapper, ReflectionProvider reflectionProvider) {
		super (mapper, reflectionProvider);
	}

	@Override
	public boolean canConvert (Class type) {
		return myClass.isAssignableFrom (type);
	}

	@Override
	public Object unmarshal (final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		Object created = instantiateNewInstance (reader, context);

		// Early recreation of changeListener List neccessary
		reflectionProvider.writeField (created, "changeListeners",
				new ArrayList<ChangeListener> (), myClass);

		try {
			created = doUnmarshal (created, reader, context);
		} catch (com.thoughtworks.xstream.converters.ConversionException ex) {
			System.out.println ("Exception beim Speichern eines PlanPolygons!");
		}
		PlanPolygon<Edge> result = (PlanPolygon<Edge>) serializationMethodInvoker.callReadResolve (created);

		// Recreate changeListener list
		for (Edge t : result.getEdges ()) {
			t.addChangeListener (result);
		}

		// Recreate transient flag
		reflectionProvider.writeField (result, "enableEventGeneration",
				new Boolean (true), myClass);

		return result;
	}
}
