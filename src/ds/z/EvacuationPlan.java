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
 * EvacuationPlan.java
 * Created on 26. November 2007, 21:32
 */

package ds.z;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** A class for representing evacuation plans. This is only a dummy until now. */
@XStreamAlias("evacuationPlan")
public class EvacuationPlan {
	
	public EvacuationPlan () {
	}
	
	public void delete () {
	}
	
	public boolean equals (Object o) {
		if (o instanceof EvacuationPlan) {
			return true;
		} else {
			return false;
		}
	}
}
