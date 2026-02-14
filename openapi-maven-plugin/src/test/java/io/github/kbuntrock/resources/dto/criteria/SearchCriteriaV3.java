package io.github.kbuntrock.resources.dto.criteria;

/**
 * @author Kévin Buntrock
 */
public class SearchCriteriaV3 extends BaseSearchCriteria<SearchCriteriaV3> {

	private boolean someBoolean;

	protected SearchCriteriaV3(final Class<SearchCriteriaV3> searchCriteriaV3Class) {
		super(searchCriteriaV3Class);
	}

	public boolean isSomeBoolean() {
		return someBoolean;
	}
}
