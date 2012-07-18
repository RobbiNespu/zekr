/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     July 5, 2012
 */
package net.sf.zekr.ui;

import net.sf.zekr.common.config.KeyboardShortcut;

import org.eclipse.swt.widgets.Display;

/**
 * @author Mohsen Saboorian
 */
public interface NativeKeyboardListener {

   void install(Display display, QuranFormController qfc, KeyboardShortcut shortcut);

   void uninstall();

}
