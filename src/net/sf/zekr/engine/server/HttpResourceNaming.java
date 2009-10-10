/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 31, 2007
 */
package net.sf.zekr.engine.server;

/**
 * @author Mohsen Saboorian
 */
interface HttpResourceNaming {
	public static final String CACHED_RESOURCE = "[cache]";
	public static final String WORKSPACE_RESOURCE = "[workspace]";
	public static final String BASE_RESOURCE = "[base]";
	public static final String WORKSPACE_OR_BASE_RESOURCE = "[w_b]";
	public static final String ABSOLUTE_RESOURCE = "[absolute]";
}
