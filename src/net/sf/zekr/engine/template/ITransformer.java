package net.sf.zekr.engine.template;

/**
 * General transformer interface.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public interface ITransformer {
	/**
	 * Sets a property in the context of transformer for further use in templates.
	 * 
	 * @param key
	 *           property key to be accessed by that inside template
	 * @param value
	 *           property value
	 */
	void setProperty(String key, Object value);

	String transform() throws TemplateTransformationException;
}
