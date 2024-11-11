package io.github.kbuntrock.resources.endpoint.queryparam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 */
@RequestMapping("api")
public interface EmptyValueParameterController {

	@GetMapping(value = "get-info", params = "contact")
	String getInfoAboutContact();

	@GetMapping(value = "get-info", params = {"auto", "moto", "plane"})
	String getInfoAboutVehicle();

}
