package io.github.kbuntrock;

import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.CommonApiConfiguration;
import io.github.kbuntrock.configuration.library.reader.AbstractLibraryReader;
import io.github.kbuntrock.context.ApiContext;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.reflection.annotation.MergedAnnotation;
import io.github.kbuntrock.reflection.annotation.MergedAnnotations;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import io.github.kbuntrock.yaml.model.SecurityRequirement;
import io.github.kbuntrock.yaml.model.SecurityScheme;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.plugin.MojoFailureException;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

/**
 * Analyse a Java class in light of an API configuration to discover REST endpoints
 * and translate them into OpenAPI domain objects (e.g., {@link Tag}).
 * <p>
 * Responsibilities:
 * - Resolve framework/library-specific annotations via {@link AbstractLibraryReader}.
 * - Apply whitelist/blacklist filters (regex) on classes and methods.
 * - Use ClassGraph {@link ScanResult} to access non-private methods.
 * - Aggregate merged annotations (class and method levels) to build OpenAPI data.
 * <p>
 * Notes on filtering:
 * - Whitelist and blacklist entries are provided as "classRegex|methodRegex".
 * - The class part can be empty to match any class for a given method pattern.
 * - Whitelist is an allow-list: if non-empty, only matching pairs are processed.
 * - Blacklist is a deny-list: any match is excluded.
 */
public class JavaClassAnalyser {

	/** Plugin execution context (logger, helpers, global state). */
	private final ApiContext context;

	/**
	 * Compiled whitelist patterns of (classPattern, methodPattern).
	 * When non-empty, a method must match at least one entry to be processed.
	 */
	private final List<Pair<Pattern, Pattern>> whiteListPatterns = new ArrayList<>();
	/**
	 * Compiled blacklist patterns of (classPattern, methodPattern).
	 * When non-empty, any matching method is excluded from processing.
	 */
	private final List<Pair<Pattern, Pattern>> blackListPatterns = new ArrayList<>();

	/** Strategy to read framework/library-specific annotations (Spring, JAX-RS, etc.). */
	private final AbstractLibraryReader libraryReader;

	/** ClassGraph scan result used to list and load class methods. */
	private final ScanResult classScanResult;

	/**
	 * Build an analyser for a given API configuration.
	 *
	 * @param context
	 *            the plugin context
	 * @param apiConfiguration
	 *            user configuration (filters, library, etc.)
	 * @param classScanResult
	 *            ClassGraph scan result for class/method discovery
	 * @param openApiTypeResolver
	 *            type resolution helper for OpenAPI schema generation
	 */
	public JavaClassAnalyser(final ApiContext context, final ApiConfiguration apiConfiguration, ScanResult classScanResult,
		final OpenApiTypeResolver openApiTypeResolver) {
		this.context = context;
		this.libraryReader = apiConfiguration.getLibrary().createReader(context, apiConfiguration, openApiTypeResolver);
		this.classScanResult = classScanResult;

		// Compile whitelist/blacklist patterns once for fast matching during analysis.
		// Expected entry format: "<classRegex>|<methodRegex>". If classRegex is empty,
		// the rule applies to any class (methodRegex still must match).
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

	/**
	 * Create a human-readable signature identifier using the method name and the simple names of its parameter types.
	 * Example: myMethod(String, Integer)
	 */
	public static String createMethodIdentifier(final Method method) {
		return Arrays.stream(method.getParameters())
			.map(p -> StringUtils.defaultString(p.getType().getSimpleName()))
			.collect(joining(", ", method.getName() + "(", ")"));
	}

	/**
	 * Create a {@link Tag} from a Java class containing REST mapping functions.
	 * The tag can be enriched by Swagger's @Tag annotation (name/description) if present.
	 * Endpoints discovered under the class' base path(s) are attached to the tag.
	 *
	 * @param clazz
	 *            a REST controller class
	 * @return an optional tag (present only if at least one endpoint has been discovered)
	 * @throws MojoFailureException
	 *             if an error occurs while reading annotations
	 */
	public Optional<Tag> getTagFromClass(final Class<?> clazz) throws MojoFailureException {
		final Tag tag = new Tag(clazz);
		context.getLogger().debug("Parsing tag : " + tag.getName());

		final MergedAnnotations mergedAnnotations = context.getMergeAnnotationsHelper().from(clazz);

		// Read Swagger @Tag annotation (optional) for name/description overrides.
		MergedAnnotation swaggerTag = mergedAnnotations.get("io.swagger.v3.oas.annotations.tags.Tag");
		if(swaggerTag.isPresent()) {
			final String tagName = swaggerTag.getString("name");
			if(!StringUtils.isEmpty(tagName)) {
				tag.setComputedName(tagName);
			}
			final String description = swaggerTag.getString("description");
			if(!StringUtils.isEmpty(description)) {
				tag.setDescription(description);
			}
		}

		readSecuritySchemes(mergedAnnotations, tag.getSecuritySchemes());
		readSecurityRequirements(mergedAnnotations, tag.getSecurityRequirements());

		// Base paths come from the library reader (framework-specific resolution).
		final List<String> basePaths = libraryReader.readBasePaths(clazz, mergedAnnotations);

		// Parse and attach endpoints for each base path.
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

	/**
	 * Reads security schemes from a class that is not necessarily a tag/controller, and adds them to the TagLibrary.
	 */
	public void readSecuritySchemesFromClass(final Class<?> clazz, final TagLibrary library) throws MojoFailureException {
		context.getLogger().debug("Parsing security schemes from class : " + clazz.getSimpleName());
		final MergedAnnotations mergedAnnotations = context.getMergeAnnotationsHelper().from(clazz);
		Set<SecurityScheme> foundSchemes = new HashSet<>();
		readSecuritySchemes(mergedAnnotations, foundSchemes);
		for(SecurityScheme scheme : foundSchemes) {
			library.addSecurityScheme(scheme);
		}
	}

	/**
	 * Discover and process all non-private methods on the class that represent REST endpoints.
	 * Filtering is applied via whitelist/blacklist prior to delegating to {@link AbstractLibraryReader}
	 * for framework-specific annotation handling.
	 */
	private void parseEndpoints(final Tag tag, final String basePath, final Class<?> clazz) throws MojoFailureException {

		context.getLogger().debug("Parsing endpoint " + clazz.getSimpleName());

		// Use ClassGraph metadata to list and load non-private methods.
		Set<Method> methods = classScanResult.getClassInfo(clazz.getCanonicalName())
			.getMethodInfo()
			.filter(methodInfo -> !methodInfo.isPrivate())
			.stream()
			.map(MethodInfo::loadClassAndGetMethod)
			.collect(toSet());

		for(final Method method : methods) {

			// Apply whitelist then blacklist filters before processing method annotations.
			if(validateWhiteList(clazz, method) && validateBlackList(clazz, method)) {
				final MergedAnnotations mergedAnnotations = context.getMergeAnnotationsHelper().from(method);
				libraryReader.computeAnnotations(clazz, basePath, method, mergedAnnotations, tag);

				// Compute security requirements at endpoint level
				if(!tag.getEndpoints().isEmpty()) {
					// Assuming the library reader added newly created endpoints to the tag.
					// We find the ones that match this method's identifier. Since the reader might
					// add multiple endpoints per method (e.g. if requestMethods array has >1 method),
					// we iterate over the tag's endpoints to find those created from this method.
					final String methodIdentifier = JavaClassAnalyser.createMethodIdentifier(method);
					List<Endpoint> endpointsForMethod = tag.getEndpoints().stream()
						.filter(e -> methodIdentifier.equals(e.getIdentifier()))
						.collect(Collectors.toList());
					for(Endpoint endpoint : endpointsForMethod) {
						readSecurityRequirements(mergedAnnotations, endpoint.getSecurityRequirements());
					}
				}
			}
		}
	}

	/**
	 * Check whether a method is allowed by the whitelist. If the whitelist is empty,
	 * everything is implicitly allowed.
	 */
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

	/**
	 * Check whether a method is excluded by the blacklist. If the blacklist is empty,
	 * nothing is explicitly excluded.
	 */
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

	private void readSecuritySchemes(MergedAnnotations mergedAnnotations, Set<SecurityScheme> securitySchemes) {
		Map<String, SecurityScheme> map = new LinkedHashMap<>();
		MergedAnnotation schemeAnnotation = mergedAnnotations.get("io.swagger.v3.oas.annotations.security.SecurityScheme");
		if(schemeAnnotation.isPresent()) {
			SecurityScheme s = createSecurityScheme(schemeAnnotation);
			map.put(s.getName(), s);
		}
		MergedAnnotation schemesAnnotation = mergedAnnotations.get("io.swagger.v3.oas.annotations.security.SecuritySchemes");
		if(schemesAnnotation.isPresent()) {
			for(MergedAnnotation req : schemesAnnotation.getAnnotationArray("value")) {
				SecurityScheme s = createSecurityScheme(req);
				map.put(s.getName(), s);
			}
		}
		securitySchemes.addAll(map.values());
	}

	private SecurityScheme createSecurityScheme(MergedAnnotation mergedAnnotation) {
		SecurityScheme scheme = new SecurityScheme();
		Optional<Object> typeOpt = mergedAnnotation.getValue("type");
		if(typeOpt.isPresent()) {
			// type is an enum, so we use its toString() method
			scheme.setType(typeOpt.get().toString());
		}
		Optional<Object> inOpt = mergedAnnotation.getValue("in");
		if(inOpt.isPresent()) {
			// in is an enum, so we use its toString() method
			// only set it if it's not the DEFAULT or empty enum value
			String inStr = inOpt.get().toString();
			if(!StringUtils.isEmpty(inStr) && !"DEFAULT".equals(inStr)) {
				scheme.setIn(inStr);
			}
		}

		String name = mergedAnnotation.getString("name");
		if(!StringUtils.isEmpty(name)) {
			scheme.setName(name);
		}
		String description = mergedAnnotation.getString("description");
		if(!StringUtils.isEmpty(description)) {
			scheme.setDescription(description);
		}
		String schemeStr = mergedAnnotation.getString("scheme");
		if(!StringUtils.isEmpty(schemeStr)) {
			scheme.setScheme(schemeStr);
		}
		String bearerFormat = mergedAnnotation.getString("bearerFormat");
		if(!StringUtils.isEmpty(bearerFormat)) {
			scheme.setBearerFormat(bearerFormat);
		}
		String openIdConnectUrl = mergedAnnotation.getString("openIdConnectUrl");
		if(!StringUtils.isEmpty(openIdConnectUrl)) {
			scheme.setOpenIdConnectUrl(openIdConnectUrl);
		}
		return scheme;
	}

	private void readSecurityRequirements(MergedAnnotations mergedAnnotations, List<SecurityRequirement> securityRequirements) {
		MergedAnnotation requirementAnnotation = mergedAnnotations
			.get("io.swagger.v3.oas.annotations.security.SecurityRequirement");
		if(requirementAnnotation.isPresent()) {
			securityRequirements.add(createSecurityRequirement(requirementAnnotation));
		}
		MergedAnnotation requirementsAnnotation = mergedAnnotations
			.get("io.swagger.v3.oas.annotations.security.SecurityRequirements");
		if(requirementsAnnotation.isPresent()) {
			for(MergedAnnotation req : requirementsAnnotation.getAnnotationArray("value")) {
				securityRequirements.add(createSecurityRequirement(req));
			}
		}
	}

	private SecurityRequirement createSecurityRequirement(MergedAnnotation mergedAnnotation) {
		SecurityRequirement requirement = new SecurityRequirement();
		String name = mergedAnnotation.getString("name");
		String[] scopesArr = mergedAnnotation.getStringArray("scopes");
		List<String> scopes = scopesArr != null ? Arrays.asList(scopesArr) : new ArrayList<>();
		requirement.addRequirement(name, scopes);
		return requirement;
	}

}
