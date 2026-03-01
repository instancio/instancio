---
hide:
  - navigation
  - toc
description: "Get started with Instancio. Install dependencies via Maven or Gradle using instancio-core or instancio-junit."
---

# Getting Started

**Requirements**:

Instancio can be used with **Java 17** or higher.

## Dependencies

The following dependencies are available from Maven central:


| Dependency        | JPMS Module Name      | Description               |
|-------------------|-----------------------|---------------------------|
| `instancio-core`  | `org.instancio.core`  | Core library              |
| `instancio-junit` | `org.instancio.junit` | JUnit Jupiter integration |
| `instancio-guava` | `org.instancio.guava` | Support for Google Guava  |
| `instancio-bom`   | -                     | Bill Of Materials         |


!!! danger "The `org.instancio:instancio` artifact on Maven central is an older dependency that should no longer be used."

### **`instancio-junit`**

Use the `instancio-junit` dependency that matches your JUnit version:

- For JUnit 5, use `instancio-junit:5.x`
- For JUnit 6, use `instancio-junit:6.x`

It includes a transitive dependency on `instancio-core`, therefore it is not necessary to import both.


=== "Maven"
    ```xml linenums="1" title="Maven"
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-junit</artifactId>
        <version>{{config.latest_release}}</version>
        <scope>test</scope>
    </dependency>
    ```
=== "Gradle"
    ```groovy linenums="1" title="Gradle"
    dependencies {
        testImplementation 'org.instancio:instancio-junit:{{config.latest_release}}'
    }
    ```

### **`instancio-core`**

If you use JUnit 4, TestNG, or would like to use Instancio standalone, then use `instancio-core`:

=== "Maven"
    ```xml linenums="1" title="Maven"
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-core</artifactId>
        <version>{{config.latest_release}}</version>
        <scope>test</scope>
    </dependency>
    ```
=== "Gradle"
    ```groovy linenums="1" title="Gradle"
    dependencies {
        testImplementation 'org.instancio:instancio-core:{{config.latest_release}}'
    }
    ```

### **`instancio-guava`**

Using `instancio-guava` requires the following dependencies on the classpath:

- either `instancio-core` or `instancio-junit`
- `com.google.guava:guava` version `23.1-jre` or higher

### **`instancio-bom`**

Use `instancio-bom` to easily manage Instancio dependencies:

=== "Maven"
```xml linenums="1" title="Maven"
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.instancio</groupId>
            <artifactId>instancio-bom</artifactId>
            <version>{{config.latest_release}}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
<dependencies>
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-core</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-junit</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-guava</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```
=== "Gradle"
```groovy linenums="1" title="Gradle"
dependencies {
    implementation platform('org.instancio:instancio-bom:{{config.latest_release}}')
    testImplementation 'org.instancio:instancio-core'
    testImplementation 'org.instancio:instancio-junit'
    testImplementation 'org.instancio:instancio-guava'
}
```

### Versioning

Instancio version numbers adhere to the `MAJOR.MINOR.PATCH` format.

- Major versions are generally reserved for significant features and breaking changes.
- Minor and Patch versions include bug fixes, smaller new features,
  but may also include minor breaking changes.

In other words, the project does **not** follow [Semantic Versioning](https://semver.org/),
though the versioning scheme loosely resembles it.
