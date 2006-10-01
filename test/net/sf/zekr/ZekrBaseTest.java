/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 29, 2006
 */
package net.sf.zekr;

import java.io.IOException;

import net.sf.zekr.common.config.ApplicationConfig;
import junit.framework.TestCase;

/**
 * All test cases should extend this base class.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ZekrBaseTest extends TestCase {

	public ZekrBaseTest() throws Exception {
		ApplicationConfig.getInstance().getRuntime().configure();
	}

}
