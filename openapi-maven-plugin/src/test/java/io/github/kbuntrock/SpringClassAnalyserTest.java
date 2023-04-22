package io.github.kbuntrock;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.EnumConfig;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.configuration.OperationIdHelper;
import io.github.kbuntrock.configuration.Substitution;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.resources.dto.TerritoryEnum;
import io.github.kbuntrock.resources.endpoint.account.AccountController;
import io.github.kbuntrock.resources.endpoint.annotation.AnnotatedController;
import io.github.kbuntrock.resources.endpoint.collection.CollectionController;
import io.github.kbuntrock.resources.endpoint.enumeration.TestEnumeration1Controller;
import io.github.kbuntrock.resources.endpoint.enumeration.TestEnumeration2Controller;
import io.github.kbuntrock.resources.endpoint.enumeration.TestEnumeration3Controller;
import io.github.kbuntrock.resources.endpoint.enumeration.TestEnumeration4Controller;
import io.github.kbuntrock.resources.endpoint.enumeration.TestEnumeration5Controller;
import io.github.kbuntrock.resources.endpoint.enumeration.TestEnumeration6Controller;
import io.github.kbuntrock.resources.endpoint.enumeration.TestEnumeration7Controller;
import io.github.kbuntrock.resources.endpoint.error.SameOperationController;
import io.github.kbuntrock.resources.endpoint.file.FileUploadController;
import io.github.kbuntrock.resources.endpoint.file.StreamResponseController;
import io.github.kbuntrock.resources.endpoint.generic.GenericDataController;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestEight;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestFive;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestFour;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestOne;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestSeven;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestSix;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestThree;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestTwo;
import io.github.kbuntrock.resources.endpoint.ignore.JsonIgnoreController;
import io.github.kbuntrock.resources.endpoint.interfacedto.InterfaceController;
import io.github.kbuntrock.resources.endpoint.map.MapController;
import io.github.kbuntrock.resources.endpoint.number.NumberController;
import io.github.kbuntrock.resources.endpoint.path.SpringPathEnhancementOneController;
import io.github.kbuntrock.resources.endpoint.path.SpringPathEnhancementTwoController;
import io.github.kbuntrock.resources.endpoint.recursive.GenericRecursiveDtoController;
import io.github.kbuntrock.resources.endpoint.recursive.GenericRecursiveInterfaceDtoController;
import io.github.kbuntrock.resources.endpoint.recursive.GenericRecursiveInterfaceListDtoInParameterController;
import io.github.kbuntrock.resources.endpoint.recursive.GenericRecursiveListDtoController;
import io.github.kbuntrock.resources.endpoint.recursive.RecursiveDtoController;
import io.github.kbuntrock.resources.endpoint.recursive.RecursiveDtoInParameterController;
import io.github.kbuntrock.resources.endpoint.spring.OptionalController;
import io.github.kbuntrock.resources.endpoint.spring.ResponseEntityController;
import io.github.kbuntrock.resources.endpoint.time.TimeController;
import io.github.kbuntrock.resources.implementation.account.AccountControllerImpl;
import io.github.kbuntrock.yaml.YamlWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

public class SpringClassAnalyserTest extends AbstractTest {

	private MavenProject createBasicMavenProject() {
		final MavenProject mavenProjet = new MavenProject();
		mavenProjet.setName("My Project");
		mavenProjet.setVersion("10.5.36");
		mavenProjet.setFile(new File(new File("pom.xml").getAbsolutePath()));
		return mavenProjet;
	}

	private DocumentationMojo createBasicMojo(final String apiLocation) {
		final DocumentationMojo mojo = new DocumentationMojo();
		final ApiConfiguration apiConfiguration = new ApiConfiguration();
		apiConfiguration.setAttachArtifact(false);
		apiConfiguration.setLocations(Collections.singletonList(apiLocation));
		apiConfiguration.setDefaultProduceConsumeGuessing(false);
		apiConfiguration.setOperationId("{method_name}");
		apiConfiguration.setLoopbackOperationName(false);
		apiConfiguration.setTagAnnotations(Collections.singletonList(TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName()));
		mojo.setTestMode(true);
		mojo.setApis(Collections.singletonList(apiConfiguration));
		mojo.setProject(createBasicMavenProject());
		return mojo;
	}

	private File createTestFile() throws IOException {
		return Files.createTempFile("openapi_test_", ".yml").toFile();
	}

	@Test
	public void multiple_genericity() throws MojoFailureException, MojoExecutionException, IOException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestOne.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/multiple_genericity.yml", generated.get(0));
	}

	@Test
	public void nested_genericity() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestTwo.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/nested_genericity.yml", generated.get(0));
	}

	@Test
	public void genericity_wrapped_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestThree.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/genericity_wrapped_dto.yml", generated.get(0));
	}

	@Test
	public void genericity_typed_wrapped_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestFour.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/genericity_typed_wrapped_dto.yml", generated.get(0));
	}

	@Test
	public void genericity_list_long() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestFive.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/genericity_list_long.yml", generated.get(0));
	}

	@Test
	public void genericity_extends() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestSix.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/genericity_extends.yml", generated.get(0));
	}

	@Test
	public void genericity_extends_class_in_parameter() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestSeven.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/genericity_extends_class_in_parameter.yml", generated.get(0));
	}

	/**
	 * A different way to achieve almost the same output than the previous test
	 *
	 * @throws MojoFailureException
	 * @throws IOException
	 * @throws MojoExecutionException
	 */
	@Test
	public void genericity_extends_class_in_parameter_v2() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestEight.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/genericity_extends_class_in_parameter_v2.yml", generated.get(0));
	}

	@Test
	public void file_upload() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(FileUploadController.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/file_upload.yml", generated.get(0));
	}

	@Test
	public void stream_download() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(StreamResponseController.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/stream_download.yml", generated.get(0));
	}

	@Test
	public void enumeration_test_1() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration1Controller.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/enumeration_test_1.yml", generated.get(0));
	}

	@Test
	public void enumeration_test_2() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration2Controller.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/enumeration_test_2.yml", generated.get(0));
	}

	@Test
	public void enumeration_test_3() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration3Controller.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/enumeration_test_3.yml", generated.get(0));
	}

	@Test
	public void enumeration_test_4() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration4Controller.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/enumeration_test_4.yml", generated.get(0));
	}

	@Test
	public void enumeration_test_5() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration5Controller.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/enumeration_test_5.yml", generated.get(0));
	}

	@Test
	public void enumeration_test_6() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration6Controller.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/enumeration_test_6.yml", generated.get(0));
	}

	@Test
	public void enumeration_test_7() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration7Controller.class.getCanonicalName());
		final EnumConfig enumConfig = new EnumConfig();
		enumConfig.setCanonicalName(TerritoryEnum.class.getCanonicalName());
		enumConfig.setValueField("code");
		mojo.getApiConfiguration().setEnumConfigList(Arrays.asList(enumConfig));
		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/enumeration_test_7.yml", generated.get(0));
	}

	@Test
	public void time_objects() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TimeController.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/time_objects.yml", generated.get(0));
	}

	@Test
	public void map_objects() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(MapController.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
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

		final DocumentationMojo mojo = createBasicMojo(SameOperationController.class.getCanonicalName());

		Exception ex = null;
		try {
			mojo.documentProject();
		} catch(final Exception e) {
			ex = e;
		}
		Assertions.assertNotNull(ex);
		Assertions.assertEquals("More than one operation mapped on GET : /api/same-operation in tag SameOperationController",
			ex.getMessage());


	}

	@Test
	public void numbers() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(NumberController.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/numbers.yml", generated.get(0));
	}

	@Test
	public void pathEnhancement() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(SpringPathEnhancementOneController.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/springPathEnhancementOne.yml", generated.get(0));
	}

	@Test
	public void optional() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(OptionalController.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/optional.yml", generated.get(0));
	}

	@Test
	public void response_entity() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(ResponseEntityController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/spring",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/response-entity.yml", generated.get(0));
	}

	@Test
	public void interface_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(InterfaceController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/interfacedto",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/interface.yml", generated.get(0));
	}

	@Test
	public void pathEnhancementTwo() throws MojoFailureException, IOException, MojoExecutionException {

		final ApiConfiguration apiConfiguration = new ApiConfiguration();
		apiConfiguration.initDefaultValues();
		apiConfiguration.setDefaultProduceConsumeGuessing(false);
		apiConfiguration.setOperationId("{method_name}");
		apiConfiguration.setLoopbackOperationName(false);
		apiConfiguration.setOperationIdHelper(new OperationIdHelper(apiConfiguration.getOperationId()));
		apiConfiguration.setTagAnnotations(Collections.singletonList(TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName()));

		final JavaClassAnalyser analyser = new JavaClassAnalyser(apiConfiguration);
		final Optional<Tag> tag = analyser.getTagFromClass(SpringPathEnhancementTwoController.class);
		final TagLibrary library = new TagLibrary();
		library.addTag(tag.get());

		final File generatedFile = createTestFile();

		new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);

		try(final InputStream generatedFileStream = new FileInputStream(generatedFile);
			final InputStream resourceFileStream = this.getClass().getClassLoader()
				.getResourceAsStream("ut/SpringClassAnalyserTest/springPathEnhancementTwo.yml")) {
			final String md5GeneratedHex = DigestUtils.md5DigestAsHex(generatedFileStream);
			final String md5ResourceHex = DigestUtils.md5DigestAsHex(resourceFileStream);

			Assertions.assertEquals(md5ResourceHex, md5GeneratedHex);
		}
	}

	@Test
	public void recursive_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(RecursiveDtoController.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/recursive_dto.yml", generated.get(0));
	}

	@Test
	public void recursive_dto_in_parameter() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(RecursiveDtoInParameterController.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/recursive_dto_in_parameter.yml", generated.get(0));
	}

	@Test
	public void generic_recursive_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericRecursiveDtoController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/recursive",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/generic_recursive_dto.yml", generated.get(0));
	}

	@Test
	public void generic_recursive_list_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericRecursiveListDtoController.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/generic_recursive_list_dto.yml", generated.get(0));
	}

	@Test
	public void generic_recursive_interface_list_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericRecursiveInterfaceListDtoInParameterController.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/generic_recursive_interface_list_dto.yml", generated.get(0));
	}

	@Test
	public void generic_recursive_interface_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericRecursiveInterfaceDtoController.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/generic_recursive_interface_dto.yml", generated.get(0));
	}

	@Test
	public void generically_typed_controller() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericDataController.class.getCanonicalName());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/generically_typed_controller.yml", generated.get(0));
	}

	@Test
	public void interface_vs_implementation() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo1 = new DocumentationMojo();
		final ApiConfiguration apiConfiguration1 = new ApiConfiguration();
		apiConfiguration1.setAttachArtifact(false);
		apiConfiguration1.setLocations(Collections.singletonList(AccountController.class.getCanonicalName()));
		apiConfiguration1.setTagAnnotations(Collections.singletonList(TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName()));
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/account",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo1.setJavadocConfiguration(javadocConfig);
		mojo1.setTestMode(true);
		mojo1.setApis(Collections.singletonList(apiConfiguration1));
		mojo1.setProject(createBasicMavenProject());

		final DocumentationMojo mojo2 = new DocumentationMojo();
		final ApiConfiguration apiConfiguration2 = new ApiConfiguration();
		apiConfiguration2.setAttachArtifact(false);
		apiConfiguration2.setLocations(Collections.singletonList(AccountControllerImpl.class.getCanonicalName()));
		apiConfiguration2.setTagAnnotations(Collections.singletonList(TagAnnotation.SPRING_REST_CONTROLLER.getAnnotationClassName()));
		apiConfiguration2.setOperationId("{tag_name}.{method_name}");
		final io.github.kbuntrock.configuration.Tag tag = new io.github.kbuntrock.configuration.Tag();
		final Substitution sub = new Substitution();
		sub.setRegex("Impl$");
		sub.setSubstitute("");
		tag.setSubstitutions(Collections.singletonList(sub));
		apiConfiguration2.setTag(tag);
		final JavadocConfiguration javadocConfig2 = new JavadocConfiguration();
		javadocConfig2.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/account",
			"src/test/java/io/github/kbuntrock/resources/implementation/account",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo2.setJavadocConfiguration(javadocConfig2);
		mojo2.setTestMode(true);
		mojo2.setApis(Collections.singletonList(apiConfiguration2));
		mojo2.setProject(createBasicMavenProject());

		final List<File> generated1 = mojo1.documentProject();
		final List<File> generated2 = mojo2.documentProject();
		checkGenerationResult(generated1.get(0), generated2.get(0));
	}

	@Test
	public void white_list_class() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo("io.github.kbuntrock.resources.endpoint");
		// Should filter on NumberController and TimeController
		final List<String> whiteList = new ArrayList<>();
		whiteList.add(".*umberControl.*");
		whiteList.add(".*imeControl.*");
		mojo.getApiConfiguration().setWhiteList(whiteList);

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/white_list_class.yml", generated.get(0));
	}

	@Test
	public void white_list_class_method() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo("io.github.kbuntrock.resources.endpoint");
		// Should filter on AccountController.invalidateSession
		final List<String> whiteList = new ArrayList<>();
		whiteList.add(".*ccountControl.*#.*lidateSession");
		mojo.getApiConfiguration().setWhiteList(whiteList);

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/white_list_class_method.yml", generated.get(0));
	}

	@Test
	public void white_list_method() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(AccountController.class.getCanonicalName());
		// Should filter on AccountController -> 4 methods in it
		final List<String> whiteList = new ArrayList<>();
		whiteList.add("#.*Account");
		mojo.getApiConfiguration().setWhiteList(whiteList);

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/white_list_method.yml", generated.get(0));

		final DocumentationMojo mojo2 = createBasicMojo(AccountController.class.getCanonicalName());
		// Should filter on AccountController -> 4 methods in it
		final List<String> whiteList2 = new ArrayList<>();
		whiteList2.add(".*#.*Account");
		mojo2.getApiConfiguration().setWhiteList(whiteList2);

		final List<File> generated2 = mojo2.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/white_list_method.yml", generated2.get(0));
	}

	@Test
	public void black_list_class() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo("io.github.kbuntrock.resources.endpoint.generic");
		// Should filter on AccountController -> 4 methods in it
		final List<String> blackList = new ArrayList<>();
		blackList.add(".*\\.GenericityTestF.*$");
		blackList.add(".*\\.GenericityTestT.*$");
		blackList.add(".*\\.GenericityTestS.*$");
		blackList.add(".*\\.GenericityTestE.*$");
		blackList.add(".*\\.ActionResource");
		blackList.add(".*\\.GenericDataController");
		mojo.getApiConfiguration().setBlackList(blackList);

		// The result should be the same as the multiple_genericity test
		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/multiple_genericity.yml", generated.get(0));
	}

	@Test
	public void black_list_class_method() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(AccountController.class.getCanonicalName());
		// Should filter on AccountController -> 4 methods in it
		final List<String> blackList = new ArrayList<>();
		blackList.add(".*AccountController#.*Password.*");
		blackList.add(".*AccountController#.*Session.*");
		blackList.add(".*AccountController#isAuthenticated");
		mojo.getApiConfiguration().setBlackList(blackList);

		// The result should be the same as the white list method test
		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/white_list_method.yml", generated.get(0));
	}

	@Test
	public void black_list_method() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(AccountController.class.getCanonicalName());
		// Should filter on AccountController -> 4 methods in it
		final List<String> blackList = new ArrayList<>();
		blackList.add("#.*Password.*");
		blackList.add("#.*Session.*");
		blackList.add("#isAuthenticated");
		mojo.getApiConfiguration().setBlackList(blackList);

		// The result should be the same as the white list method test
		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/white_list_method.yml", generated.get(0));
	}

	@Test
	public void json_ignore() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(JsonIgnoreController.class.getCanonicalName());

		// The result should be the same as the white list method test
		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/json_ignore.yml", generated.get(0));
	}

	@Test
	public void annotated_controller() throws MojoFailureException, IOException, MojoExecutionException {
		final DocumentationMojo mojo = createBasicMojo(AnnotatedController.class.getCanonicalName());
		mojo.getApis().get(0).setTagAnnotations(Collections.singletonList("io.github.kbuntrock.resources.annotation.MyRestController"));
		// The result should be the same as the white list method test
		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/annotated_controller.yml", generated.get(0));
	}

	@Test
	public void collection() throws MojoFailureException, IOException, MojoExecutionException {
		final DocumentationMojo mojo = createBasicMojo(CollectionController.class.getCanonicalName());
		// The result should be the same as the white list method test
		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/collection.yml", generated.get(0));
	}

	@Test
	public void extra_classes() throws MojoFailureException, IOException, MojoExecutionException {
		final DocumentationMojo mojo = createBasicMojo(CollectionController.class.getCanonicalName());
		mojo.getApis().get(0).setExtraSchemaClasses(Collections.singletonList("io.github.kbuntrock.resources.dto.AccountDto"));
		// The result should be the same as the white list method test
		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/extra_classes.yml", generated.get(0));
	}

}
