/*
 * OFFReader.java
 * Created: 14.11.2010, 19:00:24
 */
package opengl.bsp;

import de.tu_berlin.math.coga.math.vectormath.Vector3;
import io.FileTypeException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class OFFReader {

	DynamicTriangleMesh mesh;

	public void readOff( String filename ) throws FileNotFoundException, IOException {
		if( mesh == null )
			mesh = new DynamicTriangleMesh();

		System.out.println( "Reading model file " + filename );

		BufferedReader reader = new BufferedReader( new FileReader( new File( filename ) ) );

		String line = reader.readLine();

		if( !line.equals( "OFF" ) ) {
			throw new FileTypeException( "Not a valid OFF file" );
			//System.out.println( "ERROR: Need OFF-Format!" );
			//return;
		}

		int numVertices = 0;
		int numFaces = 0;
		int numEdges = 0;

		mesh.prepareSize( numVertices, numEdges, numFaces );


		line = reader.readLine();
		String[] split = line.split( " " );
		numVertices = Integer.parseInt( split[0] );
		numFaces = Integer.parseInt( split[1] );
		numEdges = Integer.parseInt( split[2] );

		Queue<Triangle> tList = new LinkedList<Triangle>();
		Vector3[] vertexArray = new Vector3[numVertices];

		// read vertices
		System.out.println( "Read " + numVertices + " vertices." );
		for( int i = 0; i < numVertices; i++ ) {
			float x, y, z;
			line = reader.readLine();
			split = line.split( " " );
			x = Float.parseFloat( split[0] );
			y = Float.parseFloat( split[1] );
			z = Float.parseFloat( split[2] );
			//file >> x >> y >> z;

			Vector3 temp = mesh.addVertex(x, y, z);// new Vector3( x, y, z );

			vertexArray[i] = temp;
			//BspMain.vertices.add( temp );
		}

		// TODO: Read faces
		// create a new Triangle object for each face and pass it the pointers to the
		// corresponding vertex objects, do not make copies!
		// store the triangle in the TrianglesList

		System.out.println( "Read " + numFaces + " faces." );
		for( int i = 0; i < numFaces; i++ ) {
			int i1, i2, i3;

			line = reader.readLine();
			split = line.split( " " );
			int n = Integer.parseInt( split[0] );

			//if( n != 3 ) {
			//	System.out.println( "ERROR: We need triangles!" );
			//	return null;
			//}
			switch( n ) {
				case 3:
					i1 = Integer.parseInt( split[1] );
					i2 = Integer.parseInt( split[2] );
					i3 = Integer.parseInt( split[3] );
					break;
				default:
					throw new IllegalArgumentException( "Currently only files with triangles are supported." );
			}


			//Triangle tri = new Triangle( vertexArray[i1], vertexArray[i2], vertexArray[i3] );
			mesh.addTriangle( i1, i2, i3 );
			//tList.add( tri );
		}

		reader.close();

		//return tList;
	}

	public DynamicTriangleMesh getMesh() {
		return mesh;
	}

	public void setMesh(DynamicTriangleMesh mesh) {
		this.mesh = mesh;
	}
}
