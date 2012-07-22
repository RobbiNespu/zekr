/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 23, 2006
 */
package net.sf.zekr.ui.helper;

/**
 * @author Mohsen Saboorian
 */
public interface EventProtocol {
   /**
    * Refresh the view
    */
   String REFRESH_VIEW = "REFRESH_VIEW";

   /**
    * Semi-restart the application.
    */
   String RECREATE_VIEW = "RECREATE_VIEW";

   /**
    * Clear <tt>cache</tt> directory on application exit.
    */
   String CLEAR_CACHE_ON_EXIT = "CLEAR_CACHE_ON_EXIT";

   /**
    * Tells the shell to update bookmarks menu (as bookmark tree changed).
    */
   String UPDATE_BOOKMARKS_MENU = "UPDATE_BOOKMARKS_MENU";

   String GOTO_LOCATION = "GOTO_LOCATION";

   /**
    * Event used to progress the progress bar of the splash screen at startup.
    */
   String SPLASH_PROGRESS = "SPLASH_PROGRESS";

   /**
    * Event used for indicating that the progress bar of the splash screen is now complete.
    */
   String SPLASH_PROGRESS_FULLY = "SPLASH_PROGRESS_FULLY";

   int CUSTOM_ZEKR_EVENT = -7;

   /**
    * Event to force a modal waiting form to close.
    */
   String END_WAITING = "END_WAITING";

   /**
    * Event to force updating sura name combobox.
    */
   String UPDATE_SURA_NAMES = "UPDATE_SURA_NAMES";

   /**
    * Event used to update of the import progress bar for recitations or other resources. In addition to this
    * data, event.detail also passes a number between 0 to 99 indicating the progress.
    */
   String IMPORT_PROGRESS = "IMPORT_PROGRESS";

   /**
    * Event used to update of the import progress bar for recitations or other resources to its highest point.
    * This event is raised when importing is finished.
    */
   String IMPORT_PROGRESS_DONE = "IMPORT_PROGRESS_DONE";

   /**
    * This event is raised when importing is failed.
    */
   String IMPORT_PROGRESS_FAILED = "IMPORT_PROGRESS_FAILED";

   /**
    * Event raised when importing of a recitation or other resource is canceled by user.
    */
   String IMPORT_CANCELED = "IMPORT_CANCELED";

   /* 
    Event triggered after the installation of a resource
    note that the actual string is different because is build dynamically upon the name
    of resource class
    */

   String TRANSLATION_IMPORTED = "TRANSLATIONDATA_IMPORTED";
   String TRANSLATION_REMOVED = "TRANSLATIONDATA_REMOVED";

   String RECITATION_IMPORTED = "AUDIODATA_IMPORTED";
   String RECITATION_REMOVED = "AUDIODATA_REMOVED";

   String RESOURCE_REMOVAL_FAILED = "RESOURCE_REMOVAL_FAILED";

   String NEEDS_RESTART = "NEEDS_RESTART";

}
