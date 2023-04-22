package io.github.kbuntrock.resources.dto.criteria;

/**
 * @author Kévin Buntrock
 */
public class SearchCriteriaV2 extends CriteriaWithDateType {

	private String myString;


	public SearchCriteriaV2(final Class<? extends CriteriaDateType> dateTypeClass) {
		super(dateTypeClass);
	}
}
