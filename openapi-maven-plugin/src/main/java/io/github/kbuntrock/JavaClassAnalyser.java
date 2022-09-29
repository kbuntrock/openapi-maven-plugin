package io.github.kbuntrock;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.library.JaxrsHttpVerb;
import io.github.kbuntrock.configuration.library.Library;
import io.github.kbuntrock.model.*;
import io.github.kbuntrock.utils.Logger;
import io.github.kbuntrock.utils.OpenApiDataType;
import io.github.kbuntrock.utils.ParameterLocation;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.springframework.core.annotation.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Analyse a java class in light of an api configuration object
 */
public class JavaClassAnalyser {

    private static final Pattern BEGINNING = Pattern.compile("^[a-z\\.]*");
    private static final Pattern FIRST_GENERIC = Pattern.compile("<[a-z\\.]*");
    private static final Pattern OTHER_GENERIC = Pattern.compile(",[a-z\\.]*");

    private final Log logger = Logger.INSTANCE.getLogger();

    private final ApiConfiguration apiConfiguration;

    public JavaClassAnalyser(ApiConfiguration apiConfiguration) {
        this.apiConfiguration = apiConfiguration;
    }

    /**
     * Create a Tag from a java class containing REST mapping functions
     *
     * @param clazz a REST controller class
     * @return an tag (if there is at least one declared endpoint)
     * @throws MojoFailureException
     */
    public Optional<Tag> getTagFromClass(Class<?> clazz) throws MojoFailureException {
        Tag tag = new Tag(clazz);
        logger.debug("Parsing tag : " + tag.getName());
        List<String> basePaths = Collections.singletonList("");

        MergedAnnotations mergedAnnotations = MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);

        if (Library.SPRING_MVC == apiConfiguration.getLibrary()) {
            // Spring MVC version
            MergedAnnotation<RequestMapping> requestMappingMergedAnnotation = mergedAnnotations.get(RequestMapping.class);
            if (requestMappingMergedAnnotation.isPresent()) {
                String[] paths = requestMappingMergedAnnotation.getStringArray("value");
                if (paths.length > 0) {
                    basePaths = Arrays.asList(paths);
                }
            }
        } else if (Library.JAXRS == apiConfiguration.getLibrary()) {
            // JAXRS version
            MergedAnnotation<Path> requestMappingMergedAnnotation = mergedAnnotations.get(Path.class);
            if (requestMappingMergedAnnotation.isPresent()) {
                String path = requestMappingMergedAnnotation.getString("value");
                if (!StringUtils.isEmpty(path)) {
                    basePaths = Arrays.asList(path);
                }
            }
        }


        for (String basePath : basePaths) {
            parseEndpoints(tag, basePath, clazz);
        }

        if (tag.getEndpoints().isEmpty()) {
            // There was not valid endpoint to attach to this tag. Therefore, we don't keep track of it.
            return Optional.empty();
        } else {
            return Optional.of(tag);
        }

    }

    private void parseEndpoints(Tag tag, String basePath, Class<?> clazz) throws MojoFailureException {

        logger.debug("Parsing endpoint " + clazz.getSimpleName());

        Method[] methods = clazz.getMethods();

        for (Method method : methods) {

            MergedAnnotations mergedAnnotations = MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
            if(Library.SPRING_MVC == apiConfiguration.getLibrary()) {
                computeSpringAnnotations(basePath, method, mergedAnnotations, tag);
            } else { // Jaxrs scanning
                computeJaxrsAnnotations(basePath, method, mergedAnnotations, tag);
            }

        }
    }

    private void computeSpringAnnotations(final String basePath, final Method method, final MergedAnnotations mergedAnnotations, final Tag tag) throws MojoFailureException {
        MergedAnnotation<RequestMapping> requestMappingMergedAnnotation = mergedAnnotations.get(RequestMapping.class);
        if (requestMappingMergedAnnotation.isPresent()) {
            RequestMethod[] requestMethods = requestMappingMergedAnnotation.getEnumArray("method", RequestMethod.class);
            if (requestMethods.length > 0) {
                logger.debug("Parsing request method : " + method.getName());
                String methodIdentifier = createIdentifier(method);
                List<ParameterObject> parameterObjects = readSpringParameters(method);
                DataObject responseObject = readResponseObject(method);
                int responseCode = readSpringResponseCode(mergedAnnotations);
                List<String> paths = readSpringEndpointPaths(basePath, requestMappingMergedAnnotation);
                for (RequestMethod requestMethod : requestMethods) {
                    for (String path : paths) {
                        Endpoint endpoint = new Endpoint();
                        endpoint.setType(OperationType.from(requestMethod));
                        endpoint.setPath(path);
                        endpoint.setName(method.getName());
                        endpoint.setParameters(parameterObjects);
                        endpoint.setResponseObject(responseObject);
                        endpoint.setResponseCode(responseCode);
                        setSpringConsumeProduceProperties(endpoint, requestMappingMergedAnnotation);
                        endpoint.setIdentifier(methodIdentifier);
                        endpoint.setDeprecated(isDeprecated(method));
                        tag.addEndpoint(endpoint);
                        logger.debug("Finished parsing endpoint : " + endpoint.getName() + " - " + endpoint.getType().name());
                    }
                }
            }
        }
    }

    private void computeJaxrsAnnotations(final String basePath, final Method method, final MergedAnnotations mergedAnnotations, final Tag tag) throws MojoFailureException {
        MergedAnnotation<Path> requestMappingMergedAnnotation = mergedAnnotations.get(Path.class);
        if (requestMappingMergedAnnotation.isPresent()) {

            for(JaxrsHttpVerb verb : JaxrsHttpVerb.values()) {
                MergedAnnotation m = mergedAnnotations.get(verb.getAnnotationClass());
                if(m.isPresent()) {
                    String methodIdentifier = createIdentifier(method);
                    List<ParameterObject> parameterObjects = readJaxrsParameters(method);
                    DataObject responseObject = readResponseObject(method);
                    int responseCode =  HttpStatus.OK.value();
                    readJaxrsEndpointPaths(basePath, requestMappingMergedAnnotation);
                    String path = readJaxrsEndpointPaths(basePath, requestMappingMergedAnnotation);
                    Endpoint endpoint = new Endpoint();
                    endpoint.setType(OperationType.from(verb.getAnnotationClass()));
                    endpoint.setPath(path);
                    endpoint.setName(method.getName());
                    endpoint.setParameters(parameterObjects);
                    endpoint.setResponseObject(responseObject);
                    endpoint.setResponseCode(responseCode);
                    setJaxrsConsumeProduceProperties(endpoint, mergedAnnotations);
                    endpoint.setIdentifier(methodIdentifier);
                    endpoint.setDeprecated(isDeprecated(method));
                    tag.addEndpoint(endpoint);
                    logger.debug("Finished parsing endpoint : " + endpoint.getName() + " - " + endpoint.getType().name());
                }
            }
        }
    }

    private static String createTypeIdentifier(String typeName) {


        String returnTypeName = typeName;
        Matcher matcher = BEGINNING.matcher(returnTypeName);
        if (matcher.find() && matcher.group().contains(".")) {
            returnTypeName = returnTypeName.replace(matcher.group(), "");
        }
        Matcher matcher2 = FIRST_GENERIC.matcher(returnTypeName);
        if (matcher2.find() && matcher2.group().contains(".")) {
            returnTypeName = returnTypeName.replace(matcher2.group(), "<");
        }
        Matcher matcher3 = OTHER_GENERIC.matcher(returnTypeName);
        if (matcher3.find() && matcher3.group().contains(".")) {
            returnTypeName = returnTypeName.replace(matcher3.group(), ",");
        }

        return returnTypeName;
    }

    public static String createIdentifier(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(createTypeIdentifier(method.getGenericReturnType().getTypeName()));
        sb.append("_");
        sb.append(method.getName());
        for (Parameter parameter : method.getParameters()) {
            sb.append("_");
            sb.append(createTypeIdentifier(parameter.getParameterizedType().getTypeName()));
        }
        return sb.toString();
    }

    private boolean isDeprecated(Method originalMethod) {
        Set<Method> overridenMethods = MethodUtils.getOverrideHierarchy(originalMethod, ClassUtils.Interfaces.INCLUDE);
        for(Method method: overridenMethods) {
            if(method.getDeclaredAnnotation(Deprecated.class) != null) {
                return true;
            }
        }
        return false;
    }

    private List<ParameterObject> readSpringParameters(Method originalMethod) {

        logger.debug("Reading parameters from " + originalMethod.getName());

        // Set of the method in the original class and eventually the methods in the parent classes / interfaces
        Set<Method> overridenMethods = MethodUtils.getOverrideHierarchy(originalMethod, ClassUtils.Interfaces.INCLUDE);

        Map<String, ParameterObject> parameters = new LinkedHashMap<>();

        for(Method method : overridenMethods) {
            for (Parameter parameter : method.getParameters()) {
                if (HttpServletRequest.class.isAssignableFrom(parameter.getType())) {
                    continue;
                }
                logger.debug("Parameter : " + parameter.getName());


                ParameterObject paramObj = parameters.computeIfAbsent(parameter.getName(),
                        (name) -> new ParameterObject(name, parameter.getParameterizedType()));

                MergedAnnotations mergedAnnotations = MergedAnnotations.from(parameter, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);

                // Detect if is a path variable
                MergedAnnotation<PathVariable> pathVariableMA = mergedAnnotations.get(PathVariable.class);
                if(pathVariableMA.isPresent()) {
                    paramObj.setLocation(ParameterLocation.PATH);
                    paramObj.setRequired(pathVariableMA.getBoolean("required"));
                    // The value is equivalent to the name (alias for and user of MergedAnnotation)
                    String value = pathVariableMA.getString("value");
                    if (!StringUtils.isEmpty(value)) {
                        paramObj.setName(value);
                    }
                    logger.debug("PathVariable annotation detected (" + paramObj.getName() + ")");
                }

                // Detect if is a query variable
                MergedAnnotation<RequestParam> requestParamMA = mergedAnnotations.get(RequestParam.class);
                if (requestParamMA.isPresent()) {

                    boolean isMultipartFile = MultipartFile.class == paramObj.getJavaClass() ||
                            (OpenApiDataType.ARRAY == paramObj.getOpenApiType() && MultipartFile.class == paramObj.getArrayItemDataObject().getJavaClass());
                    if (isMultipartFile) {
                        // MultipartFile parameters are considered as a requestBody)
                        paramObj.setLocation(ParameterLocation.BODY);
                    } else {
                        paramObj.setLocation(ParameterLocation.QUERY);
                    }
                    paramObj.setRequired(requestParamMA.getBoolean("required"));

                    // The value is equivalent to the name (alias for and user of MergedAnnotation)
                    String value = requestParamMA.getString("value");
                    if (!StringUtils.isEmpty(value)) {
                        paramObj.setName(value);
                    }
                    logger.debug("RequestParam annotation detected (" + paramObj.getName() + "), location is "+ paramObj.getLocation().toString());
                }

                // Detect if is a request body parameter
                MergedAnnotation<RequestBody> requestBodyMA = mergedAnnotations.get(RequestBody.class);
                if (requestBodyMA.isPresent()) {
                    paramObj.setLocation(ParameterLocation.BODY);
                    paramObj.setRequired(requestBodyMA.getBoolean("required"));
                    logger.debug("RequestBody annotation detected, location is "+ paramObj.getLocation().toString());
                }

            }
        }

        return parameters.values().stream().filter(x -> x.getLocation() != null).collect(Collectors.toList());
    }

    private List<ParameterObject> readJaxrsParameters(Method originalMethod) {

        logger.debug("Reading parameters from " + originalMethod.getName());

        // Set of the method in the original class and eventually the methods in the parent classes / interfaces
        Set<Method> overridenMethods = MethodUtils.getOverrideHierarchy(originalMethod, ClassUtils.Interfaces.INCLUDE);

        Map<String, ParameterObject> parameters = new LinkedHashMap<>();

        for(Method method : overridenMethods) {

            boolean bodyParameterDetected = false;

            for (Parameter parameter : method.getParameters()) {
                if (HttpServletRequest.class.isAssignableFrom(parameter.getType())) {
                    continue;
                }
                logger.debug("Parameter : " + parameter.getName());


                ParameterObject paramObj = parameters.computeIfAbsent(parameter.getName(),
                        (name) -> new ParameterObject(name, parameter.getParameterizedType()));

                MergedAnnotations mergedAnnotations = MergedAnnotations.from(parameter, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);

                MergedAnnotation<NotNull> notnullMA = mergedAnnotations.get(NotNull.class);
                // Detect if required
                paramObj.setRequired(notnullMA.isPresent());

                // Detect if is a path variable
                MergedAnnotation<PathParam> pathVariableMA = mergedAnnotations.get(PathParam.class);
                if(pathVariableMA.isPresent()) {
                    paramObj.setLocation(ParameterLocation.PATH);
                    // Path params are required
                    paramObj.setRequired(true);
                    // The value is equivalent to the name (alias for and user of MergedAnnotation)
                    String value = pathVariableMA.getString("value");
                    if (!StringUtils.isEmpty(value)) {
                        paramObj.setName(value);
                    }
                    logger.debug("PathParam annotation detected (" + paramObj.getName() + ")");
                }

                // Detect if is a query variable
                MergedAnnotation<QueryParam> requestParamMA = mergedAnnotations.get(QueryParam.class);
                if (requestParamMA.isPresent()) {

                    boolean isMultipartFile = MultipartFile.class == paramObj.getJavaClass() ||
                            (OpenApiDataType.ARRAY == paramObj.getOpenApiType() && MultipartFile.class == paramObj.getArrayItemDataObject().getJavaClass());
                    if (isMultipartFile) {
                        // MultipartFile parameters are considered as a requestBody)
                        paramObj.setLocation(ParameterLocation.BODY);
                    } else {
                        paramObj.setLocation(ParameterLocation.QUERY);
                    }

                    // The value is equivalent to the name (alias for and user of MergedAnnotation)
                    String value = requestParamMA.getString("value");
                    if (!StringUtils.isEmpty(value)) {
                        paramObj.setName(value);
                    }
                    logger.debug("QueryParam annotation detected (" + paramObj.getName() + "), location is "+ paramObj.getLocation().toString());
                }

                // Detect if is a request body parameter (if it is not a path or a query param)
                if (paramObj.getLocation() == null) {
                    if(bodyParameterDetected) {
                        bodyParameterDetected = true;
                        logger.error("Cannot set multiple body parameters, (" + paramObj.getName() + ")");
                    } else {
                        paramObj.setLocation(ParameterLocation.BODY);
                        logger.debug("Body parameter detected (" + paramObj.getName() + "), location is "+ paramObj.getLocation().toString());
                    }
                }

            }
        }

        return parameters.values().stream().filter(x -> x.getLocation() != null).collect(Collectors.toList());
    }

    private static int readSpringResponseCode(MergedAnnotations mergedAnnotations) {

        MergedAnnotation<ResponseStatus> responseStatusMA = mergedAnnotations.get(ResponseStatus.class);
        if (!responseStatusMA.isPresent()) {
            return HttpStatus.OK.value();
        }
        return responseStatusMA.getValue("value", HttpStatus.class).get().value();
    }

    private DataObject readResponseObject(Method method) {
        Class<?> returnType = method.getReturnType();
        if (Void.class == returnType || Void.TYPE == returnType) {
            return null;
        }
        DataObject dataObject = new DataObject(method.getGenericReturnType());
        dataObject = computeFrameworkReturnObject(dataObject);
        logger.debug(dataObject.toString());
        return dataObject;
    }

    /**
     * Some returned objects are handled in a specific manner by spring.
     * In that case, we have to adapt it
     *
     * @param dataObject source
     * @return return DataObject
     */
    private DataObject computeFrameworkReturnObject(final DataObject dataObject) {
        if (Optional.class.isAssignableFrom(dataObject.getJavaClass()) ||
                HttpEntity.class.isAssignableFrom(dataObject.getJavaClass())) {
            return new DataObject(dataObject.getGenericNameToTypeMap().get("T"));
        }
        return dataObject;
    }

    /**
     * Set the consume and produce properties of an endpoint
     *
     * @param endpoint                       the endpoint object to set
     * @param requestMappingMergedAnnotation
     */
    private static void setSpringConsumeProduceProperties(Endpoint endpoint, MergedAnnotation<RequestMapping> requestMappingMergedAnnotation) throws MojoFailureException {

        Optional<ParameterObject> body = endpoint.getParameters().stream().filter(x -> ParameterLocation.BODY == x.getLocation()).findAny();
        if (body.isPresent()) {
            String[] consumes = requestMappingMergedAnnotation.getStringArray("consumes");
            if (consumes.length > 0) {
                body.get().setFormats(Arrays.asList(consumes));
            }
        }
        if (endpoint.getResponseObject() != null) {
            String[] produces = requestMappingMergedAnnotation.getStringArray("produces");
            if (produces.length > 0) {
                endpoint.setResponseFormats(Arrays.asList(produces));
            }
        }
    }

    private static void setJaxrsConsumeProduceProperties(Endpoint endpoint, final MergedAnnotations mergedAnnotations) throws MojoFailureException {

        MergedAnnotation<Consumes> consumesMergedAnnotation = mergedAnnotations.get(Consumes.class);
        MergedAnnotation<Produces> producesMergedAnnotation = mergedAnnotations.get(Produces.class);

        Optional<ParameterObject> body = endpoint.getParameters().stream().filter(x -> ParameterLocation.BODY == x.getLocation()).findAny();
        if (body.isPresent() && consumesMergedAnnotation.isPresent()) {
            String[] consumes = consumesMergedAnnotation.getStringArray("value");
            if (consumes.length > 0) {
                body.get().setFormats(Arrays.asList(consumes));
            }
        }
        if (endpoint.getResponseObject() != null && producesMergedAnnotation.isPresent()) {
            String[] produces = producesMergedAnnotation.getStringArray("value");
            if (produces.length > 0) {
                endpoint.setResponseFormats(Arrays.asList(produces));
            }
        }
    }

    private List<String> readSpringEndpointPaths(String basePath, MergedAnnotation<RequestMapping> requestMappingMergedAnnotation) {
        String[] paths = requestMappingMergedAnnotation.getStringArray("path");
        List<String> resolvedPaths = new ArrayList<>();
        if (paths.length == 0) {
            resolvedPaths.add(concatenateBasePathAndMethodPath(basePath, "", apiConfiguration.isSpringPathEnhancement()));
        }
        for (String path : paths) {
            resolvedPaths.add(concatenateBasePathAndMethodPath(basePath, path, apiConfiguration.isSpringPathEnhancement()));
        }
        return resolvedPaths;
    }

    private String readJaxrsEndpointPaths(String basePath, MergedAnnotation<Path> requestMappingMergedAnnotation) {
        String path = requestMappingMergedAnnotation.getString("value");
        if (path == null) {
            return concatenateBasePathAndMethodPath(basePath, "", apiConfiguration.isSpringPathEnhancement());
        }
        return concatenateBasePathAndMethodPath(basePath, path, apiConfiguration.isSpringPathEnhancement());
    }

    private static String concatenateBasePathAndMethodPath(String basePath, String methodPath, boolean automaticSeparator) {
        String result = basePath + methodPath;
        if (automaticSeparator) {
            if (!methodPath.isEmpty() && !methodPath.startsWith("/") && !basePath.endsWith("/")) {
                result = basePath + "/" + methodPath;
            }
            if (!result.startsWith("/")) {
                result = "/" + result;
            }
        }
        return result;
    }

}
