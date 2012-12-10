package studentview.views;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ImportAssignment extends TitleAreaDialog {

	public ImportAssignment(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		// Set the title
		setTitle("Choose Assignment to import");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginBottom = 20;
		layout.marginRight = 20;
		layout.marginTop = 20;
		layout.marginLeft = 20;
		parent.setLayout(layout);

		Text t = new Text(parent, SWT.BORDER);
		t.setLayoutData(new GridData(200, SWT.DEFAULT));

		parent.pack();

		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		// Create Okay button
		Button okButton = 
				createButton(parent, OK, "Okay", false);
		// Add a SelectionListener
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(OK);
				// Do import.
				close();
			}
		});

		// Create Cancel button
		Button cancelButton = 
				createButton(parent, CANCEL, "Cancel", false);
		// Add a SelectionListener
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});

		parent.pack();
	}


	@Override
	protected boolean isResizable() {
		return false;
	}

} 