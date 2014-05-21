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

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import de.tu_berlin.coga.zet.model.Room;
import de.tu_berlin.coga.zet.model.RoomImpl;
import de.tu_berlin.coga.zet.model.StairArea;
import de.tu_berlin.coga.zet.model.TeleportArea;
import java.util.ArrayList;

/**
 * A converter that reads a room from the z xml format.
 * @author Jan-Philipp Kappmeier
 */
public class RoomConverter extends PlanPolygonConverter {

	/**
	 * The writing of the file just works as default. Only super classes are called.
	 * @param mapper
	 * @param reflectionProvider
	 */
	public RoomConverter( Mapper mapper, ReflectionProvider reflectionProvider ) {
		super( mapper, reflectionProvider );
	}

	/**
	 * Checks whether an object is of the type {@link de.tu_berlin.coga.zet.model.Room} and thus can be
	 * converted by this class.
	 * @param type the object
	 * @return {@code true} if the object can be converted
	 */
	@Override
	public boolean canConvert( Class type ) {
		return type.equals( RoomImpl.class );
	}

	@Override
	public void marshal( Object original, HierarchicalStreamWriter writer, MarshallingContext context ) {
		super.marshal( original, writer, context );
	}

	/**
	 * Allows reading an {@link de.tu_berlin.coga.zet.model.Room} class. Due to format extensions,
	 * needed lists for stair areas and teleport areas are created if they are
	 * missing. Also, a convenient-copy of all lists in an array is created as it
	 * is not stored in the file.
	 * @param reader the reader
	 * @param context the context
	 * @return the created {@code Room} instance
	 */
	@Override
	public Object unmarshal( final HierarchicalStreamReader reader, final UnmarshallingContext context ) {
		RoomImpl room = (RoomImpl) super.unmarshal( reader, context );

		Class<?> c = room.getClass();
		java.lang.reflect.Field field;

		try {
			// We need to be a little tricky here and have to access the fields using
			// the reflecitons API because the methods return Unmodifiable lists, thus
			// get... would throw a null pointer exception
			field = c.getDeclaredField( "teleportAreas" );
			field.setAccessible( true );
			if( field.get( room ) == null )
				field.set( room, new ArrayList<TeleportArea>() );
			field = c.getDeclaredField( "stairAreas" );
			field.setAccessible( true );
			if( field.get( room ) == null )
				field.set( room, new ArrayList<StairArea>() );
		} catch( NoSuchFieldException ex ) {
			System.err.println( "NoSuchFieldException in RoomConverter" );
		} catch( IllegalAccessException ex ) {
			System.err.println( "IllegalAccessException in RoomConverter" );
		}

		// Set up the Array for the areas
		try {
			String[] areaNames = new String[]{"assignmentAreas", "barriers", "delayAreas", "evacuationAreas", "inaccessibleAreas", "saveAreas", "stairAreas", "teleportAreas" };
			ArrayList<?>[] areas = new ArrayList<?>[areaNames.length]; // Note, that all of them are arraylists!
			for( int i = 0; i < areaNames.length; ++i) {
				field = c.getDeclaredField( areaNames[i] );
				field.setAccessible( true );
				areas[i] = (ArrayList<?>) field.get( room );				
			}
			field = c.getDeclaredField( "areas" );
			field.setAccessible( true );
			field.set( room, areas );

			// check for errors
			for( ArrayList<?> al : areas )
				for( Object a : al ) {
					if( a == null ) {
						room.repair();
						break;
					}
			}
		} catch( NoSuchFieldException ex ) {
			System.err.println( "NoSuchFieldException in RoomConverter" );
		} catch( IllegalAccessException ex ) {
			System.err.println( "IllegalAccessException in RoomConverter" );
		}
		return room;
	}
}
