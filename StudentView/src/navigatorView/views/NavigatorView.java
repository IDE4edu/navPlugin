package navigatorView.views;

import java.io.IOException;

import java.net.URL;
import java.util.Vector;


import navigatorView.NavigatorActivator;
import navigatorView.controller.AssignmentController;
import navigatorView.model.Assignment;

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


/*
 */

public class NavigatorView extends ViewPart{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.berkeley.eduride.navigatorview";

	// title of the view
	Label title;
	Vector<SequenceWidget> isagroups = new Vector<SequenceWidget>();

	Group hidden;
	RowData rowdata = new RowData();
	StackLayout stackLayout;
	Group isaHolder;
	Assignment seg;
	Image selection;
	NavigatorActivator plugin;



	/**
	 * The constructor.
	 */
	public NavigatorView() {
		plugin = NavigatorActivator.getDefault();
	}

	public Assignment getCurrentAssignment() {
		return seg;
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

		AbstractUIPlugin plugin = NavigatorActivator.getDefault();
		ImageRegistry imageRegistry = plugin.getImageRegistry();
		selection = imageRegistry.get(NavigatorActivator.SELECTION_IMAGE_ID); 
		

		rootparent.setLayout(setupLayout());
		rootparent.setLayoutData(rowdata);

		
		title = new Label(rootparent, SWT.WRAP);
		title.setText("Eduride Assignment");

		Button getAssignment = new Button(rootparent, SWT.PUSH | SWT.CENTER);
		getAssignment.setEnabled(true);
		getAssignment.setText("Get Assignment");

		isaHolder = new Group(rootparent, SWT.SHADOW_NONE);
		stackLayout = new StackLayout();
		isaHolder.setLayout(stackLayout);
		isaHolder.setLayoutData(new RowData());
		
		getAssignment.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				chooseAssignment();
				// reset the layout for the new controls
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().layout(true, true);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}

		});
	}
	
	public void chooseAssignment() {
		Shell shell = new Shell();
		AssignmentChooser dialog = new AssignmentChooser(shell);
		dialog.create();
		if (dialog.open() == org.eclipse.jface.window.Window.OK) {
			seg = dialog.getSegment();
			SequenceWidget parent = new SequenceWidget(isaHolder, SWT.SHADOW_NONE, seg, selection);
			stackLayout.topControl = parent.mainGroup;
			isaHolder.layout();
			title.setText(seg.getName());
			NavigatorActivator.getDefault().openISA(seg);
			
			// select first step
			StepWidget first = parent.stepWidgets.firstElement();
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

