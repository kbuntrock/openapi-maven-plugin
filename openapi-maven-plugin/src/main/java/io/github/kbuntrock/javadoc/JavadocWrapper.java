package io.github.kbuntrock.javadoc;

import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import io.github.kbuntrock.utils.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * @author Kevin Buntrock
 */
public class JavadocWrapper {

	private static final String INHERIT_DOC_TAG_NAME = "inheritDoc";

	private static String endOfLineReplacement = null;

	private final Javadoc javadoc;

	private Map<JavadocBlockTag.Type, List<JavadocBlockTag>> blockTagsByType;
	private Map<String, JavadocBlockTag> paramBlockTagsByName;
	private boolean inheritTagFound = false;
	private boolean sortDone = false;

	public JavadocWrapper(final Javadoc javadoc) {
		this.javadoc = javadoc;
	}

	public static void setEndOfLineReplacement(final String endOfLineReplacement) {
		JavadocWrapper.endOfLineReplacement = endOfLineReplacement;
	}

	public Javadoc getJavadoc() {
		return javadoc;
	}

	public void sortTags() {
		if(sortDone) {
			return;
		}
		sortDone = true;
		blockTagsByType = new HashMap<>();
		paramBlockTagsByName = new HashMap<>();
		for(final JavadocBlockTag blockTag : javadoc.getBlockTags()) {
			final List<JavadocBlockTag> list = blockTagsByType.computeIfAbsent(blockTag.getType(), k -> new ArrayList<>());
			list.add(blockTag);
			if(JavadocBlockTag.Type.PARAM == blockTag.getType() && blockTag.getName().isPresent()) {
				paramBlockTagsByName.put(blockTag.getName().get(), blockTag);
			} else if(JavadocBlockTag.Type.UNKNOWN == blockTag.getType() && INHERIT_DOC_TAG_NAME.equals(blockTag.getTagName())) {
				inheritTagFound = true;
			}
		}
	}

	public Optional<JavadocBlockTag> getParamBlockTagByName(final String parameterName) {
		return Optional.ofNullable(paramBlockTagsByName.get(parameterName));
	}

	public Optional<JavadocBlockTag> getReturnBlockTag() {

		final List<JavadocBlockTag> list = blockTagsByType.get(JavadocBlockTag.Type.RETURN);
		if(list != null && !list.isEmpty()) {
			return Optional.of(list.get(0));
		}
		return Optional.empty();
	}

	public Optional<String> getDescription() {
		if(javadoc.getDescription() != null) {
			String desc = javadoc.getDescription().toText();
			if(endOfLineReplacement != null) {
				desc = desc.replaceAll("\\r\\n", endOfLineReplacement).replaceAll("\\n", endOfLineReplacement);
			}
			if(!desc.isEmpty()) {
				return Optional.of(desc);
			}
		}
		return Optional.empty();
	}

	public boolean isInheritTagFound() {
		return inheritTagFound;
	}

	public void printParameters() {
		if(paramBlockTagsByName != null && !paramBlockTagsByName.isEmpty()) {
			Logger.INSTANCE.getLogger().debug("Parameters : ");
			for(final Entry<String, JavadocBlockTag> entry : paramBlockTagsByName.entrySet()) {
				Logger.INSTANCE.getLogger().debug(entry.getKey() + " : " + entry.getValue().getContent().toText());
			}
		}

	}

	public void printReturn() {
		if(blockTagsByType != null) {
			final Optional<JavadocBlockTag> returnTag = getReturnBlockTag();
			if(returnTag.isPresent()) {
				Logger.INSTANCE.getLogger().debug("Return : " + returnTag.get().getContent().toText());
			}
		}
	}
}
