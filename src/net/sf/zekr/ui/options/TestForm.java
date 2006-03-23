package net.sf.zekr.ui.options;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class TestForm {
	private Device display;

	Shell parent, shell;
	Composite body;
	GridLayout gl;
	GridData gd;
	Composite nav, det;

	ToolItem general, view;

	Composite detGroup;
	Composite navGroup;

	Composite generalTab, viewTab;

	private boolean tableChanged;

	private Button showSplash;

	public TestForm(Shell parent) {
		this.parent = parent;
		display = parent.getDisplay();
		shell = new Shell(parent, SWT.SHELL_TRIM);
		shell.setLayout(new FillLayout());
		shell.setText("Options");
		makeForm();
	}

	private void makeForm() {
		body = new Composite(shell, SWT.NONE);
		gl = new GridLayout(2, false);
		body.setLayout(gl);

		nav = new Composite(body, SWT.NONE);
		det = new Composite(body, SWT.NONE);

		gd = new GridData(GridData.FILL_VERTICAL);
		nav.setLayoutData(gd);
		nav.setLayout(new FillLayout());

		gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 400;
		gd.heightHint = 300;

		det.setLayoutData(gd);
		det.setLayout(new FillLayout());

		navGroup = new Group(nav, SWT.NONE);
		navGroup.setLayout(new RowLayout());

		SelectionAdapter sa = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ToolItem ti = (ToolItem) e.widget;
				Composite comp = (Composite) ti.getData();
				((GridData) comp.getLayoutData()).exclude = !ti.getSelection();
				comp.setVisible(ti.getSelection());
				// using shell.redraw(), shell.update(), or other refreshing techniques does not make any sense
				// only refresh when you resize manually!
			}
		};
		detGroup = new Group(det, SWT.MULTI);
		detGroup.setLayout(new GridLayout(2, false));

		ToolBar bar = new ToolBar(navGroup, SWT.VERTICAL | SWT.FLAT);

		createGeneralTab();
		general = new ToolItem(bar, SWT.RADIO);
		general.setText("General");
		general.setSelection(true);
		general.setData(generalTab);
		general.addSelectionListener(sa);

		createViewTab();
		view = new ToolItem(bar, SWT.RADIO);
		view.setText("View");
		view.setData(viewTab);
		view.addSelectionListener(sa);

		((GridData) generalTab.getLayoutData()).exclude = false;

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		Label sep = new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep.setLayoutData(gd);

		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.END;
		Composite buttons = new Composite(body, SWT.NONE);
		RowLayout rl = new RowLayout(SWT.HORIZONTAL);
		buttons.setLayout(rl);
		buttons.setLayoutData(gd);
		RowData rd = new RowData();
		rd.width = 70;

		Button ok = new Button(buttons, SWT.NONE);
		ok.setText("&OK");
		ok.setLayoutData(rd);

		Button cancel = new Button(buttons, SWT.NONE);
		cancel.setText("&Cancel");
		cancel.setLayoutData(rd);

		Button apply = new Button(buttons, SWT.NONE);
		apply.setText("&Apply");
		apply.setLayoutData(rd);

		shell.setDefaultButton(ok);
	}

	private void createGeneralTab() {
		GridData gd = new GridData();
		gd.exclude = true;
		generalTab = new Composite(detGroup, SWT.NONE);
		generalTab.setLayout(new RowLayout(SWT.VERTICAL));
		generalTab.setLayoutData(gd);
		showSplash = new Button(generalTab, SWT.CHECK);
		showSplash.setText("A test chech box");
	}

	private void createViewTab() {
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.exclude = true;
		viewTab = new Composite(detGroup, SWT.NONE);
		viewTab.setLayout(new GridLayout(2, false));
		viewTab.setLayoutData(gd);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		Text t = new Text(viewTab, SWT.MULTI);
		t.setLayoutData(gd);

		Button add = new Button(viewTab, SWT.PUSH);
		add.setText("Add...");
		add.setSize(100, 30);

		Button del = new Button(viewTab, SWT.PUSH);
		del.setText("Delete...");
		del.setSize(100, 30);
	}

	public void open() {
		shell.pack();
		shell.open();
	}

	public void loop() {
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}
	}

	public static void main(String[] args) {
		Display d = new Display();
		Shell s = new Shell(d, SWT.SHELL_TRIM);
		TestForm tf = new TestForm(s);
		tf.open();
		tf.loop();
	}
}
