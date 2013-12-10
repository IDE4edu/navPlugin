package navigatorView.views;

//Andy Carle, Berkeley Institute of Design, UC Berkeley

import java.util.ArrayList;
import java.util.Vector;

import navigatorView.model.Step;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;


public class StepWidget {
	ArrayList<StepWidget> stepWidgets;
	Label info;
	GridData includeGD;
	GridData excludeGD;
	Label selection;
	Label title;
	Step step;
	Group group;
	//Label completion;
	Button test;
	Button reset;


	public StepWidget(ArrayList<StepWidget> stepWidgets, Label selection, Label title, Step step, Group group, Button test, Button reset, Label info){
		this.stepWidgets = stepWidgets;
		this.info = info;
		this.selection = selection;
		this.title = title;
		this.step = step;
		this.group = group;
		this.test = test;
		this.reset = reset;
		this.group = group;

		includeGD = new GridData(SWT.BEGINNING, SWT.TOP, false, false, 4, 1);
		//excludeGD.exclude = true;


	}

	
	public Step getStep() {
		return step;
	}	
	
	
	
	public void select() {
		if (test != null) {
			test.setEnabled(true);
		}
		if (reset != null) {
			// TODO -- make reset work, then enable it here
			//reset.setEnabled(true);
		}
		if (info.getText() != "") {
			info.setVisible(true);
			//info.setLayoutData(includeGD);
		}
		selection.setVisible(true);
	}
	
	
	public void deselect() {
		if (test != null) {
			test.setEnabled(false);
		}
		if (reset != null) {
			reset.setEnabled(false);
		}

		info.setVisible(false);
		info.setLayoutData(excludeGD);
		selection.setVisible(false);
		//selection.setLayoutData(d);
	}
	

	
	////////////////////////
	
	
	public static StepWidget widgetFromTitle(Label title, ArrayList<StepWidget> widgets){
		for (StepWidget w : widgets){
			if (w.title == title) return w;
		}
		return null;
	}

	public static StepWidget widgetFromTest(Button test, ArrayList<StepWidget> widgets){
		for (StepWidget w : widgets){
			if (w.test == test) return w;
		}
		return null;
	}

	public static StepWidget widgetFromReset(Button reset, ArrayList<StepWidget> widgets){
		for (StepWidget w : widgets){
			if (w.reset == reset) return w;
		}
		return null;
	}
	


}

