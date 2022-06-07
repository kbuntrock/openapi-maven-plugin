package com.github.kbuntrock.it;

import com.soebes.itf.extension.assertj.MavenExecutionResultAssert;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.Assertions;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

/**
 * @author Kevin Buntrock
 */
@MavenJupiterExtension
public class BasicIT {

    @MavenTest
    @MavenGoal("install")
    void nominal_test_case(MavenExecutionResult result) throws IOException {
        MavenExecutionResultAssert resultAssert = assertThat(result);
        resultAssert.isSuccessful().out().info().contains("spec-open-api : 1 tags and 2 operations generated.");

        File target = new File(result.getMavenProjectResult().getTargetProjectDirectory(), "target");
        File generatedFile = new File(target, "spec-open-api.yml");
        Assertions.assertTrue(target.exists());
        Assertions.assertTrue(generatedFile.exists());

        File m2Directory = result.getMavenProjectResult().getTargetCacheDirectory();
        File generatedArtifactFile = new File(m2Directory, "/com/github/kbuntrock/openapi/it/openapi-basic-it/23.5.2/openapi-basic-it-23.5.2-spec-open-api.yml");
        Assertions.assertTrue(generatedArtifactFile.exists());


        try (InputStream generatedFileStream = new FileInputStream(generatedArtifactFile);
             InputStream resourceFileStream = BasicIT.class.getClassLoader().getResourceAsStream("it/BasicIT/nominal_test_case.yml")) {
            String md5GeneratedHex = DigestUtils.md5DigestAsHex(generatedFileStream);
            String md5ResourceHex = DigestUtils.md5DigestAsHex(resourceFileStream);

            Assertions.assertEquals(md5ResourceHex, md5GeneratedHex);
        }


    }

}