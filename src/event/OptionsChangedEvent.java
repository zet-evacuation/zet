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
 * Class OptionsChangedEvent
 * Erstellt 28.10.2008, 00:35:04
 */

package event;

/**
 * An event that is thrown if the program options are changed, so that the
 * values can be reloaded. Especially used for the visualization.
 * @param <S> 
 * @author Jan-Philipp Kappmeier
 */
public class OptionsChangedEvent<S> implements Event {
	protected S source;

	public OptionsChangedEvent( S source ) {
		this.source = source;
	}

	public S getSource() {
		return source;
	}
}
