package io.github.kbuntrock.javadoc;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.utils.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.maven.plugin.logging.Log;

public class JavadocParser {

	private static final String LOG_PREFIX = JavadocParser.class.getSimpleName() + " - ";
	private final JavaParser javaParser;
	private final Log logger = Logger.INSTANCE.getLogger();
	private final Map<String, ClassDocumentation> javadocMap = new HashMap<>();
	private final List<File> filesToScan;
	private final JavadocVisitor visitor = new JavadocVisitor();

	private final boolean debugScan;

	public JavadocParser(final List<File> filesToScan, final JavadocConfiguration javadocConfiguration) {

		this.filesToScan = filesToScan;
		final ParserConfiguration parserConfiguration = new ParserConfiguration();

		parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);

		Charset charset = StandardCharsets.UTF_8;
		if(Charset.isSupported(javadocConfiguration.getEncoding())) {
			charset = Charset.forName(javadocConfiguration.getEncoding());
		} else {
			logger.warn("Encoding " + javadocConfiguration.getEncoding() + " is not supported. UTF-8 will be used instead.");
			logger.warn("Supported encoding on this JVM are : " + Charset.availableCharsets().keySet().stream()
				.collect(Collectors.joining(", ")));
		}
		parserConfiguration.setCharacterEncoding(charset);
		debugScan = javadocConfiguration.isDebugScan();
		javaParser = new JavaParser(parserConfiguration);
	}

	private static String describe(final Node node) {
		if(node instanceof MethodDeclaration) {
			final MethodDeclaration methodDeclaration = (MethodDeclaration) node;
			return "Method " + methodDeclaration.getDeclarationAsString();
		}
		if(node instanceof ConstructorDeclaration) {
			final ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) node;
			return "Constructor " + constructorDeclaration.getDeclarationAsString();
		}
		if(node instanceof ClassOrInterfaceDeclaration) {
			final ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) node;
			if(classOrInterfaceDeclaration.isInterface()) {
				return "Interface " + classOrInterfaceDeclaration.getName();
			} else {
				return "Class " + classOrInterfaceDeclaration.getName();
			}
		}
		if(node instanceof EnumDeclaration) {
			final EnumDeclaration enumDeclaration = (EnumDeclaration) node;
			return "Enum " + enumDeclaration.getName();
		}
		if(node instanceof FieldDeclaration) {
			final FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
			final List<String> varNames = fieldDeclaration.getVariables().stream().map(v -> v.getName().getId())
				.collect(Collectors.toList());
			return "Field " + String.join(", ", varNames);
		}
		return node.toString();
	}

	public void scan() {
		for(final File file : filesToScan) {
			if(!file.exists()) {
				logger.warn(LOG_PREFIX + "Directory " + file.getAbsolutePath() + " does not exist.");
			} else if(!file.isDirectory()) {
				logger.warn(LOG_PREFIX + "File " + file.getAbsolutePath() + " is not a directory.");
			} else {
				try {
					logger.info(LOG_PREFIX + "Scanning directory : " + file.getAbsolutePath());
					exploreDirectory(file);
				} catch(final FileNotFoundException e) {
					logger.error(LOG_PREFIX + "Cannot read file " + file.getAbsolutePath());
					throw new RuntimeException("Cannot read file", e);
				}
			}
		}
		printDebug();
	}

	private void printDebug() {
		if(debugScan) {
			logger.debug("-------- PRINT JAVADOC SCAN RESULTS ----------");
			for(final ClassDocumentation classDocumentation : javadocMap.values()) {
				logger.debug("Class documentation for : " + classDocumentation.getCompleteName());
				logger.debug("Description : " + classDocumentation.getDescription());
				if(!classDocumentation.getMethodsJavadoc().isEmpty()) {
					for(final Entry<String, JavadocWrapper> entry : classDocumentation.getMethodsJavadoc().entrySet()) {
						logger.debug("Method doc for : " + entry.getKey());
						logger.debug("Description : " + entry.getValue().getDescription());
						entry.getValue().printParameters();
						entry.getValue().printReturn();
					}
				}

			}
		}
	}

	private void exploreDirectory(final File directory) throws FileNotFoundException {
		for(final File child : directory.listFiles()) {
			if(child.isFile() && child.getName().endsWith(".java")) {
				exploreJavaFile(child);
			} else if(child.isDirectory()) {
				exploreDirectory(child);
			}
		}
	}

	private void exploreJavaFile(final File javaFile) throws FileNotFoundException {

		try {
			final ParseResult<CompilationUnit> parseResult = javaParser.parse(javaFile);
			if(!parseResult.isSuccessful()) {
				throw new ParseProblemException(parseResult.getProblems());
			}
			final CompilationUnit compilationUnit = parseResult.getResult().get();
			visitor.visit(compilationUnit, null);
		} catch(final ParseProblemException ex) {
			Logger.INSTANCE.getLogger().warn("Error while parsing javadoc of file " + javaFile.getName() + " -> "
				+ ex.getMessage());
		}

	}

	private Optional<ClassDocumentation> findClassDocumentationForNode(final Node commentedNode) {
		ClassOrInterfaceDeclaration classDeclaration = null;
		if(commentedNode instanceof ClassOrInterfaceDeclaration) {
			classDeclaration = (ClassOrInterfaceDeclaration) commentedNode;
		} else if(commentedNode.hasParentNode() && commentedNode.getParentNode().get() instanceof ClassOrInterfaceDeclaration) {
			classDeclaration = (ClassOrInterfaceDeclaration) commentedNode.getParentNode().get();
		}
		if(classDeclaration != null) {
			final ClassOrInterfaceDeclaration dec = classDeclaration;
			return Optional.of(javadocMap.computeIfAbsent(dec.getFullyQualifiedName().get(),
				key -> new ClassDocumentation(dec.getFullyQualifiedName().get(), dec.getName().asString())));
		}
		if(commentedNode instanceof RecordDeclaration) {
			final String fullName = ((RecordDeclaration) commentedNode).getFullyQualifiedName().get();
			final RecordDeclaration dec = (RecordDeclaration) commentedNode;
			return Optional.of(javadocMap.computeIfAbsent(fullName,
				key -> new ClassDocumentation(fullName, dec.getName().asString())));
		}

		EnumDeclaration enumDeclaration = null;
		if(commentedNode instanceof EnumDeclaration) {
			enumDeclaration = (EnumDeclaration) commentedNode;
		} else if(commentedNode.hasParentNode() && commentedNode.getParentNode().get() instanceof EnumDeclaration) {
			enumDeclaration = (EnumDeclaration) commentedNode.getParentNode().get();
		}
		if(enumDeclaration != null) {
			final EnumDeclaration dec = enumDeclaration;
			return Optional.of(javadocMap.computeIfAbsent(dec.getFullyQualifiedName().get(),
				key -> new ClassDocumentation(dec.getFullyQualifiedName().get(), dec.getName().asString())));
		}

		return Optional.empty();
	}

	public Map<String, ClassDocumentation> getJavadocMap() {
		return javadocMap;
	}

	private class JavadocVisitor extends VoidVisitorAdapter {

		@Override
		public void visit(final JavadocComment comment, final Object arg) {
			super.visit(comment, arg);
			final CommentType type = CommentType.fromNode(comment.getCommentedNode().get());
			if(CommentType.OTHER != type) {
				final Optional<ClassDocumentation> classDocumentation = findClassDocumentationForNode(comment.getCommentedNode().get());
				if(classDocumentation.isPresent()) {
					final Javadoc javadoc = comment.parse();
					switch(type) {
						case CLASS:
							classDocumentation.get().setJavadoc(javadoc);
							break;
						case FIELD:
							classDocumentation.get().getFieldsJavadoc().put(
								((FieldDeclaration) comment.getCommentedNode().get()).getVariable(0).getNameAsString(),
								new JavadocWrapper(javadoc));
							break;
						case RECORD:
							classDocumentation.get().setJavadoc(javadoc);
							for(final JavadocBlockTag parameter : javadoc.getBlockTags()) {
								if(!parameter.getContent().isEmpty()) {
									classDocumentation.get().getFieldsJavadoc().put(parameter.getName().get(),
										new JavadocWrapper(new Javadoc(parameter.getContent())));
								}
							}
							break;
						case ENUM_VALUE:
							// Save an enum constant as a field
							classDocumentation.get().getFieldsJavadoc().put(
								((EnumConstantDeclaration) comment.getCommentedNode().get()).getNameAsString(),
								new JavadocWrapper(javadoc));
							break;
						case METHOD:
							final MethodDeclaration methodDeclaration = (MethodDeclaration) comment.getCommentedNode().get();
							final StringBuilder sb = new StringBuilder();
							sb.append(methodDeclaration.getType().asString());
							sb.append("_");
							sb.append(methodDeclaration.getName().asString());
							for(final Parameter parameter : methodDeclaration.getParameters()) {
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
}
