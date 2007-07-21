/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     ۲۰۰۷/۰۷/۱۵
 */
package net.sf.zekr.common.commandline;

public interface CommandConstants {
	static final String HELP_COMMAND = "-h";
	static final String HELP_COMMAND_VERBOSE = "--help";

	/** Set Zekr home/workspace folder. This command helps users to launch multiple instances of Zekr at one time. */
	static final String WORKSPACE_COMMAND = "-workspace";

	/** Index command for performing Quran text index (creating of updating indices for current user of anyone) */
	static final String INDEX_COMMAND = "-index";

	/** Clean command for deletion of cache folders, config files, and all contents of home folder. */
	static final String CLEAN_COMMAND = "-clean";
}
