package io.github.kbuntrock.utils;


/**
 * Singleton of a Cloner instance for deep copying objets
 *
 * @author Kevin Buntrock
 */
public enum Cloner {
	INSTANCE;

	private final com.rits.cloning.Cloner cloner;

	Cloner() {
		cloner = new com.rits.cloning.Cloner();
	}

	public <T> T deepClone(T o) {
		return cloner.deepClone(o);
	}

	public <T, E extends T> void copyPropertiesOfInheritedClass(final T src, final E dest) {
		cloner.copyPropertiesOfInheritedClass(src, dest);
	}

}
