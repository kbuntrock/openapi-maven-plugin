package io.github.kbuntrock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.CommonApiConfiguration;
import io.github.kbuntrock.configuration.EnumConfig;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.configuration.OperationIdHelper;
import io.github.kbuntrock.configuration.Substitution;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.resources.dto.TerritoryEnum;
import io.github.kbuntrock.resources.endpoint.account.AccountController;
import io.github.kbuntrock.resources.endpoint.annotation.AnnotatedController;
import io.github.kbuntrock.resources.endpoint.collection.CollectionController;
import io.github.kbuntrock.resources.endpoint.collision.FirstEndpoint;
import io.github.kbuntrock.resources.endpoint.collision.SecondEndpoint;
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
import io.github.kbuntrock.resources.endpoint.generic.ExtendsMap;
import io.github.kbuntrock.resources.endpoint.generic.GenericDataController;
import io.github.kbuntrock.resources.endpoint.generic.GenericMappingObject;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestEight;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestEleven;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestFive;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestFour;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestNine;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestOne;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestSeven;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestSix;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestTen;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestThree;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestTwelve;
import io.github.kbuntrock.resources.endpoint.generic.GenericityTestTwo;
import io.github.kbuntrock.resources.endpoint.generic.Issue144;
import io.github.kbuntrock.resources.endpoint.generic.Issue89;
import io.github.kbuntrock.resources.endpoint.generic.Issue95;
import io.github.kbuntrock.resources.endpoint.generic.MappingObject;
import io.github.kbuntrock.resources.endpoint.header.MultipartFileWithHeaderController;
import io.github.kbuntrock.resources.endpoint.ignore.JsonIgnoreController;
import io.github.kbuntrock.resources.endpoint.interfacedto.InterfaceController;
import io.github.kbuntrock.resources.endpoint.jackson.JacksonJsonPropertyController;
import io.github.kbuntrock.resources.endpoint.map.MapController;
import io.github.kbuntrock.resources.endpoint.nullable.NullableController;
import io.github.kbuntrock.resources.endpoint.nullable.NullableGettersSettersController;
import io.github.kbuntrock.resources.endpoint.number.NumberController;
import io.github.kbuntrock.resources.endpoint.path.SpringPathEnhancementOneController;
import io.github.kbuntrock.resources.endpoint.path.SpringPathEnhancementTwoController;
import io.github.kbuntrock.resources.endpoint.queryparam.QueryParamDtoBindingController;
import io.github.kbuntrock.resources.endpoint.queryparam.QueryParamFlatMixNestedDtoBindingController;
import io.github.kbuntrock.resources.endpoint.recursive.GenericRecursiveDtoController;
import io.github.kbuntrock.resources.endpoint.recursive.GenericRecursiveInterfaceDtoController;
import io.github.kbuntrock.resources.endpoint.recursive.GenericRecursiveInterfaceListDtoInParameterController;
import io.github.kbuntrock.resources.endpoint.recursive.GenericRecursiveListDtoController;
import io.github.kbuntrock.resources.endpoint.recursive.RecursiveDtoController;
import io.github.kbuntrock.resources.endpoint.recursive.RecursiveDtoInParameterController;
import io.github.kbuntrock.resources.endpoint.spring.OptionalController;
import io.github.kbuntrock.resources.endpoint.spring.ResponseEntityController;
import io.github.kbuntrock.resources.endpoint.time.TimeController;
import io.github.kbuntrock.resources.endpoint.uuid.UuidController;
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

	private DocumentationMojo createBasicMojo(final String... apiLocation) {
		final DocumentationMojo mojo = new DocumentationMojo();
		final ApiConfiguration apiConfiguration = new ApiConfiguration();
		apiConfiguration.setAttachArtifact(false);
		apiConfiguration.setLocations(Arrays.asList(apiLocation));
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

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void nested_genericity() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestTwo.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void genericity_wrapped_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestThree.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void genericity_typed_wrapped_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestFour.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void genericity_list_long() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestFive.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void genericity_extends() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestSix.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void genericity_extends_class_in_parameter() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestSeven.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
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

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void genericity_reference_self_class() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestNine.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void genericity_cross_reference() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestTen.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void genericity_cross_reference_in_super_constructor() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestTwelve.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void genericity_in_super_constructor() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericityTestEleven.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void issue_89() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(Issue89.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/generic",
			"src/test/java/io/github/kbuntrock/resources/dto/genericity/issue89"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void issue_95() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(Issue95.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/generic",
			"src/test/java/io/github/kbuntrock/resources/dto/genericity/issue95"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void file_upload() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(FileUploadController.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void stream_download() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(StreamResponseController.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void enumeration_test_1() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration1Controller.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void enumeration_test_2() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration2Controller.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void enumeration_test_3() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration3Controller.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void enumeration_test_4() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration4Controller.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void enumeration_test_5() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration5Controller.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void enumeration_test_6() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration6Controller.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void enumeration_test_7() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration7Controller.class.getCanonicalName());
		final EnumConfig enumConfig = new EnumConfig();
		enumConfig.setCanonicalName(TerritoryEnum.class.getCanonicalName());
		enumConfig.setValueField("code");
		mojo.getApiConfiguration().setEnumConfigList(Arrays.asList(enumConfig));
		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void time_objects() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(TimeController.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void map_objects() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(MapController.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
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

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void pathEnhancement() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(SpringPathEnhancementOneController.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void optional() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(OptionalController.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void response_entity() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(ResponseEntityController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/spring",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void interface_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(InterfaceController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/interfacedto",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
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

		final JavaClassAnalyser analyser = new JavaClassAnalyser(apiConfiguration, scanResult(SpringPathEnhancementTwoController.class));
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

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void recursive_dto_in_parameter() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(RecursiveDtoInParameterController.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void generic_recursive_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericRecursiveDtoController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/recursive",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void generic_recursive_list_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericRecursiveListDtoController.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void generic_recursive_interface_list_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericRecursiveInterfaceListDtoInParameterController.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void generic_recursive_interface_dto() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericRecursiveInterfaceDtoController.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void generically_typed_controller() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericDataController.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
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

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void white_list_class_method() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo("io.github.kbuntrock.resources.endpoint");
		// Should filter on AccountController.invalidateSession
		final List<String> whiteList = new ArrayList<>();
		whiteList.add(".*ccountControl.*#.*lidateSession");
		mojo.getApiConfiguration().setWhiteList(whiteList);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void white_list_method() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(AccountController.class.getCanonicalName());
		// Should filter on AccountController -> 4 methods in it
		final List<String> whiteList = Collections.singletonList("#.*Account");
		mojo.getApiConfiguration().setWhiteList(whiteList);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void white_list_method2() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(AccountController.class.getCanonicalName());
		// Should filter on AccountController -> 4 methods in it
		final List<String> whiteList2 = Collections.singletonList(".*#.*Account");
		mojo.getApiConfiguration().setWhiteList(whiteList2);

		checkGenerationResult(mojo.documentProject());
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
		blackList.add(".*\\.GenericityTestN.*$");
		blackList.add(".*\\.Issue.*$");
		blackList.add(".*\\.ExtendsMap.*$");
		blackList.add(".*\\.MappingObject.*$");
		blackList.add(".*\\.GenericMappingObject.*$");
		blackList.add(".*\\.ActionResource");
		blackList.add(".*\\.GenericDataController");
		mojo.getApiConfiguration().setBlackList(blackList);

		checkGenerationResult(mojo.documentProject());
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
		checkGenerationResult(mojo.documentProject());
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
		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void json_ignore() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(JsonIgnoreController.class.getCanonicalName());

		// The result should be the same as the white list method test
		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void annotated_controller() throws MojoFailureException, IOException, MojoExecutionException {
		final DocumentationMojo mojo = createBasicMojo(AnnotatedController.class.getCanonicalName());
		mojo.getApis().get(0).setTagAnnotations(Collections.singletonList("io.github.kbuntrock.resources.annotation.MyRestController"));
		// The result should be the same as the white list method test
		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void collection() throws MojoFailureException, IOException, MojoExecutionException {
		final DocumentationMojo mojo = createBasicMojo(CollectionController.class.getCanonicalName());
		// The result should be the same as the white list method test
		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void package_private() throws MojoFailureException, IOException, MojoExecutionException {
		final DocumentationMojo mojo = createBasicMojo("io.github.kbuntrock.resources.endpoint.spring.PackagePrivateResource");
		// The result should be the same as the white list method test
		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void extra_classes() throws MojoFailureException, IOException, MojoExecutionException {
		final DocumentationMojo mojo = createBasicMojo(CollectionController.class.getCanonicalName());
		mojo.getApis().get(0).setExtraSchemaClasses(Collections.singletonList("io.github.kbuntrock.resources.dto.AccountDto"));
		// The result should be the same as the white list method test
		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void name_collision() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(FirstEndpoint.class.getCanonicalName(),
			SecondEndpoint.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void tag_name_collision() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(
			io.github.kbuntrock.resources.endpoint.namecollision.one.MyController.class.getCanonicalName(),
			io.github.kbuntrock.resources.endpoint.namecollision.two.MyController.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void enpoint_path_collision() throws MojoFailureException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(
			io.github.kbuntrock.resources.endpoint.namecollision.three.MyController.class.getCanonicalName());

		assertThatThrownBy(() -> {
			mojo.documentProject();
		}).isInstanceOf(MojoRuntimeException.class)
			.hasMessageContaining("More than one operation mapped on GET : /api/controller-3/info in tag MyController");
	}

	@Test
	public void jacksonJsonProperty() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(JacksonJsonPropertyController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/jackson",
			"src/test/java/io/github/kbuntrock/resources/dto/jackson"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void optional_unmapping() throws IOException, MojoExecutionException, MojoFailureException {

		final DocumentationMojo mojo = createBasicMojo(
			io.github.kbuntrock.resources.endpoint.optional.object.OptionalController.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void nullable_default() throws MojoExecutionException, MojoFailureException, IOException {
		final DocumentationMojo mojo = createBasicMojo(NullableController.class.getCanonicalName());
		final CommonApiConfiguration commonApiConfiguration = new CommonApiConfiguration();
		mojo.setApiConfiguration(commonApiConfiguration);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void nullable_default_custom_annotation() throws MojoExecutionException, MojoFailureException, IOException {
		final DocumentationMojo mojo = createBasicMojo(NullableController.class.getCanonicalName());
		final CommonApiConfiguration commonApiConfiguration = new CommonApiConfiguration();
		commonApiConfiguration.setNonNullableAnnotation(Arrays.asList("io.github.kbuntrock.resources.dto.nullable.MyNotNull"));
		commonApiConfiguration.setNullableAnnotation(Arrays.asList("io.github.kbuntrock.resources.dto.nullable.MyNullable"));
		mojo.setApiConfiguration(commonApiConfiguration);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void nullable_default_non_nullable() throws MojoExecutionException, MojoFailureException, IOException {
		final DocumentationMojo mojo = createBasicMojo(NullableController.class.getCanonicalName());
		final CommonApiConfiguration commonApiConfiguration = new CommonApiConfiguration();
		commonApiConfiguration.setDefaultNonNullableFields(true);
		mojo.setApiConfiguration(commonApiConfiguration);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void nullable_default_non_nullable_custom_annotation() throws MojoExecutionException, MojoFailureException, IOException {
		final DocumentationMojo mojo = createBasicMojo(NullableController.class.getCanonicalName());
		final CommonApiConfiguration commonApiConfiguration = new CommonApiConfiguration();
		commonApiConfiguration.setDefaultNonNullableFields(true);
		commonApiConfiguration.setNonNullableAnnotation(Arrays.asList("io.github.kbuntrock.resources.dto.nullable.MyNotNull"));
		commonApiConfiguration.setNullableAnnotation(Arrays.asList("io.github.kbuntrock.resources.dto.nullable.MyNullable"));
		mojo.setApiConfiguration(commonApiConfiguration);

		checkGenerationResult(mojo.documentProject());
	}

    @Test
    public void nullable_getters_setters() throws MojoExecutionException, MojoFailureException, IOException {
        final DocumentationMojo mojo = createBasicMojo(NullableGettersSettersController.class.getCanonicalName());
        final CommonApiConfiguration commonApiConfiguration = new CommonApiConfiguration();
        mojo.setApiConfiguration(commonApiConfiguration);

        checkGenerationResult(mojo.documentProject());
    }

    @Test
    public void nullable_getters_setters_default_non_nullable() throws MojoExecutionException, MojoFailureException, IOException {
        final DocumentationMojo mojo = createBasicMojo(NullableGettersSettersController.class.getCanonicalName());
        final CommonApiConfiguration commonApiConfiguration = new CommonApiConfiguration();
        commonApiConfiguration.setDefaultNonNullableFields(true);
        mojo.setApiConfiguration(commonApiConfiguration);

        checkGenerationResult(mojo.documentProject());
    }

	@Test
	public void query_param_dto_binding() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(QueryParamDtoBindingController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/queryparam",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void query_param_flat_mix_nested_dto_binding() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(QueryParamFlatMixNestedDtoBindingController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/queryparam",
				"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void request_headers() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(MultipartFileWithHeaderController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/header",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void uuid() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(UuidController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/uuid"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void generic_parent_bound_by_child() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(Issue144.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/generic",
			"src/test/java/io/github/kbuntrock/resources/dto/genericity/issue144"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void extends_map() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(ExtendsMap.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/generic",
			"src/test/java/io/github/kbuntrock/resources/dto/genericity/extendsMap"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void object_mapping() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(MappingObject.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/generic",
			"src/test/java/io/github/kbuntrock/resources/dto/genericity/mappingObject"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	//@Test
	public void generic_object_mapping() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo(GenericMappingObject.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/generic",
			"src/test/java/io/github/kbuntrock/resources/dto/genericity/mappingObject"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	// TODO : nesting controller not used!!!

	private ScanResult scanResult(Class<?> clazz) {
		return new ClassGraph()
			.enableMethodInfo()
			.enableClassInfo()
			.enableAnnotationInfo()
			.ignoreClassVisibility()
			.ignoreMethodVisibility()
			.ignoreParentClassLoaders()
			.acceptClasses(clazz.getCanonicalName())
			.addClassLoader(ReflectionsUtils.getProjectClassLoader())
			.scan();

	}

}
