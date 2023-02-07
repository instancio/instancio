## Introduction

### Overview

Instancio is a Java library for generating test objects.
Its main goal is to reduce manual data setup in unit tests.
Its API was designed to be as non-intrusive and as concise as possible, while providing enough flexibility to customise generated objects.
Instancio requires no changes to production code, and it can be used out-of-the-box with zero configuration.

### Project Goals

There are several existing libraries for generating realistic test data, such as addresses, first and last names, and so on.
While Instancio also supports this use case, this is not its goal.
The idea behind the project is that most unit tests do not care what the actual values are.
They just require the _presence of a value_.
Therefore, the main goal of Instancio is simply to generate fully populated objects with random data, including arrays, collections, nested collections, generic types, and so on.
And it aims to do so with as little code as possible in order to keep the tests concise.

Another goal of Instancio is to make the tests more dynamic.
Since each test run is against random values, the tests become alive.
They cover a wider range of inputs, which might help uncover bugs that may have gone unnoticed with static data.
In many cases, the random nature of the data also removes the need for parameterising test methods.

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
Instancio.create(Class<T> type)
Instancio.create(TypeTokenSupplier<T> supplier)
Instancio.create(Model<T> model)
```

The following builder methods allow chaining additional method calls in order to customise generated values, ignore certain fields, provide custom settings, and so on.

``` java linenums="1" title="Builder API"
Instancio.of(Class<T> type).create()
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

It should be noted that generic types can also be created using `Instancio.of(Class)` and specifying the type parameters as arguments
to the `withTypeParameters()` method:


``` java linenums="1"
Pair<String, Long> pair = Instancio.of(Pair.class)
    .withTypeParameters(String.class, Long.class)
    .create();
```

However, this approach has a couple of drawbacks: it does not support nested generics, and its usage will produce an "unchecked assignment" warning.

### Creating collections

The builder API also supports creating collections using the following methods:

``` java linenums="1" title="Collections API"
Instancio.ofList(Class<T> elementType).create()
Instancio.ofSet(Class<T> elementType).create()
Instancio.ofMap(Class<K> keyType, Class<V> valueType).create()
```

Examples:

```java linenums="1"
List<Person> list = Instancio.ofList(Person.class).size(10).create();

Map<UUID, Address> map = Instancio.ofMap(UUID.class, Address.class).size(3)
    .set(field(Address.class, "city"), "Vancouver")
    .create();
```

Specifying collection's size is optional.
If no size is specified, a collection of random size (between 2 and 6 inclusive) will be generated.

### Creating `record` and `sealed` Classes

Instancio version `1.5.0` introduced support for creating

 - `record` classes when run on Java 16+, and
 - `sealed` classes when run on Java 17+.

This uses the same API as described above for creating regular classes.

### Creating a Stream of Objects

Instancio also provides methods for creating a `Stream` of objects.
The `stream()` methods return an infinite stream of distinct fully-populated instances.
Similarly to the `create()` methods, these have a shorthand form if no customisations are needed:

``` java linenums="1" title="Shorthand methods"
Instancio.stream(Class<T> type)
Instancio.stream(TypeTokenSupplier<T> supplier)
```

as well as the builder API that allows customising generated values:

``` java linenums="1" title="Stream Builder API"
Instancio.of(Class<T> type).stream()
Instancio.of(TypeTokenSupplier<T> supplier).stream()
```

The following are a couple of examples of using streams. Note the calls to `limit()`,
which are required to avoid an infinite loop.

``` java linenums="1" title="Examples of stream() methods" hl_lines="2 8"
List<Person> personList = Instancio.stream(Person.class)
    .limit(3)
    .collect(Collectors.toList());

Map<UUID, Person> personMap = Instancio.of(new TypeToken<Person>() {})
    .ignore(all(field("age"), field("address")))
    .stream()
    .limit(3)
    .collect(Collectors.toMap(Person::getUuid, Function.identity()));
```

!!! warning "Since returned streams are infinite, `limit()` _must_ be called to avoid an infinite loop."

## Selectors

Selectors are used to target fields and classes, for example in order to customise generated values.
Instancio supports different types of selectors, all of which implement the {{TargetSelector}} interface.
These types are:

- regular selectors
- method reference selector
- predicate selectors
- selector groups
- convenience selectors

All of the above can be created using static methods in the {{Select}} class.

#### Regular selectors

Regular selectors are for precise matching: they can only match a single field or a single type.

``` java linenums="1"
Select.field(String fieldName)
Select.field(Class<?> declaringClass, String fieldName)
Select.all(Class<?> type)
```
!!! attention ""
    <lnum>1</lnum> Selects field by name, declared in the class being created.<br/>
    <lnum>2</lnum> Selects field by name, declared in the specified class.<br/>
    <lnum>3</lnum> Selects the specified class, including fields and collection elements of this type.<br/>

```java title="Examples"
Select.field(Person.class, "name") // Person.name
Select.all(Set.class)
```

`Select.field()` is matched based on exact field name. If a field with the specified name does not exist, an error will be thrown.
`Select.all()` is matched using `Class` equality, therefore matching does not include subtypes.

#### Method reference selector

This selector uses method references to match fields.

``` java linenums="1"
Select.field(GetMethodSelector<T, R> methodReference)
```

```java title="Example"
Select.field(Person::getName)
```

Internally, method reference is converted to a regular field selector, equivalent to `Select.field(Class<?> declaringClass, String fieldName)`.
This is done by mapping method name to the corresponding field name. The mapping logic supports the following naming conventions:

 - Java beans - where getters are prefixed with `get` and, in case of booleans, `is`.
 - Java record - where method names match field names exactly.

For example, all the following combinations of field and method names are supported:

|Method name  |Field name  |Example|
|-------------|------------|-------|
|`getName()`  | `name`     |`field(Person::getName)`  -&gt; `field(Person.class, "name")`|
|`name()`     | `name`     |`field(Person::name)`  -&gt; `field(Person.class, "name")`|
|`isActive()` | `active`   |`field(Person::isActive)`  -&gt; `field(Person.class, "active")`|
|`isActive()` | `isActive` |`field(Person::isActive)`  -&gt; `field(Person.class, "isActive")`|

For methods that follow other naming conventions, or situations where no method is available, regular field selectors can be used instead.

!!! info "Regular selector definition"
    From here on, the definition of *regular selectors* also includes method reference selectors.

#### Predicate selectors

Predicate selectors allow for greater flexibility in matching fields and classes.
These use a plural naming convention: `fields()` and `types()`.

``` java linenums="1"
Select.fields(Predicate<Field> fieldPredicate)
Select.types(Predicate<Class<?>> classPredicate)
```

!!! attention ""
    <lnum>1</lnum> Selects all fields matching the predicate.<br/>
    <lnum>2</lnum> Selects all types matching the predicate.<br/>

``` java title="Examples"
Select.fields(field -> field.getName().contains("date"))
Select.types(klass -> Collection.class.isAssignableFrom(klass))
```

Unlike regular selectors, these can match multiple fields or types.
For example, they can be used to match all fields declared by a class, or all classes within a package.
They can also be used to match a certain type, including its subtypes.

#### Convenience selectors

Convenience selectors provide syntactic sugar built on top of regular and predicate selectors.

``` java linenums="1"
Select.all(GroupableSelector... selectors)
Select.allStrings()
Select.allInts()
Select.fields()
Select.types()
Select.root()
```
!!! attention ""
    <lnum>1</lnum> For combining multiple regular selectors.<br/>
    <lnum>2</lnum> Equivalent to `all(String.class)`.<br/>
    <lnum>3</lnum> Equivalent to `all(all(int.class), all(Integer.class))`.<br/>
    <lnum>4</lnum> Builder for constructing `Predicate<Field>` selectors.<br/>
    <lnum>5</lnum> Builder for constructing `Predicate<Class<?>>` selectors.<br/>
    <lnum>6</lnum> Selects the root, that is, the object being created.<br/>

!!! info "The `allXxx()` methods such as `allInts()`, are available for all core types."

- **`Select.all(GroupableSelector... selectors)`**

This method can be used for grouping multiple selectors, allowing for more concise code
as shown below. However, only regular selectors are groupable using `all()`. Predicate selectors are not groupable.

``` java
all(field(Address::getCity),
    field(Address.class, "postalCode"),
    all(Phone.class))
```

- **`Select.fields()`** and **`Select.types()`**

These selectors provide a builder API for constructing predicate selectors. 
For example, the following predicate that matches `Long` fields annotated with `@Id`

``` java
Select.fields(f -> f.getType() == Long.class && f.getDeclaredAnnotation(Id.class) != null)
```

can also be expressed using the `fields()` predicate builder:

``` java
Select.fields().ofType(Long.class).annotated(Id.class)
```
- **`Select.root()`**

This method selects the root object. The following snippet creates nested lists,
where the outer list and inner lists have different sizes:

``` java linenums="1" hl_lines="2"
List<List<String>> result = Instancio.of(new TypeToken<List<List<String>>>() {})
    .generate(root(), gen -> gen.collection().size(outerListSize))
    .generate(all(List.class), gen -> gen.collection().size(innerListSize))
    .create();
```

In this case, `all(List.class)` matches all lists except the outer list,
because `root()` selector has higher precedence than other selectors.

### Selector Precedence

Selector precedence rules apply when multiple selectors match a field or class:

- Regular selectors have higher precedence than predicate selectors.
- Field selectors have higher precedence than type selectors.

Consider the following example:

``` java linenums="1"
Address address = Instancio.of(Address.class)
    .set(allStrings(), "foo")
    .set(field("city"), "bar")
    .create();
```

This will produce an address object with all strings set to "foo". However, since field selectors
have higher precedence, the city will be set to "bar".

In the following example, the city will also be set to "bar" because predicate `fields()` selector
has lower precedence than the regular `field()` selector:

``` java linenums="1"
Address address = Instancio.of(Address.class)
    .set(fields().named("city"), "foo")
    .set(field("city"), "bar")
    .create();
```

#### Multiple matching selectors

When more than one selector matches a given target, then the last selector wins.
This rule applies to both, regular and predicate selectors.

In case of regular selectors, if the two are identical, the last selector simply
replaces the first (internally, regular selectors are stored as `Map` keys).

``` java linenums="1"
Address address = Instancio.of(Address.class)
    .set(field(Address.class, "city"), "foo")
    .set(field(Address.class, "city"), "bar") // wins!
    .create();
```

Predicate selectors, on the other hand, are stored as a `List` and evaluated
sequentially starting from the last entry.

``` java linenums="1"
Address address = Instancio.of(Address.class)
    .set(fields().named("city"), "foo")
    .set(fields().named("city"), "bar") // wins!
    .lenient()
    .create();
```

In this particular example, the first entry remains unused,
therefore  `lenient()` mode must be enabled to prevent unused selector error
(see [Selector Strictness](#selector-strictness)).

### Selector Scopes

Regular selectors provide the `within(Scope... scopes)` method for fine-tuning the targets selectors should be applied to.
Instancio supports two types of scope:

- Class-level scope: narrows down a selector to the specified class.
- Field-level scope: narrows down a selector to the specified field of the target class.

To illustrate how scopes work we will assume the following structure for the `Person` class
(getters and setters omitted).

``` java
class Person {
    String name;
    Address homeAddress;
    Address workAddress;
}

class Address {
    String street;
    String city;
    List<Phone> phoneNumbers;
}

class Phone {
    String areaCode;
    String number;
}
```

Note that the `Person` class has two `Address` fields.
Selecting `field(Address::getCity)` would target both addresses, `homeAddress` and `workAddress`.
Without scopes, it would not be possible to set the two cities to different values.
Using scopes solves this problem:

``` java linenums="1" title="Creating scope using toScope() method"
Scope homeAddress = field(Person::getHomeAddress).toScope();
Scope workAddress = field(Person::getWorkAddress).toScope();

Person person = Instancio.of(Person.class)
    .set(field(Address::getCity).within(homeAddress), "foo")
    .set(field(Address::getCity).within(workAddress), "bar")
    .create();
```

For more complex class structures, multiple scopes can be specified using `within(Scope...)` method.
When specifying multiple scopes, the order is important: from outermost to innermost scope.
Additional examples will be provided below.

#### Creating scopes

Scopes can be creating using:

- `Select.scope()` static methods in the {{Select}} class
- `Selector.toScope()` method provided by regular selectors

The first approach requires specifying the target class and, for field-level scopes, the name of the field:

``` java linenums="1"
Select.scope(Class<?> targetClass)
Select.scope(Class<?> targetClass, String field)
```

``` java title="Examples"
Select.scope(Phone.class);
Select.scope(Person.class, "homeAddress");
```

The second approach is to create scopes from selectors using the `toScope()` method.
This method is only available for regular (non-predicate) selectors.

``` java linenums="1"
Select.all(Class<T> targetClass).toScope()
Select.field(Class<T> targetClass, String field).toScope()
Select.field(GetMethodSelector<T, R> methodReference).toScope()
```

``` java title="Examples"
Select.all(Phone.class).toScope();
Select.field(Person.class, "homeAddress").toScope();
Select.field(Person::getHomeAddress).toScope();
```

#### Examples of using scopes

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

``` java title="Set all strings in Person.homeAddress address object"
set(allStrings().within(scope(Person.class, "homeAddress")), "foo")
```

Using `within()` also allows specifying multiple scopes. Scopes must be specified top-down, starting from the outermost to the innermost.

``` java title="Set all strings of all Phone instances contained within Person.workAddress field"
set(allStrings().within(scope(Person.class, "workAddress"), scope(Phone.class)), "foo")
```

The `Person.workAddress` object contains a list of phones, therefore `Person.workAddress` is the outermost scope and is specified first.
`Phone` class is the innermost scope and is specified last.

The final examples illustrate creation of scope objects from regular selectors. All of the following are equivalent to each other.

``` java title="Equivalent ways of creating scopes based on field"
set(allStrings().within(scope(Person.class, "homeAddress")), "foo")

set(allStrings().within(field(Person.class, "homeAddress").toScope()), "foo")

set(allStrings().within(field(Person::getHomeAddress).toScope()), "foo")
```

``` java title="Equivalent ways of creating scopes based on class"
set(allStrings().within(scope(Person.class)), "foo")

set(allStrings().within(all(Person.class).toScope()), "foo")
```

### Selector Strictness

#### Strict Mode

Instancio supports two modes: strict and lenient, an idea inspired by Mockito's highly useful strict stubbing feature.

In strict mode unused selectors will trigger an error. In lenient mode unused selectors are simply ignored.
By default, Instancio runs in strict mode. This is done for the following reasons:

- to eliminate errors in data setup
- to simplify fixing tests after refactoring
- to keep test code clean and maintainable

##### Eliminate errors in data setup

An unused selector could indicate an error in the data setup.
As an example, consider populating the following POJO:

``` java linenums="1" hl_lines="2 6"
class SamplePojo {
    SortedSet<String> values;
}

SamplePojo pojo = Instancio.of(SamplePojo.class)
    .generate(all(Set.class), gen -> gen.collection().size(10))
    .create();
```

At first glance, we might expect a `Set` of size 10 to be generated.
However, since the field is declared as a `SortedSet` and the class selector
targets `Set`, the `generate()` method will not be applied. 

Since `all(Set.class)` did not match any target, Instancio produces an error:

```
org.instancio.exception.UnusedSelectorException:

 -> Unused selectors in generate(), set(), or supply():
 1: all(Set)
```

Without being aware of this detail, it is easy to make this kind of error and face unexpected results
even with a simple class like above. It gets trickier when generating more complex classes.
Strict mode helps reduce this type of error.

##### Simplify fixing tests after refactoring

Refactoring always causes tests to break to some degree. As classes and fields get reorganised and renamed,
tests need to be updated to reflect the changes. Assuming there are existing tests utilising Instancio,
running tests in strict mode will quickly highlight any problems in data setup caused by refactoring.

##### Keep test code clean and maintainable

Last but not least, it is important to keep tests clean and maintainable. Test code should be treated
with as much care as production code. Keeping the tests clean and concise makes them easier to maintain.

#### Lenient Mode

While strict mode is highly recommended, there is an option to switch to lenient mode.
The lenient mode can be enabled using the `lenient()` method:

``` java title="Setting lenient mode using builder API"
Person person = Instancio.of(Person.class)
    // snip...
    .lenient()
    .create();
```

Lenient mode can also be enabled via `Settings`:

``` java title="Setting lenient mode using Settings"
Settings settings = Settings.create()
    .set(Keys.MODE, Mode.LENIENT);

Person person = Instancio.of(Person.class)
    .withSettings(settings)
    // snip...
    .create();
```

Lenient mode can also be enabled globally using [`instancio.properties`](#overriding-settings-using-a-properties-file):

``` java title="Setting lenient mode using properties file"
mode=LENIENT
```

## Customising Objects

Properties of an object created by Instancio can be customised using

- `generate()`
- `set()`
- `supply()`

methods defined in the {{InstancioApi}} class.


### Using `generate()`

The `generate()` method provides access to built-in generators for core types from the JDK, such strings, numeric types, dates, arrays, collections, and so on.
It allows modifying generation parameters for these types in order to fine-tune the data.
The usage is shown in the following example, where the `gen` parameter (of type {{Generators}})
exposes the available generators to simplify their discovery using IDE auto-completion.

``` java linenums="1" title="Example of using generate()"
Person person = Instancio.of(Person.class)
    .generate(field("age"), gen -> gen.ints().range(18, 65))
    .generate(field("pets"), gen -> gen.array().length(3))
    .generate(field(Phone.class, "number"), gen -> gen.text().pattern("#d#d#d-#d#d-#d#d"))
    .create();
```

Below is another example of customising a `Person`.
For instance, if the  `Person` class has a field `List<Phone>`, by default Instancio would use `ArrayList` as the implementation.
Using the collection generator, this can be overridden by specifying the type explicitly:

``` java linenums="1" title="Example: customising a collection"
Person person = Instancio.of(Person.class)
    .generate(field("phoneNumbers"), gen -> gen.collection().minSize(3).subtype(LinkedList.class))
    .generate(field(Phone.class, "countryCode"), gen -> gen.oneOf("+33", "+39", "+44", "+49"))
    .create();
```

Each generator provides methods applicable to the type it generates, for example:

- `gen.string().minLength(3).allowEmpty().nullable()`
- `gen.map().size(5).nullableValues().subtype(TreeMap.class)`
- `gen.temporal().localDate().future()`
- `gen.longs().min(Long.MIN_VALUE)`
- `gen.enumOf(MyEnum.class).excluding(MyEnum.FOO, MyEnum.BAR)`

In addition, most generators can also return generated values as Strings, for example:

``` java linenums="1"
class Foo {
    String dateString;
    String enumString;
}

Instancio.of(Foo.class)
    .generate(field("dateString"), gen -> gen.temporal().localDate().past().asString())
    .generate(field("enumString"), gen -> gen.enumOf(MyEnum.class).asString(e -> e.name().toUpperCase()))
    .create();
```

The complete list of built-in generators:

```css
Generators
│
├── booleans()
├── chars()
├── bytes()
├── shorts()
├── ints()
├── longs()
├── floats()
├── doubles()
├── string()
│
├── array()
├── collection()
├── map()
├── enumOf(Class<E>)
├── enumSet(Class<E>)
│
├── oneOf(Collection<T>)
├── oneOf(T...)
│
├── math()
│   └── bigInteger()
│   └── bigDecimal()
│
├── net()
│   └── uri()
│   └── url()
│
├── io()
│   └── file()
│
├── nio()
│   └── path()
│
├── atomic()
│   ├── atomicInteger()
│   └── atomicLong()
│
├── temporal()
│   └── calendar()
│   └── date()
│   └── duration()
│   └── instant()
│   └── localDate()
│   └── localDateTime()
│   └── localTime()
│   └── offsetDateTime()
│   └── offsetTime()
│   └── period()
│   └── sqlDate()
│   └── timestamp()
│   └── year()
│   └── yearMonth()
│   └── zonedDateTime()
│
└── text()
    └── loremIpsum()
    └── pattern(String)
    └── uuid()
```


### Using `set()`

The `set()` method can be used for setting a static value to selected targets,
just like a regular setter method:

``` java linenums="1"
Person person = Instancio.of(Person.class)
    .set(field(Phone::getCountryCode), "+1")
    .set(all(LocalDateTime.class), LocalDateTime.now())
    .create();
```

However, unlike a regular set method that can only be invoked on a single object,
the above will set `countryCode` to "+1" on _all_ generated instances of `Phone` class.
Assuming the `Person` class contains a `List<Phone>`, they will all have the specified country code.
Similarly, all `LocalDateTime` values will be set to the same instance of `now()`.


### Using `supply()`

The `supply()` method has two variants:

``` java linenums="1"
supply(TargetSelector selector, Supplier<V> supplier)
supply(TargetSelector selector, Generator<V> generator)
```

The first accepts a `java.util.function.Supplier` and is for supplying *non-random* values.
The second accepts an Instancio {{Generator}} (a functional interface) and can be used for supplying *random* values.

#### Using supply() to provide *non-random* values

The following is another example of setting all `LocalDateTime` instances to `now()`.

``` java linenums="1"
Person person = Instancio.of(Person.class)
    .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
    .create();
```

Unlike the earlier example using `set()`, this will assign a new instance for each `LocalDateTime`:

``` java linenums="1"
set(all(LocalDateTime.class), LocalDateTime.now())        // reuse the same instance for all dates
supply(all(LocalDatime.class), () -> LocalDateTime.now()) // create a new instance for each date
```

#### Using supply() to provide *random* values

The second variant of the `supply()` method can be used for supplying random values and objects.
This method takes a {{Generator}} as an argument, which is a functional interface with the following signature:

``` java linenums="1"
import org.instancio.Random;

interface Generator<T> {
    T generate(Random random);
}
```

Using the provided {{Random}} instance ensures that generated objects are reproducible.
Since `Generator` is a functional interface it can be specified as a lambda expression:

``` java linenums="1"
Person person = Instancio.of(Person.class)
        .supply(all(Phone.class), random -> Phone.builder()
                .countryCode(random.oneOf("+1", "+52"))
                .number(random.digits(7))
                .build())
        .create();
```

Generators can be used for generating simple value types as well as building complex objects.
They are described in more detail in the [Custom Generators](#custom-generators) section.

### Using `onComplete()`

Another option for customising generated data is using the {{OnCompleteCallback}}, a functional interface with the following signature:

``` java linenums="1"
interface OnCompleteCallback<T> {
    void onComplete(T object);
}
```

The `OnCompleteCallback` is invoked *after* the generated object has been fully populated.
Callbacks can only be invoked on objects:

- created internally by the engine
- created by custom generators via `supply(TargetSelector, Generator)` or generators registered via {{GeneratorProvider}}
- created by internal generators using `generate(TargetSelector, Function)`

Callbacks are *never* invoked on objects provided using:

  - `supply(TargetSelector, Supplier)`
  - `set(TargetSelector, Object)`

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

If the object is mutable, callbacks allow modifying multiple fields at once.
However, callbacks cannot be used to immutable types.

Another property of callbacks is that they are only invoked on non-null objects.
In the following example, all address instances are nullable.
Therefore, a generated address instance may either be `null` or a fully-populated object.
However, if a `null` was generated, the callback will not invoked.

``` java linenums="1" title="Callbacks only called on non-null values"
Person person = Instancio.of(Person.class)
    .withNullable(all(Address.class))
    .onComplete(all(Address.class), (Address address) -> {
        // only-called if generated address is not null
    })
    .create();
```

---

#### Summary of methods for customising objects


**Methods** `set(TargetSelector, Object)` and `supply(TargetSelector, Supplier)` are for supplying objects *as is*.
The provided objects are treated as read-only by the engine.
The behaviour of these methods cannot be customised.

- matching selectors will not be applied
- engine will not modify or populate any fields of the supplied object
- callbacks are *not* invoked on objects provided by these methods
 
**Method** `supply(TargetSelector, Generator)` is for creating objects using custom `Generator` implementations.
This method offers configurable behaviour via the `AfterGenerate` hint.
By default, the hint is set to `POPULATE_NULLS_AND_DEFAULT_PRIMITIVES`, which implies:

- matching selectors will be applied
- engine will populate `null` fields and primitive fields containing default values

The default value of the `AfterGenerate` hint can be overridden using `instancio.properties` and {{Settings}}.

!!! info
    Callbacks are always invoked on objects created by generators regardless of `AfterGenerate` value.

**Method** `generate(TargetSelector, Function)` is for customising objects created by internal generators.
Such objects include value types (numbers, strings, dates) and data structures (collections, arrays).

- matching selectors are **always** applied
- matching callbacks are **always** invoked

By default, engine generates non-null values, unless specified otherwise.


### Ignoring Fields or Classes

By default, Instancio will attempt to populate every non-static field value.
The `ignore` method can be used where this is not desirable:

``` java linenums="1" title="Example: ignoring certain fields and classes"
Person person = Instancio.of(Person.class)
    .ignore(field(Person::getPets))
    .ignore(all(LocalDateTime.class))
    .create();

// Or combining the selectors
Person person = Instancio.of(Person.class)
    .ignore(all(
        field(Person::getPets),
        all(LocalDateTime.class)))
    .create();
```

The `ignore()` method has higher precedence than other methods. For example, in the following snippet
specifying `ignore(all(LocalDateTime.class))` but supplying a value for the `lastModified` field
will actually generate a `lastModified` with a `null` value.

``` java linenums="1" title="ignore() has higher precedence than other methods"
Person person = Instancio.of(Person.class)
    .ignore(all(LocalDateTime.class))
    .supply(field(Person::getLastModified), () -> LocalDateTime.now())
    .create();
```

### Nullable Values

By default, Instancio generates non-null values.
There are cases where this behaviour may need to be relaxed, for example to verify that a piece of code does not fail in the presence of certain `null` values.
There are a few way to specify that values can be nullable which can broadly grouped into two categories:

- field nullability
- array/collection element and map key/value nullability

#### Field nullability

Field declarations can be made nullable using:

- {{withNullable}} method of the builder API
- generator spec `nullable()` method (for generators that support it)
- {{Settings}}

For example, the following marks `Person.address` and all `String` fields as nullable:

``` java linenums="1"
Person person = Instancio.of(Person.class)
    .withNullable(field(Person::getAddress))
    .withNullable(allStrings())
    .create();
```

A number of built-in generators also support marking values as nullable.
As in the previous example, this also applies only to string fields,
and not, for example, `List<String>`:

```java linenums="1"
Person person = Instancio.of(Person.class)
    .generate(allStrings(), gen -> gen.string().nullable())
    .create();
```

The final example, using `Settings`, follows the same principle:

```java linenums="1"
Settings settings = Settings.create()
    .set(Keys.STRING_NULLABLE, true);

Person person = Instancio.of(Person.class)
    .withSettings(settings)
    .create();
```

#### Element nullability

Data structure elements can be made nullable using:

- array generator: `nullableElements()`
- collection generator: `nullableElements()`
- map generator: `nullableKeys()`, `nullableValues()`
- {{Settings}} keys
    - `Keys.ARRAY_ELEMENTS_NULLABLE`
    - `Keys.COLLECTION_ELEMENTS_NULLABLE`
    - `Keys.MAP_KEYS_NULLABLE`
    - `Keys.MAP_VALUES_NULLABLE`


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

Lastly, element nullability can be specified using {{Settings}} as follows:

``` java linenums="1" title="Example: specifying nullability using Settings"
Settings settings = Settings.create()
    .set(Keys.COLLECTION_ELEMENTS_NULLABLE, true);

Person person = Instancio.of(Person.class)
    .withSettings(settings)
    .create();
```

## Subtype Mapping

Subtype mapping allows mapping a particular type to its subtype.
This can be useful for specifying a specific implementation for an abstract type.
The mapping can be specified using the `subtype` method:


``` java linenums="1"
subtype(TargetSelector selector, Class<?> subtype)
```

All the types represented by the selectors must be supertypes of the given `subtype` parameter.

``` java linenums="1" title="Example: subtype mapping" hl_lines="2 3 4"
Person person = Instancio.of(Person.class)
    .subtype(all(Pet.class), Cat.class)
    .subtype(all(all(Collection.class), all(Set.class)), TreeSet.class)
    .subtype(field("address"), AddressImpl.class)
    .create();
```

!!! attention ""
    <lnum>2</lnum> If `Pet` is an abstract type, then without the mapping all `Pet` instances will be `null`
     since Instancio would not be able to resolve the implementation class.<br/>
    <lnum>3</lnum> Multiple types can be mapped as long as the subtype is valid for all of them.<br/>
    <lnum>4</lnum> Assuming `Person` has an `Address` field, where `Address` is the superclass of `AddressImpl`.

## Using Models

A {{Model}} is a template for creating objects.
It encapsulates all the parameters specified using the builder API.
Once a model is defined, it can be used to create objects without duplicating the common properties.

``` java linenums="1" title="Example: creating objects from a Model" hl_lines="1 6"
Model<Person> simpsonsModel = Instancio.of(Person.class)
    .ignore(field(Person::getId))
    .set(field(Person::getLastName), "Simpson")
    .set(field(Address::getCity), "Springfield")
    .generate(field(Person::getAge), gen -> gen.ints().range(40, 50))
    .toModel();

Person homer = Instancio.of(simpsonsModel)
    .set(field(Person::getFirstName), "Homer")
    .create();

Person marge = Instancio.of(simpsonsModel)
    .set(field(Person::getFirstName), "Marge")
    .create();
```
!!! attention ""
    <lnum>1</lnum> The `Model` class itself does not expose any public methods, and its instances are effectively immutable.

!!! info "Objects created from a model inherit all the model's properties, including {{Settings}}, {{Mode}} and seed value."

A model can also be used as a template for creating other models.
Using the previous example, we can define a new model with additional data:

``` java linenums="1"
Model<Person> simpsonsModelWithPets = Instancio.of(simpsonsModel)
    .supply(field(Person::getPets), () -> List.of(
                new Pet(PetType.CAT, "Snowball"),
                new Pet(PetType.DOG, "Santa's Little Helper"))
    .toModel();
```

Having a common model allows test methods to create custom objects by overriding the model's properties via selectors
(including properties of nested objects). This also works for immutable objects, such as Java records.

``` java linenums="1"
Person withNewAddress = Instancio.of(simpsonsModel)
    .set(field(Address:getCity), "Springograd")
    .create();
```

This approach reduces duplication and simplifies data setup, especially for complex classes with many fields and relationships. More details on benefits of using models, including a sample project, are provided in the article
[Creating object templates using Models](/articles/creating-object-templates-using-models/).

## Custom Generators

Every type of object Instancio generates is through an implementation of the `Generator` interface.
A number of internal generators are included out of the box for creating strings, numeric types, dates, collections, and so on.
Custom generators can also be defined to satisfy certain use cases:

- For generating types not supported out of the box, for example from third-party libraries such as Guava.

- For creating pre-initialised domain objects. Some domain objects require to be constructed in a certain state to be valid.
To avoid duplicating the construction logic across different tests, it can be encapsulated by a custom generator that can be re-used across the project.

- For distributing generators as a library that can be shared across projects.


`Generator` is a functional interface with a single abstract method `generate(Random)`:


```java linenums="1"
@FunctionalInterface
interface Generator<T> {

    T generate(Random random);

    default Hints hints() {
        return null;
    }
}
```

If a generator produces random data, it must use the provided {{Random}} instance to guarantee that the created object can be reproduced for a given seed value.
The `hints()` method is for passing additional instructions to the engine. The most important hint is the {{AfterGenerate}} action which determines whether the engine should:

- populate uninitialised fields
- modify the object by applying matching selectors (if any)

```java linenums="1" title="An example of specifying hints"
@Override
public Hints hints() {
    return Hints.afterGenerate(AfterGenerate.APPLY_SELECTORS);
}
```

The `AfterGenerate` enum defines the following values:

- **`DO_NOT_MODIFY`**
  
    Indicates that the object created by the generator should not be modified.
    The engine will treat the object as read-only and assign it to the target field as is.
    Matching selectors will not be applied.

- **`APPLY_SELECTORS`**

    Indicates that the object can only be modified via matching selectors
    using `set()`, `supply()`, and `generate()` methods.

- **`POPULATE_NULLS`**

    Indicates that `null` fields declared by the object should be populated.
    In addition, the object will be modifiable using selectors as described
    by above by `APPLY_SELECTORS`.


- **`POPULATE_NULLS_AND_DEFAULT_PRIMITIVES`** **(default behaviour)**

    Indicates that primitive fields with default values declared by the object
    should be populated. In addition, the behaviour described by `POPULATE_NULLS`
    applies as well. Default primitives are defined as:

    - `0` for all numeric types
    - `false` for `boolean`
    - `'\u0000'` for `char`

- **`POPULATE_ALL`**

    Indicates that all fields should be populated, regardless of their initial values.
    This action will cause all the values to be overwritten with random data.
    This is the default mode the engine operates in when using internal generators.


In summary, a generator can instantiate an object and instruct the engine
what should be done with the object after `generate()` method returns using
the `AfterGenerate` hint.


### Registering generators using `GeneratorProvider` SPI

Instancio offers {{GeneratorProvider}} service provider interface for registering custom generators
(or overriding built-in generators) using the `ServiceLoader` mechanism.
The provider interface is defined as:

```java linenums="1"
interface GeneratorProvider {
    Map<Class<?>, Generator<?>> getGenerators();
}
```

The service provider can be registered by creating a file named `org.instancio.spi.GeneratorProvider`
under `/META-INF/services/`, and containing the fully-qualified name of the provider implementation:

``` title="/META-INF/services/org.instancio.spi.GeneratorProvider"
org.example.CustomGeneratorProvider
```

### Modifying `overwrite.existing.values` setting

Instancio configuration has a property `overwrite.existing.values` which by default is set to `true`.
This results in the following behaviour.

```java linenums="1" hl_lines="2 7" title="Example 1: field overwritten when creating an object"
class Address {
    private String country = "USA"; // default
    // snip...
}

Person person = Instancio.create(Person.class);
assertThat(person.getAddress().getCountry()).isNotEqualTo("USA"); // overwritten!
```
!!! attention ""
    <lnum>7</lnum> the country is no longer "USA" as it was overwritten with a random value.<br/>


```java linenums="1" hl_lines="4 10 13" title="Example 2: field overwritten by applying a selector"
class AddressGenerator implements Generator<Address> {
    @Override
    public Address generate(Random random) {
        return Address.builder().country("USA").build();
    }
}

Person person = Instancio.of(Person.class)
    .supply(all(Address.class), new AddressGenerator())
    .set(field(Address.class, "country"), "Canada")
    .create();

assertThat(person.getAddress().getCountry()).isEqualTo("Canada"); // overwritten!
```
!!! attention ""
    <lnum>13</lnum> The country was overwritten via the applied selector.<br/>


To disallow overwriting of initialised fields, the `overwrite.existing.values` setting can be set to `false`.
Using the last example to illustrate:

```java linenums="1" hl_lines="4" title="Example 2: field overwritten by applying a selector"
Person person = Instancio.of(Person.class)
    .supply(all(Address.class), new AddressGenerator())
    .set(field(Address.class, "country"), "Canada")
    .withSettings(Settings.create().set(Keys.OVERWRITE_EXISTING_VALUES, false))
    .create();

assertThat(person.getAddress().getCountry()).isEqualTo("USA"); // not overwritten!
```

## Assignment Settings

Assignment settings control whether values are assigned directly to fields (default behaviour)
or via setter methods. There are a few {{Settings}} keys with enum values that control the behaviour.


| `Keys` constant           | Enum class            |
|---------------------------|-----------------------|
| `ASSIGNMENT_TYPE`         | `AssignmentType`      |
| `SETTER_STYLE`            | `SetterStyle`         |
| `ON_SET_FIELD_ERROR`      | `OnSetFieldError`     |
| `ON_SET_METHOD_ERROR`     | `OnSetMethodError`    |
| `ON_SET_METHOD_NOT_FOUND` | `OnSetMethodNotFound` |

To enable assignment via methods, `Keys.ASSIGNMENT_TYPE` can be set to `AssignmentType.METHOD`.
When enabled, Instancio will attempt to resolve setter names from field names using `SETTER_STYLE` setting.
This key's value is the `SetterStyle` enum that supports three naming conventions:

- `SET` - standard setter prefix, for example `setFoo("value")`
- `WITH` - for example `withFoo("value")`
- `PROPERTY` - no prefix, for example `foo("value")`

The remaining `ON_SET_*` keys are used to control error handling behaviour:

| Key                       | Possible causes                                                                               |
|---------------------------|-----------------------------------------------------------------------------------------------|
| `ON_SET_FIELD_ERROR`      | type mismatch, unmodifiable field, access exception                                           |
| `ON_SET_METHOD_ERROR`     | type mismatch, exception thrown by setter (e.g. due to validation)                            |
| `ON_SET_METHOD_NOT_FOUND` | method does not exist, or name does not conform to naming convention defined by `SetterStyle` |

All of the above can be set to ignore errors or fail fast by raising an exception.
In addition, both `ON_SET_METHOD_*` settings can be configured to fall back to field assignment in case of an error.

The following snippet illustrates how to create an object populated via setters.
In this example, `SetterStyle.PROPERTY` is used since the `Phone` class has setters without the *set* prefix:

```java linenums="1" title="Populating via setters"
class Phone {
    private String areaCode;
    private String number;

    void areaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    void number(String number) {
        this.number = number;
    }
}

Settings settings = Settings.create()
        .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
        .set(Keys.SETTER_STYLE, SetterStyle.PROPERTY)
        .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.IGNORE);

Phone phone = Instancio.of(Phone.class)
        .withSettings(settings)
        .create();
```

{{Settings}} can be specified per object, as shown above, or globally using a properties file:

```properties
assignment.type=METHOD
setter.style=PROPERTY
on.set.method.error=IGNORE
```

See [Configuration](#configuration) for details.

## Seed

Before creating an object, Instancio initialises a random seed value.
This seed value is used internally by the pseudorandom number generator, that is, `java.util.Random`.
Instancio ensures that the same instance of the random number generator is used throughout object creation, from start to finish.
This means that Instancio can reproduce the same object again by using the same seed.
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
final long seed = 123;

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

### Specifying Seed Value

By default, if no custom seed is specified, Instancio generates a random seed value.
Therefore, each execution results in different outputs.
This behaviour can be overridden by specifying a custom seed using any of the options below.
These are ranked from lowest to highest precedence:

- `instancio.properties` file
- `@Seed` and `@WithSettings` annotations (when using [`InstancioExtension`](#junit-jupiter-integration) for  JUnit Jupiter)
- [`Settings`](#overriding-settings-programmatically) class
- {{withSeed}}  method of the builder API

For example, if a seed value is specified in the properties file, then Instancio will use this seed to generate the data
and each execution will result in the same data being generated.
If another seed is specified using `withSeed()` method, then it will take precedence over the one from the properties file.

``` java linenums="1" title="Example: instancio.properties"
seed = 123
```

``` java linenums="1" title="Seed precedence" hl_lines="1 4"
SamplePojo pojo1 = Instancio.create(SamplePojo.class);

SamplePojo pojo2 = Instancio.of(SamplePojo.class)
    .withSeed(456)
    .create();
```
!!! attention ""
    <lnum>1</lnum> `pojo1` generated using seed `123` specified in `instancio.properties`.<br/>
    <lnum>4</lnum> `pojo2` generated using seed `456` since `withSeed()` has higher precedence.


### Getting Seed Value

Sometimes it is necessary to get the seed value that was used to generate the data. One such example is for reproducing failed tests. If you are using JUnit 5, seed value is reported automatically using the `InstancioExtension` (see [JUnit Jupiter integration](#junit-jupiter-integration)). If you are using JUnit 4, TestNG, or Instancio standalone, the seed value can be obtained by calling the `asResult()` method of the builder API. This returns a `Result` containing the created object and the seed value that was used to populate its values.


``` java linenums="1" title="Example of using asResult()"
Result<Person> result = Instancio.of(Person.class).asResult();
Person person = result.get();
long seed = result.getSeed(); // seed value that was used for populating the person
// snip...
```

# Metamodel

This section expands on the [Selectors](#selectors) section, which described how to target fields.
Instancio uses reflection at field level to populate objects.
The main reason for using fields and not setters is <a hred="https://docs.oracle.com/javase/tutorial/java/generics/erasure.html" target="_blank">type erasure</a>.
It is not possible to determine the generic type of method parameters at runtime.
However, generic type information is available at field level:


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
                        <version>{{config.latest_release}}</version>
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
    testAnnotationProcessor "org.instancio:instancio-processor:{{config.latest_release}}"
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

Metamodels for classes specified in the annotation will be automatically generated during the build.
Typically metamodels are placed under a generated sources directory, such as `generated/sources` or `generated-sources`.
If your IDE does not pick up the generated classes, then adding the generated sources directory to the build path
(or simply reloading the project) should resolve this.

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
    <lnum>2</lnum> Creates a new instance containing default settings.<br/>
    <lnum>3</lnum> Creates settings from a `Map` or `java.util.Properties`.<br/>
    <lnum>4</lnum> Creates a copy of `other` settings (a clone operation).

Settings can be overridden programmatically or through a properties file.

!!! info
    To inspect all the keys and default values, simply: `System.out.println(Settings.defaults())`

## Overriding Settings Programmatically

To override programmatically, an instance of `Settings` can be passed in to the builder API:

``` java linenums="1" title="Supplying custom settings" hl_lines="2 5 8"
Settings overrides = Settings.create()
    .set(Keys.COLLECTION_MIN_SIZE, 10)
    .set(Keys.STRING_ALLOW_EMPTY, true)
    .set(Keys.SEED, 12345L) // seed is of type long (note the 'L')
    .lock();

Person person = Instancio.of(Person.class)
    .withSettings(overrides)
    .create();
```

!!! attention ""
    <lnum>2</lnum> The {{Keys}} class provides static fields for all the keys supported by Instancio.<br/>
    <lnum>5</lnum> The `lock()` method makes the settings instance immutable. This is an optional method call.
    It can be used to prevent modifications if settings are shared across multiple methods or classes.<br/>
    <lnum>8</lnum> The passed in settings instance will override default settings.

!!! attention "Range settings auto-adjust"
    When updating range settings, such as `COLLECTION_MIN_SIZE` and `COLLECTION_MAX_SIZE`,
    range bound is auto-adjusted if the new minimum is higher than the current maximum, and vice versa.


The {{Keys}} class defines a _property key_ for every key object, for example:

- `Keys.COLLECTION_MIN_SIZE` -> `"collection.min.size"`
- `Keys.STRING_ALLOW_EMPTY`  -> `"string.allow.empty"`

Using these property keys, configuration values can also be overridden using a properties file.


## Overriding Settings Using a Properties File

Default settings can be overridden using `instancio.properties`.
Instancio will automatically load this file from the root of the classpath.
The following listing shows all the property keys that can be configured.

```properties linenums="1" title="Sample configuration properties" hl_lines="1 4 10 26 27 32 36 49"
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
max.depth=100000
mode=STRICT
hint.after.generate=POPULATE_NULLS_AND_DEFAULT_PRIMITIVES
overwrite.existing.values=true
assignment.type=FIELD
on.set.field.error=IGNORE
on.set.method.error=ASSIGN_FIELD
on.set.method.not.found=ASSIGN_FIELD
setter.style=SET
seed=12345
short.max=10000
short.min=1
short.nullable=false
string.allow.empty=false
string.field.prefix.enabled=false
string.max.length=10
string.min.length=3
string.nullable=false
subtype.java.util.Collection=java.util.ArrayList
subtype.java.util.List=java.util.ArrayList
subtype.java.util.Map=java.util.HashMap
subtype.java.util.SortedMap=java.util.TreeMap
```

!!! attention ""
    <lnum>1,10,26-27</lnum> The `*.elements.nullable`, `map.keys.nullable`, `map.values.nullable` specify whether Instancio can generate `null` values for array/collection elements and map keys and values.<br/>
    <lnum>4</lnum> The other `*.nullable` properties specifies whether Instancio can generate `null` values for a given type.<br/>
    <lnum>32</lnum> Specifies the mode, either `STRICT` or `LENIENT`. See [Selector Strictness](#selector-strictness).<br/>
    <lnum>36</lnum> Specifies a global seed value.<br/>
    <lnum>49</lnum> Properties prefixed with `subtype` are used to specify default implementations for abstract types, or map types to subtypes in general.
    This is the same mechanism as [subtype mapping](#subtype-mapping), but configured via properties.


## Settings Precedence

Instancio layers settings on top of each other, each layer overriding the previous ones.
This is done in the following order:

1. `Settings.defaults()`
1. Settings from `instancio.properties`
1. Settings injected using `@WithSettings` annotation when using `InstancioExtension` (see [Settings Injection](#settings-injection))
1. Settings supplied using the builder API's {{withSettings}} method

In the absence of any other configuration, Instancio uses defaults as returned by `Settings.defaults()`. If `instancio.properties` is found at the root of the classpath, it will override the defaults. Finally, settings can also be overridden at runtime using `@WithSettings` annotation or {{withSettings}} method. The latter takes precedence over everything else.

# JUnit Jupiter Integration

Instancio supports JUnit 5 via the {{InstancioExtension}} and can be used in combination with extensions from other testing frameworks.
The extension adds a few useful features, such as

- the ability to use {{InstancioSource}} with `@ParameterizedTest` methods,
- injection of custom settings using {{WithSettings}},
- and most importantly support for reproducing failed tests using the {{Seed}} annotation.

## Reproducing Failed Tests

Since using Instancio validates your code against random inputs on each test run, having the ability to reproduce a failed tests with previously generated data becomes a necessity.
Instancio supports this use case by reporting the seed value of a failed test in the failure message using JUnit's `publishReportEntry` mechanism.

### Seed Lifecycle in a JUnit Jupiter Test

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
        .set(Keys.COLLECTION_MIN_SIZE, 10);

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
        .set(Keys.COLLECTION_MIN_SIZE, 10);

    @Test
    void overrideInjectedSettings() {
        Person person = Instancio.of(Person.class)
            .withSettings(Settings.create()
                .set(Keys.COLLECTION_MAX_SIZE, 3))
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

Using the {{InstancioSource}} annotation it is possible to have arguments provided directly to a `@ParameterizedTest` test method.
This works with a single argument and multiple arguments, each class representing one argument.

!!! warning "Using `@ParameterizedTest` requires the `junit-jupiter-params` dependency."
    See <a href="https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-setup" target="_blank">JUnit documentation for details</a>.

``` java linenums="1" title="Using @InstancioSource with @ParameterizedTest"
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @ParameterizedTest
    @InstancioSource
    void singleArgument(Person person) {
        // snip...
    }

    @ParameterizedTest
    @InstancioSource
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
