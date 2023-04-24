package io.github.kbuntrock.resources.dto.criteria;

/**
 * @author Kévin Buntrock
 */
public class SearchCriteriaV4 extends BaseSearchCriteria<SearchCriteriaV5> {

	private String toto;

	protected SearchCriteriaV4(final Class<SearchCriteriaV5> searchCriteriaV5Class) {
		super(searchCriteriaV5Class);
	}
}
