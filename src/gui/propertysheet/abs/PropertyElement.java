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
 * Interface PropertyElement
 * Created 09.04.2008, 21:29:41
 */
package gui.propertysheet.abs;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface PropertyElement {
	
	public boolean isUsedAsLocString();
	
	public void useAsLocString( boolean useAsLocString );
	
	public String getDisplayName();
	
	public String getDisplayNameTag();
	
	public void setDisplayName( String name );
}
