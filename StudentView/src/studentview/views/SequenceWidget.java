package studentview.views;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

import studentview.NavigatorActivator;
import studentview.model.Assignment;
import studentview.model.Step;
import studentview.model.Step.ExerciseType;

public class SequenceWidget implements SelectionListener, MouseListener {

	Assignment segment;

//   NATE removed this ugly thing	
//	Label currentStep;
//	Button next, back;
//	Label intro;
	StyledText junit;

	Group group;
	
	Vector<StepWidgets> steps = new Vector<StepWidgets>();

	int onStep = -1;

	public SequenceWidget(Composite parent, int style, Assignment seg,
			Image selection) {
		group = new Group(parent, style);
		this.segment = seg;

		/*	Nate removed this ugly thing at top...
		 * 	
		Group buttons = new Group(group, SWT.SHADOW_NONE);

		back = new Button(buttons, SWT.ARROW | SWT.LEFT);
		currentStep = new Label(buttons, SWT.WRAP);
		next = new Button(buttons, SWT.ARROW | SWT.RIGHT);

		RowLayout buttonsLO = new RowLayout();
		buttonsLO.justify = true;
		buttons.setLayout(buttonsLO);
		back.addSelectionListener(this);
		next.addSelectionListener(this);

		back.setEnabled(false);

		Label introduction = new Label(group, SWT.WRAP);
		intro = introduction;
		introduction.setLayoutData(new RowData(150, 100));
		*/

		for (Step e : seg.getExercises()) {
			GridData g = new GridData();
			Group stepline = new Group(group, SWT.SHADOW_NONE);

			GridLayout layout = new GridLayout();
			layout.numColumns = 4;
			layout.marginHeight = 1;
			stepline.setLayout(layout);

			g.widthHint = 18;
			Label sel = new Label(stepline, SWT.WRAP);
			sel.setImage(selection);
			sel.setVisible(false);
			sel.setLayoutData(g);
			g = new GridData();
			g.widthHint = 150;
			Label step = new Label(stepline, SWT.WRAP);
			step.setText(e.getName());
			step.addMouseListener(this);
			step.setLayoutData(g);

			Button test = null;
			Button reset = null;
			g = new GridData();
			g = new GridData();
			g.widthHint = 100;
			if (e.isCODE()) {
				reset = new Button(stepline, 0);
				reset.setText("Reset");
				reset.addSelectionListener(this);
				reset.setLayoutData(g);
			}
			g.widthHint = 75;
			if (e.hasTests()) {
				test = new Button(stepline, 0);
				test.setText(e.getLaunchButtonName());
				test.addSelectionListener(this);
				test.setLayoutData(g);
			}

			GridData d = new GridData(0, 0, true, false, 4, 0);
			d.exclude = true;
			Label get = new Label(stepline, SWT.WRAP);
			get.setText(e.getIntro());
			get.setVisible(false);
			get.setLayoutData(d);

			StepWidgets widge = new StepWidgets(sel, step, e, stepline, test,
					reset, get);
			steps.add(widge);
		}
		//intro.moveBelow(null);
		group.setLayout(setupLayout());
	}

	private Layout setupLayout() {
		RowLayout layout = new RowLayout();
		layout.wrap = true;
		layout.pack = true;
		layout.fill = true;
		layout.justify = false;
		layout.type = SWT.VERTICAL;
		layout.spacing = 3;
		return layout;
	}

	private void openStep(Step step) {
		String filename = step.getFullSourcePath();
		Path path = new Path(filename);
		
		String localFilename = step.getWithinProjectSourcePath();

			//http://www.eclipse.org/forums/index.php/t/350942/
			if (step.openWithJavaEditor()) {
				IFile file = ResourcesPlugin.getWorkspace().getRoot()
						.getFile(path);
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else if (step.openWithBrowser()){

				URI base = segment.getIsaFile().getProject().getLocationURI();
				try {
					URL url = new URL(base.toString() + localFilename);
					NavigatorActivator.getDefault().getBrowser().openURL(url);
				} catch (MalformedURLException e) {
					// TODO URL didn't get made right
					e.printStackTrace();
				} catch (PartInitException e) {
					// TODO openURL didn't work
					e.printStackTrace();
				}
			}

	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		Object s = e.getSource();

/*		if (s == back) {
			if (onStep == 0) {
				gotoStep(null);
			} else {
				gotoStep(steps.get(onStep - 1));
			}
		} else if (s == next) {
			if (onStep <= steps.size()) {
				gotoStep(steps.get(onStep + 1));
			}
		} else {*/
			if (s instanceof Button) {
				Button b = (Button) s;
				StepWidgets w = StepWidgets.widgetFromTest(b, steps);
				// if you click the button, you go to that step now.
				gotoStep(w);
				if (w != null) {
					launch(w.exercise.getLaunchConfig());
					//NavigatorActivator.getDefault().invokeTest(w.exercise.getTestClass());
				} else {
					w = StepWidgets.widgetFromReset(b, steps);
					if (w != null) {
						// do reset
					}
				}
			}
		//}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// TODO Auto-generated method stub
		// System.out.println("mousedoubleclick");
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
			StepWidgets widge = StepWidgets.widgetFromTitle((Label) s, steps);
			if (widge != null) {
				gotoStep(widge);
			}
		}
	}

	private void resetIntro() {
		//currentStep.setText("Introduction");
		//intro.setText(segment.getIntro());
		hideSelections();
	}

	private void setToStep(StepWidgets widget) {
		GridData d = new GridData(0, 0, true, false, 4, 1);
		d.exclude = false;
		//currentStep.setText(widget.title.getText());
		// intro.setText(widget.exercise.getIntro());
		hideSelections();
		hideInfo();
		widget.selection.setVisible(true);
		widget.info.setVisible(true);
		widget.info.setLayoutData(d);
		group.layout();
	}

	private void hideSelections() {
		for (StepWidgets s : steps) {
			s.selection.setVisible(false);
			GridData d = new GridData(0, 0, true, false, 4, 1);
			d.exclude = true;
			s.info.setVisible(true);
			s.info.setLayoutData(d);
		}
		//intro.setLayoutData(new RowData(150, 0));
		group.layout();
	}

	private void hideInfo() {
		for (StepWidgets s : steps) {
			s.info.setVisible(false);
		}
	}


	private void launch(String launcherName) {
		Path path = new Path(launcherName);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		String mode;
		mode = ILaunchManager.RUN_MODE;
		//mode = ILaunchManager.DEBUG_MODE;

		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration config = manager.getLaunchConfiguration(file);
		// 
		DebugUITools.launch(config, mode);
	}

	private void gotoStep(StepWidgets widget) {
		if (widget == null) {
			onStep = -1;
		} else {
			onStep = steps.indexOf(widget);
			NavigatorActivator.getDefault().stepChanged(widget.getExercise());
		}

//		if (onStep > -1) {
//			back.setEnabled(true);
//		} else {
//			back.setEnabled(false);
//		}
//
//		if (onStep + 1 < steps.size()) {
//			next.setEnabled(true);
//		} else {
//			next.setEnabled(false);
//		}
		if (widget == null) { // go back to the introduction
			resetIntro();
		} else { // open up a step
			Step ex = widget.exercise;
			try {
				openStep(ex);
			} catch (Exception boo) {
				boo.printStackTrace(System.err);
				return;
			}
			setToStep(widget);
		}

	}

}
