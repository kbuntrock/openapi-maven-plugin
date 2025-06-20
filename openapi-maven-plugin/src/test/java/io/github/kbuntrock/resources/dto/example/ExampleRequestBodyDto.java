package io.github.kbuntrock.resources.dto.example;

import java.util.List;

public class ExampleRequestBodyDto {
    private List<ExampleEnum> requestEnumList;
    private String requestName;
    private String requestValue;

    public List<ExampleEnum> getRequestEnumList() {
        return requestEnumList;
    }

    public void setRequestEnumList(List<ExampleEnum> requestEnumList) {
        this.requestEnumList = requestEnumList;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRequestValue() {
        return requestValue;
    }

    public void setRequestValue(String requestValue) {
        this.requestValue = requestValue;
    }
}
