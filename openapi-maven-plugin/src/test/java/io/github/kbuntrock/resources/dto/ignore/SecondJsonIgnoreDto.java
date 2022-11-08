package io.github.kbuntrock.resources.dto.ignore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.kbuntrock.resources.dto.Authority;
import io.github.kbuntrock.resources.dto.NumberDto;
import io.github.kbuntrock.resources.dto.PageDto;
import io.github.kbuntrock.resources.dto.TimeDto;

public class SecondJsonIgnoreDto {

	TimeDto timeDto;

	PageDto<Authority> page;

	@JsonIgnore
	NumberDto number;

	public TimeDto getTimeDto() {
		return timeDto;
	}

	public void setTimeDto(final TimeDto timeDto) {
		this.timeDto = timeDto;
	}

	public PageDto<Authority> getPage() {
		return page;
	}

	public void setPage(final PageDto<Authority> page) {
		this.page = page;
	}

	public NumberDto getNumber() {
		return number;
	}

	public void setNumber(final NumberDto number) {
		this.number = number;
	}
}
