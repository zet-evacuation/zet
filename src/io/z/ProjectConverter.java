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

import de.tu_berlin.coga.zet.model.Project;
import ds.VisualProperties;
import de.tu_berlin.coga.zet.model.Assignment;
import de.tu_berlin.coga.zet.model.Floor;
import de.tu_berlin.coga.zet.model.Room;
import de.tu_berlin.coga.zet.model.TeleportArea;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A converter that behaves just like a normal converter would do, he only adds
 * the functionality of recreating the changeListeners.
 *
 * @author Timon Kelter, Jan-Philipp Kappmeier
 */
public class ProjectConverter extends ReflectionConverter {

	private Class myClass = Project.class;

	public ProjectConverter( Mapper mapper, ReflectionProvider reflectionProvider ) {
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
		Project result = (Project) serializationMethodInvoker.callReadResolve( created );

		// Check if project is old version and does not contain visual properties
		if( result.getVisualProperties() == null ) {
			System.err.println( "VisualProperties recreated for project." );
			result.setVisualProperties( new VisualProperties() );
		}

		// Set correct value for targetAreas
		boolean teleportArea = false;
		for( Floor f : result.getBuildingPlan().getFloors() )
			for( Room r : f )
				try {
					final java.lang.reflect.Field field = Room.class.getDeclaredField( "teleportAreas" );
					field.setAccessible( true );
					if( field.get( r ) == null ) {
						field.set( r, new ArrayList<TeleportArea>() );
						teleportArea = true;
					}
				} catch( IllegalArgumentException ex ) {
					Logger.getLogger( RoomConverter.class.getName() ).log( Level.SEVERE, null, ex );
				} catch( IllegalAccessException ex ) {
					Logger.getLogger( RoomConverter.class.getName() ).log( Level.SEVERE, null, ex );
				} catch( NoSuchFieldException ex ) {
					Logger.getLogger( RoomConverter.class.getName() ).log( Level.SEVERE, null, ex );
				} catch( SecurityException ex ) {
					Logger.getLogger( RoomConverter.class.getName() ).log( Level.SEVERE, null, ex );
				}

		if( teleportArea )
			System.err.println( "TeleportArea recreated for project." );

		// Check for inconsistencies:
		for( Assignment a : result.getAssignments() ) {
			if ( a == null ) {
				System.err.println( "Assignment null" );
				while( result.getAssignments().contains( null ) )
					result.deleteAssignment( null );
			}
		}
		
		return result;
	}
}
