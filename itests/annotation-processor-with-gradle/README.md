This module that the annotation processor works in a Gradle build
(i.e. classes are written to appropriate build directory).

- Gradle: `build/generated/sources/...`
- Maven: `target/generated-sources/...`

The module doesn't compile in the IDE and can be simply excluded.
