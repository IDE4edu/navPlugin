package studentview.views;

import org.eclipse.jface.dialogs.IMessageProvider;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AssignmentChooser extends TitleAreaDialog {

  private ArrayList<String> lessons;

  public AssignmentChooser(Shell parentShell, ArrayList<String> lesson) {
    super(parentShell);
    this.lessons = lesson;
  }
  
  @Override
  public void create() {
    super.create();
    // Set the title
    setTitle("Choose assignment");
    // Set the message
    setMessage("Which assignment would you like to view?", 
        IMessageProvider.INFORMATION);
	
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    layout.marginBottom = 20;
    layout.marginRight = 20;
    layout.marginTop = 20;
    layout.marginLeft = 20;
    // layout.horizontalAlignment = GridData.FILL;
    parent.setLayout(layout);

    // The text fields will grow with the size of the dialog
    //GridData gridData = new GridData();
    //gridData.grabExcessHorizontalSpace = true;
    //gridData.horizontalAlignment = GridData.FILL;
    
    for (int i = 0; i < lessons.size(); i++) {
    	Button radio = new Button(parent, SWT.RADIO);
    	radio.setText(lessons.get(i));
    	radio.setEnabled(true);
    }
    
    return parent;
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    // Create Add button
    // Own method as we need to overview the SelectionAdapter
    Button okButton = createOkButton(parent, OK, "Done", true);
    // Add a SelectionListener
    okButton.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
            setReturnCode(OK);
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
  }

  protected Button createOkButton(Composite parent, int id, 
      String label,
      boolean defaultButton) {
    // increment the number of columns in the button bar
    ((GridLayout) parent.getLayout()).numColumns++;
    Button button = new Button(parent, SWT.PUSH);
    button.setText(label);
    button.setFont(JFaceResources.getDialogFont());
    button.setData(new Integer(id));
    button.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
      }
    });
    if (defaultButton) {
      Shell shell = parent.getShell();
      if (shell != null) {
        shell.setDefaultButton(button);
      }
    }
    setButtonLayoutData(button);
    return button;
  }
  
  @Override
  protected boolean isResizable() {
    return true;
  }
  
  public void addToList(String s) {
	  lessons.add(s);
  }
  
  public ArrayList<String> getLessons() {
	  return lessons;
  }
  
} 