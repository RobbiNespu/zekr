/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 15, 2004
 */

package net.sf.zekr.engine.log;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

/**
 * @author Mohsen Saboorian
 */
public class LogSystemImpl implements LogSystem {

	public void init(RuntimeServices rs) throws Exception {
		Logger.getLogger(LogSystemImpl.class).info("Velocity Template Engine for Zekr initialized.");
	}

	public void logVelocityMessage(int level, String message) {
		// Logger.getLogger().log(Level.toLevel(level), message);
	}

}
