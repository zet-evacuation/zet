/*
 * <></>
 * Texture.java
 * Created 10.09.2009, 11:04:00
 */

package opengl.helper;

import javax.media.opengl.GL;

/**
 * The class {@code Texture} represents a texture used by OpenGL. It can
 * bind and mipmap them.
 * @author Jan-Philipp Kappmeier
 */
public class Texture {
	/** The graphics context used by the texture */
	GL gl;
	/** The target */
	final int target;
	/** The texture id */
	final int textureID;
	/** The texture last binded, checked to not bind the same texture twice. */
	static Texture lastbind = null;
	
	public String resourceName = null;
/**
	 * Creates a new instance of {@code Texture}.
 * @param gl the graphics context in which this texture is used
 * @param target the OpenGL target, for example ({@link javax.media.opengl.GL#GL_TEXTURE_2D})
 * @param textureID the internal id of the texture (OpenGL texture name)
	 */
	public Texture( GL gl, int target, int textureID ) {
		this.gl = gl;
		this.textureID = textureID;
		this.target = target;
	}

	/**
	 * <p>Binds this texture to the current GL context. This method is a shorthand
	 * equivalent of the OpenGL code</p>
	 * <p>{@code gl.glBindTexture(texture.getTarget(), texture.getTextureID());}</p>
	 */
	public final void bind() {
		if( lastbind == null || lastbind != this ) {
			gl.glBindTexture( target, textureID );
			lastbind = this;
		}
	}

	/**
	 * Returns the {@code OpenGL} target of the texture.
	 * @return the target of the texture
	 */
	public final int getTarget() {
		return target;
	}

	/**
	 * Returns the OpenGL texture object for this texture. The ids are handled
	 * automatically by the {@link #bind()} and {@link #dispose()}.
	 * @return the id of the texture
	 */
	public final int getID() {
		return textureID;
	}

	// TODO Dispose
	public final void dispose() {
		
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "Texture";
	}
}
