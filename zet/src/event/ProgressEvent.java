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
 * ProgressEvent.java
 *
 */

package event;

import batch.tasks.ProcessUpdateMessage;

/**
 * This is a simple messaging event to submit status messages or error messages.
 * @param <S> 
 * @author Jan-Philipp Kappmeier
 */
public class ProgressEvent<S> implements Event {

	protected S source;
	private ProcessUpdateMessage message;

	public ProgressEvent( S source, ProcessUpdateMessage message ) {
		this.source = source;
		this.message = message;
	}

	public S getSource() {
		return source;
	}

	public ProcessUpdateMessage getProcessMessage() {
		return message;
	}
}


/**
 *
 */
//public class ProgressEvent<S> implements Event {
//    
//    protected S source;
//    
//    protected Task[] tasks;
//    
//    protected Task currentTask;
//
//    public ProgressEvent(S source, Task task) {
//        this.source = source;
//        this.tasks = new Task[1];
//        this.tasks[0] = task;
//        this.currentTask = task;
//    }
//    
//    public ProgressEvent(S source, Task[] tasks, Task currentTask) {
//        this.source = source;
//        this.tasks = tasks;
//        this.currentTask = currentTask;
//        if (!Arrays.asList(tasks).contains(currentTask)) {
//            throw new IllegalArgumentException("The current task must be part of the task list.");
//        }
//    }
//
//    public Task getCurrentTask() {
//        return currentTask;
//    }
//
//    public int getLength() {
//        int result = 0;
//        for (Task task : tasks) {
//            result += task.getLength();         
//        }
//        return result;
//    }    
//    
//    public int getProgress() {
//        int result = 0;
//        for (Task task : tasks) {
//            result += task.getProgress();
//            if (task == currentTask) {
//                return result;
//            }            
//        }
//        throw new AssertionError("This should not happen.");
//    }
//
//    public S getSource() {
//        return source;
//    }
//
//    public Task[] getTasks() {
//        return tasks;
//    }   
//    
//    public static class Task {
//        
//        protected String description;
//        
//        protected int length;
//        
//        protected int progress;
//
//        public Task(String description, int length, int progress) {
//            this.description = description;
//            this.length = length;
//            this.progress = progress;
//        }
//
//        public String getDescription() {
//            return description;
//        }
//
//        public int getLength() {
//            return length;
//        }
//
//        public int getProgress() {
//            return progress;
//        }       
//    }
//}
