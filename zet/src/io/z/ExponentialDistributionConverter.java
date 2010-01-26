/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
import de.tu_berlin.math.coga.rndutils.distribution.continuous.ExponentialDistribution;


/**
 * A converter that allows to load and store erlang distribution objects. It
 * loads the parameters of the distribution as xml-attributes.
 *
 * @author Jan-Philipp Kappmeier
 */
public class ExponentialDistributionConverter implements Converter {
	private Class myClass = ExponentialDistribution.class;

	@Override
	public boolean canConvert (Class type) {
		return myClass.isAssignableFrom (type);
	}

  /**
   * Writes a node to the XML-file, beginning with writing the start node.
   * @param source
   * @param writer
   * @param context
   */
	@Override
  public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
		ExponentialDistribution dist = (ExponentialDistribution) source;
    writer.addAttribute( "min", new Double(dist.getMin()).toString() );
    writer.addAttribute( "max", new Double(dist.getMax()).toString() );
		writer.addAttribute( "lambda", new Double(dist.getLambda()).toString() );
  }

	@Override
	public Object unmarshal (final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		ExponentialDistribution dist = new ExponentialDistribution();

		final double min = Double.parseDouble( reader.getAttribute( "min" ) );
		final double max = Double.parseDouble( reader.getAttribute( "max" ) );
		final double lambda = Double.parseDouble( reader.getAttribute( "lambda" ) );

		dist.setParameter( min, max );
		dist.setLambda( lambda );
		return dist;
	}
}
