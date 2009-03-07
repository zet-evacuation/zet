package util.random;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface GeneralRandom {

    public void setSeed( long seed );

		// do not give access to this method. it should be implemented by a
		// random generator, not by a wrapper interface.
		//protected int next(int bits);

		void nextBytes( byte[] bytes );

    int nextInt();

    int nextInt( int n );

		long nextLong();

    boolean nextBoolean();

    float nextFloat();

    double nextDouble();

    double nextGaussian();
	
		String getName();
		
		String getDesc();
}
