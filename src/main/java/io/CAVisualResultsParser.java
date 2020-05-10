/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.DoorCell;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.InitialConfiguration;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.RoomCell;
import org.zet.cellularautomaton.SaveCell;
import org.zet.cellularautomaton.StairCell;
import org.zet.cellularautomaton.results.ExitAction;
import org.zet.cellularautomaton.results.MoveAction;
import org.zet.cellularautomaton.results.EvacuationRecording;

/**
 * This class serializes and deserializes {@code EvacuationRecording}
 * objects to XML files. Use this class to store simulation results to disc.
 * 
 * The (de-)serializing is done via the XStream-library. 
 * @author Daniel R. Schmidt
 *
 */

public class CAVisualResultsParser {
    
    /**
     * This converts classes to XML-strings
     */
    private XStream xstream;
    
    /**
     * Creates a new parser/file writer. We also set up the
     * XML format here.
     */
    public CAVisualResultsParser(){
        xstream = new XStream();

        xstream.setMode(XStream.ID_REFERENCES);
        
        xstream.alias("Visual Results", EvacuationRecording.class);
        xstream.alias("Initial Configuration", InitialConfiguration.class);
        xstream.alias("Room", Room.class);
        xstream.alias("Room Cell", RoomCell.class);
        xstream.alias("Door Cell", DoorCell.class);
        xstream.alias("Save Cell", SaveCell.class);
        xstream.alias("Exit Cell", ExitCell.class);
        xstream.alias("Stair Cell", StairCell.class);
        xstream.alias("Move", MoveAction.class);
        xstream.alias("Exit", ExitAction.class);
        
        xstream.addImplicitCollection(InitialConfiguration.class, "rooms");
        xstream.addImplicitCollection(Room.class, "doors");
        xstream.addImplicitCollection(Room.class, "cells");
        
        xstream.useAttributeFor(DoorCell.class, "nextDoor");
        xstream.useAttributeFor(EvacCell.class, "speedFactor");
        xstream.useAttributeFor(EvacCell.class, "x");
        xstream.useAttributeFor(EvacCell.class, "y");
        xstream.useAttributeFor(EvacCell.class, "room");
        
        xstream.omitField(EvacuationRecording.class, "curTime");
    }
    
    /**
     * Converts a {@code VisualResultRecording} to an XML string and 
     * saves it in a file.
     * @param recording The recording you want to store
     * @param file The file in which to store the object for later
     * deserialization.
     * @throws IOException Throws an exception if the file cannot be
     *  written or if it already exists.
     */
    public void writeToFile(EvacuationRecording recording, File file) throws IOException{
        String xmlString = recordingToXML(recording);
        System.out.println(xmlString);
        writeString(xmlString, file);
    }
    
    /**
     * Reads a serialized EvacuationRecording from an XML file and 
 deserializes it.
     * @param file  The XML file with the serialized object
     * @return The deserializes object
     * @throws IOException Throws an exception if the file cannot be found or
     * cannot be read.
     */
    public EvacuationRecording readFromFile(File file) throws IOException{
        String xmlString = readString(file);
        return xmlToRecording(xmlString);
    }
    
    /**
     * Re-builds a {@code EvacuationRecording} from an XML string
     * @param xmlStr An XML string with a serialized 
     * {@code EvacuationRecording} object
     * 
     * @return The deserialized object.
     */
    private EvacuationRecording xmlToRecording(String xmlStr){
        return (EvacuationRecording)xstream.fromXML(xmlStr); 
    }
    
    /**
     * Reads the content of a file to a string.
     * @param file The file with the content you want to read
     * @return The content of the file in a string
     * @throws IOException Throws an exception if the file is not found
     * or cannot be read.
     */
    private String readString(File file) throws IOException{
        BufferedReader input = new BufferedReader(new FileReader(file));
        String result = new String();
        
        while(input.ready()){
            result += input.readLine();
            result += "\n";
        }
        
        input.close();
        
        return result;
    }
    
    
    /**
     * Serializes a {@code EvacuationRecording} object to an
     * XML string via XStream.
     * @param recording The object you want to serialize
     * @return The serialized object
     */
    private String recordingToXML(EvacuationRecording recording){
            return xstream.toXML(recording);
    }
    
    /**
     * Writes a string to a file.
     * @param xmlStr The string you want to store
     * @param file The file in which you want to store the string
     * @throws IOException Throws an exception if the file cannot be 
     * written or if the file already exists.
     */
    private void writeString(String xmlStr, File file) throws IOException{
        if(file.exists()){
            throw new IOException("The file already exists!");
        }
        
        BufferedWriter output = new BufferedWriter(new FileWriter(file));
        output.write(xmlStr);
        output.flush();
        output.close();
    }
}
