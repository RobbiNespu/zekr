/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 6, 2009
 */
package net.sf.zekr.common.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.swt.SWT;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Mohsen Saboorian
 */
public class KeyboardShortcut {
	public class Key {
		int stateMask;
		int keyCode;
	}

	private Map<Integer, String> keyToAction = new HashMap<Integer, String>();
	private Map<Integer, String> rtlKeyToAction = new HashMap<Integer, String>();
	private Map<String, Integer> actionToKey = new HashMap<String, Integer>();
	private Map<String, Integer> rtlActionToKey = new HashMap<String, Integer>();
	private Document doc;

	Map<String, Integer> controlMap = new HashMap<String, Integer>();
	{
		controlMap.put("up", SWT.ARROW_UP);
		controlMap.put("down", SWT.ARROW_DOWN);
		controlMap.put("left", SWT.ARROW_LEFT);
		controlMap.put("right", SWT.ARROW_RIGHT);
		controlMap.put("pageup", SWT.PAGE_UP);
		controlMap.put("pagedown", SWT.PAGE_DOWN);
		controlMap.put("home", SWT.HOME);
		controlMap.put("end", SWT.END);
		controlMap.put("insert", SWT.INSERT);
		controlMap.put("delete", (int) SWT.DEL);
		controlMap.put("enter", (int) SWT.CR);
		controlMap.put("space", 32);
		controlMap.put("backspace", (int) SWT.BS);
		controlMap.put("esc", (int) SWT.ESC);
		controlMap.put("tab", (int) SWT.TAB);
	}

	public KeyboardShortcut(Document shortcut) {
		this.doc = shortcut;
	}

	public void init() {
		Element root = doc.getDocumentElement();
		NodeList mappings = root.getElementsByTagName("mapping");
		for (int i = 0; i < mappings.getLength(); i++) {
			Element mapping = (Element) mappings.item(i);
			String action = mapping.getAttribute("action");
			String key = mapping.getAttribute("key");
			String rtlKey = mapping.getAttribute("rtlKey");
			for (int ii = 0; ii < 2; ii++) {
				if (ii == 1) {
					key = rtlKey;
				}
				if (StringUtils.isNotBlank(action) && StringUtils.isNotBlank(key)) {
					key = key.toLowerCase();
					String[] keyParts = StringUtils.split(key, '+');
					int accel = 0;
					for (int j = 0; j < keyParts.length; j++) {
						String part = keyParts[j].trim();
						if ("ctrl".equals(part)) {
							accel |= SWT.CTRL;
						} else if ("alt".equals(part)) {
							accel |= SWT.ALT;
						} else if ("shift".equals(part)) {
							accel |= SWT.SHIFT;
						} else if (controlMap.containsKey(part)) {
							accel |= controlMap.get(part);
						} else if (part.length() >= 2 && part.charAt(0) == 'f' && NumberUtils.isDigits(part.substring(1))) {
							int f = Integer.parseInt(part.substring(1));
							accel |= SWT.F1 - 1 + f;
						} else if (part.length() == 1) {
							accel |= part.toUpperCase().charAt(0);
						}
					}

					if (ii == 0) { // key
						keyToAction.put(accel, action);
						actionToKey.put(action, accel);
					} else { // rtlKey
						rtlKeyToAction.put(accel, action);
						rtlActionToKey.put(action, accel);
					}
				}
			}
		}
	}

	public static String keyCodeToString(int accelerator) {
		String accelStr = "";
		if (accelerator != 0) {
			int accKey = accelerator;
			String combKey = "";
			boolean ctrl = false, alt = false;
			if ((accelerator & SWT.CONTROL) == SWT.CONTROL) {
				accKey ^= SWT.CONTROL;
				if (GlobalConfig.isMac) {
					accelerator ^= SWT.CONTROL;
					accelerator |= SWT.COMMAND;
					combKey += "Command";
				} else {
					combKey += "Ctrl";
				}
				ctrl = true;
			}
			if ((accelerator & SWT.ALT) == SWT.ALT) {
				accKey ^= SWT.ALT;
				combKey += (ctrl ? "+" : "") + "Alt";
				alt = true;
			}
			if ((accelerator & SWT.SHIFT) == SWT.SHIFT) {
				accKey ^= SWT.SHIFT;
				combKey += (alt || ctrl ? "+" : "") + "Shift";
			}
			accelStr = combKey + "+";
			if (accKey >= 'A' && accKey <= 'Z') {
				accelStr = accelStr + (char) accKey;
			} else if (accKey >= SWT.ARROW_UP && accKey <= SWT.INSERT) {
				String s = "";
				switch (accKey) {
				case SWT.ARROW_UP:
					s = "Up";
					break;
				case SWT.ARROW_DOWN:
					s = "Down";
					break;
				case SWT.ARROW_LEFT:
					s = "Left";
					break;
				case SWT.ARROW_RIGHT:
					s = "Right";
					break;
				case SWT.PAGE_UP:
					s = "PageUp";
					break;
				case SWT.PAGE_DOWN:
					s = "PageDown";
					break;
				case SWT.HOME:
					s = "Home";
					break;
				case SWT.END:
					s = "End";
					break;
				case SWT.INSERT:
					s = "Insert";
					break;
				}
				accelStr = accelStr + s;
			} else if (accKey >= SWT.F1 && accKey <= SWT.F20) { // try function keys
				int f = accKey - SWT.F1 + 1;
				accelStr = combKey + "F" + f;
			} else {
				String s = "";
				switch (accKey) {
				case SWT.BS:
					s = "Backspace";
					break;
				case SWT.CR:
				case SWT.LF:
					s = "Enter";
					break;
				case SWT.DEL:
					s = "Delete";
					break;
				case SWT.ESC:
					s = "Esc";
					break;
				case SWT.TAB:
					s = "Tab";
					break;
				default:
					s = String.valueOf((char) accKey);
				}
				accelStr = accelStr + s;
			}
		}
		return accelStr;
	}

	public String getActionForKey(Integer key, boolean isRtl) {
		if (isRtl && rtlKeyToAction.get(key) != null) {
			return rtlKeyToAction.get(key);
		}
		return keyToAction.get(key);
	}

	public Integer getKeyForAction(String action, boolean isRtl) {
		if (isRtl && rtlActionToKey.get(action) != null) {
			return rtlActionToKey.get(action);
		}
		return actionToKey.get(action);
	}

	public Map<Integer, String> getKeyToAction(boolean isRtl) {
		return isRtl ? rtlKeyToAction : keyToAction;
	}

	public Map<String, Integer> getActionToKey(boolean isRtl) {
		return isRtl ? rtlActionToKey : actionToKey;
	}

	public Map<String, Integer> getActionToKey() {
		return actionToKey;
	}

	public Map<Integer, String> getKeyToAction() {
		return keyToAction;
	}

	public Map<Integer, String> getRtlKeyToAction() {
		return rtlKeyToAction;
	}

	public static void main(String[] args) {
		System.out.println((SWT.CONTROL | 'R') + " - " + KeyboardShortcut.keyCodeToString(SWT.CONTROL | 'R'));
	}
}
