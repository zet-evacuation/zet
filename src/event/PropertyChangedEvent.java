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
/**
 * Class PropertyChangedEvent
 * Erstellt 19.06.2008, 10:16:20
 */

package event;

/**
 *
 * @param <S> 
 * @author Jan-Philipp Kappmeier
 */
public class PropertyChangedEvent<S> implements Event {
	public enum MessageType {
		/** Indicates that the options in the Options Dialog were changed. */
		Options,
		/** Indicates that the options in the Properties Dialog were changed. */
		Properties;
	}
	protected S source;
	private String msg;
	private MessageType type;

	public PropertyChangedEvent( S source, MessageType type, String msg ) {
		this.source = source;
		this.msg = msg;
		this.type = type;
	}

	public String getMessage() {
		return msg;
	}

	public S getSource() {
		return source;
	}

	public MessageType getType() {
		return type;
	}
}
