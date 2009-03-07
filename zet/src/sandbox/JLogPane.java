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
