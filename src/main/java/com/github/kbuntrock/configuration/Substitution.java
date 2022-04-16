package com.github.kbuntrock.configuration;

import org.apache.maven.plugins.annotations.Parameter;

public class Substitution {

    @Parameter(required = false)
    private String type;
    @Parameter(required = true)
    private String regex;
    @Parameter(required = false)
    private String substitute = "";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getSubstitute() {
        return substitute;
    }

    public void setSubstitute(String substitute) {
        this.substitute = substitute;
    }
}
