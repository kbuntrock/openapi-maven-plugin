package com.github.kbuntrock.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author Kevin Buntrock
 */
public class FileUtilsTest {

    @Test
    public void path_resolver() {

        String projectDirectory = new File("").getAbsolutePath();
        String subPath = "src/test/java/com/github/kbuntrock/utils";
        File resolvedFile = FileUtils.toFile(projectDirectory, subPath);
        Assertions.assertTrue(resolvedFile.exists());
        Assertions.assertTrue(resolvedFile.isDirectory());

        projectDirectory = new File("src/test/java/com/github/kbuntrock/utils").getAbsolutePath();
        subPath = "../resources/endpoint/../dto";
        resolvedFile = FileUtils.toFile(projectDirectory, subPath);
        Assertions.assertTrue(resolvedFile.exists());
        Assertions.assertTrue(resolvedFile.isDirectory());

    }
}
