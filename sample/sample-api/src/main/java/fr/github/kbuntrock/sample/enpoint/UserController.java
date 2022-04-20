package fr.github.kbuntrock.sample.enpoint;

import fr.github.kbuntrock.sample.Constants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(path = Constants.BASE_PATH + "/user")
public interface UserController {

    @GetMapping("/availables-authorities")
    List<String> getAllAuthorities();
}
