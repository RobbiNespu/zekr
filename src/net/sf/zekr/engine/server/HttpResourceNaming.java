/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 31, 2007
 */
package net.sf.zekr.engine.server;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.runtime.Naming;

interface HttpResourceNaming {
	public static final String CACHED_RESOURCE = "[cache]";
	public static final String WORKSPACE_RESOURCE = "[workspace]";
	public static final String BASE_RESOURCE = "[base]";
	public static final String WORKSPACE_OR_BASE_RESOURCE = "[w_b]";
}
