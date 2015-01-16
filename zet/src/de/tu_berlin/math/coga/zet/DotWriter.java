/**
 * DotWriter.java
 * Created: Mar 12, 2010,12:56:25 PM
 */
package de.tu_berlin.math.coga.zet;

import org.zetool.graph.DirectedGraph;
import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DotWriter {
	public static void writeDot( String original, String filename, DirectedGraph network ) throws FileNotFoundException, IOException {
		BufferedWriter writer = new BufferedWriter( new FileWriter( new File( filename ) ) );

//		digraph G {
//2: main -> parse -> execute;
//3: main -> init;
//4: main -> cleanup;
//5: execute -> make_string;
//6: execute -> printf
//7: init -> make_string;
//8: main -> printf;
//9: execute -> compare;
//10: }

		writer.write( "/* Written by ZET DotWriter */\n" );
		writer.write( "/* original file: " + original + "*/\n" );

		writer.write( "digraph N {\n" );
		//writer.write( "splines=line\n" );
		writer.write( "  /* " + network.nodeCount() + " nodes */\n" );
		for( Node node : network.nodes() ) {
			writer.write( "  " + node.id() + ";\n");
		}
		writer.write( "  /* " + network.edgeCount() + " edges */\n" );
		for( Edge edge : network.edges() ) {
			writer.write( "  " + edge.start().id() + "->" + edge.end().id() + '\n' );
		}
		writer.write( "}\n" );

		writer.close();
	}

	public static void writeDotWithoutSuperSink( String original, String filename, DirectedGraph network, Node sink ) throws FileNotFoundException, IOException {
		BufferedWriter writer = new BufferedWriter( new FileWriter( new File( filename ) ) );

		writer.write( "/* Written by ZET DotWriter */\n" );
		writer.write( "/* original file: " + original + "*/\n" );
		writer.write( "/* uper sink not contained */\n" );

		writer.write( "digraph N {\n" );
		//writer.write( "splines=line\n" );
		writer.write( "  /* " + network.nodeCount() + " nodes */\n" );
		for( Node node : network.nodes() ) {
			if( !node.equals( sink ) )
				writer.write( "  " + node.id() + ";\n");
			else
				System.err.println( "Sink skipped" );
		}
		writer.write( "  /* " + network.edgeCount() + " edges */\n" );
		for( Edge edge : network.edges() ) {
			if( !edge.start().equals( sink ) && !edge.start().equals( sink ) )
			writer.write( "  " + edge.start().id() + "->" + edge.end().id() + '\n' );
			else
				System.err.println( "Edge skipped" );
		}
		writer.write( "}\n" );

		writer.close();
	}

	public static void writeFile( String original, EarliestArrivalFlowProblem eafp, String filename ) throws FileNotFoundException, IOException {
		writeDot( original, filename, eafp.getNetwork() );
	}

	public static void writeFileWithoutSuperSink( String original, EarliestArrivalFlowProblem eafp, String filename ) throws FileNotFoundException, IOException {
		writeDotWithoutSuperSink( original, filename, eafp.getNetwork(), eafp.getSink() );
	}

	static String[] filenames = {
//			"./testinstanz/problem.dat",
//			"./testinstanz/audimax.dat",
//			"./testinstanz/oh14.dat",
//			"./testinstanz/probeevakuierung.dat",
//			"./testinstanz/siouxfalls_5_10s.dat",
//			"./testinstanz/siouxfalls_50_10s.dat",
//			"./testinstanz/siouxfalls_500_10s.dat",
//			"./testinstanz/swiss_500_10s.dat",
			"./testinstanz/padang_10p_10s_flow01.dat",
//			"./testinstanz/4_rooms_demo.dat",
		};

		public static void main( String[] arguments ) throws FileNotFoundException, IOException {
		System.out.println( "Version 1.0" );
		System.out.println( "Reads a file with an earliest arrival flow problem and solves the problem." );
		System.out.println( "Param #1: filename/path");
		System.out.println();


		//int i = 1;
		try {
			DatFileReaderWriter ff = new DatFileReaderWriter();
			for( int i = 0; i < filenames.length; ++i ) {
				EarliestArrivalFlowProblem eat = DatFileReaderWriter.read( filenames[i] );
				//DotWriter.writeDot( "audimax.dot", filenames[i].substring( 0, filenames[i].length()-3) + "dot", eat.getNetwork() );
				DotWriter.writeFileWithoutSuperSink( "audimax.dot", eat, filenames[i].substring( 0, filenames[i].length()-4) + "_wos_.dot" );
			}//EarliestArrivalFlowProblem eat = read( filename1 );
			//ff.computeFlow( eat );
			//DatFileReaderWriter.writeFile( "problem.dat", eat, "./testinstanz/output.dat" );
		} catch( FileNotFoundException e ) {
			System.out.println( "Datei nicht gefunden." );
			printFiles();
		} catch( ArrayIndexOutOfBoundsException e ) {
			System.out.println( "Kein Dateiname angegeben." );
			printFiles();
		}
	}

	public static void printFiles() {
			System.out.println( "Mögliche Dateien:" );
			for( int i = 0; i < filenames.length; ++i )
				System.out.println( filenames[i] );
	}

}
