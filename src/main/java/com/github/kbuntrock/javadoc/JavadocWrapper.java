package com.github.kbuntrock.javadoc;

import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

import java.util.*;

/**
 * @author Kevin Buntrock
 */
public class JavadocWrapper {

    private static String INHERIT_DOC_TAG_NAME = "inheritDoc";

    private static String endOfLineReplacement = null;

    private Javadoc javadoc;

    private Map<JavadocBlockTag.Type, List<JavadocBlockTag>> blockTagsByType;
    private Map<String, JavadocBlockTag> paramBlockTagsByName;
    private boolean inheritTagFound = false;
    private boolean sortDone = false;

    public JavadocWrapper(Javadoc javadoc) {
        this.javadoc = javadoc;
    }

    public Javadoc getJavadoc() {
        return javadoc;
    }

    public static void setEndOfLineReplacement(String endOfLineReplacement) {
        JavadocWrapper.endOfLineReplacement = endOfLineReplacement;
    }

    public void sortTags() {
        if (sortDone) {
            return;
        }
        sortDone = true;
        blockTagsByType = new HashMap<>();
        paramBlockTagsByName = new HashMap<>();
        for (JavadocBlockTag blockTag : javadoc.getBlockTags()) {
            List<JavadocBlockTag> list = blockTagsByType.computeIfAbsent(blockTag.getType(), k -> new ArrayList<>());
            list.add(blockTag);
            if (JavadocBlockTag.Type.PARAM == blockTag.getType() && blockTag.getName().isPresent()) {
                paramBlockTagsByName.put(blockTag.getName().get(), blockTag);
            } else if (JavadocBlockTag.Type.UNKNOWN == blockTag.getType() && INHERIT_DOC_TAG_NAME.equals(blockTag.getTagName())) {
                inheritTagFound = true;
            }
        }
    }

    public Optional<JavadocBlockTag> getParamBlockTagByName(final String parameterName) {
        return Optional.ofNullable(paramBlockTagsByName.get(parameterName));
    }

    public Optional<JavadocBlockTag> getReturnBlockTag() {
        List<JavadocBlockTag> list = blockTagsByType.get(JavadocBlockTag.Type.RETURN);
        if (list != null && !list.isEmpty()) {
            return Optional.of(list.get(0));
        }
        return Optional.empty();
    }

    public Optional<String> getDescription() {
        if (javadoc.getDescription() != null) {
            String desc = javadoc.getDescription().toText();
            if (endOfLineReplacement != null) {
                desc = desc.replaceAll("\\r\\n", endOfLineReplacement).replaceAll("\\n", endOfLineReplacement);
            }
            if (!desc.isEmpty()) {
                return Optional.of(desc);
            }
        }
        return Optional.empty();
    }

    public boolean isInheritTagFound() {
        return inheritTagFound;
    }
}
