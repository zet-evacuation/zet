package io.z;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

import ds.z.BuildingPlan;
import ds.z.Floor;
import ds.z.event.ChangeListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

/** A converter that behaves just like a normal converter would do, he only adds
 * the functionality of recreating the changeListeners.
 *
 * @author Timon Kelter
 */
public class BuildingPlanConverter extends ReflectionConverter {
	private Class myClass = BuildingPlan.class;
	
	public BuildingPlanConverter (Mapper mapper, ReflectionProvider reflectionProvider) {
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
		// Recreate empty implicit lists
		try {
			if (reflectionProvider.getField (myClass, "floors").get (created) == null) {
				reflectionProvider.writeField (created, "floors", 
					new ArrayList<Floor> (), myClass);
			}
		} catch (IllegalAccessException ex) {
			ex.printStackTrace ();
		}
		
        created = doUnmarshal(created, reader, context);
		BuildingPlan result = (BuildingPlan)serializationMethodInvoker.callReadResolve(created);
				
		// Recreate changeListener list
		for (Floor t : result.getFloors ()) {
			t.addChangeListener (result);
		}
		
		return result;
	}
}
