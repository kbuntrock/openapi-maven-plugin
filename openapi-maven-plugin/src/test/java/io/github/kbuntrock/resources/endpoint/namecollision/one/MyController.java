package io.github.kbuntrock.resources.endpoint.namecollision.one;

import io.github.kbuntrock.resources.Constants;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kévin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/controller-1")
public interface MyController {

	@PostMapping("info")
	String setInfo();

	@DeleteMapping("info")
	String deleteInfo();

	@GetMapping("info")
	String getInfo();

	@PutMapping("info")
	String putInfo();

	@PatchMapping("info")
	String patchInfo();

}
