package io.visualization;

/**
 * An enumeration with some image formats that can be written.
 * @author Jan-Philipp Kappmeier
 */
public enum ImageFormat {
	/** Bitmap format */
	BMP( "Bitmap", "bmp" ),
	/** Graphics interchange format */
	GIF( "GIF", "gif" ),
	/** JPEG format */
	JPEG( "JPEG", "jpg" ),
	/** Portable network graphics, recommended by the www consortium. */
	PNG( "PNG", "png" );

	/** The ending of the movie format. */
	private String ending;	
	/** The name of the image format. The result of the {@link toString()} method */
	private String name;
	

	/**
	 * Creates a new enumeration instance
	 * @param name the name of the image format
	 * @param ending the ending of the image format
	 */
	ImageFormat( String name, String ending ) {
		this.ending = ending;
		this.name = name;
	}
	
	/**
	 * Returns the ending of the image format.
	 * @return the ending of the image format
	 */
	public String getEnding() {
		return ending;
	}

	/**
	 * Returns the name of the image format
	 * @return the name of the image format
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the name of the image format
	 * @return the name of the image format
	 */
	@Override
	public String toString() {
		return name;
	}
}
