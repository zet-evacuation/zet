package io.z;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

import ds.z.Assignment;
import ds.z.AssignmentArea;
import ds.z.AssignmentType;
import ds.z.event.ChangeListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

/** A converter that behaves just like a normal converter would do, he only adds
 * the functionality of recreating the changeListeners.
 *
 * @author Timon Kelter
 */
public class AssignmentTypeConverter extends ReflectionConverter {
	private Class myClass = AssignmentType.class;
	
	public AssignmentTypeConverter (Mapper mapper, ReflectionProvider reflectionProvider) {
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
		AssignmentType result = (AssignmentType)serializationMethodInvoker.callReadResolve(created);
		
		// Recreate changeListener list
		for (AssignmentArea t : result.getAssignmentAreas ()) {
			t.addChangeListener (result);
		}
		if (result.getAge () != null) {
			result.getAge ().addChangeListener (result);
		}
		if (result.getDecisiveness () != null) {
			result.getDecisiveness ().addChangeListener (result);
		}
		if (result.getDiameter () != null) {
			result.getDiameter ().addChangeListener (result);
		}
		if (result.getFamiliarity () != null) {
			result.getFamiliarity ().addChangeListener (result);
		}
		if (result.getPanic () != null) {
			result.getPanic ().addChangeListener (result);
		}
		
		return result;
	}
}
