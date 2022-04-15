This module tests that the annotation processor works in a Gradle build
(i.e. classes are written to the appropriate build directory).

- Gradle: `build/generated/sources/...`
- Maven: `target/generated-sources/...`

The module will most likely not compile in an IDE without some fiddling.
It can be simply excluded by removing it from the IDE after importing the project.
