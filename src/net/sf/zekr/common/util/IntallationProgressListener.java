/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 13, 2010
 */
package net.sf.zekr.common.util;

import net.sf.zekr.engine.addonmgr.Resource;

/**
 * @author Mohsen Saboorian
 */
public interface IntallationProgressListener {

   public void start(long totalSize);

   public boolean progress(long itemSize);

   public void finish(Resource producedObject);

}
