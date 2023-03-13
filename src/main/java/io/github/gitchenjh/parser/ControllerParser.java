package io.github.gitchenjh.parser;

import io.github.gitchenjh.model.ControllerModel;
import org.apache.maven.plugin.logging.Log;

import java.util.List;

import static io.github.gitchenjh.constant.Constants.LINE_BREAK;
import static io.github.gitchenjh.constant.Constants.REQUEST_MAPPING;

/**
 * @author 陈精华
 * @since 2023-03-11
 */
public class ControllerParser extends AbstractParser {

    private final Log logger;

    public ControllerParser(Log logger) {
        this.logger = logger;
    }

    @Override
    public ControllerModel parse(String metaStr, String clazz, List<String> imports) {
        logger.info("找到Controller：\n" + metaStr);
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
