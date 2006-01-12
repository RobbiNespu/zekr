/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 1, 2004
 */

package net.sf.zekr.engine.template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.engine.log.LogSystemImpl;
import net.sf.zekr.engine.log.Logger;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.log4j.BasicConfigurator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class TemplateUtilities {
	public static final String name = "";
	VelocityContext context;
	Template template;

	private final static Logger logger = Logger.getLogger(TemplateUtilities.class);

	public TemplateUtilities() {
		BasicConfigurator.configure();
		Velocity.addProperty("file.resource.loader.path", ApplicationPath.TEMPLATE_DIR);
		Velocity.addProperty(Velocity.RUNTIME_LOG_LOGSYSTEM, new LogSystemImpl());
		try {
			Velocity.setExtendedProperties(new ExtendedProperties(ApplicationPath.VELOCITY_CONFIG));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			Velocity.init();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Collection ayaList = new ArrayList();
		ayaList.add("1");
		ayaList.add("2");
		ayaList.add("3");
		ayaList.add("4");

		context = new VelocityContext();
		context.put("ayaList", ayaList);
		context.put("soora", new Integer(99));

		try {
			template = Velocity.getTemplate(ApplicationPath.SOORA_VIEW_TEMPLATE);
			StringWriter sw = new StringWriter();
//			template.setEncoding("UTF-8");
			template.merge(context, sw);
			System.out.println(sw.toString());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

}