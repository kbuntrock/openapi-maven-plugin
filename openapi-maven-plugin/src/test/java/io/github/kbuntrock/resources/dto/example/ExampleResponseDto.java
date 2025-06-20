package io.github.kbuntrock.resources.dto.example;

public class ExampleResponseDto {
    private ExampleEnum responseEnum;
    private String responseName;
    private String responseValue;

    public ExampleEnum getResponseEnum() {
        return responseEnum;
    }

    public void setResponseEnum(ExampleEnum responseEnum) {
        this.responseEnum = responseEnum;
    }

    public String getResponseName() {
        return responseName;
    }

    public void setResponseName(String responseName) {
        this.responseName = responseName;
    }

    public String getResponseValue() {
        return responseValue;
    }

    public void setResponseValue(String responseValue) {
        this.responseValue = responseValue;
    }
}
