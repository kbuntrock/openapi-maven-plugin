# 📖 Usage Examples

This section provides practical examples of how to use the OpenAPI Maven Plugin in real-world scenarios.  
While the main documentation explains the available configuration options in detail, these examples are designed to give you **ready-to-use templates** that you can quickly adapt to your own projects.

The goals of this section are to:
- ✅ Demonstrate common plugin configurations.
- ✅ Show how to integrate the plugin into different project structures.
- ✅ Highlight best practices for generating OpenAPI specifications during your Maven build.
- ✅ Provide starting points that you can customize to match your needs.

Each example comes with:
- A description of the use case.
- A ``pom.xml`` snippet showing the plugin setup.

These examples are intended to help both **new users** get started quickly and **advanced users** discover more powerful configurations.

---
## Default error responses on all operations

This example demonstrates how to configure the plugin to add by default two responses (401 - Unauthorized and 404 - Not Found) on all generated operations.

1. Create a json file located at ``${project.basedir}/src/main/documentation.default-errors.json``  
It describes responses added on all operations.

Its content is:

```json
{
  "401": {
    "$ref": "#/components/responses/Unauthorized"
  },
  "404": {
    "$ref": "#/components/responses/NotFound"
  }
}
```

2. Create a json file located at ``${project.basedir}/src/main/openapi-template.json``  
It describes additional content added only one time to the generated documentation. It can be used to add various elements we cannot find in the source code (servers, security, ...)

Its content is:

```json
{
  "components": {
    "responses": {
      "Unauthorized": {
        "content": {
          "application/json": {
            "schema": {
              "$ref": "#/components/schemas/Error"
            }
          }
        },
        "description": "The called is unauthorized to access this resource."
      },
      "NotFound": {
        "content": {
          "application/json": {
            "schema": {
              "$ref": "#/components/schemas/Error"
            }
          }
        },
        "description": "The specified resource was not found."
      }
    },
    "schemas": {
      "Error": {
        "description": "An error object",
        "properties": {
          "code": {
            "description": "A technical error code",
            "type": "string"
          },
          "message": {
            "description": "A human readable error message",
            "type": "string"
          }
        },
        "required": [
          "code",
          "message"
        ],
        "type": "object"
      }
    }
  }
}
```

3. Then configure the plugin like this: 

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
                <freeFields>src/main/documentation/openapi-template.json</freeFields>
                <defaultErrors>src/main/documentation/default-errors.json</defaultErrors>
            </api>
        </apis>
    </configuration>
</plugin>
```

---
## Variation to generate default error responses on all operations

We can force the plugin to inspect a java class not explicitly used by the code.

For this example, we assume there is a java file called ``Error.java`` with the following implementation:

```java
package io.github.app;

public class Error {

    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```

1. Follow the step one of the previous example


2. Create a json file located at ``${project.basedir}/src/main/openapi-template.json``  
It describes additional content added only one time to the generated documentation. But this time, it will not descrive the **Error** schema.

Its content is:

```json
{
  "components": {
    "responses": {
      "Unauthorized": {
        "content": {
          "application/json": {
            "schema": {
              "$ref": "#/components/schemas/Error"
            }
          }
        },
        "description": "The called is unauthorized to access this resource."
      },
      "NotFound": {
        "content": {
          "application/json": {
            "schema": {
              "$ref": "#/components/schemas/Error"
            }
          }
        },
        "description": "The specified resource was not found."
      }
    }
  }
}
```

3. Then configure the plugin like this:

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
                <freeFields>src/main/documentation/openapi-template.json</freeFields>
                <defaultErrors>src/main/documentation/default-errors.json</defaultErrors>
                <extraSchemaClasses>
                    <!-- Here we add the Error class to the schema, even if we can't explicitly detect if it is used by an endpoint. -->
                    <class>io.github.app.Error</class>
                </extraSchemaClasses>
            </api>
        </apis>
    </configuration>
</plugin>
```


---
## Generating separate Public and Private API documentation

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

---
## Rename generated tags

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