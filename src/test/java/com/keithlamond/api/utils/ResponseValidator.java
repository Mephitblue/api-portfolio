package com.keithlamond.api.utils;

import io.restassured.response.Response;
import org.testng.Assert;

/**
 * ResponseValidator provides reusable assertion methods for common
 * response validation patterns.
 *
 * Centralizing these validations means individual test methods stay
 * focused on what is unique about each test case rather than
 * repeating the same assertion boilerplate across the suite.
 */
public class ResponseValidator {

    /**
     * Validates that the response returned the expected HTTP status code.
     */
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Expected status code " + expectedStatusCode +
                " but got " + response.getStatusCode());
    }

    /**
     * Validates that the response body contains a field with the expected value.
     */
    public static void assertFieldValue(Response response, String fieldPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(fieldPath);
        Assert.assertEquals(actualValue, expectedValue,
                "Expected field '" + fieldPath + "' to be '" + expectedValue +
                "' but got '" + actualValue + "'");
    }

    /**
     * Validates that the response body contains a non-null, non-empty field.
     */
    public static void assertFieldNotEmpty(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        Assert.assertNotNull(value, "Expected field '" + fieldPath + "' to be present but it was null");
        Assert.assertFalse(value.toString().isEmpty(),
                "Expected field '" + fieldPath + "' to be non-empty");
    }

    /**
     * Validates that the response time is within an acceptable threshold.
     * Useful for basic performance sanity checks.
     */
    public static void assertResponseTime(Response response, long maxMilliseconds) {
        long responseTime = response.getTime();
        Assert.assertTrue(responseTime <= maxMilliseconds,
                "Response time " + responseTime + "ms exceeded threshold of " + maxMilliseconds + "ms");
    }
}
