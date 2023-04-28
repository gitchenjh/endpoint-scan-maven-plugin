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

import io.github.gitchenjh.endpointscanner.model.ControllerModel;

import java.util.List;

import static io.github.gitchenjh.endpointscanner.constant.Constants.CONTROLLER;
import static io.github.gitchenjh.endpointscanner.constant.Constants.CONTROLLER_ESCAPE;
import static io.github.gitchenjh.endpointscanner.constant.Constants.REQUEST_MAPPING;
import static io.github.gitchenjh.endpointscanner.constant.Constants.REQUEST_MAPPING_ESCAPE;
import static io.github.gitchenjh.endpointscanner.constant.Constants.REST_CONTROLLER;
import static io.github.gitchenjh.endpointscanner.constant.Constants.REST_CONTROLLER_ESCAPE;

/**
 * @author <a href="mailto:chenjh1993@qq.com">chenjh</a>
 * @since 0.1.0
 */
public class ClassParser extends AbstractParser {

    private final ControllerParser controllerParser;

    public ClassParser(ControllerParser controllerParser) {
        this.controllerParser = controllerParser;
    }

    @Override
    public ControllerModel parse(String metaStr, String clazz, List<String> imports) {
        if (metaStr.contains(CONTROLLER) && !metaStr.matches(CONTROLLER_ESCAPE)) {
            return controllerParser.parse(metaStr, clazz, imports);
        } else if (metaStr.contains(REST_CONTROLLER) && !metaStr.matches(REST_CONTROLLER_ESCAPE)) {
            return controllerParser.parse(metaStr, clazz, imports);
        } else if (metaStr.contains(REQUEST_MAPPING) && !metaStr.matches(REQUEST_MAPPING_ESCAPE)) {
            return controllerParser.parse(metaStr, clazz, imports);
        }
        return null;
    }

}
