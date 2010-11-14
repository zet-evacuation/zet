/*
 * OFFReader.java
 * Created: 14.11.2010, 19:00:24
 */
package opengl.bsp;

import de.tu_berlin.math.coga.math.vectormath.Plane;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class OFFReader {

	static List<Triangle> readOff( String filename ) throws FileNotFoundException, IOException {

		System.out.println( "Reading model file " + filename );

		BufferedReader reader = new BufferedReader( new FileReader( new File( filename ) ) );

		String line = reader.readLine();

		if( !line.equals( "OFF" ) ) {
			System.out.println( "ERROR: Need OFF-Format!" );
			return null;
		}


		int numVertices = 0;
		int numFaces = 0;
		// we don't care about the edges but we have to read them anyways
		int numEdges = 0;


		line = reader.readLine();
		String[] split = line.split( " " );
		numVertices = Integer.parseInt( split[0] );
		numFaces = Integer.parseInt( split[1] );
		numEdges = Integer.parseInt( split[2] );

		List<Triangle> tList = new ArrayList<Triangle>();
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

			Vector3 temp = new Vector3( x, y, z );

			vertexArray[i] = temp;
			BspMain.vertices.add( temp );
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

			if( n != 3 ) {
				System.out.println( "ERROR: We need triangles!" );
				return null;
			}

			i1 = Integer.parseInt( split[1] );
			i2 = Integer.parseInt( split[2] );
			i3 = Integer.parseInt( split[3] );

			Triangle tri = new Triangle( vertexArray[i1], vertexArray[i2], vertexArray[i3] );
			tList.add( tri );
		}

		reader.close();

		return tList;
	}

	static void unitize() {

		double maxx, minx, maxy, miny, maxz, minz;
		double cx, cy, cz, w, h, d;
		double scale;

		assert (BspMain.vertices.size() > 0);

		/* init min/max as first vertex */
		Vector3 vTemp = BspMain.vertices.get( 0 );

		maxx = minx = vTemp.x;// ->  x;
		maxy = miny = vTemp.y;// ->  y;
		maxz = minz = vTemp.z;// ->  z;

		//for (it = vertices.begin(); it != vertices.end(); it++) {
		for( Vector3 v : BspMain.vertices ) {
			if( v.x > maxx )
				maxx = v.x;
			if( v.x < minx )
				minx = v.x;
			if( v.y > maxy )
				maxy = v.y;
			if( v.y < miny )
				miny = v.y;
			if( v.z > maxz )
				maxz = v.z;
			if( v.z < minz )
				minz = v.z;
		}


		/* calculate model width, height, and depth */
		//w = std::fabs( maxx ) + std::fabs(minx);
		w = Math.abs( maxx ) + Math.abs( minx );
		//h =  std::fabs( maxy ) + std::fabs(miny);
		h = Math.abs( maxy ) + Math.abs( miny );
		//d =  std::fabs( maxz ) + std:: fabs  (minz);
		d = Math.abs( maxz ) + Math.abs( minz );

		/* calculate center of the model */
		cx = (maxx + minx) / 2.0;
		cy = (maxy + miny) / 2.0;
		cz = (maxz + minz) / 2.0;

		/* calculate unitizing scale factor */
		//scale = 100.0 / std::max(std::max(w, h), d);
		scale = 100.0 / Math.max( Math.max( w, h ), d );

		for( Vector3 vFront : BspMain.vertices ) {
//			    (it = vertices.begin(); it != vertices.end(); it++) {

			//Vertex* vFront =  * it;
			vFront.x -= cx;
			vFront.x *= scale;
			vFront.y -= cy;
			vFront.y *= scale;
			vFront.z -= cz;
			vFront.z *= scale;
		}
	}

	static void computePlaneEquations( List<Triangle> model ) {
		for( Triangle t : model ) {
			Plane p = new Plane();
			BspMain.planes.add( p );
			t.plane = p;
			t.computePlane();
		}
	}

	static void computeNormals( List<Triangle> model ) {
		// TODO compute face normal for each triangle in the model
		// create a new normal (Vertex object) and store a pointer
		// to it in the faceNormals list
		// Assign the normal to the triangle

		for( Triangle tri : model ) {
			Vector3 normal = tri.plane.getNormal();
			tri.faceNormal = normal;	// maybe need to clone here?
			BspMain.faceNormals.add( normal );
		}
	}
}
