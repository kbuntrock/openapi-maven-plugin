package com.github.kbuntrock.yaml;

import java.util.HashMap;
import java.util.Map;

public class Items {

    private Map<String, String> items = new HashMap<>();

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
