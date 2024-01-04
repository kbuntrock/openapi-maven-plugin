package io.github.kbuntrock.resources.endpoint.namecollision.three;

import io.github.kbuntrock.resources.Constants;
import javax.ws.rs.QueryParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kévin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/controller-3")
public interface MyController {


	@GetMapping("info")
	String getInfo();

	@GetMapping("info")
	String getInfo(@QueryParam("toto") String toto);

}
