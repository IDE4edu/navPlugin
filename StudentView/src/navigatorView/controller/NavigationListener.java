package navigatorView.controller;

import java.io.File;
import java.io.FileNotFoundException;

import navigatorView.model.Assignment;
import navigatorView.model.Step;

public interface NavigationListener {

	public void stepChanged(Step oldstep, Step newstep);
	
	public void invokeTest(Step step, String launchConfig);
	
	public void openISA(Assignment ass);
	
	public void log(String action, String message);
	
	// To log contents of a file
	public void log(String action, File txtfile) throws FileNotFoundException;

}
