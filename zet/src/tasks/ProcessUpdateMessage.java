/*
 * ProcessUpdateMessage.java
 * Created on 27.01.2008, 02:36:44
 */

package tasks;

/**
 * 
 * @author Jan-Philipp Kappmeier
 */
public class ProcessUpdateMessage {
		public int progress = 0;
		public String taskName = "";
		public String taskProgressInformation = "";
		public String taskDetailedProgressInformation = "";
		
		public ProcessUpdateMessage( int progress, String taskName, String taskProgressInformation, String taskDetailedProgressInformation ) {
			this.progress = progress;
			this.taskName = taskName;
			this.taskProgressInformation = taskProgressInformation;
			this.taskDetailedProgressInformation = taskDetailedProgressInformation;
		}
}