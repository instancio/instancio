## Introduction

### Overview

Instancio is a Java library for generating test objects.
Its main goal is to reduce the amount of manual data setup in unit tests.
Its API was designed to be as non-intrusive and as concise as possible, while providing enough flexibility to customise generated objects.
Instancio requires no changes to production code, and it can be used out-of-the-box with zero configuration.

### Project Goals

There are several existing libraries for generating realistic test data, such as addresses, first and last names, and so on.
While Instancio also supports this use case, this is not its goal.
The idea behind the project is that most unit tests do not care about the actual values.
They just require the _presence of a value_.
Therefore, the main goal of Instancio is simply to generate fully populated objects with random data, including arrays, collections, nested collections, generic types, and so on.
And it aims to do so with as little code as possible in order to keep the tests concise.

Another goal of Instancio is to make the tests more dynamic.
Since each test run is against random values, the tests become alive.
They cover a wider range of inputs, which might help uncover bugs that may have gone unnoticed with static data.

Finally, Instancio aims to provide reproducible data.
It uses a consistent seed value for each object graph it generates.
Therefore, if a test fails against a given set of inputs, Instancio supports re-generating the same data set in order to reproduce the failed test.


# Instancio API

This section provides an overview of the API for creating and customising objects.

## Creating Objects

The {{Instancio}} class is the entry point to the API.
It provides the following shorthand methods for creating objects.
These can be used when defaults suffice and generated values do not need to be customised.

``` java linenums="1" title="Shorthand methods"
Instancio.create(Class<T> klass)
Instancio.create(TypeTokenSupplier<T> supplier)
Instancio.create(Model<T> 
```

The following builder methods allow chaining additional method calls in order to customise generated values, ignore certain fields, provide custom settings, and so on.

``` java linenums="1" title="Builder API"
Instancio.of(Class<T> klass).create()
Instancio.of(TypeTokenSupplier<T> supplier).create()
Instancio.of(Model<T> model).create()
```

The three arguments accepted by these methods can be used for different purposes.
!!! attention ""
    <lnum>1</lnum> Creates an instance by specifying a class; this method should suffice in most cases.<br/>
    <lnum>2</lnum> This method is for creating instances of generic types by supplying a type token.<br/>
    <lnum>3</lnum> Creates an instance using an Instancio {{Model}}, which acts as a template for creating objects (see [Using Models](#using-models)).

``` java linenums="1" title="Examples of create() methods"
// Create by specifying the class
Person person = Instancio.create(Person.class);

// Using type tokens
Pair<String, Long> pair = Instancio.create(new TypeToken<Pair<String, Long>>() {});

Map<Integer, List<String>> map = Instancio.create(new TypeToken<Map<Integer, List<String>>>() {});

// Create from a model
Model<Person> personModel = Instancio.of(Person.class)
    .ignore(field("age"))
    .toModel();

Person personWithoutAgeAndAddress = Instancio.of(personModel)
    .ignore(field("address"))
    .create();
```

It should be noted that generic types can also be created using the `Instancio.of(Class)` method and specifying the type parameters manually:


``` java linenums="1"
Pair<String,Long> pair = Instancio.of(Pair.class)
    .withTypeParameters(String.class, Long.class)
    .create();
```

However, this approach has a couple of drawbacks: it does not supported nested generics, and its usage will generate an "unchecked assignment" warning.

### Creating a Stream of Objects

Instancio also provides methods for creating a `Stream` of objects.
The `stream()` methods return an infinite stream of distinct fully-populated instances.
Similarly to the `create()` methods, these have a shorthand form if no customisations are needed:

``` java linenums="1" title="Shorthand methods"
Instancio.stream(Class<T> klass)
Instancio.stream(TypeTokenSupplier<T> supplier)
```

as well as the builder API that allows customising generated values:

``` java linenums="1" title="Stream Builder API"
Instancio.of(Class<T> klass).stream()
Instancio.of(TypeTokenSupplier<T> supplier).stream()
```

The following are a couple of examples of using streams.

``` java linenums="1" title="Examples of stream() methods"
List<Person> personList = Instancio.stream(Person.class)
    .limit(3)
    .collect(Collectors.toList());

Map<UUID, Person> personMap = Instancio.of(new TypeToken<Person>() {})
    .ignore(all(field("age"), field("address")))
    .stream()
    .limit(3)
    .collect(Collectors.toMap(Person::getUuid, Function.identity()));
```

## Selectors

Selectors are used to target fields and classes, for example in order to customise generated values.
Selectors are provided by the {{Select}} class which contains the following methods:

``` java linenums="1" title="Static methods for targeting fields and classes"
Select.field(String field)
Select.field(Class<?> declaringClass, String field)
Select.all(Class<?> type)
Select.all(GroupableSelector... selectors)
Select.allStrings()
Select.allInts()
```

!!! attention ""
    <lnum>1</lnum> Selects the specified field of the class being created<br/>
    <lnum>2</lnum> Selects the specified field of the given class<br/>
    <lnum>3</lnum> Selects all fields of the given type<br/>
    <lnum>4</lnum> Convenience method for combining multiple selectors<br/>
    <lnum>5</lnum> Convenience method equivalent to `all(String.class)`<br/>
    <lnum>6</lnum> Convenience method equivalent to `all(all(int.class), all(Integer.class))`

!!! info "The `allXxx()` methods such as `allInts()`, are available for all core types."

The above methods return either an instance of {{Selector}} or {{SelectorGroup}} type. The latter is a container combining multiple {{Selector}}s.
For example, to ignore certain values, we can specify them individually as follows:

``` java linenums="1" title="Examples of using selectors"
Person person = Instancio.of(Person.class)
    .ignore(field("name"))
    .ignore(field(Address.class, "street"))
    .ignore(all(Phone.class))
    .create();
```

or alternatively, we can combine the selectors into a single group:

``` java linenums="1" title="Examples of using a selector group"
Person person = Instancio.of(Person.class)
    .ignore(all(
            field("name"),
            field(Address.class, "street"),
            all(Phone.class)))
    .create();
```

### Selector Scopes

Selectors also offer the `within(Scope... scopes)` method for fine-tuning which targets they should be applied to.
The method accepts one or more `Scope` objects that can be creating using the static methods in the `Select` class.

``` java linenums="1" title="Static methods for specifying selector scopes"
Select.scope(Class<?> targetClass, String field)
Select.scope(Class<?> targetClass)
```
!!! attention ""
    <lnum>1</lnum> Scope a selector to the specified field of the target class<br/>
    <lnum>2</lnum> Scope a selector to the specified class<br/>


To illustrate how scopes work we will assume the following structure for the `Person` class:

``` java linenums="1" title="Sample POJOs with getters and setters omitted"
class Person {
    private String name;
    private Address home;
    private Address work;
}

class Address {
    private String street;
    private String city;
    private List<Phone> phoneNumbers;
}

class Phone {
    private String areaCode;
    private String number;
}
```

To start off, without using scopes we can set all strings to the same value.
For example, the following snippet will set each string field of each class to "foo".

``` java linenums="1" title="Set all strings to &quot;Foo&quot;"
Person person = Instancio.of(Person.class)
    .set(allStrings(), "foo")
    .create();
```

Using `within()` we can narrow down the scope of the `allStrings()` selector. For brevity,
the `Instancio.of(Person.class)` line will be omitted.


``` java title="Set all strings in all Address instances; this includes Phone instances as they are contained within addresses"
set(allStrings().within(scope(Address.class)), "foo")
```

``` java title="Set all strings contained within lists (matches all Phone instances in our example)"
set(allStrings().within(scope(List.class)), "foo")
```

``` java title="Set all strings in Person.home address object"
set(allStrings().within(scope(Person.class, "home")), "foo")
```

``` java title="Set Address.city of the Person.home field"
set(field(Address.class, "city").within(scope(Person.class, "home")), "foo")
```

Using `within()` also allows specifying multiple scopes. Scopes must be specified top-down, starting from the outermost to the innermost.

``` java title="Set all strings of all Phone instances contained within Person.work address field"
set(allStrings().within(scope(Person.class, "work"), scope(Phone.class)), "foo")
```

The `Person.work` address object contains a list of phones, therefore `Person.work` is the outermost scope and is specified first.
`Phone` class is the innermost scope and is specified last.

## Customising Objects

Properties of an object created by Instancio can be customised using

- `generate()`
- `set()`
- `supply()`

methods defined in the {{InstancioApi}} class.


### Using `generate()`

The `generate()` method provides access to built-in generators for core types from the JDK, such strings, numeric types, dates, arrays, collections, and so on.
It allows modifying generation parameters for these types in order to fine-tune the data.
The usage is shown in the following example, where the `gen` parameter (of type {{Generators}}) exposes the available generators to simplify their discovery using IDE auto-completion.

``` java linenums="1" title="Example of using generate()"
Person person = Instancio.of(Person.class)
    .generate(field("age"), gen -> gen.ints().range(18, 65))
    .generate(field("pets"), gen -> gen.array().length(3))
    .generate(field(Phone.class, "number"), gen -> gen.text().pattern("#d#d#d-#d#d-#d#d"))
    .create();
```

Each generator provides methods applicable to the type it generates, for example:

- `gen.string().minLength(3).allowEmpty()`
- `gen.collection().size(5).nullableElements()`
- `gen.localDate().future()`
- `gen.longs().min(Long.MIN_VALUE)`

Below is another example of customising a `Person`.
For instance, if the  `Person` class has a field `List<Phone>`, by default Instancio would use `ArrayList` as the implementation.
Using the collection generator, this can be overridden by specifying the type explicitly:

``` java linenums="1" title="Example: customising a collection"
Person person = Instancio.of(Person.class)
    // Use LinkedList as List implementation
    .generate(field("phoneNumbers"), gen -> gen.collection().minSize(3).type(LinkedList.class))
    // Use random country codes from given choices
    .generate(field(Phone.class, "countryCode"), gen -> gen.oneOf("+33", "+39", "+44", "+49"))
    .create();
```

### Using `set()`

The `set()` method is self-explanatory.
It can be used to set a static value to selected fields or classes, for example:

``` java linenums="1" title="Example of using set()" hl_lines="2 3"
Person person = Instancio.of(Person.class)
    .set(field(Phone.class, "countryCode"), "+1")
    .set(all(LocalDateTime.class), LocalDateTime.now())
    .create();
```

!!! attention ""
    <lnum>2</lnum> Set `countryCode` to "+1" on _all_ generated instances of `Phone` class.<br/>
    <lnum>3</lnum> Set all `LocalDateTime` values to `now`.

### Using `supply()`

The `supply()` method has two variants:

``` java linenums="1"
supply(SelectorGroup selectors, Supplier<V> supplier)
supply(SelectorGroup selectors, Generator<V> generator)
```

!!! attention ""
    <lnum>1</lnum> For supplying *non-random* values using a `java.util.function.Supplier`.<br/>
    <lnum>2</lnum> For supplying *random* values using custom {{Generator}} implementations.

#### Using supply() to provide *non-random* values

The first variant can be used where random values are not appropriate and the generated object needs to have a meaningful state.

``` java linenums="1" title="Example" hl_lines="2 3"
Person person = Instancio.of(Person.class)
    .supply(field(Phone.class, "countryCode"), () -> "+1")
    .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
    .create();
```

!!! attention ""
    <lnum>2</lnum> Set `countryCode` to "+1" for all instances of `Phone`.<br/>
    <lnum>3</lnum> All `LocalDateTime` instances will be distinct objects with the value `now()`.

There is some overlap between the `set()` and `supply()` methods.
For instance, the following two lines will produce identical results:

``` java linenums="1" title="Example"
set(field(Phone.class, "countryCode"), "+1")
supply(field(Phone.class, "countryCode"), () -> "+1")
```

In fact, `set()` is just a convenience method to avoid using `supply()` when the value is constant.
However, the `supply()` method can be used to provide a new instance each time it is called.
For example, the following methods are _not_ identical:

``` java linenums="1" title="Example"
set(all(LocalDateTime.class), LocalDateTime.now())
supply(all(LocalDatime.class), () -> LocalDateTime.now())
```

If the `Person` class has multiple `LocalDateTime` fields, using `set()` will set them all to the same instance, while using `supply()` will set them all to distinct instances.
This difference is even more important if supplying a `Collection`, since sharing a collection instance among multiple objects is usually not desired.

#### Using supply() to provide *random* values

The second variant of the `supply()` method can be used to generate random objects.
This method takes a {{Generator}} as an argument, which is a functional interface with the following signature:

``` java linenums="1"
import org.instancio.Random;

interface Generator<T> {
    T generate(Random random);
}
```

Using the provided {{Random}} instance ensures that Instancio will be able to reproduce the generated object when needed.
The {{Random}} implementation uses a `java.util.Random` internally, but offers a more user-friendly interface and convenience methods not available in the JDK class.

``` java linenums="1" title="Creating a custom Generator"
import org.instancio.Random;

class PhoneGenerator implements Generator<Phone> {

    public Phone generate(Random random) {
        Phone phone = new Phone();
        phone.setCountryCode(random.oneOf("+1", "+52"));
        phone.setNumber(random.digits(7));
        return  phone;
    }
}
```

The custom `PhoneGenerator` can now be passed into the `supply()` method:

``` java linenums="1"
Person person = Instancio.of(Person.class)
    .supply(all(Phone.class), new PhoneGenerator())
    .create();
```

!!! info "Instancio also offers a Service Provider Interface, {{GeneratorProvider}} that can be used to register custom generators."
    This removes the need for manually passing custom generators to the `supply` method as in the above example.
    They will be picked up automatically.


#### `supply()` anti-pattern

Since the `supply()` method provides an instance of {{Random}}, the method can also be used for customising values of core type, such as strings and numbers.
However, the `generate()` method should be preferred in such cases if possible as it provides a better abstraction and would result in more readable code.

``` java linenums="1" title="generate() vs supply()" hl_lines="3 4 9"
Person bad = Instancio.of(Person.class)
    .supply(field("password"), random -> {
        int length = random.intRange(8, 21);
        return random.alphaNumeric(length);
    })
    .create();

Person person = Instancio.of(Person.class)
    .generate(field("password"), gen -> gen.string().alphaNumeric().length(8, 20))
    .create();
```

!!! attention ""
    <lnum>3-4</lnum> Not recommended: using `random` to generate a `String`.<br/>
    <lnum>9</lnum> Better approach: using the built-in string generator.

### Using `onComplete()`

Generated objects can also be customised using the {{OnCompleteCallback}}, a functional interface with the following signature:

``` java linenums="1"
interface OnCompleteCallback<T> {
    void onComplete(T object);
}
```

While the [`supply()`](#using-supply) and [`generate()`](#using-generate) methods allow specifying values during object construction, the `OnCompleteCallback` is used to modify the generated object _after_ it has been fully populated.

The following example shows how the `Address` can be modified using a callback.
If the `Person` has a `List<Address>`, the callback will be invoked for every instance of the `Address` class that was generated.

``` java linenums="1" title="Example: modifying an object via a callback"
Person person = Instancio.of(Person.class)
    .onComplete(all(Address.class), (Address address) -> {
        address.setCity("Vancouver");
        address.setProvince("BC");
        address.setCountry("Canada");
    })
    .create();
```

The advantage of callbacks is that they can be used to update multiple fields at once.
The disadvantage, however, is that they can only be used to update mutable types.

### Ignoring Fields or Classes

By default, Instancio will attempt to populate every non-static field value.
The `ignore` method can be used where this is not desirable:

``` java linenums="1" title="Example: ignoring certain fields and classes"
Person person = Instancio.of(Person.class)
    .ignore(field("pets"))
    .ignore(all(LocalDateTime.class))
    .create();

// Or combining the selectors
Person person = Instancio.of(Person.class)
    .ignore(all(field("pets"), all(LocalDateTime.class)))
    .create();
```

### Nullable Values

By default, Instancio generates non-null values for all fields.
There are cases where this behaviour may need to be relaxed, for example to verify that a piece of code does not fail in the presence of certain `null` values.
There are a few way to specify that values can be nullable.
This can be done using:

- `withNullable` method of the builder API
- generator methods (if a generator supports it)
- {{Settings}}

To specify that something is nullable using the builder API can be done as follows:

``` java linenums="1" title="Example: specifying nullability using the builder API"
Person person = Instancio.of(Person.class)
    .withNullable(field("address"))
    .withNullable(allStrings())
    .create();
```

Some built-in generators also support marking values as nullable.
In addition, Collection, Map, and Array generators allow specifying whether elements, keys or values are nullable.

``` java linenums="1" title="Example: specifying nullability using the collection generator" hl_lines="3 4"
Person person = Instancio.of(Person.class)
    .generate(field("phoneNumbers"), gen -> gen.collection()
            .nullable()
            .nullableElements())
    .create();
```
!!! attention ""
    <lnum>3</lnum> The collection itself is nullable.<br/>
    <lnum>4</lnum> Collection elements are nullable.

Assuming the `Person` class contains a `Map`, nullability can be specified for keys and values:

``` java linenums="1" title="Example: specifying nullability using the map generator"
Person person = Instancio.of(Person.class)
    .generate(all(Map.class), gen -> gen.map().nullableKeys().nullableValues())
    .create();
```

Lastly, nullability can be specified using {{Settings}}, but only for core types, such as strings and numbers:

``` java linenums="1" title="Example: specifying nullability using Settings"
Settings settings = Settings.create()
    .set(Keys.STRING_NULLABLE, true)
    .set(Keys.INTEGER_NULLABLE, true)
    .set(Keys.COLLECTION_NULLABLE, true)
    .set(Keys.COLLECTION_ELEMENTS_NULLABLE, true);

Person person = Instancio.of(Person.class)
    .withSettings(settings)
    .create();
```

## Subtype Mapping

Subtype mapping allows mapping a particular type to its subtype.
This can be useful for specifying a specific implementation for an abstract type.
The mapping can be specified using the `map` method:


``` java linenums="1"
map(SelectorGroup selectors, Class<?> subtype)
```

All the types represented by the selectors must be supertypes of the given `subtype` parameter.

``` java linenums="1" title="Example: subtype mapping" hl_lines="2 3 4"
Person person = Instancio.of(Person.class)
    .map(all(Pet.class), Cat.class)
    .map(all(all(Collection.class), all(Set.class)), TreeSet.class)
    .map(field("address"), AddressImpl.class)
    .create();
```

!!! attention ""
    <lnum>2</lnum> If `Pet` is an abstract type, then without the mapping all `Pet` instances will be `null`
     since Instancio would not be able to resolve the implementation class.<br/>
    <lnum>3</lnum> Multiple types can be mapped as long as the subtype is valid for all of them.<br/>
    <lnum>4</lnum> Assuming `Person` has an `Address` field, where `Address` is the superclass of `AddressImpl`.

## Using Models

A {{Model}} is a template for creating objects which encapsulates all the generation parameters specified using the builder API.
For example, the following model of the Simpson's household can be used to create individual Simpson characters.

``` java linenums="1" title="Example: using a model as a template for creating objects"
Model<Person> simpsonsModel = Instancio.of(Person.class)
        .supply(field("address"), () -> new Address("742 Evergreen Terrace", "Springfield", "US"))
        .supply(field("pets"), () -> List.of(
                     new Pet(PetType.CAT, "Snowball"),
                     new Pet(PetType.DOG, "Santa's Little Helper"))
        .toModel();

Person homer = Instancio.of(simpsonsModel)
    .supply(field("name"), () -> "Homer")
    .create();

Person marge = Instancio.of(simpsonsModel)
    .supply(field("name"), () -> "Marge")
    .create();
```

The `Model` class does not expose any public methods, and its instances are effectively immutable.
However, a model can be used as template for creating other models.
The next example creates a new model that includes a new `Pet`:

``` java linenums="1" title="Example: using a model as a template for creating other models"
Model<Person> modelWithNewPet = Instancio.of(simpsonsModel)
    .supply(field("pets"), () -> List.of(
                new Pet(PetType.PIG, "Plopper"),
                new Pet(PetType.CAT, "Snowball"),
                new Pet(PetType.DOG, "Santa's Little Helper"))
    .toModel();
```

## Seed

Before creating an object, Instancio initialises a random seed value.
This seed value is used internally by the pseudorandom number generator, that is, `java.util.Random`.
Instancio ensures that the same instance of the random number generator is used throughout object creation, from start to finish.
This constraint means that Instancio can reproduce the same object again by using the same seed value.
This feature allows reproducing failed tests (see the section on [reproducing tests with JUnit](#reproducing-failed-tests)).

In addition, Instancio takes care in generating values for classes like `UUID` and `LocalDateTime`, where a minor difference in values can cause an object equality check to fail.
These classes are generated in such a way, that for a given seed value, the generated values will be the same.
To illustrate with an example, we will use the following `SamplePojo` class.

``` java linenums="1" title="Sample POJO"
class SamplePojo {
    private UUID uuid;
    private LocalDateTime localDateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SamplePojo)) return false;
        SamplePojo p = (SamplePojo) o;
        return uuid.equals(p.uuid) && localDateTime.equals(p.localDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, localDateTime);
    }
}
```

By supplying the same seed value, the same object is generated:

``` java linenums="1" title="Generating two SamplePojo instances with the same seed"
final int seed = 123;

SamplePojo pojo1 = Instancio.of(SamplePojo.class)
    .withSeed(seed)
    .create();

SamplePojo pojo2 = Instancio.of(SamplePojo.class)
    .withSeed(seed)
    .create();

assertThat(pojo1).isEqualTo(pojo2);
```

If the objects are printed, both produce the same output:

```
SamplePojo(
  uuid=3bf992ad-1121-36a2-826d-94112bf1d82b,
  localDateTime=2069-10-15T10:28:31.940
)
```

!!! warning "While the generated values are the same, it is not recommended to write assertions using hard-coded values."


# Metamodel

This section expands on the [Selectors](#selectors) section, which described how to target fields.
Instancio uses reflection at field level to populate objects.
The main reason for using fields and not setters is <a hred="https://docs.oracle.com/javase/tutorial/java/generics/erasure.html" target="_blank">type erasure</a>.
It is not possible to determine the generic type of method parameters at runtime.
However, generic type information is available at field level.
In other words:


``` java linenums="1" hl_lines="2 4"
class Example {
    private List<String> values;

    void setList(List<String> values) {
        this.values = values;
    }
}
```
!!! attention ""
    <lnum>2</lnum> At runtime, this will be a `List<String>`.<br/>
    <lnum>4</lnum> This becomes a `List`.

Without knowing the list's generic type, Instancio would not be able to populate the list.
For this reason, it operates at field level.
Using fields, however, has one drawback: they require the use of field names.
To circumvent this problem, Instancio includes an annotation processor that can generate metamodel classes.

The following example shows two selectors for the `city` field of `Address`, one referencing the field by name, and the other using the generated metamodel class:

``` java linenums="1" title="Metamodel example" hl_lines="8"
// Targeting Address "city" field without the metamodel
Person person = Instancio.of(Person.class)
    .generate(field(Address.class, "city"), gen -> gen.oneOf("Paris", "London"))
    .create();

// Targeting 'Address_.city' using the metamodel
Person person = Instancio.of(Person.class)
    .generate(Address_.city, gen -> gen.oneOf("Paris", "London"))
    .create();
```

!!! attention ""
    <lnum>8</lnum> By default, `_` is used as the metamodel class suffix, but this can be customised using `-Ainstancio.suffix` argument.

## Configuring the Annotation Processor

### Maven

To configure the annotation processor with Maven, add the `<annotationProcessorPaths>` element to the build plugins section in your `pom.xml` as shown below.

!!! note
    You still need to have the Instancio library, either `instancio-core` or `instancio-junit`, in your `<dependencies>` (see [Getting Started](getting-started.md)).


``` xml linenums="1" title="Maven"
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>${your.java.version}</source>
                <target>${your.java.version}</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.instancio</groupId>
                        <artifactId>instancio-processor</artifactId>
                        <version>${instancio.version}</version>
                    </path>
                    <!-- include other processors, if any -->
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Gradle

The following can be used with Gradle version 4.6 or higher, add the following to your `build.gradle` file:

``` groovy linenums="1" title="Gradle"
dependencies {
    testAnnotationProcessor "org.instancio:instancio-processor:$instancioVersion"
}
```

## Generating Metamodels

With the annotation processor build configuration in place, metamodels can be generated using the {{InstancioMetamodel}} annotation.
The annotation can be placed on any type, including an interface as shown below.

``` java linenums="1" title="Using @InstancioMetamodel"
@InstancioMetamodel(classes = {Address.class, Person.class})
interface SampleConfig {
    // can be left blank
}
```

!!! warning "It is not recommended declaring the `@InstancioMetamodel` annotation with the same `classes` more than once."
    Doing so will result in metamodels being generated more than once as well.
    For this reason, it is better to have a dedicated class containing the `@InstancioMetamodel` annotation.


# Configuration

Instancio configuration is encapsulated by the {{Settings}} class, a map of keys and corresponding values.
The `Settings` class provides a few static methods for creating settings.

``` java linenums="1" title="Settings static factory methods"
Settings.create()
Settings.defaults()
Settings.from(Map<Object, Object> map)
Settings.from(Settings other)
```
!!! attention ""
    <lnum>1</lnum> Creates a new instance of blank settings.<br/>
    <lnum>2</lnum> Creates a new instance of default settings.<br/>
    <lnum>3</lnum> Creates settings from a `Map` or `java.util.Properties`.<br/>
    <lnum>4</lnum> Creates a copy of `other` settings (a clone operation).

Settings can be overridden programmatically or through a properties file.


!!! info
    To inspect all the keys and default values, simply: `System.out.println(Settings.defaults())`


## Overriding Settings Programmatically

To override programmatically, an instance of `Settings` can be passed in to the builder API:

``` java linenums="1" title="Supplying custom settings" hl_lines="2 4 7"
Settings overrides = Settings.create()
    .set(Keys.COLLECTION_MIN_SIZE, 10)
    .set(Keys.STRING_ALLOW_EMPTY, true)
    .lock();

Person person = Instancio.of(Person.class)
    .withSettings(overrides)
    .create();
```

!!! attention ""
    <lnum>2</lnum> The {{Keys}} class provides static fields for all the keys supported by Instancio.<br/>
    <lnum>4</lnum> The `lock()` method makes the settings instance immutable. This is an optional method call.
    It can be used to prevent modifications if settings are shared across multiple methods or classes.<br/>
    <lnum>7</lnum> The passed in settings instance will override default settings.

## Overriding Settings Using a Properties File

The {{Keys}} class defines a _property key_ for every key object, for example:

- `Keys.COLLECTION_MIN_SIZE` -> `"collection.min.size"`
- `Keys.STRING_ALLOW_EMPTY`  -> `"string.allow.empty"`

Using these property keys, configuration values can also be overridden via a properties file.
This can be done by placing `instancio.properties` at the root of the classpath and using property keys to override values (see the [sample properties file](#listing-of-all-supported-property-keys)).

## Settings Precedence

Instancio layers settings on top of each other, each layer overriding the previous ones.
This is done in the following order:

1. `Settings.defaults()`
1. Settings from `instancio.properties`
1. Settings injected into a JUnit test using `@WithSettings` annotation (see [Settings Injection](#settings-injection))
1. Settings supplied to the builder API's `withSettings(Settings)` method

Therefore, settings supplied manually take precedence over everything else.

## Listing of all Supported Property Keys

``` java linenums="1" title="Sample configuration properties" hl_lines="1 4 10 26 27 38"
array.elements.nullable=false
array.max.length=6
array.min.length=2
array.nullable=false
boolean.nullable=false
byte.max=127
byte.min=1
byte.nullable=false
character.nullable=false
collection.elements.nullable=false
collection.max.size=6
collection.min.size=2
collection.nullable=false
double.max=10000
double.min=1
double.nullable=false
float.max=10000
float.min=1
float.nullable=false
integer.max=10000
integer.min=1
integer.nullable=false
long.max=10000
long.min=1
long.nullable=false
map.keys.nullable=false
map.values.nullable=false
map.max.size=6
map.min.size=2
map.nullable=false
short.max=10000
short.min=1
short.nullable=false
string.allow.empty=false
string.max.length=9
string.min.length=3
string.nullable=false
type.mapping.java.util.Collection=java.util.ArrayList
type.mapping.java.util.List=java.util.ArrayList
type.mapping.java.util.Map=java.util.HashMap
type.mapping.java.util.SortedMap=java.util.TreeMap
```

!!! attention ""
    <lnum>1</lnum>The `*.elements.nullable`, `map.keys.nullable`, `map.values.nullable` specify whether Instancio can generate `null` values for array/collection elements and map keys and values.<br/>
    <lnum>4</lnum> The other `*.nullable` properties specifies whether Instancio can generate `null` values for a given type.<br/>
    <lnum>38</lnum> Properties prefixed with `type.mapping` are used to specify default implementations for abstract types, or map types to subtypes in general.
    This is the same mechanism as [subtype mapping](#subtype-mapping), but configured via properties.

# JUnit Integration

Instancio supports JUnit 5 via the {{InstancioExtension}} and can be used in combination with extensions from other testing frameworks.
The extension adds a few useful features, such as

- the ability to use {{InstancioSource}} with `@ParameterizedTest` methods,
- injection of custom settings using {{WithSettings}},
- and most importantly support for reproducing failed tests using the {{Seed}} annotation.

## Reproducing Failed Tests

Since using Instancio validates your code against random inputs on each test run, having the ability to reproduce a failed tests with previously generated data becomes a necessity.
Instancio supports this use case by reporting the seed value of a failed test in the failure message using JUnit's `publishReportEntry` mechanism.

### Seed Lifecycle in a JUnit Test

Instancio initialises a seed value before each test method.
This seed value is used for creating all objects during the test method's execution, unless another seed is specified explicitly using the {{withSeed}} method.

``` java linenums="1" title="Seed Lifecycle in a JUnit Test" hl_lines="5 7 10 13 15"
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @Test
    void example() {

        Person person1 = Instancio.create(Person.class);

        Person person2 = Instancio.of(Person.class)
            .withSeed(123)
            .create();

        Person person3 = Instancio.create(Person.class);

    }
}
```
!!! attention ""
    <lnum>5</lnum> Instancio initialises a random seed value, for example `8276`.<br/>
    <lnum>7</lnum> Uses seed value `8276`.<br/>
    <lnum>10</lnum> Uses the supplied seed value `123`.<br/>
    <lnum>13</lnum> Uses seed value `8276`.<br/>
    <lnum>15</lnum> Seed value `8276` goes out of scope.

!!! note
    Even though `person1` and `person3` are created using the same seed value of `8276`, they are actually distinct objects, each containing different values. This is because the same instance of the random number generator is used througout the test method.

### Test Failure Reporting

When a test method fails, Instancio adds a message containing the seed value to the failed test's output.
Using the following failing test as an example:

``` java linenums="1" title="Test failure example"
@Test
void verifyShippingAddress() {
    Person person = Instancio.create(Person.class);

    // Some method under test
    Address address = shippingService.getShippingAddress(person);

    // A failing assertion
    assertThat(address).isEqualTo(person.getAddress());
}
```

The failed test output will include the following message:

```
Test method 'verifyShippingAddress' failed with seed: 8532
```

The failed test can be reproduced by using the seed reported in the failure message.
This can be done by placing the {{Seed}} annotation on the test method:

``` java linenums="1" title="Reproducing a failed test" hl_lines="2"
@Test
@Seed(8532)
void verifyShippingAddress() {
    // snip ... same test code as above
}
```
!!! attention ""
    <lnum>2</lnum> Specifying the seed will reproduce previously generated values.

With the `@Seed` annotation in place, the data becomes effectively static.
This allows the root cause to be established and fixed.
Once the test is passing, the `@Seed` annotation can be removed so that new data will be generated on each subsequent test run.

## Settings Injection

The `InstancioExtension` also adds support for injecting {{Settings}} into a test class.
The injected settings will be used by every test method within the class.
This can be done using the {{WithSettings}} annotation.

``` java linenums="1" title="Injecting Settings into a test class" hl_lines="4 10"
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @WithSettings
    private final Settings settings = Settings.create()
        .set(Setting.COLLECTION_MIN_SIZE, 10);

    @Test
    void example() {
        Person person = Instancio.create(Person.class);

        assertThat(person.getPhoneNumbers())
            .hasSizeGreaterThanOrEqualTo(10);
    }
}
```
!!! attention ""
    <lnum>4</lnum> Inject custom settings to be used by every test method within the class.<br/>
    <lnum>10</lnum> Every object will be created using the injected settings.

!!! warning "There can be only one field annotated `@WithSettings` per test class."

Instancio also supports overriding the injected settings using the `withSettings` method as shown below.
The settings provided via the method take precedence over the injected settings (see [Settings Precedence](#settings-precedence) for further information).

``` java linenums="1" title="Overriding injecting Settings" hl_lines="11"
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @WithSettings
    private final Settings settings = Settings.create()
        .set(Setting.COLLECTION_MIN_SIZE, 10);

    @Test
    void overrideInjectedSettings() {
        Person person = Instancio.of(Person.class)
            .withSettings(Settings.create()
                .set(Setting.COLLECTION_MAX_SIZE, 3))
            .create();

        assertThat(person.getPhoneNumbers())
            .as("Injected settings can be overridden")
            .hasSizeLessThanOrEqualTo(3);
    }
}
```
!!! attention ""
    <lnum>11</lnum> Settings passed in to the builder method take precedence over the injected settings.

Instancio supports `@WithSettings` placed on static and non-static fields.
However, if the test class contains a `@ParameterizedTest` method, then the settings field *must be static*.

## Arguments Source

Using the {{InstancioSource}} annotation it is possible to have arguments provided directly to a `@ParameterzedTest` test method.
This works with a single argument and multiple arguments, each class representing one argument.

!!! warning "Using `@ParameterizedTest` requires the `junit-jupiter-params` dependency."
    See <a href="https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-setup" target="_blank">JUnit documentation for details</a>.

``` java linenums="1" title="Using @InstancioSource with @ParameterizedTest"
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @ParameterizedTest
    @InstancioSource(Person.class)
    void singleArgument(Person person) {
        // snip...
    }

    @ParameterizedTest
    @InstancioSource({Foo.class, Bar.class, Baz.class})
    void multipleArguments(Foo foo, Bar bar, Baz baz) {
        // snip...
    }
}
```

It should be noted that using `@InstancioSource` has a couple of important limitations that makes it unsuitable in many situations.

The biggest limitation is that the generated objects cannot be customised.
The only option is to customise generated values using [settings injection](#settings-injection).
However, it is not possible to customise values on a per-field basis, as you would with the builder API.

The second limitation is that it does not support parameterized types.
For instance, it is not possible to specify that `@InstancioSource(List.class)` should be of type `List<String>`.
