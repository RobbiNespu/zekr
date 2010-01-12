/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 10, 2004
 */

package net.sf.zekr.engine.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Mohsen Saboorian
 */
public class XmlUtils {

	public static final String TEXT_NODE = "#text";

	public static Node getNode(org.w3c.dom.NodeList nodeList, String tagName) {
		for (int i = 0; i < nodeList.getLength(); i++)
			if (nodeList.item(i).getNodeName().equalsIgnoreCase(tagName))
				return nodeList.item(i);
		return null;
	}

	/**
	 * @param node single <code>Node</code>.
	 * @param tagName
	 * @return all the nodes (tags) with the name of <code>tagName</code> which are present in
	 *         <code>node</code> children (depth = 1). <br>
	 *         Note that the return value is of type <code>NodeList</code>.
	 */
	public static NodeList getNodes(Node node, String tagName) {
		NodeList nodeList = new NodeList(node.getChildNodes());
		NodeList retNodeList = new NodeList();
		Node tempNode = null;
		int childCount = nodeList.size();
		for (int i = 0; i < childCount; i++) {
			tempNode = nodeList.item(i);
			if (tempNode.getNodeName().equals(tagName))
				retNodeList.add(tempNode);
		}
		return retNodeList;
	}

	/**
	 * @param list
	 * @param tagName
	 * @return all nodes in <code>list</code> with tag name equal to <code>tagName</code>
	 */
	public static NodeList getNodes(NodeList list, String tagName) {
		Node node = null;
		NodeList retList = new NodeList();
		NodeList tempList = null;
		for (Iterator<Node> iter = list.iterator(); iter.hasNext();) {
			node = iter.next();
			tempList = new NodeList(node.getChildNodes());
			for (int i = 0; i < tempList.getLength(); i++) {
				node = tempList.item(i);
				if (node.getNodeName().equalsIgnoreCase(tagName))
					retList.add(node);
			}
		}
		return retList;
	}

	/**
	 * This methods looks into <code>nodeList</code> for any node matches <code>&lttagName&gt</code> with an
	 * attribute <code>attrNode</code>.
	 * 
	 * @param nodeList a list of <code>Node</code> s
	 * @param tagName tag name
	 * @param attrName tag attribute name
	 * @return the node with <code>attrName</code> equal to <code>attrValue</code>
	 */
	public static Element getElementByNamedAttr(org.w3c.dom.NodeList nodeList, String tagName, String attrName,
			String attrValue) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equalsIgnoreCase(tagName)
					&& node.getAttributes().getNamedItem(attrName).getNodeValue().equals(attrValue))
				return (Element) node;
		}
		return null;
	}

	/**
	 * @param node
	 * @param attr
	 * @return value of the named attribute or <code>null</code> if there is no such attribute
	 */
	public static String getAttr(Node node, String attr) {
		Node n = node.getAttributes().getNamedItem(attr);
		return n == null ? null : n.getNodeValue();
	}

	/**
	 * @param element
	 * @param attr
	 * @param value
	 */
	public static void setAttr(Element element, String attr, String value) {
		element.setAttribute(attr, value);
	}

	/**
	 * @param node
	 * @param parentAttr
	 * @return value of the named attribute for parent node of <code>node</code>
	 */
	public static String getParentAttr(Node node, String parentAttr) {
		return node.getParentNode().getAttributes().getNamedItem(parentAttr).getNodeValue();
	}

	public static boolean isElement(Node node, String string) {
		return node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(string);
	}

	/**
	 * Writes a <code>org.w3c.Node</code> object into an output file.
	 * 
	 * @param node a {@link Node} object to be written to file
	 * @param outputFile ouput file
	 * @throws TransformerException
	 */
	public static void writeXml(Node node, File outputFile) throws TransformerException {
		writeXml(node, outputFile, "UTF-8");
	}

	/**
	 * Writes a <code>org.w3c.Node</code> object into an output file, specifying output file encoding.
	 * 
	 * @param node a {@link Node} object to be written to file
	 * @param outputFile ouput file
	 * @param encoding encoding of the output file
	 * @throws TransformerException
	 */
	public static void writeXml(Node node, File outputFile, String encoding) throws TransformerException {
		try {
			writeXml(node, new OutputStreamWriter(new FileOutputStream(outputFile), encoding));
		} catch (Exception e) {
			throw new TransformerException(e);
		}
	}

	/**
	 * Writes a <code>org.w3c.Node</code> object into an output <code>Writer</code>, omitting XML declaration.
	 * 
	 * @param node node object to be written to file
	 * @param outputWriter ouput writer object
	 * @throws TransformerException
	 */
	public static void writeXml(Node node, Writer outputWriter) throws TransformerException {
		writeXml(node, outputWriter, false);
	}

	/**
	 * Writes a <code>org.w3c.Node</code> object into an output <code>Writer</code>.
	 * 
	 * @param node node object to be written to file
	 * @param outputWriter ouput writer object
	 * @param omitXmlDecl omits XML declaration if <code>true</code>
	 * @throws TransformerException
	 */
	public static void writeXml(Node node, Writer outputWriter, boolean omitXmlDecl) throws TransformerException {
		Source input = new DOMSource(node);
		Result output = new StreamResult(outputWriter);
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitXmlDecl ? "yes" : "no");
		transformer.transform(input, output);
	}

}
