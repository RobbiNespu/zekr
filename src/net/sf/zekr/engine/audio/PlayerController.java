/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 19, 2009
 */
package net.sf.zekr.engine.audio;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

/**
 * Originally taken from {@link BasicController} interface. The are some differences however. For example
 * {@link #open(PlayableObject)} method is unified so that accepts a {@link PlayableObject}, which can be a
 * {@link URL}, {@link InputStream} or {@link File}. This makes it handy to handle files from different
 * sources with the same API. Event handling is also added to this interface.
 * 
 * @author Mohsen Saboorian
 */
public interface PlayerController {
   public enum PlayingItem {
      AYA, AUDHUBILLAH, BISMILLAH, SADAGHALLAH
   }

   // playing status
   public static final int UNKNOWN = -1;
   public static final int PLAYING = 0;
   public static final int PAUSED = 1;
   public static final int STOPPED = 2;
   public static final int OPENED = 3;
   public static final int SEEKING = 4;

   // play scope
   public static final String PS_CONTINUOUS = "continuous";
   public static final String PS_AYA = "aya";
   public static final String PS_PAGE = "page";
   public static final String PS_SURA = "sura";
   public static final String PS_HIZB_QUARTER = "hizb-quarter";
   public static final String PS_JUZ = "juz";

   /**
    * Open {@link PlayableObject} to play.
    * 
    * @param playableObject playable object to open
    * @param openForCaching specifies whether this playable object is opened for now-playing or should be
    *           cached for later use.
    * @throws PlayerException
    */
   public void open(PlayableObject playableObject, boolean openForCaching) throws PlayerException;

   /**
    * Open a {@link PlayableObject} for now-playing. Equal to <code>open(playableObject, false)</code>.
    * 
    * @param playableObject
    * @throws PlayerException
    */
   public void open(PlayableObject playableObject) throws PlayerException;

   /**
    * Skip bytes.
    * 
    * @param bytes
    * @return bytes skipped according to audio frames constraint.
    * @throws PlayerException
    */
   public long seek(long bytes) throws PlayerException;

   /**
    * Start playback.
    * 
    * @throws PlayerException
    */
   public void play() throws PlayerException;

   /**
    * Stop playback.
    * 
    * @throws PlayerException
    */
   public void stop() throws PlayerException;

   /**
    * Pause playback.
    * 
    * @throws PlayerException
    */
   public void pause() throws PlayerException;

   /**
    * Resume playback.
    * 
    * @throws PlayerException
    */
   public void resume() throws PlayerException;

   /**
    * Sets Pan (balance) value. Linear scale: -1.0 <--> +1.0
    * 
    * @param pan value from -1.0 to +1.0
    * @throws PlayerException
    */
   public void setPan(double pan) throws PlayerException;

   /**
    * Sets Gain value. Linear scale 0.0 <--> 1.0
    * 
    * @param gain value from 0.0 to 1.0
    * @throws PlayerException
    */
   public void setGain(double gain) throws PlayerException;

   /**
    * @return playing status. Can be either of the following values:
    *         <ul>
    *         <li>{@link #UNKNOWN}</li>
    *         <li>{@link #PLAYING}</li>
    *         <li>{@link #PAUSED}</li>
    *         <li>{@link #STOPPED}</li>
    *         <li>{@link #OPENED}</li>
    *         <li>{@link #SEEKING}</li>
    *         </ul>
    */
   public int getStatus();

   /**
    * @see BasicPlayer#addBasicPlayerListener(BasicPlayerListener)
    */
   public void addBasicPlayerListener(BasicPlayerListener bpl);

   /**
    * @see BasicPlayer#getListeners()
    */
   public Collection<BasicPlayerListener> getListeners();

   /**
    * @see BasicPlayer#removeBasicPlayerListener(BasicPlayerListener)
    */
   public void removeBasicPlayerListener(BasicPlayerListener bpl);

   /**
    * @return volume: a number between 0 to 100
    */
   public int getVolume();

   /**
    * Sets volume. It is applied to the player thread only if player status is
    * {@link PlayerController#PLAYING} or {@link PlayerController#PAUSED}
    * 
    * @param volume a number between 0 to 100
    */
   public void setVolume(int volume);

   /**
    * @return true if player should continue to the next aya after playing one aya
    */

   public boolean isMultiAya();
   //	/**
   //	 * @param multiAya specifies the continuity of playing, that is, whether to play next aya after finishing
   //	 *           one or not.
   //	 */
   //	public void setMultiAya(boolean multiAya);

   /**
    * @return number of milliseconds to wait between playing two ayas
    */
   public int getInterval();

   /**
    * @param wait number of milliseconds to wait between playing two ayas
    */
   public void setInterval(int interval);

   /**
    * @return how many times play each aya
    */
   public int getRepeatTime();

   /**
    * @param repeatTime how many times play each aya
    */
   public void setRepeatTime(int repeatTime);

   public void setPlayingItem(PlayingItem playingItem);

   public PlayingItem getPlayingItem();

   public String getPlayScope();

   public void setPlayScope(String playScope);

   public PlayableObject getCurrentPlayableObject();

   @SuppressWarnings("rawtypes")
   public Map getCurrentAudioInfo();

   public int getCurrentAudioIndex();

   void setCurrentAudioIndex(int currentAudioIndex);

   public int getRecitationCount();

   public boolean isLastRecitation();

   /**
    * Moves current audio index to the next one (to put it simple it ++es currentAudioIndex.
    * 
    * @return true if the last index already reached and should go to the next item, false otherwise.
    */
   public boolean gotoNextAudio();

}
