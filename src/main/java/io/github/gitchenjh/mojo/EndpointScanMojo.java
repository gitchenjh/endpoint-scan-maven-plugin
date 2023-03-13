package io.github.gitchenjh.mojo;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.alibaba.fastjson.JSON;
import io.github.gitchenjh.model.ControllerModel;
import io.github.gitchenjh.model.EndpointModel;
import io.github.gitchenjh.parser.SourceFileParser;
import io.github.gitchenjh.util.SourceFileUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 陈精华
 * @since 2023-03-11
 */
@Mojo(name = "scan", threadSafe = true)
@Execute(goal = "scan", phase = LifecyclePhase.PROCESS_CLASSES)
public class EndpointScanMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    public void execute() throws MojoExecutionException {

        getLog().info("开始扫描Endpoints==================================");
        String sourcePath = project.getBuild().getSourceDirectory();
        getLog().info("源码目录为：" + sourcePath);

        SourceFileUtil sourceFileUtil = new SourceFileUtil(getLog(), new File(sourcePath));
        List<File> sourceFileList = sourceFileUtil.getJavaSourceFile();
        List<ControllerModel> controllers = new ArrayList<>();
        List<EndpointModel> endpoints = new ArrayList<>();

        for (File sourceFile : sourceFileList) {
            SourceFileParser sourceFileParser = new SourceFileParser(getLog());
            sourceFileParser.parse(sourceFile);
            List<ControllerModel> currentControllers = sourceFileParser.getControllerList();
            List<EndpointModel> currentEndpoints = new ArrayList<>();
            for (ControllerModel controllerModel : currentControllers) {
                if (controllerModel != null && controllerModel.getEndpoints() != null) {
                    currentEndpoints.addAll(controllerModel.getEndpoints());
                }
            }
            controllers.addAll(currentControllers);
            endpoints.addAll(currentEndpoints);
        }

        getLog().info("扫描Endpoints结束==================================");
        File classesPath = new File(project.getBuild().getOutputDirectory());
        if (!classesPath.exists()) {
            classesPath.mkdirs();
        }
        File controllersFile = new File(classesPath, "controllers.json");
        File endpointsFile = new File(classesPath, "endpoints.json");
        getLog().info("输出controllers文件为：" + controllersFile.getAbsolutePath());
        getLog().info("输出endpoints文件为：" + endpointsFile.getAbsolutePath());
        try (OutputStreamWriter controllerWriter = new OutputStreamWriter(Files.newOutputStream(controllersFile.toPath()), StandardCharsets.UTF_8)) {
            controllerWriter.write(JSON.toJSONString(controllers));
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + controllersFile, e);
        }
        try (OutputStreamWriter endpointWriter = new OutputStreamWriter(Files.newOutputStream(endpointsFile.toPath()), StandardCharsets.UTF_8)) {
            endpointWriter.write(JSON.toJSONString(endpoints));
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + endpointsFile, e);
        }
    }

    public static void main(String[] args) {
        Log logger = new Log() {
            @Override
            public boolean isDebugEnabled() {
                return false;
            }

            @Override
            public void debug(CharSequence charSequence) {

            }

            @Override
            public void debug(CharSequence charSequence, Throwable throwable) {

            }

            @Override
            public void debug(Throwable throwable) {

            }

            @Override
            public boolean isInfoEnabled() {
                return false;
            }

            @Override
            public void info(CharSequence charSequence) {

            }

            @Override
            public void info(CharSequence charSequence, Throwable throwable) {

            }

            @Override
            public void info(Throwable throwable) {

            }

            @Override
            public boolean isWarnEnabled() {
                return false;
            }

            @Override
            public void warn(CharSequence charSequence) {

            }

            @Override
            public void warn(CharSequence charSequence, Throwable throwable) {

            }

            @Override
            public void warn(Throwable throwable) {

            }

            @Override
            public boolean isErrorEnabled() {
                return false;
            }

            @Override
            public void error(CharSequence charSequence) {

            }

            @Override
            public void error(CharSequence charSequence, Throwable throwable) {

            }

            @Override
            public void error(Throwable throwable) {

            }
        };
        logger.info("开始扫描Endpoints==================================");
        String sourcePath = "D:\\workspace\\xxxx-project\\src\\main\\java";
        logger.info("源码目录为：" + sourcePath);

        SourceFileUtil sourceFileUtil = new SourceFileUtil(logger, new File(sourcePath));
        List<File> sourceFileList = sourceFileUtil.getJavaSourceFile();
        List<ControllerModel> controllers = new ArrayList<>();
        List<EndpointModel> endpoints = new ArrayList<>();

        for (File sourceFile : sourceFileList) {
            SourceFileParser sourceFileParser = new SourceFileParser(logger);
            sourceFileParser.parse(sourceFile);
            List<ControllerModel> currentControllers = sourceFileParser.getControllerList();
            List<EndpointModel> currentEndpoints = new ArrayList<>();
            for (ControllerModel controllerModel : currentControllers) {
                if (controllerModel != null && controllerModel.getEndpoints() != null) {
                    currentEndpoints.addAll(controllerModel.getEndpoints());
                }
            }
            controllers.addAll(currentControllers);
            endpoints.addAll(currentEndpoints);
        }

        logger.info("扫描Endpoints结束==================================");
        File classesPath = new File("D:\\workspace\\xxxx-project\\target\\classes");
        if (!classesPath.exists()) {
            classesPath.mkdirs();
        }
        File controllersFile = new File(classesPath, "controllers.json");
        File endpointsFile = new File(classesPath, "endpoints.json");
        logger.info("输出controllers文件为：" + controllersFile.getAbsolutePath());
        logger.info("输出endpoints文件为：" + endpointsFile.getAbsolutePath());
        try (OutputStreamWriter controllerWriter = new OutputStreamWriter(Files.newOutputStream(controllersFile.toPath()), StandardCharsets.UTF_8)) {
            controllerWriter.write(JSON.toJSONString(controllers));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (OutputStreamWriter endpointWriter = new OutputStreamWriter(Files.newOutputStream(endpointsFile.toPath()), StandardCharsets.UTF_8)) {
            endpointWriter.write(JSON.toJSONString(endpoints));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
