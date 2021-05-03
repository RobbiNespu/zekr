/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 12, 2004
 */

package net.sf.zekr.engine.xml;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class XmlWriter {

	Element rootElement;
	Transformer transformer;
	Document document;

	/**
	 * @param qualifiedName document root tag name
	 * @throws ParserConfigurationException
	 * @throws TransformerConfigurationException
	 */
	public XmlWriter(String qualifiedName) throws ParserConfigurationException,
			TransformerConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();

		// Create the document
		document = impl.createDocument(null, qualifiedName, null);

		rootElement = document.getDocumentElement();

		TransformerFactory transFactory = TransformerFactory.newInstance();
		transformer = transFactory.newTransformer();
	}

	public Element getRoot() {
		return rootElement;
	}

	public Node appendRootChild(Element element) {
		return rootElement.appendChild(element);
	}

	public Document getDocument() {
		return document;
	}

	public void transform(OutputStream outputStream) throws TransformerException {
		Source input = new DOMSource(document);
		Result output = new StreamResult(outputStream);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(input, output);
	}

	public void transform(File file) throws TransformerException {
		Source input = new DOMSource(document);
		Result output = new StreamResult(file);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(input, output);
	}

	public void transform(Writer writer) throws TransformerException {
		Source input = new DOMSource(document);
		Result output = new StreamResult(writer);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(input, output);
	}
}