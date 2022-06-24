<img src="https://i.imgur.com/937nevX.png" alt="Instancio" width="250"/> [![Maven Central](https://img.shields.io/maven-central/v/org.instancio/instancio-core.svg)](https://search.maven.org/artifact/org.instancio/instancio-core/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=instancio_instancio&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=instancio_instancio)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=instancio_instancio&metric=coverage)](https://sonarcloud.io/summary/new_code?id=instancio_instancio)

---

# What is it?

Instancio is a Java library for populating objects.
Its main goal is to reduce manual data setup in unit tests,
especially when working with large classes.

Instead of:

```java
Person person = new Person();
person.setFirstName("test-first-name");
person.setLastName("test-last-name");
person.setDateOfBirth(LocalDate.of(1990,7,30));
// etc...
```

Instancio can do the manual work for you:

```java
Person person = Instancio.create(Person.class);
```

This one-liner returns a fully-populated person, including nested objects and collections.

You can also customise generated values using the builder API:

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

One of possible outputs:

```java
Person[
  name=Jane
  gender=FEMALE
  age=39
  pets=["MSUI", "OSQRCB"]
  address=[
    street=RMRCREFF
    city=Vancouver
    postalCode=null
    country=IFYFKJ
    phoneNumbers=[
      Phone[areaCode=778,number=271-15-75],
      Phone[areaCode=604,number=159-74-61],
      Phone[areaCode=604,number=694-13-82],
      Phone[areaCode=778,number=376-49-29]]]
  lastModified=2022-05-12T22:41:10.356320]
```

# Documentation

- [User Guide](https://www.instancio.org/user-guide)
- [Building from sources](https://www.instancio.org/building/)
- [Sample projects](https://github.com/instancio/instancio-samples)

# Features

- [JUnit 5 extension](https://www.instancio.org/user-guide/#junit-jupiter-integration)
- [Reproducible data using seeds](https://www.instancio.org/user-guide/#seed)
- [Metamodels](https://www.instancio.org/user-guide/#metamodel)
- [Object templates using models](https://www.instancio.org/user-guide/#using-models)


# Try it out

To use Instancio with JUnit 5, use the `instancio-junit` dependency.
It includes `InstancioExtension` which allows reproducing data in case of a test failure,
as well as a few other useful features.

```xml
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-junit</artifactId>
        <version>1.5.1</version>
        <scope>test</scope>
    </dependency>
```

To use Instancio with JUnit 4, TestNG, or standalone, use `instancio-core`:

```xml
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-core</artifactId>
        <version>1.5.1</version>
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


