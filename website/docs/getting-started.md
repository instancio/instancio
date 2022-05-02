---
hide:
  - navigation
  - toc
---

# Getting Started

**Requirements**: Java 8 or higher.

If you use JUnit 5, then use `instancio-junit`:


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

## With the Annotation Processor

Enable the annotation processor for metamodel support. Refer to the [Metamodel](user-guide.md#metamodel) section of the user guide for examples.

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


## Dependency Information

Instancio consists of the following three modules:

- `instancio-core` - the core library
- `instancio-junit` - JUnit 5 integration
- `instancio-processor` - annotation processor for generating metamodels

Since `instancio-junit` has a transitive dependency on the core library, it is not necessary to import both.
Importing only one would suffice.

`instancio-processor` is only needed if you would like to generate [metamodels](user-guide.md#metamodel) to avoid referencing fields by their names.

`instancio-core` itself has only two dependencies:

- `org.slf4j:slf4j-api`
- `org.objenesis:objenesis`

