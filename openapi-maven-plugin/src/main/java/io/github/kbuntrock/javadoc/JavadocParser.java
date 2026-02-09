package io.github.kbuntrock.javadoc;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.MarkdownComment;
import com.github.javaparser.ast.comments.TraditionalJavadocComment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.context.ProjectContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Lightweight parser that scans source directories for Java files and collects Javadoc
 * into a structured map keyed by fully qualified class name.
 * <p>
 * Responsibilities:
 * - Configure JavaParser (language level, encoding) and traverse the file tree.
 * - Visit classes, records, enums, fields, methods, and enum constants to capture documentation.
 * - Store the results in a {@code Map<String, ClassDocumentation>} for downstream consumers.
 * <p>
 * Notes:
 * - Both traditional Javadoc and Markdown-style comments are supported.
 * - Parsing errors are logged and do not abort the entire scan.
 * - When debug is enabled, a summary of the parsed results is logged.
 */
public class JavadocParser {

	private static final String LOG_PREFIX = JavadocParser.class.getSimpleName() + " - ";

	private final ProjectContext context;
	private final JavaParser javaParser;
	private final Map<String, ClassDocumentation> javadocMap = new HashMap<>();
	private final List<File> filesToScan;
	private final JavadocVisitor visitor = new JavadocVisitor();

	private final boolean debugScan;

	public JavadocParser(final ProjectContext context, final List<File> filesToScan,
		final JavadocConfiguration javadocConfiguration) {
		this.context = context;
		this.filesToScan = filesToScan;
		final ParserConfiguration parserConfiguration = new ParserConfiguration();

		// Choose a language level compatible with modern Java syntax in the scanned project.
		parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.BLEEDING_EDGE);

		// Honor configured encoding, with robustness against unsupported values.
		Charset charset = StandardCharsets.UTF_8;
		if(Charset.isSupported(javadocConfiguration.getEncoding())) {
			charset = Charset.forName(javadocConfiguration.getEncoding());
		} else {
			context.getLogger()
				.warn("Encoding " + javadocConfiguration.getEncoding() + " is not supported. UTF-8 will be used instead.");
			context.getLogger()
				.warn("Supported encoding on this JVM are : " + String.join(", ", Charset.availableCharsets().keySet()));
		}
		parserConfiguration.setCharacterEncoding(charset);
		debugScan = javadocConfiguration.isDebugScan();
		javaParser = new JavaParser(parserConfiguration);
	}

	/**
	 * Walk through all configured source roots, parse Java files, and populate {@link #javadocMap}.
	 * Non-fatal failures are logged and the scan continues.
	 */
	public void scan() {
		for(final File file : filesToScan) {
			if(!file.exists()) {
				context.getLogger().warn(LOG_PREFIX + "Directory " + file.getAbsolutePath() + " does not exist.");
			} else if(!file.isDirectory()) {
				context.getLogger().warn(LOG_PREFIX + "File " + file.getAbsolutePath() + " is not a directory.");
			} else {
				try {
					context.getLogger().info(LOG_PREFIX + "Scanning directory : " + file.getAbsolutePath());
					exploreDirectory(file);
				} catch(final FileNotFoundException e) {
					context.getLogger().error(LOG_PREFIX + "Cannot read file " + file.getAbsolutePath());
					throw new RuntimeException("Cannot read file", e);
				}
			}
		}
		printDebug();
	}

	/**
	 * Print a human-readable dump of the collected documentation when debug mode is enabled.
	 */
	private void printDebug() {
		if(debugScan) {
			context.getLogger().debug("-------- PRINT JAVADOC SCAN RESULTS ----------");
			for(final ClassDocumentation classDocumentation : javadocMap.values()) {
				context.getLogger().debug("Class documentation for : " + classDocumentation.getCompleteName());
				context.getLogger().debug("Summary : " + classDocumentation.getSummary());
				context.getLogger().debug("Description : " + classDocumentation.getDescription());
				if(!classDocumentation.getMethodsJavadocByIdentifier().isEmpty()) {
					for(final Entry<String, JavadocWrapper> entry : classDocumentation.getMethodsJavadocByIdentifier()
						.entrySet()) {
						context.getLogger().debug("Method doc for : " + entry.getKey());
						context.getLogger().debug("Summary : " + entry.getValue().getSummary());
						context.getLogger().debug("Description : " + entry.getValue().getDescription());
						entry.getValue().printParameters(context.getLogger());
						entry.getValue().printReturn(context.getLogger());
					}
				}

			}
		}
	}

	/**
	 * Recursively visit the directory and parse all {@code .java} files.
	 */
	private void exploreDirectory(final File directory) throws FileNotFoundException {
		for(final File child : directory.listFiles()) {
			if(child.isFile() && child.getName().endsWith(".java")) {
				exploreJavaFile(child);
			} else if(child.isDirectory()) {
				exploreDirectory(child);
			}
		}
	}

	/**
	 * Parse a Java source file with JavaParser and feed the AST to the visitor that extracts Javadoc.
	 * Errors are logged and do not interrupt the overall scan.
	 */
	private void exploreJavaFile(final File javaFile) throws FileNotFoundException {

		try {
			final ParseResult<CompilationUnit> parseResult = javaParser.parse(javaFile);
			if(!parseResult.isSuccessful()) {
				throw new ParseProblemException(parseResult.getProblems());
			}
			final CompilationUnit compilationUnit = parseResult.getResult().get();
			visitor.visit(compilationUnit, null);
		} catch(final ParseProblemException ex) {
			context.getLogger().warn("Error while parsing javadoc of file " + javaFile.getName() + " -> "
				+ ex.getMessage());
		}

	}

	/**
	 * Try to resolve or derive the enclosing class/record/enum for a node bearing a Javadoc comment.
	 * Returns the corresponding {@link ClassDocumentation} container, creating it if absent.
	 */
	private Optional<ClassDocumentation> findClassDocumentationForNode(final Node commentedNode) {
		ClassOrInterfaceDeclaration classDeclaration = null;
		if(commentedNode instanceof ClassOrInterfaceDeclaration) {
			classDeclaration = (ClassOrInterfaceDeclaration) commentedNode;
		} else if(commentedNode.hasParentNode() && commentedNode.getParentNode().get() instanceof ClassOrInterfaceDeclaration) {
			classDeclaration = (ClassOrInterfaceDeclaration) commentedNode.getParentNode().get();
		}
		if(classDeclaration != null && classDeclaration.getFullyQualifiedName().isPresent()) {
			final ClassOrInterfaceDeclaration dec = classDeclaration;
			return Optional.of(javadocMap.computeIfAbsent(dec.getFullyQualifiedName().get(),
				key -> new ClassDocumentation(dec.getFullyQualifiedName().get(), dec.getName().asString())));
		}
		if(commentedNode instanceof RecordDeclaration
			&& ((RecordDeclaration) commentedNode).getFullyQualifiedName().isPresent()) {
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
		if(enumDeclaration != null && enumDeclaration.getFullyQualifiedName().isPresent()) {
			final EnumDeclaration dec = enumDeclaration;
			return Optional.of(javadocMap.computeIfAbsent(dec.getFullyQualifiedName().get(),
				key -> new ClassDocumentation(dec.getFullyQualifiedName().get(), dec.getName().asString())));
		}

		return Optional.empty();
	}

	/**
	 * The parsed Javadoc map, keyed by fully qualified class name.
	 */
	public Map<String, ClassDocumentation> getJavadocMap() {
		return javadocMap;
	}

	/**
	 * AST visitor that inspects comment nodes and stores their parsed Javadoc into {@link ClassDocumentation}.
	 * Supports traditional and markdown Javadoc comment syntaxes and handles multiple element kinds.
	 */
	private class JavadocVisitor extends VoidVisitorAdapter {

		@Override
		public void visit(final TraditionalJavadocComment comment, final Object arg) {
			super.visit(comment, arg);
			visitInternal(comment, arg);
		}

		@Override
		public void visit(final MarkdownComment comment, final Object arg) {
			super.visit(comment, arg);
			visitInternal(comment, arg);
		}

		/**
		 * Common handling that classifies the commented element and routes the parsed Javadoc
		 * to the appropriate slot in {@link ClassDocumentation}.
		 */
		private void visitInternal(final JavadocComment comment, final Object arg) {
			final CommentType type = CommentType.fromNode(comment.getCommentedNode().get());
			if(CommentType.OTHER != type) {
				final Optional<ClassDocumentation> classDocumentation = findClassDocumentationForNode(
					comment.getCommentedNode().get());
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
							// Promote record component tags (@param-like) as field docs with the component name as key.
							for(final JavadocBlockTag parameter : javadoc.getBlockTags()) {
								if(!parameter.getContent().isEmpty() && parameter.getName().isPresent()) {
									// Parameters with no name are not taken into account (as for class) since they are not translated into documentation
									// ex of no name parameter : @author
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
							JavadocWrapper wrapper = new JavadocWrapper(javadoc);
							// Use the method signature as a stable identifier (covers overloading).
							classDocumentation.get().getMethodsJavadocByIdentifier()
								.put(methodDeclaration.getSignature().toString(), wrapper);
							break;
					}
				}
			}
		}
	}
}
