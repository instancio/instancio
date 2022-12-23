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
The object is populated with random data that can be reproduced in case of test failure.

If you require specific values, generated data can be customised using the builder API.
The following example is a little over the top just to show some of the API capabilities.

```java
Person person = Instancio.of(Person.class)
    .generate(field("age"), gen -> gen.ints().range(18, 65))
    .generate(field(Phone.class, "areaCode"), gen -> gen.oneOf("604", "778"))
    .generate(field(Phone.class, "number"), gen -> gen.text().pattern("#d#d#d-#d#d-#d#d"))
    .generate(all(List.class).within(scope(Address.class)), gen -> gen.collection().size(4))
    .set(field(Address.class, "city"), "Vancouver")
    .ignore(field(Address.class, "postalCode"))
    .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
    .onComplete(all(Person.class), (Person p) -> p.setName(p.getGender() == Gender.MALE ? "John" : "Jane"))
    .create();
```

## Main Features

- Fully reproducible data in case of test failures.
- Support for generics, `record` and `sealed` classes.
- Support for defining custom generators.
- Flexible configuration options.
- `InstancioExtension` for Junit 5 `@ExtendWith`.

## Documentation

- [User guide](https://www.instancio.org/user-guide)
- [Sample projects](https://github.com/instancio/instancio-samples)

# Latest Major Release

Version `2.0.0` is out with several [new features](https://github.com/instancio/instancio/discussions/291#discussioncomment-4446255).

# Getting Started

If you have JUnit 5 on the classpath, use the `instancio-junit` dependency.

```xml
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-junit</artifactId>
        <version>2.0.0</version>
        <scope>test</scope>
    </dependency>
```

To use Instancio with JUnit 4, TestNG, or standalone, use `instancio-core`:

```xml
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-core</artifactId>
        <version>2.0.0</version>
        <scope>test</scope>
    </dependency>
```

# Feedback

Feedback and bug reports are greatly appreciated. Please submit an
[issue](https://github.com/instancio/instancio/issues) to report a bug,
or if you have a question or a suggestion.

# Special thanks to

JetBrains for supporting this project with their [Open Source Licenses](https://www.jetbrains.com/opensource).

<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg" width="150px">


