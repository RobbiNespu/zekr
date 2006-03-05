/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 3, 2006
 */
package net.sf.zekr.common.resource.dynamic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import net.sf.zekr.common.config.GlobalConfig;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class CommonCache {

	public void create(File file, String content) throws IOException {
		// FIXIT: replace OUT_HTML_ENCODING with a proper encoding
		OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(
				new FileOutputStream(file)), GlobalConfig.OUT_HTML_ENCODING);
		osw.write(content);
	}
}
