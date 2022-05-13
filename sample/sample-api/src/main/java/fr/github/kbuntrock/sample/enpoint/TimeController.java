package fr.github.kbuntrock.sample.enpoint;

import fr.github.kbuntrock.sample.Constants;
import fr.github.kbuntrock.sample.dto.TimeDto;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequestMapping(Constants.BASE_PATH + "/time")
public interface TimeController {

    @GetMapping("/get-timedto")
    TimeDto getTimeDto();

    @GetMapping("/current-instant")
    Instant getCurrentInstant();

    @GetMapping("/current-date")
    LocalDate getCurrentDate();

    @GetMapping("/current-date-time")
    LocalDateTime getCurrentDateTime();

    @PostMapping("/current-instant")
    String setCurrentInstant(@RequestParam Instant instant);

    @PostMapping("/current-date")
    String setCurrentDate(@RequestParam LocalDate date);

    @PostMapping("/current-date-time")
    String setCurrentDateTime(@RequestParam LocalDateTime dateTime);
}
