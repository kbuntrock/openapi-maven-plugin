package io.github.kbuntrock.resources.dto;

import java.util.List;

/**
 * @author Kevin Buntrock
 */
public class SpeAccountDto extends AccountDto {

	private PageDto<List<TimeDto>>[] page;

	public PageDto<List<TimeDto>>[] getPage() {
		return page;
	}

	public void setPage(PageDto<List<TimeDto>>[] page) {
		this.page = page;
	}
}
