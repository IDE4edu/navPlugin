package navigatorView.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import navigatorView.NavigatorActivator;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.datatransfer.ExternalProjectImportWizard;
import org.json.JSONArray;

import edu.berkeley.eduride.base_plugin.EduRideBase;
import edu.berkeley.eduride.base_plugin.model.ActivityImport;
import edu.berkeley.eduride.base_plugin.util.Console;

public class ImportActivity extends TitleAreaDialog {

	ArrayList<ActivityImport> imports = new ArrayList<ActivityImport>();
	
	public ImportActivity(Shell parentShell) {
		super(parentShell);
		
		String domain = EduRideBase.getDomain();
		
		// TODO let the user select at some point, eh...
		getImportList(EduRideBase.getUsernameNoLogin());   
	}
	

	@Override
	public void create() {
		super.create();
		// Set the title
		setTitle("Choose Activities to import");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginBottom = 20;
		layout.marginRight = 20;
		layout.marginTop = 20;
		layout.marginLeft = 20;
		parent.setLayout(layout);

		for (int i = 0; i < imports.size(); i++) {

			Text t = new Text(parent, SWT.LEFT);
			t.setText(imports.get(i).getName());

		}

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

				// Do import.
				
				for (ActivityImport imp : imports) {
					imp.importMe();
				}
				
				File root = ActivityImport.getImportRoot();
				StructuredSelection currentSelection = new StructuredSelection();

				IWorkbenchWizard wizard = new ExternalProjectImportWizard(root.getPath());

				IWorkbench workbench = PlatformUI.getWorkbench();
				wizard.init(workbench, currentSelection);
				Shell shell = new Shell();
				WizardDialog wd = new WizardDialog(shell, wizard);
				wd.open();
				
				// TODO delete files, etc.
				ActivityImport.emptyImportRoot();
				
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

		parent.pack();
	}


	@Override
	protected boolean isResizable() {
		return false;
	}

	
	///////////////////////
	
	private static final String IMPORT_TARGET = "/assignment/list/";
	
	private void getImportList(String username) {
		String params = "username=" + username;
		String filetarget = IMPORT_TARGET + "?" + params;
		String jsonString = null;
		try {

			URL target = new URL("http", EduRideBase.getDomain(), filetarget);
			HttpURLConnection connection = (HttpURLConnection) target.openConnection();           
			connection.setDoOutput(false);
			connection.setDoInput(true);
			connection.setRequestMethod("GET"); 
			
	        connection.setRequestProperty("Content-length", "0");
	        connection.setUseCaches(false);
	        connection.setAllowUserInteraction(false);

	        connection.connect();
	        int status = connection.getResponseCode();

	        switch (status) {
	            case 200:
	            case 201:
	                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                StringBuilder sb = new StringBuilder();
	                String line;
	                while ((line = br.readLine()) != null) {
	                    sb.append(line+"\n");
	                }
	                br.close();
	                jsonString = sb.toString();
	        }

			
			connection.disconnect();
		} catch (MalformedURLException e) {
			Console.err(e);
		} catch (IOException e) {
			Console.err(e);
		}
		if (jsonString != null) {
			JSONArray jsonarr = new JSONArray(jsonString);
			for (int i=0; i<jsonarr.length(); i++) {
				JSONArray jsonAss = jsonarr.getJSONArray(i);
				String name = jsonAss.getString(0);
				String urlstring = jsonAss.getString(1);
				String datestring = jsonAss.getString(2);
				ActivityImport ass = null;
				try {
					ass = new ActivityImport(name, new URL(urlstring), new Date());
				} catch (MalformedURLException e) {
					NavigatorActivator.getDefault().log("importActivityFail", "bad url from server: " + urlstring);
				}
				NavigatorActivator.getDefault().log("importActivity", "got activity: " + name + ", from: " + urlstring);
				imports.add(ass);
			}
		}
	}
	
	
} 