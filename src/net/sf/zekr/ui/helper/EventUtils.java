/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 23, 2007
 */
package net.sf.zekr.ui.helper;

import net.sf.zekr.engine.log.Logger;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Mohsen Saboorian
 */
public class EventUtils {

   private static final Logger logger = Logger.getLogger(EventUtils.class);

   /**
    * Creates and sends an event of type <code>EventProtocol.CUSTOM_ZEKR_EVENT</code> to all shells listening
    * for this event. <code>Event.data</code> is set as <code>eventName</code> parameter.
    * 
    * @param eventName <code>Event.data</code> to be sent
    */
   public static void sendEvent(String eventName) {
      sendEvent(EventProtocol.CUSTOM_ZEKR_EVENT, eventName, 0);
   }

   public static void sendEvent(String eventName, int detail) {
      sendEvent(EventProtocol.CUSTOM_ZEKR_EVENT, eventName, detail);
   }

   /**
    * Creates and sends an event of type <code>eventType</code> to all shells listening for this event.
    * <code>Event.data</code> is set as <code>eventName</code> parameter.
    * 
    * @param eventType <code>Event.type</code> to be sent
    * @param eventName <code>Event.data</code> to be sent
    * @param detail
    */
   public static void sendEvent(int eventType, String eventName, int detail) {
      Display disp = Display.getCurrent();
      if (disp == null) {
         return;
      }
      Shell shells[] = disp.getShells();
      Event event = createEvent(eventType, eventName, detail);
      for (int i = 0; i < shells.length; i++) {
         if (shells[i].isListening(eventType)) {
            shells[i].notifyListeners(eventType, event);
         }
      }
   }

   /**
    * Creates and sends an event of type <code>eventType</code> to the specified control.
    * <code>Event.data</code> is set as <code>eventName</code> parameter.
    * 
    * @param control event target
    * @param eventType <code>Event.type</code> to be sent
    * @param eventName <code>Event.data</code> to be sent
    */
   public static void sendEvent(Control control, int eventType, String eventName) {
      Event event = createEvent(eventType, eventName, 0);
      control.notifyListeners(eventType, event);
   }

   /**
    * Creates and sends an event of type <code>EventProtocol.CUSTOM_ZEKR_EVENT</code> to the specified shell.
    * <code>Event.data</code> is set as <code>eventName</code> parameter.
    * 
    * @param control event target
    * @param eventName <code>Event.data</code> to be sent
    */
   public static void sendEvent(Control control, String eventName) {
      sendEvent(control, EventProtocol.CUSTOM_ZEKR_EVENT, eventName);
   }

   private static Event createEvent(int eventType, String eventName, int detail) {
      Event event = new Event();
      event.data = eventName;
      event.type = eventType;
      event.detail = detail;
      return event;
   }

   public static void sendSyncEvent(Display display, final String event) {
      display.syncExec(new Runnable() {
         public void run() {
            try {
               EventUtils.sendEvent(event);
            } catch (Exception e) {
               logger.implicitLog(e);
            }
         }
      });
   }

   public static void sendAsyncEvent(Display display, final String event) {
      display.asyncExec(new Runnable() {
         public void run() {
            try {
               EventUtils.sendEvent(event);
            } catch (Exception e) {
               logger.implicitLog(e);
            }
         }
      });
   }

}
