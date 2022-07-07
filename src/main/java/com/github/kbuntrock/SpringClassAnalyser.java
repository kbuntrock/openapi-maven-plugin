package com.github.kbuntrock;

import com.github.kbuntrock.configuration.ApiConfiguration;
import com.github.kbuntrock.model.*;
import com.github.kbuntrock.utils.Logger;
import com.github.kbuntrock.utils.OpenApiDataType;
import com.github.kbuntrock.utils.ParameterLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
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
        String basePath = "";
        RequestMapping classRequestMapping = clazz.getAnnotation(RequestMapping.class);

        if (classRequestMapping != null) {
            if (classRequestMapping.value() != null && classRequestMapping.value().length > 0) {
                basePath = classRequestMapping.value()[0];
            } else if (classRequestMapping.path() != null && classRequestMapping.path().length > 0) {
                basePath = classRequestMapping.path()[0];
            }
        }

        parseEndpoints(tag, basePath, clazz);

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
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {

                Optional<RequestMapping> requestMapping = getRequestMappingAnnotation(annotation);
                if (requestMapping.isPresent()) {
                    Optional<Endpoint> optEndpoint = readRequestMapping(basePath, requestMapping.get(), annotation);
                    if (optEndpoint.isPresent()) {
                        Endpoint endpoint = optEndpoint.get();
                        endpoint.setName(method.getName());
                        logger.debug("Parsing endpoint : " + endpoint.getName());
                        endpoint.setParameters(readParameters(method));
                        logger.debug("Parsing endpoint : " + endpoint.getName() + " - parameters read");
                        endpoint.setResponseObject(readResponseObject(method));
                        logger.debug("Parsing endpoint : " + endpoint.getName() + " - response read");
                        endpoint.setResponseCode(readResponseCode(method));
                        setConsumeProduceProperties(endpoint, annotation);
                        endpoint.setIdentifier(createIdentifier(method));
                        tag.addEndpoint(endpoint);
                        logger.debug("Parsing endpoint : " + endpoint.getName() + " - the end");
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

    private Optional<Endpoint> readRequestMapping(String basePath, RequestMapping requestMapping, Annotation realAnnotation) throws MojoFailureException {
        Optional<OperationType> operation = requestMappingToOperation(requestMapping);
        if (!operation.isPresent()) {
            return Optional.empty();
        }
        Endpoint endpoint = new Endpoint();
        endpoint.setType(operation.get());
        endpoint.setPath(readEndpointPath(basePath, realAnnotation));
        return Optional.of(endpoint);
    }

    /**
     * Set the consume and produce properties of an endpoint
     *
     * @param endpoint       the endpoint object to set
     * @param pathAnnotation An instance of RequestMapping or a subclass of RequestMapping
     */
    private static void setConsumeProduceProperties(Endpoint endpoint, Annotation pathAnnotation) throws MojoFailureException {
        Method methodConsumes = null;
        Method methodProduces = null;
        try {
            methodConsumes = pathAnnotation.annotationType().getMethod("consumes");
            methodProduces = pathAnnotation.annotationType().getMethod("produces");
        } catch (NoSuchMethodException e) {
            throw new MojoFailureException("Method 'consumes' or 'produces' not found for " + pathAnnotation.getClass().getSimpleName());
        }
        try {
            Optional<ParameterObject> parameter = endpoint.getParameters().stream().filter(x -> ParameterLocation.BODY == x.getLocation()).findAny();
            if (parameter.isPresent()) {
                String[] consumes = (String[]) methodConsumes.invoke(pathAnnotation);
                if (consumes != null && consumes.length > 0) {
                    parameter.get().setFormat(consumes[0]);
                }
            }
            if (endpoint.getResponseObject() != null) {
                String[] produces = (String[]) methodProduces.invoke(pathAnnotation);
                if (produces != null && produces.length > 0) {
                    endpoint.setResponseFormat(produces[0]);
                }
            }

        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new MojoFailureException("Method 'consumes' or 'produces' cannot be invoked for " + pathAnnotation.annotationType().getSimpleName());
        }
    }

    private String readEndpointPath(String basePath, Annotation realAnnotation) throws MojoFailureException {
        Method methodValue = null;
        Method methodPath = null;
        try {
            methodPath = realAnnotation.annotationType().getMethod("path");
            methodValue = realAnnotation.annotationType().getMethod("value");
        } catch (NoSuchMethodException e) {
            throw new MojoFailureException("Method 'value' not found for " + realAnnotation.getClass().getSimpleName());
        }
        if (methodValue == null && methodPath == null) {
            return concatenateBasePathAndMethodPath(basePath, "", apiConfiguration.isSpringPathEnhancement());
        }
        try {
            String[] paths = (String[]) methodPath.invoke(realAnnotation);
            if (paths != null && paths.length > 0) {
                return concatenateBasePathAndMethodPath(basePath, paths[0], apiConfiguration.isSpringPathEnhancement());
            }
            String[] values = (String[]) methodValue.invoke(realAnnotation);
            if (values != null && values.length > 0) {
                return concatenateBasePathAndMethodPath(basePath, values[0], apiConfiguration.isSpringPathEnhancement());
            }
            return concatenateBasePathAndMethodPath(basePath, "", apiConfiguration.isSpringPathEnhancement());

        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new MojoFailureException("Method value cannot be invoked for " + realAnnotation.annotationType().getSimpleName());
        }
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

    private static Optional<OperationType> requestMappingToOperation(RequestMapping requestMapping) {
        if (requestMapping.method().length < 1) {
            return Optional.empty();
        }

        switch (requestMapping.method()[0]) {
            case GET:
                return Optional.of(OperationType.GET);
            case PUT:
                return Optional.of(OperationType.PUT);
            case HEAD:
                return Optional.of(OperationType.HEAD);
            case POST:
                return Optional.of(OperationType.POST);
            case PATCH:
                return Optional.of(OperationType.PATCH);
            case DELETE:
                return Optional.of(OperationType.DELETE);
            case TRACE:
                return Optional.of(OperationType.TRACE);
            case OPTIONS:
                return Optional.of(OperationType.OPTIONS);
            default:
                throw new RuntimeException("RequestMethod unknow : " + requestMapping.method()[0].name());
        }
    }
}
