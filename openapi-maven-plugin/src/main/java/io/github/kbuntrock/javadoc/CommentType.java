package io.github.kbuntrock.javadoc;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.RecordDeclaration;

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
	// Record comment
	RECORD,
	// Field comment
	FIELD,
	// Enumeration value comment
	ENUM_VALUE,
	// Other comment (constructor, orphan, ...)
	OTHER;

	public static CommentType fromNode(final Node node) {
		if(node instanceof MethodDeclaration) {
			return METHOD;
		}
		if(node instanceof RecordDeclaration) {
			return RECORD;
		}
		if(node instanceof ClassOrInterfaceDeclaration || node instanceof EnumDeclaration) {
			return CLASS;
		}
		if(node instanceof FieldDeclaration) {
			return FIELD;
		}
		if(node instanceof EnumConstantDeclaration) {
			return ENUM_VALUE;
		}
		return OTHER;
	}
}


