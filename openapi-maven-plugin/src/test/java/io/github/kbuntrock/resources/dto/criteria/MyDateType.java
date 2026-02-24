package io.github.kbuntrock.resources.dto.criteria;

/**
 * @author Kévin Buntrock
 */
public class MyDateType implements CriteriaDateType {

	@Override
	public int getTimestamp() {
		return 0;
	}

	@Override
	public void setTimestamp(int timestamp) {
		// nothing to do
	}
}
