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
package statistic.ca;

import statistic.common.Data;

/**
 * @author Daniel R. Schmidt
 *
 */
public class CAData extends Data {
    
    private CellStatistic cellStatistic;
    private IndividualStatistic individualStatistic;
    private CAStatistic caStatistic;
    
    public CAData(CellStatistic cellStatistic, 
            IndividualStatistic individualStatistic, 
            CAStatistic caStatistic
    ){
        this.cellStatistic = cellStatistic;
        this.individualStatistic = individualStatistic;
        this.caStatistic = caStatistic;
    }
    
    public CellStatistic getCellStatistic(){
        return cellStatistic;
    }
    
    public IndividualStatistic getIndividualStatistic(){
        return individualStatistic;
    }
    
    public CAStatistic getCAStatistic(){
        return caStatistic;
    }
}
