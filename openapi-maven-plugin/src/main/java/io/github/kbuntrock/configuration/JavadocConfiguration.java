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
	private String encoding = "UTF-8";

	@Parameter(required = false)
	private boolean debugScan = false;
	@Parameter(required = false)
	private String endOfLineReplacement = DISABLED_EOF_REPLACEMENT;

	public List<String> getScanLocations() {
		return scanLocations;
	}

	public void setScanLocations(final List<String> scanLocations) {
		this.scanLocations = scanLocations;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}

	public String getEndOfLineReplacement() {
		return endOfLineReplacement;
	}

	public void setEndOfLineReplacement(final String endOfLineReplacement) {
		this.endOfLineReplacement = endOfLineReplacement;
	}

	public void setDebugScan(final boolean debugScan) {
		this.debugScan = debugScan;
	}

	public boolean isDebugScan() {
		return debugScan;
	}
}
