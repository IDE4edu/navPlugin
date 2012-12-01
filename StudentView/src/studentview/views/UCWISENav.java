package studentview.views;

import java.io.IOException;

import java.net.URL;
import java.util.Vector;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import studentview.Activator;
import studentview.model.Assignment;
import studentview.controller.AssignmentController;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class UCWISENav extends ViewPart{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "studentview.views.SampleView";


	Label title;
	Vector<SequenceWidget> isagroups = new Vector<SequenceWidget>();

	Group hidden;
	RowData rowdata = new RowData();
	StackLayout stackLayout;
	Group isaHolder;
	Assignment seg;
	Image selection;
	Activator plugin;

	/**
	 * The constructor.
	 */
	public UCWISENav() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */

	public void createPartControl(Composite rootparent) {

		String selectionImage = "";

		try {
			String filename = "icons/selection.gif";
			Bundle bun = Platform.getBundle("StudentView");
			IPath ip = new Path(filename);		
			URL url = FileLocator.find(bun, ip, null);
			URL res = FileLocator.resolve(url);
			selectionImage = res.getPath();
		} catch (IOException e) {
			System.err.println("Could not find image file.");
			e.printStackTrace();
		}

		Image select = new Image(rootparent.getDisplay(), selectionImage);

		rootparent.setLayout(setupLayout());
		rootparent.setLayoutData(rowdata);

		title = new Label(rootparent, SWT.WRAP);
		title.setText("Assignments");

		Button getAssignment = new Button(rootparent, SWT.PUSH);
		getAssignment.setEnabled(true);
		getAssignment.setText("Get Assignment");

		isaHolder = new Group(rootparent, SWT.SHADOW_NONE);
		stackLayout = new StackLayout();
		isaHolder.setLayout(stackLayout);
		isaHolder.setLayoutData(new RowData());
		selection = select;
		
		getAssignment.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				chooseAssignment();
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
			stackLayout.topControl = parent.group;
			isaHolder.layout();
			title.setText(seg.getName());
		}
	}

	private Layout setupLayout() {
		RowLayout layout = new RowLayout();
		layout.wrap = true;
		layout.pack = true;
		layout.fill = true;		
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

