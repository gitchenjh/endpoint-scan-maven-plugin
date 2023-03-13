package io.github.gitchenjh.model;

/**
 * @author 陈精华
 * @since 2023-03-12
 */
public class RequestMappingModel {

    private String path = "";

    private String httpMethod;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
}
