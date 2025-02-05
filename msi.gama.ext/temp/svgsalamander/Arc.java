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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

/**
 * This is a little used SVG function, as most editors will save curves as Beziers. To reduce the need to rely on the
 * Batik library, this functionallity is being bypassed for the time being. In the future, it would be nice to extend
 * the GeneralPath command to include the arcTo ability provided by Batik.
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class Arc extends PathCommand {

	public float rx = 0f;
	public float ry = 0f;
	public float xAxisRot = 0f;
	public boolean largeArc = false;
	public boolean sweep = false;

	public Arc(final boolean isRelative, final float rx, final float ry, final float xAxisRot, final boolean largeArc,
			final boolean sweep, final float x, final float y) {
		super(x, y, 6, isRelative);
		this.rx = rx;
		this.ry = ry;
		this.xAxisRot = xAxisRot;
		this.largeArc = largeArc;
		this.sweep = sweep;
	}

	@Override
	public void appendPath(final GeneralPath path, final BuildHistory hist) {
		final float offx = isRelative ? hist.history[0].x : 0f;
		final float offy = isRelative ? hist.history[0].y : 0f;

		arcTo(path, rx, ry, xAxisRot, largeArc, sweep, x + offx, y + offy, hist.history[0].x, hist.history[0].y);
		// path.lineTo(x + offx, y + offy);
		hist.setPoint(x + offx, y + offy);
	}

	/**
	 * Adds an elliptical arc, defined by two radii, an angle from the x-axis, a flag to choose the large arc or not, a
	 * flag to indicate if we increase or decrease the angles and the final point of the arc.
	 *
	 * @param rx
	 *            the x radius of the ellipse
	 * @param ry
	 *            the y radius of the ellipse
	 *
	 * @param angle
	 *            the angle from the x-axis of the current coordinate system to the x-axis of the ellipse in degrees.
	 *
	 * @param largeArcFlag
	 *            the large arc flag. If true the arc spanning less than or equal to 180 degrees is chosen, otherwise
	 *            the arc spanning greater than 180 degrees is chosen
	 *
	 * @param sweepFlag
	 *            the sweep flag. If true the line joining center to arc sweeps through decreasing angles otherwise it
	 *            sweeps through increasing angles
	 *
	 * @param x
	 *            the absolute x coordinate of the final point of the arc.
	 * @param y
	 *            the absolute y coordinate of the final point of the arc.
	 * @param x0
	 *            - The absolute x coordinate of the initial point of the arc.
	 * @param y0
	 *            - The absolute y coordinate of the initial point of the arc.
	 */
	public void arcTo(final GeneralPath path, final float rx, final float ry, final float angle,
			final boolean largeArcFlag, final boolean sweepFlag, final float x, final float y, final float x0,
			final float y0) {

		// Ensure radii are valid
		if (rx == 0 || ry == 0) {
			path.lineTo(x, y);
			return;
		}

		if (x0 == x && y0 == y) // If the endpoints (x, y) and (x0, y0) are identical, then this
			// is equivalent to omitting the elliptical arc segment entirely.
			return;

		final Arc2D arc = computeArc(x0, y0, rx, ry, angle, largeArcFlag, sweepFlag, x, y);

		final AffineTransform t =
				AffineTransform.getRotateInstance(Math.toRadians(angle), arc.getCenterX(), arc.getCenterY());
		final Shape s = t.createTransformedShape(arc);
		path.append(s, true);
	}

	/**
	 * This constructs an unrotated Arc2D from the SVG specification of an Elliptical arc. To get the final arc you need
	 * to apply a rotation transform such as:
	 *
	 * AffineTransform.getRotateInstance (angle, arc.getX()+arc.getWidth()/2, arc.getY()+arc.getHeight()/2);
	 */
	public static Arc2D computeArc(final double x0, final double y0, final double rrx, final double rry,
			final double angleInDegrees, final boolean largeArcFlag, final boolean sweepFlag, final double x,
			final double y) {
		//
		// Elliptical arc implementation based on the SVG specification notes
		//

		// Compute the half distance between the current and the final point
		final double dx2 = (x0 - x) / 2.0;
		final double dy2 = (y0 - y) / 2.0;
		// Convert angle from degrees to radians
		final double angle = Math.toRadians(angleInDegrees % 360.0);
		final double cosAngle = Math.cos(angle);
		final double sinAngle = Math.sin(angle);

		//
		// Step 1 : Compute (x1, y1)
		//
		final double x1 = cosAngle * dx2 + sinAngle * dy2;
		final double y1 = -sinAngle * dx2 + cosAngle * dy2;
		// Ensure radii are large enough
		double rx = Math.abs(rrx);
		double ry = Math.abs(rry);
		double Prx = rx * rx;
		double Pry = ry * ry;
		final double Px1 = x1 * x1;
		final double Py1 = y1 * y1;
		// check that radii are large enough
		final double radiiCheck = Px1 / Prx + Py1 / Pry;
		if (radiiCheck > 1) {
			rx = Math.sqrt(radiiCheck) * rx;
			ry = Math.sqrt(radiiCheck) * ry;
			Prx = rx * rx;
			Pry = ry * ry;
		}

		//
		// Step 2 : Compute (cx1, cy1)
		//
		double sign = largeArcFlag == sweepFlag ? -1 : 1;
		double sq = (Prx * Pry - Prx * Py1 - Pry * Px1) / (Prx * Py1 + Pry * Px1);
		sq = sq < 0 ? 0 : sq;
		final double coef = sign * Math.sqrt(sq);
		final double cx1 = coef * (rx * y1 / ry);
		final double cy1 = coef * -(ry * x1 / rx);

		//
		// Step 3 : Compute (cx, cy) from (cx1, cy1)
		//
		final double sx2 = (x0 + x) / 2.0;
		final double sy2 = (y0 + y) / 2.0;
		final double cx = sx2 + (cosAngle * cx1 - sinAngle * cy1);
		final double cy = sy2 + (sinAngle * cx1 + cosAngle * cy1);

		//
		// Step 4 : Compute the angleStart (angle1) and the angleExtent (dangle)
		//
		final double ux = (x1 - cx1) / rx;
		final double uy = (y1 - cy1) / ry;
		final double vx = (-x1 - cx1) / rx;
		final double vy = (-y1 - cy1) / ry;
		double p, n;
		// Compute the angle start
		n = Math.sqrt(ux * ux + uy * uy);
		p = ux; // (1 * ux) + (0 * uy)
		sign = uy < 0 ? -1d : 1d;
		double angleStart = Math.toDegrees(sign * Math.acos(p / n));

		// Compute the angle extent
		n = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
		p = ux * vx + uy * vy;
		sign = ux * vy - uy * vx < 0 ? -1d : 1d;
		double angleExtent = Math.toDegrees(sign * Math.acos(p / n));
		if (!sweepFlag && angleExtent > 0) {
			angleExtent -= 360f;
		} else if (sweepFlag && angleExtent < 0) { angleExtent += 360f; }
		angleExtent %= 360f;
		angleStart %= 360f;

		//
		// We can now build the resulting Arc2D in double precision
		//
		final Arc2D.Double arc = new Arc2D.Double();
		arc.x = cx - rx;
		arc.y = cy - ry;
		arc.width = rx * 2.0;
		arc.height = ry * 2.0;
		arc.start = -angleStart;
		arc.extent = -angleExtent;

		return arc;
	}
}
