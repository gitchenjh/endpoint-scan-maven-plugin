package io.github.gitchenjh.util;

import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.gitchenjh.constant.Constants.CONTROLLER;
import static io.github.gitchenjh.constant.Constants.DOT_JAVA;

/**
 * @author 陈精华
 * @since 2023-03-11
 */
public class SourceFileUtil {

    private final Log logger;
    private final File sourcePath;

    public SourceFileUtil(Log logger, File sourcePath) {
        this.logger = logger;
        this.sourcePath = sourcePath;
    }

    public List<File> getJavaSourceFile(){
        List<File> sourceFileList = new ArrayList<>();
        scanJavaSourceFile(sourcePath, sourceFileList);
        return sourceFileList;
    }

    public void scanJavaSourceFile(File file, List<File> list){
        if (!file.isDirectory()) {
            String fileName = file.getName();
            if (fileName.endsWith(DOT_JAVA)) {
                logger.info("扫描到java文件：" + file.getAbsolutePath());
                list.add(file);
            }
        } else {
            File[] subList = file.listFiles();
            if (Objects.isNull(subList) || subList.length == 0) {
                return;
            }
            for (File subFile : subList) {
                scanJavaSourceFile(subFile, list);
            }
        }
    }
}
