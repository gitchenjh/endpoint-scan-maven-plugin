package io.github.gitchenjh.endpointscanner;

import io.github.gitchenjh.endpointscanner.model.ControllerModel;
import io.github.gitchenjh.endpointscanner.model.EndpointModel;
import io.github.gitchenjh.endpointscanner.parser.SourceFileParser;
import io.github.gitchenjh.endpointscanner.util.SourceFileUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * EndpointScannerMojo
 *
 * @author <a href="mailto:chenjh1993@qq.com">chenjh</a>
 * @since 0.1.0
 *
 * @threadSafe true
 * @phase process-classes
 * @goal scan
 */
public class EndpointScannerMojo extends AbstractMojo {

    /**
     * The Maven project sourceDirectory.
     *
     * @parameter property="project.build.sourceDirectory"
     * @required
     * @readonly
     */
    private String sourceDirectory;

    /**
     * The Maven project outputDirectory.
     *
     * @parameter property="project.build.outputDirectory"
     * @required
     * @readonly
     */
    private String outputDirectory;


    public void execute() throws MojoExecutionException {

        getLog().info("Scan endpoints start==================================");
        getLog().info("Source directory：" + sourceDirectory);

        SourceFileUtil sourceFileUtil = new SourceFileUtil(getLog(), new File(sourceDirectory));
        List<File> sourceFileList = sourceFileUtil.getJavaSourceFile();
        List<ControllerModel> controllers = new ArrayList<>();
        List<EndpointModel> endpoints = new ArrayList<>();

        for (File sourceFile : sourceFileList) {
            SourceFileParser sourceFileParser = new SourceFileParser(getLog());
            sourceFileParser.parse(sourceFile);
            List<ControllerModel> currentControllers = sourceFileParser.getControllerList();
            if (currentControllers != null && !currentControllers.isEmpty() && currentControllers.get(0) != null
                    && currentControllers.get(0).getEndpoints() != null
                    && !currentControllers.get(0).getEndpoints().isEmpty() && currentControllers.get(0).getEndpoints().get(0) != null) {                List<EndpointModel> currentEndpoints = new ArrayList<>();
                for (ControllerModel controllerModel : currentControllers) {
                    currentEndpoints.addAll(controllerModel.getEndpoints());
                }
                controllers.addAll(currentControllers);
                endpoints.addAll(currentEndpoints);
            }
        }

        getLog().info("Scan endpoints end==================================");
        File classesPath = new File(outputDirectory);
        if (!classesPath.exists()) {
            classesPath.mkdirs();
        }
        File controllersFile = new File(classesPath, "project-controllers.json");
        File endpointsFile = new File(classesPath, "project-endpoints.json");
        getLog().info("Writing controllers file: " + controllersFile.getAbsolutePath());
        getLog().info("Writing endpoints file：" + endpointsFile.getAbsolutePath());
        try (OutputStreamWriter controllerWriter = new OutputStreamWriter(Files.newOutputStream(controllersFile.toPath()), StandardCharsets.UTF_8)) {
            controllerWriter.write(controllers.toString());
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + controllersFile, e);
        }
        try (OutputStreamWriter endpointWriter = new OutputStreamWriter(Files.newOutputStream(endpointsFile.toPath()), StandardCharsets.UTF_8)) {
            endpointWriter.write(endpoints.toString());
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + endpointsFile, e);
        }
    }
}
