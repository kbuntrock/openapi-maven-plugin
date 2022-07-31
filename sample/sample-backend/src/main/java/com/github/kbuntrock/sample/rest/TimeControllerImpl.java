package com.github.kbuntrock.sample.rest;

import com.github.kbuntrock.sample.dto.TimeDto;
import com.github.kbuntrock.sample.enpoint.TimeController;
import org.springframework.web.bind.annotation.RestController;

import java.time.*;

@RestController
public class TimeControllerImpl implements TimeController {

    @Override
    public TimeDto getTimeDto() {
        TimeDto timeDto = new TimeDto();
        timeDto.setDateTime(getCurrentDateTime());
        timeDto.setDate(getCurrentDate());
        timeDto.setInstant(getCurrentInstant());
        return timeDto;
    }

    @Override
    public Instant getCurrentInstant() {
        return Instant.now(Clock.systemUTC());
    }

    @Override
    public LocalDate getCurrentDate() {
        return LocalDate.now(ZoneOffset.UTC);
    }

    @Override
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public String setCurrentInstant(Instant instant) {
        return instant.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Europe/Paris"))
                .toLocalDateTime().toString();
    }

    @Override
    public String setCurrentDate(LocalDate date) {
        return date.toString();
    }

    @Override
    public String setCurrentDateTime(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Europe/Paris")).toLocalDateTime().toString();
    }


}
