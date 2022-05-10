<img src="https://i.imgur.com/937nevX.png" alt="Instancio" width="250"/> [![Maven Central](https://img.shields.io/maven-central/v/org.instancio/instancio-core.svg)](https://search.maven.org/artifact/org.instancio/instancio-core/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=instancio_instancio&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=instancio_instancio)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=instancio_instancio&metric=coverage)](https://sonarcloud.io/summary/new_code?id=instancio_instancio)

---

# What is it?

Instancio is a Java library for auto-populating objects with random data.
Its main goal is to reduce the amount of manual data setup in unit tests,
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

This returns a fully-populated person, including nested objects and collections.

See the [User Guide](https://www.instancio.org/user-guide) for more information.


# Try it out

To use Instancio with [JUnit 5 integration](#junit-5-integration):

```xml
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-junit</artifactId>
        <version>1.2.3</version>
        <scope>test</scope>
    </dependency>
```

To use Instancio with JUnit 4, TestNG, or standalone:

```xml
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-core</artifactId>
        <version>1.2.3</version>
        <scope>test</scope>
    </dependency>
```
