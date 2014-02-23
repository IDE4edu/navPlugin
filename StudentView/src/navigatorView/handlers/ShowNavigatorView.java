package navigatorView.handlers;

import navigatorView.views.ActivityChooser;
import navigatorView.views.NavigatorView;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.*;

import edu.berkeley.eduride.base_plugin.model.Activity;
import edu.berkeley.eduride.base_plugin.util.Console;


public class ShowNavigatorView implements IHandler2 {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// nope.

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
			// Get the active window
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			if (window == null)
				return null;
			// Get the active page
			IWorkbenchPage page = window.getActivePage();
			if (page == null)
				return null;
			// Open and activate the Navigator view
			try {
				NavigatorView navview = (NavigatorView) page.showView(NavigatorView.ID);
				// do this conditionally, please
				//navview.chooseActivity();
			} catch (PartInitException e) {
				Console.err("Couldn't open the navigator view.");
				Console.err(e);
			}
			
			return null;
	}

	@Override
	public boolean isEnabled() {
		// when would we never not be enabled, hey?
		return true;
	}
	
	@Override
	public void setEnabled(Object evaluationContext) {
		// ignore this -- always enabled, hey
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}


}
