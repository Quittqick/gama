/*******************************************************************************************************
 *
 * GamlLinkingService.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.linking;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.linking.impl.DefaultLinkingService;
import org.eclipse.xtext.linking.impl.IllegalNodeException;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.scoping.IScope;

import com.google.inject.Inject;

import msi.gama.lang.gaml.EGaml;
import msi.gama.lang.gaml.gaml.GamlDefinition;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.runtime.IExecutionContext;

/**
 * The class GamlLinkingService.
 * 
 * 
 * Provide the linking semantics for GAML. The references to 'imported' table definitions are stubbed out by this class
 * until the Indexer is implemented.
 * 
 * @author John Bito, adapted by Alexis Drogoul
 * @since 10 mai 2012
 */
public class GamlLinkingService extends DefaultLinkingService {

	/**
	 * Keep stubs so that new ones aren't created for each linking pass.
	 */
	private static final Map<String, List<EObject>> stubbedRefs = new Hashtable<>();
	
	/** The stubs resource. */
	private static Resource stubsResource;

	/** The resource set. */
	@Inject private XtextResourceSet resourceSet;

	/**
	 * Instantiates a new gaml linking service.
	 */
	public GamlLinkingService() {
		super();

	}

	/**
	 * Adds the symbol.
	 *
	 * @param name the name
	 * @param clazz the clazz
	 * @return the list
	 */
	public List<EObject> addSymbol(final String name, final EClass clazz) {
		List<EObject> list = stubbedRefs.get(name);
		if (list == null) {
			// DEBUG.LOG("Adding stub reference to " + name + " as a "
			// + clazz.getName());
			// DEBUG.LOG("****************************************************");
			final EObject stub = create(name, clazz);
			getResource().getContents().add(stub);
			list = Collections.singletonList(stub);
			stubbedRefs.put(name, list);
		}
		return list;
	}

	/**
	 * Creates the.
	 *
	 * @param name the name
	 * @param clazz the clazz
	 * @return the e object
	 */
	public EObject create(final String name, final EClass clazz) {
		final GamlDefinition stub = (GamlDefinition) EGaml.getInstance().getFactory().create(clazz);
		stub.setName(name);
		return stub;
	}

	/**
	 * Gets the resource.
	 *
	 * @return the resource
	 */
	private Resource getResource() {
		if (stubsResource == null) {
			stubsResource = resourceSet.createResource(URI.createURI("gaml:/newSymbols.xmi", false));
		}
		return stubsResource;
	}

	@Override
	protected IScope getScope(final EObject context, final EReference reference) {
		try {
			// AD: Necessary to save memory (cache)
			registerImportedNamesAdapter(context);
			return getScopeProvider().getScope(context, reference);
		} finally {
			unRegisterImportedNamesAdapter();
		}
	}

	/**
	 * Override default in order to supply a stub object. If the default implementation isn't able to resolve the link,
	 * assume it to be a local resource.
	 */
	@Override
	public List<EObject> getLinkedObjects(final EObject context, final EReference ref, final INode node)
			throws IllegalNodeException {
		final List<EObject> result = super.getLinkedObjects(context, ref, node);
		// If the default implementation resolved the link, return it
		if (null != result && !result.isEmpty()) { return result; }
		final String name = getCrossRefNodeAsString(node);
		if (GamlPackage.eINSTANCE.getTypeDefinition()
				.isSuperTypeOf(ref.getEReferenceType())) { return addSymbol(name, ref.getEReferenceType()); }
		if (GamlPackage.eINSTANCE.getVarDefinition()
				.isSuperTypeOf(ref.getEReferenceType())) { return addSymbol(name, ref.getEReferenceType());
		// if (name.startsWith("pref_")) {
		// return addSymbol(name, ref.getEReferenceType());
		// } else {
		// if (context.eContainer() instanceof Parameter) {
		// final Parameter p = (Parameter) context.eContainer();
		// if (p.getLeft() == context) { return addSymbol(name, ref.getEReferenceType()); }
		// }
		// }
		// if (stubbedRefs.containsKey(name)) { return stubbedRefs.get(name); }
		}
		final GamlResource resource = (GamlResource) context.eResource();
		final IExecutionContext additionalContext = resource.getCache().getOrCreate(resource).get("linking");
		if (additionalContext != null) {
			if (additionalContext
					.hasLocalVar(name)) { return Collections.singletonList(create(name, ref.getEReferenceType())); }
		}
		return Collections.EMPTY_LIST;
	}
}
