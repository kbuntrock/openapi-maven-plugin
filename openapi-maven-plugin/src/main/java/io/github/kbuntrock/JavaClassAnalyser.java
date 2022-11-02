package io.github.kbuntrock;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.CommonApiConfiguration;
import io.github.kbuntrock.configuration.library.reader.AstractLibraryReader;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.utils.Logger;
import io.github.kbuntrock.utils.ParameterLocation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Analyse a java class in light of an api configuration object
 */
public class JavaClassAnalyser {

	private static final Pattern BEGINNING = Pattern.compile("^[a-z\\.]*");
	private static final Pattern FIRST_GENERIC = Pattern.compile("<[a-z\\.]*");
	private static final Pattern OTHER_GENERIC = Pattern.compile(",[a-z\\.]*");

	private final Log logger = Logger.INSTANCE.getLogger();

	private final ApiConfiguration apiConfiguration;

	private final List<Pair<Pattern, Pattern>> whiteListPatterns = new ArrayList<>();
	private final List<Pair<Pattern, Pattern>> blackListPatterns = new ArrayList<>();

	private final AstractLibraryReader libraryReader;

	public JavaClassAnalyser(final ApiConfiguration apiConfiguration) {
		this.apiConfiguration = apiConfiguration;
		this.libraryReader = apiConfiguration.getLibrary().createReader(apiConfiguration);

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
		final Matcher matcher = BEGINNING.matcher(returnTypeName);
		if(matcher.find() && matcher.group().contains(".")) {
			returnTypeName = returnTypeName.replace(matcher.group(), "");
		}
		final Matcher matcher2 = FIRST_GENERIC.matcher(returnTypeName);
		if(matcher2.find() && matcher2.group().contains(".")) {
			returnTypeName = returnTypeName.replace(matcher2.group(), "<");
		}
		final Matcher matcher3 = OTHER_GENERIC.matcher(returnTypeName);
		if(matcher3.find() && matcher3.group().contains(".")) {
			returnTypeName = returnTypeName.replace(matcher3.group(), ",");
		}

		return returnTypeName;
	}

	public static String createIdentifier(final Method method) {
		final StringBuilder sb = new StringBuilder();
		sb.append(createTypeIdentifier(method.getGenericReturnType().getTypeName()));
		sb.append("_");
		sb.append(method.getName());
		for(final Parameter parameter : method.getParameters()) {
			sb.append("_");
			sb.append(createTypeIdentifier(parameter.getParameterizedType().getTypeName()));
		}
		return sb.toString();
	}

	private static int readSpringResponseCode(final MergedAnnotations mergedAnnotations) {

		final MergedAnnotation<ResponseStatus> responseStatusMA = mergedAnnotations.get(ResponseStatus.class);
		if(!responseStatusMA.isPresent()) {
			return HttpStatus.OK.value();
		}
		return responseStatusMA.getValue("value", HttpStatus.class).get().value();
	}

	/**
	 * Set the consume and produce properties of an endpoint
	 *
	 * @param endpoint                       the endpoint object to set
	 * @param requestMappingMergedAnnotation
	 */
	private static void setSpringConsumeProduceProperties(final Endpoint endpoint,
		final MergedAnnotation<RequestMapping> requestMappingMergedAnnotation) throws MojoFailureException {

		final Optional<ParameterObject> body = endpoint.getParameters().stream().filter(x -> ParameterLocation.BODY == x.getLocation())
			.findAny();
		if(body.isPresent()) {
			final String[] consumes = requestMappingMergedAnnotation.getStringArray("consumes");
			if(consumes.length > 0) {
				body.get().setFormats(Arrays.asList(consumes));
			}
		}
		if(endpoint.getResponseObject() != null) {
			final String[] produces = requestMappingMergedAnnotation.getStringArray("produces");
			if(produces.length > 0) {
				endpoint.setResponseFormats(Arrays.asList(produces));
			}
		}
	}

	private static void setJaxrsConsumeProduceProperties(final Endpoint endpoint, final MergedAnnotations mergedAnnotations)
		throws MojoFailureException {

		final MergedAnnotation<Consumes> consumesMergedAnnotation = mergedAnnotations.get(Consumes.class);
		final MergedAnnotation<Produces> producesMergedAnnotation = mergedAnnotations.get(Produces.class);

		final Optional<ParameterObject> body = endpoint.getParameters().stream().filter(x -> ParameterLocation.BODY == x.getLocation())
			.findAny();
		if(body.isPresent() && consumesMergedAnnotation.isPresent()) {
			final String[] consumes = consumesMergedAnnotation.getStringArray("value");
			if(consumes.length > 0) {
				body.get().setFormats(Arrays.asList(consumes));
			}
		}
		if(endpoint.getResponseObject() != null && producesMergedAnnotation.isPresent()) {
			final String[] produces = producesMergedAnnotation.getStringArray("value");
			if(produces.length > 0) {
				endpoint.setResponseFormats(Arrays.asList(produces));
			}
		}
	}

	private static String concatenateBasePathAndMethodPath(final String basePath, final String methodPath,
		final boolean automaticSeparator) {
		String result = basePath + methodPath;
		if(automaticSeparator) {
			if(!methodPath.isEmpty() && !methodPath.startsWith("/") && !basePath.endsWith("/")) {
				result = basePath + "/" + methodPath;
			}
			if(!result.startsWith("/")) {
				result = "/" + result;
			}
		}
		return result;
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
		final List<String> basePaths = libraryReader.readBasePaths(clazz, mergedAnnotations);

		for(final String basePath : basePaths) {
			parseEndpoints(tag, basePath, clazz);
		}

		if(tag.getEndpoints().isEmpty()) {
			// There was not valid endpoint to attach to this tag. Therefore, we don't keep track of it.
			return Optional.empty();
		} else {
			return Optional.of(tag);
		}

	}

	private void parseEndpoints(final Tag tag, final String basePath, final Class<?> clazz) throws MojoFailureException {

		logger.debug("Parsing endpoint " + clazz.getSimpleName());

		final Method[] methods = clazz.getMethods();

		for(final Method method : methods) {

			if(validateWhiteList(clazz, method) && validateBlackList(clazz, method)) {
				final MergedAnnotations mergedAnnotations = MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
				libraryReader.computeAnnotations(basePath, method, mergedAnnotations, tag);
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
