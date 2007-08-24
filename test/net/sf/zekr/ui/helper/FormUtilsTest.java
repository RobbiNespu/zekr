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

	public void testAddAmpersand() throws Exception {
		assertEquals( FormUtils.addAmpersand("Help"), "&Help");
		assertEquals( FormUtils.addAmpersand("H&elp"), "H&elp");
	}
}
