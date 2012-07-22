/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2004
 */

package net.sf.zekr.ui.helper;

import java.util.Map;
import java.util.Map.Entry;

import net.sf.zekr.common.util.HyperlinkUtils;
import net.sf.zekr.ui.ZekrForm;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TaskBar;
import org.eclipse.swt.widgets.TaskItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Mohsen Saboorian
 */
public class FormUtils {
   public static final String URL_DATA = "URL";

   /**
    * @param display The <code>Device</code> to extract it's bounds.
    * @return A <code>Point</code> containing (maxX, maxY) of device.
    */
   public static Point getScreenSize(Display display) {
      Rectangle r = display.getBounds();
      return new Point(r.width, r.height);
   }

   /**
    * This method shall be used to find upper-left position of a <code>Rectangle</code>, which then be
    * centered on the screen.
    * 
    * @param display The display to extract it's bounds.
    * @param widgetSize widget size and position.
    * @return Proper (x, y) value.
    */
   public static Point getScreenCenter(Display display, Rectangle widgetSize) {
      Point p = getScreenSize(display);
      return new Point((p.x - widgetSize.width) / 2, (p.y - widgetSize.height) / 2);
   }

   public static Table getTableFromMap(Composite parent, Map<String, String> map, String title1, String title2,
         int width1, int width2, Object layoutData, int style) {
      Table table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | style);
      table.setLayoutData(layoutData);
      table.setLinesVisible(true);
      table.setHeaderVisible(true);

      TableColumn nameCol = new TableColumn(table, SWT.NONE);
      nameCol.setText(title1);

      TableColumn valueCol = new TableColumn(table, SWT.NONE);
      valueCol.setText(title2);

      String key, value;
      for (Entry<String, String> entry : map.entrySet()) {
         key = entry.getKey();
         value = entry.getValue();
         new TableItem(table, SWT.NONE).setText(new String[] { key, value });
      }

      if (width1 != SWT.DEFAULT) {
         nameCol.setWidth(width1);
      } else {
         nameCol.pack();
      }
      if (width2 != SWT.DEFAULT) {
         valueCol.setWidth(width2);
      } else {
         valueCol.pack();
      }

      return table;
   }

   public static void addRow(Table table, String str1, String str2) {
      TableItem item = new TableItem(table, SWT.NONE);
      item.setText(0, str1);
      item.setText(1, str2);
   }

   public static void addEditableRow(Table table, String str1, String str2) {
      TableItem item = new TableItem(table, SWT.NONE);
      item.setText(0, str1);
      TableEditor editor = new TableEditor(table);
      Text text = new Text(table, SWT.NONE);
      editor.grabHorizontal = true;
      editor.setEditor(text, item, 1);
      text.setText(str2);
   }

   /**
    * For internal use only.
    */
   public static void updateTable(Table table, Map<String, String> suraMap) {
      TableItem[] items = table.getItems();
      int i = 0;
      for (Entry<String, String> entry : suraMap.entrySet()) {
         items[i].setText(new String[] { entry.getKey(), entry.getValue() });
         i++;
      }
      for (i = 0; i < table.getColumnCount(); i++) {
         table.getColumn(i).pack();
      }
   }

   /**
    * @param direction can be either <tt>rtl</tt> or <tt>ltr</tt>
    * @return <code>SWT.RIGHT_TO_LEFT</code> if direction is <tt>rtl</tt> (ignoring the case),
    *         <code>SWT.LEFT_TO_RIGHT</code> otherwise.
    */
   public static int toSwtDirection(String direction) {
      return "rtl".equalsIgnoreCase(direction) ? SWT.RIGHT_TO_LEFT : SWT.LEFT_TO_RIGHT;
   }

   public static void removeSelection(Table table) {
      table.remove(table.getSelectionIndices()[0]);
   }

   public static Point getCenter(Shell parent, Shell shell) {
      int x = parent.getLocation().x + parent.getSize().x / 2;
      int y = parent.getLocation().y + parent.getSize().y / 2;
      return new Point(x - shell.getSize().x / 2, y - shell.getSize().y / 2);
   }

   /**
    * check a menuText entry for an ampersand and if not found prepend the menuText with one
    * 
    * @author laejoh
    * @param menuText the menuText to be shown, i.e. the translated text with possible &amp;amp; markers to
    *           show which key on the keyboard will activate the menu item
    * @return either menuText with an &amp;amp; marker in the first position if there was no &amp;amp;
    *         menuText without any modification if there already was a &amp;amp; included
    */
   public static String addAmpersand(final String menuText) {
      if (menuText.indexOf('&') <= -1) {
         return "&" + menuText;
      }
      return menuText;
   }

   /**
    * Return the maximum length for a button when two buttons are given
    * 
    * @author laejoh
    * @param button1 a first button Button object
    * @param button2 a second button Button object
    * @return int max length of the two buttons given
    */
   public static int buttonLength(final Button button1, final Button button2) {
      return Math.max(button1.getBounds().width, button2.getBounds().width);
   }

   /**
    * Return the maximum length for a button when two buttons and a minimum length are given.
    * 
    * @author laejoh
    * @param minimum an integer giving the minimum length a button has to have
    * @param button1 a first button Button object
    * @param button2 a second button Button object
    * @return int max length of the two buttons given
    */
   public static int buttonLength(final int minimum, final Button button1, final Button button2) {
      return Math.max(minimum, buttonLength(button1, button2));
   }

   /**
    * Return the maximum length for a button when three buttons are given.
    * 
    * @author laejoh
    * @param button1 a first button
    * @param button2 a second button
    * @return int max length of the two buttons given
    */
   public static int buttonLength(final Button button1, final Button button2, final Button button3) {
      return Math.max(button3.getBounds().width, buttonLength(button1, button2));
   }

   /**
    * return the maximum length for a button when three buttons and a minimum length are given
    * 
    * @author laejoh
    * @param minimum an integer giving the minimum length a button has to have
    * @param button1 a first button
    * @param button2 a second button
    * @return int max length of the two buttons given
    */
   public static int buttonLength(final int minimum, final Button button1, final Button button2, final Button button3) {
      return Math.max(minimum, buttonLength(button1, button2, button3));
   }

   /**
    * Listener used for opening system's web browser if one clicks on a Link widget.
    */
   private static Listener linkListener = new Listener() {
      public void handleEvent(Event event) {
         if (event.type == SWT.Selection) {
            HyperlinkUtils.openBrowser((String) event.widget.getData(URL_DATA));
         }
      }
   };

   /**
    * Adds click listener to the link widget.
    * 
    * @param widget
    */
   public static void addLinkListener(Widget widget) {
      widget.addListener(SWT.Selection, linkListener);
   }

   /**
    * Limits the size of the shell to the given values.
    * 
    * @param shell the Shell to limit
    */
   public static void limitSize(Shell shell, int width, int height) {
      Point size = shell.getSize();
      shell.setSize(size.x > width ? width : size.x, size.y > height ? height : size.y);
   }

   public static Shell findShell(Display display, String shellId) {
      Shell[] shells = display.getShells();
      for (int i = 0; i < shells.length; i++) {
         if (StringUtils.equals(shellId, (String) shells[i].getData(ZekrForm.FORM_ID))) {
            return shells[i];
         }
      }
      return null;
   }

   public static Shell findShell(String shellId) {
      return findShell(Display.getDefault(), shellId);
   }

   public static String getCurrentFormId() {
      Shell shell = Display.getCurrent().getActiveShell();
      return (String) (shell != null ? shell.getData(ZekrForm.FORM_ID) : null);
   }

   public static void disposeGracefully(Shell shell) {
      try {
         if (shell != null) {
            shell.dispose();
         }
      } catch (Throwable th) {
      }
   }

   public static TaskItem getTaskBarItem(Display display, Shell shell) {
      TaskBar bar = display.getSystemTaskBar();
      if (bar == null) {
         return null;
      }
      TaskItem item = bar.getItem(shell);
      if (item == null) {
         item = bar.getItem(null);
      }
      return item;
   }

   public static void boldFont(Display display, Control control) {
      FontData[] fonts = control.getFont().getFontData();
      if (fonts.length > 0) {
         fonts[0].setStyle(SWT.BOLD);
      }
      control.setFont(new Font(display, fonts));
   }
}
