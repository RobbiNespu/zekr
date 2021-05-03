/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 6, 2009
 */
package net.sf.zekr.common.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.zekr.engine.language.LanguageEngine;

import org.apache.commons.configuration.PropertiesConfiguration;
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
   public static final int WINKEY = SWT.CTRL << 1; // we treat SWT.CTRL << 1 as Win key

   private Map<Integer, List<KeyboardAction>> keytoAction = new HashMap<Integer, List<KeyboardAction>>();
   private Map<Integer, List<KeyboardAction>> keyToActionRtl = new HashMap<Integer, List<KeyboardAction>>();

   private Map<String, Integer> actionToKey = new HashMap<String, Integer>();
   private Map<String, Integer> actionToKeyRtl = new HashMap<String, Integer>();

   private Map<String, Integer> controlMap = new HashMap<String, Integer>();

   private Document doc;
   private PropertiesConfiguration props;
   private boolean commandAndControlAreSame;

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

   public KeyboardShortcut(PropertiesConfiguration props, Document shortcut) {
      this.props = props;
      doc = shortcut;

      try {
         commandAndControlAreSame = props.getBoolean("key.commandAndControlAreSame", true);
      } catch (Exception e) {
         // workaround for a bug in Zekr 0.7.5 beta 4
         commandAndControlAreSame = true;
         props.setProperty("key.commandAndControlAreSame", "true");
      }
   }

   public void init() {
      Element root = doc.getDocumentElement();
      NodeList mappings = root.getElementsByTagName("mapping");
      for (int i = 0; i < mappings.getLength(); i++) {
         Element mapping = (Element) mappings.item(i);
         String action = mapping.getAttribute("action");
         String key = mapping.getAttribute("key");
         boolean isGlobal = Boolean.parseBoolean(mapping.getAttribute("global"));
         boolean isSuperGlobal = Boolean.parseBoolean(mapping.getAttribute("superGlobal"));
         boolean suppressOnModal = Boolean.parseBoolean(mapping.getAttribute("suppressOnModal"));
         String window = isGlobal ? null : mapping.getAttribute("window");
         String rtlKey = mapping.getAttribute("rtlKey");

         int keyCode = 0, keyCodeRtl = 0;
         if (StringUtils.isNotBlank(action)) {
            if (StringUtils.isNotBlank(key)) {
               keyCode = extractKeyCode(key);
            } else {
               continue;
            }
            if (StringUtils.isNotBlank(rtlKey)) {
               keyCodeRtl = extractKeyCode(rtlKey);
            }

            KeyboardAction command = new KeyboardAction(keyCode, keyCodeRtl, isGlobal, isSuperGlobal, suppressOnModal, window,
                  action);

            if (keyCode > 0) {
               List<KeyboardAction> actionList = keytoAction.get(keyCode);
               if (actionList == null) {
                  actionList = new ArrayList<KeyboardAction>();
                  keytoAction.put(keyCode, actionList);
               }
               actionList.add(command);

               actionToKey.put(action, keyCode);
            }

            if (keyCodeRtl > 0) {
               List<KeyboardAction> actionListRtl = keyToActionRtl.get(keyCodeRtl);
               if (actionListRtl == null) {
                  actionListRtl = new ArrayList<KeyboardAction>();
                  keyToActionRtl.put(keyCodeRtl, actionListRtl);
               }
               actionListRtl.add(command);

               actionToKeyRtl.put(action, keyCodeRtl);
            }
         }
      }
   }

   private int extractKeyCode(String key) {
      key = key.toLowerCase();
      key = StringUtils.replace(key, "\\+", "plus");
      String[] keyParts = StringUtils.split(key, '+');
      int accel = 0;

      for (int j = 0; j < keyParts.length; j++) {
         String part = keyParts[j].trim();
         if ("ctrl".equals(part)) {
            if (GlobalConfig.isMac && commandAndControlAreSame) {
               accel |= SWT.COMMAND;
            } else {
               accel |= SWT.CTRL;
            }
         } else if ("cmd".equals(part)) {
            accel |= SWT.COMMAND;
         } else if ("alt".equals(part)) {
            accel |= SWT.ALT;
         } else if ("shift".equals(part)) {
            accel |= SWT.SHIFT;
         } else if ("win".equals(part)) {
            accel |= WINKEY;
         } else if ("plus".equals(part)) {
            accel |= '+';
         } else if (controlMap.containsKey(part)) {
            accel |= controlMap.get(part);
         } else if (part.length() >= 2 && part.charAt(0) == 'f' && NumberUtils.isDigits(part.substring(1))) {
            int f = Integer.parseInt(part.substring(1));
            accel |= SWT.F1 - 1 + f;
         } else if (part.length() == 1) {
            accel |= part.toUpperCase().charAt(0);
         }
      }
      return accel;
   }

   /**
    * Converts SWT bitwise key combination to its string representation.
    * 
    * @param accelerator SWT bitwise key combination
    * @return string representation of the accelerator
    */
   public static String keyCodeToString(int accelerator) {
      String accelStr = "";
      if (accelerator != 0) {
         int accKey = accelerator;
         String combKey = "";
         boolean plusNeeded = false;
         if ((accelerator & WINKEY) == WINKEY) {
            accKey ^= WINKEY;
            combKey += "Win";
            plusNeeded = true;
         }
         if ((accelerator & SWT.CONTROL) == SWT.CONTROL) {
            accKey ^= SWT.CONTROL;
            combKey += (plusNeeded ? "+" : "") + "Ctrl";
            plusNeeded = true;
         }
         if ((accelerator & SWT.COMMAND) == SWT.COMMAND) {
            accKey ^= SWT.COMMAND;
            combKey += (plusNeeded ? "+" : "") + "Cmd";
            plusNeeded = true;
         }
         if ((accelerator & SWT.ALT) == SWT.ALT) {
            accKey ^= SWT.ALT;
            combKey += (plusNeeded ? "+" : "") + "Alt";
            plusNeeded = true;
         }
         if ((accelerator & SWT.SHIFT) == SWT.SHIFT) {
            accKey ^= SWT.SHIFT;
            combKey += (plusNeeded ? "+" : "") + "Shift";
            plusNeeded = true;
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
         } else if (accKey >= SWT.F1 && accKey <= SWT.F15) { // try function keys
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

   public List<KeyboardAction> getKeyActionList(Integer key) {
      LanguageEngine lang = LanguageEngine.getInstance();
      boolean rtl = GlobalConfig.hasBidiSupport && lang.isRtl();
      if (rtl && keyToActionRtl.containsKey(key)) {
         return keyToActionRtl.get(key);
      } else {
         return keytoAction.get(key);
      }
   }

   public Integer getKeyForAction(String action, boolean isRtl) {
      if (isRtl && actionToKeyRtl.get(action) != null) {
         return actionToKeyRtl.get(action);
      }
      return actionToKey.get(action);
   }

   public Map<String, Integer> getActionToKey(boolean isRtl) {
      return isRtl ? actionToKeyRtl : actionToKey;
   }

   public Map<String, Integer> getActionToKey() {
      return actionToKey;
   }

   public static void main(String[] args) {
      int keyBit = WINKEY | SWT.SHIFT | SWT.ALT | SWT.INSERT;
      System.out.println(keyBit + " - " + KeyboardShortcut.keyCodeToString(keyBit));
   }
}
