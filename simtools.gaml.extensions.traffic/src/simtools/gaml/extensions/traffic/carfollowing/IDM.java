package simtools.gaml.extensions.traffic.carfollowing;

import static simtools.gaml.extensions.traffic.DrivingSkill.getDeltaIDM;
import static simtools.gaml.extensions.traffic.DrivingSkill.getMaxAcceleration;
import static simtools.gaml.extensions.traffic.DrivingSkill.getMaxDeceleration;
import static simtools.gaml.extensions.traffic.DrivingSkill.getMaxSpeed;
import static simtools.gaml.extensions.traffic.DrivingSkill.getMinSafetyDistance;
import static simtools.gaml.extensions.traffic.DrivingSkill.getSpeed;
import static simtools.gaml.extensions.traffic.DrivingSkill.getTimeHeadway;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;

public class IDM {
	/**
	 * Computes the acceleration according to the Intelligent Driver Model
	 * (https://traffic-simulation.de/info/info_IDM.html), DrivingSkill.isViolatingOneway(driver)
	 *
	 * @param scope
	 * @param vehicle      the vehicle whose acceleration will be computed
	 * @param leadingDist  the bumper-to-bumper gap with its leading vehicle
	 * @param leadingSpeed the speed of the leading vehicle
	 * @return the resulting acceleration (deceleration if it is < 0)
	 */
	public static double computeAcceleration(final IScope scope,
			final IAgent vehicle,
			final double leadingDist,
			final double leadingSpeed) {
		// IDM params
		double T = getTimeHeadway(vehicle);
		double a = getMaxAcceleration(vehicle);
		double b = getMaxDeceleration(vehicle);
		double v0 = getMaxSpeed(vehicle);
		double s0 = getMinSafetyDistance(vehicle);
		double delta = getDeltaIDM(vehicle);

		double s = leadingDist;
		double v = getSpeed(vehicle);
		double dv = v - leadingSpeed;

		double sStar = s0 + Math.max(0, v * T + v * dv / 2 / Math.sqrt(a * b));
		double accel = a * (1 - Math.pow(v / v0, delta) - Math.pow(sStar / s, 2));

		return accel;
	}
}
