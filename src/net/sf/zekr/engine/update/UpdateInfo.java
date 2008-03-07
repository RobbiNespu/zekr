/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 15, 2008
 */
package net.sf.zekr.engine.update;

import java.util.Date;

/**
 * A data object which holds update information filled from the update site.
 * 
 * @author Mohsen Saboorian
 */
public class UpdateInfo {
	public static final String BETA_RELEASE = "BETA";
	public static final String DEV_RELEASE = "DEV";
	public static final String FINAL_RELEASE = "FINAL";

	public String fullName;
	public String version;

	/** can be FINAL, BETA or DEV */
	public String status;
	public String build;
	public String downloadUrl;
	public String noteUrl;
	public String info;
	public Date releaseDate;

	/** generated upate info message */
	public String message;
}
