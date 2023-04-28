/*
 * Copyright [2023] [gitchenjh]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.gitchenjh.endpointscanner.parser;

import io.github.gitchenjh.endpointscanner.model.RequestMappingModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.gitchenjh.endpointscanner.constant.Constants.API;
import static io.github.gitchenjh.endpointscanner.constant.Constants.API_OPERATION;
import static io.github.gitchenjh.endpointscanner.constant.Constants.DELETE_MAPPING;
import static io.github.gitchenjh.endpointscanner.constant.Constants.GET_MAPPING;
import static io.github.gitchenjh.endpointscanner.constant.Constants.HTTP_METHOD_DELETE;
import static io.github.gitchenjh.endpointscanner.constant.Constants.HTTP_METHOD_GET;
import static io.github.gitchenjh.endpointscanner.constant.Constants.HTTP_METHOD_POST;
import static io.github.gitchenjh.endpointscanner.constant.Constants.HTTP_METHOD_PUT;
import static io.github.gitchenjh.endpointscanner.constant.Constants.METHOD_STR;
import static io.github.gitchenjh.endpointscanner.constant.Constants.PATH_STR;
import static io.github.gitchenjh.endpointscanner.constant.Constants.POST_MAPPING;
import static io.github.gitchenjh.endpointscanner.constant.Constants.PUT_MAPPING;
import static io.github.gitchenjh.endpointscanner.constant.Constants.SPECIAL_SYMBOLS;
import static io.github.gitchenjh.endpointscanner.constant.Constants.TAGS_STR;
import static io.github.gitchenjh.endpointscanner.constant.Constants.VALUE_STR;

/**
 * @author <a href="mailto:chenjh1993@qq.com">chenjh</a>
 * @since 0.1.0
 */
public abstract class AbstractParser {

    public abstract RequestMappingModel parse(String metaStr, String clazz, List<String> imports);

    protected void resolveMapping(String str, RequestMappingModel requestMappingModel) {
        String requestMapping;
        String requestMethod = "";
        if (!str.contains("(") || str.indexOf(")") - str.indexOf("(") <= 3) {
            requestMapping = "/";
        } else {
            String requestMappingParamValues = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
            requestMapping = getAnnotationParamValue(requestMappingParamValues, VALUE_STR, true);
            if (requestMapping == null) {
                requestMapping = getAnnotationParamValue(requestMappingParamValues, PATH_STR, true);
            }
            if (requestMapping == null) {
                requestMapping = "";
            }
            requestMethod = getAnnotationParamValue(requestMappingParamValues, METHOD_STR, false);
            if (requestMethod == null) {
                requestMethod = "";
            }
            if (requestMethod.contains(".")) {
                requestMethod = requestMethod.split("\\.")[1];
            }
            if (str.startsWith(GET_MAPPING)) {
                requestMethod = HTTP_METHOD_GET;
            } else if (str.startsWith(POST_MAPPING)) {
                requestMethod = HTTP_METHOD_POST;
            } else if (str.startsWith(PUT_MAPPING)) {
                requestMethod = HTTP_METHOD_PUT;
            } else if (str.startsWith(DELETE_MAPPING)) {
                requestMethod =HTTP_METHOD_DELETE;
            }
        }
        if (!requestMapping.startsWith("/")) {
            requestMapping = "/" + requestMapping;
        }
        if (!requestMapping.equals("/") && requestMapping.endsWith("/")) {
            requestMapping = requestMapping.substring(0, requestMapping.length() - 1);
        }
        requestMappingModel.setPath(requestMapping);
        requestMappingModel.setHttpMethod(requestMethod);
    }

    protected String resolveDescription(String description){
        String[] lines = description.split("[\r\n]");
        StringBuilder result = new StringBuilder();
        for (String line :lines) {
            line = line.replaceAll("\\s","");
            if (!line.startsWith("//") && !line.startsWith("/*") && !line.startsWith("*")
                    && !line.startsWith(API) && !line.startsWith(API_OPERATION)) {
                continue;
            }
            line = line.replace("*","") .replace("/","");
            if (line.startsWith(API) || line.startsWith(API_OPERATION)) {
                if (line.indexOf("(") > 0 && line.indexOf(")") - line.indexOf("(") > 3) {
                    String descParamValues = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                    String descValue = getAnnotationParamValue(descParamValues, VALUE_STR, false);
                    String descTagValue = getAnnotationParamValue(descParamValues, TAGS_STR, false);
                    if (descValue == null && descTagValue == null) {
                        descValue = getAnnotationParamValue(descParamValues, VALUE_STR, true);
                    } else {
                        return descValue != null ? descValue : descTagValue;
                    }
                    if (descValue != null) {
                        return descValue;
                    }
                }
            }
            if (line.length() > 0 && !line.startsWith("@")) {
                result.append(line);
            }
        }
        return result.toString();
    }

    private String getAnnotationParamValue(String source, String param, Boolean getDefaultParamValue) {
        Map<String, String> paramMap = new HashMap<>();
        source = source.replaceAll("\\s*","")
                .replaceAll("\",","\"" + SPECIAL_SYMBOLS)
                .replaceAll("\"","");

        if (source.contains(SPECIAL_SYMBOLS)) {
            String[] paramValues = source.split(SPECIAL_SYMBOLS);
            for(String paramValue : paramValues){
                String[] kv = paramValue.split("=");
                if (kv.length >= 2) {
                    paramMap.put(kv[0], kv[1]);
                }
            }
        } else if (source.contains("=")) {
            String[] kv = source.split("=");
            if (kv.length >= 2) {
                paramMap.put(kv[0], kv[1]);
            }
        }
        String value = paramMap.get(param);
        if (value != null) {
            return value;
        }
        if (getDefaultParamValue) {
            return source;
        } else {
            return null;
        }
    }
}
