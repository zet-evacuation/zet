/**
 * ProjectLoader.java Created: 24.01.2011, 13:04:00
 */
package ds;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import de.tu_berlin.coga.zet.model.PlanEdge;
import de.tu_berlin.coga.zet.model.PlanPolygon;
import org.zetool.rndutils.distribution.continuous.NormalDistribution;
import org.zetool.rndutils.distribution.continuous.UniformDistribution;
import de.tu_berlin.coga.zet.model.Project;
import de.tu_berlin.coga.zet.model.ZLocalization;
import io.z.NormalDistributionConverter;
import io.z.PlanPolygonConverter;
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
	private final static XStream xmlConvert;

	static {
		final Set<String> ignore = new HashSet<String>() {
			{
				add( "xOffsetAll" );
				add( "yOffsetAll" );
				add( "heightAll" );
				add( "widthAll" );
				//add( "maxY_DefiningEdge" ); // cannot be ignored due to backwards compatibility
				//add( "minX_DefiningEdge" );
				//add( "maxX_DefiningEdge" );
				//add( "minY_DefiningEdge" );
			}
		};
		xmlConvert = new XStream() {
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
		xmlConvert.setMode( XStream.ID_REFERENCES );

		xmlConvert.alias( "ds.z.Edge", PlanEdge.class );
		xmlConvert.alias( "another", PlanEdge.class );
		xmlConvert.alias( "lineSegment", PlanEdge.class );

		//Configure aliases for external classes (Java API)
		xmlConvert.useAttributeFor( java.awt.Point.class, "x" );
		xmlConvert.useAttributeFor( java.awt.Point.class, "y" );

		xmlConvert.alias( "uniformDistribution", UniformDistribution.class );
		xmlConvert.registerConverter( new UniformDistributionConverter() );

		xmlConvert.alias( "normalDistribution", NormalDistribution.class );
		xmlConvert.registerConverter( new NormalDistributionConverter() );
		
		//@XStreamOmitField
		//private T maxY_DefiningEdge;

		xmlConvert.omitField( PlanPolygon.class, "maxY_DefiningEdge" );

		
		//@XStreamAlias("planPolygon")
		//@XMLConverter(PlanPolygonConverter.class)
		xmlConvert.alias( "planPolygon", PlanPolygon.class );
		//xmlConvert.registerConverter( new PlanPolygonConverter(null, null ) );
		//@XStreamOmitField
		//private final Class<T> edgeClassType;
		xmlConvert.omitField( PlanPolygon.class, "edgeClassType" );
		
		xmlConvert.useAttributeFor( PlanPolygon.class, "closed" );
		
		//Configure aliases for all ds.* classes
		//For this purpose the current location of the bytecode is searched for
		//all class names. These are loaded and their annotaions are examined.
		// Load "ds" because the JARClassLoader won't load "" (the FileClassLoader does)
		URL pack = Project.class.getClassLoader().getResource( "ds" );
		String url = pack.toExternalForm();
		// The artificially delete the "ds" again
		url = url.substring( 0, url.lastIndexOf( '/' ) + 1 );

		// Scan JARs iteratively
		if( url.startsWith( "jar:" ) ) {
			try {
				String jar_filename = url.substring( url.indexOf( ':' ) + 1, url.lastIndexOf( '!' ) );
				JarFile jar = new JarFile( new File( new URI( jar_filename ) ) );

				Enumeration enu = jar.entries();
				while( enu.hasMoreElements() ) {
					String entry = ((ZipEntry)enu.nextElement()).getName();
					if( entry.endsWith( ".class" ) ) {
						try {
							String classname = entry.replace( '/', '.' ).substring(
											0, entry.lastIndexOf( '.' ) );
							Class fromFile = Project.class.getClassLoader().loadClass( classname );
							processClassObject( fromFile );
						} catch( Exception ex ) {
							System.out.println( " Problem while scanning classes: Class file" + entry
											+ "\n" + ex.getLocalizedMessage() );
						}
					}
				}
			} catch( URISyntaxException | IOException ex ) {
				throw new RuntimeException( ZLocalization.loc.getString( "ds.InitProjectException" + ex.getLocalizedMessage() ) );
			}
		} else if( url.startsWith( "file:" ) ) {
			try {
				File dir = new File( new URI( url ) );
				assert (dir.isDirectory());
				scanPackage( dir, "" );
			} catch( URISyntaxException ex ) {
				throw new RuntimeException( ZLocalization.loc.getString( "ds.InitProjectException" ) );
			}
		} else {
			throw new IllegalStateException( ZLocalization.loc.getString( "ds.DeterminingJarException" ) );
		}
	}

	/** Crawls the given directory for class files and processes them. */
	private static void scanPackage( File dir, String pack ) {
		for( File f : dir.listFiles() ) {
			if( f.isDirectory() ) {
				scanPackage( f, pack + f.getName() + "." );
			} else if( f.getName().endsWith( ".class" ) ) {
				try {
					String classname = f.getName().substring( 0, f.getName().lastIndexOf( '.' ) );
					Class<?> fromFile = Project.class.getClassLoader().loadClass( pack + classname );
					processClassObject( fromFile );
				} catch( NoClassDefFoundError ex ) {
					System.err.println( "Class belonging to file '" + f.getAbsolutePath() + "' does not exist. Please check your dist directory." );
				} catch( Exception ex ) {
					System.out.println( "Problem while scanning classes: Class file" + f.getName() + "\n" + ex.getLocalizedMessage() );
				}
			}
		}
	}

	/** Used to initialize the XStream related stuff concerning the given type. */
	private static void processClassObject( Class type ) throws Exception {
		// Inhibit alias processing on the converter classes themselves
		// as this will lead to unwanted behaviour in XStream
		if( !com.thoughtworks.xstream.converters.Converter.class.isAssignableFrom( type ) ) {
			Annotations.configureAliases( xmlConvert, type );

			if( type.isAnnotationPresent( XMLConverter.class ) ) {
				xmlConvert.registerConverter( (Converter)((XMLConverter)type.getAnnotation(
								XMLConverter.class )).value().getConstructor(
												Mapper.class, ReflectionProvider.class ).newInstance(
												xmlConvert.getMapper(), xmlConvert.getReflectionProvider() ) );
			}
		}
	}

	/** Private constructor for utility class. */
	private ProjectLoader() { }

	/**
	 * Saves a project to its project file.
	 * @param p the project that is saved.
	 * @throws IOException IOException - Is thrown when the I/O-Operation fails.
	 */
	public static void save( Project p ) throws IOException {
		save( p, p.getProjectFile() );
	}

	/**
	 * @param p
	 * @param file The location where the Project shall be stored.
	 * @exception IOException - Is thrown when the I/O-Operation fails.
	 */
	public static void save( Project p, File file ) throws IOException {
		OutputStream output = new BufferedOutputStream( new FileOutputStream( file ) );

		if( file.getAbsolutePath().endsWith( ".gzet" ) ) {
			output = new GZIPOutputStream( output );
		}
		// Set project file before saving, to get it into the saved file
		p.setProjectFile( file );

		try {
			xmlConvert.toXML( p, output );
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
		if( projectFile.getAbsolutePath().endsWith( ".gzet" ) ) {
			input = new GZIPInputStream( new BufferedInputStream( new FileInputStream( projectFile ) ) );
		} else {
			input = new BufferedInputStream( new FileInputStream( projectFile ) );
		}
		Project p = (Project)xmlConvert.fromXML( input );
		input.close();
		return p;
	}

}
