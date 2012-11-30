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
import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

public interface ResourceManager {

   public List<Resource> getLoadedResources();

   /** this method should load a resource recently installed */
   public void loadResource(Resource r) throws ConfigurationException, IOException;

   /**
    * This method should unload a resource from the system closing of the files accessing to that resource, so
    * the file it self can be deleted.
    */
   public void unloadResource(Resource r);

   public Resource getCurrentResource();

   public void setCurrentResource(Resource r);

   /*
    * It will return a instance of a candidate resource from a file instance
    * this is used before the installation of the resource.
    */
   public CandidateResource getNewCandidateResource(File file);

}
