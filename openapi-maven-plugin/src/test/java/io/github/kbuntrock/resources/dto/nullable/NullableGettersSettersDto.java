package io.github.kbuntrock.resources.dto.nullable;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class NullableGettersSettersDto {

    private String noAnnotationsValue;

    private String nullableOnGetterValue;

    private String nullableOnSetterValue;

    private String notNullOnGetterValue;

    private String notNullOnSetterValue;

    public String getNoAnnotationsValue() {
        return noAnnotationsValue;
    }

    public void setNoAnnotationsValue(String noAnnotationsValue) {
        this.noAnnotationsValue = noAnnotationsValue;
    }

    @Nullable
    public String getNullableOnGetterValue() {
        return nullableOnGetterValue;
    }

    public void setNullableOnGetterValue(String nullableOnGetterValue) {
        this.nullableOnGetterValue = nullableOnGetterValue;
    }

    public String getNullableOnSetterValue() {
        return nullableOnSetterValue;
    }

    @Nullable
    public void setNullableOnSetterValue(String nullableOnSetterValue) {
        this.nullableOnSetterValue = nullableOnSetterValue;
    }

    @NotNull
    public String getNotNullOnGetterValue() {
        return notNullOnGetterValue;
    }

    public void setNotNullOnGetterValue(String notNullOnGetterValue) {
        this.notNullOnGetterValue = notNullOnGetterValue;
    }

    public String getNotNullOnSetterValue() {
        return notNullOnSetterValue;
    }

    @NotNull
    public void setNotNullOnSetterValue(String notNullOnSetterValue) {
        this.notNullOnSetterValue = notNullOnSetterValue;
    }
}
