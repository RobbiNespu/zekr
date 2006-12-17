/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 14, 2006
 */
package net.sf.zekr.common.util;

import java.io.File;
import java.io.IOException;

/**
 * Interface for an extractor.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public interface IExcractor {
	void extract(File srcFile, String destDir) throws IOException;
}
