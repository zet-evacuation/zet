/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.algorithm.scheduling;

/**
 *
 * @author Jan-Philipp
 */
public class Ressource {
	public final static Ressource DUMMY = new Ressource( 1, 100, 1, true );
	double cost;
	double stretchFactor = 1;
	int maximalCapacity;
	boolean levelling;

	public Ressource( double cost, int maximalCapacity, double stretchFactor, boolean levelling ) {
		this.cost = cost;
		this.maximalCapacity = maximalCapacity;
		this.levelling = levelling;
		this.stretchFactor = stretchFactor;
	}
}
