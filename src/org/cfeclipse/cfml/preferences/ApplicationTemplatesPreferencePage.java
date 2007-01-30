package org.cfeclipse.cfml.preferences;


import org.cfeclipse.cfml.CFMLPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
//import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PathEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ApplicationTemplatesPreferencePage extends
		FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public ApplicationTemplatesPreferencePage() {
		super(GRID);
		setPreferenceStore(CFMLPlugin.getDefault().getPreferenceStore());
	}
	
	protected Control createContents(Composite parent) {
		Composite appearanceComposite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		
		// TODO Auto-generated method stub
		return createContents(appearanceComposite);
	}
	
	protected void createFieldEditors() {
	
		//create a list of projects
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		IProject[] projectList = ws.getRoot().getProjects();
		
		String[][] projects = new String[projectList.length + 1][2];
		
		String[] project = new String[2];
		project[0] = "<none>";
		project[1] = "";
		
		//projects[0] = project;
		
		for (int i = 0; i < projectList.length; i++) {
				project = new String[2];
				project[0] = projectList[i].getName();
				project[1] = projectList[i].getName();
			projects[i + 1] = project;
		}
		
	//	System.out.println(projects);
	
	//	CCombo combo = new CCombo(getFieldEditorParent(),SWT.BORDER|SWT.SINGLE|SWT.READ_ONLY);
		
		//addField(combo);
	//	addField(new ComboFieldEditor(ApplicationTemplatesPreferenceConstants.P_APPLICATION_TEMPLATE_PROJECT,
	//			"Project", projects, getFieldEditorParent()));
		
		setTitle("Application Templates");
		setDescription("Allows you to choose which project stores your application templates");
		//setMessage("To use Application Templates, you need to select the project that contains a folder for each application");
	}
	

	
	public void init(IWorkbench workbench) {
		
	}
	

	

}
