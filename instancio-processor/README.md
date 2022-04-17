# Instancio Annotation Processor

The annotation processor generates metamodel classes for POJOs (similar idea to JPA's metamodel).
This allows referring to fields using metamodel properties instead of field names.
For example, instead of:

```java
Person person = Instancio.of(Person.class)
    .supply(field(Address.class, "city"), () -> "Paris")
    .create();
```

you would write:

```java
Person person = Instancio.of(Person.class)
    .supply(Address_.city, () -> "Paris")
    .create();
```

where `Address_` is an auto-generated metamodel class.

## Setting up Instancio metamodel generation

### Maven

In addition to Instancio library `<dependency>`, add the following to the build plugins section in your `pom.xml`:

```xml
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
                        <version>${instancio.version}</version>
                    </path>
                    <!-- include other processors, if any -->
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Gradle

If you're using Gradle version 4.6 or higher, add the following to your `build.gradle` file
(in addition to Instancio library dependency):

```groovy
dependencies {
 
    // use the processor in production scope
    annotationProcessor "org.instancio:instancio-processor:$instancioVersion"
    //
    // OR
    //
    // use the processor in test scope
    testAnnotationProcessor "org.instancio:instancio-processor:$instancio.version"
}
```

### Generating metamodels

Once the annotation processor is added to your build script, you can generate models
by adding `@InstancioMetaModel` to any class.

For example, if you are using Instancio for unit tests:

```java
@InstancioMetaModel(classes = {Person.class, Address.class})
class ExampleTest {
    @Test
    void verifyPerson() {
        Person person = Instancio.of(Person.class)
            .supply(Person_.name, () -> "Homer Simpson")
            .supply(Address_.city, () -> "Springfield")
            .create();

        // ... snip
    }
}
```

However, it is not recommended duplicating `@InstancioMetaModel(classes = {Person.class, Address.class})`
multiple times. Doing so will trigger metamodels to be generated more than once.

If you require `Person_` and `Address_` metamodels in different classes, then it is better to create a separate
class to hold the annotation:

```java
@InstancioMetaModel(classes = {Person.class, Address.class})
interface MetaModelConfig {
    // can be left blank...
}
```

This will ensure that metamodels are generated only once.
