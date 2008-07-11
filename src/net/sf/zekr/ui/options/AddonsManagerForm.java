//IN THE NAME OF ALLAH

package net.sf.zekr.ui.options;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;


/**
* @author usien
*/
public class AddonsManagerForm extends org.eclipse.swt.widgets.Composite {
	private TabItem tabItem2;
	private List list3;
	private List list2;
	private List list1;
	private Button button4;
	private Button button3;
	private Button button2;
	private Composite composite2;
	private Composite composite1;
	private Button button1;
	private TabItem tabItem3;
	private TabItem tabItem1;
	private TabFolder tabFolder2;

	public AddonsManagerForm(Composite parent, int style) {
		super(parent, style);
		initGUI();
	}
	
	/**
	* Initializes the GUI.
	*/
	private void initGUI() {
		try {
			this.setSize(386, 284);
			GridLayout thisLayout = new GridLayout();
			thisLayout.makeColumnsEqualWidth = true;
			this.setLayout(thisLayout);
			{
				composite1 = new Composite(this, SWT.NONE);
				GridLayout composite1Layout = new GridLayout();
				composite1Layout.numColumns = 2;
				GridData composite1LData = new GridData();
				composite1LData.widthHint = 378;
				composite1LData.heightHint = 241;
				composite1.setLayoutData(composite1LData);
				composite1.setLayout(composite1Layout);
				{
					tabFolder2 = new TabFolder(composite1, SWT.NONE);
					{
						tabItem2 = new TabItem(tabFolder2, SWT.NONE);
						tabItem2.setText("Translations");
						{
							list1 = new List(tabFolder2, SWT.NONE);
							tabItem2.setControl(list1);
						}
					}
					{
						tabItem1 = new TabItem(tabFolder2, SWT.NONE);
						tabItem1.setText("Themes");
						{
							list2 = new List(tabFolder2, SWT.NONE);
							tabItem1.setControl(list2);
						}
					}
					{
						tabItem3 = new TabItem(tabFolder2, SWT.NONE);
						tabItem3.setText("Recitations");
						{
							list3 = new List(tabFolder2, SWT.NONE);
							tabItem3.setControl(list3);
						}
					}
					GridData tabFolder2LData = new GridData();
					tabFolder2LData.widthHint = 292;
					tabFolder2LData.heightHint = 201;
					tabFolder2.setLayoutData(tabFolder2LData);
					tabFolder2.setSelection(0);
				}
				{
					composite2 = new Composite(composite1, SWT.NONE);
					GridLayout composite2Layout = new GridLayout();
					composite2Layout.makeColumnsEqualWidth = true;
					composite2.setLayout(composite2Layout);
					{
						button2 = new Button(composite2, SWT.PUSH | SWT.CENTER);
						GridData button2LData = new GridData();
						button2LData.widthHint = 60;
						button2LData.heightHint = 27;
						button2.setLayoutData(button2LData);
						button2.setText("Add");
					}
					{
						button3 = new Button(composite2, SWT.PUSH | SWT.CENTER);
						GridData button3LData = new GridData();
						button3LData.widthHint = 60;
						button3LData.heightHint = 27;
						button3.setLayoutData(button3LData);
						button3.setText("Apply");
					}
					{
						button4 = new Button(composite2, SWT.PUSH | SWT.CENTER);
						button4.setText("Remove");
					}
				}
			}
			{
				button1 = new Button(this, SWT.PUSH | SWT.CENTER);
				GridData button1LData = new GridData();
				button1LData.horizontalAlignment = GridData.CENTER;
				button1.setLayoutData(button1LData);
				button1.setText("Close");
			}
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		AddonsManagerForm inst = new AddonsManagerForm(shell, SWT.NULL);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.setText("Add-ons Manager");
		shell.layout();
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
