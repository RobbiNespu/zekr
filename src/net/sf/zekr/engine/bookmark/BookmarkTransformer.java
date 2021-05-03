/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jun 18, 2007
 */
package net.sf.zekr.engine.bookmark;

import java.io.File;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.engine.log.Logger;

/**
 * Bookmark exporter. This class is used to export bookmark sets in HTML format (XML+XSLT).
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class BookmarkTransformer {
	private BookmarkSet bookmarkSet;
	private File destFile;
	private Transformer transformer;
	private static BookmarkTransformer bookmarkTransformer;
	private static ResourceManager res = ResourceManager.getInstance();
	private final static Logger logger = Logger.getLogger(BookmarkTransformer.class);

	public static BookmarkTransformer getInstance() throws BookmarkTransformationException {
		if (bookmarkTransformer == null) {
			try {
				bookmarkTransformer = new BookmarkTransformer();
			} catch (Exception e) {
				throw new BookmarkTransformationException(e);
			}
		}
		return bookmarkTransformer;
	}

	public BookmarkTransformer(BookmarkSet bookmarkSet, File destFile) {
		this.bookmarkSet = bookmarkSet;
		this.destFile = destFile;
	}

	/**
	 * Creates a new bookmark transformer object. This object caches a compiled in-memory XSL transformer within itself.
	 * 
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 */
	public BookmarkTransformer() throws TransformerConfigurationException, TransformerFactoryConfigurationError {
		String xsl = res.getString("bookmark.xslt");
		logger.debug("Create a new templates object for: " + xsl);
		Templates templates = TransformerFactory.newInstance().newTemplates(new StreamSource(new File(xsl)));
		logger.debug("Create a new transformer object for: " + xsl);
		transformer = templates.newTransformer();
		logger.debug("Transformer created successfully.");
	}

	/**
	 * Export bookmark set into an HTML file.
	 * 
	 * @param bookmarkSet
	 * @param destFile
	 * @throws BookmarkTransformationException
	 */
	public void export(BookmarkSet bookmarkSet, File destFile) throws BookmarkTransformationException {
		try {
			Source source;
			if (bookmarkSet.isLoaded())
				source = new DOMSource(bookmarkSet.getXmlDocument());
			else
				source = new StreamSource(bookmarkSet.getFile());
			Result result = new StreamResult(destFile);
			logger.debug("Transform bookmark set into HTML file: " + destFile);
			transformer.transform(source, result);
			logger.debug("Transform done.");
		} catch (TransformerException e) {
			throw new BookmarkTransformationException(e);
		}
	}
}
