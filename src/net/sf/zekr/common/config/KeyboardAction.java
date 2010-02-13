/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 13, 2010
 */
package net.sf.zekr.common.config;

/**
 * @author Mohsen Saboorian
 */
public class KeyboardAction {
	/** SWT bitwise or'd key for LTR mode. */
	public int key;
	/** SWT bitwise or'd key for RTL mode. */
	public int rtlKey;
	/** Specifies whether this action should be taken place globally regardless of the current active shell. */
	public boolean global;
	/** Specifies that this action should or should not be taken place if active shell is a modal one */
	public boolean suppressOnModal;
	/** A comma-separated list of windows (shell ID) only on top of which this command can be taken place. */
	public String window;
	/** Action name. */
	public String action;

	public KeyboardAction(int key, int rtlKey, boolean global, boolean suppressOnModal, String window, String action) {
		this.key = key;
		this.rtlKey = rtlKey;
		this.global = global;
		this.suppressOnModal = suppressOnModal;
		this.window = window;
		this.action = action;
	}

	@Override
	public String toString() {
		return "KeyboardAction [action=" + action + ", global=" + global + ", key=" + key + ", rtlKey=" + rtlKey
				+ ", suppressOnModal=" + suppressOnModal + ", window=" + window + "]";
	}
}
