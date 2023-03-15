# endpoint-scan-maven-plugin

A maven plugin to scan Controllers and Endpoints in your project

## how to use

Add this plugin to `plugins` tag in your maven `pom.xml` file

```xml
<plugin>
    <groupId>io.github.gitchenjh</groupId>
    <artifactId>endpoint-scan-maven-plugin</artifactId>
    <version>${latest-version}</version>
    <executions>
        <execution>
            <phase>process-classes</phase>
            <goals>
                <goal>scan</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Then you can compile your project as usual

After compile you can find two file named as `project-controllers` and `project-endpoints` in project output folder (`target/class`) which contains the controller and endpoint information in you project