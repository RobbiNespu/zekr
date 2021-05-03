/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 10, 2004
 */

package net.sf.zekr.engine.xml;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Node;

/**
 * This class is an implementation of NodeList using an <code>ArrayList</code> as the low level container.
 * 
 * @author Mohsen Saboorian
 */
public class NodeList implements org.w3c.dom.NodeList {
	ArrayList<Node> list = null;

	public NodeList(org.w3c.dom.NodeList nodeList) {
		list = new ArrayList<Node>();
		for (int i = 0; i < nodeList.getLength(); i++)
			list.add(nodeList.item(i));
	}

	public NodeList() {
		list = new ArrayList<Node>();
	}

	/*
	 * @see org.w3c.dom.NodeList#getLength()
	 */
	public int getLength() {
		return list.size();
	}

	/*
	 * @see org.w3c.dom.NodeList#item(int)
	 */
	public Node item(int index) {
		return list.get(index);
	}

	/**
	 * Returns a string representation of this <code>NodeList</code>. Here is an example: <br>
	 * if <code>NodeList</code> contains 3 objects o1, o2, and o3, the <code>toString()</code> result should be
	 * something like this: <br>
	 * <blockquote>[{o3}, {o2}, {o3}] </blockquote> where {x} means <code>String.valueOf(x)</code>.
	 * 
	 * @return a string representation of this collection.
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		Iterator<Node> i = iterator();
		boolean hasNext = i.hasNext();
		while (hasNext) {
			Object o = i.next();
			buf.append(o == this ? "(this NodeList)" : String.valueOf(o));
			hasNext = i.hasNext();
			if (hasNext)
				buf.append(", ");
		}
		buf.append("]");
		return buf.toString();
	}

	public void add(Node node) {
		list.add(node);
	}

	public void remove(Node node) {
		list.remove(node);
	}

	/*
	 * @see org.w3c.dom.NodeList#getLength()
	 */
	public int size() {
		return getLength();
	}

	public Iterator<Node> iterator() {
		return list.iterator();
	}

	public void add(NodeList nodes) {
		for (Iterator<Node> iter = nodes.iterator(); iter.hasNext();)
			list.add(iter.next());
	}

	public void deleteAll() {
		list = new ArrayList<Node>();
	}

}
