package io.github.kbuntrock.utils;

import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

/**
 * @author Kevin Buntrock
 */
public final class JavadocUtils {

    private JavadocUtils() {
    }

    public JavadocBlockTag getFieldJavaBlocTagForName(Javadoc javadoc, String name) {
        for (JavadocBlockTag javadocBlockTag : javadoc.getBlockTags()) {
            if (JavadocBlockTag.Type.PARAM == javadocBlockTag.getType() && name.equals(javadocBlockTag.getName())) {
                return javadocBlockTag;
            }
        }
        return null;
    }
}
