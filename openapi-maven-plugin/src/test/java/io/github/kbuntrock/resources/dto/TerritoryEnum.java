package io.github.kbuntrock.resources.dto;

public enum TerritoryEnum {
	FRANCE(1),
	GERMANY(2);

	private final int code;

	TerritoryEnum(final int code) {
		this.code = code;
	}
}
