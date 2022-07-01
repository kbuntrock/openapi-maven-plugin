package com.github.kbuntrock.javadoc;

import com.github.javaparser.javadoc.Javadoc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClassDocumentation {

    private String completeName;
    private String simpleName;
    private JavadocWrapper javadocWrapper;

    private Map<String, JavadocWrapper> fieldsJavadoc;
    private Map<String, JavadocWrapper> methodsJavadoc;

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

    public JavadocWrapper getJavadoc() {
        return javadocWrapper;
    }

    public void setJavadoc(Javadoc javadoc) {
        this.javadocWrapper = new JavadocWrapper(javadoc);
    }

    public Map<String, JavadocWrapper> getFieldsJavadoc() {
        if (fieldsJavadoc == null) {
            fieldsJavadoc = new HashMap<>();
        }
        return fieldsJavadoc;
    }

    public Map<String, JavadocWrapper> getMethodsJavadoc() {
        if (methodsJavadoc == null) {
            methodsJavadoc = new HashMap<>();
        }
        return methodsJavadoc;
    }

    public Optional<String> getDescription() {
        if (javadocWrapper != null) {
            return javadocWrapper.getDescription();
        }
        return Optional.empty();
    }

}
