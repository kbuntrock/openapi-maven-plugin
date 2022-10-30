package io.github.kbuntrock.utils;

import java.io.File;
import java.nio.file.FileSystems;

/**
 * File utils
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
	 * Concat a base path and a relative path
	 *
	 * @param basePath
	 * @param relativePath
	 * @return
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
