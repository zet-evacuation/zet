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
/*
 * Created on 23.01.2008
 *
 */
package algo.ca.rule;

/**
 * @author Daniel Pluempe
 *
 */
public abstract class AbstractReactionRule extends AbstractRule {
	@Override
	public boolean executableOn( ds.ca.evac.EvacCell cell ) {
		return cell.getIndividual() == null ? false : !cell.getIndividual().isAlarmed();
	}
}
