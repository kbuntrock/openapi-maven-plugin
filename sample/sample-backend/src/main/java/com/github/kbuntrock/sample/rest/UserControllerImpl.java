package com.github.kbuntrock.sample.rest;

import fr.github.kbuntrock.sample.enpoint.UserController;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserControllerImpl implements UserController {

    @Override
    public List<String> getAllAuthorities() {
        return List.of("tata", "yoyo");
    }
}
