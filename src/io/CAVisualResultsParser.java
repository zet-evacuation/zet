package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import ds.ca.Cell;
import ds.ca.DoorCell;
import ds.ca.ExitCell;
import ds.ca.InitialConfiguration;
import ds.ca.Room;
import ds.ca.RoomCell;
import ds.ca.SaveCell;
import ds.ca.StairCell;
import ds.ca.results.ExitAction;
import ds.ca.results.MoveAction;
import ds.ca.results.VisualResultsRecording;

/**
 * This class serializes and deserializes <code>VisualResultsRecording</code>
 * objects to XML files. Use this class to store simulation results to disc.
 * 
 * The (de-)serializing is done via the XStream-library. 
 * @author Daniel Pluempe
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
        
        xstream.alias("Visual Results", VisualResultsRecording.class);
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
        xstream.useAttributeFor(Cell.class, "speedFactor");
        xstream.useAttributeFor(Cell.class, "x");
        xstream.useAttributeFor(Cell.class, "y");
        xstream.useAttributeFor(Cell.class, "room");
        
        xstream.omitField(VisualResultsRecording.class, "curTime");
    }
    
    /**
     * Converts a <code>VisualResultRecording</code> to an XML string and 
     * saves it in a file.
     * @param recording The recording you want to store
     * @param file The file in which to store the object for later
     * deserialization.
     * @throws IOException Throws an exception if the file cannot be
     *  written or if it already exists.
     */
    public void writeToFile(VisualResultsRecording recording, File file) throws IOException{
        String xmlString = recordingToXML(recording);
        System.out.println(xmlString);
        writeString(xmlString, file);
    }
    
    /**
     * Reads a serialized VisualResultsRecording from an XML file and 
     * deserializes it.
     * @param file  The XML file with the serialized object
     * @return The deserializes object
     * @throws IOException Throws an exception if the file cannot be found or
     * cannot be read.
     */
    public VisualResultsRecording readFromFile(File file) throws IOException{
        String xmlString = readString(file);
        return xmlToRecording(xmlString);
    }
    
    /**
     * Re-builds a <code>VisualResultsRecording</code> from an XML string
     * @param xmlStr An XML string with a serialized 
     * <code>VisualResultsRecording</code> object
     * 
     * @return The deserialized object.
     */
    private VisualResultsRecording xmlToRecording(String xmlStr){
        return (VisualResultsRecording)xstream.fromXML(xmlStr); 
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
     * Serializes a <code>VisualResultsRecording</code> object to an
     * XML string via XStream.
     * @param recording The object you want to serialize
     * @return The serialized object
     */
    private String recordingToXML(VisualResultsRecording recording){
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
