package com.github.kbuntrock.javadoc;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * Pertinent comment types for the documentation generation.
 *
 * @author Kevin Buntrock
 */
public enum CommentType {
    // Method comment
    METHOD,
    // Class comment
    CLASS,
    // Field comment
    FIELD,
    // Other comment (constructor, orphan, ...)
    OTHER;

    public static CommentType fromNode(Node node) {
        if (node instanceof MethodDeclaration) {
            return METHOD;
        }
        if (node instanceof ClassOrInterfaceDeclaration || node instanceof EnumDeclaration) {
            return CLASS;
        }
        if (node instanceof FieldDeclaration) {
            return FIELD;
        }
        return OTHER;
    }
}


