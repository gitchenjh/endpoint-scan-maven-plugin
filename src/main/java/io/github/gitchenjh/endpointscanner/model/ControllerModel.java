/*
 * Copyright [2023] [gitchenjh]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
