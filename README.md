# REST Assured API Test Framework

A Java-based API test automation framework built with REST Assured and TestNG, demonstrating enterprise framework design patterns for API validation.

## Framework Design

The framework is structured around three core design decisions:

**Reusable RequestSpecification** — Base URI, content type, and common headers are configured once in `BaseTest` and inherited by all test classes. Individual tests focus on what they validate, not on request setup boilerplate.

**POJO-based request and response modeling** — Jackson serializes Java objects to JSON request bodies and deserializes JSON responses into typed objects. This mirrors the Jersey/Jackson utility pattern used in production API test data engineering.

**Centralized response validation** — `ResponseValidator` provides reusable assertion methods for status codes, field values, and response time thresholds, keeping test methods focused and readable.

## Project Structure

```
src/test/java/com/keithlamond/api/
├── base/
│   └── BaseTest.java          # RequestSpec and ResponseSpec setup
├── models/
│   └── Post.java              # POJO for /posts endpoint
├── tests/
│   └── PostsTest.java         # Test cases for /posts endpoint
└── utils/
    └── ResponseValidator.java # Reusable assertion utilities

src/test/resources/
└── testng.xml                 # Test suite configuration

.github/workflows/
└── api-tests.yml              # GitHub Actions CI pipeline
```

## Test Coverage

The `PostsTest` suite covers the following patterns against the [JSONPlaceholder](https://jsonplaceholder.typicode.com) public API:

- **GET** — retrieve all resources, retrieve by ID, filter by query parameter
- **POST** — create a new resource with a POJO request body
- **PUT** — update an existing resource
- **DELETE** — delete a resource
- **Chained requests** — extract a value from one response and use it in a subsequent request
- **Response time validation** — verify API responds within an acceptable threshold
- **Negative testing** — verify correct error response for invalid resource ID

## Running the Tests

**Prerequisites:** Java 11+, Maven 3.6+

```bash
# Run the full suite
mvn test

# Run a single test class
mvn test -Dtest=PostsTest

# Run a single test method
mvn test -Dtest=PostsTest#getPostById_ValidId_Returns200WithCorrectData
```

## CI/CD Integration

The suite runs automatically on push and pull request via GitHub Actions. TestNG reports are uploaded as build artifacts on every run.

## Technologies

- **REST Assured 5.4.0** — fluent Java DSL for API test automation
- **TestNG 7.9.0** — test execution, assertions, and suite organization
- **Jackson 2.17.0** — JSON serialization and deserialization
- **Maven** — dependency management and build execution
- **GitHub Actions** — CI/CD pipeline
