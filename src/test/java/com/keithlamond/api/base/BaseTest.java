package com.keithlamond.api.base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * BaseTest establishes reusable request and response specifications
 * shared across all test classes.
 *
 * Configuration is loaded from a properties file specified by each
 * subclass via the constructor. Supported properties:
 *
 *   base.uri      — the root URL for the target API
 *   api.key.env   — the name of the environment variable holding the API key
 *   auth.scheme   — the authentication scheme to apply (none, api-key, bearer)
 *
 * Authentication schemes:
 *   none      — no authentication header added (e.g. JSONPlaceholder)
 *   api-key   — adds x-api-key header (e.g. The Cat API)
 *   bearer    — adds Authorization: Bearer header (e.g. GitHub API)
 *
 * API keys and tokens are never stored in source code. They are read
 * from environment variables at runtime and injected via GitHub Actions
 * repository secrets in CI.
 */
public class BaseTest {

    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;

    private final String propertiesFile;

    public BaseTest(String propertiesFile){
        this.propertiesFile = propertiesFile;
    }

    @BeforeClass
    public void setupSpec() throws IOException{
        Properties props = new Properties();

        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream(propertiesFile)) {
            if (input == null) {
                throw new RuntimeException("Properties file not found: " + propertiesFile);
            }
            props.load(input);
        }

        String baseUri = props.getProperty("base.uri");
        String apiKeyEnvVar = props.getProperty("api.key.env", "");

        // Build the base request specification — applied to every test in the class
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON);

        String authScheme = props.getProperty("auth.scheme", "none");

        if (!authScheme.equals("none") && !apiKeyEnvVar.isEmpty()) {
            String apiKey = System.getenv(apiKeyEnvVar);
            if (apiKey == null || apiKey.isEmpty()) {
                throw new RuntimeException("Environment variable not set: " + apiKeyEnvVar);
            }
            switch(authScheme){
                case "api-key" -> builder.addHeader("x-api-key", apiKey);
                case "bearer" -> builder.addHeader("Authorization", "Bearer " + apiKey);
                default -> throw new RuntimeException("Unknown auth scheme: " + authScheme);
            }
        }

        requestSpec = builder.build();

        // Build the base response specification — common assertions applied to every response
        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();
    }
}
