/*  
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.algorithm.scheduling;

import org.zetool.container.mapping.Identifiable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Job implements Identifiable {
	final static Job DUMMY = new Job( -1 );
	int work;
	int initRessource;
	int minRessources;
	int maxRessources;
	long minTime;
	long maxTime;
	ArrayList<Job> predecessors = new ArrayList<>();
	ArrayList<Job> successors = new ArrayList<>();
	Ressource ressource;
	int id;
	
	public Job( int id ) {
		this( 1, 1, 1, 0, 0, 0, Ressource.DUMMY, id );
	}

	public Job( int initRessource, int minRessource, int maxRessource, long minTime, long maxTime, int work, Ressource ressource, int id ) {
		this.initRessource = initRessource;
		this.minRessources = minRessource;
		this.maxRessources = maxRessource;
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.work = work;
		this.ressource = ressource;
		this.id = id;
	}
	
	public void addPredecessor( Job pred ) {
		predecessors.add( pred );
	}
	
	public void addSuccessor( Job succ ) {
		successors.add( succ );
	}
	
	public double getDuration( int r ) {
		double x = (double)r/initRessource;
		double log = Math.log10( x ) / Math.log10( 2 );
		double f1 = Math.pow( ressource.stretchFactor, log );
		double f2 = (double)work/initRessource;
		return f1 * f2;
	}
	
	public double getMinDuration() {
		return getDuration( maxRessources );
	}
	
	public double getMaxDuration() {
		return getDuration( minRessources );
	}

	public List<Job> getPredecessors() {
		return Collections.unmodifiableList( predecessors );
	}

	public int id() {
		return id;
	}

	public List<Job> getSuccessors() {
		return Collections.unmodifiableList( successors );
	}

	public String toString() {
		return "Job{" + "id=" + id + '}';
	}

	@Override
	public Identifiable clone() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}
	
	public double getCostRate() {
		double c1 = getMaxDuration()*minRessources*ressource.cost;
		double c2 = getMinDuration()*maxRessources*ressource.cost;
		//double cost = (Math.max( c1, c2)-Math.min( c1, c2))/(A.getMaxDuration()-A.getMinDuration());
		double cost = (int)((c2-c1)/(getMaxDuration()-getMinDuration()));
		return cost;
	}
	
	
}
