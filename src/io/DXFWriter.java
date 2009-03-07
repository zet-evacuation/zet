package io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import ds.z.Barrier;
import ds.z.BuildingPlan;
import ds.z.Edge;
import ds.z.Floor;
import ds.z.InaccessibleArea;
import ds.z.Room;
import ds.z.RoomEdge;
import util.DebugFlags;

/**
 * This class provides a static method to export a building plan  
 * into a dxf file.
 */
public class DXFWriter {
	
	private static int floorHeight = 2500;
	
	/**
	 * Returns a String containing the <code>edge</code> as a dxf line.
	 * @param edge the edge to be converted.
	 * @return the edge converted into a dxf line.
	 */
	private static String edgeToLine(Edge edge, int floorNr, int color){
		String output = "";
		output += "0\nLINE\n";
		output += "8\n"+floorNr+"\n";
		output += "62\n";
		output += color+"\n";
		output += "10\n";
		output += edge.getSource().x+"\n";
		output += "20\n";
		output += (-edge.getSource().y)+"\n";
		output += "30\n";
		output += floorNr*floorHeight + "\n";
		output += "11\n";
		output += edge.getTarget().x+"\n";
		output += "21\n";
		output += (-edge.getTarget().y)+"\n";
		output += "31\n";
		output += floorNr*floorHeight + "\n";
		return output;
	}
	
	
	/**
	 * Exports the given <code>BuildingPlan</code> object into
	 * a dxf file with the name <code>file</code>.
	 * @param file the name of the file to export into.
	 * @param buildingPlan the building plan to be exported.
	 */
	public static void exportIntoDXF(String file, BuildingPlan buildingPlan) throws IOException{
		String output = "0\nSECTION\n2\n";
		output += "ENTITIES\n";
		int floorNr = 0;
		for (Floor floor : buildingPlan.getFloors()){
			for (Room room : floor.getRooms()){
				for (RoomEdge edge : room.getEdges()){
					if (edge.isPassable()){
						output += edgeToLine(edge, floorNr,5);
					} else {
						output += edgeToLine(edge, floorNr,0);
					}
						
				}
				
				for (InaccessibleArea iArea : room.getInaccessibleAreas()){
					for (Edge edge : iArea.getEdges()){
						output += edgeToLine(edge, floorNr,0);
					}					
				}
				
				for (Barrier barrier : room.getBarriers()){
					for (Edge edge : barrier.getEdges()){
						output += edgeToLine(edge, floorNr,0);
					}
				}
			}
			floorNr++;
		}
		
		output += "0\nENDSEC\n";
		output += "0\nEOF\n";
		if (DebugFlags.DXF){
			System.out.println(output);
		}

	   BufferedWriter outputWriter = new BufferedWriter(new FileWriter(file));
	   outputWriter.write(output);
	   outputWriter.flush();
	   outputWriter.close();
	}

}
