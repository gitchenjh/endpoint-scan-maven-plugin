package io.github.gitchenjh.model;

import java.util.List;

/**
 * @author 高节
 * @since 2023-03-11
 */
public class EndpointModel extends RequestMappingModel {
    private String description;
    private List<String> parameters;
    private String returnType;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

}
