package com.github.kbuntrock;

import com.github.kbuntrock.utils.Logger;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;

public class AbstractTest {

    private static final Log testLogger = new TestLogger();

    @Before
    public void init() {
        Logger.INSTANCE.setLogger(testLogger);
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
