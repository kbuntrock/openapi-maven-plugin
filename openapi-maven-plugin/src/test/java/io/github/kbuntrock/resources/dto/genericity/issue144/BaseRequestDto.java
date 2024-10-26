package io.github.kbuntrock.resources.dto.genericity.issue144;

import java.util.ArrayList;
import java.util.List;

/**
 * The base request dto
 * @param <T> T must extends BaseRequestItem
 */
public class BaseRequestDto< T extends BaseRequestItem> {

	/**
	 * The price request items
	 */
	private List<T> priceRequestItems = new ArrayList<>();

	public List<T> getPriceRequestItems() {
		return priceRequestItems;
	}

	public void setPriceRequestItems(List<T> priceRequestItems) {
		this.priceRequestItems = priceRequestItems;
	}

}
