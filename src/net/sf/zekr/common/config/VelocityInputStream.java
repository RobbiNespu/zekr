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
 * This is a wrapper class for wrapping a velocity output result <code>String</code>
 * into an <code>InputStream</code>.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class VelocityInputStream extends InputStream {
	String buffer;
	int index = 0;
	byte[] b;

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
