package navigatorView.views;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;



import navigatorView.NavigatorActivator;
import navigatorView.controller.ActivityController;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

import edu.berkeley.eduride.base_plugin.model.Activity;


/*
 */

public class NavigatorView extends ViewPart{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.berkeley.eduride.navigatorview";

	// title of the view
	Label title;
	Vector<ActivityWidget> isagroups = new Vector<ActivityWidget>();

	Group hidden;
	RowData rowdata = new RowData();
	StackLayout stackLayout;
	Group isaHolder;
	Activity act;
	Image selection;
	NavigatorActivator plugin;



	/**
	 * The constructor.
	 */
	public NavigatorView() {
		plugin = NavigatorActivator.getDefault();
	}

	public Activity getCurrentActivity() {
		return act;
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */

	public void createPartControl(Composite rootparent) {

//		String selectionImage = "";

//		try {
//			String filename = "icons/selection.gif";
//			Bundle bun = Platform.getBundle("StudentView");
//			IPath ip = new Path(filename);		
//			URL url = FileLocator.find(bun, ip, null);
//			URL res = FileLocator.resolve(url);
//			selectionImage = res.getPath();
//		} catch (IOException e) {
//			System.err.println("Could not find image file.");
//			e.printStackTrace();
//		}


		selection = NavigatorActivator.getImage(NavigatorActivator.SELECTION_IMAGE);

		
		
		rootparent.setLayout(setupLayout());
		rootparent.setLayoutData(rowdata);

		
		title = new Label(rootparent, SWT.WRAP);
		title.setText("Eduride Activity");

		Button getActivity = new Button(rootparent, SWT.PUSH | SWT.CENTER);
		getActivity.setEnabled(true);
		getActivity.setText("Get Activity");

		isaHolder = new Group(rootparent, SWT.SHADOW_NONE);
		stackLayout = new StackLayout();
		isaHolder.setLayout(stackLayout);
		isaHolder.setLayoutData(new RowData());
		
		getActivity.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				chooseActivty();
				// reset the layout for the new controls
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().layout(true, true);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}

		});
	}
	
	public void chooseActivty() {
		Shell shell = new Shell();
		ActivityChooser dialog = new ActivityChooser(shell);
		dialog.create();
		if (dialog.open() == org.eclipse.jface.window.Window.OK) {
			act = dialog.getActivity();
			ActivityWidget parent = new ActivityWidget(isaHolder, SWT.SHADOW_NONE, act, selection);
			stackLayout.topControl = parent.mainGroup;
			isaHolder.layout();
			title.setText(act.getName());
			NavigatorActivator.getDefault().openISA(act);
			
			// select first step
			StepWidget first = parent.stepWidgets.get(0);
			parent.gotoStep(first);
		}
	}

	private Layout setupLayout() {
		RowLayout layout = new RowLayout();
		layout.wrap = false;
		layout.pack = true;
		layout.fill = false;		
		layout.justify = false;;
		layout.type = SWT.VERTICAL;
		layout.spacing = 10;		
		return layout;		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		title.setFocus();
	}

}

