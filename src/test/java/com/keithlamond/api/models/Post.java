package com.keithlamond.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Post represents the JSON structure returned by the /posts endpoint
 * on JSONPlaceholder.
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) tells Jackson to skip
 * any fields in the JSON response that are not mapped here — useful
 * when you only care about a subset of the response payload.
 *
 * Example response from GET /posts/1:
 * {
 *   "userId": 1,
 *   "id": 1,
 *   "title": "sunt aut facere...",
 *   "body": "quia et suscipit..."
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {

    private int userId;
    private int id;
    private String title;
    private String body;

    // Default constructor required by Jackson for deserialization
    public Post() {}

    // Constructor for building request payloads in POST/PUT tests
    public Post(int userId, String title, String body) {
        this.userId = userId;
        this.title = title;
        this.body = body;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    @Override
    public String toString() {
        return "Post{userId=" + userId + ", id=" + id +
               ", title='" + title + "', body='" + body + "'}";
    }
}
