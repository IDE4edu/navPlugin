package navigatorView.controller;

import java.io.File;

import navigatorView.model.Assignment;
import navigatorView.model.Step;

public interface NavigationListener {

	public void stepChanged(Step oldstep, Step newstep);
	
	public void invokeTest(Step step, String launchConfig);
	
	public void openISA(Assignment ass);

}
