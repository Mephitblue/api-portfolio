package com.keithlamond.api.base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

/**
 * BaseTest establishes reusable request and response specifications
 * shared across all test classes.
 *
 * This is the equivalent of the reusable utility class pattern used
 * in the Jersey/Jackson API utility work at ASRC — centralizing base
 * URI, headers, and content type so individual tests stay focused
 * on what they are validating, not on request boilerplate.
 */
public class BaseTest {

    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;

    @BeforeClass
    public void setupSpec() {

        // Build the base request specification — applied to every test in the class
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://jsonplaceholder.typicode.com")
                .setContentType(ContentType.JSON)
                .build();

        // Build the base response specification — common assertions applied to every response
        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();
    }
}
