```properties linenums="1" title="Sample instancio.properties"
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
fail.on.max.depth.reached=false
fail.on.max.generation.attempts.reached=true
fill.type=POPULATE_NULLS_AND_DEFAULT_PRIMITIVES
float.max=10000
float.min=1
float.nullable=false
ignore.field.name.regexes=foo.*,bar.*
instancio.source.samples=100
integer.max=10000
integer.min=1
integer.nullable=false
jpa.enabled=false
long.max=10000
long.min=1
long.nullable=false
map.keys.nullable=false
map.max.size=6
map.min.size=2
map.nullable=false
map.values.nullable=false
max.depth=8
max.generation.attempts=1000
mode=STRICT
hint.after.generate=APPLY_SELECTORS
assignment.type=FIELD
on.feed.property.unmatched=FAIL
on.set.field.error=IGNORE
on.set.method.error=ASSIGN_FIELD
on.set.method.not.found=ASSIGN_FIELD
on.set.method.unmatched=IGNORE
setter.style=SET
overwrite.existing.values=true
bean.validation.enabled=false
bean.validation.target=FIELD
seed=12345
set.back.references=false
short.max=10000
short.min=1
short.nullable=false
string.allow.empty=false
string.max.length=10
string.min.length=3
string.nullable=false
string.case=UPPER
string.type=ALPHABETIC
subtype.java.util.Collection=java.util.ArrayList
subtype.java.util.List=java.util.ArrayList
subtype.java.util.Map=java.util.HashMap
subtype.java.util.SortedMap=java.util.TreeMap
```

---

- The `*.elements.nullable`, `map.keys.nullable`, and `map.values.nullable` properties determine whether
  Instancio can generate `null` values for array/collection elements, as well as map keys and values.
- Other `*.nullable` properties control whether Instancio is permitted to generate `null` values for a specific type.
- The `mode` property sets the operation to either `STRICT` (default) or `LENIENT`.
  For more details, see [Selector Strictness](#selector-strictness).
- Defines a global seed value for consistent data generation.
- Properties prefixed with `subtype` define default implementations for abstract types or map types to specific subtypes.
- This follows the same mechanism as [subtype mapping](#subtype-mapping), but is configured via property files.