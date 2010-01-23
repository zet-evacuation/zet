/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * UniformDistributionConverter.java
 * Created 16.12.2009, 12:21:47
 */

package io.z;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import util.random.distributions.UniformDistribution;

/** A converter that behaves just like a normal converter would do, he only adds
 * the functionality of recreating the changeListeners.
 *
 * @author Timon Kelter
 */
public class UniformDistributionConverter extends ReflectionConverter {
	private Class myClass = UniformDistribution.class;

	public UniformDistributionConverter (Mapper mapper, ReflectionProvider reflectionProvider) {
		super (mapper, reflectionProvider);
	}

	public boolean canConvert (Class type) {
		return myClass.isAssignableFrom (type);
	}

  /**
   * Writes a node to the XML-file, beginning with writing the start node.
   * @param source
   * @param writer
   * @param context
   */
  public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
    //PropertyTreeNode node = (PropertyTreeNode) source;
		UniformDistribution dist = (UniformDistribution) source;
//    writer.startNode( "treeNode" );
    writer.addAttribute( "min", new Double(dist.getMin()).toString() );
    writer.addAttribute( "max", new Double(dist.getMax()).toString() );
//    for( int i=0; i < node.getChildCount(); i++ ) {
//      DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt( i );
//      context.convertAnother( child );
//    }
//		for( AbstractPropertyValue property : node.getProperties()	) {
//			if( property instanceof BooleanProperty )
//				context.convertAnother( property, new BooleanPropertyConverter() );
//			else if( property instanceof IntegerRangeProperty )
//				context.convertAnother( property, new IntegerRangePropertyConverter() );
//			else if( property instanceof IntegerProperty )
//				context.convertAnother( property, new IntegerPropertyConverter() );
//			else if( property instanceof DoubleProperty )
//				context.convertAnother( property, new DoublePropertyConverter() );
//			else if( property instanceof StringProperty )
//				context.convertAnother( property, new StringPropertyConverter() );
//			else if( property instanceof StringListProperty )
//				context.convertAnother( property, new StringListPropertyConverter() );
//			else if( property instanceof QualitySettingProperty )
//				context.convertAnother( property, new QualitySettingPropertyConverter() );
//		}
//    writer.endNode();
  }

	public Object unmarshal (final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		//Object created = instantiateNewInstance(reader, context);

		UniformDistribution dist = new UniformDistribution();

		double min = Double.parseDouble( reader.getAttribute( "min" ) );
		double max = Double.parseDouble( reader.getAttribute( "max" ) );

		dist.setParameter( min, max );
		//reflectionProvider.writeField( created, "min", min, myClass );
		// Early recreation of changeListener List neccessary
//		reflectionProvider.writeField (dist, "changeListeners", new ArrayList<ChangeListener> (), myClass);

//        created = doUnmarshal(created, reader, context);
//		Distribution result = (Distribution)serializationMethodInvoker.callReadResolve(created);

		return dist;
		
		//min="0.0" max="1.0"
	}
}
