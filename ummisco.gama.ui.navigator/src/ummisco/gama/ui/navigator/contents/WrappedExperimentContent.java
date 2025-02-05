/*******************************************************************************************************
 *
 * WrappedExperimentContent.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import msi.gama.runtime.GAMA;
import msi.gaml.compilation.ast.ISyntacticElement;

/**
 * The Class WrappedExperimentContent.
 */
public class WrappedExperimentContent extends WrappedSyntacticContent {

	/**
	 * Instantiates a new wrapped experiment content.
	 *
	 * @param file the file
	 * @param e the e
	 */
	public WrappedExperimentContent(final WrappedGamaFile file, final ISyntacticElement e) {
		super(file, e, GAMA.getGui().getGamlLabelProvider().getText(e));
	}

	@Override
	public WrappedGamaFile getFile() {
		return (WrappedGamaFile) getParent();
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public boolean handleDoubleClick() {
		GAMA.getGui().runModel(getParent(), element.getName());
		return true;
	}
}