/*******************************************************************************************************
 *
 * MotorJointDef.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.joints;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * Motor joint definition.
 * 
 * @author dmurph
 */
public class MotorJointDef extends JointDef {
  /**
   * Position of bodyB minus the position of bodyA, in bodyA's frame, in meters.
   */
  public final Vec2 linearOffset = new Vec2();

  /**
   * The bodyB angle minus bodyA angle in radians.
   */
  public float angularOffset;

  /**
   * The maximum motor force in N.
   */
  public float maxForce;

  /**
   * The maximum motor torque in N-m.
   */
  public float maxTorque;

  /**
   * Position correction factor in the range [0,1].
   */
  public float correctionFactor;

  /**
   * Instantiates a new motor joint def.
   */
  public MotorJointDef() {
    super(JointType.MOTOR);
    angularOffset = 0;
    maxForce = 1;
    maxTorque = 1;
    correctionFactor = 0.3f;
  }

  /**
   * Initialize.
   *
   * @param bA the b A
   * @param bB the b B
   */
  public void initialize(Body bA, Body bB) {
    bodyA = bA;
    bodyB = bB;
    Vec2 xB = bodyB.getPosition();
    bodyA.getLocalPointToOut(xB, linearOffset);

    float angleA = bodyA.getAngle();
    float angleB = bodyB.getAngle();
    angularOffset = angleB - angleA;
  }
}
