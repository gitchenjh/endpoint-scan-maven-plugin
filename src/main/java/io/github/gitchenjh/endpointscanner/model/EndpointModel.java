package io.github.gitchenjh.endpointscanner.model;

/**
 * @author <a href="mailto:chenjh1993@qq.com">chenjh</a>
 * @since 0.1.0
 */
public class EndpointModel extends RequestMappingModel {
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "{" +
                "\"path\":\"" + getPath() + "\"" +
                ", \"httpMethod\":\"" + getHttpMethod() + "\"" +
                ", \"description\":\"" + description  +
                "\"}";
    }
}
