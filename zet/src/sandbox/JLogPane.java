/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * JLogPane.java
 *
 */

package sandbox;

import java.text.DateFormat;
import java.util.Date;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Martin Groß
 */
public class JLogPane extends JScrollPane {
    
    protected JTextArea textArea;
    
    public JLogPane() {
        super();
        textArea = new JTextArea(5,0);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        this.setViewportView(textArea);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
    }
    
    public void log(String message) {
        Date now = new Date();
        String time = DateFormat.getTimeInstance().format(now);
        if (textArea.getText().equals("")) {
            textArea.setText(time + ": " + message + "\n");
        } else {
            textArea.setText(textArea.getText() + time + ": " + message + "\n");
        }
    }
    
}
