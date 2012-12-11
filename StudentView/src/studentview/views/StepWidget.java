package studentview.views;

//Andy Carle, Berkeley Institute of Design, UC Berkeley

import java.util.Vector;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import studentview.model.Step;

public class StepWidget {
	Label info;
	Label selection;
	Label title;
	Step exercise;
	Group group;
	//Label completion;
	Button test;
	Button reset;

	public StepWidget(Label selection, Label title, Step exercise, Group group, Button test, Button reset, Label info){
		this.info = info;
		this.selection = selection;
		this.title = title;
		this.exercise = exercise;
		this.group = group;
		this.test = test;
		this.group = group;
	}

	public static StepWidget widgetFromTitle(Label title, Vector<StepWidget> widgets){
		for (StepWidget w : widgets){
			if (w.title == title) return w;
		}
		return null;
	}

	public static StepWidget widgetFromTest(Button test, Vector<StepWidget> widgets){
		for (StepWidget w : widgets){
			if (w.test == test) return w;
		}
		return null;
	}

	public static StepWidget widgetFromReset(Button reset, Vector<StepWidget> widgets){
		for (StepWidget w : widgets){
			if (w.reset == reset) return w;
		}
		return null;
	}
	
	
	public Step getExercise() {
		return exercise;
	}	

}

