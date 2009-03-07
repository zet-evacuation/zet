package ds.ca;

import ds.ca.results.DynamicPotentialChangeAction;
import ds.ca.results.VisualResultsRecorder;

/**
 * A DynamicPotential is special type of PotentialMap, of that exists only once in the PotentialManager.
 */

public class DynamicPotential extends PotentialMap {
    
   /**
    * Creates a DynamicPotential.
    */
   public DynamicPotential () {
        super();    
        this.clear();
   }
   
   @Override
   public void setPotential(Cell cell, double value) {
       super.setPotential(cell, value);
       VisualResultsRecorder.getInstance().recordAction(new DynamicPotentialChangeAction(cell, value));
   }

 	/**
	 * {@inheritDoc}
	 * The Potential value of the removed cell is saved as 0 in the
	 * {@link VisualResultsRecorder}.
	 * @param cell A Cell whose mapping should be removed
	 * @throws IllegalArgumentException if the cell is not contained in the map
	 */
	@Override
	public void deleteCell( Cell cell ) throws IllegalArgumentException {
		super.deleteCell( cell );
		VisualResultsRecorder.getInstance().recordAction( new DynamicPotentialChangeAction( cell, 0 ) );
	}
	 
   /**
    * Get the potential of a specified Cell.
    * The method returns 0 if you
    * try to get the potential of a cell that does not exists.
    * @param cell A cell which potential you want to know.
    * @return potential of the specified cell or -1 if the cell is not mapped by this potential
    */
   @Override
   public int getPotential (Cell cell) throws IllegalArgumentException{
       Double potential = cellToPotential.get(cell);
       if(potential == null){
           return 0;
       } else {
				 // TODO Potential long
           return (int)Math.round( potential );
       }
   }
   
   public void clear(){
	   cellToPotential.clear();
   }
}