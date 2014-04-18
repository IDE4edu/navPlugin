/* test activities
 * html-IDE.openeditor
 */

package navigatorView.views;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.berkeley.eduride.base_plugin.isafile.ISAUtil;
import edu.berkeley.eduride.base_plugin.model.Activity;
import edu.berkeley.eduride.base_plugin.util.Console;

public class ActivityChooser extends TitleAreaDialog {

	private ArrayList<Activity> activities = new ArrayList<Activity>();
	private Activity showActivity;
	Composite activityArea;

	public ActivityChooser(Shell parentShell) {
		super(parentShell);
		// Get all the activities currently loaded in student's Eclipse
		findActivitiesInWorkspace();
	}

	// TODO use EduRideBase.ISAUtil stuff!!  hashmap, etc
	private void findActivitiesInWorkspace() {
		activities.clear();
//		try {
//			ResourcesPlugin.getWorkspace().getRoot()
//					.accept(new IResourceVisitor() {
//						@Override
//						public boolean visit(IResource resource)
//								throws CoreException {
//							if (!(resource.getType() == IResource.FILE))
//								return true;
//							String extension = resource.getFileExtension();
//							if (extension != null) {
//								if (extension.equalsIgnoreCase("isa")) {
//									parseISA((IFile) resource);
//								}
//							}
//							return true;
//						}
//					});
//		} catch (CoreException e) {
//			Console.err(e);
//		}
		Collections.copy(activities, Activity.getActivities());
		// sort activities wrt sortOrder
		Collections.sort(activities);
	}

	// TODO this goes away, uses stuff in ISAUtil, activityStore, etc...
//	public void parseISA(IFile file) {
//		Activity s = ISAUtil.parseISA(file);
//		if (s != null) {
//			activities.add(s);
//		}
//	}
	
	
	@Override
	public void create() {
		super.create();
		setTitle("Choose activity (from your workspace):");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.marginBottom = 20;
		layout.marginRight = 20;
		layout.marginTop = 20;
		layout.marginLeft = 20;
		parent.setLayout(layout);

		ScrolledComposite activityScrolledArea = new ScrolledComposite(parent,
				SWT.V_SCROLL | SWT.BORDER_DASH);
		activityScrolledArea.setMinHeight(100);
		activityScrolledArea.setMinWidth(300);
		activityArea = new Composite(activityScrolledArea, SWT.NONE);
		activityScrolledArea.setContent(activityArea);
		GridLayout activityLayout = new GridLayout();
		layout.numColumns = 1;
		activityArea.setLayout(activityLayout);
		setActivityArea();

		return parent;
	}

	private void setActivityArea() {
		for (int i = 0; i < activities.size(); i++) {

			Button radio = new Button(activityArea, SWT.RADIO);
			radio.setText(activities.get(i).getName());
			radio.setToolTipText(activities.get(i).getIntro());
			radio.setEnabled(true);

			final int z = i;
			radio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					// show the Activity
					showActivity = activities.get(z);
				}
			});
		}
		activityArea
				.setSize(activityArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void refreshActivityArea() {
		// note, the arraylist<Activity> doesn't get updated here
		Control[] assControls = activityArea.getChildren();
		for (Control assControl : assControls) {
			assControl.dispose();
		}
		setActivityArea();
		activityArea.layout();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		// Create Okay button
		Button okButton = createButton(parent, OK, "Okay", false);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setReturnCode(OK);
				close();
			}
		});

		// Create Cancel button
		Button cancelButton = createButton(parent, CANCEL, "Cancel", true);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});

		Button importButton = new Button(parent, SWT.PUSH);
		importButton.setText("Import...");
		//
		importButton.setEnabled(false);
		//
		importButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Shell shell = new Shell();
				ImportActivity dialog = new ImportActivity(shell);
				dialog.create();
				if (dialog.open() == org.eclipse.jface.window.Window.OK) {
					findActivitiesInWorkspace();
					refreshActivityArea();
				}
			}
		});

		Button refreshButton = new Button(parent, SWT.PUSH);
		refreshButton.setText("Refresh");
		refreshButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				findActivitiesInWorkspace();
				refreshActivityArea();
			}
		});
	}

	@Override
	protected boolean isResizable() {
		return true;
	}


	public Activity getActivity() {
		return showActivity;
	}
}
