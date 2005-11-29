/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2004
 */

package net.sf.zekr.ui;

import java.util.Iterator;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.language.LanguageEngineNaming;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @see 
 * @version 0.1
 */
public class FormUtils {
	/**
	 * @param device
	 *            The <code>Device</code> to extract it's bounds.
	 * @return A <code>Point</code> containing (maxX, maxY) of device.
	 */
	public static Point getScreenSize(Display display) {
		Rectangle r = display.getBounds();
		return new Point(r.width, r.height);
	}

	/**
	 * This method shall be used to find upper-left position of a <code>Rectangle</code>,
	 * which then be centered on the screen.
	 * 
	 * @param display
	 *            The display to extract it's bounds.
	 * @param widgetSize
	 *            widget size and position.
	 * @return Proper (x, y) value.
	 */
	public static Point getScreenCenter(Display display, Rectangle widgetSize) {
		Point p = getScreenSize(display);
		return new Point((p.x - widgetSize.width) / 2, (p.y - widgetSize.height) / 2);
	}

	/**
	 * For internal use only.
	 */
	public static Table getTableForMap(Composite parent, Map map, String colTitle1, String colTitle2,
			Object layoutData) {
		Table table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		table.setLayoutData(layoutData);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn nameCol = new TableColumn(table, SWT.NONE);
		nameCol.setText(colTitle1);
		nameCol.setWidth(70);

		TableColumn valueCol = new TableColumn(table, SWT.NONE);
		valueCol.setText(colTitle2);
		valueCol.setWidth(70);

		String key, value;
		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
			key = (String) iter.next();
			value = map.get(key).toString();
			new TableItem(table, SWT.NONE).setText(new String[] { key, value });
		}

		return table;
	}
	
	/**
	 * For internal use only.
	 */
	public static void updateTable(Table table, Map map) {
		TableItem[] items = table.getItems();
		String key, value;
		int i = 0;
		for (Iterator iter = map.keySet().iterator(); iter.hasNext(); i++) {
			key = (String) iter.next();
			value = map.get(key).toString();
			items[i].setText(new String[] { key, value });
		}
	}
}
