package io.github.gitchenjh.endpointscanner.parser;

import io.github.gitchenjh.endpointscanner.model.ControllerModel;
import org.apache.maven.plugin.logging.Log;

import java.util.List;

import static io.github.gitchenjh.endpointscanner.constant.Constants.CONTROLLER;
import static io.github.gitchenjh.endpointscanner.constant.Constants.CONTROLLER_ESCAPE;
import static io.github.gitchenjh.endpointscanner.constant.Constants.REQUEST_MAPPING;
import static io.github.gitchenjh.endpointscanner.constant.Constants.REQUEST_MAPPING_ESCAPE;
import static io.github.gitchenjh.endpointscanner.constant.Constants.REST_CONTROLLER;
import static io.github.gitchenjh.endpointscanner.constant.Constants.REST_CONTROLLER_ESCAPE;

/**
 * @author <a href="mailto:chenjh1993@qq.com">chenjh</a>
 * @since 0.1.0
 */
public class ClassParser extends AbstractParser {

    private final Log logger;

    private final ControllerParser controllerParser;

    public ClassParser(Log logger, ControllerParser controllerParser) {
        this.logger = logger;
        this.controllerParser = controllerParser;
    }

    @Override
    public ControllerModel parse(String metaStr, String clazz, List<String> imports) {
        if (metaStr.contains(CONTROLLER) && !metaStr.matches(CONTROLLER_ESCAPE)) {
            return controllerParser.parse(metaStr, clazz, imports);
        } else if (metaStr.contains(REST_CONTROLLER) && !metaStr.matches(REST_CONTROLLER_ESCAPE)) {
            return controllerParser.parse(metaStr, clazz, imports);
        } else if (metaStr.contains(REQUEST_MAPPING) && !metaStr.matches(REQUEST_MAPPING_ESCAPE)) {
            return controllerParser.parse(metaStr, clazz, imports);
        }
        return null;
    }

}
