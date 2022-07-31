package com.github.kbuntrock;

import com.github.kbuntrock.configuration.ApiConfiguration;
import com.github.kbuntrock.configuration.OperationIdHelper;
import com.github.kbuntrock.configuration.library.TagAnnotation;
import com.github.kbuntrock.model.Tag;
import com.github.kbuntrock.resources.endpoint.enumeration.*;
import com.github.kbuntrock.resources.endpoint.error.SameOperationController;
import com.github.kbuntrock.resources.endpoint.file.FileUploadController;
import com.github.kbuntrock.resources.endpoint.generic.GenericityTestOne;
import com.github.kbuntrock.resources.endpoint.generic.GenericityTestTwo;
import com.github.kbuntrock.resources.endpoint.map.MapController;
import com.github.kbuntrock.resources.endpoint.number.NumberController;
import com.github.kbuntrock.resources.endpoint.path.SpringPathEnhancementOneController;
import com.github.kbuntrock.resources.endpoint.path.SpringPathEnhancementTwoController;
import com.github.kbuntrock.resources.endpoint.time.TimeController;
import com.github.kbuntrock.yaml.YamlWriter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SpringClassAnalyserTest extends AbstractTest {

    private MavenProject createBasicMavenProject() {
        MavenProject mavenProjet = new MavenProject();
        mavenProjet.setName("My Project");
        mavenProjet.setVersion("10.5.36");
        return mavenProjet;
    }

    private DocumentationMojo createBasicMojo(String apiLocation) {
        DocumentationMojo mojo = new DocumentationMojo();
        ApiConfiguration apiConfiguration = new ApiConfiguration();
        apiConfiguration.setAttachArtifact(false);
        apiConfiguration.setLocations(Arrays.asList(apiLocation));
        apiConfiguration.setDefaultProduceConsumeGuessing(false);
        apiConfiguration.setOperationId("{method_name}");
        apiConfiguration.setLoopbackOperationName(false);
        apiConfiguration.setTagAnnotations(Arrays.asList(TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName()));
        mojo.setTestMode(true);
        mojo.setApis(Arrays.asList(apiConfiguration));
        mojo.setProject(createBasicMavenProject());
        return mojo;
    }

    private File createTestFile() throws IOException {
        return Files.createTempFile("openapi_test_", ".yml").toFile();
    }

    @Test
    public void multiple_genericity() throws MojoFailureException, MojoExecutionException, IOException {

        DocumentationMojo mojo = createBasicMojo(GenericityTestOne.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/multiple_genericity.yml", generated.get(0));
    }

    @Test
    public void nested_genericity() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(GenericityTestTwo.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/nested_genericity.yml", generated.get(0));
    }

    @Test
    public void file_upload() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(FileUploadController.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/file_upload.yml", generated.get(0));
    }

    @Test
    public void enumeration_test_1() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(TestEnumeration1Controller.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/enumeration_test_1.yml", generated.get(0));
    }

    @Test
    public void enumeration_test_2() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(TestEnumeration2Controller.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/enumeration_test_2.yml", generated.get(0));
    }

    @Test
    public void enumeration_test_3() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(TestEnumeration3Controller.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/enumeration_test_3.yml", generated.get(0));
    }

    @Test
    public void enumeration_test_4() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(TestEnumeration4Controller.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/enumeration_test_4.yml", generated.get(0));
    }

    @Test
    public void enumeration_test_5() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(TestEnumeration5Controller.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/enumeration_test_5.yml", generated.get(0));
    }

    @Test
    public void enumeration_test_6() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(TestEnumeration6Controller.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/enumeration_test_6.yml", generated.get(0));
    }

    @Test
    public void time_objects() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(TimeController.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/time_objects.yml", generated.get(0));
    }

    @Test
    public void map_objects() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(MapController.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/map_objects.yml", generated.get(0));
    }

    /**
     * Two operations on the same http verb + path
     *
     * @throws MojoFailureException
     * @throws IOException
     */
    @Test
    public void error_same_operation() {

        DocumentationMojo mojo = createBasicMojo(SameOperationController.class.getCanonicalName());

        Exception ex = null;
        try {
            mojo.documentProject();
        } catch (Exception e) {
            ex = e;
        }
        Assertions.assertNotNull(ex);
        Assertions.assertEquals("More than one operation mapped on GET : /api/same-operation in tag SameOperationController", ex.getMessage());


    }

    @Test
    public void numbers() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(NumberController.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/numbers.yml", generated.get(0));
    }

    @Test
    public void pathEnhancement() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(SpringPathEnhancementOneController.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/springPathEnhancementOne.yml", generated.get(0));
    }

    /**
     * TODO : to refacto when scanning of @RestController will be done.
     *
     * @throws MojoFailureException
     * @throws IOException
     * @throws MojoExecutionException
     */
    @Test
    public void pathEnhancementTwo() throws MojoFailureException, IOException, MojoExecutionException {

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        apiConfiguration.initDefaultValues();
        apiConfiguration.setDefaultProduceConsumeGuessing(false);
        apiConfiguration.setOperationId("{method_name}");
        apiConfiguration.setLoopbackOperationName(false);
        apiConfiguration.setOperationIdHelper(new OperationIdHelper(apiConfiguration.getOperationId()));
        apiConfiguration.setTagAnnotations(Arrays.asList(TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName()));

        SpringClassAnalyser analyser = new SpringClassAnalyser(apiConfiguration);
        Optional<Tag> tag = analyser.getTagFromClass(SpringPathEnhancementTwoController.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        File generatedFile = createTestFile();

        new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);

        try (InputStream generatedFileStream = new FileInputStream(generatedFile);
             InputStream resourceFileStream = this.getClass().getClassLoader().getResourceAsStream("ut/SpringClassAnalyserTest/springPathEnhancementTwo.yml")) {
            String md5GeneratedHex = DigestUtils.md5DigestAsHex(generatedFileStream);
            String md5ResourceHex = DigestUtils.md5DigestAsHex(resourceFileStream);

            Assertions.assertEquals(md5ResourceHex, md5GeneratedHex);
        }
    }

}
