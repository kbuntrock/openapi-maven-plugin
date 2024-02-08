package io.github.kbuntrock.model;

public class ExternalDocs {
    private String externalDocUrl;
    private String externalDocDescription;

    public ExternalDocs(String externalDocUrl, String externalDocDescription) {
        this.externalDocUrl = externalDocUrl;
        this.externalDocDescription = externalDocDescription;
    }

    public String getExternalDocUrl() {
        return externalDocUrl;
    }

    public void setExternalDocUrl(String externalDocUrl) {
        this.externalDocUrl = externalDocUrl;
    }

    public String getExternalDocDescription() {
        return externalDocDescription;
    }

    public void setExternalDocDescription(String externalDocDescription) {
        this.externalDocDescription = externalDocDescription;
    }
}
