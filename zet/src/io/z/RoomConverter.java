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
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;
import ds.z.Area;

import ds.z.Room;
import ds.z.StairArea;
import ds.z.TeleportArea;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A converter that behaves just like a normal converter would do, he only adds
 * the functionality of recreating the changeListeners.
 *
 * @author Timon Kelter, Jan-Philipp Kappmeier
 */
public class RoomConverter extends PlanPolygonConverter {
	/** The class that this converter can convert. */
	private Class myClass = Room.class;

	/**
	 * 
	 * @param mapper
	 * @param reflectionProvider
	 */
	public RoomConverter( Mapper mapper, ReflectionProvider reflectionProvider ) {
		super( mapper, reflectionProvider );
	}

	/**
	 * Checks whether this converter can convert an object of a speciied class, that
	 * means is testet if the class is of type {@link ds.z.Room}.
	 * @param type the object
	 * @return true if the object can be convertet
	 */
	@Override
	public boolean canConvert( Class type ) {
		return myClass.equals( type );
	}

	/**
	 * Allows reading an {@link ds.z.Room} class. During the process all
	 * change listeners are recreated.
	 * @param reader the reader
	 * @param context the context
	 * @return the created <code>Room</code> instance
	 */
	@Override
	public Object unmarshal( final HierarchicalStreamReader reader, final UnmarshallingContext context ) {
		Room result = (Room) super.unmarshal( reader, context );
		Class<?> c = result.getClass();

		java.lang.reflect.Field field;
		try {
			field = c.getDeclaredField( "teleportAreas" );
			field.setAccessible( true );
			field.set( result, new ArrayList<TeleportArea>() );
		} catch( IllegalArgumentException ex ) {
			Logger.getLogger( RoomConverter.class.getName() ).log( Level.SEVERE, null, ex );
		} catch( IllegalAccessException ex ) {
			Logger.getLogger( RoomConverter.class.getName() ).log( Level.SEVERE, null, ex );
		} catch( NoSuchFieldException ex ) {
			Logger.getLogger( RoomConverter.class.getName() ).log( Level.SEVERE, null, ex );
		} catch( SecurityException ex ) {
			Logger.getLogger( RoomConverter.class.getName() ).log( Level.SEVERE, null, ex );
		}

		for( Area t : result.getAssignmentAreas() ) {
//			t.addChangeListener( result );
		}
		for( Area t : result.getBarriers() ) {
//			t.addChangeListener( result );
		}
		for( Area t : result.getDelayAreas() ) {
//			t.addChangeListener( result );
		}
		for( Area t : result.getInaccessibleAreas() ) {
//			t.addChangeListener( result );
		}
		for( Area t : result.getSaveAreas() ) { // Evacuation areas are contained!
//			t.addChangeListener( result );
		}

		try {
			for( Area t : result.getStairAreas() ) {
//				t.addChangeListener( result );
			}
		} catch( Exception ex ) {
			try {
				field = c.getDeclaredField( "stairAreas" );
				field.setAccessible( true );
				field.set( result, new ArrayList<StairArea>() );
				for( Area t : result.getStairAreas() ) {
//					t.addChangeListener( result );
				}
			} catch( NoSuchFieldException ex1 ) {
				System.err.println( "NoSuchFieldException in RoomConverter" );
			} catch( SecurityException ex1 ) {
				System.err.println( "SecurityException in RoomConverter" );
			} catch( IllegalArgumentException ex1 ) {
				System.err.println( "IllegalArgumentException in RoomConverter" );
			} catch( IllegalAccessException ex1 ) {
				System.err.println( "IllegalAccessException in RoomConverter" );
			}
		}

		return result;
	}
}
