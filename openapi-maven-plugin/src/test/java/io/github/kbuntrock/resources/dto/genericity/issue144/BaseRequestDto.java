package io.github.kbuntrock.resources.dto.genericity.issue144;

import java.util.ArrayList;
import java.util.List;

public class BaseRequestDto< T extends BaseRequestItem> {

	private List<T> priceRequestItems = new ArrayList<>();

	public List<T> getPriceRequestItems() {
		return priceRequestItems;
	}

	public void setPriceRequestItems(List<T> priceRequestItems) {
		this.priceRequestItems = priceRequestItems;
	}

}
