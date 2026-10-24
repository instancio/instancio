<img src="https://i.imgur.com/937nevX.png" alt="Instancio" width="140" align="left" hspace="12"/>

[![Maven Central](https://img.shields.io/maven-central/v/org.instancio/instancio-core.svg)](https://central.sonatype.com/artifact/org.instancio/instancio-core)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=instancio_instancio&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=instancio_instancio)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=instancio_instancio&metric=coverage)](https://sonarcloud.io/summary/new_code?id=instancio_instancio)
[![Sponsor](https://img.shields.io/badge/Sponsor-%E2%9D%A4-ea4aaa)](https://github.com/sponsors/instancio)

[User Guide](https://www.instancio.org/user-guide) | [Javadocs](https://javadoc.io/doc/org.instancio/instancio-core/latest/) | [Quickstart project](https://github.com/instancio/instancio-quickstart)

**Sponsored by** <a href="https://cybozu.co.jp/en/company/" target="_blank"><img src="https://www.instancio.org/assets/logo-cybozu.png" height="18" alt="Cybozu" align="absmiddle"/></a><br clear="left"/>

---

# What is it?

Instancio is a Java library that automatically creates and populates objects for your unit tests.

Instead of manually setting up test data:

```java
Person person = new Person();
person.setFirstName("first-name");
person.setAge(22);
person.setAddress(address);
//...
```

You can simply do the following:

```java
Person person = Instancio.create(Person.class);
```

This one-liner returns a fully-populated person, including nested objects and collections.
The object is populated with random data that can be **reproduced** in case of test failure.

## What else can Instancio do?

**Create collections and streams:**

```java
List<Person> persons = Instancio.ofList(Person.class).size(10).create();
Stream<Person> stream = Instancio.stream(Person.class).limit(5);
```

**Customise generated values:**

```java
Person person = Instancio.of(Person.class)
    .generate(field(Person::getDateOfBirth), gen -> gen.temporal().localDate().past())
    .generate(field(Phone::getNumber), gen -> gen.text().pattern("#d#d#d-#d#d-#d#d"))
    .set(field(Person::getLastName), "Simpson")
    .create();
```

**Reuse object templates (Models):**

```java
Model<Person> simpsons = Instancio.of(Person.class)
    .set(field(Person::getLastName), "Simpson")
    .toModel();

Person homer = Instancio.of(simpsons)
    .set(field(Person::getFirstName), "Homer")
    .create();
```

**Inject arguments with JUnit** ([video tutorial](https://www.youtube.com/watch?v=H8jT43SQis0)):

```java
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @Test
    void example(@Given Person person, @Given String name) {
        //...
    }

    @RepeatedTest(100)
    void repeated(@Given UUID id) {
        // runs 100 times with different generated inputs
    }
}
```

# Installation [![Maven Central](https://img.shields.io/maven-central/v/org.instancio/instancio-junit.svg?label=Latest%20version)](https://central.sonatype.com/artifact/org.instancio/instancio-junit)

Add the dependency using the latest version shown in the Maven Central badge above. See the [documentation](https://www.instancio.org/getting-started) for other modules, including the BOM.

**Maven**

```xml
<dependency>
    <groupId>org.instancio</groupId>
    <artifactId>instancio-junit</artifactId>
    <version>LATEST_VERSION</version>
    <scope>test</scope>
</dependency>
```

**Gradle**

```kotlin
testImplementation("org.instancio:instancio-junit:LATEST_VERSION")
```

# Sponsors

Find Instancio useful, or use it in a commercial product? Please consider supporting its maintenance, or ask your company to, via [GitHub Sponsors](https://github.com/sponsors/instancio) or [Open Collective](https://opencollective.com/instancio).

<a href="https://cybozu.co.jp/en/company/" target="_blank"><img src="https://www.instancio.org/assets/logo-cybozu.png" width="240" alt="Cybozu"/></a>

Thanks to [JetBrains](https://www.jetbrains.com/?from=Instancio) for supporting Instancio with their open source licenses.

**Backers**

- [@mevlana14](https://github.com/mevlana14)

[![Backers](https://opencollective.com/instancio/tiers/backer.svg?avatarHeight=60&width=890&button=false)](https://opencollective.com/instancio)

[![Sponsors](https://opencollective.com/instancio/tiers/sponsor.svg?avatarHeight=80&width=890)](https://opencollective.com/instancio)

**For Enterprise**

Available as part of the Tidelift Subscription.

The maintainers of Instancio and thousands of other packages are working with Tidelift to deliver commercial
support and maintenance for the open source dependencies you use to build your applications. Save time, reduce risk,
and improve code health, while paying the maintainers of the exact dependencies you use.
[Learn more.](https://tidelift.com/subscription/pkg/maven-org.instancio.instancio-core?utm_source=maven-org.instancio.instancio-core&utm_medium=referral&utm_campaign=enterprise&utm_term=repo)

# Feedback

Feedback and bug reports are greatly appreciated!

- Please submit an [issue](https://github.com/instancio/instancio/issues) for bug reports and feature requests.
- For general feedback or questions, please create a post in the [Discussions](https://github.com/instancio/instancio/discussions).

