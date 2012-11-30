package studentview.controller;

import studentview.model.Step;

public interface NavigationListener {

	public void stepChanged(Step s);
	
	public void invokeTest(Class<?> c);

}
