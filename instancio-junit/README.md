# Instancio JUnit 5 integration

Instancio supports JUnit 5 integration via the `InstancioExtension`.

Since Instancio generates random data on each test run, its usefulness in unit testing would be limited if
a failed test could not be reproduced. `InstancioExtension` exists primarily for this purpose.
It allows you to reproduce data that resulted in a test failure.

For example, if we have the following test class where the `verifyPerson()` method failed:

```java
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @Test
    void verifyPerson() {
        Person person = Instancio.create(Person.class);
        // some test code...
        // ... some assertion fails
    }
}
```

The failed test will report the seed value that was used by the random number generator, for example:

**`"Test method 'verifyPerson' failed with seed: 12345"`**

You can annotate the failed test method with `@Seed(12345)` and it will generate exactly
the same data as in the previous test run:

```java
@Test
@Seed(12345) // will reproduce previously generated data
void verifyPerson() {
    Person person = Instancio.create(Person.class);
    // snip...
}
```

With the `Seed` annotation in place, the data basically becomes static. Therefore, once you've
determined what caused the test failure and resolved it, you can remove the `Seed` annotation
so that new data will be generated on each subsequent test run.

## Custom settings in unit tests

The extension also allows specifying custom `Settings` using the `@WithSettings` annotation.
Annotated settings will be automatically injected into Instancio models.
For example:

```java
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @WithSettings
    private final Settings settings = Settings.create()
        .set(Setting.COLLECTION_MIN_SIZE, 50)
        .set(Setting.COLLECTION_MAX_SIZE, 100);

    @Test
    void someTest() {
        // will use the specified Settings
        Person person = Instancio.create(Person.class);
    }
}
```

At most one `Settings` field can be annotated per test class.

## `@InstancioSource` Arguments Provider

Instancio can also generate arguments for parameterized tests:

```java
@ParameterizedTest
@InstancioSource({UUID.class, Person.class})
void parameterizedExample(UUID uuid, Person person) {
    // provides a random UUID and a fully-populated Person instance
}
```

Parameterized tests also support the `@Seed` and `@WithSettings` annotations.
However, if you use `@WithSettings`, make sure you declare the `Settings` field as static.
This is because JUnit runs parameterized tests in a static context (for the same reason
JUnit 5's `@MethodSource` must be used on a static method).

```java
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @WithSettings
    private static final Settings settings = Settings.create()
        .set(Setting.COLLECTION_MIN_SIZE, 50)
        .set(Setting.COLLECTION_MAX_SIZE, 100);

    @Seed(12345)
    @ParameterizedTest
    @InstancioSource(Person.class)
    void someTest(Person person) {
        // will use the specified Settings (must be declared as a static field)
    }
}
```
