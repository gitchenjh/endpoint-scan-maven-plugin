package io.github.gitchenjh.parser;

import io.github.gitchenjh.constant.Constants;
import io.github.gitchenjh.model.ControllerModel;
import io.github.gitchenjh.model.EndpointModel;
import io.github.gitchenjh.util.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.github.gitchenjh.constant.Constants.LINE_BREAK;

/**
 * @author 陈精华
 * @since 2023-03-11
 */
public class SourceFileParser {

    private static final String IMPORT = "import";
    private static final String PACKAGE = "package";
    private final Log logger;

    private final MethodParser methodParser;

    private final ControllerParser controllerParser;

    private final ClassParser classParser;

    private List<ControllerModel> controllerList = new CopyOnWriteArrayList<>();

    public List<ControllerModel> getControllerList() {
        return controllerList;
    }

    public SourceFileParser(Log logger) {
        this.logger = logger;
        this.methodParser = new MethodParser(logger);
        this.controllerParser = new ControllerParser(logger);
        this.classParser = new ClassParser(logger, controllerParser);
    }

    public void parse(File file) {
        controllerList = new CopyOnWriteArrayList<>();
        List<EndpointModel> endpointList = new CopyOnWriteArrayList<>();
        String pkg = "";
        String clazz = "";
        List<String> imports = new ArrayList<>();
        boolean gotHead = false;
        boolean gotClazz = false;
        StringBuffer stringBuffer = new StringBuffer();
        Stack<String> stack = new Stack<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            ControllerModel currentController = null;
            EndpointModel currentEndpoint = null;
            while ((line = reader.readLine()) != null) {
                line = StringUtils.trim(line);
                if ("".equals(line)) {
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
                    stringBuffer.append(line).append(LINE_BREAK);
                    if (!gotClazz && line.startsWith("public class ")) {
                        clazz = line.substring("public class ".length());
                        if (clazz.endsWith("{")) {
                            clazz = clazz.substring(0, clazz.length() - 1);
                        }
                        clazz = StringUtils.trim(clazz);
                        clazz = clazz.split(" ")[0];
                        gotClazz = true;
                    }
                    if ((line.startsWith("@") && !line.endsWith("{")) || line.startsWith("/**") || line.startsWith("//") || line.startsWith("*")
                            // 排除 lambda 语法
                            || line.matches("\\(+\\{+") || line.matches("\\}+\\)")) {
                        continue;
                    }
                    if ((line.contains("{") && !line.matches(Constants.OPEN_BRACE_ESCAPE)) || (line.contains("}") && !line.matches(Constants.CLOSE_BRACE_ESCAPE))) {
                        // TODO 待优化 一行中有 {***} 或 } *** { 的情况
                        continue;
                    }

                    if (line.contains("{") && !line.matches(Constants.OPEN_BRACE_ESCAPE)) {
                        if (stack.empty()) {
                            ControllerModel controller = classParser.parse(stringBuffer.toString(), pkg + "." + clazz, imports);
                            if (controller != null) {
                                currentController = controller;
                            }
                        } else {
                            currentEndpoint = methodParser.parse(stringBuffer.toString(), pkg + "." + clazz, imports);
                        }
                        stack.push(stringBuffer.toString());
                        stringBuffer = new StringBuffer();
                    }
                    if (line.contains("}") && !line.matches(Constants.CLOSE_BRACE_ESCAPE)) {
                        if (stack.empty()) {
                            logger.warn("代码解析异常，代码块如下：\n" + stringBuffer.toString());
                        } else {
                            String codeBlock = stack.pop() + stringBuffer;
                            stringBuffer = new StringBuffer();
                            if (stack.empty()) {
                                // 一个class code block扫描完
                                controllerList.add(currentController);
                                for (EndpointModel endpoint : endpointList) {
                                    endpoint.setPath(currentController.getPath() + endpoint.getPath());
                                }
                                if (endpointList.size() > 0) {
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
            logger.error("找不到文件：" + file.getAbsolutePath(), e);
            throw new RuntimeException(e);
        }
    }
}
