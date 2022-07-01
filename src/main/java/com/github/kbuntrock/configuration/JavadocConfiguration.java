package com.github.kbuntrock.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * @author Kevin Buntrock
 */
public class JavadocConfiguration {

    @Parameter(required = false)
    private List<String> scanLocations;

    @Parameter(required = false)
    private String endOfLineReplacement;

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
