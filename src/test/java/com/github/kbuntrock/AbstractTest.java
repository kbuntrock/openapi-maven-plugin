package com.github.kbuntrock;

import com.github.kbuntrock.javadoc.JavadocMap;
import com.github.kbuntrock.reflection.ReflectionsUtils;
import com.github.kbuntrock.utils.Logger;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;

public class AbstractTest {

    private static final Log testLogger = new TestLogger();

    @BeforeAll
    public static void initTestClass() {
        Logger.INSTANCE.setLogger(testLogger);
        ReflectionsUtils.initiateTestMode();
    }

    @BeforeEach
    public void initTest() {
        JavadocMap.INSTANCE.setJavadocMap(new HashMap<>());
    }

    public static class TestLogger implements Log {

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void debug(CharSequence content) {

        }

        @Override
        public void debug(CharSequence content, Throwable error) {

        }

        @Override
        public void debug(Throwable error) {

        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public void info(CharSequence content) {

        }

        @Override
        public void info(CharSequence content, Throwable error) {

        }

        @Override
        public void info(Throwable error) {

        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public void warn(CharSequence content) {

        }

        @Override
        public void warn(CharSequence content, Throwable error) {

        }

        @Override
        public void warn(Throwable error) {

        }

        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public void error(CharSequence content) {

        }

        @Override
        public void error(CharSequence content, Throwable error) {

        }

        @Override
        public void error(Throwable error) {

        }
    }
}
