/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     July 5, 2012
 */
package net.sf.zekr.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.sf.zekr.common.config.KeyboardAction;
import net.sf.zekr.common.config.KeyboardShortcut;
import net.sf.zekr.engine.log.Logger;

import org.eclipse.swt.widgets.Display;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;

/**
 * @author Mohsen Saboorian
 */
public class WindowsNativeKeyboardListener implements NativeKeyboardListener {
   private Logger logger = Logger.getLogger(WindowsNativeKeyboardListener.class);

   private List<KeyboardAction> superGlobalActionList = new ArrayList<KeyboardAction>();

   @Override
   public void install(final Display display, final QuranFormController qfc, KeyboardShortcut shortcut) {
      JIntellitype jIntellitype = null;
      try {
         jIntellitype = JIntellitype.getInstance();
      } catch (Exception e) {
         logger.error("Error instantiating JIntellitype: " + e.toString());
         return;
      }

      for (Entry<String, Integer> e : shortcut.getActionToKey().entrySet()) {
         List<KeyboardAction> al = shortcut.getKeyActionList(e.getValue());
         for (KeyboardAction keyboardAction : al) {
            if (keyboardAction.superGlobal) {
               String keyStr = KeyboardShortcut.keyCodeToString(keyboardAction.key);
               logger.debug(String.format("Registering super global shortcut for %s: %s", keyboardAction, keyStr));
               jIntellitype.registerHotKey(superGlobalActionList.size(), keyStr);
               superGlobalActionList.add(keyboardAction);
            }
         }
      }

      // install super global listeners
      jIntellitype.addHotKeyListener(new HotkeyListener() {
         @Override
         public void onHotKey(int identifier) {
            if (identifier < superGlobalActionList.size()) {
               KeyboardAction action = superGlobalActionList.get(identifier);
               logger.debug(String.format("Hotkey detected. id: %s, action: ", identifier, action.action));
               qfc.executeAction(action.action);
            }
         }
      });

      // install media player listeners
      jIntellitype.addIntellitypeListener(new IntellitypeListener() {
         @Override
         public void onIntellitype(final int command) {
            switch (command) {
            // volume is usually handled by OS, so better not to handle it again inside Zekr
            /*case JIntellitype.APPCOMMAND_VOLUME_UP:
            case JIntellitype.APPCOMMAND_VOLUME_DOWN:
            case JIntellitype.APPCOMMAND_VOLUME_MUTE:*/

            case JIntellitype.APPCOMMAND_MEDIA_PREVIOUSTRACK:
            case JIntellitype.APPCOMMAND_MEDIA_NEXTTRACK:
            case JIntellitype.APPCOMMAND_MEDIA_STOP:
            case JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE:
               display.asyncExec(new Runnable() {
                  @Override
                  public void run() {
                     if (command == JIntellitype.APPCOMMAND_MEDIA_PREVIOUSTRACK) {
                        qfc.playerPrev();
                     } else if (command == JIntellitype.APPCOMMAND_MEDIA_NEXTTRACK) {
                        qfc.playerNext();
                     } else if (command == JIntellitype.APPCOMMAND_MEDIA_STOP) {
                        qfc.playerStop();
                     } else if (command == JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE) {
                        qfc.playerTogglePlayPause();
                     }
                  }
               });
               break;
            default:
               break;
            }
         }
      });
   }

   @Override
   public void uninstall() {
      JIntellitype jIntellitype;
      try {
         jIntellitype = JIntellitype.getInstance();
         jIntellitype.cleanUp();
      } catch (Exception e) {
         logger.error("Error instanciating JIntellitype: " + e.toString());
         return;
      }
   }
}
