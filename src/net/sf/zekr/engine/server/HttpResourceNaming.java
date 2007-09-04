/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 31, 2007
 */
package net.sf.zekr.engine.server;

import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.runtime.Naming;

import org.apache.commons.lang.StringUtils;

public class HttpResourceNaming {
	public static final String CACHED_RESOURCE = "[cache]";
	public static final String WORKSPACE_RESOURCE = "[workspace]";
	public static final String BASE_RESOURCE = "[base]";

	public static String getRealPath(String path) {
		path = StringUtils.replace(path, CACHED_RESOURCE, Naming.getCacheDir());
		path = StringUtils.replace(path, WORKSPACE_RESOURCE, Naming.getWorkspace());
		path = StringUtils.replace(path, BASE_RESOURCE, GlobalConfig.RUNTIME_DIR);
		return path;
	}
}
