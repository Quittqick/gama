/*******************************************************************************************************
 *
 * FrictionJointDef.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
/**
 * Created at 7:23:39 AM Jan 20, 2011
 */
package org.jbox2d.dynamics.joints;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * Friction joint definition.
 * 
 * @author Daniel Murphy
 */
public class FrictionJointDef extends JointDef {


  /**
   * The local anchor point relative to bodyA's origin.
   */
  public final Vec2 localAnchorA;

  /**
   * The local anchor point relative to bodyB's origin.
   */
  public final Vec2 localAnchorB;

  /**
   * The maximum friction force in N.
   */
  public float maxForce;

  /**
   * The maximum friction torque in N-m.
   */
  public float maxTorque;

  /**
   * Instantiates a new friction joint def.
   */
  public FrictionJointDef() {
    super(JointType.FRICTION);
    localAnchorA = new Vec2();
    localAnchorB = new Vec2();
    maxForce = 0f;
    maxTorque = 0f;
  }

  /**
   * Initialize the bodies, anchors, axis, and reference angle using the world anchor and world
   * axis.
   */
  public void initialize(Body bA, Body bB, Vec2 anchor) {
    bodyA = bA;
    bodyB = bB;
    bA.getLocalPointToOut(anchor, localAnchorA);
    bB.getLocalPointToOut(anchor, localAnchorB);
  }
}
