package io.github.kbuntrock.resources.endpoint.examples;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.EnumTest1Dto;
import io.github.kbuntrock.resources.dto.example.ExampleRequestBodyDto;
import io.github.kbuntrock.resources.dto.example.ExampleResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(Constants.BASE_API + "/test-examples-1")
public interface TestExamplesController {

	@PostMapping()
	String postExample(@RequestBody ExampleRequestBodyDto exampleRequestBodyDto);

	@GetMapping("/{paramField}")
	ExampleResponseDto getParameter(@PathVariable("paramField") String param,
									@RequestHeader("headerField") String headerField);

	@GetMapping()
	ExampleResponseDto getHeader();
}
