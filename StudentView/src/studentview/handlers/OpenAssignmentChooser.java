package studentview.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.*;
import studentview.model.Assignment;

import studentview.views.AssignmentChooser;
import studentview.views.UCWISENav;

public class OpenAssignmentChooser implements IHandler2 {

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
			IWorkbenchWindow window = HandlerUtil
			.getActiveWorkbenchWindowChecked(event);
			if (window == null)
				return null;
			// Get the active page
			IWorkbenchPage page = window.getActivePage();
			if (page == null)
				return null;
			UCWISENav navview;
			// Open and activate the Navigator view
			try {
				navview = (UCWISENav) page.showView(UCWISENav.ID);
				navview.chooseAssignment();
			} catch (PartInitException e) {
				System.err.println("Couldn't open the navigator view, bummer.");;
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
