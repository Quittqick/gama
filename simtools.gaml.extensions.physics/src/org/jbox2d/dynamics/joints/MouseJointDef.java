/*******************************************************************************************************
 *
 * MouseJointDef.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.joints;

import org.jbox2d.common.Vec2;

/**
 * Mouse joint definition. This requires a world target point, tuning parameters, and the time step.
 * 
 * @author Daniel
 */
public class MouseJointDef extends JointDef {
  /**
   * The initial world target point. This is assumed to coincide with the body anchor initially.
   */
  public final Vec2 target = new Vec2();

  /**
   * The maximum constraint force that can be exerted to move the candidate body. Usually you will
   * express as some multiple of the weight (multiplier * mass * gravity).
   */
  public float maxForce;

  /**
   * The response speed.
   */
  public float frequencyHz;

  /**
   * The damping ratio. 0 = no damping, 1 = critical damping.
   */
  public float dampingRatio;

  /**
   * Instantiates a new mouse joint def.
   */
  public MouseJointDef() {
    super(JointType.MOUSE);
    target.set(0, 0);
    maxForce = 0;
    frequencyHz = 5;
    dampingRatio = .7f;
  }
}
