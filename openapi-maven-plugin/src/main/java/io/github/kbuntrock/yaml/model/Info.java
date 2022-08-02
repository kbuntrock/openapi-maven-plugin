package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public class Info {

    @JsonIgnore
    private static String INFO_FIELD = "info";

    private String title;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String description;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String termsOfService;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object contact;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object license;
    private String version;

    public Info(String title, String version, Optional<JsonNode> freefields) {
        this.title = title;
        this.version = version;
        if (freefields.isPresent() && freefields.get().get(INFO_FIELD) != null) {
            JsonNode infos = freefields.get().get(INFO_FIELD);
            if (infos.get("title") != null) {
                this.title = infos.get("title").textValue();
            }
            if (infos.get("version") != null) {
                this.version = infos.get("version").textValue();
            }
            if (infos.get("description") != null) {
                this.description = infos.get("description").textValue();
            }
            if (infos.get("termsOfService") != null) {
                this.termsOfService = infos.get("termsOfService").textValue();
            }
            if (infos.get("contact") != null) {
                this.contact = infos.get("contact");
            }
            if (infos.get("license") != null) {
                this.license = infos.get("license");
            }

        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermsOfService() {
        return termsOfService;
    }

    public void setTermsOfService(String termsOfService) {
        this.termsOfService = termsOfService;
    }

    public Object getContact() {
        return contact;
    }

    public void setContact(Object contact) {
        this.contact = contact;
    }

    public Object getLicense() {
        return license;
    }

    public void setLicense(Object license) {
        this.license = license;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
