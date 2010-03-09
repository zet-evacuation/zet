/**
 * GeneralRandom.java
 */
package de.tu_berlin.math.coga.rndutils.generators;

/**
 * <p>An interface defining the methods a random generator should provide. The most
 * of them are already defined by {@link java.util.Random}, for sake of
 * completeness some methods are added.</p>
 * <p>Wrapper classes that provide this interface to some other random
 * generators should implement these additional methods.</p>
 * <p>Note that no quality measures or implementation details are given in the
 * description as these may differ from the method of generating random values.
 * Syncronization also follows from the random generator, but not in general.</p>
 * <p>The random generators should provide a name and a short description, so
 * that they can be used in a list of random generators and an (experienced)
 * user can chose the generator he likes best (or that fits best for his
 * application).</p>
 * @author Jan-Philipp Kappmeier
 */
public interface GeneralRandom {

	/**
	 * Sets the seed of the random number generator using a single long seed. This
	 * method defines a unique state for the random generator so that it can be
	 * recreated again by setting the same seed again.
	 * @param seed the seed
	 */
	public void setSeed( long seed );

	/**
	 * Returns the next pseudorandom, uniformly distributed boolean value from
	 * the random number generator's sequence. The values {@code true} and
	 * {@code false} are produced with (approximately) equal probability.
	 * @return the next pseudorandom, uniformly distributed boolean value from this random number generator's sequence
	 */
	public boolean nextBoolean();

	/**
	 * Generates random bytes and places them into the supplied {@code byte} array.
	 * The number of random bytes produced is equal to the length of the array.
	 * @param bytes the non-null byte array in which to put the random bytes
	 */
	public void nextBytes( byte[] bytes );

	/**
	 * The next pseudorandom, uniformly distributed {@code byte} value from this
	 * random number generator's sequence. All 2^8 possible {@code byte} values
	 * are returned with approximately equal probability.
	 * @return the next pseudorandom, uniformly distributed byte value from this random number generator's sequence
	 */
	public byte nextByte();


	/**
	 * The next pseudorandom, uniformly distributed {@code char} value from this
	 * random number generator's sequence. All 2^16 possible {@code char} values
	 * are returned with approximately equal probability.
	 * @return the next pseudorandom, uniformly distributed char value from this random number generator's sequence
	 */
	public char nextChar();

	/**
	 * The next pseudorandom, uniformly distributed {@code short} value from
	 * this random number generator's sequence. All 2^16 possible {@code short}
	 * values are returned with approximately equal probability.
	 * @return the next pseudorandom, uniformly distributed short value from this random number generator's sequence
	 */
	public short nextShort();

	/**
	 * The next pseudorandom, uniformly distributed {@code long} value from this
	 * random number generator's sequence. All 2^64 possible {@code long} values
	 * are returned with approximately equal probability.
	 * @return the next pseudorandom, uniformly distributed long value from this random number generator's sequence
	 */
	public int nextInt();

	/**
	 * The next pseudorandom, uniformly distributed {@code int} value from this
	 * random number generator's sequence. It lies between 0 inclusive and
	 * {@code n} exlusive.
	 * @return the next pseudorandom, uniformly distributed int value from this random number generator's sequence
	 */
	public int nextInt( int n );

	/**
	 * The next pseudorandom, uniformly distributed {@code long} value from this
	 * random number generator's sequence. All 2^64 possible {@code long} values
	 * are returned with approximately equal probability.
	 * @return the next pseudorandom, uniformly distributed long value from this random number generator's sequence
	 */
	public long nextLong();

	/**
	 * Returns the next pseudorandom, uniformly distributed {@code float} value
	 * between {@code 0.0} and {@code 1.0} from the random number generator's
	 * sequence. One {@code float} value is chosen approximately uniformly
	 * with 0.0 inclusive and 1.0 exclusive. All 2^24 float values of the form
	 * m * 2^-24 (with positive inegral m) are produced with (approximatley)
	 * equal probability.
	 * @return the next pseudorandom, uniformly distributed float value between 0.0 and 1.0 from this random number generator's sequence
	 */
	public float nextFloat();

	/**
	 * Returns the next pseudorandom, uniformly distributed {@code double} value
	 * between {@code 0.0} and {@code 1.0} from the random number generator's
	 * sequence. One {@code double} value is chosen approximately uniformly
	 * with 0.0 inclusive and 1.0 exclusive. All 2^53 float values of the form
	 * m * 2^-53 (with positive inegral m) are produced with (approximatley)
	 * equal probability.
	 * @return the next pseudorandom, uniformly distributed double value between 0.0 and 1.0 from this random number generator's sequence
	 */
	public double nextDouble();

	/**
	 * Returns the next pseudorandom, Gaussian or normally distributed double
	 * value with mean 0.0 and standard deviation 1.0 from this random number
	 * generator's sequence.
	 * @return the next pseudorandom, Gaussian or normally distributed double value with mean 0.0 and standard deviation 1.0 from this random number generator's sequence
	 */
	public double nextGaussian();

	/**
	 * Returns the name of the random generator.
	 * @return the name of the random generator
	 */
	public String getName();

	/**
	 * Returns a description of the random generator.
	 * @return a description of the random generator
	 */
	public String getDesc();
}
