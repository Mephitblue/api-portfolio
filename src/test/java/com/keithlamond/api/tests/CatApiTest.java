package com.keithlamond.api.tests;

import com.keithlamond.api.base.BaseTest;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

/**
 * CatApiTest covers the /breeds and images/search endpoints on CatAPI.
 *
 * CatAPI is a public set of APIs serving images and information on cats
 * that supports GET, POST, and DELETE.  It requires an API key for validation
 * and is used in this portfolio for demonstrating authentication via an API key.
 *
 * API documentation: https://developers.thecatapi.com
 */

public class CatApiTest extends BaseTest {

    public CatApiTest(){
        super("catapi.properties");
    }

    @Test(description = "GET /breeds returns status 200, array is non-empty and all items have id and name")
    public void getBreeds_ArrayNonEmpty_ItemsHaveIdName_Status200(){
        given(requestSpec)
        .when()
            .get("/breeds")
        .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0))
            .body("id", everyItem(notNullValue()))
            .body("name", everyItem(notNullValue()));
    }

    @Test(description = "GET /breeds/abys returns status 200, verify name and origin")
    public void getBreeds_ValidId_VerifyNameOrigin_Status200(){
        given(requestSpec)
        .when()
            .get("/breeds/abys")
        .then()
            .statusCode(200)
            .body("name", equalTo("Abyssinian"))
            .body("origin", equalTo("Egypt"));
    }

    @Test(description = "GET /breeds/invalid returns status 400")
    public void getBreeds_InvalidId_Status400(){
        given(requestSpec)
        .when()
            .get("/breeds/invalid")
        .then()
            .statusCode(400);
    }

    @Test(description = "GET /breeds with parameter limit 2, returns status 200, verify exactly 2 results")
    public void getBreeds_ParameterLimit2_Verify2Results_Status200(){
        given(requestSpec)
            .queryParam("limit", 2)
        .when()
            .get("/breeds")
        .then()
            .statusCode(200)
            .body("$.size()", equalTo(2));
    }

    @Test(description = "GET /images/search, returns status 200, verify 1 result and its url field is not null")
    public void getImagesSearch_Verify1Result_UrlNotNull_Status200(){
        given(requestSpec)
        .when()
            .get("/images/search")
        .then()
            .statusCode(200)
            .body("$.size()", equalTo(1))
            .body("[0].url", notNullValue());
    }
}
