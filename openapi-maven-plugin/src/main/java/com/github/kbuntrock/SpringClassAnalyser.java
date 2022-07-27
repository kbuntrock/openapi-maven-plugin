package com.github.kbuntrock;

import com.github.kbuntrock.configuration.ApiConfiguration;
import com.github.kbuntrock.model.*;
import com.github.kbuntrock.utils.Logger;
import com.github.kbuntrock.utils.OpenApiDataType;
import com.github.kbuntrock.utils.ParameterLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpringClassAnalyser {

    private static final Pattern BEGINNING = Pattern.compile("^[a-z\\.]*");
    private static final Pattern FIRST_GENERIC = Pattern.compile("<[a-z\\.]*");
    private static final Pattern OTHER_GENERIC = Pattern.compile(",[a-z\\.]*");

    private final Log logger = Logger.INSTANCE.getLogger();

    private final ApiConfiguration apiConfiguration;

    public SpringClassAnalyser(ApiConfiguration apiConfiguration) {
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
        List<String> basePaths = Arrays.asList("");

        MergedAnnotations mergedAnnotations = MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
        MergedAnnotation<RequestMapping> requestMappingMergedAnnotation = mergedAnnotations.get(RequestMapping.class);

        if (requestMappingMergedAnnotation.isPresent()) {
            String[] paths = requestMappingMergedAnnotation.getStringArray("path");
            if (paths.length > 0) {
                basePaths = Arrays.asList(paths);
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

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {

            MergedAnnotations mergedAnnotations = MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
            MergedAnnotation<RequestMapping> requestMappingMergedAnnotation = mergedAnnotations.get(RequestMapping.class);
            if (requestMappingMergedAnnotation.isPresent()) {
                RequestMethod[] requestMethods = requestMappingMergedAnnotation.getEnumArray("method", RequestMethod.class);
                if (requestMethods.length > 0) {
                    logger.debug("Parsing request method : " + method.getName());
                    List<ParameterObject> parameterObjects = readParameters(method);
                    DataObject responseObject = readResponseObject(method);
                    int responseCode = readResponseCode(method);
                    List<String> paths = readEndpointPaths(basePath, requestMappingMergedAnnotation);
                    Optional<Deprecated> deprecated = getDeprecatedAnnotation(method);
                    for (RequestMethod requestMethod : requestMethods) {
                        for (String path : paths) {
                            Endpoint endpoint = new Endpoint();
                            endpoint.setType(OperationType.from(requestMethod));
                            endpoint.setPath(path);
                            endpoint.setName(method.getName());
                            endpoint.setParameters(parameterObjects);
                            endpoint.setResponseObject(responseObject);
                            endpoint.setResponseCode(responseCode);
                            setConsumeProduceProperties(endpoint, requestMappingMergedAnnotation);
                            endpoint.setIdentifier(createIdentifier(method));
                            if (deprecated.isPresent()) {
                                endpoint.setDeprecated(true);
                            }
                            tag.addEndpoint(endpoint);
                            logger.debug("Finished parsing endpoint : " + endpoint.getName() + " - " + endpoint.getType().name());
                        }
                    }
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

    /**
     * The annotation given in parameter can be a "subtype" annotation of RequestMapping (GetMapping, PostMapping, ...)
     *
     * @param annotation
     * @return an Optional<RequestMapping>
     */
    private Optional<RequestMapping> getRequestMappingAnnotation(Annotation annotation) {
        RequestMapping requestMapping = null;
        if (annotation.annotationType() == RequestMapping.class) {
            requestMapping = (RequestMapping) annotation;
        } else if (annotation.annotationType().getAnnotation(RequestMapping.class) != null) {
            requestMapping = annotation.annotationType().getAnnotation(RequestMapping.class);
        }
        return Optional.ofNullable(requestMapping);
    }

    private Optional<Deprecated> getDeprecatedAnnotation(Method method) {
        return Optional.ofNullable(method.getAnnotation(Deprecated.class));
    }

    private List<ParameterObject> readParameters(Method method) {
        List<ParameterObject> parameters = new ArrayList<>();

        for (Parameter parameter : method.getParameters()) {
            if (HttpServletRequest.class.isAssignableFrom(parameter.getType())) {
                continue;
            }

            ParameterObject paramObj = new ParameterObject(parameter.getParameterizedType());
            paramObj.setName(parameter.getName());

            // Detect if is a path variable
            PathVariable pathAnnotation = parameter.getAnnotation(PathVariable.class);
            if (pathAnnotation != null) {
                paramObj.setLocation(ParameterLocation.PATH);
                paramObj.setRequired(pathAnnotation.required());
                if (!StringUtils.isEmpty(pathAnnotation.value())) {
                    paramObj.setName(pathAnnotation.value());
                } else if (!StringUtils.isEmpty(pathAnnotation.name())) {
                    paramObj.setName(pathAnnotation.name());
                }
            }

            // Detect if is a query variable
            RequestParam queryAnnotation = parameter.getAnnotation(RequestParam.class);
            if (queryAnnotation != null) {
                boolean isMultipartFile = MultipartFile.class == paramObj.getJavaClass() ||
                        (OpenApiDataType.ARRAY == paramObj.getOpenApiType() && MultipartFile.class == paramObj.getArrayItemDataObject().getJavaClass());
                if (isMultipartFile) {
                    // MultipartFile parameters are considered as a requestBody)
                    paramObj.setLocation(ParameterLocation.BODY);
                } else {
                    paramObj.setLocation(ParameterLocation.QUERY);
                }
                paramObj.setRequired(queryAnnotation.required());

                if (!StringUtils.isEmpty(queryAnnotation.value())) {
                    paramObj.setName(queryAnnotation.value());
                } else if (!StringUtils.isEmpty(queryAnnotation.name())) {
                    paramObj.setName(queryAnnotation.name());
                }
            }

            // Detect if is a request body parameter
            RequestBody requestBodyAnnotation = parameter.getAnnotation(RequestBody.class);
            if (requestBodyAnnotation != null) {
                paramObj.setLocation(ParameterLocation.BODY);
                paramObj.setRequired(requestBodyAnnotation.required());
            }

            if (paramObj.getLocation() != null) {
                parameters.add(paramObj);
            }

        }
        return parameters;
    }

    private static int readResponseCode(Method method) {

        ResponseStatus responseStatus = method.getAnnotation(ResponseStatus.class);
        if (responseStatus == null) {
            return HttpStatus.OK.value();
        }

        return responseStatus.value().value();
    }

    private DataObject readResponseObject(Method method) {
        Class<?> returnType = method.getReturnType();
        if (Void.class == returnType || Void.TYPE == returnType) {
            return null;
        }
        DataObject dataObject = new DataObject(method.getGenericReturnType());
        logger.debug(dataObject.toString());
        return dataObject;
    }

    /**
     * Set the consume and produce properties of an endpoint
     *
     * @param endpoint                       the endpoint object to set
     * @param requestMappingMergedAnnotation
     */
    private static void setConsumeProduceProperties(Endpoint endpoint, MergedAnnotation<RequestMapping> requestMappingMergedAnnotation) throws MojoFailureException {

        Optional<ParameterObject> body = endpoint.getParameters().stream().filter(x -> ParameterLocation.BODY == x.getLocation()).findAny();
        if (body.isPresent()) {
            String[] consumes = requestMappingMergedAnnotation.getStringArray("consumes");
            if (consumes.length > 0) {
                body.get().setFormat(consumes[0]);
            }
        }
        if (endpoint.getResponseObject() != null) {
            String[] produces = requestMappingMergedAnnotation.getStringArray("produces");
            if (produces.length > 0) {
                endpoint.setResponseFormat(produces[0]);
            }
        }
    }

    private List<String> readEndpointPaths(String basePath, MergedAnnotation<RequestMapping> requestMappingMergedAnnotation) {
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
