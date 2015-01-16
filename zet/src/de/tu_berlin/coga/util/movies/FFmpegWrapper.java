/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

package de.tu_berlin.coga.util.movies;

import org.zetool.common.util.Formatter;
import org.zetool.common.util.IOTools;
import info.clearthought.layout.TableLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 * A {@link MovieWriter} that works as a wrapper for the open source movie
 * encoder ffmpeg. See <a href=http://ffmpeg.mplayerhq.hu/>http://ffmpeg.mplayerhq.hu/</a> for more information.
 * @author Jan-Philipp Kappmeier
 */
public class FFmpegWrapper extends MovieWriter {
	/** Is 2-pass encoding enabled or disabled. */
	private boolean twoPassEncoding = true;
	/** The name of an MP3 or WAV file used as music for the movie. */
	private String mp3File = "";
	/** An advanced parameter string that can be created by the user. */
	private String advancedParameter = "";
	/** The length of the movie. */
	private double duration = 1;
	/** A string used to describe the codec used by ffmpeg. Needs to start with a space. */
	private String codecParameterString = "";
	private String codecParameterStringPass1 = "";
	private String codecParameterStringPass2 = "";


	/**
	 * {@inheritDoc}
	 * <p>Encodes the movie, one or two times, depending if 2-pass encoding
	 * is enabled or disabled.</p>
	 */
	@Override
	public void create( List<String> inputFiles, String filename ) {
		command = ""; // clear all commandlines to store the new ones.

		duration = inputFiles.size() / (double)framerate;
		// create player string
		switch( movieFormat ) {
			case DIVX:
				codecParameterString = " -vcodec mpeg4 -vtag DX50";
				break;
			case H264:
				// libx264 -fpre "/homes/combi/kappmeie/Dateien/Programme/zet/tools/ffmpeg/presets/libx264-placebo.ffpreset"
				// path for the presets
				String programPath = "./tools/ffmpeg/";
				File f = new File( programPath );
				programPath += "ffmpeg";

				String preset = "libx264-placebo";

				String presetPath = "";
				try {
					presetPath = f.getCanonicalPath();
				} catch( IOException ex ) {
					presetPath = f.getAbsolutePath();
				}
				presetPath += "/presets/";// + preset + ".ffpreset";
				codecParameterString = " -vcodec libx264"; // -fpre \""+ presetPath + "\"";
				if( !twoPassEncoding )
					codecParameterString = " -vcodec libx264 -fpre \"" + presetPath + preset + ".ffpreset\" -crf 22";
				else {
					codecParameterString = " -vcodec libx264 ";
					codecParameterStringPass1 = " -fpre \""+ presetPath + preset + "_firstpass.ffpreset\"";
					codecParameterStringPass2 = " -fpre \""+ presetPath + preset + ".ffpreset\"";
				}
				break;
			case MPEG1:
				codecParameterString = " -vcodec mpeg1video";
				break;
			case MPEG2:
				codecParameterString = " -vcodec mpeg2video";
				break;
			case MPEG4:
				codecParameterString = " -vcodec mpeg4 -vtag FMP4";
				break;
			case XVID:
				codecParameterString = " -vcodec mpeg4 -vtag XVID";
				break;
			case MOV:
				throw new java.lang.IllegalArgumentException( "Das MOV-Containerformat (Quicktime) wird zur Zeit nicht unterstützt. Nutzen sie den JMF-Writer." );
			default:
				throw new java.lang.IllegalArgumentException( "Nicht unterstütztes Videoformat." );
		}

		if( mp3File.trim().equals( "" ) )
			System.out.println( "Starte Video-Encodierung ohne Audio." );
		else
			System.out.println( "Starte Video-Encodierung." );

		int ret;
		if( twoPassEncoding ) {
			ret = record( 1, filename );
			if( ret != 0 )
				System.err.println( "exit code: " + ret );
			ret = 0;
			if( ret != 0 )
				System.err.println( "exit code: " + ret );
			ret= record( 2, filename );
		} else {
			ret = record( 0, filename );
			if( ret != 0 )
			  System.err.println( "exit code: " + ret );
		}
		if( ret == 0 )
			System.out.println( "Video erstellen erfolgreich!" );
	}

	/**
	 * Encodes the movie and saves it with a specified filename. Creates the
	 * string with the parameters send to ffmpeg encoder depending of the settings.
	 * The resolution is set to a value divisible by two.
	 * @param pass the number of the pass. can be 1, 2 or 0 if no 2-pass encoding is enabled
	 * @param filename the filename for the movie
	 * @throws java.lang.IllegalArgumentException if pass is not 0, 1 or 2
	 */
	private int record( int pass, String filename ) throws IllegalArgumentException {

		try {
			// framesize must be a multiple of 2 (at least for mpeg4)
			width += width % 2;
			height += height % 2;
			String thisCommand = "";
			// Basic command
			String programPath = "./tools/ffmpeg/ffmpeg";
			thisCommand += programPath + " -f image2";

			// Input Parameters: framerate
			thisCommand += " -r " + framerate;

			// Input files
			thisCommand += " -i \"./" + path + framename+"%0" + FRAMEDIGITS + "d." + frameFormat.getEnding() + "\"";

			// maybe mp3 file
			if( !mp3File.trim().equals( "" ) ) {
				System.out.println( "Codiere mit Audio." );
				File mp3 = new File( mp3File );
				if( mp3.exists() )
					thisCommand += " -i \"" + mp3File + "\" -acodec libmp3lame";
				else
					System.err.println( "Datei '" + mp3.getAbsolutePath() + "' nicht gefunden. Encodiere ohne Audio." );
			}
			// Quality parameter
			thisCommand += " -b " + bitrate + "k -bt " + bitrate + "k -s " + width + "x" + height;

			// Video codec
			thisCommand += codecParameterString;
			if( pass == 1 )
				thisCommand += codecParameterStringPass1;
			if( pass == 2 )
				thisCommand += codecParameterStringPass2;

			// duration (needed to stop, if mp3-file is larger)
			thisCommand += " -t " + Double.toString( duration );
			// the 2-pass-encoding
			switch( pass ) {
				case 0:
					// do nothing
					break;
				case 1:
					thisCommand += " -pass 1 -passlogfile \"./" + path + filename + "\"";
					break;
				case 2:
					// use auto-overwrite (-y) because the file was already written in pass 1
					thisCommand += " -y -pass 2 -passlogfile \"./" + path + filename + "\"";
					break;
				default:
					throw new IllegalArgumentException( "Pass must be 0, 1 or 2" );
			}
			if( !advancedParameter.equals( "" ) ) {
				thisCommand += " " + advancedParameter;
			}
			// Output file
			thisCommand += " \"./" + path + filename + "." + movieFormat.getEnding() + "\"";

			// Store commandline to the global commands.
			if( command.equals( "" ) )
				command += thisCommand;
			else
				command += '\n' + thisCommand;
			System.out.println( "Encode video with command line: " + thisCommand );

			System.out.println( thisCommand );
			Process process = new ProcessBuilder( IOTools.parseCommandString( thisCommand ) ).start();
			FFmpegInputHandler errorHandler = new FFmpegInputHandler( process.getErrorStream(), "Error Stream" );
			errorHandler.setVerbose( false );
			errorHandler.start();
			process.waitFor();
			return process.exitValue();
		} catch( InterruptedException ex ) {
			System.err.println( "Encoding process was interupted!" );
			return -1;
		} catch( IOException ex ) {
			System.err.println( "Exception in FFMPEG-Wrapper!" );
			return -1;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>Creates a new frame image filename. The frame number is the only used
	 * parameter, as the encoder reads all files beginning with the frame prefix
	 * and a number. Existing files are overwritten without any request.</p>
	 * @return the filename for the file number
	 */
	@Override
	public String getFilename( int number ) {
		return path + framename + Formatter.fillLeadingZeros( number, FRAMEDIGITS ) + "." + frameFormat.getEnding();
	}

	/**
	 * Returns the advanced options panel for the ffmpeg encoder. It is possible
	 * to enable or disable 2-pass encoding and to submit a music file and some
	 * advanced options, if you know what you are doing.
	 * @return the panel
	 */
	@Override
	public JPanel getAdvancedConfigurationPanel() {
		int space = 10;
		double size[][] = // Columns
		{
			{ space, TableLayout.FILL, space, 120, space },
			//Rows
			{ space,
				TableLayout.PREFERRED,	// 2-pass
				space,
				TableLayout.PREFERRED,	// mp3-file
				TableLayout.PREFERRED,	// mp3-file
				space,
				TableLayout.PREFERRED,	// additional output commands
				TableLayout.PREFERRED,	// additional output commands
				space
			}
		};
		JPanel panel = new JPanel( new TableLayout( size ) );
		final JCheckBox chk2PassEncoding = new JCheckBox( "2-pass encoding" );
		chk2PassEncoding.setSelected( true );
		chk2PassEncoding.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				twoPassEncoding = chk2PassEncoding.isSelected();
			}
		});
		panel.add( chk2PassEncoding, "1,1,3,1" );

		panel.add( new JLabel( "Musikdatei:" ), "1,3,3,3" );
		final JTextField txtMusicFile = new JTextField();
		panel.add( txtMusicFile, "1,4" );
		txtMusicFile.addFocusListener( new FocusListener() {
			@Override public void focusGained( FocusEvent e ) { }

			@Override
			public void focusLost( FocusEvent e ) {
				mp3File = txtMusicFile.getText();
			}
		});
		final JButton btnSelectMusicFile = new JButton( "Durchsuchen" );
		btnSelectMusicFile.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
					JFileChooser musicFileChooser = new JFileChooser();
					musicFileChooser.setFileFilter( new FileFilter() {
						@Override
						public boolean accept( File f ) {
							return f.isDirectory() || f.getName().toLowerCase().endsWith( ".mp3" ) || f.getName().toLowerCase().endsWith( ".wav" );
						}

						@Override
						public String getDescription() {
							return "Musikdateien (*.mp3;*.wav)";
						}
					} );
					musicFileChooser.showOpenDialog( null );
					txtMusicFile.setText( musicFileChooser.getSelectedFile().getPath() );
					mp3File = txtMusicFile.getText();
			}
		});
		panel.add( btnSelectMusicFile, "3,4" );

		panel.add( new JLabel( "Zusätzliche Optionen (eigene Gefahr):"), "1,6,3,6" );
		final JTextField txtAdvancedCommands = new JTextField();
		txtAdvancedCommands.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent e ) { }

			@Override
			public void focusLost( FocusEvent e ) {
				advancedParameter = txtAdvancedCommands.getText();
			}

		});
		panel.add( txtAdvancedCommands, "1,7,3,7" );

		return panel;
	}

	public static void main( String[] args) throws IOException {
		String programPath = "./tools/ffmpeg/ffmpeg";
		File f = new File( programPath );
		System.out.println( f.getAbsolutePath() );
		System.out.println( f.getPath() );
		System.out.println( f.getCanonicalPath() );
	}
}
