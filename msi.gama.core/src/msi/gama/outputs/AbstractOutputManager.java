/*******************************************************************************************************
 *
 * AbstractOutputManager.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Class AbstractOutputManager.
 *
 * @author drogoul
 * @since 9 juin 2013
 *
 */
public abstract class AbstractOutputManager extends Symbol implements IOutputManager {

	static {
		DEBUG.ON();
	}

	/** The autosave. */
	final IExpression autosave;

	/** The in init phase. */
	volatile boolean inInitPhase;

	/**
	 * Properties
	 */

	/** The layout. */
	LayoutStatement layout;

	/** The outputs. */
	protected final IMap<String, IOutput> outputs = GamaMapFactory.create();

	// protected final IList<MonitorOutput> monitors = GamaListFactory.create();

	/** The virtual outputs. */
	protected final IMap<String, IOutput> virtualOutputs = GamaMapFactory.create();

	/** The display index. */
	protected int displayIndex;

	/** The sync. */
	protected boolean sync = GamaPreferences.Runtime.CORE_SYNC.getValue();

	/**
	 * Checks if is sync.
	 *
	 * @return the sync
	 */
	@Override
	public boolean isSync() { return sync; }

	/**
	 * Instantiates a new abstract output manager.
	 *
	 * @param desc
	 *            the desc
	 */
	public AbstractOutputManager(final IDescription desc) {
		super(desc);
		autosave = desc.getFacetExpr(IKeyword.AUTOSAVE);
		sync = "true".equals(desc.getLitteral("synchronized"))
				|| desc.hasFacet(IKeyword.AUTOSAVE) && !"false".equals(desc.getLitteral(IKeyword.AUTOSAVE));
	}

	@Override
	public Map<String, ? extends IOutput> getOutputs() { return outputs; }

	@Override
	public Iterator<IOutput> iterator() {
		return Iterators.unmodifiableIterator(outputs.values().iterator());
	}

	@Override
	public IOutput getOutputWithId(final String id) {
		return outputs.get(id);
	}

	@Override
	public void putAll(final Map<String, IOutput> mm) {
		outputs.putAll(mm);
	}

	@Override
	public IOutput getOutputWithOriginalName(final String s) {
		return Iterables.find(this, each -> each.getOriginalName().equals(s), null);
	}

	@Override
	public void add(final IOutput output) {
		if (output instanceof IDisplayOutput && ((IDisplayOutput) output).isVirtual()) {
			virtualOutputs.put(output.getId(), output);
		}
		// else if (output instanceof MonitorOutput monitor
		// && GamaPreferences.Interface.CORE_MONITOR_PARAMETERS.getValue()) {
		// monitors.add(monitor);
		// }
		else {
			outputs.put(output.getId(), output);
		}
	}

	@Override
	public synchronized void dispose() {
		super.dispose();
		try {
			// AD: explicit addition of an ArrayList to prevent dispose errors
			// (when outputs remove themselves from the list)
			GAMA.desynchronizeFrontmostExperiment();
			for (final IOutput output : new ArrayList<>(outputs.values())) { output.dispose(); }
			// for (final IOutput output : new ArrayList<>(monitors)) { output.dispose(); }
			clear();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	// hqnghi
	// for instant, multi-simulation cannot have their owns outputs display at
	// same time.
	public void clear() {
		outputs.clear();
	}

	@Override
	public void remove(final IOutput o) {
		if (!(o instanceof AbstractOutput)) return;
		if (((AbstractOutput) o).isUserCreated()) {
			o.dispose();
			outputs.values().remove(o);
		} else {
			o.setPaused(true);
		}
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		for (final ISymbol s : commands) {
			if (s instanceof LayoutStatement) {
				layout = (LayoutStatement) s;
			} else if (s instanceof IOutput o) {
				if (o instanceof IDisplayOutput && ((IDisplayOutput) o).isAutoSave()) { sync = true; }
				add(o);
				o.setUserCreated(false);
				if (o instanceof LayeredDisplayOutput ldo) { ldo.setIndex(displayIndex++); }
			}
		}
	}

	@Override
	public void forceUpdateOutputs() {
		for (final IDisplayOutput o : getDisplayOutputs()) { o.update(); }
		// if (GamaPreferences.Interface.CORE_MONITOR_PARAMETERS.getValue() && !monitors.isEmpty()) {
		// GAMA.getGui().updateParameterView(monitors.get(0).getScope());
		// }
	}

	@Override
	public void pause() {
		for (final IDisplayOutput o : getDisplayOutputs()) { o.setPaused(true); }
	}

	@Override
	public void resume() {
		for (final IDisplayOutput o : getDisplayOutputs()) { o.setPaused(false); }
	}

	@Override
	public void close() {
		for (final IDisplayOutput o : getDisplayOutputs()) { o.close(); }
	}

	@Override
	public Iterable<IDisplayOutput> getDisplayOutputs() {
		return Iterables.filter(outputs.values(), IDisplayOutput.class);
	}

	@Override
	public Collection<MonitorOutput> getMonitors() {
		return Lists.newArrayList(Iterables.filter(outputs.values(), MonitorOutput.class));

		// return monitors;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean init(final IScope scope) {
		name = scope.getRoot().getName();
		// if (this.hasFacet("synchronized")) {
		// boolean sync = this.getFacetValue(scope, "synchronized", false);
		// if (sync) {
		// scope.getExperiment().getSpecies().synchronizeAllOutputs();
		// } else {
		// scope.getExperiment().getSpecies().desynchronizeAllOutputs();
		// }
		// }
		// boolean atLeastOneOutputAutosaving = false;<>
		for (final IOutput output : ImmutableList.copyOf(this)) {
			if (!open(scope, output)) return false;
			// if (output instanceof IDisplayOutput && ((IDisplayOutput) output).isAutoSave()) {
			// atLeastOneOutputAutosaving = true;
			// }
		}
		// atLeastOneOutputAutosaving |=
		evaluateAutoSave(scope);
		// if (atLeastOneOutputAutosaving) { GAMA.synchronizeFrontmostExperiment(); }

		return true;
	}

	/**
	 * Evaluate auto save.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	private boolean evaluateAutoSave(final IScope scope) throws GamaRuntimeException {
		boolean isAutosaving = false;
		if (autosave != null) {
			String path = null;
			if (autosave.getGamlType().equals(Types.STRING)) {
				path = Cast.asString(scope, autosave.value(scope));
				isAutosaving = path != null && !path.isBlank();
			} else {
				isAutosaving = Cast.asBool(scope, autosave.value(scope));
			}
			if (isAutosaving) { SnapshotMaker.getInstance().doSnapshot(scope, path); }
		}
		return isAutosaving;
	}

	/**
	 * Sets the layout.
	 *
	 * @param layout
	 *            the new layout
	 */
	public void setLayout(final LayoutStatement layout) { this.layout = layout; }

	/**
	 * Gets the layout.
	 *
	 * @return the layout
	 */
	public LayoutStatement getLayout() { return layout; }

	@Override
	public boolean open(final IScope scope, final IOutput output) {

		if (scope.init(output).passed()) {
			output.setPaused(false);
			if (initialStep(scope, output)) {
				try {
					output.open();
					// DEBUG.OUT("Updating the output");
					output.update();
				} catch (final RuntimeException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;

	}

	/**
	 * Initial step.
	 *
	 * @param scope
	 *            the scope
	 * @param output
	 *            the output
	 * @return true, if successful
	 */
	protected boolean initialStep(final IScope scope, final IOutput output) {
		inInitPhase = true;
		boolean result = false;
		try {
			result = scope.step(output).passed();
		} finally {
			inInitPhase = false;
		}
		return result;
	}

	@Override
	public boolean step(final IScope scope) {
		getDisplayOutputs().forEach(each -> { each.setRendered(false); });
		outputs.forEach((name, each) -> {
			if (each instanceof LayeredDisplayOutput ldo) { ldo.linkScopeWithGraphics(); }
			if (each.isRefreshable() && each.getScope().step(each).passed()) { each.update(); }
		});
		if (scope.getExperiment().isSynchronized() && !inInitPhase) {
			while (!allOutputsRendered()) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}
			}
		}
		if (autosave != null) {
			boolean isAutosaving = false;
			String autosavingPath = null;
			if (autosave.getGamlType().equals(Types.STRING)) {
				isAutosaving = true;
				autosavingPath = Cast.asString(scope, autosave.value(scope));
			} else {
				isAutosaving = Cast.asBool(scope, autosave.value(scope));
			}
			if (isAutosaving) { SnapshotMaker.getInstance().doSnapshot(scope, autosavingPath); }
		}

		return true;
	}

	/**
	 * All outputs rendered.
	 *
	 * @return true, if successful
	 */
	protected boolean allOutputsRendered() {
		for (IDisplayOutput each : this.getDisplayOutputs()) { if (!each.isRendered()) return false; }
		return true;
	}

}