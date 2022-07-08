package com.github.kbuntrock.javadoc;

import com.github.javaparser.javadoc.Javadoc;
import com.github.kbuntrock.SpringClassAnalyser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ClassDocumentation {

    private String completeName;
    private String simpleName;
    private JavadocWrapper javadocWrapper;

    private Map<String, JavadocWrapper> fieldsJavadoc;
    private Map<String, JavadocWrapper> methodsJavadoc;

    private boolean inheritanceEnhancementIsDone = false;
    private Class<?> javaClass;

    public ClassDocumentation(String completeName, String simpleName) {
        this.completeName = completeName;
        this.simpleName = simpleName;
    }

    public void inheritanceEnhancement(final Class<?> javaClass, final EnhancementType enhancementType) {
        if (inheritanceEnhancementIsDone) {
            return;
        }
        inheritanceEnhancementIsDone = true;
        this.javaClass = javaClass;

        // TODO : Do the same concept for main class description
        Map<String, ClassDocumentation> javadocMap = JavadocMap.INSTANCE.getJavadocMap();
        Set<ClassDocumentation> parentsClassDocumentation = new HashSet<>();
        if (javaClass.getSuperclass() != null && Object.class != javaClass.getSuperclass()) {
            listInheritance(javadocMap, parentsClassDocumentation, javaClass.getSuperclass());
        }
        for (Class<?> javaInterface : javaClass.getInterfaces()) {
            listInheritance(javadocMap, parentsClassDocumentation, javaInterface);
        }
        if (parentsClassDocumentation.isEmpty()) {
            return;
        }

        if (EnhancementType.FIELDS == enhancementType || EnhancementType.BOTH == enhancementType) {
            Field[] fields = javaClass.getFields();
            if (fields.length > 0) {
                List<JavadocEntry> fieldEntriesToAddOrReplace = new ArrayList<>();
                for (Field field : fields) {
                    JavadocWrapper currentJavadoc = fieldsJavadoc.get(field.getName());
                    if (currentJavadoc != null) {
                        currentJavadoc.sort();
                    }
                    if (currentJavadoc == null || currentJavadoc.isInheritTagFound()) {
                        for (ClassDocumentation parentClass : parentsClassDocumentation) {
                            JavadocWrapper parentJavadoc = parentClass.fieldsJavadoc.get(field.getName());
                            if (parentJavadoc != null) {
                                parentJavadoc.sort();
                            }
                            if (parentJavadoc != null && !parentJavadoc.isInheritTagFound()) {
                                fieldEntriesToAddOrReplace.add(new JavadocEntry(field.getName(), parentJavadoc));
                                break;
                            }
                        }
                    }

                }
                for (JavadocEntry entry : fieldEntriesToAddOrReplace) {
                    fieldsJavadoc.put(entry.name, entry.javadocWrapper);
                }
            }
        }

        if (EnhancementType.METHODS == enhancementType || EnhancementType.BOTH == enhancementType) {

            Method[] methods = javaClass.getMethods();
            if (methods.length > 0) {

                List<JavadocEntry> methodEntriesToAddOrReplace = new ArrayList<>();
                for (Method method : methods) {
                    String methodIdentifier = SpringClassAnalyser.createIdentifier(method);
                    JavadocWrapper currentJavadoc = methodsJavadoc.get(methodIdentifier);
                    if (currentJavadoc != null) {
                        currentJavadoc.sort();
                    }
                    if (currentJavadoc == null || currentJavadoc.isInheritTagFound()) {
                        for (ClassDocumentation parentClass : parentsClassDocumentation) {
                            JavadocWrapper parentJavadoc = parentClass.methodsJavadoc.get(methodIdentifier);
                            if (parentJavadoc != null) {
                                parentJavadoc.sort();
                            }
                            if (parentJavadoc != null && !parentJavadoc.isInheritTagFound()) {
                                methodEntriesToAddOrReplace.add(new JavadocEntry(methodIdentifier, parentJavadoc));
                                break;
                            }
                        }
                    }

                }
                for (JavadocEntry entry : methodEntriesToAddOrReplace) {
                    methodsJavadoc.put(entry.name, entry.javadocWrapper);
                }
            }
        }
    }


    private void listInheritance(Map<String, ClassDocumentation> javadocMap, Set<ClassDocumentation> documentationSet, Class<?> classToAdd) {
        ClassDocumentation classDocumentation = javadocMap.get(classToAdd.getCanonicalName());
        if (classDocumentation != null) {
            documentationSet.add(classDocumentation);
            if (classToAdd.getSuperclass() != null && Object.class != classToAdd.getSuperclass()) {
                listInheritance(javadocMap, documentationSet, classToAdd.getSuperclass());
            }
            for (Class<?> javaInterface : classToAdd.getInterfaces()) {
                listInheritance(javadocMap, documentationSet, javaInterface);
            }
        }
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

    private class JavadocEntry {
        protected String name;
        protected JavadocWrapper javadocWrapper;

        public JavadocEntry(String name, JavadocWrapper javadocWrapper) {
            this.name = name;
            this.javadocWrapper = javadocWrapper;
        }
    }

    public enum EnhancementType {
        FIELDS, METHODS, BOTH;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassDocumentation that = (ClassDocumentation) o;
        return Objects.equals(completeName, that.completeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(completeName);
    }
}
