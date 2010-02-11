/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 11, 2010
 */
package net.sf.zekr.engine.audio;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Mohsen Saboorian
 */
public class TestInputStream extends InputStream {
	@Override
	public int read() throws IOException {
		return 0;
	}
}
