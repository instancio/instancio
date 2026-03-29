---
description: "Instancio support for generating Protocol Buffer messages"
---

!!! info "Experimental API `@since 6.0.0`"

The `instancio-protobuf` module supports generating Protobuf messages.
See [Getting Started](getting-started.md#instancio-protobuf) for dependency information.

Instancio populates proto object instances via `Message.Builder` setter methods
and invokes `build()` to create an instance.
Internally, it uses [`AssignmentType.METHOD`](user-guide.md#assignment-settings) to achieve this,
however this setting is not exposed to user code.

## Supported Field Types

Instancio supports most of the protobuf field types except the following:

| Field type               | Behaviour                                                  |
|--------------------------|------------------------------------------------------------|
| `oneof`                  | Fields remain at proto defaults (empty string, zero, etc.) |
| `google.protobuf.Any`    | Field is empty                                             |
| `google.protobuf.Struct` | Struct is present but contains no fields                   |

## Creating Messages

Proto messages are created with the standard {{Instancio}} APIs:

```java linenums="1"
Person person = Instancio.create(Person.class);
List<Person> list = Instancio.createList(Person.class);
```

All supported field types are populated, including nested messages, repeated fields, and maps.
Self-referential fields are terminated with `getDefaultInstance()` to prevent infinite recursion.

## Selectors

The following selectors are supported with protobuf messages:

- `Select.all(Class)`
- `Select.field(GetMethodSelector)`

For example:

```java linenums="1"
Person result = Instancio.of(Person.class)
    .set(all(Gender.class), Gender.FEMALE)
    .set(field(Person::getName), "Alice")
    .create();
```

Using `allInts()` or `all(int.class)` is not recommended because it
may affect generation of enum values, which are represented as integers in protobuf classes.

Setter selectors on message builders are not supported. For example, the following will throw an unused selector error:

```java
Person result = Instancio.of(Person.class)
    .withSetting(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
    .set(setter(Person.Builder::setName), "Alice")
    .create();
```

Predicate selectors should work, however, please keep in mind that proto fields have trailing underscores
when matching fields by name.

Proto has internal housekeeping fields which Instancio prunes from its node graph.
This is done on a best-effort basis but the behaviour may vary across different versions of `protobuf-java`.
To inspect the graph, you can enable the verbose mode: `Instancio.of().verbose().create()`.

The standard wrapper types (`StringValue`, `Int32Value`, `BoolValue`, etc.) are treated as single-field messages.
For example, `Select.allStrings()` will also apply to `StringValue` fields.

!!! info "See [Selectors](user-guide.md#selectors) in the user guide for the full selector reference."

## Customising Messages

All standard customisation methods (`set`, `generate`, `supply`, `ignore`, `assign`, `onComplete`)
work with proto messages.
The [user guide](user-guide.md#customising-objects) covers these in full; this section documents
proto-specific behaviour.

`org.instancio.protobuf.GenProto` provides protobuf-specific generator specs, for example `Duration` and `Timestamp`:

```java linenums="1"
Person person = Instancio.of(Person.class)
    .generate(field(Person::getDateOfBirth), GenProto.timestamp().past())
    .create();
```

### Null and Default Values

Protobuf fields cannot hold `null`.
When Instancio would otherwise produce `null` (via `ignore()` or `withNullable()`) it
substitutes the proto default for that field's type:

| Target                              | Resulting value                                     |
|-------------------------------------|-----------------------------------------------------|
| Scalar field (e.g. `string`)        | Empty string `""`, zero, `false`, empty bytes       |
| `enum` field                        | First declared constant (proto default, number `0`) |
| Nested message field                | `MessageType.getDefaultInstance()`                  |
| Element type of `repeated` or `map` | Empty list / map                                    |

```java linenums="1"
Person person = Instancio.of(Person.class)
    .ignore(field(Person::getName))
    .withNullable(all(Address.class))
    .create();
```
!!! attention ""
    <lnum>1</lnum> name will produce an empty string `""`<br/>
    <lnum>2</lnum> address might produce `Address.getDefaultInstance()`<br/>

!!! info "`withNullable(root())` still works as expected - the root object itself may be `null`"
