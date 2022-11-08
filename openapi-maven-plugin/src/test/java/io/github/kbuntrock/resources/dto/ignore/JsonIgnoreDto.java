package io.github.kbuntrock.resources.dto.ignore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.kbuntrock.resources.dto.KeyAndPasswordDto;
import io.github.kbuntrock.resources.dto.TerritoryEnum;

public class JsonIgnoreDto {

	@JsonIgnore
	private TerritoryEnum territory;

	private KeyAndPasswordDto keyAndPassword;

	private SecondJsonIgnoreDto secondJsonIgnore;

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

	public SecondJsonIgnoreDto getSecondJsonIgnore() {
		return secondJsonIgnore;
	}

	public void setSecondJsonIgnore(final SecondJsonIgnoreDto secondJsonIgnore) {
		this.secondJsonIgnore = secondJsonIgnore;
	}
}
