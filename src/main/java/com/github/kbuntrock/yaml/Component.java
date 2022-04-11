package com.github.kbuntrock.yaml;

import java.util.List;
import java.util.Map;

public class Component {

    Map<String, ComponentSchema> schemas;

    public Map<String, ComponentSchema> getSchemas() {
        return schemas;
    }

    public void setSchemas(Map<String, ComponentSchema> schemas) {
        this.schemas = schemas;
    }
}
