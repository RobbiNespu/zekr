/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 28, 2004
 */

package net.sf.zekr.engine.theme;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.util.IQuranText;
import net.sf.zekr.common.util.QuranPropertiesUtils;
import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class QuranViewTemplate extends AbstractQuranViewTemplate {
	public static final String LINE_BY_LINE_LAYOUT = "BLOCK";
	public static final String BLOCK_LAYOUT = "INLINE";

	public QuranViewTemplate(IQuranText text) {
		super(text);
		engine.put("LAYOUT", config.getQuranLayout());
	}
}
