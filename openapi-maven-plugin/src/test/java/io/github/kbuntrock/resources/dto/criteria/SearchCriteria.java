package io.github.kbuntrock.resources.dto.criteria;

/**
 * @author Kévin Buntrock
 */
public class SearchCriteria extends CriteriaWithDateType {

	private String myString;

	public SearchCriteria() {
		super(MyDateType.class);
	}
}
