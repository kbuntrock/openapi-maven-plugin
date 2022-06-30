package com.github.kbuntrock.javadoc;

import com.github.javaparser.javadoc.Javadoc;

import java.util.HashMap;
import java.util.Map;

public class ClassDocumentation {

    private String completeName;
    private String simpleName;
    private Javadoc javadoc;

    private Map<String, Javadoc> fieldsJavadoc;
    private Map<String, Javadoc> methodsJavadoc;

    public ClassDocumentation(String completeName, String simpleName) {
        this.completeName = completeName;
        this.simpleName = simpleName;
    }

    public String getCompleteName() {
        return completeName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public Javadoc getJavadoc() {
        return javadoc;
    }

    public void setJavadoc(Javadoc javadoc) {
        this.javadoc = javadoc;
    }

    public Map<String, Javadoc> getFieldsJavadoc() {
        if (fieldsJavadoc == null) {
            fieldsJavadoc = new HashMap<>();
        }
        return fieldsJavadoc;
    }

    public Map<String, Javadoc> getMethodsJavadoc() {
        if (methodsJavadoc == null) {
            methodsJavadoc = new HashMap<>();
        }
        return methodsJavadoc;
    }

}
