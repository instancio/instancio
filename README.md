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

## Main Features

- Fully reproducible data in case of test failures.
- Support for generics, `record` and `sealed` classes, Java 21 sequenced collections.
- Support for defining custom generators.
- Support for generating data based on Bean Validation annotations.
- Flexible configuration options.
- `InstancioExtension` for Junit 5 `@ExtendWith`.
- Support for Guava via `instancio-guava` module (experimental)
- Support for property-based testing via `instancio-quickcheck` module (experimental)

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
    <version>LATEST</version>
    <scope>test</scope>
</dependency>
```

To use Instancio with JUnit 4, TestNG, or standalone, use `instancio-core`:

```xml
<dependency>
    <groupId>org.instancio</groupId>
    <artifactId>instancio-core</artifactId>
    <version>LATEST</version>
    <scope>test</scope>
</dependency>
```

# Feedback

Feedback and bug reports are greatly appreciated!

- Please submit an [issue](https://github.com/instancio/instancio/issues) for bug reports and feature requests.
- For general feedback or questions, please create a post in the [Discussions](https://github.com/instancio/instancio/discussions).

Please check the [User Guide](https://www.instancio.org/user-guide), previous issues and discussions before creating a new one.

# Third-party Extensions

- [instancio-jpa](https://github.com/Mobe91/instancio-jpa) created by [Moritz Becker](https://github.com/Mobe91)

> Instancio-jpa is an extension on top of the Instancio library that enables the creation and population
> of JPA entities including the subsequent persistence of these entities using JPA for the purpose of test
> data generation in integration tests.

# Sponsors

If you like Instancio, please consider supporting its maintenance and development
by becoming a [sponsor](https://github.com/sponsors/armandino).

A big thank you to the current project sponsors:

- [@BjarkeTornager](https://github.com/BjarkeTornager)
- [@darthblonderss](https://github.com/darthblonderss)

Thanks to [JetBrains](https://www.jetbrains.com/opensource) and [YourKit](https://www.yourkit.com)
for supporting this project with their open source licenses.

<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg" width="100px" alt="JetBrains logo">

<img src="https://www.yourkit.com/images/yklogo.png" width="140px" alt="YourKit logo">

## For Enterprise

Available as part of the Tidelift Subscription

The maintainers of Instancio and thousands of other packages are working with Tidelift to deliver commercial
support and maintenance for the open source dependencies you use to build your applications. Save time, reduce risk,
and improve code health, while paying the maintainers of the exact dependencies you use.
[Learn more.](https://tidelift.com/subscription/pkg/maven-org.instancio.instancio-core?utm_source=maven-org.instancio.instancio-core&utm_medium=referral&utm_campaign=enterprise&utm_term=repo)
