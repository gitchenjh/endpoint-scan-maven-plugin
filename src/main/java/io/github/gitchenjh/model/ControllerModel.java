package io.github.gitchenjh.model;

import java.util.List;

/**
 * @author 陈精华
 * @since 2023-03-12
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
}
