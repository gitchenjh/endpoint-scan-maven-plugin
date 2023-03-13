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
    private List<EndpointModel> endpointList = new CopyOnWriteArrayList<>();

    public List<ControllerModel> getControllerList() {
        return controllerList;
    }

    public List<EndpointModel> getEndpointList() {
        return endpointList;
    }

    public SourceFileParser(Log logger) {
        this.logger = logger;
        this.methodParser = new MethodParser(logger);
        this.controllerParser = new ControllerParser(logger);
        this.classParser = new ClassParser(logger, controllerParser);
    }

    public void parse(File file) {
        controllerList = new CopyOnWriteArrayList<>();
        endpointList = new CopyOnWriteArrayList<>();
        String pkg = "";
        String clazz = "";
        List<String> imports = new ArrayList<>();
        boolean isHead = true;
        StringBuffer stringBuffer = new StringBuffer();
        Stack<String> stack = new Stack<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            ControllerModel currentController = null;
            EndpointModel currentEndpoint = null;
            List<ControllerModel> controllerList = new ArrayList<>();
            List<EndpointModel> endpointList = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                line = StringUtils.trim(line);
                if ("".equals(line)) {
                    continue;
                }
                if (isHead && !line.startsWith(IMPORT) && !line.startsWith(PACKAGE)) {
                    isHead = false;
                }
                if (isHead) {
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
                    if (line.startsWith("@")
                            || line.startsWith("/**")
                            || line.startsWith("*")) {
                        continue;
                    }
                    if (line.startsWith("public class ")) {
                        clazz = line.substring("public class ".length(), line.length());
                        if (clazz.endsWith("{")) {
                            clazz = clazz.substring(0, clazz.length() - 1);
                        }
                        clazz = StringUtils.trim(clazz);
                    }
                    if (line.contains("{") && !line.matches(Constants.OPEN_BRACE_ESCAPE)) {
                        if (stack.empty()) {
                            currentController = classParser.parse(stringBuffer.toString(), pkg + "." + clazz, imports);
                        } else {
                            currentEndpoint = methodParser.parse(stringBuffer.toString(), pkg + "." + clazz, imports);
                        }
                        stringBuffer = new StringBuffer();
                        stringBuffer.append(line);
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
                                ControllerModel finalCurrentController = currentController;
                                endpointList.forEach(endpoint -> endpoint.setPath(finalCurrentController.getPath() + endpoint.getPath()));
                                currentController.setEndpoints(endpointList);
                                endpointList = new ArrayList<>();
                            } else {
                                // 一个class code block未扫描完
                                endpointList.add(currentEndpoint);
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
