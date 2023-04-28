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
package io.github.gitchenjh.endpointscanner.constant;

/**
 * @author <a href="mailto:chenjh1993@qq.com">chenjh</a>
 * @since 0.1.0
 */
public class Constants {
    public static final String OPEN_BRACE_ESCAPE = ".*\".*\\{.*\".*";
    public static final String CLOSE_BRACE_ESCAPE = ".*\".*\\}.*\".*";
    public static final String LINE_BREAK = "\r\n";
    public static final String SPECIAL_SYMBOLS = "≈Ω_Ω≈";
    public static final String DOT_JAVA = ".java";
    public static final String CONTROLLER = "@Controller";
    public static final String REST_CONTROLLER = "@RestController";
    public static final String API = "@Api";
    public static final String API_OPERATION = "@ApiOperation";
    public static final String CONTROLLER_ESCAPE = ".*\".*@Controller.*\".*";
    public static final String REST_CONTROLLER_ESCAPE = ".*\".*@RestController.*\".*";
    public static final String REQUEST_MAPPING_ESCAPE = ".*\".*@RequestMapping.*\".*";
    public static final String VALUE_STR = "value";
    public static final String TAGS_STR = "tags";
    public static final String PATH_STR = "path";
    public static final String METHOD_STR = "method";
    public static final String REQUEST_MAPPING = "@RequestMapping";
    public static final String GET_MAPPING = "@GetMapping";
    public static final String POST_MAPPING = "@PostMapping";
    public static final String PUT_MAPPING = "@PutMapping";
    public static final String DELETE_MAPPING = "@DeleteMapping";
    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_PUT = "PUT";
    public static final String HTTP_METHOD_DELETE = "DELETE";
}


