/*******************************************************************************************************
 *
 * ShowHideParametersViewHandler.java, in ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import org.eclipse.core.commands.*;
import msi.gama.runtime.GAMA;

/**
 * The Class ShowHideParametersViewHandler.
 */
public class ShowHideParametersViewHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		GAMA.getGui().showParameterView(null, GAMA.getExperiment());
		return null;
	}
}
