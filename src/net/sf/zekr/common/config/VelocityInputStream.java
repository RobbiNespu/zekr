/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 4, 2005
 */
package net.sf.zekr.common.config;

import java.io.IOException;
import java.io.InputStream;

import net.sf.zekr.engine.template.TemplateEngine;

/**
 * This is a wrapper class for converting velocity output result <code>String</code>
 * into a {@link InputStream}.
 * 
 * @author Mohsen Saboorian
 */
public class VelocityInputStream extends InputStream {
	private String buffer;
	private int index = 0;
	private byte[] b;

	public VelocityInputStream(String fileName) throws Exception {
		buffer = TemplateEngine.getInstance().getUpdated(fileName);
		b = buffer.getBytes();
	}

	public int read() throws IOException {
		if (b.length > ++index)
			return b[index];
		return -1;
	}
}
