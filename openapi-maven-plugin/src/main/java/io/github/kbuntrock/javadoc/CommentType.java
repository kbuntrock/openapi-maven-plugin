package io.github.kbuntrock.javadoc;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;

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
    // Enumeration value comment
    ENUM_VALUE,
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
        if (node instanceof EnumConstantDeclaration) {
            return ENUM_VALUE;
        }
        return OTHER;
    }
}


