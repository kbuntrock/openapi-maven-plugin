package io.github.kbuntrock.model;

import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.nullsLast;

public class Response implements Comparable<Response> {
    private final Integer code;
    private final DataObject object;
    private final String description;
    private final List<String> formats;

    public Response(Integer code, DataObject object, String description, List<String> formats) {
        this.code = code;
        this.object = object;
        this.description = description;
        this.formats = formats;
    }

    @Override
    public int compareTo(final Response o) {
        return Comparator
                .comparing((Response response) -> response.code, nullsLast(Integer::compareTo))
                .compare(this, o);
    }

    public Integer getCode() {
        return code;
    }

    public DataObject getObject() {
        return object;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getFormats() {
        return formats;
    }
}
