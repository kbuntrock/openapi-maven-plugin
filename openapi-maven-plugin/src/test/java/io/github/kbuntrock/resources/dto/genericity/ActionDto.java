package io.github.kbuntrock.resources.dto.genericity;

import javax.validation.constraints.Size;

/**
 * @author Kévin Buntrock
 */
public class ActionDto extends EntityDto {

	@Size(max = 15, message = "text too long")
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
