package ds.ca;

/**
 * A Stair-Cell is special type of cell and therefore inherits properties and methods
 * from the abstract class Cell. Stair-Cells usually have a lower Speed-Factor than 
 * other cells and therefore individuals usually move slower across this type
 * of cell. 
 * @author marcel
 *
 */
public class StairCell extends Cell implements Cloneable
{
	/**
	 * Constant defining the standard Speed-Factor of a Stair-Cell, which is
	 * usually < 1.
	 */
	public static final double STANDARD_STAIRCELL_UP_SPEEDFACTOR = 0.5d;
	public static final double STANDARD_STAIRCELL_DOWN_SPEEDFACTOR = 0.6d;
	//public static final double STANDARD_ROOMCELL_SPEEDFACTOR = 1d;
	
	private double speedFactorUp;
	private double speedFactorDown;
	
    /**
     * This constructor creates an empty Stair-Cell with the standard Speed-Factor
     * used for this special cell-type.
     * @param x x-coordinate of the cell in the room, 0 <= x <= width-1
     * @param y y-coordinate of the cell in the room, 0 <= y <= height-1
     */
    public StairCell (int x, int y) 
    {    	
    	this(null, RoomCell.STANDARD_ROOMCELL_SPEEDFACTOR, x, y);
    	this.setDownSpeedFactor(STANDARD_STAIRCELL_DOWN_SPEEDFACTOR);
    	this.setUpSpeedFactor(STANDARD_STAIRCELL_UP_SPEEDFACTOR);    	
    }
    
    /**
     * Constructor defining an empty cell with a manual-set Speed-Factor.
     * @param speedFactor Defines how fast the cell can be crossed. The value should
     * be a rational number greater than or equal to 0 and smaller or equal to 1. 
     * Otherwise the standard value "STANDARD_STAIRCELL_SPEEDFACTOR" is set.
     * @param x x-coordinate of the cell in the room, 0 <= x <= width-1
     * @param y y-coordinate of the cell in the room, 0 <= y <= height-1
     */
    public StairCell(double speedFactor, int x, int y)
    {
    	this(null, speedFactor, x, y);
    	this.setDownSpeedFactor(STANDARD_STAIRCELL_DOWN_SPEEDFACTOR);
    	this.setUpSpeedFactor(STANDARD_STAIRCELL_UP_SPEEDFACTOR);  
    }
    
    public StairCell(double speedFactor, double speedFactorUp, double speedFactorDown,int x, int y)
    {
    	this(null, speedFactor, x, y);
        graphicalRepresentation = '/';
    	this.setDownSpeedFactor(speedFactorDown);
    	this.setUpSpeedFactor(speedFactorUp);  
    }
    
    /**
     * Constructor defining the value of individual. The value of SpeedFactor
     * will be the standard value used for Stair-Cells.
     * @param individual Defines the individual that occupies the cell. If the cell
     * is not occupied, the value is set to "null".
     * @param x x-coordinate of the cell in the room, 0 <= x <= width-1
     * @param y y-coordinate of the cell in the room, 0 <= y <= height-1
     */
    public StairCell(Individual individual, int x, int y)
    {
    	this(individual, RoomCell.STANDARD_ROOMCELL_SPEEDFACTOR, x, y);
    	this.setDownSpeedFactor(STANDARD_STAIRCELL_DOWN_SPEEDFACTOR);
    	this.setUpSpeedFactor(STANDARD_STAIRCELL_UP_SPEEDFACTOR);  
    }

    /**
     * Constructor defining the values of individual and speedFactor.
     * @param individual Defines the individual that occupies the cell. If the cell
     * is not occupied, the value is set to "null".
     * @param speedFactor Defines how fast the cell can be crossed. The value should
     * be a rational number greater than 0 and smaller or equal to 1. Otherwise the
     * standard value "STANDARD_STAIRCELL_SPEEDFACTOR" is set.
     * @param x x-coordinate of the cell in the room, 0 <= x <= width-1
     * @param y y-coordinate of the cell in the room, 0 <= y <= height-1
     */
    public StairCell(Individual individual, double speedFactor, int x, int y){
        this(individual, speedFactor, x, y, null);
    	this.setDownSpeedFactor(STANDARD_STAIRCELL_DOWN_SPEEDFACTOR);
    	this.setUpSpeedFactor(STANDARD_STAIRCELL_UP_SPEEDFACTOR); 
    }
    
    
    public StairCell(Individual individual, double speedFactor, int x, int y, Room room)
    {
    	super(individual, speedFactor, x, y, room);
        graphicalRepresentation = '/';
    	this.setDownSpeedFactor(STANDARD_STAIRCELL_DOWN_SPEEDFACTOR);
    	this.setUpSpeedFactor(STANDARD_STAIRCELL_UP_SPEEDFACTOR);  
    }
    
    public StairCell(Individual individual, double speedFactor, double speedFactorUp, double speedFactorDown,int x, int y, Room room)
    {
    	super(individual, speedFactor, x, y, room);
        graphicalRepresentation = '/';
    	this.setDownSpeedFactor(speedFactorDown);
    	this.setUpSpeedFactor(speedFactorUp);  
    }
    
    /**
     * Changes the Speed-Factor of the Room-Cell to the specified value.
     * @param speedFactor Defines how fast the cell can be crossed. The value should
     * be a rational number greater than or equal to 0 and smaller or equal to 1. 
     * Otherwise the standard value "STANDARD_ROOMCELL_SPEEDFACTOR" is set.
     */
    public void setSpeedFactor(double speedFactor)
    {
    	if ((speedFactor >= 0) && (speedFactor <= 1))
    		this.speedFactor = speedFactor;
    	else
    		this.speedFactor = RoomCell.STANDARD_ROOMCELL_SPEEDFACTOR;
    }
    
    /**
     * Changes the Speed-Factor of the Stair-Cell to the specified value.
     * @param speedFactor Defines how fast the cell can be crossed. The value should
     * be a rational number greater than or equal to 0 and smaller or equal to 1. 
     * Otherwise the standard value "STANDARD_STAIRCELL_UP_SPEEDFACTOR" is set.
     */
    public void setUpSpeedFactor(double speedFactor)
    {
    	if ((speedFactor >= 0) && (speedFactor <= 1))
    		this.speedFactorUp = speedFactor;
    	else
    		this.speedFactorUp = StairCell.STANDARD_STAIRCELL_UP_SPEEDFACTOR;
    }
    
    /**
     * Changes the Speed-Factor of the Stair-Cell to the specified value.
     * @param speedFactor Defines how fast the cell can be crossed. The value should
     * be a rational number greater than or equal to 0 and smaller or equal to 1. 
     * Otherwise the standard value "STANDARD_STAIRCELL_DOWN_SPEEDFACTOR" is set.
     */
    public void setDownSpeedFactor(double speedFactor)
    {
    	if ((speedFactor >= 0) && (speedFactor <= 1))
    		this.speedFactorDown = speedFactor;
    	else
    		this.speedFactorDown = StairCell.STANDARD_STAIRCELL_DOWN_SPEEDFACTOR;
    }
    
    public double getSpeedFactorDown(){
    	return speedFactorDown;
    }
    
    public double getSpeedFactorUp(){
    	return speedFactorUp;
    }
    /**
     * Returns a copy of itself as a new Object.
     */
    public StairCell clone(){
        return clone(false);
    }   
    
    public StairCell clone(boolean cloneIndividual)
    {
    	StairCell aClone = new StairCell(this.getX(), this.getY());
    	basicClone(aClone, cloneIndividual);
    	return aClone;
    }
    
    public String toString(){
    	return "T;"+super.toString();
    }

}

