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
 * Reads base URI and optional API key configuration from a properties
 * file. Subclasses specify which properties file to load by passing
 * the filename to the constructor. API keys are never stored in source
 * code — they are read from environment variables at runtime.
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

        if (!apiKeyEnvVar.isEmpty()) {
            String apiKey = System.getenv(apiKeyEnvVar);
            if (apiKey == null || apiKey.isEmpty()) {
                throw new RuntimeException("Environment variable not set: " + apiKeyEnvVar);
            }
            builder.addHeader("x-api-key", apiKey);
        }

        requestSpec = builder.build();

        // Build the base response specification — common assertions applied to every response
        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();
    }
}
