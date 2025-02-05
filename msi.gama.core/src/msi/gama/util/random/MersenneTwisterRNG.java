/*******************************************************************************************************
 *
 * MersenneTwisterRNG.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package msi.gama.util.random;

import msi.gama.common.util.RandomUtils;

/**
 * <p>
 * Random number generator based on the
 * <a href="http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/emt.html" target=
 * "_top">Mersenne Twister</a> algorithm developed by Makoto Matsumoto and
 * Takuji Nishimura.
 * </p>
 *
 * <p>
 * This is a very fast random number generator with good statistical properties
 * (it passes the full DIEHARD suite). This is the best RNG for most
 * experiments. If a non-linear generator is required, use the slower
 * {@link AESCounterRNG} RNG.
 * </p>
 *
 * <p>
 * This PRNG is deterministic, which can be advantageous for testing purposes
 * since the output is repeatable. If multiple instances of this class are
 * created with the same seed they will all have identical output.
 * </p>
 *
 * <p>
 * This code is translated from the original C version and assumes that we will
 * always seed from an array of bytes. I don't pretend to know the meanings of
 * the magic numbers or how it works, it just does.
 * </p>
 *
 * @author Makoto Matsumoto and Takuji Nishimura (original C version)
 * @author Daniel Dyer (Java port)
 */
public class MersenneTwisterRNG extends GamaRNG {

	// The actual seed size isn't that important, but it should be a multiple of
	/** The Constant SEED_SIZE_BYTES. */
	// 4.
	private static final int SEED_SIZE_BYTES = 16;

	/** The Constant N. */
	// Magic numbers from original C version.
	private static final int N = 624;
	
	/** The Constant M. */
	private static final int M = 397;
	
	/** The Constant MAG01. */
	private static final int[] MAG01 = { 0, 0x9908b0df };
	
	/** The Constant UPPER_MASK. */
	private static final int UPPER_MASK = 0x80000000;
	
	/** The Constant LOWER_MASK. */
	private static final int LOWER_MASK = 0x7fffffff;
	
	/** The Constant BOOTSTRAP_SEED. */
	private static final int BOOTSTRAP_SEED = 19650218;
	
	/** The Constant BOOTSTRAP_FACTOR. */
	private static final int BOOTSTRAP_FACTOR = 1812433253;
	
	/** The Constant SEED_FACTOR1. */
	private static final int SEED_FACTOR1 = 1664525;
	
	/** The Constant SEED_FACTOR2. */
	private static final int SEED_FACTOR2 = 1566083941;
	
	/** The Constant GENERATE_MASK1. */
	private static final int GENERATE_MASK1 = 0x9d2c5680;
	
	/** The Constant GENERATE_MASK2. */
	private static final int GENERATE_MASK2 = 0xefc60000;

	/** The seed. */
	private final byte[] seed;

	// Lock to prevent concurrent modification of the RNG's internal state.
	// private final ReentrantLock lock = new ReentrantLock();

	/** The mt. */
	private final int[] mt = new int[N]; // State vector.
	
	/** The mt index. */
	private int mtIndex = 0; // Index into state vector.

	/**
	 * Creates a new RNG and seeds it using the default seeding strategy.
	 */
	// public MersenneTwisterRNG() {
	// this(DefaultSeedGenerator.getInstance().generateSeed(SEED_SIZE_BYTES));
	// }

	/**
	 * Seed the RNG using the provided seed generation strategy.
	 * 
	 * @param seedGenerator
	 *            The seed generation strategy that will provide the seed value
	 *            for this RNG.
	 * @throws SeedException
	 *             If there is a problem generating a seed.
	 */
	public MersenneTwisterRNG(final RandomUtils seedGenerator) {
		this(seedGenerator.generateSeed(SEED_SIZE_BYTES));
	}

	/**
	 * Creates an RNG and seeds it with the specified seed data.
	 * 
	 * @param seed
	 *            The seed data used to initialise the RNG.
	 */
	public MersenneTwisterRNG(final byte[] seed) {
		if (seed == null || seed.length != SEED_SIZE_BYTES) {
			throw new IllegalArgumentException("Mersenne Twister RNG requires a 128-bit (16-byte) seed.");
		}
		this.seed = seed.clone();

		final int[] seedInts = convertBytesToInts(this.seed);

		// This section is translated from the init_genrand code in the C
		// version.
		mt[0] = BOOTSTRAP_SEED;
		for (mtIndex = 1; mtIndex < N; mtIndex++) {
			mt[mtIndex] = BOOTSTRAP_FACTOR * (mt[mtIndex - 1] ^ mt[mtIndex - 1] >>> 30) + mtIndex;
		}

		// This section is translated from the init_by_array code in the C
		// version.
		int i = 1;
		int j = 0;
		for (int k = Math.max(N, seedInts.length); k > 0; k--) {
			mt[i] = (mt[i] ^ (mt[i - 1] ^ mt[i - 1] >>> 30) * SEED_FACTOR1) + seedInts[j] + j;
			i++;
			j++;
			if (i >= N) {
				mt[0] = mt[N - 1];
				i = 1;
			}
			if (j >= seedInts.length) {
				j = 0;
			}
		}
		for (int k = N - 1; k > 0; k--) {
			mt[i] = (mt[i] ^ (mt[i - 1] ^ mt[i - 1] >>> 30) * SEED_FACTOR2) - i;
			i++;
			if (i >= N) {
				mt[0] = mt[N - 1];
				i = 1;
			}
		}
		mt[0] = UPPER_MASK; // Most significant bit is 1 - guarantees non-zero
							// initial array.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getSeed() {
		return seed.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final int next(final int bits) {
		usage++;
		int y;
		// try {
		// lock.lock();

		if (mtIndex >= N) // Generate N ints at a time.
		{
			int kk;
			for (kk = 0; kk < N - M; kk++) {
				y = mt[kk] & UPPER_MASK | mt[kk + 1] & LOWER_MASK;
				mt[kk] = mt[kk + M] ^ y >>> 1 ^ MAG01[y & 0x1];
			}
			for (; kk < N - 1; kk++) {
				y = mt[kk] & UPPER_MASK | mt[kk + 1] & LOWER_MASK;
				mt[kk] = mt[kk + M - N] ^ y >>> 1 ^ MAG01[y & 0x1];
			}
			y = mt[N - 1] & UPPER_MASK | mt[0] & LOWER_MASK;
			mt[N - 1] = mt[M - 1] ^ y >>> 1 ^ MAG01[y & 0x1];

			mtIndex = 0;
		}

		y = mt[mtIndex++];

		// } finally {
		// lock.unlock();
		// }
		// Tempering
		y ^= y >>> 11;
		y ^= y << 7 & GENERATE_MASK1;
		y ^= y << 15 & GENERATE_MASK2;
		y ^= y >>> 18;

		return y >>> 32 - bits;
	}
}
