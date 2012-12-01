/* test assignments
 * html-IDE.openeditor
 */

package studentview.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import java.util.ArrayList;

import org.eclipse.swt.SWT;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import studentview.model.Assignment;

public class AssignmentChooser extends TitleAreaDialog {

	private ArrayList<Assignment> assignments;
	private Assignment showAssignment;

	public AssignmentChooser(Shell parentShell) {
		super(parentShell);
		assignments = new ArrayList<Assignment>();
		// Get all the assignments currently loaded in student's Eclipse
		try {
			ResourcesPlugin.getWorkspace().getRoot().accept(new IResourceVisitor() {			
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if (!(resource.getType() == IResource.FILE)) return true;
					String extension = resource.getFileExtension();
					if (extension != null) {
						if (extension.equalsIgnoreCase("isa")) {
							parseISA((IFile)resource);
						}
					}
					return true;
				}
			});
		} catch (CoreException e1) {
			System.err.println("Core Exception!!!");
			e1.printStackTrace();
		}
	}

	@Override
	public void create() {
		super.create();
		setTitle("Choose assignment to work on");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginBottom = 20;
		layout.marginRight = 20;
		layout.marginTop = 20;
		layout.marginLeft = 20;
		parent.setLayout(layout);

		for (int i = 0; i < assignments.size(); i++) {

			Button radio = new Button(parent, SWT.RADIO);
			radio.setText(assignments.get(i).getName());
			radio.setToolTipText(assignments.get(i).getIntro());
			radio.setEnabled(true);

			final int z = i;
			radio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					//show the Assignment
					showAssignment = assignments.get(z);
				}
			});
		}
		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		/*
		Button importButton = new Button(parent, SWT.PUSH);
		importButton.setText("Import assignment");
		importButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Shell shell = new Shell();
				ImportAssignment dialog = new ImportAssignment(shell);
				dialog.create();
				dialog.open();
			}
		});
		*/
		Button okButton = createButton(parent, OK, "Okay", false);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setReturnCode(OK);
				close();
			}
		});
		// Create Cancel button
		Button cancelButton = createButton(parent, CANCEL, "Cancel", false);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	public void parseISA(IFile file) {
		Assignment s = Assignment.parseISA(file);
		if (s == null) System.err.println("Failed to parse file: " + file.getName());
		s.getIntro();
		assignments.add(s);
	}

	public Assignment getSegment() {
		return showAssignment;
	}
} 

