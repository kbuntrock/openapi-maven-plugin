---
sidebar_position: 2
sidebar_label: Multiple documentations
---

# Generating separate Public and Private API documentation

This example demonstrates how to configure the plugin to generate two distinct OpenAPI specifications from the same project: one for a private API and another for a public API, while keeping the generation as fast as possible (no double plugin execution).

```xml
<plugin>
    <groupId>io.github.kbuntrock</groupId>
    <artifactId>openapi-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>documentation</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <apis>
            <api>
                <locations>
                    <location>io.github.app</location>
                </locations>
                <whiteList>
                    <!-- All controllers in the "admin" and "healthcheck" packages  -->
                    <entry>io.github.app.api.endpoint.admin.*</entry>
                    <entry>io.github.app.api.endpoint.healthcheck.*</entry>
                </whiteList>
                <filename>private-documentation.yml</filename>
            </api>
            <api>
                <locations>
                    <location>io.github.app</location>
                </locations>
                <blackList>
                    <!-- All controllers not in the "admin" and "healthcheck" packages  -->
                    <entry>io.github.app.api.endpoint.admin.*</entry>
                    <entry>io.github.app.api.endpoint.healthcheck.*</entry>
                </blackList>
                <filename>public-documentation.yml</filename>
            </api>
        </apis>
    </configuration>
</plugin>
```