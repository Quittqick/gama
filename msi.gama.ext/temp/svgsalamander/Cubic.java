/*
 * MoveTo.java
 *
 *
 * The Salamander Project - 2D and 3D graphics libraries in Java Copyright (C) 2004 Mark McKay
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Mark McKay can be contacted at mark@kitfox.com. Salamander and other projects can be found at http://www.kitfox.com
 *
 * Created on January 26, 2004, 8:40 PM
 */

package msi.gama.ext.svgsalamander;

// import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import java.awt.geom.GeneralPath;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class Cubic extends PathCommand {

	public float k1x = 0f;
	public float k1y = 0f;
	public float k2x = 0f;
	public float k2y = 0f;

	public Cubic(final boolean isRelative, final float k1x, final float k1y, final float k2x, final float k2y,
			final float x, final float y) {
		super(x, y, 6, isRelative);
		this.k1x = k1x;
		this.k1y = k1y;
		this.k2x = k2x;
		this.k2y = k2y;
	}

	@Override
	public void appendPath(final GeneralPath path, final BuildHistory hist) {
		float offx = isRelative ? hist.history[0].x : 0f;
		float offy = isRelative ? hist.history[0].y : 0f;

		path.curveTo(k1x + offx, k1y + offy, k2x + offx, k2y + offy, x + offx, y + offy);
		hist.setPointAndKnot(x + offx, y + offy, k2x + offx, k2y + offy);
	}
}