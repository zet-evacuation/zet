/*
 * JCorner.java
 *
 * Created on 14. Dezember 2007, 16:05
 */

package gui.components;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;


/**
 * A simple component that fills itself with white color. Can be used to fill
 * unused corners of scrollpanes.
 * @author Jan-Philipp Kappmeier
 */
public class JCorner extends JComponent {
  protected void paintComponent(Graphics g) {
    g.setColor( Color.white );
    g.fillRect(0, 0, getWidth(), getHeight());
  }
}
