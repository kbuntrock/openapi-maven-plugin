---
sidebar_position: 1
sidebar_label: Default errors
---

# Default error responses on all operations

This example demonstrates how to configure the plugin to add by default two responses (401 - Unauthorized and 404 - Not Found) on all generated operations.

## Initial method

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
## Variation

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