package navigatorView.controller;

import navigatorView.model.Step;

public interface NavigationListener {

	public void stepChanged(Step s);
	
	public void invokeTest(Class<?> c);

}
