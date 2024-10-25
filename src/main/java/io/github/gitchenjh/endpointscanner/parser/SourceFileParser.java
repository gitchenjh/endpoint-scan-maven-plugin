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

import io.github.gitchenjh.endpointscanner.constant.Constants;
import io.github.gitchenjh.endpointscanner.model.ControllerModel;
import io.github.gitchenjh.endpointscanner.model.EndpointModel;
import io.github.gitchenjh.endpointscanner.util.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.github.gitchenjh.endpointscanner.constant.Constants.LINE_BREAK;

/**
 * @author <a href="mailto:chenjh1993@qq.com">chenjh</a>
 * @since 0.1.0
 */
public class SourceFileParser {

    private static final String IMPORT = "import";
    private static final String PACKAGE = "package";
    private final Log logger;

    private final MethodParser methodParser;

    private final ClassParser classParser;

    private List<ControllerModel> controllerList = new CopyOnWriteArrayList<>();

    public List<ControllerModel> getControllerList() {
        return controllerList;
    }

    public SourceFileParser(Log logger) {
        this.logger = logger;
        this.methodParser = new MethodParser(logger);
        ControllerParser controllerParser = new ControllerParser(logger);
        this.classParser = new ClassParser(controllerParser);
    }

    public void parse(File file) {
        controllerList = new CopyOnWriteArrayList<>();
        List<EndpointModel> endpointList = new CopyOnWriteArrayList<>();
        String pkg = "";
        String clazz = "";
        List<String> imports = new ArrayList<>();
        boolean gotHead = false;
        boolean gotClazz = false;
        StringBuilder stackCodeStr = new StringBuilder();
        Stack<String> stack = new Stack<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            ControllerModel currentController = null;
            EndpointModel currentEndpoint = null;
            while ((line = reader.readLine()) != null) {
                line = StringUtils.trim(line);
                if (line.isEmpty()) {
                    continue;
                }
                if (!gotHead && !line.startsWith(IMPORT) && !line.startsWith(PACKAGE)) {
                    gotHead = true;
                }
                if (!gotHead) {
                    String[] words = line.split(" ");
                    if (PACKAGE.equals(words[0]) && words.length == 2) {
                        pkg = words[1].endsWith(";") ? words[1].substring(0, words[1].length() - 1) : words[1];
                        continue;
                    }
                    if (IMPORT.equals(words[0]) && words.length == 2) {
                        imports.add(words[1].endsWith(";") ? words[1].substring(0, words[1].length() - 1) : words[1]);
                    }
                } else {
                    stackCodeStr.append(line).append(LINE_BREAK);
                    if (!gotClazz && line.startsWith("public class ")) {
                        clazz = line.substring("public class ".length());
                        if (clazz.endsWith("{")) {
                            clazz = clazz.substring(0, clazz.length() - 1);
                        }
                        clazz = StringUtils.trim(clazz);
                        clazz = clazz.split(" ")[0];
                        gotClazz = true;
                    }
                    if ((line.startsWith("@") && (!line.endsWith("{") || line.endsWith("({"))) || line.startsWith("/**") || line.startsWith("//") || line.startsWith("*")
                            // 排除 lambda 语法
                            || line.matches("\\(+\\{*\\(*") || line.matches(".*}+\\)")) {
                        continue;
                    }
                    if ((line.contains("{") && !line.matches(Constants.OPEN_BRACE_ESCAPE)) && (line.contains("}") && !line.matches(Constants.CLOSE_BRACE_ESCAPE))) {
                        // TODO 待优化 一行中有 {***} 或 } *** { 的情况
                        continue;
                    }

                    if (line.contains("{") && !line.matches(Constants.OPEN_BRACE_ESCAPE)) {
                        if (stack.empty()) {
                            ControllerModel controller = classParser.parse(stackCodeStr.toString(), pkg + "." + clazz, imports);
                            if (controller != null) {
                                currentController = controller;
                            }
                        } else {
                            currentEndpoint = methodParser.parse(stackCodeStr.toString(), pkg + "." + clazz, imports);
                        }
                        stack.push(stackCodeStr.toString());
                        stackCodeStr = new StringBuilder();
                    }
                    if (line.contains("}") && !line.matches(Constants.CLOSE_BRACE_ESCAPE)) {
                        if (stack.empty()) {
                            logger.warn("Block code parse exception: \n" + stackCodeStr);
                        } else {
                            stack.pop();
                            stackCodeStr = new StringBuilder();
                            if (stack.empty()) {
                                if (currentController == null) {
                                    continue;
                                }
                                // 一个class code block扫描完
                                controllerList.add(currentController);
                                for (EndpointModel endpoint : endpointList) {
                                    endpoint.setPath(currentController.getPath() + endpoint.getPath());
                                }
                                if (!endpointList.isEmpty()) {
                                    currentController.setEndpoints(endpointList);
                                    endpointList = new ArrayList<>();
                                }
                            } else {
                                // 一个class code block未扫描完
                                if (currentEndpoint != null) {
                                    endpointList.add(currentEndpoint);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Can not find file: " + file.getAbsolutePath(), e);
            throw new RuntimeException(e);
        }
        while (!stack.empty()) {
            logger.warn("Block code parse exception: \n" + stack.pop());
        }
    }
}
