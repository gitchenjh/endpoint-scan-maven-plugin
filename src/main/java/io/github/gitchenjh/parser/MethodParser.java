package io.github.gitchenjh.parser;

import io.github.gitchenjh.model.EndpointModel;
import org.apache.maven.plugin.logging.Log;

import java.util.List;

import static io.github.gitchenjh.constant.Constants.LINE_BREAK;
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
            if (line.startsWith(REQUEST_MAPPING)) {
                resolveMapping(line, endpointModel);
                String desc = resolveDescription(metaStr);
                endpointModel.setDescription(desc);
                return endpointModel;
            }
        }
        return null;
    }
}
