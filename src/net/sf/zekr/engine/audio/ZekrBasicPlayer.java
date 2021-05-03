/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 12, 2010
 */
package net.sf.zekr.engine.audio;

import java.util.Map;

import javazoom.jlgui.basicplayer.BasicPlayer;

/**
 * @author Mohsen Saboorian
 */
@SuppressWarnings("rawtypes")
public class ZekrBasicPlayer extends BasicPlayer {
   private Map audioInfo;

   public Map getAudioInfo() {
      return audioInfo;
   }

   public void setAudioInfo(Map audioInfo) {
      this.audioInfo = audioInfo;
   }
}
