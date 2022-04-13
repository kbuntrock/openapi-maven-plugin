package com.github.kbuntrock;

import com.github.kbuntrock.model.*;
import com.github.kbuntrock.utils.ParameterLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SpringClassAnalyser {

    private Log logger = new SystemStreamLog();

    public Optional<Tag> getTagFromClass(Class<?> clazz) throws MojoFailureException {
        Tag tag = new Tag(clazz.getSimpleName());
        logger.info("tag start : " + tag.getName());
        String basePath = "";
        RequestMapping classRequestMapping = clazz.getAnnotation(RequestMapping.class);

        logger.info("Looking for : " + RequestMapping.class.getCanonicalName());
        for (Annotation annotation : clazz.getAnnotations()) {
            logger.info("annotation : " + annotation.annotationType().getCanonicalName());
        }

        logger.info("classRequestMapping_ ? " + clazz.getAnnotations()[0]);
        logger.info("classRequestMapping_ ?? " + clazz.getAnnotations()[0].getClass().getCanonicalName());
        logger.info("classRequestMapping ? " + classRequestMapping);
        logger.info("classRequestMapping_2 ? " + classRequestMapping.name());
        logger.info("classRequestMapping_3 ? " + classRequestMapping.value().length);
        if (classRequestMapping.value().length > 0) {
            basePath = classRequestMapping.value()[0];
        }

        logger.info("classRequestMapping processing");
        parseEndpoints(tag, basePath, clazz);

        if (tag.getEndpoints().isEmpty()) {
            // There was not valid endpoint to attach to this tag. Therefore, we don't keep track of it.
            return Optional.empty();
        } else {
            return Optional.of(tag);
        }

    }

    private void parseEndpoints(Tag tag, String basePath, Class clazz) throws MojoFailureException {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                RequestMapping requestMapping = null;
                Annotation realAnnotation = annotation;
                if (annotation.annotationType() == RequestMapping.class) {
                    requestMapping = (RequestMapping) annotation;
                } else if (annotation.annotationType().getAnnotation(RequestMapping.class) != null) {
                    requestMapping = annotation.annotationType().getAnnotation(RequestMapping.class);
                }
                if (requestMapping != null) {
                    Optional<Endpoint> optEndpoint = readRequestMapping(basePath, requestMapping, realAnnotation);
                    if (optEndpoint.isPresent()) {
                        Endpoint endpoint = optEndpoint.get();
                        endpoint.setName(method.getName());
                        endpoint.setParameters(readParameters(method));
                        endpoint.setResponseObject(readResponseObject(method));
                        endpoint.setResponseCode(readResponseCode(method));
                        tag.addEndpoint(endpoint);
                    }
                }

            }
        }
    }

    private static List<ParameterObject> readParameters(Method method) {
        List<ParameterObject> parameters = new ArrayList<>();

        for (Parameter parameter : method.getParameters()) {
            if (parameter.getType().isAssignableFrom(HttpServletRequest.class)) {
                continue;
            }

            ParameterObject paramObj = new ParameterObject();
            paramObj.setName(parameter.getName());

            ParameterizedType parameterizedType = null;
            Type genericReturnType = parameter.getParameterizedType();
            if (genericReturnType instanceof ParameterizedType) {
                parameterizedType = (ParameterizedType) genericReturnType;
            }

            paramObj.setJavaType(parameter.getType(), parameterizedType);

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
                paramObj.setLocation(ParameterLocation.QUERY);
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

            parameters.add(paramObj);
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

    private static DataObject readResponseObject(Method method) {
        Class<?> returnType = method.getReturnType();
        if (Void.class == returnType || Void.TYPE == returnType) {
            return null;
        }
        DataObject dataObject = new DataObject();
        ParameterizedType parameterizedType = null;
        Type genericReturnType = method.getGenericReturnType();
        if (genericReturnType instanceof ParameterizedType) {
            parameterizedType = (ParameterizedType) genericReturnType;
        }
        dataObject.setJavaType(returnType, parameterizedType);
        return dataObject;
    }

    private static Optional<Endpoint> readRequestMapping(String basePath, RequestMapping requestMapping, Annotation realAnnotation) throws MojoFailureException {
        Optional<Operation> operation = requestMappingToOperation(requestMapping);
        if (operation.isEmpty()) {
            return Optional.empty();
        }
        Endpoint endpoint = new Endpoint();
        endpoint.setOperation(operation.get());
        endpoint.setPath(readEndpointPath(basePath, realAnnotation));
        return Optional.of(endpoint);
    }

    private static String readEndpointPath(String basePath, Annotation realAnnotation) throws MojoFailureException {
        Method methodValue = null;
        Method methodPath = null;
        try {
            methodPath = realAnnotation.annotationType().getMethod("path");
            methodValue = realAnnotation.annotationType().getMethod("value");
        } catch (NoSuchMethodException e) {
            throw new MojoFailureException("Method 'value' not found for " + realAnnotation.getClass().getSimpleName());
        }
        if (methodValue == null && methodPath == null) {
            return basePath;
        }
        try {
            String[] paths = (String[]) methodPath.invoke(realAnnotation);
            if (paths != null && paths.length > 0) {
                return basePath + paths[0];
            }
            String[] values = (String[]) methodValue.invoke(realAnnotation);
            if (values != null && values.length > 0) {
                return basePath + values[0];
            }
            return basePath;

        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new MojoFailureException("Method value cannot be invoked for " + realAnnotation.annotationType().getSimpleName());
        }


    }

    private static Optional<Operation> requestMappingToOperation(RequestMapping requestMapping) {
        if (requestMapping.method().length < 1) {
            return Optional.empty();
        }

        switch (requestMapping.method()[0]) {
            case GET:
                return Optional.of(Operation.GET);
            case PUT:
                return Optional.of(Operation.PUT);
            case HEAD:
                return Optional.of(Operation.HEAD);
            case POST:
                return Optional.of(Operation.POST);
            case PATCH:
                return Optional.of(Operation.PATCH);
            case DELETE:
                return Optional.of(Operation.DELETE);
            case TRACE:
                return Optional.of(Operation.TRACE);
            case OPTIONS:
                return Optional.of(Operation.OPTIONS);
            default:
                throw new RuntimeException("RequestMethod unknow : " + requestMapping.method()[0].name());
        }
    }
}
