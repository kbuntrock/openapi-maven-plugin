package io.github.kbuntrock.resources.dto.criteria;

/**
 * @author Kévin Buntrock
 */
public class CriteriaWithDateType {

	private final Class<? extends CriteriaDateType> dateCriteria;

	public CriteriaWithDateType(final Class<? extends CriteriaDateType> dateTypeClass) {
		this.dateCriteria = dateTypeClass;
	}

	public Class<? extends CriteriaDateType> getDateCriteria() {
		return dateCriteria;
	}
}
