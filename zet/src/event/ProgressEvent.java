/*
 * ProgressEvent.java
 *
 */

package event;

import tasks.ProcessUpdateMessage;

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
