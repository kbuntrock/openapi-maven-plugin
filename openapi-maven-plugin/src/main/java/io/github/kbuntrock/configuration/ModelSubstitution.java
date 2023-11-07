package io.github.kbuntrock.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.Map;
import java.util.Objects;

public class ModelSubstitution implements Map.Entry<String, String> {

    @Parameter
    protected String from;

    @Parameter
    protected String to;

    @Override
    public String getKey() {
        return from;
    }

    @Override
    public String getValue() {
        return to;
    }

    @Override
    public String setValue(String value) {
        String old = to;
        to = value;
        return old;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelSubstitution that = (ModelSubstitution) o;

        return Objects.equals(from, that.from);
    }

    @Override
    public int hashCode() {
        return from != null ? from.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ModelSubstitution{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
