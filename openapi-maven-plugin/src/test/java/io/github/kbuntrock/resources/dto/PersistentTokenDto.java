package io.github.kbuntrock.resources.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * A remember-me token, always attached to the requesting user
 */
public class PersistentTokenDto {

    @NotNull
    private String series;

    @NotNull
    private LocalDate tokenDate;

    private String ipAddress;

    private String userAgent;

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public LocalDate getTokenDate() {
        return tokenDate;
    }

    public void setTokenDate(LocalDate tokenDate) {
        this.tokenDate = tokenDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
