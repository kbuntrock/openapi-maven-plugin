package io.github.kbuntrock.resources.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * An wrapping object for time values
 */
public class TimeDto {

	/**
	 * The instant field
	 */
	Instant instant;
	/**
	 * The date field
	 */
	LocalDate date;
	/**
	 * The date time field
	 */
	LocalDateTime dateTime;

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
}
