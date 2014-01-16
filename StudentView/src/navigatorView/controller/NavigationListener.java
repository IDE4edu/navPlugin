package navigatorView.controller;

import java.io.File;
import java.io.FileNotFoundException;

import edu.berkeley.eduride.base_plugin.model.Activity;
import edu.berkeley.eduride.base_plugin.model.Step;

public interface NavigationListener {

	public void stepChanged(Step oldstep, Step newstep);
	
	public void invokeTest(Step step, String launchConfig);
	
	public void openISA(Activity ass);
	
	public void log(String action, String message);
	
	// To log contents of a file
	public void log(String action, File txtfile) throws FileNotFoundException;

}
