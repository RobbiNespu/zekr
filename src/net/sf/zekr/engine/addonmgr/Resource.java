/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     25/07/2010
 */
package net.sf.zekr.engine.addonmgr;

import java.io.File;

import net.sf.zekr.common.ZekrMessageException;
import net.sf.zekr.engine.common.LocalizedResource;

//This interface represent a resource for the application, it can be a installed resource
//or a not installed resource (CandidateResource)
/**
 * @author Mohsen Saboorian
 */
public interface Resource {

   /**
    * This method is only applicable in the case of CandidateResources
    * 
    * @param progressListener
    * @return
    * @throws ZekrMessageException
    */

   public boolean isCurrent();

   public boolean isLoaded();

   public String getId();

   public LocalizedResource getLocalizedResource();

   public String getDescription();

   @SuppressWarnings("rawtypes")
   public Class getType();

   public File getFile();

   /**
    * Is(or is going to be) this resource shared between all the users.
    * 
    * @return
    */
   public Boolean isShared();

   public String getInstallationFolder();

   public void setIsShared(Boolean b);

}
