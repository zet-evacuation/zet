public class Main
{
    	public static void main(String[] args)
	{
		int iterations = 10;

		long multiplications = 1000000000L; // 10 million

		long r = 0;

		long accum = 0;

		for(int i = 0; i < iterations; i++)
		{
			Long start = System.currentTimeMillis();


			/** Standard. */
//			for(long m = 0; m < multiplications; m++) // Average execution time: [15 ms]
//			{
//				r += i * i;
//			}

			/** Optimization, not having to load 'multiplications' into a register. */
//			for(int m = multiplications; --m >= 0;) // Average execution time: [13 ms, 14 ms]
//			{
//				r += i * i;
//			}

			/** Optimization, removed a GOTO instruction. */
			long m = multiplications; // Average execution time: [12 ms, 13ms]
			do
			{
				r += i * i;
			} while(--m > 0);

			Long end = System.currentTimeMillis();

			accum += end - start;
		}

		System.out.println("Iterations: " + iterations + ", Multiplications: " +
				multiplications + ", Average ms: " + accum/iterations + " Result: " + r);
    }
}