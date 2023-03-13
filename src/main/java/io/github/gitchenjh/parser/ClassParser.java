package io.github.gitchenjh.parser;

import io.github.gitchenjh.model.ControllerModel;
import org.apache.maven.plugin.logging.Log;

import java.util.List;

import static io.github.gitchenjh.constant.Constants.CONTROLLER;
import static io.github.gitchenjh.constant.Constants.CONTROLLER_ESCAPE;
import static io.github.gitchenjh.constant.Constants.REQUEST_MAPPING;
import static io.github.gitchenjh.constant.Constants.REQUEST_MAPPING_ESCAPE;
import static io.github.gitchenjh.constant.Constants.REST_CONTROLLER;
import static io.github.gitchenjh.constant.Constants.REST_CONTROLLER_ESCAPE;

/**
 * @author 陈精华
 * @since 2023-03-11
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
