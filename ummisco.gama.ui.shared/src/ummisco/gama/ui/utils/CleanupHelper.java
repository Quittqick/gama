/*******************************************************************************************************
 *
 * CleanupHelper.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.eclipse.ui.internal.ActionSetContributionItem;
import org.eclipse.ui.internal.CoolBarToTrimManager;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.views.GamaPreferencesView;

/**
 * The Class CleanupHelper.
 */
public class CleanupHelper {

	static {
		DEBUG.ON();
	}

	/**
	 * Run.
	 */
	public static void run() {
		RemoveUnwantedWizards.run();
		RemoveUnwantedActionSets.run();
		RearrangeMenus.run();
		ForceMaximizeRestoration.run();
		RemoveActivities.run();
		Job prefs = new Job("Preloading preferences") {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				GamaPreferencesView.preload();
				return Status.OK_STATUS;
			}
		};
		prefs.setUser(true);
		prefs.setPriority(Job.DECORATE);
		prefs.schedule();

	}

	/**
	 * The Class RemoveActivities.
	 */
	static class RemoveActivities {

		/**
		 * Run.
		 */
		static void run() {
			final IWorkbenchActivitySupport was = PlatformUI.getWorkbench().getActivitySupport();
			was.setEnabledActivityIds(new HashSet<>());
		}
	}

	/**
	 * The Class ForceMaximizeRestoration.
	 */
	static class ForceMaximizeRestoration {

		/**
		 * Run.
		 */
		public static void run() {

			final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			for (final IWorkbenchWindow window : windows) {
				final IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					page.addPartListener(new IPartListener2() {

						@Override
						public void partVisible(final IWorkbenchPartReference partRef) {}

						@Override
						public void partOpened(final IWorkbenchPartReference partRef) {}

						@Override
						public void partInputChanged(final IWorkbenchPartReference partRef) {}

						@Override
						public void partHidden(final IWorkbenchPartReference partRef) {}

						@Override
						public void partDeactivated(final IWorkbenchPartReference partRef) {}

						@Override
						public void partClosed(final IWorkbenchPartReference partRef) {}

						@Override
						public void partBroughtToTop(final IWorkbenchPartReference partRef) {}

						@Override
						public void partActivated(final IWorkbenchPartReference partRef) {
							final IViewReference[] refs = page.getViewReferences();
							final IEditorReference[] eds = page.getEditorReferences();
							for (final IViewReference ref : refs) {
								if (!partRef.equals(ref) && page.getPartState(ref) == IWorkbenchPage.STATE_MAXIMIZED) {
									page.toggleZoom(ref);
									break;
								}
							}
							for (final IEditorReference ref : eds) {
								if (!partRef.equals(ref) && page.getPartState(ref) == IWorkbenchPage.STATE_MAXIMIZED) {
									page.toggleZoom(ref);
									break;
								}
							}

						}
					});
				}
			}
		}
	}

	/**
	 * The Class RemoveUnwantedActionSets.
	 */
	static class RemoveUnwantedActionSets extends PerspectiveAdapter /* implements IStartup */ {

		/** The toolbar action sets to remove. */
		String[] TOOLBAR_ACTION_SETS_TO_REMOVE = { "org.eclipse", "msi.gama.lang.gaml.Gaml",
				"org.eclipse.ui.edit.text.actionSet.convertLineDelimitersTo" };

		/** The menus to remove. */
		String[] MENUS_TO_REMOVE = { "org.eclipse.ui.run", "window", "navigate", "project" };

		/**
		 * Run.
		 */
		public static void run() {
			final RemoveUnwantedActionSets remove = new RemoveUnwantedActionSets();
			final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			for (final IWorkbenchWindow window : windows) {
				final IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					// Doing the initial cleanup on the default perspective
					// (modeling)
					remove.perspectiveActivated(page, null);
				}
				window.addPerspectiveListener(remove);
			}
		}

		@Override
		public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
			// if (perspective != null) {
			// DEBUG.OUT("Perspective " + perspective.getId() + " activated");
			// }
			final WorkbenchWindow w = (WorkbenchWindow) page.getWorkbenchWindow();
			if (w.isClosing()) return;
			WorkbenchHelper.runInUI("Cleaning menus", 0, m -> {
				try {
					if (w.isClosing()) return;
					final CoolBarToTrimManager cm = (CoolBarToTrimManager) w.getCoolBarManager2();
					final IContributionItem[] items = cm.getItems();
					// We remove all contributions to the toolbar that do not relate to gama
					for (final IContributionItem item : items) {
						for (final String s1 : TOOLBAR_ACTION_SETS_TO_REMOVE) {
							if (item.getId().contains(s1)) {
								try {
									if (w.getCoolBarManager2().find(item.getId()) != null) {
										w.getCoolBarManager2().remove(item);
									}
								} catch (final Exception e) {}
							}
						}
					}

					// exploreMenus(w.getMenuBarManager(), "");
					for (final String s2 : MENUS_TO_REMOVE) {
						w.getMenuBarManager().remove(s2);
						w.getMenuManager().remove(s2);
					}
					w.getMenuManager().update(true);
					w.getMenuBarManager().update(true);
					cm.updateAll(true);
					// WorkaroundForIssue3210.run(cm);
				} catch (final Exception e) {}
			});

		}

		/**
		 * Perspective changed.
		 *
		 * @param p
		 *            the p
		 * @param d
		 *            the d
		 * @param c
		 *            the c
		 */
		@Override
		public void perspectiveChanged(final IWorkbenchPage p, final IPerspectiveDescriptor d, final String c) {
			if (IWorkbenchPage.CHANGE_RESET_COMPLETE.equals(c)) { perspectiveActivated(p, d); }

		}

	}

	/**
	 * The Class RemoveUnwantedWizards.
	 */
	static class RemoveUnwantedWizards {

		/** The categories to remove. */
		private static Set<String> CATEGORIES_TO_REMOVE =
				new HashSet<>(Arrays.asList("org.eclipse.pde.PDE", "org.eclipse.emf.codegen.ecore.ui.wizardCategory"));

		/** The ids to remove. */
		private static Set<String> IDS_TO_REMOVE = new HashSet<>(
				Arrays.asList("org.eclipse.ui.wizards.new.project", "org.eclipse.equinox.p2.replication.import",
						"org.eclipse.equinox.p2.replication.importfrominstallation",
						"org.eclipse.team.ui.ProjectSetImportWizard", "org.eclipse.equinox.p2.replication.export",
						"org.eclipse.team.ui.ProjectSetExportWizard"));

		/**
		 * Run.
		 */
		static void run() {
			final List<IWizardCategory> cats = new ArrayList<>();
			AbstractExtensionWizardRegistry r =
					(AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getNewWizardRegistry();
			cats.addAll(Arrays.asList(r.getRootCategory().getCategories()));
			r = (AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getImportWizardRegistry();
			cats.addAll(Arrays.asList(r.getRootCategory().getCategories()));
			r = (AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getExportWizardRegistry();
			cats.addAll(Arrays.asList(r.getRootCategory().getCategories()));
			for (final IWizardDescriptor wizard : getAllWizards(cats.toArray(new IWizardCategory[0]))) {
				final String id = wizard.getCategory().getId();
				if (CATEGORIES_TO_REMOVE.contains(id) || IDS_TO_REMOVE.contains(wizard.getId())) {
					// DEBUG.LOG("Removing wizard " + wizard.getId() +
					// " in category " + id);
					final WorkbenchWizardElement element = (WorkbenchWizardElement) wizard;
					r.removeExtension(element.getConfigurationElement().getDeclaringExtension(),
							new Object[] { element });
				}
			}

		}

		/**
		 * Gets the all wizards.
		 *
		 * @param categories
		 *            the categories
		 * @return the all wizards
		 */
		static private IWizardDescriptor[] getAllWizards(final IWizardCategory[] categories) {
			final List<IWizardDescriptor> results = new ArrayList<>();
			for (final IWizardCategory wizardCategory : categories) {

				results.addAll(Arrays.asList(wizardCategory.getWizards()));
				results.addAll(Arrays.asList(getAllWizards(wizardCategory.getCategories())));
			}
			return results.toArray(new IWizardDescriptor[0]);
		}

	}

	/**
	 * The Class RearrangeMenus.
	 */
	static class RearrangeMenus {

		/** The Constant MENU_ITEMS_TO_REMOVE. */
		public final static Set<String> MENU_ITEMS_TO_REMOVE = new HashSet<>(Arrays.asList("openWorkspace",
				"helpSearch", "org.eclipse.search.OpenFileSearchPage", "textSearchSubMenu", "reopenEditors",
				"converstLineDelimitersTo", "org.eclipse.equinox.p2.ui.sdk.update",
				"org.eclipse.equinox.p2.ui.sdk.install", "org.eclipse.equinox.p2.ui.sdk.installationDetails",
				"org.eclipse.e4.ui.importer.openDirectory.menu"));

		/** The Constant MENU_IMAGES. */
		public final static Map<String, String> MENU_IMAGES = new HashMap<>() {
			{
				put("print", "menu.print2");
				put("save", "menu.save2");
				put("saveAs", "menu.saveas2");
				put("saveAll", "menu.saveall2");
				put("revert", "menu.revert2");
				put("refresh", "navigator/navigator.refresh2");
				put("new", "navigator/navigator.new2");
				put("import", "menu.import2");
				put("export", "menu.export2");
				put("undo", "menu.undo2");
				put("redo", "menu.redo2");
				put("cut", "menu.cut2");
				put("copy", "menu.copy2");
				put("paste", "menu.paste2");
				put("delete", "menu.delete2");
				put("helpContents", "menu.help2");
				put("org.eclipse.search.OpenSearchDialog", "menu.search2");
				put("org.eclipse.ui.openLocalFile", "navigator/navigator.open2");
				put("converstLineDelimitersTo", "menu.delimiter2");
			}
		};

		/**
		 * Run.
		 */
		public static void run() {
			WorkbenchHelper.runInUI("Rearranging menus", 0, m -> {
				final IWorkbenchWindow window = Workbench.getInstance().getActiveWorkbenchWindow();
				if (window instanceof WorkbenchWindow) {
					final IMenuManager menuManager = ((WorkbenchWindow) window).getMenuManager();
					for (final IContributionItem item : menuManager.getItems()) {
						IMenuManager menu = null;
						if (item instanceof MenuManager) {
							menu = (MenuManager) item;
						} else if (item instanceof ActionSetContributionItem
								&& ((ActionSetContributionItem) item).getInnerItem() instanceof MenuManager) {
							menu = (MenuManager) ((ActionSetContributionItem) item).getInnerItem();
						}
						if (menu != null) { processItems(menu); }
					}
					menuManager.updateAll(true);
				}

			});

		}

		/**
		 * Process items.
		 *
		 * @param menu
		 *            the menu
		 */
		private static void processItems(final IMenuManager menu) {
			// final StringBuilder sb = new StringBuilder();
			// sb.append("Menu ").append(menu.getId()).append(" :: ");
			for (final IContributionItem item : menu.getItems()) {
				final String name = item.getId();
				// DEBUG.LOG(name);
				if (MENU_ITEMS_TO_REMOVE.contains(name)) {
					item.setVisible(false);
					continue;
				}
				if (item.isGroupMarker() || item.isSeparator() || !item.isVisible()) { continue; }
				if (MENU_IMAGES.containsKey(name)) {
					changeIcon(menu, item, GamaIcons.create(MENU_IMAGES.get(name)).descriptor());
				}
				// sb.append(Strings.LN).append(Strings.TAB);
				// sb.append(name).append('[').append(item.getClass().getSimpleName()).append("]:: ");
			}
			// DEBUG.LOG(sb.toString());
		}

		/**
		 * Change icon.
		 *
		 * @param menu
		 *            the menu
		 * @param item
		 *            the item
		 * @param image
		 *            the image
		 */
		private static void changeIcon(final IMenuManager menu, final IContributionItem item,
				final ImageDescriptor image) {
			if (item instanceof ActionContributionItem) {
				((ActionContributionItem) item).getAction().setImageDescriptor(image);
			} else if (item instanceof CommandContributionItem) {
				final CommandContributionItemParameter data = ((CommandContributionItem) item).getData();
				data.commandId = ((CommandContributionItem) item).getCommand().getId();
				data.icon = image;
				final CommandContributionItem newItem = new CommandContributionItem(data);
				newItem.setId(item.getId());
				menu.insertAfter(item.getId(), newItem);
				menu.remove(item);
				item.dispose();
			} else if (item instanceof ActionSetContributionItem) {
				changeIcon(menu, ((ActionSetContributionItem) item).getInnerItem(), image);
			} else if (item instanceof MenuManager) { ((MenuManager) item).setImageDescriptor(image); }
		}

	}

}
