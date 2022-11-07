---
hide:
  - navigation
  - toc
---

# Using Instancio with JUnit 5

This article is an introduction to using Instancio extension for JUnit 5. Here we will cover

- using `InstancioExtension` for reproducing failed tests
- injecting settings into test classes
- running tests with custom seed values
- generating data for `@ParameterizedTest`

## Pre-requisites

- You will need to include `instancio-junit` dependency.
- It is assumed you already have JUnit 5 on the classpath.

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

## Why use the Instancio JUnit extension

By default, Instancio tests your code against randomly generated data. Unless you configured it otherwise, each time a test is executed, it is run against a different data set. This brings up the question of how to reproduce a failed test? One of the benefits of the extension is that it reports the seed value that was used to generate the data. Knowing the seed value allows us to reproduce the original data that caused the test to fail.

To get started, we will need to declare the extension in our test class. This is similar to using other test extensions, such as `MockitoExtension`. In fact, they can be used together if both are needed. We will use the following sample test case verifying the conversion of a `Person` to `PersonDTO`.



``` java linenums="1" title="Sample test class"
@ExtendWith(InstancioExtension.class)
class PersonToPersonDTOTest {

    @Test
    void verifyPersonDTO() {
        Person person = Instancio.create(Person.class);
      
        // Method under test
        PersonDTO dto = personMapper.toDto(person);
      
        assertThat(dto.getFirstName()).isEqualTo(person.getFirstName());
        assertThat(dto.getLastName()).isEqualTo(person.getSurname());
        // ... remaining assertions
    }
}
```


If this test fails, Instancio will report the failure as follows:

```
Test method 'verifyPersonDTO' failed with seed: 34567
```

Using the reported seed value `34567`, we can annotate the test method to reproduce the data:


``` java linenums="1" title="Reproducing the data" hl_lines="4"
@ExtendWith(InstancioExtension.class)
class ExampleTest{

    @Seed(34567)
    @Test
    void verifyPersonDTO() {
      // same code as before
    }
}
```
!!! attention ""
    <lnum>4</lnum> Placing the seed annotation as shown above will make the data effectively static.<br/>


Now each time the test is run, it will produce the same data, allowing us to fix the cause of the failure. Once the cause is resolved, the `@Seed` annotation can be removed so that new data will be generated on each subsequent test run. How this works is described in more detail in the user guide, but to summarise, Instancio supplies each test method with a seed value. If the `@Seed` annotation is present, Instancio will use its value; if not, it will generate a random seed.


## Injecting Settings into tests

Another feature provided by the extension is its support for injecting custom settings. Instancio settings are encapsulated by the `Settings` class. This allows overriding various parameters like generated number ranges; array, map, and collection sizes; whether generated values can be null, and so on. For example, by default, Instancio generates

- non-null values
- non-empty collections
- positive numbers

Using `@WithSettings` annotation we can override default behaviour as follows:


``` java linenums="1" title="Injecting settings" hl_lines="4"
@ExtendWith(InstancioExtension.class)
class PersonToPersonDTOTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.COLLECTION_MIN_SIZE, 0)
            .set(Keys.COLLECTION_MAX_SIZE, 5)
            .set(Keys.INTEGER_MIN, Integer.MIN_VALUE)
            .set(Keys.INTEGER_MAX, Integer.MAX_VALUE)
            .set(Keys.STRING_NULLABLE, true);

    @Test
    void verifyPersonDTO() {
        // person will populated using above settings
        Person person = Instancio.create(Person.class);
        // ... snip
    }
}
```

With the above settings in place, Instancio might generate `null` strings, empty collections, and negative integers. The settings will apply to test methods in this test class only. If you need to override settings globally, this can be done by placing instancio.properties file at the root of the classpath.


## Instancio Arguments Source

Last but not least, you can use the `@InstancioSource` annotation with `@ParameterizedTest` methods. JUnit 5 provides `@ParameterizedTest` support via the [`junit-jupiter-params`](https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-params/latest) dependency.


Once you have the dependency on the classpath, you can declare a test method as follows:

``` java linenums="1" title="Parameterized test with a single argument" hl_lines="4-5"
@ExtendWith(InstancioExtension.class)
class PersonToPersonDTOTest {

    @ParameterizedTest
    @InstancioSource(Person.class)
    void singleArgument(Person person) {
        // provides a fully-populated person as an argument
    }
}
```

Instancio will provide a populated instance of the class specified in the annotation. You can specify any number of classes in the annotation. Just remember to declare a method argument for each class in the annotation:


``` java linenums="1" title="Parameterized test with multiple arguments"
@ParameterizedTest
@InstancioSource(String.class, UUID.class, Foo.class)
void multipleArguments(String str, UUID uuid, Foo foo) {
    // any number of arguments can be specified...
}
```

There are a couple of important limitations to using @InstancioSource to be aware of.

First, it cannot provide instances of generic types. For example, there is no way to specify a List<Person>.

Second, you cannot customise the object as you would with the builder API. In other words, there is no way to specify something like this:

``` java linenums="1"
Person person = Instancio.of(Person.class)
    .set(field(Phone.class, "countryCode"), "+1")
    .set(all(LocalDateTime.class), LocalDateTime.now())
    .create();
```

However, in situations where these limitations do not apply, it offers a convenient way of providing data to a test method. From simple values such as Strings and numbers to complex data types.