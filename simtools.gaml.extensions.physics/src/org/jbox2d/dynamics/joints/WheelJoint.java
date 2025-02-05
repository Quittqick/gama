/*******************************************************************************************************
 *
 * WheelJoint.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.joints;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.SolverData;
import org.jbox2d.pooling.IWorldPool;

//Linear constraint (point-to-line)
//d = pB - pA = xB + rB - xA - rA
//C = dot(ay, d)
//Cdot = dot(d, cross(wA, ay)) + dot(ay, vB + cross(wB, rB) - vA - cross(wA, rA))
//   = -dot(ay, vA) - dot(cross(d + rA, ay), wA) + dot(ay, vB) + dot(cross(rB, ay), vB)
//J = [-ay, -cross(d + rA, ay), ay, cross(rB, ay)]

//Spring linear constraint
//C = dot(ax, d)
//Cdot = = -dot(ax, vA) - dot(cross(d + rA, ax), wA) + dot(ax, vB) + dot(cross(rB, ax), vB)
//J = [-ax -cross(d+rA, ax) ax cross(rB, ax)]

//Motor rotational constraint
//Cdot = wB - wA
//J = [0 0 -1 0 0 1]

/**
 * A wheel joint. This joint provides two degrees of freedom: translation along an axis fixed in
 * bodyA and rotation in the plane. You can use a joint limit to restrict the range of motion and a
 * joint motor to drive the rotation or to model rotational friction. This joint is designed for
 * vehicle suspensions.
 * 
 * @author Daniel Murphy
 */
public class WheelJoint extends Joint {

  /** The m frequency hz. */
  private float m_frequencyHz;
  
  /** The m damping ratio. */
  private float m_dampingRatio;

  /** The m local anchor A. */
  // Solver shared
  private final Vec2 m_localAnchorA = new Vec2();
  
  /** The m local anchor B. */
  private final Vec2 m_localAnchorB = new Vec2();
  
  /** The m local X axis A. */
  private final Vec2 m_localXAxisA = new Vec2();
  
  /** The m local Y axis A. */
  private final Vec2 m_localYAxisA = new Vec2();

  /** The m impulse. */
  private float m_impulse;
  
  /** The m motor impulse. */
  private float m_motorImpulse;
  
  /** The m spring impulse. */
  private float m_springImpulse;

  /** The m max motor torque. */
  private float m_maxMotorTorque;
  
  /** The m motor speed. */
  private float m_motorSpeed;
  
  /** The m enable motor. */
  private boolean m_enableMotor;

  /** The m index A. */
  // Solver temp
  private int m_indexA;
  
  /** The m index B. */
  private int m_indexB;
  
  /** The m local center A. */
  private final Vec2 m_localCenterA = new Vec2();
  
  /** The m local center B. */
  private final Vec2 m_localCenterB = new Vec2();
  
  /** The m inv mass A. */
  private float m_invMassA;
  
  /** The m inv mass B. */
  private float m_invMassB;
  
  /** The m inv IA. */
  private float m_invIA;
  
  /** The m inv IB. */
  private float m_invIB;

  /** The m ax. */
  private final Vec2 m_ax = new Vec2();
  
  /** The m ay. */
  private final Vec2 m_ay = new Vec2();
  
  /** The m s bx. */
  private float m_sAx, m_sBx;
  
  /** The m s by. */
  private float m_sAy, m_sBy;

  /** The m mass. */
  private float m_mass;
  
  /** The m motor mass. */
  private float m_motorMass;
  
  /** The m spring mass. */
  private float m_springMass;

  /** The m bias. */
  private float m_bias;
  
  /** The m gamma. */
  private float m_gamma;

  /**
   * Instantiates a new wheel joint.
   *
   * @param argPool the arg pool
   * @param def the def
   */
  protected WheelJoint(IWorldPool argPool, WheelJointDef def) {
    super(argPool, def);
    m_localAnchorA.set(def.localAnchorA);
    m_localAnchorB.set(def.localAnchorB);
    m_localXAxisA.set(def.localAxisA);
    Vec2.crossToOutUnsafe(1.0f, m_localXAxisA, m_localYAxisA);


    m_motorMass = 0.0f;
    m_motorImpulse = 0.0f;

    m_maxMotorTorque = def.maxMotorTorque;
    m_motorSpeed = def.motorSpeed;
    m_enableMotor = def.enableMotor;

    m_frequencyHz = def.frequencyHz;
    m_dampingRatio = def.dampingRatio;
  }

  /**
   * Gets the local anchor A.
   *
   * @return the local anchor A
   */
  public Vec2 getLocalAnchorA() {
    return m_localAnchorA;
  }

  /**
   * Gets the local anchor B.
   *
   * @return the local anchor B
   */
  public Vec2 getLocalAnchorB() {
    return m_localAnchorB;
  }

  @Override
  public void getAnchorA(Vec2 argOut) {
    m_bodyA.getWorldPointToOut(m_localAnchorA, argOut);
  }

  @Override
  public void getAnchorB(Vec2 argOut) {
    m_bodyB.getWorldPointToOut(m_localAnchorB, argOut);
  }

  @Override
  public void getReactionForce(float inv_dt, Vec2 argOut) {
    final Vec2 temp = pool.popVec2();
    temp.set(m_ay).mulLocal(m_impulse);
    argOut.set(m_ax).mulLocal(m_springImpulse).addLocal(temp).mulLocal(inv_dt);
    pool.pushVec2(1);
  }

  @Override
  public float getReactionTorque(float inv_dt) {
    return inv_dt * m_motorImpulse;
  }

  /**
   * Gets the joint translation.
   *
   * @return the joint translation
   */
  public float getJointTranslation() {
    Body b1 = m_bodyA;
    Body b2 = m_bodyB;

    Vec2 p1 = pool.popVec2();
    Vec2 p2 = pool.popVec2();
    Vec2 axis = pool.popVec2();
    b1.getWorldPointToOut(m_localAnchorA, p1);
    b2.getWorldPointToOut(m_localAnchorA, p2);
    p2.subLocal(p1);
    b1.getWorldVectorToOut(m_localXAxisA, axis);

    float translation = Vec2.dot(p2, axis);
    pool.pushVec2(3);
    return translation;
  }

  /** For serialization */
  public Vec2 getLocalAxisA() {
    return m_localXAxisA;
  }

  /**
   * Gets the joint speed.
   *
   * @return the joint speed
   */
  public float getJointSpeed() {
    return m_bodyA.m_angularVelocity - m_bodyB.m_angularVelocity;
  }

  /**
   * Checks if is motor enabled.
   *
   * @return true, if is motor enabled
   */
  public boolean isMotorEnabled() {
    return m_enableMotor;
  }

  /**
   * Enable motor.
   *
   * @param flag the flag
   */
  public void enableMotor(boolean flag) {
    m_bodyA.setAwake(true);
    m_bodyB.setAwake(true);
    m_enableMotor = flag;
  }

  /**
   * Sets the motor speed.
   *
   * @param speed the new motor speed
   */
  public void setMotorSpeed(float speed) {
    m_bodyA.setAwake(true);
    m_bodyB.setAwake(true);
    m_motorSpeed = speed;
  }

  /**
   * Gets the motor speed.
   *
   * @return the motor speed
   */
  public float getMotorSpeed() {
    return m_motorSpeed;
  }

  /**
   * Gets the max motor torque.
   *
   * @return the max motor torque
   */
  public float getMaxMotorTorque() {
    return m_maxMotorTorque;
  }

  /**
   * Sets the max motor torque.
   *
   * @param torque the new max motor torque
   */
  public void setMaxMotorTorque(float torque) {
    m_bodyA.setAwake(true);
    m_bodyB.setAwake(true);
    m_maxMotorTorque = torque;
  }

  /**
   * Gets the motor torque.
   *
   * @param inv_dt the inv dt
   * @return the motor torque
   */
  public float getMotorTorque(float inv_dt) {
    return m_motorImpulse * inv_dt;
  }

  /**
   * Sets the spring frequency hz.
   *
   * @param hz the new spring frequency hz
   */
  public void setSpringFrequencyHz(float hz) {
    m_frequencyHz = hz;
  }

  /**
   * Gets the spring frequency hz.
   *
   * @return the spring frequency hz
   */
  public float getSpringFrequencyHz() {
    return m_frequencyHz;
  }

  /**
   * Sets the spring damping ratio.
   *
   * @param ratio the new spring damping ratio
   */
  public void setSpringDampingRatio(float ratio) {
    m_dampingRatio = ratio;
  }

  /**
   * Gets the spring damping ratio.
   *
   * @return the spring damping ratio
   */
  public float getSpringDampingRatio() {
    return m_dampingRatio;
  }

  /** The r A. */
  // pooling
  private final Vec2 rA = new Vec2();
  
  /** The r B. */
  private final Vec2 rB = new Vec2();
  
  /** The d. */
  private final Vec2 d = new Vec2();

  @Override
  public void initVelocityConstraints(SolverData data) {
    m_indexA = m_bodyA.m_islandIndex;
    m_indexB = m_bodyB.m_islandIndex;
    m_localCenterA.set(m_bodyA.m_sweep.localCenter);
    m_localCenterB.set(m_bodyB.m_sweep.localCenter);
    m_invMassA = m_bodyA.m_invMass;
    m_invMassB = m_bodyB.m_invMass;
    m_invIA = m_bodyA.m_invI;
    m_invIB = m_bodyB.m_invI;

    float mA = m_invMassA, mB = m_invMassB;
    float iA = m_invIA, iB = m_invIB;

    Vec2 cA = data.positions[m_indexA].c;
    float aA = data.positions[m_indexA].a;
    Vec2 vA = data.velocities[m_indexA].v;
    float wA = data.velocities[m_indexA].w;

    Vec2 cB = data.positions[m_indexB].c;
    float aB = data.positions[m_indexB].a;
    Vec2 vB = data.velocities[m_indexB].v;
    float wB = data.velocities[m_indexB].w;

    final Rot qA = pool.popRot();
    final Rot qB = pool.popRot();
    final Vec2 temp = pool.popVec2();

    qA.set(aA);
    qB.set(aB);

    // Compute the effective masses.
    Rot.mulToOutUnsafe(qA, temp.set(m_localAnchorA).subLocal(m_localCenterA), rA);
    Rot.mulToOutUnsafe(qB, temp.set(m_localAnchorB).subLocal(m_localCenterB), rB);
    d.set(cB).addLocal(rB).subLocal(cA).subLocal(rA);

    // Point to line constraint
    {
      Rot.mulToOut(qA, m_localYAxisA, m_ay);
      m_sAy = Vec2.cross(temp.set(d).addLocal(rA), m_ay);
      m_sBy = Vec2.cross(rB, m_ay);

      m_mass = mA + mB + iA * m_sAy * m_sAy + iB * m_sBy * m_sBy;

      if (m_mass > 0.0f) {
        m_mass = 1.0f / m_mass;
      }
    }

    // Spring constraint
    m_springMass = 0.0f;
    m_bias = 0.0f;
    m_gamma = 0.0f;
    if (m_frequencyHz > 0.0f) {
      Rot.mulToOut(qA, m_localXAxisA, m_ax);
      m_sAx = Vec2.cross(temp.set(d).addLocal(rA), m_ax);
      m_sBx = Vec2.cross(rB, m_ax);

      float invMass = mA + mB + iA * m_sAx * m_sAx + iB * m_sBx * m_sBx;

      if (invMass > 0.0f) {
        m_springMass = 1.0f / invMass;

        float C = Vec2.dot(d, m_ax);

        // Frequency
        float omega = 2.0f * MathUtils.PI * m_frequencyHz;

        // Damping coefficient
        float d = 2.0f * m_springMass * m_dampingRatio * omega;

        // Spring stiffness
        float k = m_springMass * omega * omega;

        // magic formulas
        float h = data.step.dt;
        m_gamma = h * (d + h * k);
        if (m_gamma > 0.0f) {
          m_gamma = 1.0f / m_gamma;
        }

        m_bias = C * h * k * m_gamma;

        m_springMass = invMass + m_gamma;
        if (m_springMass > 0.0f) {
          m_springMass = 1.0f / m_springMass;
        }
      }
    } else {
      m_springImpulse = 0.0f;
    }

    // Rotational motor
    if (m_enableMotor) {
      m_motorMass = iA + iB;
      if (m_motorMass > 0.0f) {
        m_motorMass = 1.0f / m_motorMass;
      }
    } else {
      m_motorMass = 0.0f;
      m_motorImpulse = 0.0f;
    }

    if (data.step.warmStarting) {
      final Vec2 P = pool.popVec2();
      // Account for variable time step.
      m_impulse *= data.step.dtRatio;
      m_springImpulse *= data.step.dtRatio;
      m_motorImpulse *= data.step.dtRatio;

      P.x = m_impulse * m_ay.x + m_springImpulse * m_ax.x;
      P.y = m_impulse * m_ay.y + m_springImpulse * m_ax.y;
      float LA = m_impulse * m_sAy + m_springImpulse * m_sAx + m_motorImpulse;
      float LB = m_impulse * m_sBy + m_springImpulse * m_sBx + m_motorImpulse;

      vA.x -= m_invMassA * P.x;
      vA.y -= m_invMassA * P.y;
      wA -= m_invIA * LA;

      vB.x += m_invMassB * P.x;
      vB.y += m_invMassB * P.y;
      wB += m_invIB * LB;
      pool.pushVec2(1);
    } else {
      m_impulse = 0.0f;
      m_springImpulse = 0.0f;
      m_motorImpulse = 0.0f;
    }
    pool.pushRot(2);
    pool.pushVec2(1);

    // data.velocities[m_indexA].v = vA;
    data.velocities[m_indexA].w = wA;
    // data.velocities[m_indexB].v = vB;
    data.velocities[m_indexB].w = wB;
  }

  @Override
  public void solveVelocityConstraints(SolverData data) {
    float mA = m_invMassA, mB = m_invMassB;
    float iA = m_invIA, iB = m_invIB;

    Vec2 vA = data.velocities[m_indexA].v;
    float wA = data.velocities[m_indexA].w;
    Vec2 vB = data.velocities[m_indexB].v;
    float wB = data.velocities[m_indexB].w;

    final Vec2 temp = pool.popVec2();
    final Vec2 P = pool.popVec2();

    // Solve spring constraint
    {
      float Cdot = Vec2.dot(m_ax, temp.set(vB).subLocal(vA)) + m_sBx * wB - m_sAx * wA;
      float impulse = -m_springMass * (Cdot + m_bias + m_gamma * m_springImpulse);
      m_springImpulse += impulse;

      P.x = impulse * m_ax.x;
      P.y = impulse * m_ax.y;
      float LA = impulse * m_sAx;
      float LB = impulse * m_sBx;

      vA.x -= mA * P.x;
      vA.y -= mA * P.y;
      wA -= iA * LA;

      vB.x += mB * P.x;
      vB.y += mB * P.y;
      wB += iB * LB;
    }

    // Solve rotational motor constraint
    {
      float Cdot = wB - wA - m_motorSpeed;
      float impulse = -m_motorMass * Cdot;

      float oldImpulse = m_motorImpulse;
      float maxImpulse = data.step.dt * m_maxMotorTorque;
      m_motorImpulse = MathUtils.clamp(m_motorImpulse + impulse, -maxImpulse, maxImpulse);
      impulse = m_motorImpulse - oldImpulse;

      wA -= iA * impulse;
      wB += iB * impulse;
    }

    // Solve point to line constraint
    {
      float Cdot = Vec2.dot(m_ay, temp.set(vB).subLocal(vA)) + m_sBy * wB - m_sAy * wA;
      float impulse = -m_mass * Cdot;
      m_impulse += impulse;

      P.x = impulse * m_ay.x;
      P.y = impulse * m_ay.y;
      float LA = impulse * m_sAy;
      float LB = impulse * m_sBy;

      vA.x -= mA * P.x;
      vA.y -= mA * P.y;
      wA -= iA * LA;

      vB.x += mB * P.x;
      vB.y += mB * P.y;
      wB += iB * LB;
    }
    pool.pushVec2(2);

    // data.velocities[m_indexA].v = vA;
    data.velocities[m_indexA].w = wA;
    // data.velocities[m_indexB].v = vB;
    data.velocities[m_indexB].w = wB;
  }

  @Override
  public boolean solvePositionConstraints(SolverData data) {
    Vec2 cA = data.positions[m_indexA].c;
    float aA = data.positions[m_indexA].a;
    Vec2 cB = data.positions[m_indexB].c;
    float aB = data.positions[m_indexB].a;

    final Rot qA = pool.popRot();
    final Rot qB = pool.popRot();
    final Vec2 temp = pool.popVec2();

    qA.set(aA);
    qB.set(aB);

    Rot.mulToOut(qA, temp.set(m_localAnchorA).subLocal(m_localCenterA), rA);
    Rot.mulToOut(qB, temp.set(m_localAnchorB).subLocal(m_localCenterB), rB);
    d.set(cB).subLocal(cA).addLocal(rB).subLocal(rA);

    Vec2 ay = pool.popVec2();
    Rot.mulToOut(qA, m_localYAxisA, ay);

    float sAy = Vec2.cross(temp.set(d).addLocal(rA), ay);
    float sBy = Vec2.cross(rB, ay);

    float C = Vec2.dot(d, ay);

    float k = m_invMassA + m_invMassB + m_invIA * m_sAy * m_sAy + m_invIB * m_sBy * m_sBy;

    float impulse;
    if (k != 0.0f) {
      impulse = -C / k;
    } else {
      impulse = 0.0f;
    }

    final Vec2 P = pool.popVec2();
    P.x = impulse * ay.x;
    P.y = impulse * ay.y;
    float LA = impulse * sAy;
    float LB = impulse * sBy;

    cA.x -= m_invMassA * P.x;
    cA.y -= m_invMassA * P.y;
    aA -= m_invIA * LA;
    cB.x += m_invMassB * P.x;
    cB.y += m_invMassB * P.y;
    aB += m_invIB * LB;

    pool.pushVec2(3);
    pool.pushRot(2);
    // data.positions[m_indexA].c = cA;
    data.positions[m_indexA].a = aA;
    // data.positions[m_indexB].c = cB;
    data.positions[m_indexB].a = aB;

    return MathUtils.abs(C) <= Settings.linearSlop;
  }
}
