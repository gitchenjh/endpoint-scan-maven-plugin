package io.github.gitchenjh.parser;

import io.github.gitchenjh.model.ControllerModel;
import org.apache.maven.plugin.logging.Log;

import static io.github.gitchenjh.constant.Constants.LINE_BREAK;
import static io.github.gitchenjh.constant.Constants.REQUEST_MAPPING;

/**
 * @author 高节
 * @since 2023-03-11
 */
public class MethodParser extends AbstractParser {

    private final Log logger;

    public MethodParser(Log logger) {
        this.logger = logger;
    }

    @Override
    public void parse(String metaStr) {
        ControllerModel controllerParser = new ControllerModel();
        String[] lines = metaStr.split(LINE_BREAK);
        for (String line : lines) {
            if (line.startsWith(REQUEST_MAPPING)) {
                resolveMapping(line, controllerParser);
                break;
            }
        }
    }
}
