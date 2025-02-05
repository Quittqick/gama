/*******************************************************************************************************
 *
 * GamaMaterialType.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMaterial;

/**
 * Written by mazarsju
 *
 * @todo Description
 *
 */
@SuppressWarnings ("unchecked")
@type (
		name = IKeyword.MATERIAL,
		id = IType.MATERIAL,
		wraps = { GamaMaterial.class },
		kind = ISymbolKind.Variable.REGULAR,
		concept = { /* TODO */ })
public class GamaMaterialType extends GamaType<GamaMaterial> {

	/** The default material. */
	public static GamaMaterial DEFAULT_MATERIAL = new GamaMaterial(0, 0);

	/**
	 * Material.
	 *
	 * @param damper the damper
	 * @param reflectivity the reflectivity
	 * @return the gama material
	 */
	@operator (
			value = "material",
			can_be_const = true,
			category = { /* TODO */ },
			concept = { /* TODO */ })
	@doc (
			value = "Returns a material defined by a given damper and reflectivity",
			masterDoc = true)
	public static GamaMaterial material(final double damper, final double reflectivity) {
		return new GamaMaterial(damper, reflectivity);
	}

	@Override
	public GamaMaterial cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, copy);
	}

	/**
	 * Static cast.
	 *
	 * @param scope the scope
	 * @param obj the obj
	 * @param copy the copy
	 * @return the gama material
	 */
	public static GamaMaterial staticCast(final IScope scope, final Object obj, final boolean copy) {
		if (obj instanceof GamaMaterial) return (GamaMaterial) obj;
		return null;
	}

	@Override
	public GamaMaterial getDefault() {
		return null;
	}

	@Override
	public IType<?> getContentType() {
		return Types.get(FLOAT);
	}

	@Override
	public IType<?> getKeyType() {
		return Types.get(INT);
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

}
