---
hide:
  - navigation
  - toc
---

# Getting Started

**Requirements**:

Instancio is packaged as a multi-release JAR and can be used with **Java 8** or higher.

Since version `1.5.0` supports creating

- `java.lang.Record` classes on Java 16
- `sealed` classes on Java 17

## Dependencies

There are three dependencies available from Maven central:


| Dependency            | Module Name               | Description |
| --------------------- | ------------------------- |------------ |
| `instancio-core`      | `org.instancio.core`      | Core library |
| `instancio-junit`     | `org.instancio.junit`     | JUnit Jupiter integration |
| `instancio-processor` | `org.instancio.processor` | Annotation processor for generating metamodels |


### **`instancio-junit`**

If you use JUnit Jupiter, then use `instancio-junit`.
Since `instancio-junit` has a transitive dependency on `instancio-core`, it is not necessary to import both.
Importing only `instancio-junit` one would suffice.


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

`instancio-core` itself has minimal `compile` dependencies:

- `org.slf4j:slf4j-api`
- `org.objenesis:objenesis`

### **`instancio-processor`**

The annotation processor generates metamodels, which can be used to avoid referencing fields by their names. The annotation processor can be enabled as shown below. Please refer to the [Metamodel](user-guide.md#metamodel) section of the user guide for examples.

=== "Maven"
    ``` xml linenums="1" title="Maven"
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>${your.java.version}</source>
                    <target>${your.java.version}</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.instancio</groupId>
                            <artifactId>instancio-processor</artifactId>
                            <version>{{config.latest_release}}</version>
                        </path>
                        <!-- include other processors, if any -->
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
    ```
=== "Gradle (4.6 or higher)"
    ``` groovy linenums="1" title="Gradle"
    dependencies {
        testAnnotationProcessor "org.instancio:instancio-processor:{{config.latest_release}}"
    }
    ```
