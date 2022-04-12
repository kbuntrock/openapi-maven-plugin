package com.github.kbuntrock.yaml.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Items {

    private Map<String, String> items = new LinkedHashMap<>();

    // TODO : manque le format

    public Items(String type) {
        this.items.put("type", type);
    }

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }
}
