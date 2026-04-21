package io.github.kbuntrock.resources.dto.criteria;

/**
 * @author Kévin Buntrock
 */
public class BaseSearchCriteria<SELF extends BaseSearchCriteria<?>> extends SearchCriteria {

	public final SELF self;

	protected BaseSearchCriteria(final Class<SELF> selfClass) {
		self = selfClass.cast(this);
	}

	public SELF getSelf() {
		return self;
	}
}