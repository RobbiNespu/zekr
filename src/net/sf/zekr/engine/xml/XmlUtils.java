/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 10, 2004
 */

package net.sf.zekr.engine.xml;

import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
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
	 * @return all the nodes (tags) with the name of <code>tagName</code> which
	 *         are present in <code>node</code> children (depth = 1). <br>
	 *         Note that the return value is of type
	 *         <code>NodeList</code>.
	 */
	public static NodeList getNodes(Node node, String tagName) {
		NodeList nodeList = new NodeList(node.getChildNodes());
		NodeList retNodeList = new NodeList();
		Node tempNode = null;
		int childCount = nodeList.size();
		for (int i = 0; i < childCount; i++) {
			tempNode = nodeList.item(i);
			if (tempNode.getNodeName().equalsIgnoreCase(tagName))
				retNodeList.add(tempNode);
		}
		return retNodeList;
	}

	/**
	 * @param list
	 * @param tagName
	 * @return all nodes in <code>list</code> with tag name equal to
	 *         <code>tagName</code>
	 */
	public static NodeList getNodes(NodeList list, String tagName) {
		Node node = null;
		NodeList retList = new NodeList();
		NodeList tempList = null;
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			node = (Node) iter.next();
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
	 * This methods looks into <code>nodeList</code> for any node matches
	 * <code>&lttagName&gt</code> with an attribute <code>attrNode</code>.
	 * 
	 * @param nodeList a list of <code>Node</code> s
	 * @param tagName tag name
	 * @param attrName tag attribute name
	 * @return the node with <code>attrName</code> equal to
	 *         <code>attrValue</code>
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
	 * @param node
	 * @param attr
	 * @param value
	 * @return updated element node
	 */
	public static void setAttr(Element element, String attr, String value) {
		element.setAttribute(attr, value);
	}
	
//	public static Node setOrCreateAttr(Element element, String attr, String value) {
//		element.setA
//	}


	/**
	 * @param node
	 * @param parentAttr
	 * @return value of the named attribute for parent node of <code>node</code>
	 */
	public static String getParentAttr(Node node, String parentAttr) {
		return node.getParentNode().getAttributes().getNamedItem(parentAttr).getNodeValue();
	}
	
}