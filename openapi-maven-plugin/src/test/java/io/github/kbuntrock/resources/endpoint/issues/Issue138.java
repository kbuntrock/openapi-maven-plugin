package io.github.kbuntrock.resources.endpoint.generic;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@RequestMapping("/issue138")
public class Issue138 {

	@GetMapping
	public Response issue138() {
		return Response.ok("Type K");
	}

	public static class Response extends HashMap<String, Object> {

		public static Response ok(Object data) {
			Response response = new Response();
			response.put("code", 20000);
			response.put("data", data);
			return response;
		}
	}
}