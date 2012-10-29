package studentview.views;

//Andy Carle, Berkeley Institute of Design, UC Berkeley

import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import studentview.model.Step;
import studentview.model.Assignment;

public class SequenceWidget implements SelectionListener, MouseListener {

	Assignment segment;

	Label currentStep; 
	Button next, back;
	Label intro;
	StyledText junit;

	Group group;

	Vector<StepWidgets> steps = new Vector<StepWidgets>();

	int onStep = -1;


	public SequenceWidget(Composite parent, int style, Assignment segment) {
		group = new Group(parent, style);
		this.segment = segment;		
	}

	private void openStep(String filename) {
		//System.out.println("Trying to open filename: " + filename);
		Path path = new Path(filename);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			IDE.openEditor(page, file);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		Object s = e.getSource();

		if (s == back) {					
			if (onStep == 0) {								
				gotoStep(null);
			} else {
				gotoStep(steps.get(onStep - 1));
			}
		}else if(s == next) {			
			if (onStep <= steps.size()) {
				gotoStep(steps.get(onStep + 1));
			}
		} else {
			if (s instanceof Button) {
				Button b = (Button)s;
				StepWidgets w = StepWidgets.widgetFromTest(b, steps);
				if (w != null) {
					launch(w.exercise.getTestname());
				} else {
					w = StepWidgets.widgetFromReset(b, steps);
					if (w != null) {
						//do reset
					}
				}
			}	
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("mousedoubleclick");
	}

	private Object MD;

	@Override
	public void mouseDown(MouseEvent e) {
		MD = e.getSource();
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (e.getSource() == MD) mouseClick(e);		
	}

	private void mouseClick(MouseEvent e) {
		Object s = e.getSource();

		if (s instanceof Label) {
			StepWidgets widge = StepWidgets.widgetFromTitle((Label)s, steps); 
			if (widge != null) {
				gotoStep(widge);
			}			
		}
	}

	private void resetIntro() {
		currentStep.setText("Introduction");
		intro.setText(segment.getIntro());
		hideSelections();
	}

	private void setToStep(StepWidgets widget) {
		currentStep.setText(widget.title.getText());
		intro.setText(widget.exercise.getIntro());
		hideSelections();
		widget.selection.setVisible(true);
	}

	private void hideSelections() {
		for(StepWidgets s : steps) {
			s.selection.setVisible(false);
		}
	}

	private void launch(String launcherName) {
		Path path = new Path(launcherName);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration config = manager.getLaunchConfiguration(file);
		DebugUITools.launch(config, ILaunchManager.RUN_MODE);		
	}

	private void gotoStep(StepWidgets widget) {
		if (widget == null) {
			onStep = -1;
		} else {
			onStep = steps.indexOf(widget);
		}

		if (onStep > -1) {
			back.setEnabled(true);
		} else {
			back.setEnabled(false);
		}

		if (onStep + 1 < steps.size()) {
			next.setEnabled(true);
		} else {
			next.setEnabled(false);
		}
		if (widget == null) { //go back to the introduction
			resetIntro();
		} else { //open up a step
			Step ex = widget.exercise;
			try {
				openStep(ex.getFilename());
			} catch (Exception boo) {
				boo.printStackTrace(System.err);
				return;
			}
			setToStep(widget);
		}

	}

}