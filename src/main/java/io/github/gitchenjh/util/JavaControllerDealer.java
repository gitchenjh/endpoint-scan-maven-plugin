package io.github.gitchenjh.util;

import io.github.gitchenjh.model.EndpointModel;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 陈精华
 * @since 2023-03-11
 */
public class JavaControllerDealer {

    private static final String SEP = "\r\n";
    private static final String REQUEST_MAPPING = "@RequestMapping";
    private static final String POST_MAPPING = "@PostMapping";
    private static final String GET_MAPPING = "@GetMapping";

    private final Log logger;
    private final String path;
    private List<EndpointModel> endpointList = new ArrayList<>();

    public JavaControllerDealer(Log logger, String path) {
        this.logger = logger;
        this.path = path;
    }

    public List<EndpointModel> parse() {
        logger.info("正在处理：" + this.path);
        try {
            this.doParse();
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info("找到endpoint数量为："+this.endpointList.size());
        return this.endpointList;
    }

    public void doParse() throws IOException {
        Set<String> imports = new HashSet<>();
        boolean isHead = true;
        StringBuilder temp = new StringBuilder(); //用于连接行
        Stack<String> stack = new Stack<>();//用于提取类与方法

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.path), "UTF-8"));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = this.preDealStr(line);
            if ("".equals(line)) continue;
            if (isHead && !line.startsWith("import") && !line.startsWith("package")) {
                isHead = false;
            }

            if (isHead) {//处理类头
                String[] words = line.split(" ");
                if ("package".equals(words[0])) continue;//跳过包
                if ("import".equals(words[0]) && words.length >= 2) {//处理import
                    imports.add(words[1].endsWith(";") ? words[1].substring(0, words[1].length() - 1) : words[1]);
                    continue;
                }
            } else {
                temp.append(line).append(JavaControllerDealer.SEP);//将每行加到临时存放的StringBuilder中
                if (line.startsWith("@") || line.startsWith("/**") || line.startsWith("*")) {
                    // TODO: 暂时不需要处理
                } else if (line.contains("{") && !line.matches(".*\".*\\{.*\".*")) {
                    if (line.contains("}") && !line.matches(".*\".*\\}.*\".*")) {//处理单行包含一组
                        if (line.contains(REQUEST_MAPPING)||temp.toString().contains(REQUEST_MAPPING) ||
                                line.contains(POST_MAPPING)||temp.toString().contains(POST_MAPPING)||
                                line.contains(GET_MAPPING)||temp.toString().contains(GET_MAPPING)) {//单行方法
                            String methodStr = temp.toString();
                            parseMethod(methodStr);
                        }
                    }else{
                        stack.push(temp.toString());
                    }

                    temp = new StringBuilder();
                } else if (line.contains("}") && !line.matches(".*\".*\\}.*\".*")) {
                    String value = "";
                    if (!stack.isEmpty()) value=stack.pop();
                    if (value.contains("@Controller")||value.contains("@RestController")) {
                        //栈中类级别是最后弹出的，所以可以在解析类的时候为其他方法加上路径
                        String classStr = value;
                        logger.info("找到controller类：\n" + classStr);
                        parseClass(classStr);
                    } else if (value.contains(REQUEST_MAPPING) || value.contains(POST_MAPPING) || value.contains(GET_MAPPING)) {
                        String methodStr = value;
                        logger.info("找到endpoint方法：\n" + methodStr);
                        parseMethod(methodStr);
                    }
                    temp = new StringBuilder();
                } else {
                    temp.append(line).append(JavaControllerDealer.SEP);
                }
            }
        }
        br.close();
    }

    /**
     * 解析类
     * @param controller
     */
    private void parseClass(String controller) {
        String map = "";
        if(controller.contains(REQUEST_MAPPING) || controller.contains(POST_MAPPING) || controller.contains(GET_MAPPING)){
            map = controller.substring(controller.indexOf("(")+1,controller.indexOf(")"));
            map = map.replaceAll("\\s*","").replaceAll("\"","");
            if(map.contains(",")){
                String[] vs = map.split(",");
                for(String v : vs){
                    String[] kv = v.split("=");
                    if("value".equals(kv[0])){
                        map = kv[1];
                    }
                }
            }else {
                if(map.contains("=")){
                    String[] kv = map.split("=");
                    map = kv[1];
                }
            }
            map = map.endsWith("/")?map.substring(0,map.length()-1):map;
            map = map.startsWith("/")?map:("/"+map);
            logger.info("map:"+map);
        }
        for(EndpointModel a : this.endpointList){
            String mapPath = map+a.getPath();
            a.setPath(mapPath.startsWith("//")?mapPath.substring(1):mapPath);
        }
    }

    /**
     * 解析方法
     * @param controller
     */
    private void parseMethod(String controller){
        EndpointModel endpointModel = new EndpointModel();
        StringBuilder description = new StringBuilder();
        StringBuilder method = new StringBuilder();

        String[] lines = controller.split(JavaControllerDealer.SEP);
        int rmIndex = lines.length; //用于校验方法，方法写在多行
        for(int i=0; i< lines.length; i++){
            if(lines[i].startsWith("/**")){
                description = new StringBuilder().append(lines[i]).append(JavaControllerDealer.SEP);
            }else if (lines[i].startsWith("*")){
                description.append(lines[i]).append(JavaControllerDealer.SEP);
            }else if (lines[i].startsWith(REQUEST_MAPPING) || lines[i].startsWith(POST_MAPPING) || lines[i].startsWith(GET_MAPPING)){
                rmIndex = i;
                resolveMapping(lines[i],endpointModel); //解析RequestMapping
            }else if(i>rmIndex&&!lines[i].startsWith("@")){
                method.append(lines[i]);  //暂时不作解析 ////// TODO: 2016/9/30
            }
        }
        endpointModel.setDescription(resolveDescription(description.toString()));
        this.endpointList.add(endpointModel);
    }

    private String resolveDescription(String description){
        String[] ds = description.replace("*","").replace("/","").split("\r|\n");
        StringBuilder result = new StringBuilder();
        for (String d :ds){
            d = d.replaceAll("\\s","");
            if (d.length()>0&&!d.startsWith("@")){
                result.append(d);
            }
        }
        return result.toString();
    }

    private void resolveMapping(String str, EndpointModel endpointModel){
        if (str.indexOf("(") < 0 || str.indexOf(")") - str.indexOf("(") <= 3) {
            endpointModel.setPath("");
            if (str.startsWith(POST_MAPPING)) {
                endpointModel.setHttpMethod("POST");
            } else if (str.startsWith(GET_MAPPING)) {
                endpointModel.setHttpMethod("GET");
            }
            return;
        }
        String requestMapping = str.substring(str.indexOf("(")+1,str.indexOf(")")).replaceAll("\\s*","").replaceAll("\"","");
        if(requestMapping.contains(",")){
            String[] vs = requestMapping.split(",");
            for(String v : vs){
                String[] kv = v.split("=");
                if ("value".equals(kv[0])) {
                    endpointModel.setPath(kv[1].startsWith("/")?kv[1]:("/"+kv[1]));
                } else if("method".equals(kv[0])){
                    endpointModel.setHttpMethod(kv[1].contains(".")?kv[1].split("\\.")[1]:kv[1]);
                }
            }
        } else{
            if(requestMapping.contains("=")){
                String[] kv = requestMapping.split("=");
                endpointModel.setPath(kv[1].startsWith("/")?kv[1]:("/"+kv[1]));
            }else{
                endpointModel.setPath(requestMapping.startsWith("/") ? requestMapping : "/" + requestMapping);
            }
        }
    }

    private String preDealStr(String str) {
        Pattern p = Pattern.compile("\t|\r|\n");
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

}