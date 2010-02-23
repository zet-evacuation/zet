/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
package ds.ca.results;

import java.util.Iterator;
import java.util.Vector;
import ds.ca.InitialConfiguration;

/**
 * This class serves as a container for visualization results of a simulation
 * with a cellular automaton. We store an initial configuration of the 
 * automaton together with actions which reflect changes on the configuration.
 * By executing the actions in the same order as they were recorded, one can 
 * "replay" the simulation without re-computation of the cellular automaton.
 *  
 * This class also provides the methods for replaying the recorded actions
 * in the correct order. To do so, we provide a "current time step" in the
 * recording. You can move this time step forward and backwards in the
 * recording to see the actions at different points in time. The time step
 * is initialized with <code>-1</code>, yet the recording starts at time 
 * <code>0</code>! This enables you to iterate through all time steps by 
 * repeatedly calling <code>nextActions()</code> (in particular, you first get
 * the actions at time 0 if you do so!).
 * 
 * You can serialize an instance of this class to disc with the 
 * <code>CAVisualResultsParser</code>. Thus, you can use this class
 * to store simulation results.
 *  
 * @author Daniel Pluempe
 *
 */
public class VisualResultsRecording{
    
    /**
     * The initial configuration of a cellular automaton
     */
    private InitialConfiguration initialConfig;
    
    /**
     * A vector which stores a vector of actions for every time step
     */
    private Vector<Vector<Action>> actions;
    
    /**
     * The current time step
     */
    private int curTime;  
    
    private long maxDynamicPotential = -1;
    
    /**
     * Creates a new <code>VisualResultsRecording</code> instance. 
     * @param initialConfig The initial configuration of a cellular automaton
     * @param actions A vector which contains a vector of actions for every
     * time step. 
     */    
    public VisualResultsRecording(InitialConfiguration initialConfig, Vector<Vector<Action>> actions){
        this.initialConfig = initialConfig;
        this.actions = actions;
        this.curTime = -1;
    }
    
    /**
     * Returns the initial configuration corresponding to the actions
     * stored in this class. The parameters of the actions stored here
     * refer to objects in this initial configuration.
     * @return The initial configuration of a cellular automaton that
     * provides the objects for the recorded actions.
     */
    public InitialConfiguration getInitialConfig(){
        return initialConfig;
    }
        
    /**
     * Tells you whether there is a next time step (i.e. if you
     * have not yet reached the end of the recording).
     * @return <code>true</code> if the recording extends
     * to the next time step or <code>false</code> otherwise.
     */
    public boolean hasNext(){
        return (curTime < actions.size() -1);
    }
    
    /**
     * Tells you whether there is a previous time step (i.e. if you
     * have not yet reached the start of the recording).
     * @return <code>true</code> if the recording extends
     * to the previous time step or <code>false</code> otherwise.
     */  
    public boolean hasPrevious(){
        return (curTime > 0);
    }

    /**
     * Tells you the current time step.
     * @return The current time step. The time step is initialized with
     * <code>-1</code>
     */
    public int getCurTime(){
        return curTime;
    }
    
    /** 
     * Rewinds the recording to its start, i.e. resets the current time step 
     * to <code>-1</code>.
     */
    public void rewind(){
        //if(actions.size() > 0){
            curTime = -1;
        //}
    }
    
    /** 
     * Fast forwards the recording to its end, i.e. sets the current time step 
     * to <code>length-1</code>.
     */
    public void forward(){
        curTime = Math.max(0,actions.size() - 1);
    }
    
    /**
     * Sets the current time step to <code>time</code>.
     * @param time The time you want to jump to.
     */
    public void jumpToTime(int time){
        if(time >= actions.size() || time < 0){
            throw new IndexOutOfBoundsException("Index " 
                    + time + " is not a valid timestep.");
        }
        
        curTime = time;
    }    
    
    /**
     * Gets all actions recorded at the next time step in a vector of
     * actions. Returns the actions at time step 0 at first call due
     * to the initialization of time step. 
     * Advances current time step by one.
     * @return A vector with all actions at the next time step  
     */
    public Vector<Action> nextActions(){
        if(curTime >= actions.size() - 1){
            throw new IndexOutOfBoundsException(
                    "There is no next action (from Index" + curTime+")");
        }
            
        curTime++;
        return actions.get(curTime);
    }
    
    /**
     * Gets all actions for the current time step in a vector. Note that
     * the current time step is initialized with <code>-1</code> so you need
     * to either call <code>jumpToTime()</code> or <code>nextActions()</code>
     * at least once before you can use this method.
     * 
     * @return A vector with all actions recorded at the current time
     * step. 
     */    
    public Vector<Action> curActions(){
        if(curTime < 0){
            throw new IndexOutOfBoundsException("Please call nextActions() once" +
            		"before calling this method.");
        }
        
        return actions.get(curTime);
    }
    
    /**
     * Gets all actions for the previous time step in a vector. Decreases
     * the current time step by one.
     * @return A vector with all actions recorded at the previous time step.
     */
    public Vector<Action> prevActions(){
        if(curTime <= 0){
            throw new IndexOutOfBoundsException(
                    "There is no previous action (from Index " + curTime +")");
        }
        
        curTime--;
        return actions.get(curTime);
    }
    
    /**
     * Removes all actions of the given type from the current time step of the
     * recording and returns all removed actions in a <code>Vector</code>
     * @param <T> An action type. All removed actions will be a sub-type of this type.
     * @param actionType All actions of this type will be removed. Must be a sub-type of T.
     * @return All actions that have been removed in the same order as they were in the
     * recording. 
     */
    @SuppressWarnings("unchecked") 
    public <T extends Action> Vector<T> filterActions(Class<? extends T> actionType){
        Vector<T> filteredActions = new Vector<T>();
        
        Iterator<Action> actions = curActions().iterator();
        while(actions.hasNext()){
            Action nextAction = actions.next(); 
            if(actionType.isInstance(nextAction)){
                filteredActions.add((T)nextAction);
                //actions.remove();
            }
        }
        
        return filteredActions;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Action> Vector<T> filterAllActions(Class<? extends T> actionType){
        Vector<T> filteredActions = new Vector<T>();
        
        for(Vector<Action> actionsForStep : this.actions){
            for(Action action : actionsForStep){
                if(actionType.isInstance(action)){
                    filteredActions.add((T)action);
                }
            }
        }
        
        return filteredActions;
    }
   
    /**
     * Gets the length of the recording, i.e. the last time step plus one.
     * @return The length of the recording.
     */
    public int length(){
        return actions.size();
    }
    
    public long getMaxDynamicPotential(){
        if(maxDynamicPotential == -1){
            calculateMaxDynamicPotential();
        }
        return maxDynamicPotential;
    }
    
    private void calculateMaxDynamicPotential(){
        long maxDynamicPotential = 0;
        
        Vector<DynamicPotentialChangeAction> potentialChanges = filterAllActions(DynamicPotentialChangeAction.class);
        for(DynamicPotentialChangeAction change : potentialChanges){
            if(change.getNewPotentialValue() > maxDynamicPotential){
                maxDynamicPotential = change.getNewPotentialValue();
            }
        }
        
        this.maxDynamicPotential = maxDynamicPotential;
    }
}
