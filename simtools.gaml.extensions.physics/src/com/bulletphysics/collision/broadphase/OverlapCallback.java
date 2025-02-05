/*******************************************************************************************************
 *
 * OverlapCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.broadphase;

/**
 * OverlapCallback is used when processing all overlapping pairs in broadphase.
 *
 * @see OverlappingPairCache#processAllOverlappingPairs
 * @author jezek2
 */
@FunctionalInterface
public interface OverlapCallback {

	/**
	 * Process overlap.
	 *
	 * @param pair the pair
	 * @return true, if successful
	 */
	// return true for deletion of the pair
	boolean processOverlap( BroadphasePair pair);

}
