package io.github.kbuntrock;

/**
 * @author Kévin Buntrock
 */
public enum TagLibraryHolder {
	INSTANCE;

	private TagLibrary tagLibrary;

	public TagLibrary getTagLibrary() {
		return tagLibrary;
	}

	public void setTagLibrary(final TagLibrary tagLibrary) {
		this.tagLibrary = tagLibrary;
	}
}
