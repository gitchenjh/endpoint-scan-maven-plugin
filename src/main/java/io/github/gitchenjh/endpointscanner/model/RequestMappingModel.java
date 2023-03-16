package io.github.gitchenjh.endpointscanner.model;

/**
 * @author <a href="mailto:chenjh1993@qq.com">chenjh</a>
 * @since 0.1.0
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
