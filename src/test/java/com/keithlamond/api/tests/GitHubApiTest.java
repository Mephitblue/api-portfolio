package com.keithlamond.api.tests;

import com.keithlamond.api.base.BaseTest;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

/**
 * GitHubApiTest covers the /user, /users, and /repos endpoints on the GitHub REST API.
 *
 * The GitHub REST API is a production API providing access to GitHub resources
 * including users, repositories, and organizations. It requires a Personal Access
 * Token passed as a Bearer token in the Authorization header, demonstrating OAuth 2.0
 * Bearer token authentication as distinct from the API key pattern used in CatApiTest.
 *
 * Endpoints covered:
 *   GET /user                              — authenticated user profile
 *   GET /users/Mephitblue                  — public user profile
 *   GET /users/Mephitblue/repos            — public repository list
 *   GET /repos/Mephitblue/api-portfolio    — specific repository details
 *   GET /users/invaliduser99999            — negative test, expects 404
 *
 * API documentation: https://docs.github.com/en/rest
 */

public class GitHubApiTest extends BaseTest{
    private static final String API_PORTFOLIO_DESCRIPTION =
        "Java REST Assured API test framework targeting JSONPlaceholder. " +
        "Covers GET, POST, PUT, DELETE, query filtering, negative testing, " +
        "chained requests, and response time validation. Maven/TestNG/Jackson " +
        "stack with GitHub Actions CI.";

    public GitHubApiTest() {
        super("github.properties");
    }

    @Test(description = "GET /user returns status 200, verify login, id, name, and location fields")
    public void getUser_VerifyLoginIdNameLocation_Status200() {
        given(requestSpec)
        .when()
            .get("/user")
        .then()
            .statusCode(200)
            .body("login", equalTo("Mephitblue"))
            .body("name", equalTo("Keith Lamond"))
            .body("location", equalTo("Portland OR"))
            .body("id", equalTo(8877959));
    }

    @Test(description = "GET /users/Mephitblue returns status 200, verify html_url and url")
    public void getUsersMephitblue_VerifyHtml_UrlAndUrl_Status200(){
        given(requestSpec)
        .when()
           .get("/users/Mephitblue")
        .then()
            .statusCode(200)
            .body("html_url", equalTo("https://github.com/Mephitblue"))
            .body("url", equalTo( "https://api.github.com/users/Mephitblue"));
    }

    @Test(description = "GET /users/Mephitblue/repos returns status 200, verify array not null and api-portfolio private and visibility fields")
    public void getUsersMephitblue_Repos_NoNull_ApiPortfolioPrivateVisibility_Status200(){
        given(requestSpec)
        .when()
            .get("/users/Mephitblue/repos")
        .then()
            .statusCode(200)
            .body("$.size()", greaterThan(0))
            .body("find { it.name == 'api-portfolio' }.private", equalTo(false))
            .body("find { it.name == 'api-portfolio' }.visibility", equalTo("public"));
    }

    @Test(description = "GET /repos/Mephitblue/api-portfolio returns status 200, verify full_name, description, and owner.login")
    public void getReposMephitblueApiPortfolio_VerifyFullNameDescriptionLogin_Status200(){
        given(requestSpec)
        .when()
            .get("/repos/Mephitblue/api-portfolio")
        .then()
            .statusCode(200)
            .body("full_name", equalTo("Mephitblue/api-portfolio"))
            .body("description", equalTo(API_PORTFOLIO_DESCRIPTION))
            .body("owner.login", equalTo("Mephitblue"));
    }

    @Test(description = "GET /users/invaliduser9999 returns status 404")
    public void getUsersInvalidUser_Status404(){
        given(requestSpec)
        .when()
            .get("/users/invaliduser9999")
        .then()
            .statusCode(404);
    }
}
