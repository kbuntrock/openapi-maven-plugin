package io.github.kbuntrock.resources.dto.genericity.issue144;

import java.util.List;

/**
 * The base request dto interface
 * @param <T> T must extends BaseRequestItemInterface
 */
public interface BaseRequestDtoInterface<T extends BaseRequestItemInterface> {

	/**
	 * The price request items
	 */
	List<T> getPriceRequestItems();

}
