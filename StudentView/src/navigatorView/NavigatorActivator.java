package navigatorView;

//Andy Carle, Berkeley Institute of Design, UC Berkeley

import java.io.File;
import java.util.ArrayList;

import navigatorView.controller.NavigationListener;
import navigatorView.model.Assignment;
import navigatorView.model.Step;
import navigatorView.views.NavigatorView;

import edu.berkeley.eduride.base_plugin.*;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;


/**
 * The activator class controls the plug-in life cycle
 */
public class NavigatorActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "edu.berkeley.eduride.navigatorview"; //$NON-NLS-1$

	// The shared instance
	private static NavigatorActivator plugin;

	private IWebBrowser browser = null;

	/**
	 * The constructor
	 */
	public NavigatorActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// context.
		plugin = this;

		initBrowser();   // try, anyway.
	}

	
	private void initBrowser() {
		try {
			IWorkbench wb = PlatformUI.getWorkbench();
			if (wb != null) {
				IWorkbenchBrowserSupport wbbs = wb.getBrowserSupport();
				if (wbbs != null) {

					browser = wbbs.createBrowser(PLUGIN_ID);
				}
			}
		} catch (PartInitException e) {
			// e.printStackTrace();
		} catch (IllegalStateException e) {
			// e.printStackTrace();
		}
	}


	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static NavigatorActivator getDefault() {
		return plugin;
	}

	
	
	//////  shared images
	
	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static final String SELECTION_IMAGE_ID = "edu.berkeley.eduride.navigation.selection";

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);
		Bundle bundle = Platform.getBundle(PLUGIN_ID);

		ImageDescriptor myImage = ImageDescriptor.createFromURL(FileLocator
				.find(bundle, new Path("icons/selection.gif"), null));
		registry.put(SELECTION_IMAGE_ID, myImage);
	}

	// //////////////////////

	public IWebBrowser getBrowser() {
		if (browser == null) {
			initBrowser();
		}
		return browser;
	}

	
	
	
	
	// returns "Welcome <user>", etc
	public String getWelcomeMessage() {
		String username = EduRideBase.getDisplayNameMaybeLogin();
		if (username == null) {
			return "(not authenticated)";
		} else {
			return "Welcome " + username;
		}
	}
	
	// ///////////////////

	// EduRideJunitView

	
	
	// methods to figure out what the last launch launchconfig was
	
	private static ILaunchConfiguration currentLaunchConfig = null;
	public static void setCurrentLaunchConfig(ILaunchConfiguration config) {
		currentLaunchConfig = config;
	}
	
	
	public static ILaunchConfiguration getLastLaunchConfiguration() {
		return currentLaunchConfig;
	}
	
	
	
	/////////////////////////////////////////
	
	///  step changed listeners
	
	ArrayList<NavigationListener> listeners = new ArrayList<NavigationListener>();

	public boolean registerListener(NavigationListener l) {
		boolean result = listeners.add(l);
		return (result);
	}

	public boolean removeListener(NavigationListener l) {
		return (listeners.remove(l));
	}

	// called when the step is changed in the view
	public void stepChanged(Step oldstep, Step newstep) {
		for (NavigationListener l : listeners) {
			l.stepChanged(oldstep, newstep);
		}
	}

	
	// called when the 'test' button clicked on a step
	public void invokeTest(Step step, String launchConfig) {
		for (NavigationListener l : listeners) {
			l.invokeTest(step, launchConfig);
		}
	}

	
	public void openISA(Assignment ass) {
		for (NavigationListener l : listeners) {
			l.openISA(ass);
		}
	}
	

	
	

}
