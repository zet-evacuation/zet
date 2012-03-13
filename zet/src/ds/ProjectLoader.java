/**
 * ProjectLoader.java
 * Created: 24.01.2011, 13:04:00
 */
package ds;

import ds.z.Project;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import de.tu_berlin.math.coga.rndutils.distribution.continuous.NormalDistribution;
import de.tu_berlin.math.coga.rndutils.distribution.continuous.UniformDistribution;
import ds.z.ZLocalization;
import io.z.NormalDistributionConverter;
import io.z.UniformDistributionConverter;
import io.z.XMLConverter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;


/**
 * Loads and saves zet projects. A singleton class.
 * @author Jan-Philipp Kappmeier
 */
public class ProjectLoader {
	//private static ProjectLoader instance = new ProjectLoader();

	private static XStream xml_convert;

	public static XStream getXStream() {
		return xml_convert;
	}


static {
	final Set<String> ignore = new HashSet<String>() {{
			add( "xOffsetAll" );
			add( "yOffsetAll" );
			add( "heightAll" );
			add( "widthAll" );
			//add( "maxY_DefiningEdge" ); // cannot be ignored due to backwards compatibility
			//add( "minX_DefiningEdge" );
			//add( "maxX_DefiningEdge" );
			//add( "minY_DefiningEdge" );
		}};
		xml_convert = new XStream() {
		@Override
			protected MapperWrapper wrapMapper( MapperWrapper next ) {
				return new MapperWrapper( next ) {
					@Override
					public boolean shouldSerializeMember( Class definedIn, String fieldName ) {
						// ignore some keywords
						if( ignore.contains( fieldName ) ) {
							return false;
						}
						//if( definedIn != Object.class )
						//	return false;
						return super.shouldSerializeMember( definedIn, fieldName );
					}
				};
			}
		};
		xml_convert.setMode( XStream.ID_REFERENCES );

		//Configure aliases for external classes (Java API)
		xml_convert.useAttributeFor( java.awt.Point.class, "x" );
		xml_convert.useAttributeFor( java.awt.Point.class, "y" );

		xml_convert.alias( "uniformDistribution", UniformDistribution.class );
		xml_convert.registerConverter( new UniformDistributionConverter() );

		xml_convert.alias( "normalDistribution", NormalDistribution.class );
		xml_convert.registerConverter( new NormalDistributionConverter() );

		//Configure aliases for all ds.* classes
		//For this purpose the current location of the bytecode is searched for
		//all class names. These are loaded and their annotaions are examined.

		// Load "ds" because the JARClassLoader won't load "" (the FileClassLoader does)
		URL pack = Project.class.getClassLoader().getResource( "ds" );
		String url = pack.toExternalForm();
		// The artificially delete the "ds" again
		url = url.substring( 0, url.lastIndexOf( '/' ) + 1 );

		// Scan JARs iteratively
		if( url.startsWith( "jar:" ) )
			try {
				String jar_filename = url.substring( url.indexOf( ':' ) + 1, url.lastIndexOf( '!' ) );
				JarFile jar = new JarFile( new File( new URI( jar_filename ) ) );

				Enumeration enu = jar.entries();
				while( enu.hasMoreElements() ) {
					String entry = ((ZipEntry)enu.nextElement()).getName();
					if( entry.endsWith( ".class" ) )
						try {
							String classname = entry.replace( '/', '.' ).substring(
											0, entry.lastIndexOf( '.' ) );
							Class fromFile = Project.class.getClassLoader().loadClass( classname );
							processClassObject( fromFile );
						} catch( Exception ex ) {
							System.out.println( " Problem while scanning classes: Class file" + entry +
											"\n" + ex.getLocalizedMessage() );
						}
				}
			} catch( URISyntaxException | IOException ex ) {
				throw new RuntimeException( ZLocalization.getSingleton().getString( "ds.InitProjectException" + ex.getLocalizedMessage() ) );
			}
		else if( url.startsWith( "file:" ) )
			try {
				File dir = new File( new URI( url ) );
				assert (dir.isDirectory());
				scanPackage( dir, "" );
			} catch( URISyntaxException ex ) {
				throw new RuntimeException( ZLocalization.getSingleton().getString( "ds.InitProjectException" ) );
			}
		else
			throw new IllegalStateException( ZLocalization.getSingleton().getString( "ds.DeterminingJarException" ) );
	}

	/** Crawls the given directory for class files and processes them. */
	private static void scanPackage( File dir, String pack ) {
		for( File f : dir.listFiles() )
			if( f.isDirectory() )
				scanPackage( f, pack + f.getName() + "." );
			else if( f.getName().endsWith( ".class" ) )
				try {
					String classname = f.getName().substring( 0, f.getName().lastIndexOf( '.' ) );
					Class fromFile = Project.class.getClassLoader().loadClass( pack + classname );					
					processClassObject( fromFile );
				} catch( NoClassDefFoundError ex ) {
					System.err.println( "Class belonging to file '" + f.getAbsolutePath() + "' does not exist. Please check your dist directory." );
				} catch( Exception ex ) {
					System.out.println( "Problem while scanning classes: Class file" + f.getName() + "\n" + ex.getLocalizedMessage() );
				}
	}

	/** Used to initialize the XStream related stuff concerning the given type. */
	private static void processClassObject( Class type ) throws Exception {
		// Inhibit alias processing on the converter classes themselves
		// as this will lead to unwanted behaviour in XStream
		if( !com.thoughtworks.xstream.converters.Converter.class.isAssignableFrom( type ) ) {
			Annotations.configureAliases( xml_convert, type );

			if( type.isAnnotationPresent( XMLConverter.class ) )
				xml_convert.registerConverter( (Converter)((XMLConverter)type.getAnnotation(
								XMLConverter.class )).value().getConstructor(
								Mapper.class, ReflectionProvider.class ).newInstance(
								xml_convert.getMapper(), xml_convert.getReflectionProvider() ) );
		}
	}

	private ProjectLoader() {

	}

//	public static ProjectLoader getInstance() {
//		return instance;
//	}

	/**
	 * Saves a project to its project file.
	 * @param p the project that is saved.
	 * @throws IOException IOException - Is thrown when the I/O-Operation fails.
	 */
	public static void save( Project p ) throws IOException {
		save( p, p.getProjectFile() );
	}

	/**
	 * @param file The location where the Project shall be stored.
	 * @exception IOException - Is thrown when the I/O-Operation fails.
	 */
	public static void save( Project p, File file ) throws IOException {
		OutputStream output = new BufferedOutputStream( new FileOutputStream( file ) );

		if( file.getAbsolutePath().endsWith( ".gzet" ) )
			output = new GZIPOutputStream( output );
		// Set project file before saving, to get it into the saved file
		p.setProjectFile( file );

		try {
			xml_convert.toXML( p, output );
		} catch( Exception e ) {
			e.printStackTrace( System.err );
		}
		output.flush();
		output.close();
	}

	/**
	 * @param projectFile
	 * @exception IOException - Is thrown when the I/O-Operation fails.
	 * @return The Project that was stored in the denoted file.
	 */
	public static Project load( File projectFile ) throws IOException {
		//FileReader
		InputStream input;
		if( projectFile.getAbsolutePath().endsWith( ".gzet" ) )
			input = new GZIPInputStream( new BufferedInputStream( new FileInputStream( projectFile ) ) );
		else
			input = new BufferedInputStream( new FileInputStream( projectFile ) );
		Project p = (Project) xml_convert.fromXML( input );
		input.close();
		return p;
	}


}
