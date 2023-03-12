package io.github.gitchenjh.parser;

import org.apache.maven.plugin.logging.Log;

import static io.github.gitchenjh.constant.Constants.CONTROLLER;
import static io.github.gitchenjh.constant.Constants.CONTROLLER_ESCAPE;
import static io.github.gitchenjh.constant.Constants.REST_CONTROLLER;
import static io.github.gitchenjh.constant.Constants.REST_CONTROLLER_ESCAPE;

/**
 * @author 高节
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
    public void parse(String metaStr) {
        String map = "";
        if (metaStr.contains(CONTROLLER) && !metaStr.matches(CONTROLLER_ESCAPE)) {
            controllerParser.parse(metaStr);
        } else if (metaStr.contains(REST_CONTROLLER) && metaStr.matches(REST_CONTROLLER_ESCAPE)) {
            controllerParser.parse(metaStr);
        }
    }

}
