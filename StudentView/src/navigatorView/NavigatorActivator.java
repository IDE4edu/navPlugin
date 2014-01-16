package navigatorView;

//Andy Carle, Berkeley Institute of Design, UC Berkeley

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import navigatorView.controller.NavigationListener;
import navigatorView.views.NavigatorView;
import edu.berkeley.eduride.base_plugin.*;
import edu.berkeley.eduride.base_plugin.model.Activity;
import edu.berkeley.eduride.base_plugin.model.Step;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
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
					int style = IWorkbenchBrowserSupport.AS_EDITOR;
					browser = wbbs.createBrowser(style, PLUGIN_ID, "EduRide", "EduRide");
				}
			}
		} catch (PartInitException e) {
			log("install", "Failed to initialize browser -- partInitException");
		} catch (IllegalStateException e) {
			log("install", "Failed to initialize browser -- IllegalStateException");
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
	 * setup shared images
	 */
	public static final String SELECTION_IMAGE = "icons/selection.gif";
	

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);
		
		putImageIntoRegistry(SELECTION_IMAGE, registry);
	}
	
	
	
	////////// 
	// how to get an image
	
	
	public static Image getImage(String imageID) {
		ImageRegistry registry = getDefault().getImageRegistry();
		return (registry.get(imageID));
	}
	



	//////

	
	private void putImageIntoRegistry(String pathStr, ImageRegistry registry) {

		ImageDescriptor selection = ImageDescriptor.createFromURL(FileLocator
				.find(Platform.getBundle(PLUGIN_ID), new Path(pathStr), null));
		registry.put(pathStr, selection);
	}



	
	////////////////////////////////////////
	
	
	
	
	
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
	// UNUSED
	public static void setCurrentLaunchConfig(ILaunchConfiguration config) {
		currentLaunchConfig = config;
	}
	
	
	// UNUSED RIGHT NOW. 
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

	
	public void openISA(Activity ass) {
		for (NavigationListener l : listeners) {
			l.openISA(ass);
		}
	}
	

	public void log(String action, String message) {
		for (NavigationListener l : listeners) {
			l.log(action, message);
		}
	}
	
	public void logFileContents(File txtfile) {
		for (NavigationListener l : listeners) {
			try {
				String action = "File:" + txtfile.getName();
				l.log(action, txtfile);
			} catch (FileNotFoundException e) {
				l.log("fileLogFail", "Whoops, tried to log entire file that didn't exist: " + txtfile.getPath());
				e.printStackTrace();
			}
		}
	}
	
	

}
