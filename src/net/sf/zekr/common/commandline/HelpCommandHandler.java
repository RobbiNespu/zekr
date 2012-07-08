/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 15, 2007
 */
package net.sf.zekr.common.commandline;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;

import net.sf.zekr.common.config.GlobalConfig;

/**
 * Class for handling help (<tt>-h</tt> or <tt>--help</tt>) command.
 * 
 * @author Mohsen Saboorian
 */
public class HelpCommandHandler extends CommandHandler {
	private String helpMessage;

	public HelpCommandHandler(String command) {
		PropertyResourceBundle prb = null;
		try {
			InputStream is = HelpCommandHandler.class.getResourceAsStream("help.properties");
			prb = new PropertyResourceBundle(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			helpMessage = prb == null ? "" : prb.getString("commandline.help");
		}

	}

	public void execute() throws CommandException {
		stdout.println("Zekr, Open Quranic Project, ver. " + GlobalConfig.ZEKR_VERSION + ", build "
				+ GlobalConfig.ZEKR_BUILD_NUMBER + ".");
		stdout.println(helpMessage);
	}

	@Override
	public boolean isSingle() {
		return false;
	}
}
