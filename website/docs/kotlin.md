---
description: "Instancio Kotlin API"
---

!!! info "Experimental API `@since 6.0.0`"

The `instancio-kotlin` module provides a Kotlin-native API for creating objects and working with selectors.
See [Getting Started](getting-started.md#instancio-kotlin) for dependency information.

It offers the same functionality as the core Java API, enhanced with Kotlin-specific improvements
such as reified type parameters and Kotlin property references,
which eliminate the need to pass `Class` objects or type tokens explicitly.

The `KInstancio` object is the entry point, mirroring the Java {{Instancio}} class:

```kotlin linenums="1"
// Create a single instance — no Class argument needed
val person: Person = KInstancio.create<Person>()

// Create a generic type — no TypeToken needed
val pairs: List<Pair<String, Long>> = KInstancio.createList<Pair<String, Long>>()

// Use the builder API to customise generated values
val person: Person = KInstancio.of<Person>()
    .generate(KSelect.field(Person::age)) { gen -> gen.ints().range(18, 65) }
    .create()
```

The `KSelect` object provides all selector methods available in the Java {{Select}} class,
with the addition of Kotlin property reference selectors for both `field()` and `scope()`:

```kotlin linenums="1"
// Select a field using a Kotlin property reference
val person: Person = KInstancio.of<Person>()
    .ignore(KSelect.field(Person::address))
    .set(KSelect.allStrings().within(KSelect.scope(Person::homeAddress)), "N/A")
    .create()
```