---
sidebar_position: 2
sidebar_label: Documentations multiples
---

# Générer deux documentations d'API: une publique et une privée

Cet exemple montre comment configurer le plugin pour générer deux spécifications OpenAPI distinctes à partir du même projet :
- une pour l’API **privée**,
- une autre pour l’API **publique**,

Le tout en gardant le processus rapide (une seule exécution du plugin).

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