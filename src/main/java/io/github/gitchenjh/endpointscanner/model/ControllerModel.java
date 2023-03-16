package io.github.gitchenjh.endpointscanner.model;

import java.util.List;

/**
 * @author <a href="mailto:chenjh1993@qq.com">chenjh</a>
 * @since 0.1.0
 */
public class ControllerModel extends RequestMappingModel {

    public String clazz;

    public String description;

    public List<EndpointModel> endpoints;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EndpointModel> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<EndpointModel> endpoints) {
        this.endpoints = endpoints;
    }

    @Override
    public String toString() {
        return "{" +
                "\"clazz\":\"" + clazz + "\"" +
                ", \"path\":\"" + getPath() + "\"" +
                ", \"httpMethod\":\"" + getHttpMethod() + "\"" +
                ", \"description\":\"" + description + "\"" +
                ", \"endpoints\":" + (endpoints == null ? "[]" : endpoints.toString()) +
                "}";
    }
}
