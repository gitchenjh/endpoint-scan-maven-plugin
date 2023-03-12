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
import io.github.gitchenjh.model.EndpointModel;
import io.github.gitchenjh.util.JavaControllerDealer;
import io.github.gitchenjh.util.SourceFileUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
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
 * @author 高节
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
        List<EndpointModel> endpointList = new ArrayList<>();
        List<File> javaList = new SourceFileUtil(getLog(), new File(sourcePath)).getJavaSourceFile();
        for (File source : javaList){
            endpointList.addAll(new JavaControllerDealer(getLog(), source.getAbsolutePath()).parse());
        }
        String endpointStr = JSON.toJSONString(endpointList);
        getLog().info("扫描Endpoints结束==================================");
        File classesPath = new File(project.getBuild().getOutputDirectory());
        if (!classesPath.exists()) {
            classesPath.mkdirs();
        }
        File file = new File(classesPath, "endpoints.json");
        getLog().info("输出目录为：" + file.getAbsolutePath());
        try (OutputStreamWriter out = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            out.write(endpointStr);
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + file, e);
        }
    }
}
