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
    .withNullable(field("pets"))
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

See the [User Guide](https://www.instancio.org/user-guide) for more information.

# Try it out

To use Instancio with JUnit 5 use the  `instancio-junit` dependency. It includes `InstancioExtension`
which allows reproducing data in case of a test failure, as well as a few other things.

```xml
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-junit</artifactId>
        <version>1.3.4</version>
        <scope>test</scope>
    </dependency>
```

To use Instancio with JUnit 4, TestNG, or standalone, use `instancio-core`:

```xml
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-core</artifactId>
        <version>1.3.4</version>
        <scope>test</scope>
    </dependency>
```
