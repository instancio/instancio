![logo](https://i.imgur.com/m3KJW6g.png "instancio-logo")
 [![Maven Central](https://img.shields.io/maven-central/v/org.instancio/instancio-core.svg)](https://search.maven.org/artifact/org.instancio/instancio-core/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=instancio_instancio&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=instancio_instancio)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=instancio_instancio&metric=coverage)](https://sonarcloud.io/summary/new_code?id=instancio_instancio)

---

- [What is it?](#what-is-it)
- [Try it out](#try-it-out)
- [Instancio API](#instancio-api)
    - [`create()`](#create)
    - [Bindings](#bindings)
    - [`supply()`](#supply)
    - [`generate()`](#generate)
    - [`onComplete()`](#oncomplete)
    - [`ignore()`](#ignore)
    - [`withNullable()`](#withnullable)
    - [`map()`](#map)
    - [Creating generic classes](#creating-generic-classes)
    - [Using Models](#using-models)
    - [`withSettings()`](#withsettings)
- [JUnit 5 integration](#junit5-integration)
-----

# What is it?

Instancio is a Java library for auto-populating objects with random data.
Its main goal is to reduce the amount of manual data setup in unit tests.

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

This returns a fully-populated person, including nested objects and collections. Instancio will set all fields
to random values. By default, values will be:

- non-null
- non-blank strings
- numbers greater than zero
- collections/maps/arrays will have at least one element

The out-of-the-box defaults can be customised through a properties file or at runtime.

# Try it out

Instancio with JUnit 5 integration:

```xml
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-junit</artifactId>
        <version>1.1.0</version>
        <scope>test</scope>
    </dependency>
```

Instancio standalone:

```xml
    <dependency>
        <groupId>org.instancio</groupId>
        <artifactId>instancio-core</artifactId>
        <version>1.1.0</version>
        <scope>test</scope>
    </dependency>
```

# Instancio API

The following is an overview of the API and its features for creating objects and customising generated data.

- [`create()`](#create) - create an instance from a Class.
- [Bindings](#bindings) - for targeting which values to customise, either by field or class.
- [`supply()`](#supply) - for supplying custom (non-random) values.
- [`generate()`](#generate) - for customising the generated random data, such as number ranges, string lengths, collection sizes, etc.
- [`onComplete()`](#oncomplete) - for updating a generated object via a callback.
- [`ignore()`](#ignore) - for ignoring certain fields or types.
- [`withNullable()`](#withnullable) - for allowing random null values for certain fields or types.
- [`map()`](#map) - for mapping a superclass to a specific subclass.
- [Creating generic classes](#creating-generic-classes) - for creating instances of generic classes.
- [Using Models](#using-models) - for encapsulating generation parameters in reusable models.
- [`withSettings()`](#withsettings) - for specifying custom settings at runtime.



## Create

Instancio offers two ways of creating an instance from a class: the short form and the builder API.

The short form can be used when you don't need to customise generated data:

```java
Person person = Instancio.create(Person.class);
```

The builder API allows tweaking generated data by field or class:

```java
Person person = Instancio.of(Person.class)
    .supply(field("lastUpdated"), () -> LocalDateTime.now())
    .create();
```

## Bindings

In order to customise generated values Instancio uses `Bindings`. Bindings are used to target fields and classes.

To illustrate how bindings work, we will use the `ignore()` method, which tells Instancio to ignore a field or class.

For example, to specify that all `LocalDate` fields should be ignored:

```java
Person person = Instancio.of(Person.class)
    .ignore(all(LocalDate.class))
    .create();
```

To specify that only `Person.dateOfBirth` field should be ignored:

```java
Person person = Instancio.of(Person.class)
    .ignore(field("dateOfBirth"))
    .create();
```

If the `Person` class contains a `List<Address> addresses`, we can specify that
`Address.city` field should be ignored as follows:

```java
Person person = Instancio.of(Person.class)
    .ignore(field(Address.class,"city")) // ignore city for all instances of Address
    .create();
```

If we specify `ignore(all(Address.class))`, then `List<Address> addresses` field will be an empty list.

We can also combine several bindings using `Bindings.of(Binding...)` method:

```java
    Person person = Instancio.of(Person.class)
        .ignore(Bindings.of(field("dateOfBirth"), field(Address.class,"city"), all(LocalDate.class)))
        .create();
```

To summarise, the `Bindings` class contains static methods for targeting:

- `field("someField")` - target field of the class being created
- `field(Foo.class, "someField")` - target `someField` of `Foo` class
- `all(Bar.class)` - target all fields of type `Bar`
- `of(Binding...)` - convenience method for targeting several bindings at once

In addition, the `Bindings` class also contains convenience methods for targeting core types such as strings and numeric
types, for example `allStrings()`, which is a shorthand for `all(String.class)`.

Note that the `allXxx()` methods such as `allInts()` and `allBooleans()`, are convenience methods for targeting both,
the primitive and wrapper types. For example, `allInts()` is a composite binding defined
as `Binding.of(all(int.class), all(Integer.class))`.

## supply()

The `supply(Binding, Generator)` method allows setting custom non-random values.

- The `Binding` argument is used to target a field or class
- The `Generator` argument is functional interface similar to the JDK's `Supplier`
  and can be passed in as a lambda function.

```java
Person person = Instancio.of(Person.class)
    .supply(field("name"), () -> "Homer Simpson")
    .supply(field(Phone.class, "countryCode"), () -> "+1")
    .create();
 ```

This will set `Person.name` to `"Homer Simpson"` and all instances of `Phone` will have `countryCode` set to `"+1"`.

The `supply()` method can also be used to pass instances of complex types.

```java
Person person = Instancio.of(Person.class)
    .supply(field("address"), () -> new Address("742 Evergreen Terrace", "Springfield", "US"))
    .create();
 ```

Instancio will not modify the `supply()` parameters in any way. For instance if the `Address`
class had additional fields they will not be populated.

## `generate()`

The `generate()` method allows customising generated random values generated.

```java
Person person = Instancio.of(Person.class)
    .generate(field("age"), gen -> gen.ints.min(18).max(65))
    .create();
 ```

The `gen` variable provides access to built-in generators for tweaking the values of most commonly
used types, such as numbers, strings, and collections.

```java
Person person = Instancio.of(Person.class)
    .generate(field(Address.class, "phoneNumbers"), gen -> gen.collection().minSize(0).maxSize(3))
    .generate(field(Address.class, "city"), gen -> gen.oneOf("Burnaby", "Vancouver", "Richmond"))
    .create();
```

Will generate a `List<Phone>` with a random size, up to max size of 3, and set the city to `oneOf`
the given values.

## `onComplete()`

The `onComplete()` method allows to modifing generated instances via a callback.
This method only makes sense for mutable types. For example, the following
will update all instances of `Phone` class `countryCode` to `"+1"` after
`Phone` class has been fully popoulated.


```java
Person person = Instancio.of(Person.class)
    .onComplete(all(Phone.class), (Phone phone) -> phone.setCountryCode("+1"))
    .create();
```

The functionality of `onComplete()` is similar to the `supply()` method but there are some
differences that can make one preferable over the other:

- `supply()` method
    - can only accept a single value
    - if the argument is a complex type, it will assign the object as is
      (e.g. it will not auto-populate any fields)

- `onComplete()` method
    - takes a callback that is invoked after the object has been fully populated
    - the callback can be used to update multiple fields at once
    - can only be used to update mutable types

## `ignore()`

The `ignore()` method tells Instancio to ignore a field or class. Instancio will not generate values for
ignored bindings. This can be useful when:

- we want to ignore an irrelevant complex type with a lot of data
- preserve value of fields  pre-initialised with a default value

```java
Person person = Instancio.of(Person.class)
    .ignore(all(Address.class))
    .ignore(field("gender"))
    .create();
```

## `withNullable()`

By default, Instancio generates only non-null values. `withNullable()` allows
overriding this behaviour. For example, if we want to test whether our code
handles `null` lists:

```java
Person person = Instancio.of(Person.class)
    .withNullable(all(List.class))
    .create();
```

If `withNullable()` is bound to a primitive type, then it is ignored. For example, in the following snippet nullable
is specified for `allLongs()`:

```java
Person person = Instancio.of(Person.class)
    .withNullable(allLongs())
    .create();
```

`allLongs()` includes `Long` wrapper and `long` primitives. Therefore, `Long` wrapper will
be nullable, however 'nullability' will be ignored for `long` primitives.

## `map()`

The `map()` method allows mapping a supertype to a subtype. For example,
if our `Person` class has a field `List<Pet> pets` where `Pet` is an
interface, Instancio will not know which implementation to use.
In this case, you can specify the type of `Pet` using the map method:

```java
Person person = Instancio.of(Person.class)
    .map(Pet.class, Cat.class)
    .create();
```

## Creating generic classes

Instancio offers two ways of creating generic classes.

First (and preferred) approach is using a type token (same idea as Guava's `TypeToken` or Jackson's `TypeReference`):

```java
Map<String, Person> map = Instancio.create(new TypeToken<Map<String, Person>>() {});

// also supports the builder API
Map<String, Person> map = Instancio.of(new TypeToken<Map<String, Person>>() {}).create();
```

The second approach is by specifying type parameters explicitly:


```java
Map<String, Person> map = Instancio.of(Map.class)
    .withTypeParameters(String.class, Person.class)
    .create();
```

Note that using the second approach will generate an "unchecked assignment"
warning. In addition, it cannot handle nested generics such
as `Map<Foo, Pair<Bar, Baz>>`. For these reasons, using type tokens should
be preferable in most cases.


## Using Models

Models are basically templates for creating objects and can be created
using the `toModel()` method. For example, we can create a model of
for the Simpson's household:

```java
Model<Person> simpsonsModel = Instancio.of(Person.class)
        .supply(field("address"), () -> new Address("742 Evergreen Terrace", "Springfield", "US"))
        .supply(field("pets"), () -> List.of(
                     new Pet(PetType.CAT, "Snowball"),
                     new Pet(PetType.DOG, "Santa's Little Helper"))
        .toModel();
```

Now that we have a model, we can use it to create persons without duplicating the generation parameters

```java
Person homer = Instancio.of(simpsonsModel)
    .supply(field("name"), () -> "Homer")
    .create();

Person marge = Instancio.of(simpsonsModel)
    .supply(field("name"), () -> "Marge")
    .create();
```

A model can also used to create another model. This snippet shows how to create a new model to include a new pet.

```java
Model<Person> withNewPet = Instancio.of(simpsonsModel)
    .supply(field("pets"), () -> List.of(
                new Pet(PetType.PIG, "Plopper"),
                new Pet(PetType.CAT, "Snowball"),
                new Pet(PetType.DOG, "Santa's Little Helper"))
    .toModel();
```

## `withSettings()`

Instancio offers a settings API for overriding default settings. For example,
by default Instancio generates only positive numbers. One way to override
this behaviour is by using settings:

```java
Settings settings = Settings.create()
    .set(Setting.LONG_MIN, Long.MIN_VALUE)
    .set(Setting.INTEGER_MIN, Integer.MIN_VALUE);

Person person = Instancio.of(Person.class)
    .withSettings(settings)
    .create();
```

The `org.instancio.settings.Setting` enum contains all the setting keys.

# JUnit 5 integration

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

