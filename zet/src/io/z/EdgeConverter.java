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

import ds.z.Edge;
import ds.z.RoomEdge;
import ds.z.TeleportEdge;


/** A converter that behaves just like a normal converter would do, he only adds
 * the functionality of recreating the changeListeners.
 *
 * @author Timon Kelter
 */
public class EdgeConverter extends ReflectionConverter {
	private Class myClass = Edge.class;
	
	public EdgeConverter (Mapper mapper, ReflectionProvider reflectionProvider) {
		super (mapper, reflectionProvider);
	}
	
	@Override
	public boolean canConvert (Class type) {
		return myClass.isAssignableFrom (type);
	}
	
	@Override
	public Object unmarshal (final HierarchicalStreamReader reader, 
			final UnmarshallingContext context) {
		Object created = instantiateNewInstance(reader, context);
		
		// Early recreation of changeListener List neccessary
///		reflectionProvider.writeField (created, "changeListeners", new ArrayList<ChangeListener> (), myClass);
		
        created = doUnmarshal(created, reader, context);
		Edge result = (Edge)serializationMethodInvoker.callReadResolve(created);
				
		if (result instanceof RoomEdge) {
			// Recreate transient fields
			reflectionProvider.writeField (created, "ensureMatchWithLinkTarget", 
				new Boolean (!(result instanceof TeleportEdge)), RoomEdge.class);
		}
		if (result instanceof TeleportEdge) {
			// Recreate transient fields
			reflectionProvider.writeField (created, "revertLinkTargetOnDelete", 
				new Boolean (true), TeleportEdge.class);
		}
		
		return result;
	}
}