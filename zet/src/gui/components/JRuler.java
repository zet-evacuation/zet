package gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;

public class JRuler extends JComponent {

  public enum RulerDisplayUnits {

    Mikrometer( 0.0001, "my" ),
    Millimeter( 0.001, "mm" ),
    Centimeter( 0.01, "cm" ),
    TwoCentimeter( 0.02, "2cm" ),
    Decimeter( 0.1, "dm" ),
    Meter( 1.0, "m" ),
    Inch( 0.0254, "in" ),
    Foot( 0.3048, "ft" ),
    Yard( 0.9144, "yd" );
    private final double unit;
    private final String text;

    RulerDisplayUnits( double unit, String text ) {
      this.unit = unit;
      this.text = text;
    }

    double unit() {
      return unit;
    }

    @Override
    public String toString() {
      return text;
    }
  }

  public enum RulerOrientation {

    Horizontal, Vertical;
  }
  /** The width for zoom factor 1.0 */
  private int zoomFactorIndependentWidth;
  /** The currently set unit of the ruler. */
  private RulerDisplayUnits unit = RulerDisplayUnits.Centimeter;
  /** The height of an horizontal ruler, or the width of a vertical, respectiveley. */
  private int size = 30;
  /** The orientation of the ruler. Can be horizontal or vertical. */
  private RulerOrientation orientation;
  /** The background color of the ruler. */
  public Color background = Color.WHITE;
  /** The foreground color of the ruler. */
  public Color foreground = Color.BLACK;
  /** The font for the numbers and units. */
  public Font font = new Font( "SansSerif", Font.PLAIN, 10 );
  /** The stepwide that defines the scale elements painted. */
  public double scalePaintStep = 10;
  /** The most left position. */
  public double offset = 0;
  /** Private zoomfactor */
  private double zoomFactor = 1000;
  static final int longTick = 10;
  private int bigScaleStep = 1;
  private int smallScaleStep = 1;

  public JRuler( RulerOrientation orientation, RulerDisplayUnits unit ) {
    this.orientation = orientation;
    this.unit = unit;
  }

  public RulerDisplayUnits getDisplayUnit() {
    return this.unit;
  }

  @Override
  protected void paintComponent( Graphics g ) {
    // Get bounds
    Rectangle drawArea = g.getClipBounds();

    // Fill background
    g.setColor( background );
    g.fillRect( drawArea.x, drawArea.y, drawArea.width, drawArea.height );

    // Set font and foreground color
    g.setColor( foreground );
    g.setFont( font );

    // Draw border lines
    if( orientation == RulerOrientation.Horizontal ) {
      g.drawLine( drawArea.x, size-1, drawArea.x + drawArea.width, size-1 );
    } else {
      g.drawLine( size-1, drawArea.y, size-1, drawArea.y + drawArea.height );
    }

    // Some vars we need.
    String text = null;
    int end = 0;
    //int start = 0;
    int tickLength = 0;

    double increment = zoomFactor * unit.unit();

    // Use clipping bounds to calculate first and last tick locations.
    if( orientation == RulerOrientation.Horizontal ) {
      //  start = (int) Math.round((drawArea.x / increment) * increment) - 40;
      end = (int) Math.round( ( ( ( drawArea.x + drawArea.width ) / increment ) + 1 ) * increment );
    } else {
      //  start = (int) Math.round((drawArea.y / increment) * increment) - 40;
      end = (int) Math.round( ( ( ( drawArea.y + drawArea.height ) / increment ) + 1 ) * increment );
    }

    // The 0-value is not explicitly drawn.

    double startOffset = ( offset / unit.unit() );
    int factor = (int) Math.floor( startOffset / (double) bigScaleStep );
    int drawPos = 0;
    int i = factor * bigScaleStep;
    int iOffset = i;
    double correction;
    correction = i - startOffset;

    //int i = (int)Math.ceil( start/increment );
    while( drawPos < end ) {
      drawPos = (int) Math.round( ( i - iOffset ) * increment + correction * increment );
      if( i % bigScaleStep == 0 ) {
        tickLength = 10;
        text = Integer.toString( i );
      } else {
        tickLength = 7;
        text = null;
      }
      if( tickLength != 0 ) {
        if( orientation == RulerOrientation.Horizontal ) {
          g.drawLine( drawPos, size - 1, drawPos, size - tickLength - 1 );
          if( text != null ) {
            g.drawString( text, drawPos - 3, 16 );
          }
        } else {
          g.drawLine( size - 1, drawPos, size - tickLength - 1, drawPos );
          if( text != null ) {
            g.drawString( text, 7, drawPos + 3 );
          }
        }
      }
      i += smallScaleStep;//++;
    }
  }

  public void setBigScaleStep( int scale ) {
    if( scale <= 0 ) {
      throw new java.lang.IllegalArgumentException( "Scale is negative or zero" );
    }
    this.bigScaleStep = scale;
  }

  public void setDisplayUnit( RulerDisplayUnits unit ) {
    this.unit = unit;
  }

  public void setHeight( int pw ) {
    this.zoomFactorIndependentWidth = pw;
    setPreferredHeight( (int) Math.ceil( pw * zoomFactor ) );
  }

  public void setPreferredHeight( int ph ) {
    setPreferredSize( new Dimension( size, ph ) );
  }

  public void setPreferredWidth( int pw ) {
    setPreferredSize( new Dimension( pw, size ) );
  }

  public void setSize( int size ) {
    if( size <= 0 ) {
      throw new java.lang.IllegalArgumentException( "Size not positive" );
    }
    this.size = size;
  }

  public void setSmallScaleStep( int scale ) {
    if( scale <= 0 ) {
      throw new java.lang.IllegalArgumentException( "Scale is negative or zero" );
    }
    this.smallScaleStep = scale;
  }

  public void setWidth( int ph ) {
    this.zoomFactorIndependentWidth = ph;
    setPreferredWidth( (int) Math.ceil( ph * zoomFactor ) );
  }

  /**
   * Sets a specified zoom factor for the ruler. 
   * A zoom factor of 1 means that one pixel represents 1 millimeter.
   * @param zoomFactor the zoom factor
   * @throws java.lang.IllegalArgumentException if the zoomfactor is negative
   */
  public void setZoomFactor( double zoomFactor ) {
    if( zoomFactor <= 0 ) {
      throw new java.lang.IllegalArgumentException( "Zoomfactor negative" );
    }
    this.zoomFactor = 1000 * zoomFactor;
  }
}
