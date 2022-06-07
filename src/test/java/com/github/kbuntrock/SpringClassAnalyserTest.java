package com.github.kbuntrock;

import com.github.kbuntrock.configuration.ApiConfiguration;
import com.github.kbuntrock.model.Tag;
import com.github.kbuntrock.reflection.ReflectionsUtils;
import com.github.kbuntrock.resources.endpoint.AccountController;
import com.github.kbuntrock.resources.endpoint.enumeration.*;
import com.github.kbuntrock.resources.endpoint.error.SameOperationController;
import com.github.kbuntrock.resources.endpoint.file.FileUploadController;
import com.github.kbuntrock.resources.endpoint.generic.GenericityTestOne;
import com.github.kbuntrock.resources.endpoint.generic.GenericityTestTwo;
import com.github.kbuntrock.resources.endpoint.map.MapController;
import com.github.kbuntrock.resources.endpoint.time.TimeController;
import com.github.kbuntrock.yaml.YamlWriter;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

public class SpringClassAnalyserTest extends AbstractTest {

    @BeforeAll
    public static void initTest() {
        ReflectionsUtils.initiateTestMode();
    }


    private MavenProject createBasicMavenProject() {
        MavenProject mavenProjet = new MavenProject();
        mavenProjet.setName("My Project");
        mavenProjet.setVersion("10.5.36");
        return mavenProjet;
    }

    private File createTestFile() throws IOException {
        return Files.createTempFile("openapi_test_", ".yml").toFile();
    }

    @Test
    public void multiple_genericity() throws MojoFailureException, IOException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(GenericityTestOne.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        File generatedFile = createTestFile();

        new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);

        try (InputStream generatedFileStream = new FileInputStream(generatedFile);
             InputStream resourceFileStream = this.getClass().getClassLoader().getResourceAsStream("ut/SpringClassAnalyserTest/multiple_genericity.yml")) {
            String md5GeneratedHex = DigestUtils.md5DigestAsHex(generatedFileStream);
            String md5ResourceHex = DigestUtils.md5DigestAsHex(resourceFileStream);

            Assertions.assertEquals(md5ResourceHex, md5GeneratedHex);
        }
    }

    @Test
    public void nested_genericity() throws MojoFailureException, IOException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(GenericityTestTwo.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        File generatedFile = createTestFile();

        new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);

        try (InputStream generatedFileStream = new FileInputStream(generatedFile);
             InputStream resourceFileStream = this.getClass().getClassLoader().getResourceAsStream("ut/SpringClassAnalyserTest/nested_genericity.yml")) {
            String md5GeneratedHex = DigestUtils.md5DigestAsHex(generatedFileStream);
            String md5ResourceHex = DigestUtils.md5DigestAsHex(resourceFileStream);

            Assertions.assertEquals(md5ResourceHex, md5GeneratedHex);
        }
    }

    @Test
    public void file_upload() throws MojoFailureException, IOException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(FileUploadController.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        File generatedFile = createTestFile();

        new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);

        try (InputStream generatedFileStream = new FileInputStream(generatedFile);
             InputStream resourceFileStream = this.getClass().getClassLoader().getResourceAsStream("ut/SpringClassAnalyserTest/file_upload.yml")) {
            String md5GeneratedHex = DigestUtils.md5DigestAsHex(generatedFileStream);
            String md5ResourceHex = DigestUtils.md5DigestAsHex(resourceFileStream);

            Assertions.assertEquals(md5ResourceHex, md5GeneratedHex);
        }
    }

    @Test
    public void enumeration_test_1() throws MojoFailureException, IOException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(TestEnumeration1Controller.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        File generatedFile = createTestFile();

        new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);

        try (InputStream generatedFileStream = new FileInputStream(generatedFile);
             InputStream resourceFileStream = this.getClass().getClassLoader().getResourceAsStream("ut/SpringClassAnalyserTest/enumeration_test_1.yml")) {
            String md5GeneratedHex = DigestUtils.md5DigestAsHex(generatedFileStream);
            String md5ResourceHex = DigestUtils.md5DigestAsHex(resourceFileStream);

            Assertions.assertEquals(md5ResourceHex, md5GeneratedHex);
        }
    }

    @Test
    public void enumeration_test_2() throws MojoFailureException, IOException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(TestEnumeration2Controller.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        File generatedFile = createTestFile();

        new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);

        try (InputStream generatedFileStream = new FileInputStream(generatedFile);
             InputStream resourceFileStream = this.getClass().getClassLoader().getResourceAsStream("ut/SpringClassAnalyserTest/enumeration_test_2.yml")) {
            String md5GeneratedHex = DigestUtils.md5DigestAsHex(generatedFileStream);
            String md5ResourceHex = DigestUtils.md5DigestAsHex(resourceFileStream);

            Assertions.assertEquals(md5ResourceHex, md5GeneratedHex);
        }
    }

    @Test
    public void enumeration_test_3() throws MojoFailureException, IOException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(TestEnumeration3Controller.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        File generatedFile = createTestFile();

        new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);

        try (InputStream generatedFileStream = new FileInputStream(generatedFile);
             InputStream resourceFileStream = this.getClass().getClassLoader().getResourceAsStream("ut/SpringClassAnalyserTest/enumeration_test_3.yml")) {
            String md5GeneratedHex = DigestUtils.md5DigestAsHex(generatedFileStream);
            String md5ResourceHex = DigestUtils.md5DigestAsHex(resourceFileStream);

            Assertions.assertEquals(md5ResourceHex, md5GeneratedHex);
        }
    }

    @Test
    public void enumeration_test_4() throws MojoFailureException, IOException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(TestEnumeration4Controller.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        File generatedFile = createTestFile();

        new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);

        try (InputStream generatedFileStream = new FileInputStream(generatedFile);
             InputStream resourceFileStream = this.getClass().getClassLoader().getResourceAsStream("ut/SpringClassAnalyserTest/enumeration_test_4.yml")) {
            String md5GeneratedHex = DigestUtils.md5DigestAsHex(generatedFileStream);
            String md5ResourceHex = DigestUtils.md5DigestAsHex(resourceFileStream);

            Assertions.assertEquals(md5ResourceHex, md5GeneratedHex);
        }
    }

    @Test
    public void enumeration_test_5() throws MojoFailureException, IOException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(TestEnumeration5Controller.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        File generatedFile = createTestFile();

        new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);

        try (InputStream generatedFileStream = new FileInputStream(generatedFile);
             InputStream resourceFileStream = this.getClass().getClassLoader().getResourceAsStream("ut/SpringClassAnalyserTest/enumeration_test_5.yml")) {
            String md5GeneratedHex = DigestUtils.md5DigestAsHex(generatedFileStream);
            String md5ResourceHex = DigestUtils.md5DigestAsHex(resourceFileStream);

            Assertions.assertEquals(md5ResourceHex, md5GeneratedHex);
        }
    }

    @Test
    public void enumeration_test_6() throws MojoFailureException, IOException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(TestEnumeration6Controller.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        File generatedFile = createTestFile();

        new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);

        try (InputStream generatedFileStream = new FileInputStream(generatedFile);
             InputStream resourceFileStream = this.getClass().getClassLoader().getResourceAsStream("ut/SpringClassAnalyserTest/enumeration_test_6.yml")) {
            String md5GeneratedHex = DigestUtils.md5DigestAsHex(generatedFileStream);
            String md5ResourceHex = DigestUtils.md5DigestAsHex(resourceFileStream);

            Assertions.assertEquals(md5ResourceHex, md5GeneratedHex);
        }
    }

    @Test
    public void time_objects() throws MojoFailureException, IOException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(TimeController.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        File generatedFile = createTestFile();

        new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);

        try (InputStream generatedFileStream = new FileInputStream(generatedFile);
             InputStream resourceFileStream = this.getClass().getClassLoader().getResourceAsStream("ut/SpringClassAnalyserTest/time_objects.yml")) {
            String md5GeneratedHex = DigestUtils.md5DigestAsHex(generatedFileStream);
            String md5ResourceHex = DigestUtils.md5DigestAsHex(resourceFileStream);

            Assertions.assertEquals(md5ResourceHex, md5GeneratedHex);
        }
    }

    @Test
    public void map_objects() throws MojoFailureException, IOException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(MapController.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        File generatedFile = createTestFile();

        new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);

        try (InputStream generatedFileStream = new FileInputStream(generatedFile);
             InputStream resourceFileStream = this.getClass().getClassLoader().getResourceAsStream("ut/SpringClassAnalyserTest/map_objects.yml")) {
            String md5GeneratedHex = DigestUtils.md5DigestAsHex(generatedFileStream);
            String md5ResourceHex = DigestUtils.md5DigestAsHex(resourceFileStream);

            Assertions.assertEquals(md5ResourceHex, md5GeneratedHex);
        }
    }

    /**
     * Two operations on the same http verb + path
     *
     * @throws MojoFailureException
     * @throws IOException
     */
    @Test
    public void error_same_operation() throws MojoFailureException, IOException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(SameOperationController.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        File generatedFile = createTestFile();

        Exception ex = null;
        try {
            new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);
        } catch (Exception e) {
            ex = e;
        }
        Assertions.assertNotNull(ex);
        Assertions.assertEquals("More than one operation mapped on GET : /api/same-operation in tag SameOperationController", ex.getMessage());


    }


    //@Test
    public void basicParsing() throws MojoFailureException, IOException {

        MavenProject mavenProject = new MavenProject();
        mavenProject.setName("Mon super projet");
        mavenProject.setVersion("2.5.9-SNAPSHOT");
        ClassLoader projectClassLoader = AccountController.class.getClassLoader();
        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(GenericityTestTwo.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        new YamlWriter(mavenProject, apiConfiguration).write(new File("D:\\Dvpt\\openapi-maven-plugin\\target\\component.yml"), library);

        System.out.println();
    }

}
