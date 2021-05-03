/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 29, 2007
 */
package net.sf.zekr.engine.template;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

/**
 * A loader for templates stored on the file system.
 * 
 * @author Mohsen Saboorian
 */
public class ZekrFileResourceLoader extends ResourceLoader {
	public void init(ExtendedProperties configuration) {
		// do nothing
	}

	public synchronized InputStream getResourceStream(String templateName) throws ResourceNotFoundException {
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(new File(templateName).getAbsolutePath()));
		} catch (FileNotFoundException e) {
			// do nothing!
		}
		if (is != null) // if no exception occurred
			return is;

		throw new ResourceNotFoundException("Resource not found: " + templateName);
	}

	public boolean isSourceModified(Resource resource) {
		return false;
	}

	public long getLastModified(Resource resource) {
		File file = new File(resource.getName());
		return file.canRead() ? file.lastModified() : 0;
	}
}
