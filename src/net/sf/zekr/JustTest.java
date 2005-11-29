/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 23, 2004
 */

package net.sf.zekr;

import java.io.IOException;

import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.resource.QuranTextProperties;
import net.sf.zekr.common.runtime.InitRuntime;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.error.ErrorForm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @see TODO
 * @version 0.1
 */
public class JustTest {
	public static void main(String[] args) throws IOException {
		/*
		 * RandomAccessFile file = new RandomAccessFile("sample-page1.html", "r");
		 * byte[] b = new byte[(int) file.length()]; file.readFully(b); String str =
		 * new String(b); String text = "kj fdkslf fdlkj gfslkj gdslkj }0{ dskjh
		 * fdjh fdksjh sfd}1{"; AyaTransformer transformer = new
		 * AyaTransformer(str, text, "}");
		 * System.out.println(transformer.transformAll()); // Snippet107();
		 */

//		quranTextTest();
//		loggerTest();
//		templateTest();
//		initRuntime();
		testErrorForm();
	}
	
	public static void testErrorForm() {
		Display display = new Display();
		ErrorForm ef = new ErrorForm(display, new Exception("salam"));
		ef.show();
	}
	
	public static void templateTest() {
//		TemplateUtilities tu = new TemplateUtilities();
	}
	
	public static void initRuntime() throws IOException {
		InitRuntime runtime = new InitRuntime();
		runtime.configureDirectories();
	}
	
	public static void loggerTest() {
		Logger logger = Logger.getLogger();
		logger.warn("Hello just warning!");
		logger.fatal("Hello fatal!");
		logger.fatal("Hello just warning!");
		logger.fatal("Hello just warning!");
		logger.debug("Salaam");
	}

	public static void quranTextTest() throws IOException {
		QuranText text = QuranText.getInstance();
		System.out.println(text.get(2, 3));
		System.out.println();
		System.out.println(text.get(12, 3));
		System.out.println();
		System.out.println(text.get(32, 3));
		System.out.println();
		System.out.println(text.get(42, 3));
		System.out.println("[220]".matches("\\[\\d+]"));
	}

	public static void quranTextPropertiesTest() {
		QuranTextProperties properties = QuranTextProperties.getInstance();
		System.out.println(properties.getAyaDelimiter());
		System.out.println(properties.getSooraStartSign());
		System.out.println(properties.getSooraNumberLeftString());

	}

	public static void tableTest() {
		Display display = new Display();
		Shell shell = new Shell(display);
		Table table = new Table(shell, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		String[] titles = { " ", "C", "!", "Description", "Resource", "In Folder", "Location" };
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, 0);
			column.setText(titles[i]);
		}
		int count = 128;
		for (int i = 0; i < count; i++) {
			TableItem item = new TableItem(table, SWT.NULL);
			item.setText(0, "x");
			item.setText(1, "y");
			item.setText(2, "!");
			item.setText(3, "this stuff behaves the way I expect");
			item.setText(4, "almost everywhere");
			item.setText(5, "some.folder");
			item.setText(6, "line " + i + " in nowhere");
		}
		for (int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}
		table.setSize(table.computeSize(SWT.DEFAULT, 300));
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public static void Snippet107() {
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		SashForm form = new SashForm(shell, SWT.HORIZONTAL);
		form.setLayout(new FillLayout());

		Composite child1 = new Composite(form, SWT.NONE);
		child1.setLayout(new FillLayout());
		new Label(child1, SWT.NONE).setText("Label in pane 1");
		child1.setBounds(10, 10, 200, 200);

		Composite child2 = new Composite(form, SWT.NONE);
		child2.setLayout(new FillLayout());
		new Button(child2, SWT.PUSH).setText("Button in pane2");

		//		Composite child3 = new Composite(form,SWT.NONE);
		//		child3.setLayout(new FillLayout());
		new Label(form, SWT.PUSH).setText("Label in pane3");

		form.setWeights(new int[] { 20, 60, 20 });
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}