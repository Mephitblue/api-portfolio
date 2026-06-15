package com.keithlamond.api.tests;

import com.keithlamond.api.base.BaseTest;
import com.keithlamond.api.models.Post;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

/**
 * PostsTest covers the /posts endpoint on JSONPlaceholder.
 *
 * JSONPlaceholder is a free REST API for testing and prototyping.
 * It supports GET, POST, PUT, PATCH, and DELETE — responses are
 * simulated but structurally correct, making it ideal for framework
 * development and portfolio demonstrations.
 *
 * API documentation: https://jsonplaceholder.typicode.com
 */

public class PostsTest extends BaseTest {

    @Test(description = "GET /posts returns 200 and non-empty list")
    public void getAllPosts_Returns200() {
        given(requestSpec)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .body("$.size()", greaterThan(0));
    }

    @Test(description = "GET /posts ID 1 and verify correct post returned and it has Title field")
    public void getPostById_ValidId_Returns200WithCorrectData() {
        Post post = given(requestSpec)
                .when()
                .get("/posts/1")
                .then()
                .statusCode(200)
                .extract()
                .as(Post.class);
        Assert.assertEquals(post.getId(), 1);
        Assert.assertEquals(post.getTitle(), "sunt aut facere repellat provident occaecati excepturi optio reprehenderit");
    }

    @Test(description = "GET /posts with invalid ID of 9999 and check returned status is a 404")
    public void getPostByID_InvalidId_Returns404() {
        given(requestSpec)
                .when()
                .get("/posts/9999")
                .then()
                .statusCode(404);
    }

    @Test(description = "GET /posts filtered on userId 1 and verify 10 records returned and they all have userId 1")
    public void getAllPosts_FilteredByUserID1_Returns200WithCorrectSizeAndUserId() {
        given(requestSpec)
                .queryParam("userId", 1)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(10))
                .body("userId", everyItem(equalTo(1)));
    }

    @Test(description = "POST /post with userId 101, title new post, and body test message.  Verify post contents echoed in response")
    public void postPosts_UserId_Title_Body_ReturnEchoPostFields() {
        int userId = 101;
        String title = "New test post";
        String body = "Body of new test post";
        Post post = new Post(userId, title, body);
        given(requestSpec)
                .body(post)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("userId", equalTo(userId))
                .body("title", equalTo(title))
                .body("body", equalTo(body));

    }

    @Test(description = "PUT /posts/1 with userId 101, title, updated post, and body updated body. Verify updated post contents echoed in response")
    public void putPosts_UserId_Title_Body_ReturnEchoUpdatedFields() {
        int userId = 101;
        String title = "Updated Post";
        String body = "Updated body";
        Post updatedPost = new Post(userId, title, body);
        given(requestSpec)
                .body(updatedPost)
                .when()
                .put("/posts/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userId", equalTo(userId))
                .body("title", equalTo(title))
                .body("body", equalTo(body));
    }

    @Test(description = "DELETE /posts/1 and verify 200 status code")
    public void deletePosts_Id1_Verify200StatusCode() {
        given(requestSpec)
                .when()
                .delete("/posts/1")
                .then()
                .statusCode(200);
    }

    @Test(description = "POST /posts new post with userId, title, and body fields, verify echo response, and extract returned id")
    public void postPosts_userId_title_body_VerifyEchoAndExtractId() {
        int userId = 201;
        String title = "New post for chain GET check";
        String body = "Body of new post for chain GET check";
        Post post = new Post(userId, title, body);
        int id = given(requestSpec)
                .body(post)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .body("userId", equalTo(userId))
                .body("title", equalTo(title))
                .body("body", equalTo(body))
                .extract()
                .path("id");

        Assert.assertTrue(id > 0);

/*
    Note - JsonPlaceHolder doesn't support chaining.  Below is the code to perform a GET to verify
    newly created data in the first POST part of the chain.

       given(requestSpec)
        .when()
            .get("/posts/" + id)
        .then()
            .statusCode(200)
            .body("id", equalTo(id))
            .body("userId", equalTo(userId))
            .body("title", equalTo(title))
            .body("body", equalTo(body));*/
    }

    @Test(description = "GET /posts returns 200 and response time less than 2 seconds")
    public void getAllPosts_Returns200_ResponseTimeLessThan2000Milliseconds() {
        given(requestSpec)
        .when()
            .get("/posts")
        .then()
            .statusCode(200)
            .time(lessThan(2000L));
    }

    @DataProvider(name = "postIds")
    public Object[][] postIds() {
        return new Object[][]{
                {1},
                {2},
                {3}
        };
    }

    @Test(dataProvider = "postIds", description = "GET /posts/{id} with multiple valid IDs return 200")
    public void getMultiplePosts_ValidUserIds_Return200(int id){
        given(requestSpec)
        .when()
            .get("/posts/{id}", id)
        .then()
            .statusCode(200);
    }
}
