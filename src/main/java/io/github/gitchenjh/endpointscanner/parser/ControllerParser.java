package io.github.gitchenjh.endpointscanner.parser;

import io.github.gitchenjh.endpointscanner.model.ControllerModel;
import org.apache.maven.plugin.logging.Log;

import java.util.List;

import static io.github.gitchenjh.endpointscanner.constant.Constants.LINE_BREAK;
import static io.github.gitchenjh.endpointscanner.constant.Constants.REQUEST_MAPPING;

/**
 * @author <a href="mailto:chenjh1993@qq.com">chenjh</a>
 * @since 0.1.0
 */
public class ControllerParser extends AbstractParser {

    private final Log logger;

    public ControllerParser(Log logger) {
        this.logger = logger;
    }

    @Override
    public ControllerModel parse(String metaStr, String clazz, List<String> imports) {
        logger.debug("Found controller：\n" + metaStr);
        ControllerModel controllerModel = new ControllerModel();
        String[] lines = metaStr.split(LINE_BREAK);
        for (String line : lines) {
            if (line.startsWith(REQUEST_MAPPING)) {
                resolveMapping(line, controllerModel);
            }
        }
        String desc = resolveDescription(metaStr);
        controllerModel.setClazz(clazz);
        controllerModel.setDescription(desc);
        return controllerModel;
    }
}
