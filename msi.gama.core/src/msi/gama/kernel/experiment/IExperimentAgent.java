/*******************************************************************************************************
 *
 * IExperimentAgent.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.List;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationPopulation;

/**
 * The Interface IExperimentAgent.
 */
public interface IExperimentAgent extends ITopLevelAgent {

	/**
	 * Gets the species.
	 *
	 * @return the species
	 */
	@Override
	IExperimentPlan getSpecies();

	/**
	 * Gets the working path.
	 *
	 * @return the working path
	 */
	String getWorkingPath();

	/**
	 * Gets the working paths.
	 *
	 * @return the working paths
	 */
	List<String> getWorkingPaths();

	/**
	 * Gets the warnings as errors.
	 *
	 * @return the warnings as errors
	 */
	Boolean getWarningsAsErrors();

	/**
	 * Gets the minimum duration.
	 *
	 * @return the minimum duration
	 */
	Double getMinimumDuration();

	/**
	 * Sets the minimum duration.
	 *
	 * @param d
	 *            the new minimum duration
	 */
	void setMinimumDuration(Double d);

	/**
	 * Close simulations.
	 */
	void closeSimulations();

	/**
	 * Close simulation.
	 *
	 * @param simulationAgent
	 *            the simulation agent
	 */
	void closeSimulation(SimulationAgent simulationAgent);

	/**
	 * Gets the simulation population.
	 *
	 * @return the simulation population
	 */
	SimulationPopulation getSimulationPopulation();

	/**
	 * Checks if is memorize.
	 *
	 * @return true, if is memorize
	 */
	boolean isMemorize();

	/**
	 * Can step back.
	 *
	 * @return true, if successful
	 */
	boolean canStepBack();

	/**
	 * Inform status.
	 */
	void informStatus();

	/**
	 * Checks if is headless.
	 *
	 * @return true, if is headless
	 */
	boolean isHeadless();

	/**
	 * Checks if is synchronized.
	 *
	 * @return true, if is synchronized
	 */
	default boolean isSynchronized() { return getSpecies().isSynchronized(); }

}
