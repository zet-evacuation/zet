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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package gui;

import org.zetool.math.Conversion;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.gl2.GLUgl2;

import org.zetool.opengl.framework.JMovingEyePanel;
import org.zetool.opengl.helper.TextureFontStrings;
import org.zetool.opengl.helper.Texture;
import org.zetool.opengl.helper.TextureFont;
import org.zetool.opengl.helper.TextureManager;

/**
 * An OpenGL-panel displaying some textual copyright information about the zet evacuation tool. The information scrolls
 * from bottom to top, and starts again.
 *
 * @author Jan-Philipp Kappmeier
 */
public class CreditsPanel extends JMovingEyePanel {

    /** The instance of the texture manager. */
    private TextureManager texMan;
    /** Describes if the textures are loaded or not. */
    private boolean texturesLoaded = false;
    /** The texture containing the font information. */
    private Texture texFont;
    /** The texture containing the logo. */
    private Texture texLogo;
    /** A texture-font object using to draw getText on the {@code OpenGL} panel. */
    private TextureFont font;
    /** The top getX of the getText. */
    private float startPos = -5;
    /** The array containing the getText. */
    private TextureFontStrings lines = new TextureFontStrings(false);
    /** Decides, whether the animation is paused. */
    private boolean paused = false;

    /**
     * Create a new instance of the {@code CreditsPanel}. Initializes the default {@code OpenGL} canvas and removes all
     * listeners as the credits are view-only.
     */
    public CreditsPanel() {
        super();
        removeListener();
        initLines();
        useMouseWheelListener();
        useKeyListener();
    }

    @Override
    /**
     * Initializes the graphics context and initializes the texture font.
     *
     * @param drawable the context which is used for the credits panel
     */
    public void initGFX(GLAutoDrawable drawable) {
        drawable.getGL().glEnable(GL.GL_TEXTURE_2D);
        super.initGFX(drawable);
        glu = new GLUgl2();
        if (!texturesLoaded) {
            texMan = TextureManager.getInstance();
            texMan.setGL(drawable.getGL().getGL2());
            texMan.setGLU(glu);
            loadTextures();
            font = new TextureFont(drawable.getGL().getGL2(), texFont);
            font.buildFont3(16, 14, 16, 0.7f, (0.7f * 3) / 4);
            texFont.bind();
            texturesLoaded = true;
        }
        drawable.getGL().glEnable(GL.GL_TEXTURE_2D);
        // move camera to the right position
        pitch(20);
        getPos().y += 7;
    }

    /**
     * Displays the getText on the screen.
     *
     * @param drawable the context which is used for the credits panel
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        super.display(drawable);    // clear the screen

        // reset view
        gl.glLoadIdentity();
        this.look();

        // enable texture mode and draw logo
        texLogo.bind();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3d(-10, -4, startPos);                // lower left
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3d(-10, -4, startPos - 10);           // upper left
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3d(10, -4, startPos - 10);            // upper right
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3d(10, -4, startPos);                 // lower right
        gl.glEnd();

        // load font texture and draw text
        texFont.bind();
        drawLines(lines, startPos + 1);
    }

    /**
     * Moves the getText a bit upwards. The scrolling is not time independent, that means lags can occur on slower
     * hardware.
     */
    @Override
    public void animate() {
        super.animate();
        final double timePerPixel = Conversion.SEC_TO_NANO_SECONDS / 1;
        startPos -= paused ? 0 : (this.getDeltaTime() / timePerPixel);
    }

    /**
     * Loads the texture files from hard disk.
     */
    private void loadTextures() {
        texLogo = texMan.newTexture("logo1", "./textures/logo1.png");
        texFont = texMan.newTexture("font1", "./textures/font1.bmp");
    }

    /**
     * Draws some lines on the screen using the texture font. If the getText was completely scrolled over the whole
     * screen, the top getX is reseted.
     *
     * @param lines an array containing all lines that should be displayed
     */
    private void drawLines(TextureFontStrings lines, float start) {
        int end = 0;
        for (int i = 0; i < lines.size(); i++) {
            font.print(-lines.getX(i), -4f, start + i * 1.1f, lines.getText(i));
        }
        if (end > this.getHeight() + 48) {
            startPos = -128;
        }
    }

    /**
     * Adds all lines of the credits getText to the font line object.
     */
    private void initLines() {
        lines.setXoffset(10.3);
        Path creditsFile = Paths.get("./credits.txt");
        try {
            for (String line : Files.readAllLines(creditsFile, StandardCharsets.UTF_8)) {
                lines.add(parse(line.substring(0, line.length() - 2)), line.endsWith("1"));
            }
        } catch (IOException ex) {
            lines.add("Error loading credits", true);
            lines.add("Plese repair your ZET installation", true);
        }
    }

    private String parse(String input) {
        return input.replace("#VERSION", ZETMain.version);
    }

    /**
     * Changes the current position of the text according to the mouse wheel change.
     *
     * @param e the mouse wheel event
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        startPos = Math.min(-5, startPos + e.getWheelRotation());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            paused = !paused;
        }
    }

}
