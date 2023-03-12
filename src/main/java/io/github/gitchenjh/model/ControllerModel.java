package io.github.gitchenjh.model;

/**
 * @author 高节
 * @since 2023-03-12
 */
public class ControllerModel extends RequestMappingModel {

    public String pkg;

    public String description;

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
