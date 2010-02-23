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
/**
 * Class IntegerRangeProperty
 * Erstellt 11.04.2008, 18:01:12
 */

package gui.editor.properties.types;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import gui.editor.properties.converter.IntegerPropertyConverter;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias( "intRangeNode" )
@XStreamConverter( IntegerPropertyConverter.class )
public class IntegerRangeProperty extends IntegerProperty {
	final JSlider slider = new JSlider();
	int minValue;
	int maxValue;
	int minorTick;
	int majorTick;

	@Override
	public JPanel getPanel() {
		JPanel panel = new JPanel();
		slider.setValue( getValue() );
		slider.setMinimum( minValue );
		slider.setMaximum( maxValue );
		slider.setMinorTickSpacing( minorTick );
		slider.setMajorTickSpacing( majorTick );
		slider.setPaintTicks( true );
		slider.setPaintLabels( true );
		slider.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				updateModel();
			}
		});
		panel.add( slider );
		return panel;
	}	
	
	public int getMajorTick() {
		return majorTick;
	}

	public void setMajorTick( int majorTick ) {
		this.majorTick = majorTick;
	}

	public int getMinorTick() {
		return minorTick;
	}

	public void setMinorTick( int minorTick ) {
		this.minorTick = minorTick;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue( int maxValue ) {
		this.maxValue = maxValue;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue( int minValue ) {
		this.minValue = minValue;
	}
	
	/**
	 * 
	 */
	@Override
	protected void updateModel() {
		setValue( slider.getValue() );
	}
}
