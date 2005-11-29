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
 * @since Zekr 1.0
 * @see TODO
 * @version 0.1
 */
public class LogSystemImpl implements LogSystem {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.velocity.runtime.log.LogSystem#init(org.apache.velocity.runtime.RuntimeServices)
	 */
	public void init(RuntimeServices rs) throws Exception {
		Logger.getLogger().info("Velocity Template Engine for Zekr initialized.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.velocity.runtime.log.LogSystem#logVelocityMessage(int,
	 *      java.lang.String)
	 */
	public void logVelocityMessage(int level, String message) {
//		 Logger.getLogger().log(Level.toLevel(level), message);
	}

}