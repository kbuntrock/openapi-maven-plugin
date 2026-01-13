package io.github.kbuntrock;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.apache.maven.rtinfo.RuntimeInformation;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.library.Library;

/**
 * To ensure this project can be effectively maintained over time, the purpose of this class is to collect a limited set of anonymous data.
 * This behavior can be disabled through configuration.
 *
 * The data collected are:
 * - Java version used to run Maven
 * - Maven version
 * - Version of this plugin
 * - Configured library (Spring, Jakarta REST, or JAX-RS)
 * - Half SHA-256 hash of the project identifier
 *
 * The collection of this data is entirely optional and must under no circumstances block the generation of the documentation or interfere
 * with the logs normally produced.
 */
public class Analytics {

	private static final String JSON_TEMPLATE = "{\"hash-id\":\"%s\",\"java-version\":\"%s\",\"maven-version\":\"%s\",\"plugin-version\":\"%s\",\"library-id\":%d}";
	private static final String ANALYTICS_URL = "https://api.openapi-maven-plugin.eu/api/v1/notify";

	private boolean sendAnalyticsConfigured;
	private String javaVersion;
	private String mavenVersion;
	private String pluginVersion;
	private int libraryId;
	private String hashId;

	public static Analytics build(
		boolean analytics,
		ApiConfiguration apiConfiguration,
		MavenProject project,
		PluginDescriptor pluginDescriptor,
		RuntimeInformation runtimeInformation,
		boolean testMode) {
		return new Analytics(analytics, apiConfiguration, project, pluginDescriptor, runtimeInformation, testMode);
	}

	private Analytics(boolean analytics, ApiConfiguration apiConfiguration, MavenProject project,
		PluginDescriptor pluginDescriptor,
		RuntimeInformation runtimeInformation, boolean testMode) {
		try {
			this.sendAnalyticsConfigured = !testMode && analytics;
			if(sendAnalyticsConfigured) {
				javaVersion = ObjectUtils.firstNonNull(System.getProperty("java.version"), "");
				mavenVersion = runtimeInformation != null ? runtimeInformation.getMavenVersion() : "";
				pluginVersion = pluginDescriptor != null ? pluginDescriptor.getVersion() : "";
				libraryId = createLibraryId(apiConfiguration.getLibrary());
				hashId = createHashId(project);
			}
		} catch(Exception e) {
			// Make sure no exception at all can come from the analytics part and hide other errors
		}
	}

	private String createHashId(MavenProject project) {
		String id = project.getGroupId() + ":" + project.getArtifactId();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(id.getBytes(StandardCharsets.UTF_8));
			byte[] truncated = Arrays.copyOf(hash, 16); // 16 octets = 128 bits (half the original size)
			return Base64.getEncoder().withoutPadding().encodeToString(truncated);
		} catch(Exception e) {
			// Nothing to do
		}
		return "";
	}

	public void send() {
		try {
			if(shouldSendAnalytics()) {
				String json = String.format(JSON_TEMPLATE, hashId, javaVersion, mavenVersion, pluginVersion, libraryId);
				postNotificationAsync(ANALYTICS_URL, json).get();
			}
		} catch(Exception ignored) {
			// Make sure no exception at all can come from the analytics part and hide other errors
		}
	}

	private boolean shouldSendAnalytics() {
		// Even if analytics is configured, the data are only sent approximately once out of four times in order to limit unnecessary data
		// transfers.
		// This results in approximately:
		// - a 94% chance of sending information over 10 builds
		// - a 99.7% chance of sending information over 20 builds
		return sendAnalyticsConfigured && (System.currentTimeMillis() % 4 == 0);
	}

	private CompletableFuture<Void> postNotificationAsync(String url, String json) {
		return CompletableFuture.supplyAsync(() -> {
			HttpURLConnection con = null;
			try {
				con = (HttpURLConnection) new URL(url).openConnection();
				con.setRequestMethod("POST");
				con.setConnectTimeout(500);
				con.setReadTimeout(500);
				con.setDoOutput(true);
				con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				con.setRequestProperty("Accept", "*/*");

				byte[] body = json.getBytes(StandardCharsets.UTF_8);
				try(OutputStream os = con.getOutputStream()) {
					os.write(body);
				}
				con.getResponseCode();
			} catch(Exception ignored) {
				return null;
			} finally {
				if(con != null) {
					con.disconnect();
				}
			}
			return null;
		}, Executors.newFixedThreadPool(1));
	}

	private int createLibraryId(Library library) {
		switch(library) {
			case SPRING_MVC:
				return 1;
			case JAKARTA_RS:
				return 2;
			case JAVAX_RS:
				return 3;
			default:
				return 0;
		}
	}

}
