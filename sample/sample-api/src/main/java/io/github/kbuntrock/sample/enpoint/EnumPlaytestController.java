package io.github.kbuntrock.sample.enpoint;

import io.github.kbuntrock.sample.dto.EnumWrapper;
import io.github.kbuntrock.sample.dto.Authority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = "api/enum-playtest")
public interface EnumPlaytestController {

    @GetMapping("authority-wrapper")
    EnumWrapper getAuthorityWrapper();

    @GetMapping("/authorities")
    List<Authority> getAuthorities();

    @GetMapping("/authority")
    Authority getAuthority();

    @PostMapping("/authority-wrapper")
    String setAuthorityWrapper(@RequestBody EnumWrapper enumWrapper);

    @PostMapping("/authorities")
    String setAuthorityList(@RequestBody List<Authority> authorities);

    @PostMapping("/authority-body")
    String setAuthorityAsBody(@RequestBody Authority authority);

    @PostMapping("/authority-query-param")
    String setAuthorityAsQueryParam(@RequestParam Authority authority);

    @PostMapping("/authority-path-param/{authority}")
    String setAuthorityAsPathParam(@PathVariable Authority authority);

}
