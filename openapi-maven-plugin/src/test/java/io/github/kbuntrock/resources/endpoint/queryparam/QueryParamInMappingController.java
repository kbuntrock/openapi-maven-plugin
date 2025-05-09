package io.github.kbuntrock.resources.endpoint.queryparam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 */
@RequestMapping("api")
public interface QueryParamInMappingController {

	@GetMapping(value = "get-info-contact", params = {"param1=value1", "param2=value2", "param3!=value3"})
	String getInfoAboutContact();

	@GetMapping(value = "get-info-vehicule", params = {"!param1", "param2"})
	String getInfoAboutVehicle();

}
