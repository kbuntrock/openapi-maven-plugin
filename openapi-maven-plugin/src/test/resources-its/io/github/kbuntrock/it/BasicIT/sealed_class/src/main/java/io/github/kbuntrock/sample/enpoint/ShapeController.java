package io.github.kbuntrock.sample.enpoint;

import io.github.kbuntrock.sample.dto.CircleDto;
import io.github.kbuntrock.sample.dto.RecordStatusDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Shape rest controller
 */
@RequestMapping(path = "/api/shape")
public interface ShapeController {


	/**
	 * Get a circle
	 *
	 * @return the circle object
	 */
	@GetMapping("/circle")
	CircleDto getCircle();

	/**
	 * Get the api status
	 *
	 * @return the status of the shape api
	 */
	@GetMapping("/status")
	RecordStatusDto getStatus();

}
