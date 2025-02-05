/*******************************************************************************************************
 *
 * EditorsList.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.experiment.parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.ItemList;
import msi.gama.kernel.experiment.IExperimentDisplayable;
import msi.gama.metamodel.agent.IAgent;
import ummisco.gama.ui.interfaces.IParameterEditor;
import ummisco.gama.ui.parameters.AbstractEditor;

/**
 * The Class EditorsList.
 *
 * @param <T>
 *            the generic type
 */
public abstract class EditorsList<T> implements ItemList<T> {

	/** The categories. */
	/* Map to associate a category to each parameter */
	protected final Map<T, Map<String, IParameterEditor<?>>> categories = new LinkedHashMap<>();

	@Override
	public List<T> getItems() { return new ArrayList<>(categories.keySet()); }

	@Override
	public abstract String getItemDisplayName(final T obj, final String previousName);

	/**
	 * Adds the.
	 *
	 * @param params
	 *            the params
	 * @param agent
	 *            the agent
	 */
	public abstract void add(final Collection<? extends IExperimentDisplayable> params, final IAgent agent);

	/**
	 * Gets the categories.
	 *
	 * @return the categories
	 */
	public Map<T, Map<String, IParameterEditor<?>>> getCategories() { return categories; }

	/**
	 * Revert to default value.
	 */
	public void revertToDefaultValue() {
		for (final Map<String, IParameterEditor<?>> editors : categories.values()) {
			for (final IParameterEditor<?> ed : editors.values()) { ed.revertToDefaultValue(); }
		}
	}

	@Override
	public void removeItem(final T name) {
		categories.remove(name);
	}

	@Override
	public void pauseItem(final T name) {}

	@Override
	public abstract void updateItemValues(boolean synchronously);

	@Override
	public void resumeItem(final T obj) {}

	@Override
	public void focusItem(final T obj) {}

	@Override
	public void makeItemVisible(final T obj, final boolean b) {}

	@Override
	public void makeItemSelectable(final T obj, final boolean b) {}

	/**
	 * Checks if is enabled.
	 *
	 * @param gpParam
	 *            the gp param
	 * @return true, if is enabled
	 */
	public boolean isEnabled(final AbstractEditor<?> gpParam) {
		return true;
	}

}
