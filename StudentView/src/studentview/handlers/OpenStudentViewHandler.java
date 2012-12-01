package studentview.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.*;
import studentview.model.Assignment;

import studentview.views.AssignmentChooser;
import studentview.views.UCWISENav;

public class OpenStudentViewHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

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
			// Open and activate the Favorites view
			try {
				page.showView(UCWISENav.ID);
			} catch (PartInitException e) {
				//FavoritesLog.logError("Failed to open the Favorites view", e);
			}
			return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
