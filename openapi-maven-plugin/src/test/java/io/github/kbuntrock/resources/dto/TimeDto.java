package io.github.kbuntrock.resources.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimeDto {

    Instant instant;
    LocalDate date;
    LocalDateTime dateTime;

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
