package io.github.kbuntrock.yaml.model;

import io.github.kbuntrock.utils.OpenApiConstants;

import java.util.LinkedHashMap;
import java.util.Map;

public class Items {

    private Map<String, String> items = new LinkedHashMap<>();

    public Items(String type) {
        this.items.put(OpenApiConstants.TYPE, type);
    }

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }
}
