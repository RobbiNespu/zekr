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
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

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
		RowData rd1 = new RowData(); rd1.width = 11;
		RowData rd2 = new RowData(); rd2.width = 26;
		RowData rd3 = new RowData(); rd3.width = 33;
		b1.setLayoutData(rd1); 
		b2.setLayoutData(rd2); 
		b3.setLayoutData(rd3); 
		int b1width = b1.getBounds().width;
		int b2width = b2.getBounds().width;
		int b3width = b3.getBounds().width;
		// we check the two button configuration without minimum length
		// we should get b2 as the minimal length because it's the largest
		assertEquals( b2width, FormUtils.buttonLength(b1, b2));
		assertEquals( b2width, FormUtils.buttonLength(b2, b1));
		// we check the three button configuration without minimum
		assertEquals( b3width, FormUtils.buttonLength(b1, b2, b3));
		assertEquals( b3width, FormUtils.buttonLength(b1, b3, b2));
		assertEquals( b3width, FormUtils.buttonLength(b2, b3, b1));
		assertEquals( b3width, FormUtils.buttonLength(b2, b1, b3));
		assertEquals( b3width, FormUtils.buttonLength(b3, b2, b1));
		assertEquals( b3width, FormUtils.buttonLength(b3, b1, b2));
		// two button configuration with a minimal length < smallest button
		assertEquals( b1width, FormUtils.buttonLength(b1width, b1, b2));
		// two button configuration width a minimal length > biggest button
		assertEquals( b3width, FormUtils.buttonLength(b3width, b2, b1));
		// three button configuration with a minimal length < smallest button
		assertEquals( b1width, FormUtils.buttonLength(b1width, b1, b2, b3));
		// three button configuration with a minimal length > biggest button
		assertEquals( b3width+1, FormUtils.buttonLength(b3width+1, b3, b2, b1));
	}	
}
