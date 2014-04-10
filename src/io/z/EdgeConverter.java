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

import de.tu_berlin.coga.zet.model.PlanEdge;
import de.tu_berlin.coga.zet.model.RoomEdge;
import de.tu_berlin.coga.zet.model.TeleportEdge;


/**
 * A converter that behaves just like a normal converter would do, it only adds
 * the functionality of recreating the changeListeners.
 *
 * @author Timon Kelter
 */
public class EdgeConverter extends ReflectionConverter {
	private Class myClass = PlanEdge.class;

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

        created = doUnmarshal(created, reader, context);
		PlanEdge result = (PlanEdge)serializationMethodInvoker.callReadResolve(created);

		if (result instanceof RoomEdge) {
			// Recreate transient fields
			reflectionProvider.writeField (created, "ensureMatchWithLinkTarget",
				Boolean.valueOf(!(result instanceof TeleportEdge)), RoomEdge.class);
		}
		if (result instanceof TeleportEdge) {
			// Recreate transient fields
			reflectionProvider.writeField (created, "revertLinkTargetOnDelete",
				Boolean.valueOf( true ), TeleportEdge.class);
		}

		return result;
	}
}
