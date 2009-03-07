package io.z;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;
import ds.z.Edge;

import util.random.distributions.Distribution;
import ds.z.event.ChangeListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

/** A converter that behaves just like a normal converter would do, he only adds
 * the functionality of recreating the changeListeners.
 *
 * @author Timon Kelter
 */
public class DistributionConverter extends ReflectionConverter {
	private Class myClass = Distribution.class;
	
	public DistributionConverter (Mapper mapper, ReflectionProvider reflectionProvider) {
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
		Distribution result = (Distribution)serializationMethodInvoker.callReadResolve(created);
		
		return result;
	}
}
