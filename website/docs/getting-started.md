---
hide:
  - navigation
  - toc
---

# Getting Started

**Requirements**:

Instancio is packaged as a multi-release JAR and can be used with **Java 8** or higher.

It has a single `compile` dependency on `org.slf4j:slf4j-api`.

Since version `1.5.0` Instancio supports creating:

- `java.lang.Record` classes on Java 16
- `sealed` classes on Java 17

## Dependencies

The following dependencies are available from Maven central:


| Dependency              | JPMS Module Name            | Description                        |
|-------------------------|-----------------------------|------------------------------------|
| `instancio-core`        | `org.instancio.core`        | Core library                       |
| `instancio-junit`       | `org.instancio.junit`       | JUnit Jupiter integration          |
| `instancio-guava`       | `org.instancio.guava`       | Support for Google Guava           |
| `instancio-quickcheck`  | `org.instancio.quickcheck`  | Support for property-based testing |


!!! warning "The `org.instancio:instancio` artifact on Maven central is an older dependency that should no longer be used."

### **`instancio-junit`**

If you have JUnit 5 on the classpath, then use `instancio-junit`.

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

### **`instancio-quickcheck`**

Using `instancio-quickcheck` requires the following dependencies on the classpath:

- either `instancio-core` or `instancio-junit`
- JUnit 5 version `5.10.1` or above

!!! note
    Since `instancio-quickcheck` implements a JUnit 5 engine, it relies on a number of JUnit APIs.
    For this reason, it is only guaranteed to work with the JUnit version declared in the project's
    [pom.xml](https://github.com/instancio/instancio/blob/main/pom.xml).
    Support for older versions of Junit 5 is on a best-effort basis and is not guaranteed.
