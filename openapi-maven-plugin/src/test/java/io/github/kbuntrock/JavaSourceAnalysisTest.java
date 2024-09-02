package io.github.kbuntrock;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.resources.dto.AccountDto;
import io.github.kbuntrock.resources.dto.Authority;
import io.github.kbuntrock.resources.dto.SpeAccountDto;
import io.github.kbuntrock.resources.dto.TimeDto;
import io.github.kbuntrock.resources.endpoint.javasource.ControllerOne;
import io.github.kbuntrock.resources.endpoint.javasource.ControllerThree;
import io.github.kbuntrock.resources.endpoint.javasource.ControllerTwo;
import java.util.Optional;
import java.util.Set;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Check if all DataObjects which will endup in the schema reference section are correctly marked.
 *
 * @author Kevin Buntrock
 */
public class JavaSourceAnalysisTest extends AbstractTest {

	private boolean containsClassRepresentation(final Set<DataObject> set, final Class<?> clazz) {
		for(final DataObject dataObject : set) {
			if(dataObject.getJavaClass().equals(clazz)) {
				return true;
			}
		}
		return false;
	}

	@Test
	public void controllerOne() throws MojoFailureException {

		final ApiConfiguration apiConfiguration = new ApiConfiguration();
		apiConfiguration.initDefaultValues();

		final JavaClassAnalyser analyser = new JavaClassAnalyser(apiConfiguration, scanResult());
		final Optional<Tag> tag = analyser.getTagFromClass(ControllerOne.class);
		final TagLibrary library = new TagLibrary();
		library.addTag(tag.get());

		Assertions.assertEquals(3, library.getSchemaObjects().size());
		Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), TimeDto.class));
		Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), Authority.class));
		Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), SpeAccountDto.class));
	}

	@Test
	public void controllerTwo() throws MojoFailureException {

		final ApiConfiguration apiConfiguration = new ApiConfiguration();
		apiConfiguration.initDefaultValues();

		final JavaClassAnalyser analyser = new JavaClassAnalyser(apiConfiguration, scanResult());
		final Optional<Tag> tag = analyser.getTagFromClass(ControllerTwo.class);
		final TagLibrary library = new TagLibrary();
		library.addTag(tag.get());

		Assertions.assertEquals(3, library.getSchemaObjects().size());
		Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), TimeDto.class));
		Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), Authority.class));
		Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), SpeAccountDto.class));
	}

	@Test
	public void controllerThree() throws MojoFailureException {

		final ApiConfiguration apiConfiguration = new ApiConfiguration();
		apiConfiguration.initDefaultValues();

		final JavaClassAnalyser analyser = new JavaClassAnalyser(apiConfiguration, scanResult());
		final Optional<Tag> tag = analyser.getTagFromClass(ControllerThree.class);
		final TagLibrary library = new TagLibrary();
		library.addTag(tag.get());

		Assertions.assertEquals(4, library.getSchemaObjects().size());
		Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), TimeDto.class));
		Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), Authority.class));
		Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), SpeAccountDto.class));
		Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), AccountDto.class));
	}

	private ScanResult scanResult() {
		return new ClassGraph()
			.enableMethodInfo()
			.enableClassInfo()
			.enableAnnotationInfo()
			.ignoreClassVisibility()
			.ignoreMethodVisibility()
			.ignoreParentClassLoaders()
			.acceptPackages("io.github.kbuntrock.resources")
			.addClassLoader(ReflectionsUtils.getProjectClassLoader())
			.scan();

	}

}
