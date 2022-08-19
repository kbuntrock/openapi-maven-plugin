package io.github.kbuntrock;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.configuration.OperationIdHelper;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.resources.endpoint.enumeration.*;
import io.github.kbuntrock.resources.endpoint.error.SameOperationController;
import io.github.kbuntrock.resources.endpoint.file.FileUploadController;
import io.github.kbuntrock.resources.endpoint.file.StreamResponseController;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestFour;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestOne;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestThree;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestTwo;
import io.github.kbuntrock.resources.endpoint.interfacedto.InterfaceController;
import io.github.kbuntrock.resources.endpoint.map.MapController;
import io.github.kbuntrock.resources.endpoint.number.NumberController;
import io.github.kbuntrock.resources.endpoint.path.SpringPathEnhancementOneController;
import io.github.kbuntrock.resources.endpoint.path.SpringPathEnhancementTwoController;
import io.github.kbuntrock.resources.endpoint.recursive.*;
import io.github.kbuntrock.resources.endpoint.spring.OptionalController;
import io.github.kbuntrock.resources.endpoint.spring.ResponseEntityController;
import io.github.kbuntrock.resources.endpoint.time.TimeController;
import io.github.kbuntrock.yaml.YamlWriter;
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
        mavenProjet.setFile(new File(new File("pom.xml").getAbsolutePath()));
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
    public void genericity_wrapped_dto() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(GenericityTestThree.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/genericity_wrapped_dto.yml", generated.get(0));
    }

    @Test
    public void genericity_typed_wrapped_dto() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(GenericityTestFour.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/genericity_typed_wrapped_dto.yml", generated.get(0));
    }

    @Test
    public void file_upload() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(FileUploadController.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/file_upload.yml", generated.get(0));
    }

    @Test
    public void stream_download() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(StreamResponseController.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/stream_download.yml", generated.get(0));
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

    @Test
    public void optional() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(OptionalController.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/optional.yml", generated.get(0));
    }

    @Test
    public void response_entity() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(ResponseEntityController.class.getCanonicalName());
        JavadocConfiguration javadocConfig = new JavadocConfiguration();
        javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/spring",
                "src/test/java/io/github/kbuntrock/resources/dto"));
        mojo.setJavadocConfiguration(javadocConfig);

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/response-entity.yml", generated.get(0));
    }

    @Test
    public void interface_dto() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(InterfaceController.class.getCanonicalName());
        JavadocConfiguration javadocConfig = new JavadocConfiguration();
        javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/interfacedto",
                "src/test/java/io/github/kbuntrock/resources/dto"));
        mojo.setJavadocConfiguration(javadocConfig);

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/interface.yml", generated.get(0));
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

    @Test
    public void recursive_dto() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(RecursiveDtoController.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/recursive_dto.yml", generated.get(0));
    }

    @Test
    public void recursive_dto_in_parameter() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(RecursiveDtoInParameterController.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/recursive_dto_in_parameter.yml", generated.get(0));
    }

    @Test
    public void generic_recursive_dto() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(GenericRecursiveDtoController.class.getCanonicalName());
        JavadocConfiguration javadocConfig = new JavadocConfiguration();
        javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/recursive",
                "src/test/java/io/github/kbuntrock/resources/dto"));
        mojo.setJavadocConfiguration(javadocConfig);

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/generic_recursive_dto.yml", generated.get(0));
    }

    @Test
    public void generic_recursive_list_dto() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(GenericRecursiveListDtoController.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/generic_recursive_list_dto.yml", generated.get(0));
    }

    @Test
    public void generic_recursive_interface_list_dto() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(GenericRecursiveInterfaceListDtoInParameterController.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/generic_recursive_interface_list_dto.yml", generated.get(0));
    }

    @Test
    public void generic_recursive_interface_dto() throws MojoFailureException, IOException, MojoExecutionException {

        DocumentationMojo mojo = createBasicMojo(GenericRecursiveInterfaceDtoController.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/SpringClassAnalyserTest/generic_recursive_interface_dto.yml", generated.get(0));
    }

}
