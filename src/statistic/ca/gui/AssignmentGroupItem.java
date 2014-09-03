/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
package statistic.ca.gui;

import java.util.ArrayList;
import java.util.UUID;

/**
 * 
 * @author Matthias Woste
 *
 */

public class AssignmentGroupItem {

	private ArrayList<String> types;
	private ArrayList<UUID> uuids;
	
	public AssignmentGroupItem(){
		types = new ArrayList<String>();
		uuids = new ArrayList<UUID>();
	}
	
	public AssignmentGroupItem(String s, UUID u){
		types = new ArrayList<String>();
		uuids = new ArrayList<UUID>();
		types.add(s);
		uuids.add(u);
	}
	
	public void addItem(String s, UUID u){
		types.add(s);
		uuids.add(u);
	}
	
	public void addItem(ArrayList<String> sList, ArrayList<UUID> uList){
		types.addAll(sList);
		uuids.addAll(uList);
	}
	
	public void removeItem(String s){
		types.remove(types.indexOf(s));
		uuids.remove(types.indexOf(s));
	}
	
	public ArrayList<String> getAssignmentTypes(){
		return types;
	}
	
	public ArrayList<UUID> getAssignmentUUIDs(){
		return uuids;
	}
	
	public String toString(){
		String r = "";
		for(String s : types){
			r += s + ", ";
		}
		return r.substring(0, r.length()-2);
	}
	
}
