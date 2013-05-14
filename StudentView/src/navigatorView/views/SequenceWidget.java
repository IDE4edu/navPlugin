package navigatorView.views;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Vector;

import javax.security.auth.callback.LanguageCallback;

import navigatorView.NavigatorActivator;
import navigatorView.model.Assignment;
import navigatorView.model.Step;
import navigatorView.model.Step.ExerciseType;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.ide.IDE;

public class SequenceWidget implements SelectionListener, MouseListener {

	Assignment assignment;
	Font bold;
	Font italic;
	// NATE removed this ugly thing
	// Label currentStep;
	// Button next, back;
	// Label intro;
	StyledText junit;   //?

	Group mainGroup;
	Vector<StepWidget> stepWidgets = new Vector<StepWidget>();
	int onStep = -1;

	public SequenceWidget(Composite parent, int style, Assignment assignment,
			Image selectionIcon) {
		mainGroup = new Group(parent, style);
		this.assignment = assignment;

		// swt fonts, smh
		FontData existingFD = parent.getFont().getFontData()[0];
		bold = new Font(parent.getDisplay(), new FontData(existingFD.getName(), existingFD.getHeight(), SWT.BOLD));
		italic = new Font(parent.getDisplay(), new FontData(existingFD.getName(), existingFD.getHeight(), SWT.ITALIC));
		parent.addDisposeListener(new DisposeListener() {
		    public void widgetDisposed(DisposeEvent e) {
		        bold.dispose();
		        italic.dispose();
		    }
		});
		
		/*
		 * Nate removed this ugly thing at top...
		 * 
		 * Group buttons = new Group(mainGroup, SWT.SHADOW_NONE);
		 * 
		 * back = new Button(buttons, SWT.ARROW | SWT.LEFT); currentStep = new
		 * Label(buttons, SWT.WRAP); next = new Button(buttons, SWT.ARROW |
		 * SWT.RIGHT);
		 * 
		 * RowLayout buttonsLO = new RowLayout(); buttonsLO.justify = true;
		 * buttons.setLayout(buttonsLO); back.addSelectionListener(this);
		 * next.addSelectionListener(this);
		 * 
		 * back.setEnabled(false);
		 * 
		 * Label introduction = new Label(mainGroup, SWT.WRAP); intro =
		 * introduction; introduction.setLayoutData(new RowData(150, 100));
		 */

		for (Step exercise : assignment.getExercises()) {

			Group stepGroup = new Group(mainGroup, SWT.SHADOW_NONE);

			GridLayout assignmentLayout = new GridLayout();
			assignmentLayout.numColumns = 4;
			assignmentLayout.marginHeight = 1;
			stepGroup.setLayout(assignmentLayout);

			GridData griddata;
			
			griddata = new GridData();
			griddata.widthHint = 18;
			Label selected = new Label(stepGroup, SWT.WRAP);
			selected.setImage(selectionIcon);
			selected.setVisible(false);
			selected.setLayoutData(griddata);
			
			griddata = new GridData();
			griddata.widthHint = 135;
			
			Label stepTitle = new Label(stepGroup, SWT.WRAP);
			stepTitle.setFont(bold);
			stepTitle.setText(exercise.getName());
			stepTitle.addMouseListener(this);
			stepTitle.setLayoutData(griddata);

			Button reset = null;
			griddata = new GridData();
			griddata.widthHint = 60;
			if (exercise.isCODE()) {
				reset = new Button(stepGroup, 0);
				reset.setText("Reset");
				reset.addSelectionListener(this);
				reset.setLayoutData(griddata);
				reset.setEnabled(false);
			}
			
			Button test = null;
			griddata = new GridData();
			griddata.widthHint = 70;
			if (exercise.hasTests()) {
				test = new Button(stepGroup, 0);
				test.setText(exercise.getLaunchButtonName());
				test.addSelectionListener(this);
				test.setLayoutData(griddata);
				test.setEnabled(false);
			}
			
			GridData infoGD = new GridData(SWT.BEGINNING, SWT.TOP, false, false, 4, 1);
			infoGD.exclude = true;
			Label stepInfo = new Label(stepGroup, SWT.WRAP);
			stepInfo.setFont(italic);
			stepInfo.setText(exercise.getIntro());
			//stepInfo.setVisible(false);
			stepInfo.setLayoutData(infoGD);

			StepWidget widge = new StepWidget(stepWidgets, selected, stepTitle, exercise, stepGroup, test,
					reset, stepInfo);
			widge.excludeGD = infoGD;
			stepWidgets.add(widge);
		}
		
		// intro.moveBelow(null);
		mainGroup.setLayout(setupLayout());
		//mainGroup.pack();
//		for (StepWidget widge : stepWidgets) {
//			widge.includeGD.widthHint = widge.info.getBounds().width;
//		}
//		mainGroup.pack();
//		for (StepWidget widge : stepWidgets) {
//			widge.deselect();
//		}

	}

	private Layout setupLayout() {
		RowLayout layout = new RowLayout();
		layout.wrap = true;
		layout.pack = true;
		layout.fill = true;
		layout.justify = false;
		layout.type = SWT.VERTICAL;
		layout.spacing = 2;
		return layout;
	}

	


	
	///////////////////
	// Button events
	// TODO move the selection listeners into the fricken step widgets, how about?
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		Object s = e.getSource();

		/*
		 * if (s == back) { if (onStep == 0) { gotoStep(null); } else {
		 * gotoStep(stepWidgets.get(onStep - 1)); } } else if (s == next) { if (onStep
		 * <= stepWidgets.size()) { gotoStep(stepWidgets.get(onStep + 1)); } } else {
		 */
		
		if (s instanceof Button) {
			Button b = (Button) s;
			StepWidget w = StepWidget.widgetFromTest(b, stepWidgets);
			// if you click the button, you go to that step now.
			// buttons in non-selected steps shouldn't be active not, so no
			gotoStep(w);
			if (w != null) {
				NavigatorActivator.getDefault().invokeTest(w.getExercise(),
						w.exercise.getLaunchConfig());
				String launchConfigName = w.exercise.getProjectName()
						+ w.exercise.getLaunchConfig();
				launch(launchConfigName);
			} else {
				w = StepWidget.widgetFromReset(b, stepWidgets);
				if (w != null) {
					// TODO do reset
				}
			}
		}
		// }
	}

	
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {

	}

	
	//////////// 
	//  Mouse events
	
	
	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	private Object MD;

	@Override
	public void mouseDown(MouseEvent e) {
		MD = e.getSource();
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (e.getSource() == MD)
			mouseClick(e);
	}

	private void mouseClick(MouseEvent e) {
		Object s = e.getSource();

		if (s instanceof Label) {
			StepWidget widge = StepWidget.widgetFromTitle((Label) s, stepWidgets);
			if (widge != null) {
				gotoStep(widge);
			}
		}
	}

	
	//////////////////////////////////////
	

	
	// main entrypoint for step changes
	public void gotoStep(StepWidget newWidget) {
		StepWidget oldWidget = null;
		Step oldStep = null;
		
		if (onStep != -1) {
			oldWidget = stepWidgets.get(onStep);
			oldStep = oldWidget.getExercise();
		}
		
		if (newWidget == null) {
			onStep = -1;
			//NavigatorActivator.getDefault().stepChanged(null, null);
		} else if (oldWidget != newWidget) {
			if (oldWidget != null) {
				oldWidget.deselect();
			}
			newWidget.select();
			onStep = stepWidgets.indexOf(newWidget);
			
			Step newStep = newWidget.getExercise();			
			// log the change -- oldStep might be null
			NavigatorActivator.getDefault().stepChanged(oldStep, newStep);
			try {
				openEditor(newStep);
			} catch (Exception boo) {
				NavigatorActivator.getDefault().log("openStepFail", newStep.getName() + ": " + boo.getMessage());
				boo.printStackTrace(System.err);
				return;
			}

			mainGroup.layout(true, true);
		}

	}
	
	

	
	
	
	private void openEditor(Step step) {
		// http://www.eclipse.org/forums/index.php/t/350942/
		if (step.openWithJavaEditor()) {
			IFile file = step.getSourceIFile();
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditor(page, file);
			} catch (PartInitException e) {

				e.printStackTrace();
			}

		} else if (step.openWithBrowser()) {
			URL url = null;
			try {
				if (step.sourceIsProjectLocal()) {
					// gotta find the location on disk
					URI base = assignment.getIsaFile().getProject()
							.getLocationURI();
					url = new URL(base.toString() + step.getSource());
				} else {
					// source is absolute -- already a url probably
					url = new URL(step.getSource());
				}
				IWebBrowser b = NavigatorActivator.getDefault().getBrowser();
				if (b != null) {
					
					b.openURL(url);
				}

			} catch (MalformedURLException e) {
				NavigatorActivator.getDefault().log("browserFail", "Malformed URL Exception, uh oh.  url: " + url + ", step name: " + step.getName());
				System.err.println("Whoops, bad url.  Couldn't make it from "
						+ step.getSource());
				e.printStackTrace();
			} catch (PartInitException e) {
				NavigatorActivator.getDefault().log("browserFail", "PartInitException, uh oh.  url: " + url + ", step name: " + step.getName());
				System.err.println("Couldn't open the URL from <source>"
						+ step.getSource());
				e.printStackTrace();
			}
		}

	}

	
	


	private void launch(String launchConfigName) {
		Path path = new Path(launchConfigName);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		String mode;
		mode = ILaunchManager.RUN_MODE;
		// mode = ILaunchManager.DEBUG_MODE;

		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration config = manager.getLaunchConfiguration(file);

		NavigatorActivator.setCurrentLaunchConfig(config);

		//
		DebugUITools.launch(config, mode);
	}

	
}
