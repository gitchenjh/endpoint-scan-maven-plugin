package io.github.gitchenjh.util;

import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 高节
 * @since 2023-03-11
 */
public class PathDealer {
    private final Log logger;
    private final String sourcePath;

    public PathDealer(Log logger, String sourcePath) {
        this.logger = logger;
        this.sourcePath = sourcePath;
    }

    public List<String> getJavaList(){
        List<String> sourceFileList = new ArrayList<>();
        scanJava(sourcePath, sourceFileList);
        return sourceFileList;
    }

    private void scanJava(String path,List<String> list){
        File fd = new File(path);
        if(!fd.isDirectory()){//如果是文件
            String fileName = fd.getName();
            if(fileName.endsWith(".java")&&fileName.contains("Controller")){
                logger.info("扫描到.java文件："+path);
                list.add(path);
            }
        } else {
            String[] fileList =  fd.list();
            for (int i = 0; i < Objects.requireNonNull(fileList).length; i++) {
                path = fd.getAbsolutePath()+File.separator+fileList[i];
                scanJava(path, list);
            }
        }
    }

}
