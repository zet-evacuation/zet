/*
 * TextureManager.java
 * Created on 31.01.2008, 21:29:51
 */
package opengl.helper;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * A class used for loading textures from the hard disk and activating them
 * in OpenGL.
 * @author Jan-Philipp Kappmeier
 */
public class TextureManager {
	private static TextureManager instance;
	private Map<String, Texture> textures;

	private TextureManager() {
		textures = new HashMap<String, Texture>();
	}

	public static TextureManager getInstance() {
		if( instance == null )
			instance = new TextureManager();
		return instance;
	}

	public void clear() {
		textures.clear();
	}

	public void clear( int capacity ) {
		textures.clear();
		textures = new HashMap<String, Texture>( capacity );
	}

	public void load( String texName, String fileName ) {
		Texture texture;
		try {
			System.err.println( "Loading texture..." );
			File texFile = new File( fileName );
			texture = TextureIO.newTexture( texFile, true );
			// Write the loaded texture again to the harddisk!
			//File texFileOut1 = new File( fileName + "_geschrieben.jpg" );
			//File texFileOut2 = new File( fileName + "_geschrieben.tga" );
			//File texFileOut3 = new File( fileName + "_geschrieben.bmp" );
			//File texFileOut4 = new File( fileName + "_geschrieben.dds" );
			//File texFileOut5 = new File( fileName + "_geschrieben.gif" );
			//TextureIO.write( texture, texFileOut1 );
			//TextureIO.write( texture, texFileOut2 );
			//TextureIO.write( texture, texFileOut3 );
			//TextureIO.write( texture, texFileOut4 );
			//TextureIO.write( texture, texFileOut5 );
			
			System.err.println( "Texture estimated memory size = " + texture.getEstimatedMemorySize() );
		} catch( IOException e ) {
			e.printStackTrace();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			e.printStackTrace( new PrintStream( bos ) );
			JOptionPane.showMessageDialog( null,
							bos.toString(),
							"Error loading texture",
							JOptionPane.ERROR_MESSAGE );
			return;
		}
		add( texName, texture );
	}

	public void add( String texName, Texture tex ) {
		textures.put( texName, tex );
	}

	public Texture get( String texName ) {
		return textures.get( texName );
	}

	public void bind( String texName ) {
		get( texName ).bind();
	}
}
