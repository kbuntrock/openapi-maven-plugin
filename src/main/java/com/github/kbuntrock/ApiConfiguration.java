package com.github.kbuntrock;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

public class ApiConfiguration {

    /**
     * A list of location to find api endpoints. A location could be a class or a package
     */
    @Parameter(required = true)
    private List<String> locations;

    @Parameter
    private String filename = "openapi.yml";

    /**
     * List of strings which should be removed from the tags names
     */
    @Parameter(required = false)
    private List<String> tagRemovableStrings = new ArrayList<>();

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<String> getTagRemovableStrings() {
        return tagRemovableStrings;
    }

    public void setTagRemovableStrings(List<String> tagRemovableStrings) {
        this.tagRemovableStrings = tagRemovableStrings;
    }
}
