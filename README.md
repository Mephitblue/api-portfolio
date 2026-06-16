# REST Assured API Test Framework

A Java-based API test automation framework built with REST Assured and TestNG, demonstrating enterprise framework design patterns for API validation across multiple APIs with and without authentication.

## Framework Design

The framework is structured around four core design decisions:

**Properties-driven configuration** — Base URI and authentication settings are loaded from properties files at runtime. Each test class specifies its target API by passing a properties file name to `BaseTest`. API keys are never stored in source code — they are read from environment variables.

**Reusable RequestSpecification** — Base URI, content type, and authentication headers are configured once in `BaseTest` and inherited by all test classes. Individual tests focus on what they validate, not on request setup boilerplate.

**POJO-based request and response modeling** — Jackson serializes Java objects to JSON request bodies and deserializes JSON responses into typed objects, allowing assertions on strongly typed fields rather than raw strings.

**Centralized response validation** — `ResponseValidator` provides reusable assertion methods for status codes, field values, and response time thresholds, keeping test methods focused and readable.

## Project Structure

```
src/test/java/com/keithlamond/api/
├── base/
│   └── BaseTest.java              # Properties-driven RequestSpec and ResponseSpec setup
├── models/
│   └── Post.java                  # POJO for /posts endpoint
├── tests/
│   ├── PostsTest.java             # Test cases for JSONPlaceholder /posts endpoint
│   └── CatApiTest.java            # Test cases for The Cat API /breeds and /images endpoints
└── utils/
    └── ResponseValidator.java     # Reusable assertion utilities

src/test/resources/
├── jsonplaceholder.properties     # Base URI config for JSONPlaceholder
├── catapi.properties              # Base URI and auth config for The Cat API
├── schemas/
│   ├── post-schema.json           # JSON schema for single post response
│   └── posts-array-schema.json   # JSON schema for posts array response
└── testng.xml                     # Test suite configuration

.github/workflows/
└── api-tests.yml                  # GitHub Actions CI pipeline
```

## Test Coverage

### PostsTest — [JSONPlaceholder](https://jsonplaceholder.typicode.com/) (no authentication)

- GET — retrieve all resources, retrieve by ID, filter by query parameter
- POST — create a new resource with a POJO request body
- PUT — update an existing resource
- DELETE — delete a resource
- Chained requests — extract a value from one response and use it in a subsequent request
- Response time validation — verify API responds within an acceptable threshold
- Negative testing — verify correct error response for invalid resource ID
- JSON schema validation — verify response structure matches defined schema for both single object and array responses
- Data-driven testing — verify multiple IDs and expected titles using `@DataProvider`

### CatApiTest — [The Cat API](https://thecatapi.com/) (API key authentication)

- GET — retrieve all breeds, retrieve breed by ID, search images
- Query parameter filtering — limit results with query parameters
- Negative testing — verify correct error response for invalid breed ID
- Authentication — API key injected via environment variable, added to RequestSpec as a header

## Running the Tests

Prerequisites: Java 11+, Maven 3.6+

The Cat API tests require a `CAT_API_KEY` environment variable to be set. Sign up for a free key at [thecatapi.com](https://thecatapi.com).

```bash
# Run the full suite
mvn test

# Run a single test class
mvn test -Dtest=PostsTest

# Run a single test method
mvn test -Dtest=PostsTest#getPostById_ValidId_Returns200WithCorrectData
```

## CI/CD Integration

The suite runs automatically on push and pull request via GitHub Actions. The `CAT_API_KEY` is stored as a GitHub Actions repository secret and injected as an environment variable at runtime. TestNG reports are uploaded as build artifacts on every run.

## Technologies

- **REST Assured 5.4.0** — fluent Java DSL for API test automation
- **TestNG 7.9.0** — test execution, assertions, data-driven testing, and suite organization
- **Jackson 2.17.0** — JSON serialization and deserialization
- **Maven** — dependency management and build execution
- **GitHub Actions** — CI/CD pipeline with secret management