/*******************************************************************************************************
 *
 * If.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.gaml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>If</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.If#getLeft <em>Left</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.If#getOp <em>Op</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.If#getRight <em>Right</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.If#getIfFalse <em>If False</em>}</li>
 * </ul>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getIf()
 * @model
 * @generated
 */
public interface If extends Expression
{
  /**
   * Returns the value of the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Left</em>' containment reference.
   * @see #setLeft(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getIf_Left()
   * @model containment="true"
   * @generated
   */
  Expression getLeft();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.If#getLeft <em>Left</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Left</em>' containment reference.
   * @see #getLeft()
   * @generated
   */
  void setLeft(Expression value);

  /**
   * Returns the value of the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Op</em>' attribute.
   * @see #setOp(String)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getIf_Op()
   * @model
   * @generated
   */
  String getOp();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.If#getOp <em>Op</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Op</em>' attribute.
   * @see #getOp()
   * @generated
   */
  void setOp(String value);

  /**
   * Returns the value of the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Right</em>' containment reference.
   * @see #setRight(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getIf_Right()
   * @model containment="true"
   * @generated
   */
  Expression getRight();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.If#getRight <em>Right</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Right</em>' containment reference.
   * @see #getRight()
   * @generated
   */
  void setRight(Expression value);

  /**
   * Returns the value of the '<em><b>If False</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>If False</em>' containment reference.
   * @see #setIfFalse(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getIf_IfFalse()
   * @model containment="true"
   * @generated
   */
  Expression getIfFalse();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.If#getIfFalse <em>If False</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>If False</em>' containment reference.
   * @see #getIfFalse()
   * @generated
   */
  void setIfFalse(Expression value);

} // If
