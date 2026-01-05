package io.github.kbuntrock.resources.endpoint.issues;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;

@RequestMapping("/issue262")
public interface Issue262 {

	@GetMapping("/some-redirection")
	RedirectView redirectUsingRedirectView(@RequestParam int aNumber);
}
