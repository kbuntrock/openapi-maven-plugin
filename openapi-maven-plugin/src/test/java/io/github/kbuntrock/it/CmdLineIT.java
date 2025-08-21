package io.github.kbuntrock.it;

import com.soebes.itf.extension.assertj.MavenExecutionResultAssert;
import com.soebes.itf.jupiter.extension.*;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension
public class CmdLineIT {

    @Nested
    @MavenProject(value = "simple_project")
    @TestMethodOrder( MethodOrderer.OrderAnnotation.class )
    @MavenGoal("clean")
    @MavenGoal("compile")
    @MavenGoal("${project.groupId}:${project.artifactId}:${project.version}:documentation")
    class cmdLine
    {
        /**
         * No location set
         */
        @MavenTest
        @Order(1)
        public void noLocation(final MavenExecutionResult result) throws IOException {
            final MavenExecutionResultAssert resultAssert = assertThat(result);
            resultAssert.isFailure().out().error().anySatisfy(s ->  assertThat(s).contains("At least one api configuration element should be configured"));
        }

        /**
         *
         */
        @MavenTest
        @SystemProperty(value = "openapi.locations", content = "io.github.kbuntrock.sample.enpoint")
        @Order(2)
        public void nothingToDocument(final MavenExecutionResult result) throws IOException {
            final MavenExecutionResultAssert resultAssert = assertThat(result);
            resultAssert.isFailure().out().error().anySatisfy(s ->  assertThat(s)
                    .contains("There is nothing to document. Please check if you have correctly configured the plugin or if the java version used by maven is high enough to read the compiled project classes"));
        }

        @MavenTest
        @SystemProperty(value = "openapi.locations", content = "io.github.kbuntrock.sample.enpoint")
        @SystemProperty(value = "openapi.tagAnnotations", content = "RequestMapping")
        @Order(3)
        public void generateWithoutCompileOption(final MavenExecutionResult result) throws IOException {
            final MavenExecutionResultAssert resultAssert = assertThat(result);
            resultAssert.isSuccessful().out().info().contains("spec-open-api.yml : 1 tags and 2 operations generated.");

            final File target = new File(result.getMavenProjectResult().getTargetProjectDirectory().toFile(), "target");
            final File generatedFile = new File(target, "spec-open-api.yml");
            Assertions.assertTrue(target.exists());
            Assertions.assertTrue(generatedFile.exists());

            try(final InputStream generatedFileStream = Files.newInputStream(generatedFile.toPath());
                final InputStream resourceFileStream = BasicIT.class.getClassLoader().getResourceAsStream("it/CmdLineIT/generateWithoutCompileOption.yml")) {
                assertThat(generatedFileStream).hasSameContentAs(resourceFileStream);
            }
        }

        @MavenTest
        @SystemProperty(value = "maven.compiler.parameters", content = "true")
        @SystemProperty(value = "openapi.locations", content = "io.github.kbuntrock.sample.enpoint")
        @SystemProperty(value = "openapi.tagAnnotations", content = "RequestMapping")
        @SystemProperty(value = "openapi.filename", content = "my-doc.yml")
        @Order(4)
        public void generateWithCompileOptionAndDifferentFilename(final MavenExecutionResult result) throws IOException {
            final MavenExecutionResultAssert resultAssert = assertThat(result);
            resultAssert.isSuccessful().out().info().contains("my-doc.yml : 1 tags and 2 operations generated.");

            final File target = new File(result.getMavenProjectResult().getTargetProjectDirectory().toFile(), "target");
            final File generatedFile = new File(target, "my-doc.yml");
            Assertions.assertTrue(target.exists());
            Assertions.assertTrue(generatedFile.exists());

            try(final InputStream generatedFileStream = Files.newInputStream(generatedFile.toPath());
                final InputStream resourceFileStream = BasicIT.class.getClassLoader().getResourceAsStream("it/CmdLineIT/generateWithCompileOption.yml")) {
                assertThat(generatedFileStream).hasSameContentAs(resourceFileStream);
            }
        }

        @MavenTest
        @SystemProperty(value = "maven.compiler.parameters", content = "true")
        @SystemProperty(value = "openapi.locations", content = "io.github.kbuntrock.sample.enpoint")
        @SystemProperty(value = "openapi.tagAnnotations", content = "RequestMapping")
        @SystemProperty(value = "openapi.filename", content = "my-doc.yml")
        @SystemProperty(value = "openapi.javadoc.scanEnabled", content = "false")
        @Order(5)
        public void generateDisableJavadoc(final MavenExecutionResult result) throws IOException {
            final MavenExecutionResultAssert resultAssert = assertThat(result);
            resultAssert.isSuccessful().out().info().contains("my-doc.yml : 1 tags and 2 operations generated.");

            resultAssert.out().info().contains("Javadoc scan is disabled.");

            final File target = new File(result.getMavenProjectResult().getTargetProjectDirectory().toFile(), "target");
            final File generatedFile = new File(target, "my-doc.yml");
            Assertions.assertTrue(target.exists());
            Assertions.assertTrue(generatedFile.exists());

            try(final InputStream generatedFileStream = Files.newInputStream(generatedFile.toPath());
                final InputStream resourceFileStream = BasicIT.class.getClassLoader().getResourceAsStream("it/CmdLineIT/generateNoJavadoc.yml")) {
                assertThat(generatedFileStream).hasSameContentAs(resourceFileStream);
            }
        }

        @MavenTest
        @SystemProperty(value = "maven.compiler.parameters", content = "true")
        @SystemProperty(value = "openapi.locations", content = "io.github.kbuntrock.sample.enpoint")
        @SystemProperty(value = "openapi.tagAnnotations", content = "RequestMapping")
        @SystemProperty(value = "openapi.filename", content = "my-doc.yml")
        @SystemProperty(value = "openapi.javadoc.locations", content = "src/main/toto,src/main/tata")
        @Order(6)
        public void generateWrongJavadocPath(final MavenExecutionResult result) throws IOException {
            final MavenExecutionResultAssert resultAssert = assertThat(result);
            resultAssert.isSuccessful().out().info().contains("my-doc.yml : 1 tags and 2 operations generated.");

            // JavadocParser - Directory src\main\toto does not exist.
            resultAssert.out().warn().anySatisfy(
                    s ->  assertThat(s)
                            .contains("JavadocParser - Directory")
                            .contains("src" + FileSystems.getDefault().getSeparator() + "main" + FileSystems.getDefault().getSeparator() + "toto does not exist."));
            resultAssert.out().warn().anySatisfy(
                    s ->  assertThat(s)
                            .contains("JavadocParser - Directory")
                            .contains("src" + FileSystems.getDefault().getSeparator() + "main" + FileSystems.getDefault().getSeparator() + "tata does not exist."));

            final File target = new File(result.getMavenProjectResult().getTargetProjectDirectory().toFile(), "target");
            final File generatedFile = new File(target, "my-doc.yml");
            Assertions.assertTrue(target.exists());
            Assertions.assertTrue(generatedFile.exists());

            try(final InputStream generatedFileStream = Files.newInputStream(generatedFile.toPath());
                final InputStream resourceFileStream = BasicIT.class.getClassLoader().getResourceAsStream("it/CmdLineIT/generateNoJavadoc.yml")) {
                assertThat(generatedFileStream).hasSameContentAs(resourceFileStream);
            }
        }

        @MavenTest
        @SystemProperty(value = "maven.compiler.parameters", content = "true")
        @SystemProperty(value = "openapi.locations", content = "io.github.kbuntrock.sample.enpoint")
        @SystemProperty(value = "openapi.tagAnnotations", content = "RequestMapping")
        @SystemProperty(value = "openapi.library", content = "JAKARTA_RS")
        @Order(7)
        public void wrongLibrary(final MavenExecutionResult result) throws IOException {
            final MavenExecutionResultAssert resultAssert = assertThat(result);
            resultAssert.isFailure().out().error().anySatisfy(s ->  assertThat(s)
                    .contains("jakarta.ws.rs.Path cannot be loaded. Please check if the correct dependencies are in your project classpath."));

        }
    }
}
