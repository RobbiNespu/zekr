/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 14, 2004
 */

package net.sf.zekr.engine.theme;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.log.Logger;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * An adapter class for velocity template engine.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class TemplateEngine {
	VelocityContext context;
	private static TemplateEngine thisInstance;
	Template template;

	/**
	 * Instantiate a sample <code>TemplateEngine</code>
	 */
	private TemplateEngine() {
		try {
			System.setProperty("zekr.home", Naming.HOME_PATH);
			Velocity.setExtendedProperties(new ExtendedProperties("res/config/lib/velocity.properties"));
			// Velocity.addProperty("file.resource.loader.path",
			// ApplicationPath.THEME_DIR);
			Velocity.init();
			context = new VelocityContext();
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).log(e);
		}
	}

	/**
	 * @return the template engine instance. A call to this method will reset the context of the template
	 *         engine.
	 */
	public static TemplateEngine getInstance() {
		if (thisInstance == null) {
			thisInstance = new TemplateEngine();
			return thisInstance;
		}
		thisInstance.resetContext();
		return thisInstance;
	}

	/**
	 * @param name The file name of the desired template
	 * @return The associated <code>Template</code> object
	 * @throws Exception
	 */
	public Template getTemplate(String name) throws Exception {
		return Velocity.getTemplate(name);
	}

	/**
	 * Add a key-value pair to the template engine context.
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value) {
		context.put(key, value);
	}

	/**
	 * Add a key-value pair to the template engine context. value should be of type <code>String</code>.
	 * 
	 * @param key
	 * @param value
	 */
	public void putWrappedString(String key, String value) {
		context.put(key, value);
	}

	/**
	 * Add a collection of key-value pairs to the template engine context. Keys should be of type
	 * <code>String</code>.
	 */
	public void putAll(Map map) {
		for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			context.put((String) entry.getKey(), entry.getValue());
		}
	}

	/**
	 * @param name the file name of the desired template
	 * @return the result <code>String</code> after the context map is merged (applied) into the source
	 *         template file.
	 * @throws Exception
	 */
	public String getUpdated(String name) throws Exception {
		template = Velocity.getTemplate(name);
		Writer writer = new StringWriter();
		template.merge(context, writer);
		return writer.toString();
	}

	public void resetContext() {
		context = new VelocityContext();
	}

}
