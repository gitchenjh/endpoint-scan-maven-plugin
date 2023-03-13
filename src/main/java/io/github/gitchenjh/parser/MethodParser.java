package io.github.gitchenjh.parser;

import io.github.gitchenjh.model.EndpointModel;
import org.apache.maven.plugin.logging.Log;

import java.util.List;

import static io.github.gitchenjh.constant.Constants.API_OPERATION;
import static io.github.gitchenjh.constant.Constants.DELETE_MAPPING;
import static io.github.gitchenjh.constant.Constants.GET_MAPPING;
import static io.github.gitchenjh.constant.Constants.LINE_BREAK;
import static io.github.gitchenjh.constant.Constants.POST_MAPPING;
import static io.github.gitchenjh.constant.Constants.PUT_MAPPING;
import static io.github.gitchenjh.constant.Constants.REQUEST_MAPPING;

/**
 * @author 陈精华
 * @since 2023-03-11
 */
public class MethodParser extends AbstractParser {

    private final Log logger;

    public MethodParser(Log logger) {
        this.logger = logger;
    }

    @Override
    public EndpointModel parse(String metaStr, String clazz, List<String> imports) {
        EndpointModel endpointModel = new EndpointModel();
        String[] lines = metaStr.split(LINE_BREAK);
        for (String line : lines) {
            if (line.startsWith(REQUEST_MAPPING)
                    || line.startsWith(GET_MAPPING) || line.startsWith(POST_MAPPING)
                    || line.startsWith(PUT_MAPPING) || line.startsWith(DELETE_MAPPING)) {
                logger.info("找到Endpoint：\n" + metaStr);
                resolveMapping(line, endpointModel);
                return endpointModel;
            }
        }
        String desc = resolveDescription(metaStr);
        endpointModel.setDescription(desc);
        if (metaStr.contains(API_OPERATION)) {
            endpointModel.setPath("");
            return endpointModel;
        } else {
            if ("".equals(endpointModel.getPath())) {
                return null;
            } else {
                return endpointModel;
            }
        }
    }
}
