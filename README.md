<img src="https://i.imgur.com/937nevX.png" alt="Instancio" width="250"/> [![Maven Central](https://img.shields.io/maven-central/v/org.instancio/instancio-core.svg)](https://search.maven.org/artifact/org.instancio/instancio-core/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=instancio_instancio&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=instancio_instancio)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=instancio_instancio&metric=coverage)](https://sonarcloud.io/summary/new_code?id=instancio_instancio)

---

# What is it?

Instancio is a Java library that automatically creates and populates objects for your unit tests.

Instead of manually setting up test data:

```java
Address address  = new Address();
address.setStreet("street");
address.setCity("city");
//...

Person person = new Person();
person.setFirstName("first-name");
person.setLastName("last-name");
person.setAge(22);
person.setGender(Gender.MALE);
person.setAddress(address);
//...
```

You can simply do the following:

```java
Person person = Instancio.create(Person.class);
```

This one-liner returns a fully-populated person, including nested objects and collections.

The object is populated with random data that can be <b>reproduced</b> in case of test failure.

### What else can Instancio do?

1. Create collections of objects:

```java
List<Person> persons = Instancio.ofList(Person.class).size(10).create();
```

2. Create streams of objects:

```java
Stream<Person> persons = Instancio.stream(Person.class).limit(5);
```

3. Create generic types:

```java
Pair<List<Foo>, List<Bar>> pairOfLists = Instancio.create(new TypeToken<Pair<List<Foo>, List<Bar>>>() {});
```

4. Customise generated values:

```java
Person person = Instancio.of(Person.class)
    .generate(field(Person::getDateOfBirth), gen -> gen.temporal().localDate().past())
    .generate(field(Phone::getAreaCode), gen -> gen.oneOf("604", "778"))
    .generate(field(Phone::getNumber), gen -> gen.text().pattern("#d#d#d-#d#d-#d#d"))
    .subtype(all(AbstractAddress.class), AddressImpl.class)
    .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
    .onComplete(all(Person.class), (Person p) -> p.setName(p.getGender() == Gender.MALE ? "John" : "Jane"))
    .create();
```

5. Create reusable templates (Models) of objects:

```java
Model<Person> simpsons = Instancio.of(Person.class)
    .set(field(Person::getLastName), "Simpson")
    .set(field(Address::getCity), "Springfield")
    .generate(field(Person::getAge), gen -> gen.ints().range(40, 50))
    .toModel();

Person homer = Instancio.of(simpsons)
    .set(field(Person::getFirstName), "Homer")
    .create();

Person marge = Instancio.of(simpsons)
    .set(field(Person::getFirstName), "Marge")
    .create();
```

6. Use data feeds for data defined in external sources (e.g. CSV or JSON):

```java
List<Person> person = Instancio.ofList(Person.class)
    .size(10)
    .applyFeed(all(Person.class), feed -> feed.ofResource("persons.csv"))
    .create();
```

7. Define custom feeds for reuse across tests:

```java
@Feed.Source(resource = "persons.csv")
interface PersonFeed extends Feed {

  @TemplateSpec("${firstName} ${lastName}")
  FeedSpec<String> fullName();

  @FunctionSpec(params = {"fullName", "dateOfBirth"}, provider = BioProvider.class)
  FeedSpec<String> bio();

  class BioProvider implements FunctionProvider {

    String describePerson(String fullName, LocalDate dateOfBirth) {
      int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
      return String.format("%s is %d years old", fullName, age);
    }
  }
}

// Usage:
List<Person> person = Instancio.ofList(Person.class)
    .applyFeed(all(Person.class), feed -> feed.of(PersonFeed.class))
    .create();
```

8. Inject arguments into fields and parameters with JUnit 5 ([video tutorial](https://www.youtube.com/watch?v=H8jT43SQis0)):

```java
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @Given
    private List<String> randomList;

    @Test
    void example1(@Given String randomSting, @Given(SomeCustomProvider.class) customString) {
        //...
    }

    @ValueSource(strings = {"foo", "bar"})
    @ParameterizedTest
    void example2(String fooOrBar, @Given long randomLong) {
        // ...
    }
}
```

9. Run parameterized tests against many samples of generated inputs:

```java
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @InstancioSource(samples = 1000)
    @ParameterizedTest
    void example(UUID randomUuid, Foo randomFoo, @Given(BarProvider.class) Bar customBar) {
        // runs the test method 1000 times against generated inputs
    }
}
```

## Main Features

- Fully reproducible data in case of test failures.
- Support for generics, `record` and `sealed` classes, Java 21 sequenced collections.
- Data generation based on JPA and Bean Validation annotations (Hibernate, jakarta).
- Flexible configuration options and SPIs.
- Support for defining custom generators.
- `InstancioExtension` for Junit 5 `@ExtendWith`.
- Object population via fields and setters.

## Getting Started

- [User guide](https://www.instancio.org/user-guide) (instancio.org)
- [Javadocs](https://javadoc.io/doc/org.instancio/instancio-core/latest/) (javadoc.io)
- [Quickstart](https://github.com/instancio/instancio-quickstart) (github.com) sample Maven project with usage examples

  ```sh
  git clone https://github.com/instancio/instancio-quickstart.git
  ```

# Maven Coordinates

If you have JUnit 5 on the classpath, use the `instancio-junit` dependency.

```xml
<dependency>
    <groupId>org.instancio</groupId>
    <artifactId>instancio-junit</artifactId>
    <version>RELEASE</version>
    <scope>test</scope>
</dependency>
```

To use Instancio with JUnit 4, TestNG, or standalone, use `instancio-core`:

```xml
<dependency>
    <groupId>org.instancio</groupId>
    <artifactId>instancio-core</artifactId>
    <version>RELEASE</version>
    <scope>test</scope>
</dependency>
```

To better manage the different dependencies use `instancio-bom`:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.instancio</groupId>
            <artifactId>instancio-bom</artifactId>
            <version>RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

> [!CAUTION]
> The `org.instancio:instancio` artifact on Maven central is an older dependency that should no longer be used.

# Feedback

Feedback and bug reports are greatly appreciated!

- Please submit an [issue](https://github.com/instancio/instancio/issues) for bug reports and feature requests.
- For general feedback or questions, please create a post in the [Discussions](https://github.com/instancio/instancio/discussions).

Please check the [User Guide](https://www.instancio.org/user-guide), previous issues and discussions before creating a new one.

# Sponsors

If you like Instancio, please consider supporting its maintenance and development
by becoming a [sponsor](https://github.com/sponsors/instancio).

A big thank you to the current project sponsors:

- [@darthblonderss](https://github.com/darthblonderss)
- [@mauravan](https://github.com/mauravan)
- [@mevlana14](https://github.com/mevlana14)

Thanks to [JetBrains](https://jb.gg/OpenSource) and [YourKit](https://www.yourkit.com)
for supporting this project with their open source licenses.

<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.svg" width="150px" alt="JetBrains logo">
<img src="https://www.yourkit.com/images/yklogo.png" width="140px" alt="YourKit logo">

## For Enterprise

Available as part of the Tidelift Subscription

The maintainers of Instancio and thousands of other packages are working with Tidelift to deliver commercial
support and maintenance for the open source dependencies you use to build your applications. Save time, reduce risk,
and improve code health, while paying the maintainers of the exact dependencies you use.
[Learn more.](https://tidelift.com/subscription/pkg/maven-org.instancio.instancio-core?utm_source=maven-org.instancio.instancio-core&utm_medium=referral&utm_campaign=enterprise&utm_term=repo)
