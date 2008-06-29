/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 29, 2006
 */
package net.sf.zekr;

import junit.framework.TestCase;
import net.sf.zekr.common.config.ApplicationConfig;

/**
 * All test cases should extend this base class.
 * 
 * @author Mohsen Saboorian
 */
public class ZekrBaseTest extends TestCase {
	/**
	 * Some test cases cannot run before {@link ApplicationConfig} is instantiated.
	 */
	public ZekrBaseTest() throws Exception {
		ApplicationConfig.getInstance().getRuntime().configure();
	}

}
