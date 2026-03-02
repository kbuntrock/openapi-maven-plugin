package io.github.kbuntrock.resources.dto.criteria;

/**
 * @author Kévin Buntrock
 */
public class SearchCriteriaV6 extends BaseSearchCriteria<SearchCriteriaV6> {

	private boolean someBoolean;

	protected SearchCriteriaV6() {
		super(SearchCriteriaV6.class);
	}

	public boolean isSomeBoolean() {
		return someBoolean;
	}
}
