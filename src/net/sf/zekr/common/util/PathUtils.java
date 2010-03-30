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
	public static final String WORKSPACE_OR_BASE_RESOURCE = "<workspace_base>";
	public static final String WORKSPACE_RESOURCE = "<workspace>";
	public static final String INSTALLDIR_RESOURCE = "<installdir>";
	public static final String ABSOLUTE_RESOURCE = "<absolute>";
	public static final String BASE_RESOURCE = "<base>";

	public static Map<String, String> pathLookup = new HashMap<String, String>();
	static {
		pathLookup.put(WORKSPACE_RESOURCE, FilenameUtils.normalize(Naming.getWorkspace()));
		pathLookup.put(BASE_RESOURCE, FilenameUtils.normalize(GlobalConfig.RUNTIME_DIR));
	}

	/**
	 * Resolves a dynamic path denoted by either of variables {@link #WORKSPACE_RESOURCE},
	 * {@link #BASE_RESOURCE}, {@link #WORKSPACE_OR_BASE_RESOURCE}, {@link #ABSOLUTE_RESOURCE} or
	 * {@link #INSTALLDIR_RESOURCE} in the beginning of the parameter. If the parameter doesn't contain any of
	 * the variables, simply a <code>new File(unresolvedPath)</code> is returned.
	 * <p/>
	 * Except for {@link #ABSOLUTE_RESOURCE}, a slash (or backslash) character should always be followed by
	 * either of variable resources mentioned. For example <code>&lt;base&gt;/path/to/file</code> is correct
	 * while <code>&lt;base&gt;path/to/file</code> is incorrect. Be careful that a slash after &lt;absolute&gt;
	 * is treated as a path element. Here are two examples: <br/>
	 * 1. <code>&lt;absolute&gt;/usr/share/zekr/res</code> <br/>
	 * 2. <code>&lt;absolute&gt;C:/Program Files/Zekr/res</code>
	 * 
	 * @param unresolvedPath
	 * @param unresolvedParentFolder this parameter will be taken as the parent folder of unresolvedPath if it
	 *           started with none of {@link #WORKSPACE_RESOURCE}, {@link #BASE_RESOURCE},
	 *           {@link #WORKSPACE_OR_BASE_RESOURCE}, or {@link #INSTALLDIR_RESOURCE}
	 * @return a file refers to the resolved path of the input parameter or null if unresolvedPath is
	 *         <code>null</code> or empty.
	 */
	public static File resolve(String unresolvedPath, String unresolvedParentFolder) {
		String baseDir;
		File resolvedFile;
		if (org.apache.commons.lang.StringUtils.isBlank(unresolvedPath)) {
			return null;
		}
		if (unresolvedPath.startsWith(WORKSPACE_RESOURCE)) {
			baseDir = pathLookup.get(WORKSPACE_RESOURCE);
			resolvedFile = new File(baseDir, unresolvedPath.substring(WORKSPACE_RESOURCE.length() + 1));
		} else if (unresolvedPath.startsWith(ABSOLUTE_RESOURCE)) {
			resolvedFile = new File(unresolvedPath.substring(ABSOLUTE_RESOURCE.length()));
		} else if (unresolvedPath.startsWith(BASE_RESOURCE)) {
			baseDir = (String) pathLookup.get(BASE_RESOURCE);
			resolvedFile = new File(baseDir, unresolvedPath.substring(BASE_RESOURCE.length() + 1));
		} else if (unresolvedPath.startsWith(WORKSPACE_OR_BASE_RESOURCE)) {
			baseDir = Naming.getWorkspace();
			String substring = unresolvedPath.substring(WORKSPACE_OR_BASE_RESOURCE.length() + 1);
			resolvedFile = new File(baseDir, substring);
			if (!resolvedFile.exists()) {
				baseDir = pathLookup.get(WORKSPACE_RESOURCE);
				resolvedFile = new File(baseDir, substring);
			}
		} else if (unresolvedPath.startsWith(INSTALLDIR_RESOURCE)) {
			resolvedFile = new File(GlobalConfig.ZEKR_INSTALL_DIR, unresolvedPath
					.substring(INSTALLDIR_RESOURCE.length() + 1));
		} else {
			resolvedFile = new File(unresolvedParentFolder, unresolvedPath);
		}
		return resolvedFile;
	}

	public static boolean isOnlineContent(String fileName) {
		return fileName.startsWith("http://") || fileName.startsWith("https://") || fileName.startsWith("ftp://");
	}
}
