package com.github.kbuntrock.javadoc;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JavadocParser {

    private String projectPath;
    private boolean test;

    private Map<String, Object> parseMap = new HashMap<>();

    public JavadocParser(String projectPath) {
        this(projectPath, false);
    }

    public JavadocParser(String projectPath, boolean test) {
        this.projectPath = projectPath;
        this.test = test;
    }

    public void analyseClass(Class<?> clazz) throws IOException {
        Path path = Path.of(projectPath, getProjectClassPath(clazz));
        if(path.toFile().exists()) {
            CompilationUnit compilationUnit = StaticJavaParser.parse(path);
        }

        System.out.println("");
    }

    private String[] getProjectClassPath(Class<?> clazz) {
        String[] classPath = clazz.getCanonicalName().split("\\.");
        classPath[classPath.length - 1] = classPath[classPath.length - 1] + ".java";
        String[] classPathInProject = new String[classPath.length + 3];
        System.arraycopy(classPath, 0, classPathInProject, 3, classPath.length);
        classPathInProject[0] = "src";
        if(test) {
            classPathInProject[1] = "test";
        } else {
            classPathInProject[1] = "main";
        }
        classPathInProject[2] = "java";
        return classPathInProject;
    }
}
