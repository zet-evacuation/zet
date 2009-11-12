package de.tu_berlin.math.coga.rndutils.generators;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface GeneralRandom {

    public void setSeed( long seed );

		// do not give access to this method. it should be implemented by a
		// random generator, not by a wrapper interface.
		//protected int next(int bits);

		public void nextBytes( byte[] bytes );

    public int nextInt();

    public int nextInt( int n );

		public long nextLong();

    public boolean nextBoolean();

    public float nextFloat();

    public double nextDouble();

    public double nextGaussian();
	
		public String getName();
		
		public String getDesc();
}
