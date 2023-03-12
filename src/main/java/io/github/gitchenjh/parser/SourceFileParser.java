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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import static io.github.gitchenjh.constant.Constants.LINE_BREAK;

/**
 * @author 高节
 * @since 2023-03-11
 */
public class SourceFileParser {

    private static final String IMPORT = "import";
    private static final String PACKAGE = "package";

    private final Log logger;

    public SourceFileParser(Log logger) {
        this.logger = logger;
    }

    public void parse(File file) {
        String pkg = "";
        Set<String> imports = new HashSet<>();
        boolean isHead = true;
        StringBuffer temp = new StringBuffer();
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
                if (isHead) {//处理类头
                    String[] words = line.split(" ");
                    if (PACKAGE.equals(words[0]) && words.length == 2) {
                        pkg = words[1].endsWith(";") ? words[1].substring(0, words[1].length() - 1) : words[1];
                        continue;
                    }
                    if (IMPORT.equals(words[0]) && words.length == 2) {
                        imports.add(words[1].endsWith(";") ? words[1].substring(0, words[1].length() - 1) : words[1]);
                    }
                } else {
                    temp.append(line).append(LINE_BREAK);
                    if (line.startsWith("@")
                            || line.startsWith("/**")
                            || line.startsWith("*")) {
                        continue;
                    }
                    if (line.contains("{") && !line.matches(Constants.OPEN_BRACE_ESCAPE)) {
                        if (stack.empty()) {
//                            currentController = parseClass(temp.toString(), );
                            ControllerModel controller = new ControllerModel();
                        } else {
//                            currentEndpoint = parseMethod(temp.toString());
                            EndpointModel endpoint = new EndpointModel();
                        }
                        temp = new StringBuffer();
                        temp.append(line);
                    }
                    if (line.contains("}") && !line.matches(Constants.CLOSE_BRACE_ESCAPE)) {
                        if (stack.empty()) {
                            logger.warn("代码解析异常，代码块如下：\n" + temp.toString());
                        } else {
                            String codeBlock = stack.pop() + temp;
                            temp = new StringBuffer();
                            if (stack.empty()) {
                                // 一个class code block扫描完
                                controllerList.add(currentController);
                            } else {
                                // 一个class code block未扫描完
                                endpointList.add(currentEndpoint);
                                endpointList = new ArrayList<>();
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
