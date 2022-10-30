package io.github.kbuntrock.configuration;

import java.util.List;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author Kevin Buntrock
 */
public class JavadocConfiguration {

	public static String DISABLED_EOF_REPLACEMENT = "disabled";

	@Parameter(required = false)
	private List<String> scanLocations;

	@Parameter(required = false)
	private String endOfLineReplacement = DISABLED_EOF_REPLACEMENT;

	public List<String> getScanLocations() {
		return scanLocations;
	}

	public void setScanLocations(List<String> scanLocations) {
		this.scanLocations = scanLocations;
	}

	public String getEndOfLineReplacement() {
		return endOfLineReplacement;
	}

	public void setEndOfLineReplacement(String endOfLineReplacement) {
		this.endOfLineReplacement = endOfLineReplacement;
	}
}
