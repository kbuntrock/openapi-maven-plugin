package io.github.kbuntrock.resources.dto.criteria;

/**
 * @author Kévin Buntrock
 */
public class SearchCriteria extends CriteriaWithDateType {

	public String myString;

	public SearchCriteria() {
		super(MyDateType.class);
	}
}
