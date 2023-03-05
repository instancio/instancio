### Hibernate 5 Validator tests

Tests using version 5 of Hibernate Validator, which has a smaller set of constraints.

This verifies that class-not-found error is not thrown if a constraint
(e.g. `@UUID`) is not provided by the older version of the validator.
