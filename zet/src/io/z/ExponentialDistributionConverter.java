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

/*
 * NormalDistributionConverter.java
 * Created 26.01.2010, 18:48:37
 */
package io.z;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import de.tu_berlin.math.coga.rndutils.distribution.continuous.ErlangDistribution;
import de.tu_berlin.math.coga.rndutils.distribution.continuous.ExponentialDistribution;

/**
 * A converter that allows to load and store {@link ExponentialDistribution}
 * objects. It loads the parameters of the distribution as xml-attributes.
 * @author Jan-Philipp Kappmeier
 */
public class ExponentialDistributionConverter implements Converter {
	/**
	 * <p>Checks wheather an object can be converted by this class, that is if it
	 * is of the type {@link ExponentialDistribution}</p>
	 * {@inheritDoc}
	 * @param type the type of the object that is checked
	 * @return true if this converter can convert an object of the given type
	 */
	@Override
	public boolean canConvert( Class type ) {
		return ExponentialDistribution.class.isAssignableFrom( type );
	}

	/**
	 * <p>Writes the attributes of the {@link ExponentialDistribution} to the
	 * xml tag. Written attributes are min, max and lambda.</p>
	 * {@inheritDoc}
	 * @param source the source which is saved
	 * @param writer the file writer
	 * @param context the current marshalling context.
	 */
	@Override
	public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
		final ExponentialDistribution dist = (ExponentialDistribution)source;
		writer.addAttribute( "min", new Double( dist.getMin() ).toString() );
		writer.addAttribute( "max", new Double( dist.getMax() ).toString() );
		writer.addAttribute( "lambda", new Double( dist.getLambda() ).toString() );
	}

	/**
	 * <p>Reads the attributes of an {@link ExponentialDistribution} and
	 * creates the object instance. Readed attributes are min, max and lambda1.</p>
	 * {inheritDoc}
	 * @param reader the reader for the xml input stream
	 * @param context the current marshalling context
	 * @return the new instance of {@link ExponentialDistribution}
	 */
	@Override
	public Object unmarshal( final HierarchicalStreamReader reader, final UnmarshallingContext context ) {
		ExponentialDistribution dist = new ExponentialDistribution( Double.parseDouble( reader.getAttribute( "lambda" ) ) );
		dist.setParameter( Double.parseDouble( reader.getAttribute( "min" ) ), Double.parseDouble( reader.getAttribute( "max" ) ) );
		return dist;
	}
}
