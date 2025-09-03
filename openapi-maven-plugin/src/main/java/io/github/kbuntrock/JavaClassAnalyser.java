package io.github.kbuntrock;

import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.CommonApiConfiguration;
import io.github.kbuntrock.configuration.library.reader.AstractLibraryReader;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.model.annotation.OperationAnnotationInfo;
import io.github.kbuntrock.utils.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

/**
 * Analyse a java class in light of an api configuration object
 */
public class JavaClassAnalyser {

	private final Log logger = Logger.INSTANCE.getLogger();

	private final List<Pair<Pattern, Pattern>> whiteListPatterns = new ArrayList<>();
	private final List<Pair<Pattern, Pattern>> blackListPatterns = new ArrayList<>();

	private final AstractLibraryReader libraryReader;

	private final ScanResult classScanResult;

	public JavaClassAnalyser(final ApiConfiguration apiConfiguration, ScanResult classScanResult) {
		this.libraryReader = apiConfiguration.getLibrary().createReader(apiConfiguration);
		this.classScanResult = classScanResult;

		// Compilation of white list / black list patterns
		if(apiConfiguration.getWhiteList() != null) {
			for(final String whiteEntry : apiConfiguration.getWhiteList()) {
				final String[] regexArray = whiteEntry.split(CommonApiConfiguration.SEPARATOR_CLASS_METHOD);
				if(regexArray.length == 2) {
					if(StringUtils.isEmpty(regexArray[0])) {
						whiteListPatterns.add(Pair.of(null, Pattern.compile(regexArray[1])));
					} else {
						whiteListPatterns.add(Pair.of(Pattern.compile(regexArray[0]), Pattern.compile(regexArray[1])));
					}
				}
			}
		}
		if(apiConfiguration.getBlackList() != null) {
			for(final String blackEntry : apiConfiguration.getBlackList()) {
				final String[] regexArray = blackEntry.split(CommonApiConfiguration.SEPARATOR_CLASS_METHOD);
				if(regexArray.length == 2) {
					if(StringUtils.isEmpty(regexArray[0])) {
						blackListPatterns.add(Pair.of(null, Pattern.compile(regexArray[1])));
					} else {
						blackListPatterns.add(Pair.of(Pattern.compile(regexArray[0]), Pattern.compile(regexArray[1])));
					}
				}
			}
		}
	}

	private static String createTypeIdentifier(final String typeName) {

		String returnTypeName = typeName;
		final String[] toReplace = typeName.split("[<>,]");
		final List<Pair<String, String>> replacementList = new ArrayList<>();
		for(final String s : toReplace) {
			final String[] replacementArray = s.split("\\.");
			replacementList.add(Pair.of(s, replacementArray[replacementArray.length - 1]));
		}
		for(final Pair<String, String> pair : replacementList) {
			returnTypeName = returnTypeName.replace(pair.getLeft(), pair.getRight());
		}

		return returnTypeName;
	}

	public static String createMethodIdentifier(final Method method) {
		return Arrays.stream(method.getParameters())
				.map(p -> StringUtils.defaultString(p.getType().getSimpleName()))
				.collect(joining(", ", method.getName() + "(", ")"));
	}

	/**
	 * Create a Tag from a java class containing REST mapping functions
	 *
	 * @param clazz a REST controller class
	 * @return an tag (if there is at least one declared endpoint)
	 * @throws MojoFailureException
	 */
	public Optional<Tag> getTagFromClass(final Class<?> clazz) throws MojoFailureException {
		final Tag tag = new Tag(clazz);
		logger.debug("Parsing tag : " + tag.getName());

		final MergedAnnotations mergedAnnotations = MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);

		// Read swagger tag annotation
		MergedAnnotation<Annotation> swaggerTag = mergedAnnotations.get("io.swagger.v3.oas.annotations.tags.Tag");
		if(swaggerTag.isPresent()) {
			final String tagName = swaggerTag.getString("name");
			if (!StringUtils.isEmpty(tagName)) {
				tag.setComputedName(tagName);
			}
			final String description = swaggerTag.getString("description");
			if (!StringUtils.isEmpty(description)) {
				tag.setDescription(description);
			}
		}


		final List<String> basePaths = libraryReader.readBasePaths(clazz, mergedAnnotations);

		for(final String basePath : basePaths) {
			parseEndpoints(tag, basePath, clazz);
		}

		if(tag.getEndpoints().isEmpty()) {
			// There was no valid endpoint to attach to this tag. Therefore, we don't keep track of it.
			return Optional.empty();
		} else {
			return Optional.of(tag);
		}

	}

	private void parseEndpoints(final Tag tag, final String basePath, final Class<?> clazz) throws MojoFailureException {

		logger.debug("Parsing endpoint " + clazz.getSimpleName());

		Set<Method> methods = classScanResult.getClassInfo(clazz.getCanonicalName())
			.getMethodInfo()
			.filter(methodInfo -> !methodInfo.isPrivate())
			.stream()
			.map(MethodInfo::loadClassAndGetMethod)
			.collect(toSet());

		for(final Method method : methods) {

			if(validateWhiteList(clazz, method) && validateBlackList(clazz, method)) {
				final MergedAnnotations mergedAnnotations = MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
				libraryReader.computeAnnotations(clazz, basePath, method, mergedAnnotations, tag);
			}
		}
	}

	private boolean validateWhiteList(final Class<?> clazz, final Method method) {

		if(!whiteListPatterns.isEmpty()) {
			for(final Pair<Pattern, Pattern> pair : whiteListPatterns) {
				boolean validateLeft = true;
				if(pair.getLeft() != null) {
					validateLeft = pair.getLeft().matcher(clazz.getCanonicalName()).matches();
				}
				if(validateLeft && pair.getRight().matcher(method.getName()).matches()) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	private boolean validateBlackList(final Class<?> clazz, final Method method) {
		if(!blackListPatterns.isEmpty()) {
			for(final Pair<Pattern, Pattern> pair : blackListPatterns) {
				boolean validateLeft = true;
				if(pair.getLeft() != null) {
					validateLeft = pair.getLeft().matcher(clazz.getCanonicalName()).matches();
				}
				if(validateLeft && pair.getRight().matcher(method.getName()).matches()) {
					return false;
				}
			}
		}
		return true;
	}

}
