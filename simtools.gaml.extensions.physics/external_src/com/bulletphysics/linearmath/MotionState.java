/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library Copyright (c) 2003-2008 Erwin Coumans
 * http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held
 * liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter
 * it and redistribute it freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not
 * required. 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the
 * original software. 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.linearmath;

/**
 * MotionState allows the dynamics world to synchronize the updated world transforms with graphics. For optimizations,
 * potentially only moving objects get synchronized (using {@link #setWorldTransform setWorldTransform} method).
 *
 * @author jezek2
 */

public interface MotionState {

	/**
	 * Returns world transform.
	 */
	Transform getWorldTransform(Transform out);

	/**
	 * Sets world transform. This method is called by JBullet whenever an active object represented by this MotionState
	 * is moved or rotated.
	 */
	void setWorldTransform(Transform worldTrans);

}
