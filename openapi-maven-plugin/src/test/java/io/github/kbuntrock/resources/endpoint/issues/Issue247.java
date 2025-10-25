package io.github.kbuntrock.resources.endpoint.issues;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/issue247")
public interface Issue247 {

    @GetMapping
    ResponseEntity<PageImpl> getEmployees(@Parameter(hidden = true) Pageable pageable) throws Exception;

    @GetMapping("/list")
    ResponseEntity<List[]> getEmployeesArrayOfList() throws Exception;
}
