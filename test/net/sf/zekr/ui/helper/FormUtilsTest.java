/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Johan Laenen
 * Start Date:     Aug 24, 2007
 */
package net.sf.zekr.ui.helper;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.RowData;

/**
 * @author    Johan Laenen
 * @since	  Zekr 1.0
 */
public class FormUtilsTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * @author laejoh
	 * @throws Exception
	 */
	public void testAddAmpersand() throws Exception {
		assertEquals( "&Help", FormUtils.addAmpersand("Help") );
		assertEquals( "&Help", FormUtils.addAmpersand("&Help") );
		assertEquals( "H&elp", FormUtils.addAmpersand("H&elp") );
		assertEquals( "He&lp", FormUtils.addAmpersand("He&lp") );
		assertEquals( "Hel&p", FormUtils.addAmpersand("Hel&p") );
		assertEquals( "Help&", FormUtils.addAmpersand("Help&") );
		assertEquals( "&H&e&l&p&", FormUtils.addAmpersand("&H&e&l&p&") );
	}
	
	/**
	 * @author laejoh
	 * @throws Exception
	 */
	public void testButtonlength() throws Exception {
		Display display = new Display();
		Shell shell = new Shell(display);
		Button b1 = new Button(shell, SWT.NONE);
		Button b2 = new Button(shell, SWT.NONE);
		Button b3 = new Button(shell, SWT.NONE);
		RowData rd1 = new RowData();
		RowData rd2 = new RowData();
		RowData rd3 = new RowData();
		b1.setText("*");
		b2.setText("**");
		b3.setText("***");
		b1.setLayoutData(rd1); b1.pack(); // length unknown
		b2.setLayoutData(rd2); b2.pack(); // length 26
		b3.setLayoutData(rd3); b3.pack(); // length 33
		// we check the two button configuration without minimum length
		assertEquals( 26, FormUtils.buttonLength(b1, b2));
		assertEquals( 26, FormUtils.buttonLength(b2, b1));
		// we check the three button configuration without minimum
		assertEquals( 33, FormUtils.buttonLength(b1, b2, b3));
		assertEquals( 33, FormUtils.buttonLength(b1, b3, b2));
		assertEquals( 33, FormUtils.buttonLength(b2, b3, b1));
		assertEquals( 33, FormUtils.buttonLength(b2, b1, b3));
		assertEquals( 33, FormUtils.buttonLength(b3, b2, b1));
		assertEquals( 33, FormUtils.buttonLength(b3, b1, b2));
		// two button configuration with a minimal length < smallest button
		assertEquals( 26, FormUtils.buttonLength(25, b1, b2));
		assertEquals( 26, FormUtils.buttonLength(24, b2, b1));
		// three button configuration with a minimal length < smallest
		assertEquals( 33, FormUtils.buttonLength(32, b1, b2, b3));
		assertEquals( 33, FormUtils.buttonLength(31, b3, b2, b1));
		assertEquals( 33, FormUtils.buttonLength(30, b1, b3, b2));
		assertEquals( 33, FormUtils.buttonLength(29, b3, b1, b2));
		assertEquals( 33, FormUtils.buttonLength(28, b2, b3, b1));
		assertEquals( 33, FormUtils.buttonLength(27, b2, b1, b3));
		// as above for two buttons but > smallest
		assertEquals( 80, FormUtils.buttonLength(80, b1, b2));
		assertEquals( 79, FormUtils.buttonLength(79, b2, b1));
		// as above for three buttons but > smalest
		assertEquals( 78, FormUtils.buttonLength(78, b1, b2, b3));
		assertEquals( 77, FormUtils.buttonLength(77, b1, b3, b2));
		assertEquals( 76, FormUtils.buttonLength(76, b2, b3, b1));
		assertEquals( 75, FormUtils.buttonLength(75, b2, b1, b3));
		assertEquals( 74, FormUtils.buttonLength(74, b3, b2, b1));
		assertEquals( 73, FormUtils.buttonLength(73, b3, b1, b2));
	}	
}
