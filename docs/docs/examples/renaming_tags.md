---
sidebar_position: 3
sidebar_label: Renaming tags
---

# Rename generated tags

Tags are by default generated from the class name. But you might want a more user friendly name for your documentation.

This example demonstrate how to strip the suffix ``Controller`` at the end of each tag, and then add a space between each word delimided by an uppercase.

For example, the class ``AdminPanelController.java`` will then translate to a tag named ``Admin Panel``.

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
                <tag>
                    <substitutions>
                        <sub>
                            <regex>Controller$</regex>
                            <substitute></substitute>
                        </sub>
                        <sub>
                            <!-- Split with a space each time an uppercase character is found -->
                            <regex>(?&lt;!^)[A-Z]</regex>
                            <substitute xml:space="preserve"> $0</substitute>
                        </sub>
                    </substitutions>
                </tag>
            </api>
        </apis>
    </configuration>
</plugin>
```