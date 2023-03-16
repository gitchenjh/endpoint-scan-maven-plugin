package io.github.gitchenjh.endpointscanner.parser;

import io.github.gitchenjh.endpointscanner.constant.Constants;
import io.github.gitchenjh.endpointscanner.model.EndpointModel;
import org.apache.maven.plugin.logging.Log;

import java.util.List;

/**
 * @author <a href="mailto:chenjh1993@qq.com">chenjh</a>
 * @since 0.1.0
 */
public class MethodParser extends AbstractParser {

    private final Log logger;

    public MethodParser(Log logger) {
        this.logger = logger;
    }

    @Override
    public EndpointModel parse(String metaStr, String clazz, List<String> imports) {
        EndpointModel endpointModel = new EndpointModel();
        String[] lines = metaStr.split(Constants.LINE_BREAK);
        for (String line : lines) {
            if (line.startsWith(Constants.REQUEST_MAPPING)
                    || line.startsWith(Constants.GET_MAPPING) || line.startsWith(Constants.POST_MAPPING)
                    || line.startsWith(Constants.PUT_MAPPING) || line.startsWith(Constants.DELETE_MAPPING)) {
                logger.debug("Found endpoint：\n" + metaStr);
                resolveMapping(line, endpointModel);
                String desc = resolveDescription(metaStr);
                endpointModel.setDescription(desc);
                return endpointModel;
            }
        }
        if (metaStr.contains(Constants.API_OPERATION)) {
            String desc = resolveDescription(metaStr);
            endpointModel.setDescription(desc);
            return endpointModel;
        }
        return null;
    }
}
