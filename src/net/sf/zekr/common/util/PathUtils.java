/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2009
 */
package net.sf.zekr.common.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.runtime.Naming;

import org.apache.commons.io.FilenameUtils;

/**
 * @author Mohsen Saboorian
 */
public class PathUtils {
	public static final String WORKSPACE_RESOURCE = "<workspace>";
	public static final String BASE_RESOURCE = "<base>";

	public static Map pathLookup = new HashMap();
	static {
		pathLookup.put(WORKSPACE_RESOURCE, FilenameUtils.normalize(Naming.getWorkspace()));
		pathLookup.put(BASE_RESOURCE, FilenameUtils.normalize(GlobalConfig.RUNTIME_DIR));
	}

	/**
	 * Resolves a dynamic path denoted by either of variables {@link #WORKSPACE_RESOURCE} or
	 * {@link #BASE_RESOURCE} in the start part of the parameter. If the parameter doesn't contain any of the
	 * variables, simply a <code>new File(unresolvedPath)</code> is returned.
	 * 
	 * @param unresolvedPath
	 * @return a file refers to the resolved path of the input parameter
	 */
	public static File resolve(String unresolvedPath) {
		String baseDir;
		File resolvedFile;
		if (unresolvedPath.startsWith(WORKSPACE_RESOURCE)) {
			baseDir = Naming.getWorkspace();
			resolvedFile = new File(baseDir, unresolvedPath.substring(WORKSPACE_RESOURCE.length()));
		} else if (unresolvedPath.startsWith(BASE_RESOURCE)) {
			baseDir = (String) pathLookup.get(BASE_RESOURCE);
			resolvedFile = new File(baseDir, unresolvedPath.substring(BASE_RESOURCE.length()));
		} else {
			resolvedFile = new File(unresolvedPath);
		}
		return resolvedFile;
	}

}
