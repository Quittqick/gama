/*******************************************************************************************************
 *
 * PrismaticJointDef.java, in simtools.gaml.extensions.physics, is part of the source code of the
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
 * Prismatic joint definition. This requires defining a line of motion using an axis and an anchor
 * point. The definition uses local anchor points and a local axis so that the initial configuration
 * can violate the constraint slightly. The joint translation is zero when the local anchor points
 * coincide in world space. Using local anchors and a local axis helps when saving and loading a
 * game.
 * 
 * @warning at least one body should by dynamic with a non-fixed rotation.
 * @author Daniel
 * 
 */
public class PrismaticJointDef extends JointDef {


  /**
   * The local anchor point relative to body1's origin.
   */
  public final Vec2 localAnchorA;

  /**
   * The local anchor point relative to body2's origin.
   */
  public final Vec2 localAnchorB;

  /**
   * The local translation axis in body1.
   */
  public final Vec2 localAxisA;

  /**
   * The constrained angle between the bodies: body2_angle - body1_angle.
   */
  public float referenceAngle;

  /**
   * Enable/disable the joint limit.
   */
  public boolean enableLimit;

  /**
   * The lower translation limit, usually in meters.
   */
  public float lowerTranslation;

  /**
   * The upper translation limit, usually in meters.
   */
  public float upperTranslation;

  /**
   * Enable/disable the joint motor.
   */
  public boolean enableMotor;

  /**
   * The maximum motor torque, usually in N-m.
   */
  public float maxMotorForce;

  /**
   * The desired motor speed in radians per second.
   */
  public float motorSpeed;

  /**
   * Instantiates a new prismatic joint def.
   */
  public PrismaticJointDef() {
    super(JointType.PRISMATIC);
    localAnchorA = new Vec2();
    localAnchorB = new Vec2();
    localAxisA = new Vec2(1.0f, 0.0f);
    referenceAngle = 0.0f;
    enableLimit = false;
    lowerTranslation = 0.0f;
    upperTranslation = 0.0f;
    enableMotor = false;
    maxMotorForce = 0.0f;
    motorSpeed = 0.0f;
  }


  /**
   * Initialize the bodies, anchors, axis, and reference angle using the world anchor and world
   * axis.
   */
  public void initialize(Body b1, Body b2, Vec2 anchor, Vec2 axis) {
    bodyA = b1;
    bodyB = b2;
    bodyA.getLocalPointToOut(anchor, localAnchorA);
    bodyB.getLocalPointToOut(anchor, localAnchorB);
    bodyA.getLocalVectorToOut(axis, localAxisA);
    referenceAngle = bodyB.getAngle() - bodyA.getAngle();
  }
}
