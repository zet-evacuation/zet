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
import ds.z.PlanPolygon;

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
