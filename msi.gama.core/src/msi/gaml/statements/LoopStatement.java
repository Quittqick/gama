/*******************************************************************************************************
 *
 * LoopStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.FlowStatus;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.serializer;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.descriptions.SymbolSerializer;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.IStatement.Breakable;
import msi.gaml.statements.LoopStatement.LoopSerializer;
import msi.gaml.statements.LoopStatement.LoopValidator;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

// A group of commands that can be executed repeatedly.

/**
 * The Class LoopStatement.
 */

/**
 * The Class LoopStatement.
 */

/**
 * The Class LoopStatement.
 */

/**
 * The Class LoopStatement.
 */

/**
 * The Class LoopStatement.
 */

/**
 * The Class LoopStatement.
 */

/**
 * The Class LoopStatement.
 */
@symbol (
		name = IKeyword.LOOP,
		kind = ISymbolKind.SEQUENCE_STATEMENT,
		with_sequence = true,
		breakable = true,
		continuable = true,
		concept = { IConcept.LOOP })
@facets (
		value = { @facet (
				name = IKeyword.FROM,
				type = IType.INT,
				optional = true,
				doc = @doc ("an int expression")),
				@facet (
						name = IKeyword.TO,
						type = IType.INT,
						optional = true,
						doc = @doc ("an int expression")),
				@facet (
						name = IKeyword.STEP,
						type = IType.INT,
						optional = true,
						doc = @doc ("an int expression")),
				@facet (
						name = IKeyword.NAME,
						type = IType.NEW_TEMP_ID,
						optional = true,
						doc = @doc ("a temporary variable name")),
				@facet (
						name = IKeyword.OVER,
						type = { IType.CONTAINER, IType.POINT },
						optional = true,
						doc = @doc ("a list, point, matrix or map expression")),
				@facet (
						name = IKeyword.WHILE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("a boolean expression")),
				@facet (
						name = IKeyword.TIMES,
						type = IType.INT,
						optional = true,
						doc = @doc ("an int expression")) },
		omissible = IKeyword.NAME)
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@doc (
		value = "Allows the agent to perform the same set of statements either a fixed number of times, or while a condition is true, or by progressing in a collection of elements or along an interval of integers. Be aware that there are no prevention of infinite loops. As a consequence, open loops should be used with caution, as one agent may block the execution of the whole model.",
		usages = { @usage (
				value = "The basic syntax for repeating a fixed number of times a set of statements is:",
				examples = { @example (
						value = "loop times: an_int_expression {",
						isExecutable = false),
						@example (
								value = "     // [statements]",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false),
						@example (
								value = "int sumTimes <- 1;",
								isTestOnly = true),
						@example (
								value = "loop times: 3 {sumTimes <- sumTimes + sumTimes;}",
								isTestOnly = true),
						@example (
								var = "sumTimes",
								equals = "8",
								isTestOnly = true) }),
				@usage (
						value = "The basic syntax for repeating a set of statements while a condition holds is:",
						examples = { @example (
								value = "loop while: a_bool_expression {",
								isExecutable = false),
								@example (
										value = "     // [statements]",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false),
								@example (
										value = "int sumWhile <- 1;",
										isTestOnly = true),
								@example (
										value = "loop while: (sumWhile < 5) {sumWhile <- sumWhile + sumWhile;}",
										isTestOnly = true),
								@example (
										var = "sumWhile",
										equals = "8",
										isTestOnly = true) }),
				@usage (
						value = "The basic syntax for repeating a set of statements by progressing over a container of a point is:",
						examples = { @example (
								value = "loop a_temp_var over: a_collection_expression {",
								isExecutable = false),
								@example (
										value = "     // [statements]",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }),
				@usage (
						value = "The basic syntax for repeating a set of statements while an index iterates over a range of values with a fixed step of 1 is:",
						examples = { @example (
								value = "loop a_temp_var from: int_expression_1 to: int_expression_2 {",
								isExecutable = false),
								@example (
										value = "     // [statements]",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }),
				@usage (
						value = "The incrementation step of the index can also be chosen:",
						examples = { @example (
								value = "loop a_temp_var from: int_expression_1 to: int_expression_2 step: int_expression3 {",
								isExecutable = false),
								@example (
										value = "     // [statements]",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false),
								@example (
										value = "int sumFor <- 0;",
										isTestOnly = true),
								@example (
										value = "loop i from: 10 to: 30 step: 10 {sumFor <- sumFor + i;}",
										isTestOnly = true),
								@example (
										var = "sumFor",
										equals = "60",
										isTestOnly = true) }),
				@usage (
						value = "In these latter three cases, the name facet designates the name of a temporary variable, whose scope is the loop, and that takes, in turn, the value of each of the element of the list (or each value in the interval). For example, in the first instance of the \"loop over\" syntax :",
						examples = { @example (
								value = "int a <- 0;"),
								@example (
										value = "loop i over: [10, 20, 30] {"),
								@example (
										value = "     a <- a + i;"),
								@example (
										value = "} // a now equals 60"),
								@example (
										var = "a",
										equals = "60",
										isTestOnly = true) }),
				@usage (
						value = "The second (quite common) case of the loop syntax allows one to use an interval of integers. The from and to facets take an integer expression as arguments, with the first (resp. the last) specifying the beginning (resp. end) of the inclusive interval (i.e. [to, from]). If the step is not defined, it is assumed to be equal to 1 or -1, depending on the direction of the range. If it is defined, its sign will be respected, so that a positive step will never allow the loop to enter a loop from i to j where i is greater than j",
						examples = { @example (
								value = "list the_list <-list (species_of (self));"),
								@example (
										value = "loop i from: 0 to: length (the_list) - 1 {"),
								@example (
										value = "     ask the_list at i {"),
								@example (
										value = "        // ..."),
								@example (
										value = "     }"),
								@example (
										value = "} // every  agent of the list is asked to do something") }) })
@serializer (LoopSerializer.class)
@validator (LoopValidator.class)
@SuppressWarnings ({ "rawtypes" })
public class LoopStatement extends AbstractStatementSequence implements Breakable {

	/**
	 * The Class LoopValidator.
	 */
	public static class LoopValidator implements IDescriptionValidator<IDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription description) {
			final IExpressionDescription times = description.getFacet(TIMES);
			final IExpressionDescription over = description.getFacet(OVER);
			final IExpressionDescription from = description.getFacet(FROM);
			final IExpressionDescription to = description.getFacet(TO);
			final IExpressionDescription cond = description.getFacet(WHILE);
			IExpressionDescription name = description.getFacet(NAME);
			if (name != null && name.isConst() && name.toString().startsWith(INTERNAL)) { name = null; }
			// See Issue #3085
			if (name != null) { Assert.nameIsValid(description); }
			if (times != null) {
				processTimes(description, over, from, to, cond, name);
			} else if (over != null) {
				processOver(description, from, to, cond, name);
			} else if (cond != null) {
				processCond(description, from, to, name);
			} else if (from != null) {
				processFromTo(description, to, name);
			} else if (to != null) {
				description.error("'loop' is missing the 'from:' facet", IGamlIssue.MISSING_FACET,
						description.getUnderlyingElement(), FROM, "0");
			} else {
				description.error("Missing the definition of the kind of loop to perform (times, over, while, from/to)",
						IGamlIssue.MISSING_FACET);
			}
		}

		/**
		 * Process from to.
		 *
		 * @param description
		 *            the description
		 * @param to
		 *            the to
		 * @param name
		 *            the name
		 */
		private void processFromTo(final IDescription description, final IExpressionDescription to,
				final IExpressionDescription name) {
			if (name == null) {
				description.error("No variable has been declared", IGamlIssue.MISSING_NAME, NAME);
				return;
			}
			if (to == null) {
				description.error("'loop' is missing the 'to:' facet", IGamlIssue.MISSING_FACET,
						description.getUnderlyingElement(), TO, "0");
			}
		}

		/**
		 * Process from to.
		 *
		 * @param description
		 *            the description
		 * @param from
		 *            the from
		 * @param to
		 *            the to
		 * @param name
		 *            the name
		 */
		private void processCond(final IDescription description, final IExpressionDescription from,
				final IExpressionDescription to, final IExpressionDescription name) {
			if (from != null) {
				description.error("'while' and 'from' are not compatible", IGamlIssue.CONFLICTING_FACETS, WHILE, FROM);
			}
			if (to != null) {
				description.error("'while' and 'to' are not compatible", IGamlIssue.CONFLICTING_FACETS, WHILE, TO);
			}
			if (name != null) { description.error("No variable should be declared", IGamlIssue.UNUSED, WHILE, NAME); }
		}

		/**
		 * Process over.
		 *
		 * @param description
		 *            the description
		 * @param from
		 *            the from
		 * @param to
		 *            the to
		 * @param cond
		 *            the cond
		 * @param name
		 *            the name
		 */
		private void processOver(final IDescription description, final IExpressionDescription from,
				final IExpressionDescription to, final IExpressionDescription cond, final IExpressionDescription name) {
			if (cond != null) {
				description.error("'over' and 'while' are not compatible", IGamlIssue.CONFLICTING_FACETS, OVER, WHILE);
			} else if (from != null) {
				description.error("'over' and 'from' are not compatible", IGamlIssue.CONFLICTING_FACETS, OVER, FROM);
			} else if (to != null) {
				description.error("'over' and 'to' are not compatible", IGamlIssue.CONFLICTING_FACETS, OVER, TO);
			}
			if (name == null) { description.error("No variable has been declared", IGamlIssue.MISSING_NAME, OVER); }
		}

		/**
		 * Process times.
		 *
		 * @param description
		 *            the description
		 * @param over
		 *            the over
		 * @param from
		 *            the from
		 * @param to
		 *            the to
		 * @param cond
		 *            the cond
		 * @param name
		 *            the name
		 * @return true, if successful
		 */
		private void processTimes(final IDescription description, final IExpressionDescription over,
				final IExpressionDescription from, final IExpressionDescription to, final IExpressionDescription cond,
				final IExpressionDescription name) {
			if (over != null) {
				description.error("'times' and 'over' are not compatible", IGamlIssue.CONFLICTING_FACETS, TIMES, OVER);
			} else if (cond != null) {
				description.error("'times' and 'while' are not compatible", IGamlIssue.CONFLICTING_FACETS, TIMES,
						WHILE);
			} else if (from != null) {
				description.error("'times' and 'from' are not compatible", IGamlIssue.CONFLICTING_FACETS, TIMES, FROM);
			} else if (to != null) {
				description.error("'times' and 'to' are not compatible", IGamlIssue.CONFLICTING_FACETS, TIMES, TO);
			}
			if (name != null) { description.error("No variable should be declared", IGamlIssue.UNUSED, NAME); }
		}

	}

	/**
	 * The Class LoopSerializer.
	 */
	public static class LoopSerializer extends SymbolSerializer<SymbolDescription> {

		@Override
		protected String serializeFacetValue(final SymbolDescription s, final String key,
				final boolean includingBuiltIn) {
			if (NAME.equals(key) && (s.hasFacet(TIMES) || s.hasFacet(WHILE))) return null;
			return super.serializeFacetValue(s, key, includingBuiltIn);
		}

	}

	/** The executer. */
	private final LoopExecuter executer;

	/** The var name. */
	private final String varName;
	// private final Object[] result = new Object[1];

	/**
	 * Instantiates a new loop statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public LoopStatement(final IDescription desc) {
		super(desc);
		final boolean isWhile = getFacet(IKeyword.WHILE) != null;
		final boolean isList = getFacet(IKeyword.OVER) != null;
		final boolean isBounded = getFacet(IKeyword.FROM) != null && getFacet(IKeyword.TO) != null;
		varName = desc.getName();
		executer = isWhile ? new While() : isList ? new Over() : isBounded ? new Bounded() : new Times();
	}

	@Override
	public void enterScope(final IScope scope) {
		// 25/02/14: Suppressed because already done in loopBody() :
		// super.enterScope(scope);

		// if (varName != null) { scope.addVarWithValue(varName, null); }
	}

	@Override
	public void leaveScope(final IScope scope) {
		// Should clear any _loop_halted status present
		// if (varName != null) { scope.removeAllVars(); }
		// scope.popLoop();
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		try {
			return executer.runIn(scope);
		} finally {
			scope.getAndClearBreakStatus();
		}
	}

	/**
	 * Loop body.
	 *
	 * @param scope
	 *            the scope
	 * @param var
	 *            the var
	 * @param result
	 *            the result
	 * @return true, if successful
	 */
	protected FlowStatus loopBody(final IScope scope, final Object var, final Object[] result) {
		scope.push(this);
		// We set it explicitely to the newly created scope
		if (varName != null) { scope.setVarValue(varName, var, true); }
		result[0] = super.privateExecuteIn(scope);
		scope.pop(this);
		// return !scope.interrupted();
		return scope.getAndClearContinueStatus();
	}

	/**
	 * The Interface LoopExecuter.
	 */
	interface LoopExecuter {

		/**
		 * Run in.
		 *
		 * @param scope
		 *            the scope
		 * @return the object
		 */
		Object runIn(final IScope scope);
	}

	/**
	 * The Class Bounded.
	 */
	class Bounded implements LoopExecuter {

		/** The from. */
		private final IExpression from = getFacet(IKeyword.FROM);

		/** The to. */
		private final IExpression to = getFacet(IKeyword.TO);

		/** The step. */
		private final IExpression step = getFacet(IKeyword.STEP);

		/** The constant step. */
		private Integer constantFrom, constantTo, constantStep;

		/** The step defined. */
		private final boolean stepDefined;

		/**
		 * Instantiates a new bounded.
		 *
		 * @throws GamaRuntimeException
		 *             the gama runtime exception
		 */
		Bounded() throws GamaRuntimeException {
			final IScope scope = null;
			// final IScope scope = GAMA.obtainNewScope();
			if (from.isConst()) { constantFrom = Cast.asInt(scope, from.value(scope)); }
			if (to.isConst()) { constantTo = Cast.asInt(scope, to.value(scope)); }
			if (step == null) {
				stepDefined = false;
				constantStep = 1;
			} else if (step.isConst()) {
				stepDefined = true;
				constantStep = Cast.asInt(scope, step.value(scope));
			} else {
				stepDefined = true;
			}
		}

		@Override
		public Object runIn(final IScope scope) throws GamaRuntimeException {
			final Object[] result = new Object[1];
			final int f = constantFrom == null ? Cast.asInt(scope, from.value(scope)) : constantFrom;
			final int t = constantTo == null ? Cast.asInt(scope, to.value(scope)) : constantTo;
			int s = constantStep == null ? Cast.asInt(scope, step.value(scope)) : constantStep;
			final boolean negative = f - t > 0;
			// if ( f == t ) { return null; }
			boolean shouldBreak = false;
			if (negative) {
				if (s > 0) {
					if (stepDefined) return null;
					s = -s;
				}
				for (int i = f, n = t - 1; i > n && !shouldBreak; i += s) {
					FlowStatus status = loopBody(scope, i, result);
					switch (status) {
						case CONTINUE:
							continue;
						case BREAK:
						case RETURN:
						case DIE:
						case DISPOSE:
							shouldBreak = true;
							break;
						default:
					}
				}
			} else {
				for (int i = f, n = t + 1; i < n && !shouldBreak; i += s) {
					FlowStatus status = loopBody(scope, i, result);
					switch (status) {
						case CONTINUE:
							continue;
						case BREAK:
						case RETURN:
						case DIE:
						case DISPOSE:
							shouldBreak = true;
							break;
						default:
					}
				}
			}
			return result[0];
		}
	}

	/**
	 * The Class Over.
	 */
	class Over implements LoopExecuter {

		/** The over. */
		private final IExpression over = getFacet(IKeyword.OVER);

		@Override
		public Object runIn(final IScope scope) throws GamaRuntimeException {
			final Object[] result = new Object[1];
			final Object obj = over.value(scope);
			final Iterable list_ =
					!(obj instanceof IContainer) ? Cast.asList(scope, obj) : ((IContainer) obj).iterable(scope);
			boolean shouldBreak = false;

			for (final Object each : list_) {

				switch (loopBody(scope, each, result)) {
					case CONTINUE:
						continue;
					case BREAK:
					case RETURN:
					case DIE:
					case DISPOSE:
						shouldBreak = true;
						break;
					default:
						;
				}
				if (shouldBreak) {
					break;
					// if (!loopBody(scope, each, result)) { break; } }
				}
			}
			return result[0];
		}
	}

	/**
	 * The Class Times.
	 */
	class Times implements LoopExecuter {

		/** The times. */
		private final IExpression times = getFacet(IKeyword.TIMES);

		/** The constant times. */
		private Integer constantTimes;

		/**
		 * Instantiates a new times.
		 *
		 * @throws GamaRuntimeException
		 *             the gama runtime exception
		 */
		Times() throws GamaRuntimeException {
			if (times.isConst()) { constantTimes = Types.INT.cast(null, times.getConstValue(), null, false); }
		}

		@Override
		public Object runIn(final IScope scope) throws GamaRuntimeException {
			final Object[] result = new Object[1];
			final int max = constantTimes == null ? Cast.asInt(scope, times.value(scope)) : constantTimes;
			boolean shouldBreak = false;
			for (int i = 0; i < max && !shouldBreak; i++) {
				switch (loopBody(scope, null, result)) {
					case CONTINUE:
						continue;
					case BREAK:
					case RETURN:
					case DIE:
					case DISPOSE:
						shouldBreak = true;
						break;
					default:
						;
				}
			}
			return result[0];
		}

	}

	/**
	 * The Class While.
	 */
	class While implements LoopExecuter {

		/** The cond. */
		private final IExpression cond = getFacet(IKeyword.WHILE);

		@Override
		public Object runIn(final IScope scope) throws GamaRuntimeException {
			final Object[] result = new Object[1];
			boolean shouldBreak = false;
			while (Cast.asBool(scope, cond.value(scope)) && !shouldBreak) {
				switch (loopBody(scope, null, result)) {
					case CONTINUE:
						continue;
					case BREAK:
					case RETURN:
					case DIE:
					case DISPOSE:
						shouldBreak = true;
						break;
					default:
						;
				}
			}
			return result[0];
		}
	}

}
