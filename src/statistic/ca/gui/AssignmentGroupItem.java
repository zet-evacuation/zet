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
