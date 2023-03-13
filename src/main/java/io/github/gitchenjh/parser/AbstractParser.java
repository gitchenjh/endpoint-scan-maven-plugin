package io.github.gitchenjh.parser;

import io.github.gitchenjh.model.RequestMappingModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.gitchenjh.constant.Constants.API;
import static io.github.gitchenjh.constant.Constants.API_OPERATION;
import static io.github.gitchenjh.constant.Constants.DELETE_MAPPING;
import static io.github.gitchenjh.constant.Constants.GET_MAPPING;
import static io.github.gitchenjh.constant.Constants.HTTP_METHOD_DELETE;
import static io.github.gitchenjh.constant.Constants.HTTP_METHOD_GET;
import static io.github.gitchenjh.constant.Constants.HTTP_METHOD_POST;
import static io.github.gitchenjh.constant.Constants.HTTP_METHOD_PUT;
import static io.github.gitchenjh.constant.Constants.METHOD_STR;
import static io.github.gitchenjh.constant.Constants.PATH_STR;
import static io.github.gitchenjh.constant.Constants.POST_MAPPING;
import static io.github.gitchenjh.constant.Constants.PUT_MAPPING;
import static io.github.gitchenjh.constant.Constants.TAG_STR;
import static io.github.gitchenjh.constant.Constants.VALUE_STR;

/**
 * @author 陈精华
 * @since 2023-03-11
 */
public abstract class AbstractParser {

    public abstract RequestMappingModel parse(String metaStr, String clazz, List<String> imports);

    protected void resolveMapping(String str, RequestMappingModel requestMappingModel) {
        if (str.startsWith(GET_MAPPING)) {
            requestMappingModel.setHttpMethod(HTTP_METHOD_GET);
        } else if (str.startsWith(POST_MAPPING)) {
            requestMappingModel.setHttpMethod(HTTP_METHOD_POST);
        } else if (str.startsWith(PUT_MAPPING)) {
            requestMappingModel.setHttpMethod(HTTP_METHOD_PUT);
        } else if (str.startsWith(DELETE_MAPPING)) {
            requestMappingModel.setHttpMethod(HTTP_METHOD_DELETE);
        }
        if (!str.contains("(") || str.indexOf(")") - str.indexOf("(") <= 3) {
            requestMappingModel.setPath("/");
        }
        String requestMappingParamValues = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
        String requestMapping = getAnnotationParamValue(requestMappingParamValues, VALUE_STR, true);
        if (requestMapping == null) {
            requestMapping = getAnnotationParamValue(requestMappingParamValues, PATH_STR, true);
        }
        if (requestMapping == null) {
            requestMapping = "";
        }
        String requestMethod = getAnnotationParamValue(requestMappingParamValues, METHOD_STR, false);
        if (requestMethod == null) {
            requestMethod = "";
        }
        if (requestMethod.contains(".")) {
            requestMethod = requestMethod.split("\\.")[1];
        }
        requestMappingModel.setPath(requestMapping);
        requestMappingModel.setHttpMethod(requestMethod);
    }

    protected String resolveDescription(String description){
        String[] lines = description.replace("*","")
                .replace("/","")
                .split("\r|\n");
        StringBuilder result = new StringBuilder();
        for (String line :lines) {
            line = line.replaceAll("\\s","");
            if (line.startsWith(API) || line.startsWith(API_OPERATION)) {
                if (line.indexOf("(") > 0 && line.indexOf(")") - line.indexOf("(") > 3) {
                    String descParamValues = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                    String descValue = getAnnotationParamValue(descParamValues, VALUE_STR, true);
                    if (descValue == null) {
                        descValue = getAnnotationParamValue(descParamValues, TAG_STR, true);
                    }
                    if (descValue != null) {
                        return descValue;
                    }
                }
            }
            if (line.length() >0 && !line.startsWith("@")){
                result.append(line);
            }
        }
        return result.toString();
    }

    private String getAnnotationParamValue(String source, String param, Boolean getDefaultParamValue) {
        Map<String, String> paramMap = new HashMap<>();
        source = source.replaceAll("\\s*","")
                .replaceAll("\"","");
        if (source.contains(",")) {
            String[] paramValues = source.split(",");
            for(String paramValue : paramValues){
                String[] kv = paramValue.split("=");
                paramMap.put(kv[0], kv[1]);
            }
        } else if (source.contains("=")) {
            String[] kv = source.split("=");
            paramMap.put(kv[0], kv[1]);
        } else {
            if (getDefaultParamValue) {
                return source;
            } else {
                return null;
            }
        }
        return paramMap.get(param);
    }
}
