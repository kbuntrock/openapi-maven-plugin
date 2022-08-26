package io.github.kbuntrock.javadoc;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import io.github.kbuntrock.utils.Logger;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JavadocParser {

    private static final String LOG_PREFIX = JavadocParser.class.getSimpleName() + " - ";

    private Map<String, ClassDocumentation> javadocMap = new HashMap<>();

    private List<File> filesToScan;

    private final Log logger = Logger.INSTANCE.getLogger();

    public JavadocParser(List<File> filesToScan) {
        this.filesToScan = filesToScan;
    }

    public void scan() {
        for (File file : filesToScan) {
            if (!file.exists()) {
                logger.warn(LOG_PREFIX + "Directory " + file.getAbsolutePath() + " does not exist.");
            } else if (!file.isDirectory()) {
                logger.warn(LOG_PREFIX + "File " + file.getAbsolutePath() + " is not a directory.");
            } else {
                try {
                    logger.info(LOG_PREFIX + "Scanning directory : " + file.getAbsolutePath());
                    exploreDirectory(file);
                } catch (FileNotFoundException e) {
                    logger.error(LOG_PREFIX + "Cannot read file " + file.getAbsolutePath());
                    throw new RuntimeException("Cannot read file", e);
                }
            }
        }
    }

    private void exploreDirectory(final File directory) throws FileNotFoundException {
        for (File child : directory.listFiles()) {
            if (child.isFile() && child.getName().endsWith(".java")) {
                exploreJavaFile(child);
            } else if (child.isDirectory()) {
                exploreDirectory(child);
            }
        }
    }

    private void exploreJavaFile(final File javaFile) throws FileNotFoundException {
        CompilationUnit compilationUnit = StaticJavaParser.parse(javaFile, StandardCharsets.UTF_8);
        JavadocVisitor visitor = new JavadocVisitor();
        visitor.visit(compilationUnit, null);
    }

    private class JavadocVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(JavadocComment comment, Object arg) {
            super.visit(comment, arg);
            CommentType type = CommentType.fromNode(comment.getCommentedNode().get());
            if (CommentType.OTHER != type) {
                Optional<ClassDocumentation> classDocumentation = findClassDocumentationForNode(comment.getCommentedNode().get());
                if (classDocumentation.isPresent()) {
                    Javadoc javadoc = comment.parse();
                    switch (type) {
                        case CLASS:
                            classDocumentation.get().setJavadoc(javadoc);
                            break;
                        case FIELD:
                            classDocumentation.get().getFieldsJavadoc().put(
                                    ((FieldDeclaration) comment.getCommentedNode().get()).getVariable(0).getNameAsString(), new JavadocWrapper(javadoc));
                            break;
                        case ENUM_VALUE:
                            // Save an enum constant as a field
                            classDocumentation.get().getFieldsJavadoc().put(
                                    ((EnumConstantDeclaration) comment.getCommentedNode().get()).getNameAsString(), new JavadocWrapper(javadoc));
                            break;
                        case METHOD:
                            MethodDeclaration methodDeclaration = (MethodDeclaration) comment.getCommentedNode().get();
                            StringBuilder sb = new StringBuilder();
                            sb.append(methodDeclaration.getType().asString());
                            sb.append("_");
                            sb.append(methodDeclaration.getName().asString());
                            for (Parameter parameter : methodDeclaration.getParameters()) {
                                sb.append("_");
                                sb.append(parameter.getType().asString());
                            }
                            classDocumentation.get().getMethodsJavadoc().put(sb.toString(), new JavadocWrapper(javadoc));
                            break;
                    }
                }
            }
        }
    }

    private Optional<ClassDocumentation> findClassDocumentationForNode(Node commentedNode) {
        ClassOrInterfaceDeclaration classDeclaration = null;
        if (commentedNode instanceof ClassOrInterfaceDeclaration) {
            classDeclaration = (ClassOrInterfaceDeclaration) commentedNode;
        } else if (commentedNode.hasParentNode() && commentedNode.getParentNode().get() instanceof ClassOrInterfaceDeclaration) {
            classDeclaration = (ClassOrInterfaceDeclaration) commentedNode.getParentNode().get();
        }
        if (classDeclaration != null) {
            final ClassOrInterfaceDeclaration dec = classDeclaration;
            return Optional.of(javadocMap.computeIfAbsent(dec.getFullyQualifiedName().get(),
                    key -> new ClassDocumentation(dec.getFullyQualifiedName().get(), dec.getName().asString())));
        }
        EnumDeclaration enumDeclaration = null;
        if (commentedNode instanceof EnumDeclaration) {
            enumDeclaration = (EnumDeclaration) commentedNode;
        } else if (commentedNode.hasParentNode() && commentedNode.getParentNode().get() instanceof EnumDeclaration) {
            enumDeclaration = (EnumDeclaration) commentedNode.getParentNode().get();
        }
        if (enumDeclaration != null) {
            final EnumDeclaration dec = enumDeclaration;
            return Optional.of(javadocMap.computeIfAbsent(dec.getFullyQualifiedName().get(),
                    key -> new ClassDocumentation(dec.getFullyQualifiedName().get(), dec.getName().asString())));
        }

        return Optional.empty();
    }

    private static String describe(Node node) {
        if (node instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) node;
            return "Method " + methodDeclaration.getDeclarationAsString();
        }
        if (node instanceof ConstructorDeclaration) {
            ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) node;
            return "Constructor " + constructorDeclaration.getDeclarationAsString();
        }
        if (node instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) node;
            if (classOrInterfaceDeclaration.isInterface()) {
                return "Interface " + classOrInterfaceDeclaration.getName();
            } else {
                return "Class " + classOrInterfaceDeclaration.getName();
            }
        }
        if (node instanceof EnumDeclaration) {
            EnumDeclaration enumDeclaration = (EnumDeclaration) node;
            return "Enum " + enumDeclaration.getName();
        }
        if (node instanceof FieldDeclaration) {
            FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
            List<String> varNames = fieldDeclaration.getVariables().stream().map(v -> v.getName().getId()).collect(Collectors.toList());
            return "Field " + String.join(", ", varNames);
        }
        return node.toString();
    }

//    private String[] getProjectClassPath(Class<?> clazz) {
//        String[] classPath = clazz.getCanonicalName().split("\\.");
//        classPath[classPath.length - 1] = classPath[classPath.length - 1] + ".java";
//        String[] classPathInProject = new String[classPath.length + 3];
//        System.arraycopy(classPath, 0, classPathInProject, 3, classPath.length);
//        classPathInProject[0] = "src";
//        if (test) {
//            classPathInProject[1] = "test";
//        } else {
//            classPathInProject[1] = "main";
//        }
//        classPathInProject[2] = "java";
//        return classPathInProject;
//    }


    public Map<String, ClassDocumentation> getJavadocMap() {
        return javadocMap;
    }
}
