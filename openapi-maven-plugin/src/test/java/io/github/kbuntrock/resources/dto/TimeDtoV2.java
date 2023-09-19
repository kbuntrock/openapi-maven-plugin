package io.github.kbuntrock.resources.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeDtoV2 {

	Instant instant;
	LocalDate date;
	LocalDateTime dateTime;
	LocalTime time;

	public Instant getInstant() {
		return instant;
	}

	public void setInstant(final Instant instant) {
		this.instant = instant;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(final LocalDate date) {
		this.date = date;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(final LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(final LocalTime time) {
		this.time = time;
	}
}
