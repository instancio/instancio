Instancio
=========
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=instancio_instancio&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=instancio_instancio)

Instancio is a library for auto-generating test data.
It uses reflection to create and populate objects with random data:

Note: this is a work in progress and the API is still unstable. 

### Examples

#### Create an instance of `Person` and populate it with random data.
```
Person person = Instancio.of(Person.class).create();
```

#### Specify custom value generators for fields.
```
Person person = Instancio.of(Person.class)
    .supply(field("fullName"), () -> "Homer Simpson") // Person.name
    .supply(field(Address.class, "phoneNumber"), () -> new PhoneNumber("+1", "123-45-67"))
    .create();
 ``` 

#### Allow `null` values to be generated.

```
Person person = Instancio.of(Person.class)
    .withNullable(allStrings()) // all strings is nullable
    .withNullable(all(Date.class)) // all dates are nullable
    .withNullable(field(Address.class, "street")) // Address.street is nullable
    .create();
```

#### Ignore certain fields or classes

```
Person person = Instancio.of(Person.class)
    .ignore(field("age")) // Person.age will be ignored
    .ignore(field(Address.class, "city")) // Address.city will be ignored
    .ignore(all(Date.class)) // all dates will be ignored
    .create();
```

#### Creating generic classes

Option 1: supply type parameters
```
// Note: this will have an unchecked assignment warning
// To avoid the warning, use option 2
List<Person> person = Instancio.of(List.class)
    .withTypeParameters(Person.class)
    .create();
```

Option 2: using a `TypeToken`

```
List<Person> person = Instancio.of(new TypeToken<List<Person>>() {}).create();
```
