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
/**
 * Class PlanImage
 * Created 18.04.2008, 11:43:18
 */

package gui.editor.planimage;

import gui.editor.CoordinateTools;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PlanImage {
	private BufferedImage image = null;
	private Image drawnImage = null;
	private int imageX = 0;
	private int imageY = 0;
	private int drawnImageX = 0;
	private int drawnImageY = 0;
	private int offsetX = 0;
	private int offsetY = 0;
	private float alpha = 0.5f;
	private Rectangle visible;
	int correctnessX = 0;
	int correctnessY = 0;
	
	/**
	 * Returns the currently set background image.
	 * @return the image
	 */
	public BufferedImage getImage() {
		return image;
	}	
	
	/**
	 * 
	 * @param g2
	 */
	public void paintComponent( Graphics2D g2 ) {
		if ( image != null && drawnImage != null ) {
			g2.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha ) );
			g2.drawImage( drawnImage, (int)(correctnessX + drawnImageX), (int)(correctnessY + drawnImageY), null );
			g2.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 ) );
		}
	}	
	
	public void resize() {
		update();
	}

	public void setVisibleRect( Rectangle visibleRect ) {
		this.visible = visibleRect;
	}
	
	public void update() {
		if( image == null )
			return;
		this.drawnImageX = CoordinateTools.translateToScreen( imageX + offsetX );
		this.drawnImageY = CoordinateTools.translateToScreen( imageY + offsetY );
		
		visible.x = Math.max( visible.x - drawnImageX, 0 );
		visible.y = Math.max( visible.y - drawnImageY, 0 );
		
		final double visiblePixelWidth  = CoordinateTools.getPictureZoomFactor() * CoordinateTools.translateToScreenW( image.getWidth() );
		final double visiblePixelHeight = CoordinateTools.getPictureZoomFactor() * CoordinateTools.translateToScreenW( image.getHeight() );
		if( visible.x >= visiblePixelWidth || visible.y >= visiblePixelHeight ) {
			System.out.println( "Außerhalb des Bildschirms!" );
			drawnImage = null;
			return;
		}

		final double pixelsize = CoordinateTools.getPictureZoomFactor() * CoordinateTools.translateToScreenW( 1 );
		final double usedPixelX = Math.ceil( visible.width / pixelsize )+1;
		final double usedPixelY = Math.ceil( visible.height / pixelsize )+1;
		final int x;
		final int y;
		final int h;
		final int w;
		BufferedImage vi;
		if( usedPixelX >= image.getWidth() ) {
			x = 0;
			w = image.getWidth();
		} else {
			w = (int)usedPixelX;
			final double xt = ( visible.x / visiblePixelWidth ) * image.getWidth();
			int xl = (int)Math.floor( xt );
			if( xl + w > image.getWidth() )
				xl = image.getWidth() - w;
			x = xl;
		}
		if( usedPixelY >= image.getHeight() ) {
			y = 0;
			h = image.getHeight();
		} else {
			h = (int)usedPixelY;
			final double yt = ( visible.y / visiblePixelHeight ) * image.getHeight();
			int yl = (int)Math.floor( yt );
			if( yl + h > image.getHeight() )
				yl = image.getHeight() - h;
			y = yl;
		}
		vi = image.getSubimage( x, y, w, h );
		correctnessX = (int) Math.round(x * pixelsize);
		correctnessY = (int) Math.round(y * pixelsize);
		drawnImage = vi.getScaledInstance( (int)(CoordinateTools.getPictureZoomFactor()*CoordinateTools.translateToScreen( w )), (int)(CoordinateTools.getPictureZoomFactor()*CoordinateTools.translateToScreen( h )), Image.SCALE_FAST);
	}
	
	public int getImageX() {
		return imageX;
	}

	public int getImageY() {
		return imageY;
	}
	
	/**
	 * Sets an background image that is displayed.
	 * @param image the image
	 */
	public void setImage( BufferedImage image ) {
		if( image == null )
			drawnImage = null;
		this.image = image;
		update();
	}

	public void setImageX( int imageX ) {
		this.imageX = imageX;
		this.drawnImageX = CoordinateTools.translateToScreen( imageX + offsetX );
	}

	public void setImageY( int imageY ) {
		this.imageY = imageY;
		this.drawnImageY = CoordinateTools.translateToScreen( imageY + offsetY );
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX( int offsetX ) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY( int offsetY ) {
		this.offsetY = offsetY;
	}

	/**
	 * Returns the currently set alpha value for the image.
	 * @return the alpha value
	 */
	public float getAlpha() {
		return alpha;
	}

	/**
	 * <p>Sets the alpha value for the image. The values can be within 0 and 1, 0
	 * means that the image is displayed fully transparent, where a value of 1
	 * represents no transparency.</p>
	 * <p>Values less than 0 or greater than 1 are ignored, note that no exception
	 * is thrown.</p>
	 * @param alpha the new alpha value
	 */
	public void setAlpha( double alpha ) {
	 if( alpha < 0 || alpha > 1)
		 return;
		this.alpha = (float)alpha;
	}
	
	/**
	 * Returns the width on screen, using the current zoom factor. See more at
	 * {@link CoordinateTools}
	 * @return the width
	 */
	public int getWidth() {
		return drawnImage == null ? 0 : drawnImage.getWidth( null );
	}
	
	/**
	 * Returns the height on screen, using the current zoom factor. See more at
	 * {@link CoordinateTools}
	 * @return the height
	 */
	public int getHeight() {
		return drawnImage == null ? 0 : drawnImage.getHeight( null );
	}
	
}
