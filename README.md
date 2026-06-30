# REST Assured API Test Framework

A Java-based API test automation framework built with REST Assured and TestNG, demonstrating enterprise framework design patterns for API validation across multiple APIs with varying authentication schemes. The framework runs in an isolated Docker container — no local Java or Maven installation required.

## Framework Design

The framework is structured around four core design decisions:

**Properties-driven configuration** — Base URI and authentication settings are loaded from properties files at runtime. Each test class specifies its target API by passing a properties file name to `BaseTest`. API keys and tokens are never stored in source code — they are read from environment variables.

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
│   ├── CatApiTest.java            # Test cases for The Cat API /breeds and /images endpoints
│   └── GitHubApiTest.java         # Test cases for GitHub REST API /user, /users, and /repos endpoints
└── utils/
    └── ResponseValidator.java     # Reusable assertion utilities

src/test/resources/
├── jsonplaceholder.properties     # Base URI config for JSONPlaceholder
├── catapi.properties              # Base URI and auth config for The Cat API
├── github.properties              # Base URI and auth config for GitHub REST API
├── schemas/
│   ├── post-schema.json           # JSON schema for single post response
│   └── posts-array-schema.json   # JSON schema for posts array response
└── testng.xml                     # Test suite configuration

.github/workflows/
└── api-tests.yml                  # GitHub Actions CI pipeline
```

## Running the Tests

### Prerequisites

**All execution paths** require two environment variables:

| Variable | Source |
|----------|--------|
| `CAT_API_KEY` | Free key from [thecatapi.com](https://thecatapi.com) |
| `GH_TOKEN` | Personal Access Token from [github.com/settings/tokens](https://github.com/settings/tokens) with `public_repo` and `read:user` scopes |

---

### Docker (no local Java or Maven required)

**Build the image:**

```bash
docker build \
  --build-arg CAT_API_KEY=$CAT_API_KEY \
  --build-arg GH_TOKEN=$GH_TOKEN \
  -t api-portfolio .
```

**Run the full test suite:**

```bash
docker run --rm \
  -e CAT_API_KEY=$CAT_API_KEY \
  -e GH_TOKEN=$GH_TOKEN \
  api-portfolio
```

**Run a single test class:**

```bash
docker run --rm \
  -e CAT_API_KEY=$CAT_API_KEY \
  -e GH_TOKEN=$GH_TOKEN \
  api-portfolio \
  mvn test -Dtest=PostsTest --batch-mode
```

**Mount the target directory to retrieve reports after the run:**

```bash
docker run --rm \
  -e CAT_API_KEY=$CAT_API_KEY \
  -e GH_TOKEN=$GH_TOKEN \
  -v $(pwd)/target:/app/target \
  api-portfolio
```

TestNG HTML and XML reports land in `target/surefire-reports/` on the host after the container exits.

---

### Docker Compose

Set your credentials in the shell, then:

```bash
export CAT_API_KEY=your_key_here
export GH_TOKEN=your_token_here

docker-compose up --build
```

The container exits when the test run completes. Reports are written to `./target/surefire-reports/` via the volume mount configured in `docker-compose.yml`. Exit code is non-zero if any test fails, making it suitable for use in scripts.

To run a subsequent execution without rebuilding:

```bash
docker-compose up
```

---

### Local (Java 17+ and Maven 3.6+ required)

```bash
# Run the full suite
mvn test

# Run a single test class
mvn test -Dtest=PostsTest

# Run a single test method
mvn test -Dtest=PostsTest#getPostById_ValidId_Returns200WithCorrectData
```

---

## CI/CD Integration

The suite runs automatically on push and pull request via GitHub Actions. The workflow builds the Docker image, caches layers between runs using GitHub Actions cache, and executes the full test suite inside the container.

`CAT_API_KEY` and `GH_TOKEN` are stored as GitHub Actions repository secrets and injected at build and run time. TestNG reports are uploaded as build artifacts with a 14-day retention window on every run.

---

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
- Authentication — API key read from environment variable, injected as `x-api-key` header via `RequestSpecBuilder`

### GitHubApiTest — [GitHub REST API](https://docs.github.com/en/rest) (Bearer token authentication)

- GET — authenticated user profile, public user profile, repository list, specific repository details
- Nested field assertions — verify fields within nested JSON objects using JsonPath dot notation
- Groovy JsonPath expressions — locate specific items within arrays using `find` closures
- Negative testing — verify correct error response for invalid user
- Authentication — Personal Access Token read from environment variable, injected as `Authorization: Bearer` header via `RequestSpecBuilder`

---

## Authentication Schemes

| Scheme    | Header                  | Used By         |
|-----------|-------------------------|-----------------|
| `none`    | none                    | JSONPlaceholder |
| `api-key` | `x-api-key`             | The Cat API     |
| `bearer`  | `Authorization: Bearer` | GitHub REST API |

---

## Technologies

- **REST Assured 5.4.0** — fluent Java DSL for API test automation
- **TestNG 7.9.0** — test execution, assertions, data-driven testing, and suite organization
- **Jackson 2.17.0** — JSON serialization and deserialization
- **Docker** — containerized test execution with no local runtime dependency
- **Maven** — dependency management and build execution
- **Java 17** — text blocks, switch expressions, and modern language features
- **GitHub Actions** — CI/CD pipeline with Docker-based test execution and repository secret management
