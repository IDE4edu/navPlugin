package studentview.views;

//Andy Carle, Berkeley Institute of Design, UC Berkeley 
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
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

import studentview.model.Step;
import studentview.model.Assignment;
import studentview.model.Step.ExerciseType;

import static java.lang.System.out;


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

	/**
	 * The constructor.
	 */
	public UCWISENav() {
		System.out.print("hi");
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
		
		/*
		final Combo combo = new Combo(rootparent, SWT.READ_ONLY);
		
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int sel = combo.getSelectionIndex();
				if (sel >= 0 && sel < isagroups.size()) {					
					stackLayout.topControl = isagroups.get(sel).group;
					isaHolder.layout();
				}
			}
		});
		*/
		
		isaHolder = new Group(rootparent, SWT.SHADOW_NONE);
		stackLayout = new StackLayout();
		isaHolder.setLayout(stackLayout);
		//isaHolder.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true, 2, 1));
		isaHolder.setLayoutData(new RowData());
		//isaHolder.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		final Image selection = select;
		getAssignment.addSelectionListener(new SelectionListener() {

		    public void widgetSelected(SelectionEvent event) {
		    	Shell shell = new Shell();
				AssignmentChooser dialog = new AssignmentChooser(shell);
				dialog.create();
				if (dialog.open() == org.eclipse.jface.window.Window.OK) {
					seg = dialog.getSegment();
					
					final SequenceWidget parent = new SequenceWidget(isaHolder, SWT.SHADOW_NONE, seg);
					Group buttons = new Group(parent.group, SWT.SHADOW_NONE);

					parent.currentStep = new Label(buttons, SWT.WRAP);
				
					RowLayout buttonsLO = new RowLayout();
					buttonsLO.justify = true;
					buttons.setLayout(buttonsLO);
				
					final Label intro = new Label(parent.group, SWT.WRAP);
					
					intro.setText(seg.getIntro());
					parent.intro = intro;
					intro.setLayoutData(new RowData(title.getSize().x, 70));
					
					for (final Step e : seg.getExercises()) {
						Group stepline = new Group(parent.group, SWT.SHADOW_NONE);
						Label sel = new Label(stepline, SWT.WRAP);			
						sel.setImage(selection);
						sel.setVisible(false);
						Label step = new Label(stepline, SWT.WRAP); 
						step.setText(e.getName());
						step.addMouseListener(parent);
						
						Button test = null;
						Button reset = null;
						if (e.getTestname() != null && !("".equalsIgnoreCase(e.getTestname().trim()))) {
							test = new Button(stepline, 0);
							test.setText("Run Tests");
							//test.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
						}
						
						if (e.getType() == ExerciseType.EDIT) {
							reset = new Button(stepline, 0);				
							reset.setText("Reset Exercise");
							//reset.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
						}							
					
						stepline.setLayout(new RowLayout());			
						
						final Button showNext = new Button(stepline, SWT.ARROW | SWT.DOWN);
						final Button showPrev = new Button(stepline, SWT.ARROW | SWT.UP);
						showNext.setEnabled(true);
				    	showPrev.setEnabled(false);
				    	
				    	final Label intro1 = new Label(parent.group, SWT.WRAP);
				    	final String nameText = intro1.getText();
				    	//intro1.setLayoutData(new RowData(intro2.getSize().x, 100));
				    	
						showNext.addSelectionListener(new SelectionListener() {

						    public void widgetSelected(SelectionEvent event) {
						    	showNext.setEnabled(false);
						    	showPrev.setEnabled(true);
						    	intro1.setText(e.getIntro());
						    }

						    public void widgetDefaultSelected(SelectionEvent event) {
						    	showNext.setEnabled(true);
						    	showPrev.setEnabled(false);
						    }
						});
						
						showPrev.addSelectionListener(new SelectionListener() {

							public void widgetSelected(SelectionEvent event) {
						    	showNext.setEnabled(true);
						    	showPrev.setEnabled(false);
						    	intro1.setText(nameText);
						    }

						    public void widgetDefaultSelected(SelectionEvent event) {
						  	    showNext.setEnabled(false);
						    	showPrev.setEnabled(true);
						    }
						});

						StepWidgets widge = new StepWidgets(sel, step, e, stepline, test, reset);
						parent.steps.add(widge);
						parent.group.setLayout(setupLayout());
						isagroups.add(parent);
					}
					
					if (isagroups.size() > 0) {			
						stackLayout.topControl = isagroups.get(0).group;
						//combo.select(0);
					}
					
				}
		    }

		    public void widgetDefaultSelected(SelectionEvent event) {
		    }
		});
		
		//isaHolder.layout();
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