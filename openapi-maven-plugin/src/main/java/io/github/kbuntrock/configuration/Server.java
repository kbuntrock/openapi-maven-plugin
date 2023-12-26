package io.github.kbuntrock.configuration;

import org.apache.maven.plugins.annotations.Parameter;

public class Server {

    @Parameter(required = true)
    private String url;
    @Parameter
    private String description;

    public Server() {
    }

    public Server(final Server server) {
        this.url = server.url;
        this.description = server.description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
