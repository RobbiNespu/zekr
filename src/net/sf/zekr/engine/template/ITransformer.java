package net.sf.zekr.engine.template;


/**
 * General transformer interface.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public interface ITransformer {
	String transform() throws TemplateTransformationException;
}
