package studentview.views;

//Andy Carle, Berkeley Institute of Design, UC Berkeley

import java.util.Vector;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import studentview.model.Step;

public class StepWidgets {
	Label selection;
	Label title;
	Step exercise;
	Group group;
	//Label completion;
	Button test;
	Button reset;

	public StepWidgets(Label selection, Label title, Step exercise, Group group, Button test, Button reset){
		this.selection = selection;
		this.title = title;
		this.exercise = exercise;
		this.group = group;
		this.test = test;
		this.group = group;
	}
	
	public static StepWidgets widgetFromTitle(Label title, Vector<StepWidgets> widgets){
		for (StepWidgets w : widgets){
			if (w.title == title) return w;
		}
		return null;
	}
	
	public static StepWidgets widgetFromTest(Button test, Vector<StepWidgets> widgets){
		for (StepWidgets w : widgets){
			if (w.test == test) return w;
		}
		return null;
	}
	
	public static StepWidgets widgetFromReset(Button reset, Vector<StepWidgets> widgets){
		for (StepWidgets w : widgets){
			if (w.reset == reset) return w;
		}
		return null;
	}
	
}
