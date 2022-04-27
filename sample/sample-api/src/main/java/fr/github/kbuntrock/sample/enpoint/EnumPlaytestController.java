package fr.github.kbuntrock.sample.enpoint;

import fr.github.kbuntrock.sample.Constants;
import fr.github.kbuntrock.sample.dto.Authority;
import fr.github.kbuntrock.sample.dto.EnumWrapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = Constants.BASE_PATH + "/enum-playtest")
public interface EnumPlaytestController {

    @GetMapping("/authority-wrapper")
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
