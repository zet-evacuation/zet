/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * TextureManager.java
 * Created on 31.01.2008, 21:29:51
 */

package opengl.helper;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 * A class used to load textures from the hard disk and to activate them
 * in {@code OpenGL}.
 * @author Jan-Philipp Kappmeier
 */
public class TextureManager {
	private static TextureManager instance;
	private Map<String, Texture> textures;
	private GL gl;
	private GLU glu;

	private TextureManager() {
		textures = new HashMap<String, Texture>();
	}

	public static TextureManager getInstance() {
		if( instance == null )
			instance = new TextureManager();
		return instance;
	}

	public void setGL( GL gl ) {
		this.gl = gl;
	}

	public void setGLU( GLU glu ) {
		this.glu = glu;
	}

	private int createTextureID() {
		int tmp[] = new int[1];
		gl.glGenTextures(1, tmp, 0);
		return tmp[0];
	}

	public Texture getTexture( String name ) throws java.io.IOException {
		Texture tex = textures.get( name );
		return tex;
	}

	public Texture newTexture( String name, String filepath ) {
		Texture tex = null;
		try {
			int srcPixelFormat = 0;
			// Create texture name in OpenGL memory
			int textureID = createTextureID();
			tex = new Texture( gl, GL.GL_TEXTURE_2D, textureID );
			// Bind the texture
			gl.glBindTexture( GL.GL_TEXTURE_2D, textureID );

			// Load image
			BufferedImage bufferedImage;// = loadImage( resourceName );
			File f = new File( filepath );
			bufferedImage = ImageIO.read( f );

		if( bufferedImage.getColorModel().hasAlpha() )
			srcPixelFormat = GL.GL_RGBA;
		else
			srcPixelFormat = GL.GL_RGB;
		ByteBuffer textureBuffer = convertImageData( bufferedImage, tex );
		//if(target == GL.GL_TEXTURE_2D ) {
		gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR );
//		Values for GL_TEXTURE_MIN_FILTER
//    GL.GL_NEAREST;	// next value in manhatten metric
//    GL.GL_LINEAR; // meadian of the four nearest texture element
//    GL.GL_NEAREST_MIPMAP_NEAREST;	// selects mip map that fits best and criteria from GL_NEAREST
//    GL.GL_LINEAR_MIPMAP_NEAREST;	// selects mip map that fits best and criteria from GL_LINEAR
//    GL.GL_NEAREST_MIPMAP_LINEAR;	// selects the two mip maps that fit best and uses criteria fom GL_NEAREST
//    GL.GL_LINEAR_MIPMAP_LINEAR; 	// selects the two mip maps that fit best and uses critera fom GL_LINEAR. the final value is the median

		gl.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR  );
//		Values for GL_TEXTURE_MAG_FILTER
//    GL.GL_NEAREST;	// next value in manhatten metric
//    GL.GL_LINEAR; // meadian of the four nearest texture element
		//}
			int dstPixelFormat = GL.GL_RGB; // A
			//gl.glTexImage2D( GL.GL_TEXTURE_2D, 0, dstPixelFormat, bufferedImage.getWidth(), bufferedImage.getHeight(), 0, srcPixelFormat, GL.GL_UNSIGNED_BYTE, textureBuffer.rewind() );
			//glu.gluBuild2DMipmaps( GL.GL_TEXTURE_2D, dstPixelFormat, get2Fold(bufferedImage.getWidth()), get2Fold(bufferedImage.getHeight()), srcPixelFormat, GL.GL_UNSIGNED_BYTE, textureBuffer );
			glu.gluBuild2DMipmaps( GL.GL_TEXTURE_2D, dstPixelFormat, bufferedImage.getWidth(), bufferedImage.getHeight(), srcPixelFormat, GL.GL_UNSIGNED_BYTE, textureBuffer.rewind() );
		} catch( IOException ex ) {
			System.err.println( "Error loading texture" );
		}
		add( name, tex );
		return tex;
	}

	private ByteBuffer convertImageData( BufferedImage bufferedImage, Texture texture ) {
		ByteBuffer imageBuffer = null;
		int texWidth = 2;
		int texHeight = 2;
		// TODO more efficient...
		while( texWidth < bufferedImage.getWidth() )
			texWidth *= 2;
		while( texHeight < bufferedImage.getHeight() )
			texHeight *= 2;
//		for( ; texWidth < bufferedImage.getWidth(); texWidth *= 2 );
//		for( ; texHeight < bufferedImage.getHeight(); texHeight *= 2 );

		ColorModel glAlphaColorModel = new ComponentColorModel( java.awt.color.ColorSpace.getInstance( 1000 ), new int[]{8, 8, 8, 8}, true, false, 3, 0 );
		int ai[] = new int[4];
		ai[0] = 8;
		ai[1] = 8;
		ai[2] = 8;
		ColorModel glColorModel = new ComponentColorModel( java.awt.color.ColorSpace.getInstance( 1000 ), ai, false, false, 1, 0 );

		//texture.setTextureHeight( texHeight );
		//texture.setTextureWidth( texWidth );

		BufferedImage texImage;
		if( bufferedImage.getColorModel().hasAlpha() ) {
			WritableRaster raster = Raster.createInterleavedRaster( 0, texWidth, texHeight, 4, null );
			texImage = new BufferedImage( glAlphaColorModel, raster, false, new Hashtable() );
		} else {
			WritableRaster raster = Raster.createInterleavedRaster( 0, texWidth, texHeight, 3, null );
			texImage = new BufferedImage( glColorModel, raster, false, new Hashtable() );
		}
		Graphics g = texImage.getGraphics();
		g.setColor( new Color( 0.0F, 0.0F, 0.0F, 0.0F ) );
		g.fillRect( 0, 0, texWidth, texHeight );
		g.drawImage( bufferedImage, 0, 0, null );
		byte data[] = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();
		imageBuffer = ByteBuffer.allocateDirect( data.length );
		imageBuffer.order( java.nio.ByteOrder.nativeOrder() );
		imageBuffer.put( data, 0, data.length );
		return imageBuffer;
	}

	public void clear() {
		textures.clear();
	}

	public void clear( int capacity ) {
		textures.clear();
		textures = new HashMap<String, Texture>( capacity );
	}

//	public void load( String texName, String fileName ) {
//		Texture texture;
//		try {
//			System.err.println( "Loading texture..." );
//			File texFile = new File( fileName );
//			//texture = TextureIO.newTexture( texFile, true );
//			// Write the loaded texture again to the harddisk!
//			//File texFileOut1 = new File( fileName + "_geschrieben.jpg" );
//			//File texFileOut2 = new File( fileName + "_geschrieben.tga" );
//			//File texFileOut3 = new File( fileName + "_geschrieben.bmp" );
//			//File texFileOut4 = new File( fileName + "_geschrieben.dds" );
//			//File texFileOut5 = new File( fileName + "_geschrieben.gif" );
//			//TextureIO.write( texture, texFileOut1 );
//			//TextureIO.write( texture, texFileOut2 );
//			//TextureIO.write( texture, texFileOut3 );
//			//TextureIO.write( texture, texFileOut4 );
//			//TextureIO.write( texture, texFileOut5 );
//			
//			System.err.println( "Texture estimated memory size = " + texture.getEstimatedMemorySize() );
//		} catch( IOException e ) {
//			e.printStackTrace();
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			e.printStackTrace( new PrintStream( bos ) );
//			JOptionPane.showMessageDialog( null,
//							bos.toString(),
//							"Error loading texture",
//							JOptionPane.ERROR_MESSAGE );
//			return;
//		}
//		add( texName, texture );
//	}

	public void add( String texName, Texture tex ) {
		textures.put( texName, tex );
	}

	public final Texture get( String texName ) {
		return textures.get( texName );
	}

	public final void bind( String texName ) {
		get( texName ).bind();
	}
}
