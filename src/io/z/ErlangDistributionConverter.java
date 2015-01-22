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
 * Created 26.01.2010, 18:48:26
 */
package io.z;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.zetool.rndutils.distribution.continuous.ErlangDistribution;

/**
 * A converter that allows to load and store {@link ErlangDistribution}
 * objects. It loads the parameters of the distribution as xml-attributes.
 * @author Jan-Philipp Kappmeier
 */
public class ErlangDistributionConverter implements Converter {
	/**
	 * <p>Checks wheather an object can be converted by this class, that is if it
	 * is of the type {@link ErlangDistribution}</p>
	 * {@inheritDoc}
	 * @param type the type of the object that is checked
	 * @return true if this converter can convert an object of the given type
	 */
	@Override
	public boolean canConvert( Class type ) {
		return ErlangDistribution.class.isAssignableFrom( type );
	}

	/**
	 * <p>Writes the attributes of the {@link ErlangDistribution} to the
	 * xml tag. Written attributes are min, max, lambda and k.</p>
	 * {@inheritDoc}
	 * @param source the source which is saved
	 * @param writer the file writer
	 * @param context the current marshalling context.
	 */
	@Override
	public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
		final ErlangDistribution dist = (ErlangDistribution)source;
		writer.addAttribute( "min", new Double( dist.getMin() ).toString() );
		writer.addAttribute( "max", new Double( dist.getMax() ).toString() );
		writer.addAttribute( "lambda", new Double( dist.getLambda() ).toString() );
		writer.addAttribute( "k", new Integer( dist.getK() ).toString() );
	}

	/**
	 * <p>Reads the attributes of an {@link ErlangDistribution} and creates the
	 * object instance. Readed attributes are min, max lambda and k.</p>
	 * {inheritDoc}
	 * @param reader the reader for the xml input stream
	 * @param context the current marshalling context
	 * @return the new instance of {@link ErlangDistribution}
	 */
	@Override
	public Object unmarshal( final HierarchicalStreamReader reader, final UnmarshallingContext context ) {
		ErlangDistribution dist = new ErlangDistribution( Double.parseDouble( reader.getAttribute( "lambda" ) ), Integer.parseInt( reader.getAttribute( "k" ) ) );
		dist.setParameter( Double.parseDouble( reader.getAttribute( "min" ) ), Double.parseDouble( reader.getAttribute( "max" ) ) );
		return dist;
	}
}
