package com.github.kbuntrock;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;

public class ApiConfiguration {

    /**
     * A list of location to find api endpoints. A location could be a class or a package
     */
    @Parameter(required = true)
    private List<String> locations;

    @Parameter(defaultValue = "openapi.yaml", required = true)
    private File filename;

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public File getFilename() {
        return filename;
    }

    public void setFilename(File filename) {
        this.filename = filename;
    }
}
