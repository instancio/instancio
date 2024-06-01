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
And it aims to do so with as little code as possible to keep the tests concise.

Another goal of Instancio is to make the tests more dynamic.
Since each test run is against random values, the tests become alive.
They cover a wider range of inputs, which might help uncover bugs that may have gone unnoticed with static data.
In many cases, the random nature of the data also removes the need for parameterising test methods.

Finally, Instancio aims to provide reproducible data.
It uses a consistent seed value for each object graph it generates.
Therefore, if a test fails against a given set of inputs, Instancio supports re-generating the same data set in order to reproduce the failed test.


# Instancio Basics

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
    .ignore(field(Person::getAge))
    .toModel();

Person personWithoutAgeAndAddress = Instancio.of(personModel)
    .ignore(field(Person::getAddress))
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

Collections can be created using one of the following methods.
These methods create a collection of random size, between 2 and 6 elements, inclusive:

``` java linenums="1" title="Collections API: using Class"
Instancio.createList(Class<T> elementType)
Instancio.createSet(Class<T> elementType)
Instancio.createMap(Class<K> keyType, Class<V> valueType)
```

In addition, there is a builder API for creating a collection of a specified size.
The builder API also supports customising properties of collection elements.

``` java linenums="1" title="Collections builder API: using Class"
Instancio.ofList(Class<T> elementType).create()
Instancio.ofSet(Class<T> elementType).create()
Instancio.ofMap(Class<K> keyType, Class<V> valueType).create()
```

If the element is a generic type, the following methods can be used instead:

``` java linenums="1" title="Collections builder API: using TypeToken"
Instancio.ofList(TypeTokenSupplier<T> elementType).create()
Instancio.ofSet(TypeTokenSupplier<T> elementType).create()
Instancio.ofMap(TypeTokenSupplier<K> keyType, TypeTokenSupplier<V> valueType).create()
```

In addition, `ofList()` and `ofSet()` can be used to create collections
from models:

``` java linenums="1" title="Collections builder API: using Model"
Instancio.ofList(Model<T> elementModel).create()
Instancio.ofSet(Model<T> elementModel).create()
```

```java linenums="1" title="Examples"
List<Person> list = Instancio.createList(Person.class);

List<Person> list = Instancio.ofList(Person.class).size(10).create();

List<Pair<String, Integer>> list = Instancio.ofList(new TypeToken<Pair<String, Integer>>() {}).create();

Map<UUID, Address> map = Instancio.ofMap(UUID.class, Address.class).size(3)
    .set(field(Address::getCity), "Vancouver")
    .create();

// Create from a model
Model<Person> personModel = Instancio.of(Person.class)
    .ignore(field(Person::getAge))
    .toModel();

Set<Person> set = Instancio.ofSet(personModel).size(5).create();
```

Specifying the collection size is optional.
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
    .ignore(all(field(Person::getAge), field(Person::getAddress)))
    .stream()
    .limit(3)
    .collect(Collectors.toMap(Person::getUuid, Function.identity()));
```

!!! warning "Since returned streams are infinite, `limit()` _must_ be called to avoid an infinite loop."

### Creating Blank Objects

!!! info "This is an experimental API available since version `4.7.0`"

In addition to creating fully-populated objects, Instancio provides
an API for creating blank objects using the following methods:

``` java title="Shorthand method"
Instancio.createBlank(Class<T> type)
```

``` java title="Builder API"
Instancio.ofBlank(Class<T> type).create()
```

Blank objects have value fields (such as strings, numbers, dates) set to `null`
and nested POJO references initialised to blank POJOs.
The following is a simple example that assumes a `Person` class with
a `name` and `address` fields:

``` java linenums="1" title="Examples of creating blank objects"
Person person = Instancio.createBlank(Person.class);

// Output:
// Person[name=null, address=Address[street=null, city=null, country=null]]

Person person = Instancio.ofBlank(Person.class)
    .set(field(Address::getCountry), "Canada")
    .create();

// Output:
// Person[name=null, address=Address[street=null, city=null, country=Canada]]
```

See the documentation for [`setBlank(TargetSelector)`](#using-setblank) method
for more details on blank objects.

### Creating Simple Values

Instancio provides the `Gen` class for generating simple value types such as strings, numbers, dates, and so on.
This class can generate:

- a single value using `get()` method
- a list of values using the `list(int)` method

```java title="Generate a single value"
URL url = Gen.net().url().get();

String randomChoice = Gen.oneOf("foo", "bar", "baz").get();
```

```java title="Generate a list of values"
List<LocalDate> pastDates = Gen.temporal().localDate().past().list(5);

List<String> uuids = Gen.text().uuid().upperCase().withoutDashes().list(5);
```

!!! info "See [Built-in Generators](#built-in-generators) for a list of available generators"

## Selectors

Selectors are used to target fields and classes, for example in order to customise generated values.
Instancio supports different types of selectors, all of which implement the {{TargetSelector}} interface.
These types are:

- [regular selectors](#regular-selectors)
- [method reference selector](#method-reference-selector)
- [predicate selectors](#predicate-selectors)
- [convenience selectors](#convenience-selectors)
- [setter selectors](#setter-selectors)

All of the above can be created using static methods from the {{Select}} class.

#### Regular selectors

Regular selectors are for precise matching: they can only match a single field or a single type.

``` java linenums="1"
Select.field(String fieldName)
Select.field(Class<?> declaringClass, String fieldName)
Select.all(Class<?> type)
```
!!! attention ""
    <lnum>1</lnum> Selects the field by name, declared in the class being created.<br/>
    <lnum>2</lnum> Selects the field by name, declared in the specified class.<br/>
    <lnum>3</lnum> Selects the specified class, including fields and collection elements of this type.<br/>

```java title="Examples"
Select.field(Person.class, "name") // Person.name
Select.all(Set.class)
```

`Select.field()` is matched based on the exact field name.
If a field with the specified name does not exist, an error will be thrown.
`Select.all()` is matched using `Class` equality, therefore matching does not include subtypes.

#### Method reference selector

This selector uses method references to match fields.

``` java linenums="1"
Select.field(GetMethodSelector<T, R> methodReference)
```

```java title="Example"
Select.field(Person::getName)
```

Internally, method reference is converted to a regular field selector,
equivalent to `Select.field(Class<?> declaringClass, String fieldName)`.
This is done by mapping the method name to the corresponding field name.
The mapping logic supports the following naming conventions:

 - Java beans - where getters are prefixed with `get` and, in case of booleans, `is`.
 - Java record - where method names match field names exactly.

For example, all the following combinations of field and method names are supported:

|Method name  |Field name  | Example                                                            |
|-------------|------------|--------------------------------------------------------------------|
|`getName()`  | `name`     | `field(Person::getName)`  -&gt; `field(Person.class, "name")`      |
|`name()`     | `name`     | `field(Person::name)`  -&gt; `field(Person.class, "name")`         |
|`isActive()` | `active`   | `field(Person::isActive)`  -&gt; `field(Person.class, "active")`   |
|`isActive()` | `isActive` | `field(Person::isActive)`  -&gt; `field(Person.class, "isActive")` |

For methods that follow other naming conventions, or situations where no method is available, regular field selectors can be used instead.

!!! info "Regular selector definition"
    From here on, the definition of *regular selectors* also includes method reference selectors.

##### Kotlin method reference selector

Since Instancio does not include Kotlin dependencies,
the method reference selector described above is only supported for Java classes.
If you use Kotlin, a similar selector can be implemented as a simple utility class shown below.
This sample assumes that the property has a non-null backing `javaField`:

``` kotlin title="Sample implementation method reference selector for Kotlin" linenums="1"
class KSelect {
    companion object {
        fun <T, V> field(property: KProperty1<T, V>): TargetSelector {
            val field = property.javaField!!
            return Select.field(field.declaringClass, field.name)
        }
    }
}

// Usage: KSelect.field(SamplePojo::value)
```

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
    <lnum>1</lnum> For combining multiple selectors.<br/>
    <lnum>2</lnum> Equivalent to `all(String.class)`.<br/>
    <lnum>3</lnum> Equivalent to `all(all(int.class), all(Integer.class))`.<br/>
    <lnum>4</lnum> Builder for constructing `Predicate<Field>` selectors.<br/>
    <lnum>5</lnum> Builder for constructing `Predicate<Class<?>>` selectors.<br/>
    <lnum>6</lnum> Selects the root, that is, the object being created.<br/>

!!! info "The `allXxx()` methods such as `allInts()`, are available for all core types."

- **`Select.all(GroupableSelector... selectors)`**

This method can be used for grouping multiple selectors, allowing for more concise code as shown below.

``` java
all(field(Address::getCity),
    field(Address.class, "postalCode"),
    all(Phone.class))
```

- **`Select.fields()`** and **`Select.types()`**

These selectors provide a builder API for constructing predicate selectors. 
For example, the following selector matches `Long` fields annotated with `@Id`

``` java
Select.fields().ofType(Long.class).annotated(Id.class)
```

which is equivalent to using the following predicate:

``` java
Select.fields(f -> f.getType() == Long.class && f.getDeclaredAnnotation(Id.class) != null)
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

#### Setter selectors

!!! info "Setter selectors are available since version `4.0.0`"

Setter selectors allow targeting setter methods.
These selectors can only be used if [Method Assignment](#assignment-settings) is enabled.

``` java linenums="1"
Select.setter(String methodName)
Select.setter(Class<?> declaringClass, String methodName)
Select.setter(Class<?> declaringClass, String methodName, Class<?> parameterType)
```
!!! attention ""
    <lnum>1</lnum> Selects the setter by name, declared in the class being created.<br/>
    <lnum>2</lnum> Selects the setter by name, declared in the specified class.<br/>
    <lnum>3</lnum> Selects the setter by name and parameter type, declared in the specified class (for overloaded methods).<br/>

!!! warning "Parameter type must be specified for overloaded methods."
    Omitting the parameter type will lead to undefined behaviour.

```java title="Examples"
Select.setter(Person.class, "setName")
Select.setter(Pojo.class, "setValue", String.class) // Pojo.setValue(String)
Select.setter(Pojo.class, "setValue", Long.class)   // Pojo.setValue(Long)
```

In addition, a setter can be selected using the method reference selector:

``` java linenums="1"
Select.setter(SetMethodSelector<T, U> methodReference)
```

```java title="Example"
Select.setter(Person::setName)
```

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

In the following example, the city will also be set to "bar" because the predicate `fields()` selector
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

In the case of regular selectors, if the two are identical, the last selector simply
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
which would cause an unused selector error.
Therefore `lenient()` mode must be enabled to prevent the error
(see [Selector Strictness](#selector-strictness)).

### Selector Scopes

Selectors provide the `within(Scope... scopes)` method for fine-tuning the targets selectors should be applied to.
Instancio supports two types of scope:

- Class-level scope: narrows down a selector to the specified class.
- Field-level scope: narrows down a selector to the specified field of the target class.
- Predicate scope: narrows down a selector using a field or class predicate.

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
Additional examples are provided below.

#### Creating scopes

Scopes can be created using:

- `Select.scope()` static methods in the {{Select}} class
- `Selector.toScope()` method provided by regular selectors
- `PredicateSelector.toScope()` method provided by predicate selectors

The first approach requires specifying the target class and, for field-level scopes, the name of the field:

``` java linenums="1"
Select.scope(Class<?> targetClass)
Select.scope(Class<?> targetClass, String field)
Select.scope(GetMethodSelector<T, R> methodReference)
Select.scope(PredicateSelector selector)
```

``` java title="Examples"
Select.scope(Phone.class);
Select.scope(Person.class, "homeAddress");
Select.scope(Person::getHomeAddress);
Select.scope(type -> type == Address.class);
```

The second approach is to create scopes from selectors using the `toScope()` method.

``` java linenums="1"
Select.all(Class<T> targetClass).toScope()
Select.field(Class<T> targetClass, String field).toScope()
Select.field(GetMethodSelector<T, R> methodReference).toScope()
Select.fields(Predicate<Field> predicate).toScope()
Select.types(Predicate<Class> predicate).toScope()
```

``` java title="Examples"
Select.all(Phone.class).toScope();
Select.field(Person.class, "homeAddress").toScope();
Select.field(Person::getHomeAddress).toScope();
Select.fields(field -> field.getName().equals("id")).toScope();
Select.types(type -> type == Address.class).toScope();
```

#### Examples of using scopes

To start off, without using scopes we can set all strings to the same value.
For example, the following snippet will set each string field of each class to "foo".

``` java linenums="1" title="Set all strings to &quot;Foo&quot;"
Person person = Instancio.of(Person.class)
    .set(allStrings(), "foo")
    .create();
```

Using `within()` we can narrow down the scope of the `allStrings()` selector as shown in the following examples.
For brevity, `Instancio.of(Person.class)` is omitted.


``` java title="Set all strings in all Address instances; this includes Phone instances as they are contained within addresses"
allStrings().within(scope(Address.class))
```

``` java title="Set all strings contained within lists (matches all Phone instances in our example)"
allStrings().within(scope(List.class))
```

``` java title="Set all strings in Person.homeAddress address object"
allStrings().within(scope(Person.class, "homeAddress"))
```

Using `within()` also allows specifying multiple scopes. Scopes must be specified top-down, starting from the outermost to the innermost.

``` java title="Set all strings of all Phone instances contained within Person.workAddress field"
allStrings().within(scope(Person::getWorkAddress), scope(Phone.class))
```

The `Person.workAddress` object contains a list of phones, therefore `Person.workAddress` is the outermost scope and is specified first.
`Phone` class is the innermost scope and is specified last.

The final examples illustrate the creation of scope objects from regular selectors.
The following examples are equivalent to each other:

``` java title="Equivalent ways of creating scopes based on field"
allStrings().within(scope(Person.class, "homeAddress"))

allStrings().within(scope(Person::getHomeAddress))

allStrings().within(field(Person.class, "homeAddress").toScope())

allStrings().within(field(Person::getHomeAddress).toScope())
```

``` java title="Equivalent ways of creating scopes based on class"
allStrings().within(scope(Person.class))

allStrings().within(all(Person.class).toScope())
```

### Selector Depth

Regular and predicate selectors can also be narrowed down by specifying target's depth.
Both selector types allow specifying depth as an integer value:

``` java linenums="1"
Select.all(Class<T> targetClass).atDepth(int depth)
Select.field(Class<T> targetClass, String field).atDepth(int depth)
Select.field(GetMethodSelector<T, R> methodReference).atDepth(int depth)

Select.fields(Predicate<Field> fieldPredicate).atDepth(int depth)
Select.types(Predicate<Class> typePredicate).atDepth(int depth)
```

In addition, predicate selectors also support specifying depth as a predicate:

``` java linenums="1"
Select.fields(Predicate<Field> fieldPredicate).atDepth(Predicate<Integer> depthPredicate)
Select.types(Predicate<Class> typePredicate).atDepth(Predicate<Integer> depthPredicate)
```

We will use the following class structure for the examples:

```java
Depth       Class
----------------------
  0         Root
            /  \
  1        A    B
              / | \
  2          A  A  C
                  / \
  3              A   D
                      \
  4                    A
```

where the classes are defined as:

```java
record Root(A a, B b) {}
record A(String value) {}
record B(A a1, A a2, C c) {}
record C(A a, D d) {}
record D(A a) {}
```

In the first few examples, we will target class `A` at different levels:

``` java title="Select the <code>A</code> at depth 1"
Root root = Instancio.of(Root.class)
    .set(all(A.class).atDepth(1), new A("Hello!"))
    .create();

=> Root[a=A[value="Hello!"], b=B[a1=A[value="BRHD"], a2=A[value="AVBMJRP"], c=C[a=A[value="PZK"], d=D[a=A[value="AQVXCT"]]]]]
```

Similar to the above, but omitting `Instancio.of()` for brevity:

``` java title="Select the two <code>A</code> nodes at depth 2"
all(A.class).atDepth(2)

=> Root[a=A[value="FNPI"], b=B[a1=A[value="Hello!"], a2=A[value="Hello!"], c=C[a=A[value="IDLOM"], d=D[a=A[value="QXPW"]]]]]
```

``` java title="Select the <code>A</code> nodes at depths 3 and 4 (and beyond, if any)"
types().of(A.class).atDepth(depth -> depth > 2)

=> Root[a=A[value="MWAASZU"], b=B[a1=A[value="ODSRTG"], a2=A[value="TDG"], c=C[a=A[value="hello!"], d=D[a=A[value="hello!"]]]]]
```

``` java title="Select all (four) <code>A</code> nodes reachable from <code>B</code>"
all(A.class).within(scope(B.class))

=> Root[a=A[value="GNDUXU"], b=B[a1=A[value="hello!"], a2=A[value="hello!"], c=C[a=A[value="hello!"], d=D[a=A[value="hello!"]]]]]
```

The next example is targeting `allStrings()`, therefore the value is being set to `"Hello!"` instead of `new A("Hello!")`.
This snippet targets all strings that are reachable from class `A`, but only if class `A` is at depth `3` or greater.

``` java title="Select all Strings reachable from <code>A</code> nodes at depth 3 or greater" linenums="1" hl_lines="2"
Root root = Instancio.of(Root.class)
    .set(allStrings().within(all(A.class).atDepth(3).toScope()), "Hello!")
    .create();

=> Root[a=A[value="SERWVQV"], b=B[a1=A[value="PTF"], a2=A[value="CHZP"], c=C[a=A[value="hello!"], d=D[a=A[value="hello!"]]]]]
```
!!! attention ""
    <lnum>2</lnum> When a selector `atDepth(N)` is converted to `toScope()`, the selection matches any target at depth `N` or greater.


The final example is targeting the field `A.value` but only within the `a1` field of class `B`:

``` java title="Select <code>A.value</code> of the <code>a1</code> field"
field(A::value).within(field(B::a1).toScope())

=> Root[a=A[value="DBOS"], b=B[a1=A[value="hello!"], a2=A[value="KFBWJL"], c=C[a=A[value="VLTNXF"], d=D[a=A[value="CDV"]]]]]
```


### Selector Strictness

#### Strict Mode

Instancio supports two modes: strict and lenient, an idea inspired by Mockito's highly useful strict stubbing feature.

In strict mode, unused selectors will trigger an error. In lenient mode, unused selectors are simply ignored.
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
even with a simple class like the above. It gets trickier when generating more complex classes.
Strict mode helps reduce this type of error.

##### Simplify fixing tests after refactoring

Refactoring always causes tests to break to some degree. As classes and fields get reorganised and renamed,
tests need to be updated to reflect the changes. Assuming there are existing tests utilising Instancio,
running tests in strict mode will quickly highlight any problems in data setup caused by refactoring.

##### Keep test code clean and maintainable

Last but not least, it is important to keep tests clean and maintainable. Test code should be treated
with as much care as production code. Keeping the tests clean and concise makes them easier to maintain.

#### Lenient Mode

While strict mode is highly recommended, Instancio provides a few options to disable
checking for unused selectors. The following are the possible options, with the least recommended last: 

1. At selector level, by marking an individual selector as lenient
2. At object level, by treating all selectors as lenient
3. Via {{Settings}}
4. Globally via `instancio.properties`

The first option is shown below, where the selector is marked as `lenient()`:

```java title="Marking an individual selector as lenient"
Person person = Instancio.of(Person.class)
    .set(fields().named("someFieldThatMayNotExist").lenient(), "some value")
    .create();
```

The second option is to enable lenient mode for all selectors:

``` java title="Setting lenient mode using the builder API"
Person person = Instancio.of(Person.class)
    .set(fields().named("someFieldThatMayNotExist"), "some value")
    .set(fields().named("anotherFieldThatMayNotExist"), "another value")
    .lenient()
    .create();
```

Alternatively, lenient mode can be enabled using `Settings`:

``` java title="Setting lenient mode using <code>Settings</code>"
Settings settings = Settings.create()
    .set(Keys.MODE, Mode.LENIENT);

Person person = Instancio.of(Person.class)
    .withSettings(settings)
    // snip... same selectors as above
    .create();
```

Lastly, lenient mode can also be enabled globally using [`instancio.properties`](#overriding-settings-using-a-properties-file).
This is the least recommended option.

``` java title="Setting lenient mode using <code>instancio.properties</code>"
mode=LENIENT
```

## Customising Objects

Properties of an object created by Instancio can be customised using

- [`generate()`](#using-generate)
- [`set()`](#using-set)
- [`supply()`](#using-supply)

methods defined in the {{InstancioApi}} class.


### Using `generate()`

The `generate()` method provides access to built-in generators for core types from the JDK,
such as strings, numeric types, dates, arrays, collections, and so on.
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

!!! info "See [Built-in Generators](#built-in-generators) for a list of available generators"

In addition, most generators can also map values to a different type.
For example, the following returns generated values as Strings:

``` java linenums="1"
class Foo {
    String dateString;
    String enumString;
}

Instancio.of(Foo.class)
    .generate(field("dateString"), gen -> gen.temporal().localDate().past().asString())
    .generate(field("enumString"), gen -> gen.enumOf(MyEnum.class).as(e -> e.name().toUpperCase()))
    .create();
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

---

#### Summary of methods for customising objects


**Methods** `set(TargetSelector, Object)` and `supply(TargetSelector, Supplier)` are for supplying objects *as is*.
The provided objects are treated as read-only by the engine.
The behaviour of these methods cannot be customised.

- matching selectors will not be applied
- the engine will not modify or populate any fields of the supplied object
- callbacks are *not* invoked on objects provided by these methods

**Method** `supply(TargetSelector, Generator)` is for creating objects using custom `Generator` implementations.
This method offers configurable behaviour via the `AfterGenerate` hint.
By default, the hint is set to `POPULATE_NULLS_AND_DEFAULT_PRIMITIVES`, which implies:

- matching selectors will be applied
- the engine will populate `null` fields and primitive fields containing default values

The default value of the `AfterGenerate` hint can be overridden using `instancio.properties` and {{Settings}}.

!!! info
Callbacks are always invoked on objects created by generators regardless of `AfterGenerate` value.

**Method** `generate(TargetSelector, GeneratorSpecProvider)` is for customising objects created by internal generators.
Such objects include value types (numbers, strings, dates) and data structures (collections, arrays).

- matching selectors are **always** applied
- matching callbacks are **always** invoked

By default, the engine generates non-null values, unless specified otherwise.


### Using `assign()`

!!! info "This is an experimental API available since version `3.0.0`"

The assignment API allows customising an object by passing in one or more `Assignment` objects as a vararg:

```java
InstancioApi<T> assign(Assignment... assignments);
```

Assignments can be created using static methods provided by the {{Assign}} class.
It provides three entry-point methods for creating an assignment:

```java linenums="1"
Assign.given(TargetSelector origin)
Assign.given(TargetSelector origin, TargetSelector destination)
Assign.valueOf(TargetSelector target)
```

Each `Assignment` may have:

- an origin selector - the source of the assignment; must match exactly one value
- destination selector(s) whose target(s) will be assigned a given value
- `Generator` for generating the value to assign
- `Predicate` that must be satisfied by the origin for the assignment to be applied
- `Function` for mapping the value before assigning to destination targets

The three types of expressions can be used for different use-cases described
in the following sections.

#### `Assign.valueOf(target)`

This builder provides two options:

- Assign value directly **to** the `target`
- Assign value **of** the `target` to another selector

The first option is to assign a value directly to the `target` selector using
`set()`, `supply()`, or `generate()` methods as shown in the following example:

```java
Assignment[] assignments = {
    Assign.valueOf(Person::getName).set("Bob"),
    Assign.valueOf(Person::getAge).generate(gen->gen.ints().range(1,100))
};

Person person = Instancio.of(Person.class)
    .assign(assignments)
    .create();
```

The second option is to set the value of the `target` to another selector.
For example, the following snippet assigns the value of `Person.firstName` to `Person.preferredName`:

```java linenums="1"
Assignment preferredName = Assign.valueOf(Person::getFirstName).to(Person::getPreferredName);

Person person = Instancio.of(Person.class)
    .assign(preferredName)
    .create();
```

This method supports an optional predicate to specify when the assignment should be applied.
In the following example, the preferred name is set to the first name only if the first name starts with "A".
Otherwise, the preferred name will be set to a random value.

```java linenums="1"
Person person = Instancio.of(Person.class)
    .generate(field(Person::getFirstName), gen -> gen.oneOf("Alice", "Alex", "Robert"))
    .assign(valueOf(Person::getFirstName)
            .to(Person::getPreferredName)
            .when((String firstName) -> firstName.startsWith("A")))
    .create()
```

In addition, the expression allows specifying a `Function` for transforming the result.
For example, the following snippet sets the preferred name to the same value as the first name,
unless the first name is *Robert*, in which case the preferred name will be set to *Bob*:

```java linenums="1"
Person person = Instancio.of(Person.class)
    .generate(field(Person::getFirstName), gen -> gen.oneOf("Alice", "Alex", "Robert"))
    .assign(valueOf(Person::getFirstName)
            .to(Person::getPreferredName)
            .as((String firstName) -> "Robert".equals(firstName) ? "Bob" : firstName))
    .create()
```

#### `Assign.given(origin)`

This expression allows setting different destination selectors with different values
based on a given origin and predicate. The following snippet sets
cancellation-related order data when the generated order status is `CANCELLED`:

```java linenums="1"
List<Order> orders = Instancio.ofList(Order.class)
    .assign(Assign.given(Order::getStatus)
        .is(OrderStatus.CANCELLED)
        .set(field(Order::getCancellationReason), "Shipping delays")
        .set(field(Order::isRefundIssued), true)
        .generate(field(Order::getCancellationDate), gen -> gen.temporal().instant().past()))
    .create();
```

#### `Assign.given(origin, destination)`

This expression allows mapping predicates to different values
for a given pair of origin and destination selectors:

```java linenums="1"
Person person = Instancio.of(Person.class)
    .assign(Assign.given(field(Address::getCountry), field(Phone::getCountryCode))
        .set(When.isIn("Canada", "USA"), "+1")
        .set(When.is("Italy"), "+39")
        .set(When.is("Poland"), "+48")
        .set(When.is("Germany"), "+49")
        .elseGenerate(gen -> gen.ints().range(1, 999).as(code -> "+" + code)))
    .create();
```

The {{When}} class provides convenience methods for creating predicates.
In addition to `set(predicate, value)` method shown above, this API
also supports `supply()` and `generate()`, as well as the following
optional methods to specify an `else` action if none of the predicates match:

- `elseSet()`
- `elseSupply()`
- `elseGenerate()`

#### Assignment Restrictions

##### Origin selector restrictions

When using assignments, the origin selector must match a single target.
For this reason, the origin does not support selector groups and
primitive/wrapper selectors, such as `allInts()`.

For example, `Assign.given(allStrings())` is acceptable for the following class
because there is only one string that matches the selector:

```java
record MyRecord(String string, Integer number) {}
```

but not for the following classes because `allStrings()` would match more than one string value:

```java
record MyRecord(String string1, String string2, Integer number) {}

record MyRecord(List<String> string, Integer number) {}
```

Convenience primitive/wrapper selectors, such as `allInts()`, are not accepted
by assignments as they are a shorthand for `Select.all(all(int.class). all(Integer.class))`.
Assignment expressions must specify either the primitive, or the wrapper type explicitly:
`Assign.given(all(Integer.class))`.


##### Collection restrictions

Assignments have an additional restriction when used with collections.
If the origin selector of an assignment targets a collection element,
then destination selector(s) must be within the collection.
Assuming we have the following data model:

```java
class Person {
    String name;
    String countryOfCitizenship;
    List<Address> addresses;
}

class Address {
    String street;
    String city;
    String country;
}
```

It is possible to create an assignment based on address `city` and fields `country`:

```java linenums="1"
Person person = Instancio.of(Person.class)
    .generate(field(Address::getCountry), gen -> gen.oneOf("France", "Italy", "Spain"))
    .assign(given(field(Address::getCountry), field(Address::getCity))
        .set(When.is("France"), "Paris")
        .set(When.is("Italy"), "Rome")
        .set(When.is("Spain"), "Madrid"))
    .create();
```

Since `Address` is a collection element, the assignment is scoped to each `Address` instance.
The following usage, on the other hand, is **invalid**:

```java linenums="1" title="Invalid usage"
Person person = Instancio.of(Person.class)
    .generate(field(Address::getCountry), gen -> gen.oneOf("France", "Italy", "Spain"))
    .assign(valueOf(Address::getCountry).to(Person::getCountryOfCitizenship))
    .create();
```

This is because `Address` is a collection element, therefore, `valueOf(Address::getCountry)` matches multiple values.
For this reason, no guarantee is made as to the value that will be assigned to `Person.countryOfCitizenship` field.

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
However, callbacks cannot be used to modify immutable types.

Another property of callbacks is that they are only invoked on non-null objects.
In the following example, all address instances are nullable.
Therefore, a generated address instance may either be `null` or a fully-populated object.
However, if a `null` was generated, the callback will not be invoked.

``` java linenums="1" title="Callbacks are only called on non-null values"
Person person = Instancio.of(Person.class)
    .withNullable(all(Address.class))
    .onComplete(all(Address.class), (Address address) -> {
        // only-called if the generated address is not null
    })
    .create();
```

### Using `filter()`

!!! info "This is an experimental API available since version 4.6.0"

This method can be used to filter generated values using a predicate.
If the predicate evaluates to `false` for a given value, Instancio will generate a new value.
The new value will also be tested against the predicate, and so on.

A simple example is to generate a list of even numbers:

```java title="Example: generate even numbers" linenums="1"
List<Integer> evenNumbers = Instancio.ofList(Integer.class)
    .filter(allInts(), (Integer i) -> i % 2 == 0)
    .create();
```

A more realistic use case is to  ensure that certain fields of a generated
object have unique values. For instance, we may want to generate a list of `Person`
objects with unique string and numeric values. If the `Person` contains numeric IDs,
this will ensure that the generated ID values are distinct across all instances:

```java title="Example: generate an object with unique values" linenums="1" hl_lines="5"
Set<?> generatedValues = new HashSet<>();

List<Person> persons = Instancio.ofList(Person.class)
    .size(100)
    .filter(all(allInts(), allLongs(), allStrings()), generatedValues::add)
    .create();
```
!!! attention ""
    <lnum>5</lnum> Generate distinct ints, longs and strings by rejecting duplicates.<br/>


It should be noted that using the `filter()` method can be inefficient if the probability
of generating a random value that would be rejected by the predicate is high.
This is because a value that does not satisfy the predicate must be generated again.
This results in many values being generated and discarded.

!!! warning "Maximum retries"
    An exception will be thrown if the number of retries to generate a value for a given node exceeds `1000`.

In addition. it is not recommended to use `filter()` with POJOs or collections. For example,
if `isActive()` returns `false` in the following snippet, the entire `User` object will be generated from scratch:

```java title="Not recommended!" linenums="1"
List<User> users = Instancio.ofList(User.class)
    .filter(all(User.class), (User user) -> user.isActive())
    .create();
```

Therefore, customising objects using other APIs, such as [`generate()`](#using-generate)
should be preferred over `filter()`, if possible.

### Using `setBlank()`

!!! info "This is an experimental API available since version 4.7.0"

This method can be used to initialise certain parts of an object to be blank using selectors.
Its behaviour is the same as the methods below (which can be used for creating a blank **root object**):

- `Instancio.createBlank(Class<T>)`
- `Instancio.ofBlank(Class<T>)`

Blank objects have the following properties:

- value fields (such as strings, numbers, dates) are `null`
- arrays, collections, and maps are empty
- references to POJOs are initialised to blank objects

For example, assuming the `Person` class below (getters and setters omitted):

```java linenums="1"
class Person {
    String name;
    LocalDate dateOfBirth;
    List<Phone> phoneNumbers;
    Address address;
}
```

The following snippet

```java linenums="1" hl_lines="1"
Person person = Instancio.of(Person.class)
    .setBlank(field(Person::getPhoneNumbers))
    .setBlank(all(Address.class))
    .create();
```
!!! attention ""
    <lnum>1</lnum> We use `of(Person.class)` and **not** `ofBlank(Person.class)`, as the latter would create a blank root object.<br/>

will produce a partially blank object, where only the `name` and `dateOfBirth` fields are populated with random values.
The `address` field has been set to a blank object and `phoneNumbers` to an empty `List`:

```
// Person[
//   name=NOBXGV,
//   dateOfBirth=2022-03-18,
//   phoneNumbers=[]
//   address=Address[street=null, city=null, country=null]
// ]
```

Blank objects can be customised further if needed. For example, we can set the country field to "Canada",
generate a collection of blank phones of size `2`, and set the `countryCode` to `+1` as shown below:

```java linenums="1"
Person person = Instancio.of(Person.class)
    .setBlank(field(Person::getPhoneNumbers))
    .setBlank(all(Address.class))
    .generate(field(Person::getPhoneNumbers), gen -> gen.collection().size(2))
    .set(field(Phone::getCountryCode), "+1")
    .set(field(Address::getCountry), "Canada")
    .create();

// Sample output:
// Person[
//   name=GLTJXQM,
//   dateOfBirth=2029-04-06,
//   phoneNumbers=[
//       Phone[countryCode=+1,number=<null>],
//       Phone[countryCode=+1,number=<null>]]
//   address=Address[street=null, city=null, country=Canada]
// ]
```


### Using `ignore()`

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

#### Precedence of `ignore()`

The `ignore()` method has higher precedence than other methods. For example, in the following snippet
specifying `ignore(all(LocalDateTime.class))` but supplying a value for the `lastModified` field
will actually generate a `lastModified` with a `null` value.


``` java linenums="1" title="Example"
Person person = Instancio.of(Person.class)
    .ignore(all(LocalDateTime.class))
    .supply(field(Person::getLastModified), () -> LocalDateTime.now())
    .create();
```

#### Usage of `ignore()` with Java records

When `ignore()` is used to target one of the required arguments of a `record` constructor,
then a default value for the ignored type will be generated.

``` java linenums="1" title="Example"
record PersonRecord(String name, int age) {}

PersonRecord person = Instancio.of(PersonRecord.class)
    .ignore(allInts())
    .ignore(allStrings())
    .create();

// will produce: PersonRecord[name=null, age=0]
```

### Nullable Values

By default, Instancio generates non-null values.
There are cases where this behaviour may need to be relaxed, for example,
to verify that a piece of code does not fail in the presence of certain `null` values.
There are a few ways to specify that values can be nullable which can broadly be grouped into two categories:

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

Some built-in generators also support marking values as nullable using the `nullable()` method.
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

Subtype mapping allows mapping a type to its subtype. This can be used for:

- specifying an implementation class for an abstract class or interface
- testing behaviour using different implementations

By default, Instancio does not resolve the implementation class of an abstract type.
The only exceptions to this are JDK classes, such `List`, `Map`, `CharSequence`, etc,
which default to `ArrayList`, `HashMap`, and `String`, respectively.

For user-defined abstract types, the implementation class must be specified via the API.
If not specified:

- a `null` value will be generated where the abstract type is a field
- an empty collection will be generated where the abstract type is collection element
- an exception will be thrown if root type is abstract and no subtype is specified

### Specifying Subtypes

The mapping can be specified using the `subtype()` method provided by {{InstancioApi}}:

``` java
subtype(TargetSelector selector, Class<?> subtype)
```

or the `mapType()` method declared by the {{Settings}} class:

``` java
mapType(Class<?> type, Class<?> subtype)
```

Functionally, the two methods are equivalent. However, one may be preferable over another
depending on the use case.

The `subtype()` method

- has higher precedence than the one offered by `Settings`
- provides more flexibility as it can be applied to any selector target, such as fields

The `Settings.mapType()` method, on the other hand

- can only be mapped based on class
- is reusable since an instance of `Settings` can be shared across multiple test methods
- allows subtypes to be specified via `instancio.properties` and applied automatically

The following is an example of using the `subtype()` method:

``` java linenums="1" hl_lines="2 4"
Person person = Instancio.of(Person.class)
    .subtype(all(all(Collection.class), all(Set.class)), TreeSet.class)
    .subtype(field(Person::getPet), Cat.class)
    .subtype(all(Address.class), AddressImpl.class)
    .create();
```
!!! attention ""
    <lnum>2</lnum> Group selector can be used as long as the subtype is valid for all group members.<br/>
    <lnum>4</lnum> Assuming `Address` is the superclass of `AddressImpl`.

The same can be specified using `Settings`:

``` java linenums="1" hl_lines="2"
Settings settings = Settings.create()
    .mapType(Pet.class, Cat.class)
    .mapType(Collection.class, TreeSet.class)
    .mapType(Set.class, TreeSet.class)
    .mapType(Address.class, AddressImpl.class);

Person person = Instancio.of(Person.class)
    .withSettings(settings)
    .create();
```
!!! attention ""
    <lnum>2</lnum> Note that this is not exactly equivalent to `subtype(field(Person::getPet), Cat.class)`,
    since the field selector is a more specific target.

Finally, when specifying the mapping via `instancio.properties`:

``` properties linenums="1"
subtype.java.util.Collection=java.util.TreeSet
subtype.java.util.Set=java.util.TreeSet
subtype.com.example.Pet=com.example.Cat
subtype.com.example.Address=com.example.AddressImpl
```

subtypes are resolved automatically:

``` java linenums="1"
Person person = Instancio.create(Person.class);

assertThat(person.getPet()).isExactlyInstanceOf(Cat.class)
```

In addition to specifying subtypes using the API or properties file, subtypes can be resolved
automatically by implementing the [`InstancioServiceProvider`](#instancio-service-provider-interface) interface.

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

This approach reduces duplication and simplifies data setup, especially for complex classes with many fields and relationships.
More details on the benefits of using models, including a sample project, are provided in the article
[Creating object templates using Models](/articles/creating-object-templates-using-models/).

### Using `setModel()`

!!! info "This is an experimental API available since version 4.4.0"

The method `setModel(TargetSelector, Model)` allows applying a model to another object using a selector.

To illustrate with an example, we will assume the following classes:

```java
record Foo(String value) {}
record Container(Foo fooA, Foo fooB) {}
```

Given a model of `Foo`:

```java linenums="1"
Model<Foo> fooModel = Instancio.of(Foo.class)
    .set(field(Foo::value), "foo")
    .toModel();
```

The model can be applied to a specific `Foo` field declared by the `Container`:

```java linenums="1"
Container container = Instancio.of(Container.class)
    .setModel(field(Container::fooA), fooModel)
    .create();

// Sample output: Container[fooA=Foo[value="foo"], fooB=Foo[value="ANBQNR"]]
```

`setModel()` works by applying selectors defined within the model to the target object.
In doing so, it narrows down the scope of selectors defined in the model, such as `field(Foo::value)`,
to the model's target `field(Container::fooA)`, as shown in this diagram:

<img src="/assets/setmodel-selector-scope.svg" alt="Selector scope applied by setModel()">

In other words, the `Container` creation example is equivalent to:

```java linenums="1" hl_lines="2"
Container container = Instancio.of(Container.class)
    .set(field(Foo::value).within(scope(Container::fooA)), "foo")
    .create();
```

`setModel()` works for all Instancio API methods that accept `TargetSelector` as an argument,
such as `assign()`, `generate()`, `ignore()`, and so on. However, the following properties of the `Model`
are **not** applied to the target object:

- `Settings`
- `lenient()` mode
- custom seed value, if any

#### Overriding Selectors Defined by the `Model`

When creating an object, it is possible to override selectors defined within the model.
Building on the previous example, we can override `field(Foo::value)` as follows:

```java linenums="1" hl_lines="3"
Container container = Instancio.of(Container.class)
    .setModel(field(Container::fooA), fooModel)
    .set(field(Foo::value).within(scope(Container::fooA)), "bar")
    .create();

// Sample output: Container[fooA=Foo[value="bar"], fooB=Foo[value="ORVQFJF"]]
```
!!! attention ""
    <lnum>3</lnum> This selector replaces the original selector defined by the `Model`.<br/>

In this example, the overriding selector is the same as the model's selector (including the scope).
If instead of:

```java
.set(field(Foo::value).within(scope(Container::fooA)), "bar")
```

we specify:

```java
.set(field(Foo::value), "bar")
```

Then the output will be:

```
Container[fooA=Foo[value="bar"], fooB=Foo[value="bar"]]
```

In addition, the original selector defined within the model will trigger an [unused selector](#selector-strictness) error.
In such cases, the model's selector can be marked as `lenient()`:

```java linenums="1" hl_lines="2"
Model<Foo> fooModel = Instancio.of(Foo.class)
    .set(field(Foo::value).lenient(), "foo")
    .toModel();
```

The reason for the error is that there are now two different selectors that match the same node `Foo.value`.
This can be verified by calling `verbose()`, which will output all the selectors and matching nodes:

```java linenums="1" hl_lines="4"
Container container = Instancio.of(Container.class)
    .setModel(field(Container::fooA), fooModel)
    .set(field(Foo::value).within(scope(Container::fooA)), "bar")
    .verbose()
    .create();
```

Will output:

```hl_lines="4 7"
Selectors and matching nodes, if any:

 -> Method: generate(), set(), supply()
    - field(Foo, "value")
       \_ Node[Foo.value, depth=2, type=String]

    - field(Foo, "value").within(scope(Container, "fooA")).lenient()
       \_ Node[Foo.value, depth=2, type=String]

```

The output confirms that there are two different selectors matching the node `Foo.value`,
therefore selector with the lowest precedence will the trigger unused selector error unless it is marked as `lenient()`.

In summary, when a `Model` is provided to the `setModel()` method, the selectors defined
within the model will be subject to the usual [selector precedence rules](#selector-precedence).

## Custom Generators

Every type of object Instancio generates is through an implementation of the `Generator` interface.
A number of internal generators are included out of the box for creating strings, numeric types, dates, collections, and so on.
Custom generators can also be defined to satisfy certain use cases:

- Generating types not supported out of the box, for example from third-party libraries such as Guava.

- Creating pre-initialised domain objects. Some domain objects require to be constructed in a certain state to be valid.
To avoid duplicating the construction logic across different tests, it can be encapsulated by a custom generator that can be reused across the project.

- Distributing generators as a library that can be shared across projects.


The `Generator` is a functional interface with a single abstract method `generate(Random)`:


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

In short, the `AfterGenerate` enum defines what should happen to an object returned by a generator.
The exact semantics of `AfterGenerate` actions vary depending on the type of object.
Different rules are applied to POJOs, arrays, collections, and records.
For example, if a generator returns an instance of a `record`, the returned instance
cannot be modified regardless of the `AfterGenerate` hint.

The following describes the actions as they apply to POJOs:

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

When defining custom array or collection generators, the following hints can also be used:

- {{ArrayHint}}
- {{CollectionHint}}
- {{MapHint}}

#### Custom Generator Example

To illustrate the above with an example, consider the following generator that creates an instance of a `Phone`:

```java linenums="1"
class PhoneGenerator implements Generator<Phone> {

    @Override
    public Phone generate(Random random) {
        int countryCodeLength = random.intRange(1, 3);

        return Phone.builder()
            .countryCode("+" + random.digits(countryCodeLength))
            .number(random.digits(8))
            .build();
    }
}
```

Since no `hints()` are specified, the default `AfterGenerate` action is `POPULATE_NULLS_AND_DEFAULT_PRIMITIVES`.
Therefore, the generated object can be customised using selectors:

```java linenums="1"
Phone phone = Instancio.of(Phone.class)
    .set(field(Phone::getCountryCode), "+55")
    .create();
```

This will produce an object like `Phone[countryCode="+55", number="83703291"]`, where the `number`
is set by the `PhoneGenerator`, and the `countryCode` is overridden by applying the selector.
For certain use cases, it may be necessary to prevent deliberate or accidental modification
of generated objects. In such cases, the generator can include the `DO_NOT_MODIFY` hint as shown below:

```java linenums="1"
class PhoneGenerator implements Generator<Phone> {

    @Override
    public Phone generate(Random random) { /* same as before */ }

    @Override
    public Hints hints() {
        return Hints.afterGenerate(AfterGenerate.DO_NOT_MODIFY);
    }
}
```

In summary, a generator can instantiate an object and instruct the engine
what should be done with the object after `generate()` method returns using
the `AfterGenerate` hint.


!!! warning "Generating `record` objects"
    It should be noted that if a generator returns an instance of a `record`, then the created object cannot
    be modified regardless of the `AfterGenerate` hint. This is due to immutability of records,
    since they cannot be modified after construction.


!!! info "Custom generators can also be specified using the [Instancio Service Provider Interface](#instancio-service-provider-interface)"

## Assignment Settings

Assignment settings control whether values are assigned directly to fields (default behaviour)
or via setter methods. There are a few setting {{Keys}} that control the behaviour.

| `Keys` constant             | Value type             | Default        | Description                                                   |
|-----------------------------|------------------------|----------------|---------------------------------------------------------------|
| `ASSIGNMENT_TYPE`           | `AssignmentType`       | `FIELD`        | Should values be assigned via fields or setters               |
| `SETTER_STYLE`              | `SetterStyle`          | `SET`          | Naming convention used for setters                            |
| `ON_SET_FIELD_ERROR`        | `OnSetFieldError`      | `IGNORE`       | What should happen if field assignment fails                  |
| `ON_SET_METHOD_ERROR`       | `OnSetMethodError`     | `ASSIGN_FIELD` | What should happen if method assignment fails                 |
| `ON_SET_METHOD_NOT_FOUND`   | `OnSetMethodNotFound`  | `ASSIGN_FIELD` | What should happen if a field does not have a matching setter |
| `ON_SET_METHOD_UNMATCHED`   | `OnSetMethodUnmatched` | `IGNORE`       | What should happen if a setter does not have a matching field |
| `SETTER_EXCLUDE_MODIFIER`   | `int`                  | `0` (none)     | Which setters should be ignored based on method modifiers     |
| `OVERWRITE_EXISTING_VALUES` | `boolean`              | `true`         | Should initialised fields be overwritten with random values   |

To enable assignment via methods, `Keys.ASSIGNMENT_TYPE` can be set to `AssignmentType.METHOD`.
This setting only applies to mutable fields because `final` fields cannot have setters.
For non-static `final` fields Instancio will fall back to `AssignmentType.FIELD`.

When method assignment is enabled, Instancio will attempt to resolve setter names from field names using `SETTER_STYLE` setting.
This key's value is the `SetterStyle` enum that supports three naming conventions:

- `SET` - standard setter prefix: `setFoo("value")`
- `WITH` - for example: `withFoo("value")`
- `PROPERTY` - no prefix: `foo("value")`

There might be cases where a setter does not match any field using the configured naming convention.
`ON_SET_METHOD_UNMATCHED` determines what happens in such cases.
The default value is `OnSetMethodUnmatched.IGNORE`, therefore unmatched setters will not be invoked.
The behaviour can be overridden by setting the value to `OnSetMethodUnmatched.INVOKE`.

!!! warning "Special care must be taken when enabling unmatched setters in the presence of [overloaded setters](#overloaded-unmatched-setters)."

`SETTER_EXCLUDE_MODIFIER` specifies whether setters with certain method modifiers should be ignored (by default, there are no exclusions).
For example, using this setting it is possible to instruct Instancio to ignore private and package-private setters.

The remaining `ON_SET_*` keys are used to control error-handling behaviour:

| Key                       | Possible causes                                                                                       |
|---------------------------|-------------------------------------------------------------------------------------------------------|
| `ON_SET_FIELD_ERROR`      | type mismatch, unmodifiable field, access exception                                                   |
| `ON_SET_METHOD_ERROR`     | type mismatch, an exception is thrown by setter (e.g. due to validation)                              |
| `ON_SET_METHOD_NOT_FOUND` | method does not exist, or the name does not conform to the naming convention defined by `SetterStyle` |

All of the above can be set to ignore errors or fail fast by raising an exception.
In addition, both `ON_SET_METHOD_*` settings can be configured to fall back to field assignment in case of an error.

!!! warning "Setting `Keys.ON_SET_FIELD_ERROR` to `OnSetFieldError.IGNORE`"
    An error caused by assigning an incompatible type is considered a user error and is never ignored.<br/><br/>
    For example, attempting to `set(allStrings(), 12345)` will always trigger an error
    regardless of the `ON_SET_FIELD_ERROR` setting.

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

### Unmatched Setters

When `Keys.ASSIGNMENT_TYPE` is set to `METHOD`, Instancio parses fields *and* setters declared by a class.
In addition, it attempts to match each field to the corresponding setter using its name and parameter type.
If a setter does not have a matching field, it is treated as *unmatched*.
This can be illustrated using the following `Person` class.

```java linenums="1"
class Person {
    private final Map<String, String> attributes = new HashMap<>();
    private String name;

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getFavouriteFood() {
        return attributes.get("FAVOURITE_FOOD");
    }

    void setFavouriteFood(String favouriteFood) {
        attributes.put("FAVOURITE_FOOD", favouriteFood);
    }
}
```

The class has the `setName(String)` method that matches the `String name` field. In addition,
it has the `setFavouriteFood(String)` method. This setter is unmatched because it does not
have a corresponding `String favouriteFood` field.

| Field         | Setter                     |                  |
|---------------|----------------------------|------------------|
| `String name` | `setName(String)`          | Matched setter   |
| `-`           | `setFavouriteFood(String)` | Unmatched setter |


#### Using `OnSetMethodUnmatched.INVOKE`

In the first example, we will create an object with the following settings and `verbose()` mode enabled:

```java linenums="1" hl_lines="3-5"
Person person = Instancio.of(Person.class)
    .withSettings(Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE)
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE))
    .verbose()
    .create();
```
!!! attention ""
    <lnum>3</lnum> Assign values via setter methods instead of fields.<br/>
    <lnum>4</lnum> Ignore fields that do not have a setter.<br/>
    <lnum>5</lnum> Invoke unmatched setters.


`verbose()` mode prints the node hierarchy to standard out:

```
<0:Person>
 ├──<1:Person: Map<String, String> attributes>    // Field without a setter
 │   ├──<2:String>  // map key
 │   └──<2:String>  // map value
 ├──<1:Person: String name; setName(String)>      // Field with a setter
 └──<1:Person: setFavouriteFood(String)>          // Setter without a field (unmatched setter)
```

The following is a sample object generated by the snippet:

```
Person(name=OHARWES, attributes={FAVOURITE_FOOD=JRKB})
```

The `attributes` map was not populated with random entries because `ON_SET_METHOD_NOT_FOUND`
was set to `OnSetMethodNotFound.IGNORE`. However, since `ON_SET_METHOD_UNMATCHED` was set to `OnSetMethodUnmatched.INVOKE`,
Instancio invoked the `setFavouriteFood` method passing in a random value.
Therefore, the map contains a `FAVOURITE_FOOD` entry.

Next, we will set `ON_SET_METHOD_NOT_FOUND` to `OnSetMethodNotFound.ASSIGN_FIELD`, keeping everything else the same:

```java linenums="1" hl_lines="4"
Person person = Instancio.of(Person.class)
    .withSettings(Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.ASSIGN_FIELD)
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE))
    .verbose()
    .create();
```

This time, a sample output might look as follows:

```
Person(name=EJH, attributes={YQJ=CDQVQVL, WTUHE=GWXZ, FAVOURITE_FOOD=XCF, TYLHQJ=GKDIH})
```

In the above example, Instancio generates a map with random values and assigns it directly
via the field since it does not have a setter.
`setFavouriteFood` is also invoked, which adds the `FAVOURITE_FOOD` entry to the map.

#### Using `OnSetMethodUnmatched.IGNORE`

In the next example, we modify the above snippet to set `ON_SET_METHOD_UNMATCHED` to `OnSetMethodUnmatched.IGNORE`:

```java linenums="1" hl_lines="5"
Person person = Instancio.of(Person.class)
    .withSettings(Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE)
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.IGNORE))
    .verbose()
    .create();
```

Since unmatched setters are ignored, the node hierarchy does not have the `<1:Person: setFavouriteFood(String)>` node:

```
<0:Person>
 ├──<1:Person: Map<String, String> attributes>
 │   ├──<2:String>
 │   └──<2:String>
 └──<1:Person: String name; setName(String)>
```

The sample output contains an empty map since the unmatched setter was not invoked:

```
Person(name=NQMNABNBM, attributes={})
```

If we modify the last example to set `Keys.ON_SET_METHOD_NOT_FOUND` to `OnSetMethodNotFound.ASSIGN_FIELD`,
then the `attributes` map will be populated with random entries but no `FAVOURITE_FOOD`.


#### Overloaded unmatched setters

Special care must be taken when a field has more than one setter.
Consider the following class with two `setValue` methods:

```java linenums="1"
class Pojo {
    private int value;

    int getValue() {
        return value;
    }

    void setValue(int value) {
        System.out.println("setValue(int) called with: " + value);
        this.value = value;
    }

    void setValue(double value) {
        System.out.println("setValue(double) called with: " + value);
        this.value = (int) value;
    }
}
```

At first glance, the following example should produce the `value` field set to `123`:

```java linenums="1"
Pojo pojo = Instancio.of(Pojo.class)
    .withSettings(Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE))
    .set(field(Pojo::getValue), 123)
    .verbose()
    .create();
```

However, running the snippet will produce the following output:

```
setValue(int) called with: 123
setValue(double) called with: 7387.1580772
```

This is because `ON_SET_METHOD_UNMATCHED` is set to `OnSetMethodUnmatched.INVOKE`,
which results in two nodes related to the `value` field:

```
<0:Pojo>
 ├──<1:Pojo: int value; setValue(int)>      // field with matching setter
 └──<1:Pojo: setValue(double)>              // unmatched setter without a field
```

Instancio would populate each of these nodes.
Nodes that have fields are populated first, and nodes without fields (unmatched setters) are last.
The reason for this is that unmatched setters often require fields to be initialised before being invoked
(for instance, an unmatched setter might be adding an element to a collection).

Therefore, for the above snippet to produce the expected value of `123`,
the `setValue(double)` method would need to be ignored (or `ON_SET_METHOD_UNMATCHED` set to `OnSetMethodUnmatched.IGNORE`):

```java linenums="1" hl_lines="6"
Pojo pojo = Instancio.of(Pojo.class)
    .withSettings(Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE))
    .set(field(Pojo::getValue), 123)
    .ignore(setter(Pojo.class, "setValue", double.class))
    .create();
```
!!! attention ""
    <lnum>6</lnum> Because the method is overloaded, the selector `setter(Pojo.class, "setValue", double.class)`
    must be used to specify the parameter explicitly, instead of the more concise `setter(Pojo::setValue)`.

### Initialised Fields

The setting `Keys.OVERWRITE_EXISTING_VALUES` controls whether Instancio can overwrite initialised fields
with random values. "Initialised" is defined as having a non-default value. Default values are:

- `0` for `byte`, `short`, `int`, `long`, `float`, `double`, and `char`
- `false` for `boolean`
- `null` for `Object`

Below are a few examples of how this setting
works using the following class (getters and setters omitted):

```java linenums="1"
class Foo {
    String value = "initial";
}
```

By default, `OVERWRITE_EXISTING_VALUES` is set to `true`.
As a result, Instancio overwrites initialised fields with random values.

```java linenums="1" hl_lines="3" title="Default behaviour"
Foo foo = Instancio.create(Foo.class);

// Sample output: Foo[value=VEQHJ]
```

When `OVERWRITE_EXISTING_VALUES` is set to false `false`, the initialised value is preserved.

```java linenums="1" hl_lines="2 5" title="Preserve initialised values"
Foo foo = Instancio.of(Foo.class)
    .set(Keys.OVERWRITE_EXISTING_VALUES, false)
    .create();

// Output: Foo[value=initial]
```

Finally, regardless of the `OVERWRITE_EXISTING_VALUES` setting, initialised values can be overwritten using a selector.

```java linenums="1" hl_lines="3 6" title="Overwrite initialised value using a selector"
Foo foo = Instancio.of(Foo.class)
    .set(Keys.OVERWRITE_EXISTING_VALUES, false)
    .set(field(Foo::getValue), "Hello")
    .create();

// Output: Foo[value=Hello]
```

## Maximum Depth Setting

This setting controls the maximum depth for populating an object.
Instancio will populate values up to the maximum depth.
Beyond that, values will be `null` unless the maximum depth is set to a higher value.

The count starts from the root object, which is at depth 0.
Children of the root object are at depth 1, grandchildren at depth 2, and so on.
The default value is defined by the `Keys.MAX_DEPTH` setting key.

The primary reasons for modifying this setting are:

- **To improve the performance.**

    The performance may be inadequate when generating data for large complex classes.
    Consider reducing the maximum depth value in such cases
    (and also, using `ignore()` to exclude certain objects).

- **To generate data beyond the default maximum depth.**

    If the default maximum depth is not sufficient to fully populate an object,
    consider increasing the value.

### Modifying Maximum Depth

The setting can be set to a custom value using one of the following options,
from lowest to highest precedence.

Using `instancio.properties` to define a new global maximum depth:

```properties
max.depth=15
```

Using {{Settings}} with `Keys.MAX_DEPTH` key.

```java
Settings settings = Settings.create().set(Keys.MAX_DEPTH, 15);
Person person = Instancio.of(Person.class)
    .withSettings(settings)
    .create();
```

Using the API method {{withMaxDepth}}:

```java
Person person = Instancio.of(Person.class)
    .withMaxDepth(15)
    .create();
```

## Cyclic Objects

Data models often have circular relationships. A common example is a one-to-many relationship among JPA entities.
Consider the following example where each `OrderItem` references the `Order` to which it belongs
(getters and setters omitted for brevity):

```java linenums="1"
class Order {
    Long id;
    List<OrderItem> items;
}

class OrderItem {
    Long id;
    Order order;
}
```

The default behaviour of Instancio is to terminate cycles with a `null` reference.
For example, the following snippet will produce `OrderItem.order` references set to `null`:

```java linenums="1"
Order order = Instancio.create(Order.class);

// Sample output:
// Order(id=2132, items=[OrderItem(id=9318, order=null), OrderItem(id=6077, order=null)])
```

It is, however, possible to set a back-reference to the root object instead of generating `null`.
One way to accomplish this is using the [`assign()`](#using-assign) API:

```java linenums="1"
Order order = Instancio.of(Order.class)
    .assign(valueOf(root()).to(OrderItem::getOrder))
    .create();

assertThat(order.getItems()).allSatisfy(item ->
    assertThat(item.getOrder()).isSameAs(order));
```

An alternative option is to assign back-references automatically using the `Keys.SET_BACK_REFERENCES` setting.
The following snippet will produce the same result as the example above:

```java linenums="1" hl_lines="1 4"
Settings settings = Settings.create().set(Keys.SET_BACK_REFERENCES, true);

Order order = Instancio.of(Order.class)
    .withSettings(settings)
    .create();
```
!!! attention ""
    <lnum>1</lnum> Note that this setting must be enabled explicitly using `Settings` or `instancio.properties`


When this setting is enabled, Instancio will set the `OrderItem.order` reference to a previously generated
`Order` instance. In this example it happens to be the root object.

It should be noted that in certain cases, enabling `SET_BACK_REFERENCES` may produce unwanted results.
Consider the following example of creating an `OrderItem`:

```java linenums="1"
OrderItem item = Instancio.of(OrderItem.class)
    .withSettings(Settings.create().set(Keys.SET_BACK_REFERENCES, true))
    .create();
```

This will produce the following objects:

``` mermaid
graph LR
  A[OrderItem] --> B[Order];
  B --> C["List&lt;OrderItem&gt;"];
```

where **all** elements of `List<OrderItem>` are the **same instance** of the `OrderItem`.


# Cartesian Product

!!! info "This is an experimental API available since version `4.0.0`"

The following methods are the entry points for generating the Cartesian product:

``` java linenums="1" title="Cartesian Product API"
Instancio.ofCartesianProduct(Class<T> type)
Instancio.ofCartesianProduct(TypeTokenSupplier<T> supplier)
Instancio.ofCartesianProduct(Model<T> model)
```

Inputs can be specified using the following method:

```java
with(TargetSelector, Object...)
```

As an example, consider the snippet below.

```java linenums="1"
record Widget(String type, int num) {}

List<Widget> results = Instancio.ofCartesianProduct(Widget.class)
    .with(field(Widget::type), "FOO", "BAR", "BAZ")
    .with(field(Widget::num), 1, 2, 3)
    .create();
```

This will produce a list containing 9 results in lexicographical order:

```python
[Widget[type=FOO, num=1],
 Widget[type=FOO, num=2],
 Widget[type=FOO, num=3],
 Widget[type=BAR, num=1],
 Widget[type=BAR, num=2],
 Widget[type=BAR, num=3],
 Widget[type=BAZ, num=1],
 Widget[type=BAZ, num=2],
 Widget[type=BAZ, num=3]]
```

### Limitations

The selector passed to the `with()` method must match a single target.
For example, the target cannot be a collection element:

```java linenums="1"
record Widget(String type, int num) {}
record Container(List<Widget> widgets) {}

List<Container> results = Instancio.ofCartesianProduct(Container.class)
    .with(field(Widget::type), "FOO", "BAR", "BAZ")
    .with(field(Widget::num), 1, 2, 3)
    .create();
}
```

The above will produce an error with a message: `"no item is available to emit()"`.


# Bean Validation

Instancio can generate valid data based on Bean Validation annotations.
This is an experimental feature and is disabled by default.

The feature can be enabled via {{Settings}} using `Keys.BEAN_VALIDATION_ENABLED`,
or globally, using `instancio.properties`:

```properties
bean.validation.enabled=true
```

Instancio supports annotations from:

- `jakarta.validation.constraints`
- `javax.validation.constraints`
- `org.hibernate.validator.constraints`

It will generate data based on the constraints, depending on what is available on the classpath.
Instancio does not provide the dependencies transitively.

=== "`jakarta.validation`"
    ```xml
    <dependency>
        <groupId>jakarta.validation</groupId>
        <artifactId>jakarta.validation-api</artifactId>
        <version>${jakarta-validation-api-version}</version>
    </dependency>
    ```
=== "`javax.validation`"
    ```xml
    <dependency>
        <groupId>javax.validation</groupId>
        <artifactId>validation-api</artifactId>
        <version>${javax-validation-api-version}</version>
    </dependency>
    ```
=== "`hibernate`"
    ```xml
    <dependency>
        <groupId>org.hibernate.validator</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>${hibernate-validator-version}</version>
    </dependency>
    ```

By default, Instancio reads annotations from fields. The behaviour can be customised to read annotations from
getters instead. This can be done using the `Keys.BEAN_VALIDATION_TARGET` setting or `instancio.properties`:

```properties
bean.validation.target=GETTER
```

## Supported Annotations

The list of supported annotations is provided below.
Anything not listed is unsupported, including `*.List` annotations.

### Jakarta or Javax

- `@AssertFalse`
- `@AssertTrue`
- `@DecimalMax`
- `@DecimalMin`
- `@Digits` (`fraction()` is supported by `BigDecimal`, but not `float` or `double`)
- `@Email`
- `@Future` (not supported by `MonthDay`)
- `@FutureOrPresent` (delegates to `Future`)
- `@Max`
- `@Min`
- `@Negative`
- `@NegativeOrZero`
- `@NotBlank` (delegates to `NotEmpty`)
- `@NotEmpty`
- `@NotNull`
- `@Past` (not supported by `MonthDay`)
- `@PastOrPresent` (delegates to `Past`)
- `@Positive`
- `@PositiveOrZero`
- `@Size`

### Hibernate

#### Standard

- `@CreditCardNumber`
- `@DurationMin`
- `@DurationMax`
- `@EAN`
- `@ISBN`
- `@Length`
- `@LuhnCheck` (`ignoreNonDigitCharacters` is ignored, only digits are generated)
- `@Mod10Check` (`ignoreNonDigitCharacters` is ignored, only digits are generated)
- `@Mod11Check` (`ignoreNonDigitCharacters` is ignored, only digits are generated)
- `@Range`
- `@UniqueElements`
- `@URL` (`protocol`, `host`, `port` are supported; `regexp` not supported)
- `@UUID`

#### Polish

- `@NIP`
- `@PESEL`
- `@REGON`

# JPA

In addition to [Bean Validation](#bean-validation), Instancio supports generating data
based on JPA `@Column` annotation. This is an experimental feature available from version `3.3.0`.
It is disabled by default and can be enabled via {{Settings}} using `Keys.JPA_ENABLED`,
or globally, using `instancio.properties`:

```properties
jpa.enabled=true
```

In addition, `jakarta` or `javax` API must be present on the classpath for the feature to be activated.
Instancio does not provide the dependency transitively:

```xml
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>${jakarta-persistence-api-version}</version>
</dependency>
```

or

```xml
<dependency>
    <groupId>javax.persistence</groupId>
    <artifactId>javax.persistence-api</artifactId>
    <version>${javax-persistence-api-version}</version>
</dependency>
```

## Supported Attributes

The following `@Column` attributes are suported:

- `precision` -  supported by `BigDecimal` fields (with limitations described below)
- `scale` - supported by `BigDecimal` fields
- `length` - supported by `String` fields

#### Limitations

If used with `Keys.BEAN_VALIDATION_ENABLED`, Bean Validation annotations take precedence.
For instance, the `precision` attribute is not honoured if a field is annotated with any of the following
Bean Validation/Hibernate Validator annotations:

- `@Min`, `@Max`
- `@DecimalMin`, `@DecimalMax`
- `@Negative`, `@NegativeOrZero`
- `@Positive`, `@PositiveOrZero`
- `@Range`

To illustrate with an example, consider the following field declaration:

```java linenums="1"
@Column(precision = 5, scale = 3)
@Min(1)
@Max(7)
private BigDecimal value;
```

`precision = 5, scale = 3` implies a range of `[10.000, 99.999]`,
whereas the `@Min` and `@Max` limit the range to `[1, 7]`.
Since the `@Min` and `@Max` annotations take precedence, the `precision` attribute will be ignored,
and the generated `value` will be between `1.000` and `7.000`, inclusive, and have the specified scale of `3`.

# Quickcheck

!!! warning "`instancio-quickcheck` is an experimental module"

Instancio's data generation capabilities make it a perfect fit for property-based testing, a flavour of writing test cases inspired by [QuickCheck: a lightweight tool for random testing of Haskell programs](https://dl.acm.org/doi/10.1145/351240.351266) paper. This style focuses on automation of testing of program properties using random input generation. Here is a sneak peek on a simple test case that uses Instancio's property-based testing experimental support.

```java linenums="1"
@DisplayName("Test that each person has valid age")
@Property(samples = 100)
public void age(@ForAll Person p) {
    assertThat(i.age()).isGreaterThan(0);
}
```

The test case verifies that one of the properties of the `Person` type (in this case `age` property) should be a strictly positive number for any generated `Person` instance. To ensure that each generated `Person` instance satisfies this condition, a custom property generation strategy can be provided:

```java linenums="1"
@DisplayName("Test that each person has valid age")
@Property(samples = 100)
public void age(@ForAll("persons") Person p) {
    assertThat(i.age()).isGreaterThan(0);
}

public Arbitrary<Person> persons() {
    return Arbitrary
        .fromStream(Stream.generate(() -> Instancio
            .of(Person.class)
            .generate(Select.field(Person::age), gen -> gen.ints().min(1).max(150))
            .create()));
}
```

At the moment, Instancio's property-based testing offers only basic capabilities:

 - the `@Property` annotation denotes an individual test case with the possibility to set the number of samples to generate for each property in question (the default value is `1000`)
 - the `@ForAll` annotation on test case method argument denotes the individual property to generate samples against with the possibility to provide a custom generation strategy

The `Arbitrary<?>` interface hooks in custom generator strategies. The test engine inspects all accessible instance methods of the test class to find out the candidates for the properties under test (using the generator name as a method name and expecting `Arbitrary<?>` instance as a return value).

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
    <lnum>8</lnum> The passed-in settings instance will override default settings.

!!! attention "Range settings auto-adjust"
    When updating range settings, such as `COLLECTION_MIN_SIZE` and `COLLECTION_MAX_SIZE`,
    the range bound is auto-adjusted if the new minimum is higher than the current maximum, and vice versa.


The {{Keys}} class defines a _property key_ for every key object, for example:

- `Keys.COLLECTION_MIN_SIZE` -> `"collection.min.size"`
- `Keys.STRING_ALLOW_EMPTY`  -> `"string.allow.empty"`

Using these property keys, configuration values can also be overridden using a properties file.


## Overriding Settings Using a Properties File

Default settings can be overridden using `instancio.properties`.
Instancio will automatically load this file from the root of the classpath.
The following listing shows all the property keys that can be configured.


```properties linenums="1" title="Sample configuration properties" hl_lines="1 4 11 29 30 35 44 55"
array.elements.nullable=false
array.max.length=6
array.min.length=2
array.nullable=false
bigdecimal.scale=2
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
fail.on.error=false
float.max=10000
float.min=1
float.nullable=false
integer.max=10000
integer.min=1
integer.nullable=false
jpa.enabled=false
long.max=10000
long.min=1
long.nullable=false
map.keys.nullable=false
map.values.nullable=false
map.max.size=6
map.min.size=2
map.nullable=false
max.depth=8
mode=STRICT
hint.after.generate=POPULATE_NULLS_AND_DEFAULT_PRIMITIVES
overwrite.existing.values=true
assignment.type=FIELD
on.set.field.error=IGNORE
on.set.method.error=ASSIGN_FIELD
on.set.method.not.found=ASSIGN_FIELD
on.set.method.unmatched=IGNORE
setter.style=SET
seed=12345
set.back.references=false
short.max=10000
short.min=1
short.nullable=false
string.allow.empty=false
string.field.prefix.enabled=false
string.max.length=10
string.min.length=3
string.nullable=false
string.type=ALPHABETIC
subtype.java.util.Collection=java.util.ArrayList
subtype.java.util.List=java.util.ArrayList
subtype.java.util.Map=java.util.HashMap
subtype.java.util.SortedMap=java.util.TreeMap
```

!!! attention ""
    <lnum>1,11,29-30</lnum> The `*.elements.nullable`, `map.keys.nullable`, `map.values.nullable` specify whether Instancio can generate `null` values for array/collection elements and map keys and values.<br/>
    <lnum>4</lnum> The other `*.nullable` properties specifies whether Instancio can generate `null` values for a given type.<br/>
    <lnum>35</lnum> Specifies the mode, either `STRICT` (default) or `LENIENT`. See [Selector Strictness](#selector-strictness).<br/>
    <lnum>44</lnum> Specifies a global seed value.<br/>
    <lnum>55</lnum> Properties prefixed with `subtype` are used to specify default implementations for abstract types, or map types to subtypes in general.
    This is the same mechanism as [subtype mapping](#subtype-mapping), but configured via properties.


## Settings Precedence

Instancio layers settings on top of each other, each layer overriding the previous ones.
This is done in the following order:

1. `Settings.defaults()`
1. Settings from `instancio.properties`
1. Settings injected using `@WithSettings` annotation when using `InstancioExtension` (see [Settings Injection](#settings-injection))
1. Settings supplied using the builder API's {{withSettings}} method

In the absence of any other configuration, Instancio uses defaults as returned by `Settings.defaults()`. If `instancio.properties` is found at the root of the classpath, it will override the defaults. Finally, settings can also be overridden at runtime using `@WithSettings` annotation or {{withSettings}} method. The latter takes precedence over everything else.

# Instancio Service Provider Interface

The {{InstancioServiceProvider}} interface allows customising how objects are created and populated.
It defines the following methods, which return `null` by default and can be overridden as needed:

- `GeneratorProvider getGeneratorProvider()`
- `AnnotationProcessor getAnnotationProcessor()`
- `SetterMethodResolver getSetterMethodResolver()`
- `TypeResolver getTypeResolver()`
- `TypeInstantiator getTypeInstantiator()`

An implementation of `InstancioServiceProvider` can be registered by creating
a file named `org.instancio.spi.InstancioServiceProvider` under `/META-INF/services/`.
The file should contain the fully-qualified name of the implementation class, for example:

``` title="/META-INF/services/org.instancio.spi.InstancioServiceProvider"
org.example.InstancioServiceProviderImpl
```

## `GeneratorProvider`

This interface allows mapping a `Node` to a `GeneratorSpec`:

```java
interface GeneratorProvider {
    GeneratorSpec<?> getGenerator(Node node, Generators gen);
}
```

The `Generators` parameter provides access to built-in generators.
This is the same class that is provided to the `generate()` method.

The `Node` object represents a class and/or field in the node hierarchy,
for example:

- `Person.dateOfBirth` node will have the target class `LocalDate` and the `java.lang.reflect.Field` `dateOfBirth`
- the collection element node of `List<Phone>` will have the target class `Phone` and a `null` field

### Use Case

The main use case for implementing the `GeneratorProvider` is to have generators resolved automatically.
For example, the following implementation generates maximum string length based on the `length` attribute
of the JPA `@Column` annotation.

!!! info "More on JPA"
    From version `3.3.0` Instancio supports this functionality out-of-the-box. See the [JPA section](#jpa) for more details.

```java linenums="1" hl_lines="13 16"
import javax.persistence.Column;

public class GeneratorProviderImpl implements GeneratorProvider {

    @Override
    public GeneratorSpec<?> getGenerator(final Node node, final Generators gen) {
        Field field = node.getField();
        Class<?> targetClass = node.getTargetClass();

        if (targetClass == String.class && field != null) {
            Column column = field.getDeclaredAnnotation(Column.class);
            if (column != null) {
                return gen.string().maxLength(column.length());
            }
        }
        return null;
    }
}
```
!!! attention ""
    <lnum>13</lnum> Set maximum string length based on the `length` attribute.<br/>
    <lnum>16</lnum> Returning `null` means a value will be generated using built-in generators.<br/>

Assuming the following entity:

```java linenums="1"
class Phone {
    @Column(length = 3)
    String countryCode;

    @Column(length = 15)
    String number;
}
```

Calling `Instancio.create()` should produce string lengths that conform to the schema:

```java linenums="1"
Phone phone = Instancio.create(Phone.class);
assertThat(phone.getCountryCode()).hasSizeLessThanOrEqualTo(3);
assertThat(phone.getNumber()).hasSizeLessThanOrEqualTo(15);
```

Using the API methods `set()`, `supply()`, or `generate()`
it is still possible to override values using the API if needed:

```java linenums="1"
Phone phone = Instancio.of(Phone.class)
    .generate(field(Phone::getNumber), gen -> gen.string().length(20))
    .create();
```

## `AnnotationProcessor`

!!! info "This is an experimental API available since version `4.5.0`"

This interface allows processing custom annotations:

```java
interface AnnotationProcessor {
    // no methods to implement
}
```

It has no methods to implement. Instead, it relies on user-defined methods marked with
the `@AnnotationHandler` annotation. The accepted signatures for `@AnnotationHandler` methods are:

```java
@AnnotationHandler
void example(Annotation annotation, GeneratorSpec<?> spec, Node node)

@AnnotationHandler
void example(Annotation annotation, GeneratorSpec<?> spec)
```

The `annotation` and `spec` parameters can be subtypes `java.lang.annotation.Annotation`
and `org.instancio.generator.GeneratorSpec`, respectively.
The `node` parameter is optional, and can be omitted if it's not needed.

### Use Case

The main use case for implementing the `AnnotationProcessor` is to customise generated values
based on custom annotations. Let's assume we have the following annotations and a POJO:

```java linenums="1"  hl_lines="13 14"
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Hex {
    int length();
}

@Retention(RetentionPolicy.RUNTIME)
public @interface MapWithKeys {
    String[] value();
}

class Pojo {
    @MapWithKeys({"foo", "bar"})
    private Map<String, @Hex(length = 10) String> map;
}
```
!!! attention ""
<lnum>13</lnum> The `@MapWithKeys` annotation specifies that a given `Map` must contain given keys.<br/>
<lnum>14</lnum> the `@Hex` annotation denotes that a string must be a hexadecimal value of the specified `length()`.<br/>


Our `Pojo` declares a `Map` that should contain hexadecimal strings as values.
The keys can be arbitrary strings, but the map should contain `foo` and `bar`.
To achieve this, we can implement an `AnnotationProcessor` as shown below.

```java linenums="1" hl_lines="3 4 8 9"
public class AnnotationProcessorImpl implements AnnotationProcessor {

    @AnnotationHandler
    void withKeys(MapWithKeys annotation, MapGeneratorSpec<String, ?> mapSpec) {
        mapSpec.withKeys(annotation.values());
    }

    @AnnotationHandler
    void hexString(Hex annotation, StringGeneratorSpec stringSpec) {
        stringSpec.hex().length(annotation.length());
    }
}
```
!!! attention ""
<lnum>3,8</lnum> methods must be annotated with `@AnnotationHandler`.<br/>
<lnum>4,9</lnum> the third parameter (`Node`) is omitted as it's not needed in this example.<br/>

Instancio will use methods marked with `@AnnotationHandler` to process the annotations.
The first argument must be the annotation, and the second is the `GeneratorSpec`
applicable to the annotated type (to find the specific spec interface, see the
`org.instancio.generator.specs` package [Javadoc](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/generator/specs/package-summary.html)
or the `org.instancio.generators.Generators` class).

Once the above is in place, the following snippet:

```java
Pojo pojo = Instancio.create(Pojo.class);
```

should produce output similar to:

```
Pojo[map={bar=2F5E92847B, NGJKQBQ=25F845DB67, foo=824D732CAA, ODDVXUPESM=2EDB5EB46A}]
```

It should be noted that the `AnnotationProcessor` can only be used to customise
existing generators. To define a custom `Generator` for a given annotation,
use a custom [`GeneratorProvider`](#generatorprovider).

## `SetterMethodResolver`

This interface is for providing custom resolution of setter methods from fields
when `Keys.ASSIGNMENT_TYPE` is set to `AssignmentType.METHOD`:

```java
interface SetterMethodResolver {
    Method getSetter(Node node);
}
```

### Use Case

Out of the box, Instancio can resolve setter methods from fields assuming method names
follow standard naming conventions (see [Assignment Settings](#assignment-settings) for details).
A custom `SetterMethodResolver` implementation allows tests to use `AssignmentType.METHOD`
with applications that follow non-standard naming conventions.

Consider the following example, where the POJO has a field prefixed with an underscore.
The goal is to populate the POJO via the setter method as it contains some logic:

```java linenums="1"
class Pojo {
    private String _value;

    public String getValue() {
        return _value;
    }

    public void setValue(String value) {
        this._value = value.length() + ":" + value;
    }
}
```

However, the field name `_value` does not map to method name `setValue()`.
Therefore, the setter will not be resolved, and the value will be populated
via field assignment as a fallback. A custom `SetterMethodResolver` can be
implemented to handle this case as shown below.

```java linenums="1" title="Resolves setter method names for fields prefixed with an underscore " hl_lines="14 16"
public class SetterMethodResolverImpl implements SetterMethodResolver {

    @Override
    public Method getSetter(Node node) {
        Field field = node.getField();

        // discard the '_' prefix
        char[] ch = field.getName().substring(1).toCharArray();
        ch[0] = Character.toUpperCase(ch[0]);

        String methodName = "set" + new String(ch);

        return Arrays.stream(field.getDeclaringClass().getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst()
                .orElse(null);
    }
}
```
!!! attention ""
    <lnum>14</lnum> For brevity, matching is done by name only, ignoring parameter types.<br/>
    <lnum>16</lnum> Returning `null` means built-in method resolvers will be used as a fallback.<br/>

With the above in place, the `Pojo` can be created as follows:

```java linenums="1"
Settings settings = Settings.create()
    .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD);

Pojo pojo = Instancio.of(Pojo.class)
    .withSettings(settings)
    .create();

// Sample output: Pojo[_value="5:EVHKT"]
```

## `TypeResolver`

This interface allows mapping a type to a subtype:

```java
interface TypeResolver {
    Class<?> getSubtype(Class<?> type);
}
```

The subtype mapping uses the same mechanism as the `subtype()` API method.

### Use Case

The primary use case for implementing the `TypeResolver` is to resolve subtypes automatically.
By default, Instancio does not resolve the implementation class if given an abstract type.
Instead, the implementation class must be specified manually.
This can be done either via the `subtype()` method:

```java linenums="1"
Animal animal = Instancio.of(Animal.class)
    .subtype(all(Animal.class), Cat.class)
    .create();
```

or via `Settings`:

```java linenums="1"
Settings settings = Settings.create()
    .mapType(Animal.class, Cat.class);

Animal animal = Instancio.of(Animal.class)
    .withSettings(settings)
    .create();
```

Using `TypeResolver`, the subtype can be resolved automatically:

```java linenums="1"
public class TypeResolverImpl implements TypeResolver {

    @Override
    public Class<?> getSubtype(final Class<?> type) {
        if (type == Animal.class) {
            return Cat.class;
        }
        return null;
    }
}
```

Then calling `Instancio.create()` should use the specified subtype:

```java linenums="1"
Animal animal = Instancio.create(Animal.class);
assertThat(animal).isExactlyInstanceOf(Cat.class);
```

!!! info "Scanning the Classpath"
    Using `TypeResolver` it is also possible to resolve implementation classes
    via classpath scanning, for example, using a third-party library.
    For a sample implementation, see [`type-resolver-sample`](https://github.com/instancio/instancio-samples).

## `TypeInstantiator`

This interface is for providing custom instantiation logic for classes that
Instancio is unable to instantiate out-of-the-box:

```java
interface TypeInstantiator {
    Object instantiate(Class<?> type);
}
```

By default, Instancio attempts to instantiate a class using the default constructor.
If the default constructor is unavailable or fails (for example, the constructor throws an exception),
Instancio will attempt to use a constructor with the least number of parameters and pass in default values.
If the last option also fails, then it resorts to JDK-specific approaches, such as using `sun.misc.Unsafe`.
There may be situations where all the listed options fail, which would result in `null` values
being generated. Using `TypeInstantiator` allows plugging in custom instantiation logic.

# Troubleshooting

## Debugging

Instancio uses [SLF4J](https://www.slf4j.org) for logging. Most of the messages are logged
at `DEBUG` or `TRACE` level. Logging information can be useful when Instancio produces an error
or does not generate expected values.

In addition to logging, the builder API provides the `verbose()` method that outputs
current settings as well as the internal model containing the node hierarchy to
standard output. For example:

```java linenums="1" hl_lines="2"
List<Phone> result = Instancio.ofList(Phone.class)
    .verbose()
    .create();
```

will produce (ignoring settings output for brevity):

``` linenums="1"
// snip...

### Node hierarchy

Format: <depth:class: field>

<0:List>
 └──<1:Phone>
     ├──<2:Phone: String countryCode>
     └──<2:Phone: String number>

 -> Node max depth ........: 2
 -> Model max depth .......: 8
 -> Total nodes ...........: 4
 -> Seed ..................: 2699444350509138652
```
!!! attention ""
    <lnum>12</lnum> maximum depth of the object.<br/>
    <lnum>13</lnum> configured maximum depth up to which values will be generated.<br/>
    <lnum>14</lnum> total number of nodes the root type contains.<br/>
    <lnum>15</lnum> seed that was used to populate the data.<br/>

The `verbose()` method can be particularly useful when working with deep, complex class
hierarchies that contain many fields, collections, and cyclic relationships.
For example, some APIs such as `assign()` and `emit()` require that a given selector
matches exactly one target. The node hierarchy can be used to troubleshoot cases
where the selector happens to match more than one target. The visual representation
makes it easier to fine-tune the selector by specifying selector [scope](#selector-scopes)
or [depth](#selector-depth).


## Error Handling

The default behaviour of Instancio is to fully populate an object, up to a certain depth.
In case of internal errors, Instancio will still attempt to return an object, though some
fields or collections may not be fully populated.

Consider the following somewhat contrived example:

```java linenums="1" title="An impossible Set"
Set<Boolean> set = Instancio.ofSet(Boolean.class)
    .size(10)
    .create();
```

Since  it is not possible to create a `Set` of 10 booleans, Instancio will generate a set of size 2,
containing values `true` and `false`. However, internally, this use case will produce an exception
that is suppressed by default. At `DEBUG` log level, Instancio will report the following:

```
Suppressed error because Keys.FAIL_ON_ERROR (fail.on.error) is disabled.
-> To propagate the error, set Keys.FAIL_ON_ERROR setting to true.
-> To display the stack trace, run in verbose() mode or with TRACE logging.

org.instancio.exception.InstancioException: Internal error occurred creating an object.

Internal errors are suppressed by default and
can be ignored if not applicable to the current test
 -> at com.example.ExampleTest(ExampleTest.java:123)

Reason: unable to populate Collection of size 10: class Set<Boolean> (depth=0)
```

As the message suggests, the `Keys.FAIL_ON_ERROR` setting can be enabled to propagate
internal errors. This can be done via the `Settings` API or configuration file:

```properties title="instancio.properties"
fail.on.error=true
```

# Seed

Before creating an object, Instancio initialises a random seed value.
This seed value is used internally by the pseudorandom number generator, that is, `java.util.Random`.
Instancio ensures that the same instance of the random number generator is used throughout object creation, from start to finish.
This means that Instancio can reproduce the same object again by using the same seed.
This feature allows reproducing failed tests (see the section on [reproducing tests with JUnit](#reproducing-failed-tests)).

In addition, Instancio handles classes like `UUID` and `LocalDateTime`,
where a minor difference in values can cause an object equality check to fail.
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

## Specifying Seed Value

By default, if no custom seed is specified, Instancio generates a random seed value.
Therefore, each execution results in different outputs.
This behaviour can be overridden by specifying a custom seed using any of the options below.
These are ranked from highest to lowest precedence:

1. {{withSeed}}  method of the builder API
1. {{withSettings}} or {{withSetting}} method of the builder API using `Keys.SEED`
1. `@WithSettings` annotations (requires [`InstancioExtension`](#junit-jupiter-integration))
1. `@Seed` annotation  (requires [`InstancioExtension`](#junit-jupiter-integration))
1. `instancio.properties` file (see [Global Seed](#global-seed) for details)
1. random seed

Precedence rules are summarised in the following table, where each number represents a seed value,
and `R` represents a random seed.

| Random<br>seed | `.properties` | `@Seed` | `@WithSettings` | `.withSettings()` | `.withSeed()` | Actual<br>seed |
|:--------------:|:-------------:|:-------:|:---------------:|:-----------------:|:-------------:|:--------------:|
|       R        |       5       |    4    |        3        |         2         |     **1**     |     **1**      |
|       R        |       5       |    4    |        3        |       **2**       |       -       |     **2**      |
|       R        |       5       |    4    |      **3**      |         -         |       -       |     **3**      |
|       R        |       5       |  **4**  |        -        |         -         |       -       |     **4**      |
|       R        |     **5**     |    -    |        -        |         -         |       -       |     **5**      |
|     **R**      |       -       |    -    |        -        |         -         |       -       |     **R**      |


### `@WithSettings` seed

When a seed is specified via `@WithSettings`, all objects generated
within the test class are created using the given seed.
For this reason, if two objects of the same type are created, both instances will be identical, for example:

```java linenums="1"
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SEED, 12345L);

    @Test
    void example() {
        Pojo pojo1 = Instancio.create(Pojo.class);
        Pojo pojo2 = Instancio.create(Pojo.class);

        assertThat(pojo1).isEqualTo(pojo2);
    }
}
```

This is because the above snippet is equivalent to:

```java linenums="1"
@ExtendWith(InstancioExtension.class)
class ExampleTest {

    @Test
    void example() {
        Settings settings = Settings.create()
                .set(Keys.SEED, 12345L);

        Pojo pojo1 = Instancio.of(Pojo.class)
                .withSettings(settings)
                .create();

        Pojo pojo2 = Instancio.of(Pojo.class)
                .withSettings(settings)
                .create();

        assertThat(pojo1).isEqualTo(pojo2);
    }
}
```



### Global Seed

A global seed can be specified in `instancio.properties` using the `seed` property key:

```properties
seed=9283754
```

There are some important differences in how the global seed works depending on whether
tests declare the `InstancioExtension`.

#### Global Seed Without the `InstancioExtension`

When tests are run without the extension, the same `Random` instance is used across all test classes and methods.
Therefore, generated data is affected by the order in which test methods are run.

Let's assume the configured seed in the properties file produces the following output if `test1` is run first:

```java linenums="1"
class ExampleTest {
    @Test
    void test1() {
        String s1 = Instancio.create(String.class); // Output: "FCGVRXSUU"
    }

    @Test
    void test2() {
        String s2 = Instancio.create(String.class); // Output: "OCNVRBX"
    }
}
```

If `test2` were to run first, then `s2` will be `FCGVRXSUU` and `s1` will be `OCNVRBX`.

In short, when using the global seed _without_ the `InstancioExtension`, the generated data is static for:

- a given test method, or
- a set of test methods that are run in a particular order

For this reason, using a global seed without the extension is not recommended,
as it makes it harder to reproduce the data in case of test failure.

#### Global Seed With the `InstancioExtension`

When using the extension, each test method gets its own instance of `Random` initialised
with the seed from the properties file. As a result, generated data is not affected by the order
in which test methods are run.

For example, the following snippet will always produce the same output:

```java linenums="1" hl_lines="1"
@ExtendWith(InstancioExtension.class)
class ExampleTest {
    @Test
    void T1() {
        String t1 = Instancio.create(String.class); // Output: "FCGVRXSUU"
    }

    @Test
    void T2() {
        String t2 = Instancio.create(String.class); // Output: "FCGVRXSUU"
    }
}
```

## Getting the Seed Value

Sometimes it is necessary to get the seed value that was used to generate the data. One such example
is for reproducing failed tests. If you are using JUnit 5, the seed value is reported automatically
using the `InstancioExtension` (see [JUnit Jupiter integration](#junit-jupiter-integration)). If you are using JUnit 4, TestNG,
or Instancio standalone, the seed value can be obtained by calling the `asResult()` method of the builder API.
This returns a `Result` containing the created object and the seed value that was used to populate its values.


``` java linenums="1" title="Example of using asResult()"
Result<Person> result = Instancio.of(Person.class).asResult();
Person person = result.get();
long seed = result.getSeed(); // seed value that was used for populating the person
// snip...
```


# JUnit Jupiter Integration

Instancio supports JUnit 5 via the {{InstancioExtension}} and can be used in combination with extensions from other testing frameworks.
The extension adds a few useful features, such as

- the ability to use {{InstancioSource}} with `@ParameterizedTest` methods,
- injection of custom settings using {{WithSettings}},
- and most importantly support for reproducing failed tests using the {{Seed}} annotation.

## Reproducing Failed Tests

Since using Instancio validates your code against random inputs on each test run,
having the ability to reproduce failed tests with previously generated data becomes a necessity.
Instancio supports this use case by reporting the seed value of a failed test in the failure message using JUnit's `publishReportEntry` mechanism.

### Data Guarantees

The library guarantees that the same data is generated for a given seed **and** version of the library.
For this reason, making assertions against generated values is highly discouraged to avoid breaking changes.
For example, the following test suffers from tight coupling with the random number generator implementation
and may break when upgrading to a newer version of Instancio.

```java linenums="1" hl_lines="5-7"
Person person = Instancio.of(Person.class)
    .withSeed(1234)
    .create();

// Not recommended!
assertThat(person.getName()).isEqualTo("VEONRGF");
assertThat(person.getPhoneNumbers()).hasSize(3);
```

### Seed Lifecycle in a JUnit Jupiter Test

Instancio initialises a seed value before each test method.
This seed value is used for creating all objects during the test method's execution
unless another seed is specified explicitly using the {{withSeed}} method.

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
    <lnum>5</lnum> Instancio initialises a random seed value, for example, `8276`.<br/>
    <lnum>7</lnum> Uses seed value `8276`.<br/>
    <lnum>10</lnum> Uses the supplied seed value `123`.<br/>
    <lnum>13</lnum> Uses seed value `8276`.<br/>
    <lnum>15</lnum> Seed value `8276` goes out of scope.

Even though `person1` and `person3` are created using the same seed value of `8276`,
they are actually distinct objects, each containing different values. This is because
the same instance of the random number generator is used throughout the test method.

It should be noted that if the test fails, only the seed generated internally is reported
(`8276` in the above example). Seeds specified via `withSeed()` or `Settings` are not reported
since the value is already known.


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
Test method 'verifyShippingAddress' failed with seed: 8532 (seed source: random seed)
```

The _seed source_ indicates whether the Instancio extension generated a random seed
or used a seed provided by the user. The possible seed sources are listed below
(see also [Specifying seed value](#specifying-seed-value)):

- seed specified via `Settings` annotated with `@WithSettings`
- seed specified using the `@Seed` annotation
- random seed (default behaviour when an explicit seed is not specified)

!!! warning "Seeds specified using {{withSeed}} or {{withSettings}} methods are not reported by the Instancio extension."

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
    <lnum>11</lnum> Settings passed to the builder method take precedence over the injected settings.

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

It should be noted that using `@InstancioSource` has one important limitations in that generated objects cannot be customised.
The only option is to customise generated values using [settings injection](#settings-injection).
However, it is not possible to customise values on a per-field basis like with the builder API.

# Appendix

## Built-in Generators

The following list of generators is available via the [`generate()`](#using-generate) method.

Most of these generators are also available through the standalone [`Gen`](#creating-simple-values) class.

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
├── emit()
├── enumOf(Class<E>)
├── enumSet(Class<E>)
│
├── oneOf(Collection<T>)
├── oneOf(T...)
│
├── optional()
│
├── math()
│   ├── bigInteger()
│   └── bigDecimal()
│
├── net()
│   ├── email()
│   ├── ip4()
│   ├── uri()
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
│   ├── calendar()
│   ├── date()
│   ├── duration()
│   ├── instant()
│   ├── localDate()
│   ├── localDateTime()
│   ├── localTime()
│   ├── offsetDateTime()
│   ├── offsetTime()
│   ├── period()
│   ├── sqlDate()
│   ├── timestamp()
│   ├── year()
│   ├── yearMonth()
│   └── zonedDateTime()
│
├── text()
│   ├── csv()
│   ├── loremIpsum()
│   ├── pattern(String)
│   └── uuid()
│
├── checksum()
│   ├── luhn()
│   ├── mod10()
│   └── mod11()
│
├── finance()
│   └── creditCard()
│
├── id()
│   ├── ean()
│   ├── isbn()
│   ├── can()
│   │   └── sin()
│   ├── pol()
│   │   ├── nip()
│   │   ├── pesel()
│   │   └── regon()
│   └── usa()
│       └── ssn()
│
└── spatial()
    └── coordinate()
        ├── lat()
        └── lon()
```

!!! info "The `io().file()` and `nio().path()` generators can save files on the filesystem."

