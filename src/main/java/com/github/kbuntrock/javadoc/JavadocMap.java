package com.github.kbuntrock.javadoc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin Buntrock
 */
public enum JavadocMap {
    INSTANCE;

    private Map<String, ClassDocumentation> javadocMap = new HashMap<>();

    public Map<String, ClassDocumentation> getJavadocMap() {
        return javadocMap;
    }

    public void setJavadocMap(Map<String, ClassDocumentation> javadocMap) {
        this.javadocMap = javadocMap;
    }

    public boolean isPresent() {
        return this.javadocMap != null;
    }
}
