/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * JLogField.java
 * Created 29.10.2009, 15:56:54
 */
package gui.components;

import event.EventListener;
import event.EventServer;
import event.MessageEvent;
import info.clearthought.layout.TableLayout;
import java.util.ArrayList;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * The class <code>JLogField</code> is a text area that displays log and error
 * messages. The error messages are displayed in red.
 * @author Jan-Philipp Kappmeier
 */
public class JLogField extends JPanel implements EventListener<MessageEvent> {
	JEditorPane log;
	ArrayList<String> strings = new ArrayList<String>();
	String text = "";

	/**
	 * Creates a new instance of <code>JLogField</code>.
	 */
	public JLogField() {
		double size[][] = // Columns
						{
			{TableLayout.FILL},
			//Rows
			{TableLayout.FILL}
		};

		setLayout( new TableLayout( size ) );

		log = new JEditorPane( "text/html", "" );

    JScrollPane scrollPane = new JScrollPane( log );

		add( scrollPane, "0,0" );

		EventServer.getInstance().registerListener( this, MessageEvent.class );
	}

	/**
	 * <p>Handles incoming events. An event comes, if some debugger/logger sends
	 * messages of type {@link MessageType}. Only messages of the types
	 * {@link MessageType.Log} and {@link MessageType.LogError} are handled.</p>
	 * <p>{@code Log} events are displayed in normal font style while
	 * {@code LogError} events are displayed red.</p>
	 * @param event the event that occured.
	 */
	public void handleEvent( MessageEvent event ) {
		String s = "";
		switch( event.getType() ) {
			case Log:
				s = event.getMessage() + "<br>";
				break;
			case LogError:
				s = "<font color=\"red\">" + event.getMessage() + "</font><br>";
				break;
		}
		strings.add( s );
		text += s;
		log.setText( "<html>" + text + "</html>" );
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "JLogWindow";
	}
}
