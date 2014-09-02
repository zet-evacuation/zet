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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.z;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

import de.tu_berlin.coga.zet.model.AssignmentType;
import de.tu_berlin.coga.zet.model.ZControl;

/**
 * A converter that behaves just like a normal converter would do.
 * @author Timon Kelter, Jan-Philipp Kappmeier
 */
public class AssignmentTypeConverter extends ReflectionConverter {
	private Class myClass = AssignmentType.class;

	public AssignmentTypeConverter( Mapper mapper, ReflectionProvider reflectionProvider ) {
		super( mapper, reflectionProvider );
	}

	@Override
	public boolean canConvert( Class type ) {
		return myClass.isAssignableFrom( type );
	}

	@Override
	public Object unmarshal( final HierarchicalStreamReader reader, final UnmarshallingContext context ) {
		Object created = instantiateNewInstance( reader, context );

		created = doUnmarshal( created, reader, context );
		AssignmentType result = (AssignmentType) serializationMethodInvoker.callReadResolve( created );

		// Old projects may not contain a parameter for reaction time. Set it to
		// the default value if it is not present.
		if( result.getReaction() == null )
			result.setReaction( ZControl.getDefaultAssignmentTypeDistribution( "reaction" ) );

		return result;
	}
}
