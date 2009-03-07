package io.z;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;
import ds.z.Edge;

import ds.z.Floor;
import ds.z.Room;
import ds.z.event.ChangeListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

/** A converter that behaves just like a normal converter would do, he only adds
 * the functionality of recreating the changeListeners.
 *
 * @author Timon Kelter
 */
public class FloorConverter extends ReflectionConverter {
	private Class myClass = Floor.class;
	
	public FloorConverter (Mapper mapper, ReflectionProvider reflectionProvider) {
		super (mapper, reflectionProvider);
	}
	
	public boolean canConvert (Class type) {
		return myClass.isAssignableFrom (type);
	}
	
	public Object unmarshal (final HierarchicalStreamReader reader, 
			final UnmarshallingContext context) {
		Object created = instantiateNewInstance(reader, context);
		
		// Early recreation of changeListener List neccessary
		reflectionProvider.writeField (created, "changeListeners", 
			new ArrayList<ChangeListener> (), myClass);
		
        created = doUnmarshal(created, reader, context);
		Floor result = (Floor)serializationMethodInvoker.callReadResolve(created);
		
		// Recreate changeListener list
		for (Room t : result.getRooms ()) {
			t.addChangeListener (result);
		}
		
		// Recreate transient flag
		reflectionProvider.writeField (result, "enableEventGeneration",
				new Boolean (true), myClass);
		
		// Legacy support for old example files
		if (!result.boundStructureAvailable ()) {
			result.recomputeBounds ();
		}
		
		return result;
	}
}
