package io.github.kbuntrock;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.configuration.OperationIdHelper;
import io.github.kbuntrock.configuration.Substitution;
import io.github.kbuntrock.configuration.library.Library;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.resources.endpoint.account.AccountController;
import io.github.kbuntrock.resources.endpoint.account.AccountJaxrsController;
import io.github.kbuntrock.resources.endpoint.enumeration.*;
import io.github.kbuntrock.resources.endpoint.error.SameOperationController;
import io.github.kbuntrock.resources.endpoint.file.FileUploadController;
import io.github.kbuntrock.resources.endpoint.file.StreamResponseController;
import io.github.kbuntrock.resources.endpoint.generic.*;
import io.github.kbuntrock.resources.endpoint.interfacedto.InterfaceController;
import io.github.kbuntrock.resources.endpoint.map.MapController;
import io.github.kbuntrock.resources.endpoint.number.NumberController;
import io.github.kbuntrock.resources.endpoint.path.SpringPathEnhancementOneController;
import io.github.kbuntrock.resources.endpoint.path.SpringPathEnhancementTwoController;
import io.github.kbuntrock.resources.endpoint.recursive.*;
import io.github.kbuntrock.resources.endpoint.spring.OptionalController;
import io.github.kbuntrock.resources.endpoint.spring.ResponseEntityController;
import io.github.kbuntrock.resources.endpoint.time.TimeController;
import io.github.kbuntrock.resources.implementation.account.AccountControllerImpl;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JaxrsClassAnalyserTest extends AbstractTest {

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
        apiConfiguration.setLibrary(Library.JAXRS.name());
        apiConfiguration.setAttachArtifact(false);
        apiConfiguration.setLocations(Arrays.asList(apiLocation));
        mojo.setTestMode(true);
        mojo.setApis(Arrays.asList(apiConfiguration));
        mojo.setProject(createBasicMavenProject());
        return mojo;
    }


    @Test
    public void jaxrs_basic() throws MojoFailureException, MojoExecutionException, IOException {

        DocumentationMojo mojo = createBasicMojo(AccountJaxrsController.class.getCanonicalName());

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/JaxrsClassAnalyserTest/jaxrs_basic.yml", generated.get(0));
    }

}
