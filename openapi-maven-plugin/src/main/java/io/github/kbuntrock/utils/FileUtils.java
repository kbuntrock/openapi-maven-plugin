package io.github.kbuntrock.utils;

import java.io.File;
import java.nio.file.FileSystems;

/**
 * File utilities for safe path concatenation and normalization.
 *
 * @author Kevin Buntrock
 */
public final class FileUtils {

	/**
	 * Private Constructor
	 */
	private FileUtils() {
	}

	/**
	 * Concatenate a base path and a relative path, then normalize to an absolute {@link File}.
	 *
	 * @param basePath
	 *            absolute or project-based root path
	 * @param relativePath
	 *            path relative to {@code basePath}
	 * @return normalized absolute {@link File}
	 */
	public static File toFile(final String basePath, final String relativePath) {
		StringBuilder sb = new StringBuilder(basePath);
		if(!basePath.isEmpty() && !relativePath.isEmpty()
			&& (!basePath.endsWith("/") || !basePath.endsWith("\\"))
			&& (!relativePath.startsWith("/") || !relativePath.startsWith("\\"))) {
			sb.append("/");
		}
		sb.append(relativePath);
		return new File(FileSystems.getDefault().getPath(sb.toString()).normalize().toAbsolutePath().toString());
	}

}
