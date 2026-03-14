package io.github.kbuntrock.resources.dto.ignore;

import io.github.kbuntrock.resources.dto.KeyAndPasswordDto;
import io.github.kbuntrock.resources.dto.TerritoryEnum;
import jakarta.json.bind.annotation.JsonbTransient;

public class JakartaJsonTransientDto {

	@JsonbTransient
	private TerritoryEnum territory;

	private KeyAndPasswordDto keyAndPassword;

	public TerritoryEnum getTerritory() {
		return territory;
	}

	public void setTerritory(final TerritoryEnum territory) {
		this.territory = territory;
	}

	public KeyAndPasswordDto getKeyAndPassword() {
		return keyAndPassword;
	}

	public void setKeyAndPassword(final KeyAndPasswordDto keyAndPassword) {
		this.keyAndPassword = keyAndPassword;
	}

}
