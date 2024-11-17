---
hide:
  - navigation
  - toc
---

# Creating object templates using Models

## Introduction

One of the challenges of setting up data for different test cases is creating objects in different states.
Test classes will often have helper methods with arguments for creating objects.
Even worse, common setup code will sometimes be duplicated across test methods.
Manual data setup can get quite complicated as classes get bigger and more complex.
This is especially true for classes with many relationships.
In this article, we will show how Models can be used to solve these challenges.

!!! note "GitHub link to the sample project is provided at the end of the article."

## Testing background

We will be testing a service that converts a college `Applicant` to an Avro class `ApplicantAvro`.
The `Applicant` class is provided below.
The `ApplicantAvro` class is auto-generated using an Avro schema, but essentially it has the same structure.

``` java
class Applicant {
    Long id;
    String firstName;
    String middleName;
    String lastName;
    Integer age;
    Grade grade;
    Address address;
}

enum Grade { A, B, C, D, F }

class Address {
    String street;
    String city;
    String country;
    String postalCode;
}
```

Our conversion service `ApplicantToAvroMapper` requires that an applicant is 18-25 years old and has achieved grade A or B.
If those conditions are met, it constructs an Avro object and returns it as the result.

``` java
public class ApplicantToAvroMapper {

    public ApplicantAvro toAvro(final Applicant applicant) {
        Validate.isTrue(applicant.getAge() >= 18 && applicant.getAge() <= 25,
                "Applicant must be between 18 and 25 years of age");

        Validate.isTrue(applicant.getGrade() == Grade.A || applicant.getGrade() == Grade.B,
                "Applicant's grade must be either A or B");

        return ApplicantAvro.newBuilder()
                .setFirstName(applicant.getFirstName())
                .setMiddleName(applicant.getMiddleName())
                .setLastName(applicant.getLastName())
                .setAge(applicant.getAge())
                .setGrade(applicant.getGrade().toString())
                .setAddress(addressToAvro(applicant.getAddress()))
                .build();
    }

    private AddressAvro addressToAvro(Address address) {
        return address == null ? null : AddressAvro.newBuilder()
                .setStreet(address.getStreet())
                .setCity(address.getCity())
                .setPostalCode(address.getPostalCode())
                .setCountry(address.getCountry())
                .build();
    }

```

In addition, the Avro schema requires all the fields to be non-null except `middleName` and `postalCode`.
If a required field is `null`, the Avro builder will throw an `AvroRuntimeException`.

## Test cases

Good tests should cover all branches of conditional logic.
This gives us greater confidence in our code and oftentimes uncovers issues we may have overlooked.
Although our service is very simple, it still presents a few scenarios that need to be covered.


| Test case                            | Expectation                                  |
| ------------------------------------ |----------------------------------------------|
| Successful conversion                | An `ApplicantAvro` object                    |
| Applicant is under 18                | Validation error: `IllegalArgumentException` |
| Applicant is over 25                 | Validation error: `IllegalArgumentException` |
| Applicant has grade C, D, or E       | Validation error: `IllegalArgumentException` |
| Applicant missing required data      | Conversion error: `AvroRuntimeException`     |
| Applicant missing optional data      | An `ApplicantAvro` object                    |


### Successful scenario

Testing the successful scenario *"should be"* straightforward:

 - construct a **valid** object (age 18-25, grade A or B, and all required fields are not null)
 - pass it to the method under test
 - verify result has expected values

However, if we want to be thorough, then we need to verify as many valid states as *reasonably* possible.
For example, is the range inclusive or exclusive?
Our range is inclusive, so we should have a test for an applicant aged 18, and another 25. We don't really need to verify the numbers in the middle.
This will ensure our service implemented the numeric bounds check correctly.
We should also test an applicant with grade A, and another with grade B.
Finally, we should verify that our service is not rejecting an applicant if optional data (such as `middleName`) is missing.

### Application validation

Testing **validation** errors also *"should not"* be too difficult:

 - construct an **invalid** object
 - pass it to the method under test
 - verify the method throws the expected exception

Here again we should cover as many bases as *reasonably* possible. This includes
applicants aged 17 and 26 and those with grades C, D, and F.

### Schema validation

Finally, we have schema validation implemented within auto-generated Avro classes.
For example, `firstName` is required in our [schema](https://github.com/instancio/instancio-samples/blob/main/instancio-models-sample/src/main/avro/applicant.avsc),
therefore passing `null` to `setFirstName()` will throw an `AvroRuntimeException`.
We should have a test for that.
Ideally, we should verify this with **every** required field being `null`. Doing so has two benefits:

1. it guarantees that we will not introduce unintended changes when modifying the schema in the future;
1. if we switch from Avro to another format, the test will ensure the same constraints are enforced in the new schema.

However, most real world projects will rarely go this far due to time constraints or simply because it is too much effort.
We will look at how to implement such a test fairly easily using Instancio.


## Writing tests

### Implementing successful scenario tests

Let's start by testing a valid applicant. Typically, it will be implemented as follows:


```java hl_lines="35 36"
@Test
@DisplayName("Valid applicant should be successfully converted to Avro")
void verifyValidApplicantAvro() {
    // Given
    Applicant applicant = createValidApplicant();

    // When
    ApplicantAvro applicantAvro = mapper.toAvro(applicant);

    // Then
    assertThat(applicantAvro).isNotNull();
    assertThat(applicantAvro.getFirstName()).isEqualTo(applicant.getFirstName());
    assertThat(applicantAvro.getMiddleName()).isEqualTo(applicant.getMiddleName());
    assertThat(applicantAvro.getLastName()).isEqualTo(applicant.getLastName());
    assertThat(applicantAvro.getAge()).isEqualTo(applicant.getAge());
    assertThat(applicantAvro.getGrade()).isEqualTo(applicant.getGrade().name());
    assertThat(applicantAvro.getAddress()).isNotNull();

    Address address = applicant.getAddress();
    AddressAvro addressAvro = applicantAvro.getAddress();
    assertThat(addressAvro.getStreet()).isEqualTo(address.getStreet());
    assertThat(addressAvro.getCity()).isEqualTo(address.getCity());
    assertThat(addressAvro.getCountry()).isEqualTo(address.getCountry());
    assertThat(addressAvro.getPostalCode()).isEqualTo(address.getPostalCode());
}

private static Applicant createValidApplicant() {
    Address address = new Address();
    address.setStreet("street");
    address.setCity("city");
    address.setCountry("country");
    address.setPostalCode("postal-code");

    Applicant applicant = new Applicant();
    applicant.setFirstName("first-name");
    applicant.setLastName("last-name");
    applicant.setAge(18);
    applicant.setGrade(Grade.A);
    applicant.setAddress(address);

    return applicant;
}
```

We create a valid applicant and pass it to the method under test.
However, note the highlighted lines. The above test does not verify other successful scenarios we outlined earlier.
To do so, we can add parameters using JUnit 5 `@ParameterizedTest` to construct an applicant with different values.
The updated code is shown below. JUnit will automatically convert parameters to correct types, including the enum.


``` java hl_lines="9"
@CsvSource({
        "18, A",
        "18, B",
        "25, A",
        "25, B"
})
@ParameterizedTest
void verifyValidApplicantAvro(int age, Grade grade) {
    Applicant applicant = createValidApplicant(age, grade);
    ApplicantAvro applicantAvro = mapper.toAvro(applicant);
    // Remaining code is the same
}

private static Applicant createValidApplicant(int age, Grade grade) {
    Applicant applicant = new Applicant();
    applicant.setAge(age);
    applicant.setGrade(grade);
    // Remaining code is the same

    return applicant;
}
```

Finally, we need to verify that the service does not reject an applicant if optional fields `middleName` and `postalCode` are `null`.
At this point, the test is already starting to get more complicated and forcing us to make decisions on how to proceed.
Possible options are:

1. update the parameterized test to include `middleName` and `postalCode`
1. do not populate optional fields in `createValidApplicant()`
1. refactor `ParameterizedTest` to use `@MethodSource` instead of `@CsvSource`
1. create a new test method

**Option 1** seems messy. Adding parameters makes the code harder to read and maintain. What if we need to add more optional parameters in the future?

!!! quote "*Clean Code*, Robert C. Martin"
    The ideal number of arguments for a function is zero (niladic). Next comes one (monadic), followed closely by two (dyadic). Three arguments (triadic) should be avoided where possible. More than three (polyadic) requires very special justification - and then shouldn’t be used anyway.


**Option 2** is also not ideal. If the optional values are `null` then we are no longer testing the mapping for those fields.
Both, expected and actual, would always be `null` giving us a false sense of confidence.

``` java
assertThat(applicantAvro.getMiddleName()).isEqualTo(applicant.getMiddleName());
assertThat(addressAvro.getPostalCode()).isEqualTo(address.getPostalCode());
```

**Option 3** would be to modify the test method.
Maybe instead of passing individual arguments to the parameterized test, we pass an `Applicant` object as an argument.
This can be implemented using `@MethodSource` as the source of arguments.

``` java hl_lines="1 3 8"
@MethodSource("validApplicants")
@ParameterizedTest
void verifyValidApplicantAvro(Applicant applicant) {
    ApplicantAvro applicantAvro = mapper.toAvro(applicant);
    // Remaining code is the same
}

private static Stream<Arguments> validApplicants() {
    Applicant applicant1 = createValidApplicant(18, Grade.A);
    Applicant applicant2 = createValidApplicant(18, Grade.B);

    // Set optional fields to null
    Applicant applicant3 = createValidApplicant(25, Grade.B);
    applicant3.setMiddleName(null);
    applicant3.getAddress().setPostalCode(null);

    return Stream.of(
            Arguments.of(applicant1),
            Arguments.of(applicant2),
            Arguments.of(applicant3)
            // etc...
    );
}
```

**Option 4** is to simply create a new test method as shown below.
Since we are adding a new test method,
we will need refactor the assertions into a separate `assertApplicant()` method to avoid duplicating them.


``` java
@Test
@DisplayName("Applicant with missing optional data should not be rejected")
void applicantWithMissingOptionalData() {
    // Given
    Applicant applicant = createValidApplicant(18, Grade.A);
    applicant.setMiddleName(null);
    applicant.getAddress().setPostalCode(null);

    // When
    ApplicantAvro applicantAvro = mapper.toAvro(applicant);

    // Then
    assertApplicant(applicant, applicantAvro);
}

private static void assertApplicant(final Applicant applicant, final ApplicantAvro applicantAvro) {
    assertThat(applicantAvro.getFirstName()).isEqualTo(applicant.getFirstName());
    assertThat(applicantAvro.getMiddleName()).isEqualTo(applicant.getMiddleName());
    // Remaining assertions...
}
```

#### Improving the test

As we saw, test code can start to get more complicated and time-consuming very quickly even with our simple service.
Let's see how we can improve it. We are going to replace the data setup method by delegating object creation to Instancio.
An `Applicant` can be created simply as follows:

``` java
Applicant applicant = Instancio.create(Applicant.class);
```

However, since we need a valid applicant with a certain age range and grades, we need to specify those parameters as well.
Below is our test and the updated `createValidApplicant()` method:

``` java hl_lines="4 10 11"
@Test
@DisplayName("Valid applicant should be successfully converted to Avro")
void verifyValidApplicantAvro() {
    Applicant applicant = Instancio.create(createValidApplicant());
    // Remaining code is the same
}

private static Applicant createValidApplicant() {
    return Instancio.of(Applicant.class)
            .generate(field("age"), gen -> gen.ints().range(18, 25))
            .generate(all(Grade.class), gen -> gen.oneOf(Grade.A, Grade.B))
            .create();
}
```

Notice that we no longer need to use `@ParameterizedTest` or worry about populating a valid `Applicant` object in different states.
When the test runs, Instancio will generate an `Applicant` based on the specified parameters.
Since the object is randomly generated, our test will automatically cover different permutations of valid applicants.

We also need to verify that when optional fields are `null`, the method under test still works as expected.
By default, Instancio generates non-null values. Therefore, we need to specify which fields are nullable.
This can be done by tweaking `Applicant` creation as follows:

``` java hl_lines="3-5"
private static Applicant createValidApplicant() {
    return Instancio.of(Applicant.class)
            .withNullable(all(
                    field("middleName"),
                    field(Address.class, "postalCode")))
            .generate(field("age"), gen -> gen.ints().range(18, 25))
            .generate(all(Grade.class), gen -> gen.oneOf(Grade.A, Grade.B))
            .create();
```

The `withNullable()`  method will randomly generate `null` values for the specified fields.
The `all()` selector is a convenience method for grouping multiple selectors together.

Finally, JUnit 5 offers the `@RepeatedTest` annotation which can be used for executing a test multiple times.
We could use this annotation to ensure a greater number of data permutations our test is run against.
It might be unnecessary given the limited range of inputs in this simple example,
however, it is a good option to have at disposal when working with larger data sets.


``` java hl_lines="1"
@RepeatedTest(10)
void verifyValidApplicantAvro() { ... }
```

This completes our "success scenario" test case. Next we will test the validation rules.

### Implementing application validation tests

As a reminder, the service throws an exception if an `Applicant` does not meet the following requirements:

``` java
public ApplicantAvro toAvro(Applicant applicant) {

    Validate.isTrue(applicant.getAge() >= 18 && applicant.getAge() <= 25,
            "Applicant must be between 18 and 25 years of age");

    Validate.isTrue(applicant.getGrade() == Grade.A || applicant.getGrade() == Grade.B,
            "Applicant's grade must be either A or B");

    // ...snip...
}
```

Just as before, this requires constructing `Applicant` objects in different states.
This time we will solve the problem using an Instancio `Model`.
A model can be thought of as a template for generating objects.
To create a model, the `createValidApplicant()` method can be modified as follows:

``` java linenums="1" hl_lines="1 8"
private static Model<Applicant> createValidApplicantModel() {
    return Instancio.of(Applicant.class)
            .withNullable(all(
                    field("middleName"),
                    field(Address.class, "postalCode")))
            .generate(field("age"), gen -> gen.ints().range(18, 25))
            .generate(all(Grade.class), gen -> gen.oneOf(Grade.A, Grade.B))
            .toModel();
}
```

The highlighted lines are the modifications.
First, the method signature now returns `Model<Applicant>` (and the method was renamed to `createValidApplicantModel()`).
The second change is instead of calling `create()`, which returns an `Applicant` instance, now we call `toModel()`.
We can now use this model instance as a template to generate `Applicant` instances as follows:

``` java
Applicant applicant = Instancio.create(createValidApplicantModel());
```

Using the valid applicant model, we can also construct **invalid** applicants by overriding certain parameters.
For example, age validation can be verified by using the valid applicant model and applying invalid age parameters as shown below.


``` java linenums="1"
@Test
@DisplayName("Validation should fail if applicant is under 18 or over 25")
void applicantAgeValidation() {
    Applicant applicant = Instancio.of(createValidApplicantModel())
            .generate(field("age"), gen -> gen.oneOf(17, 26))
            .create();

    assertThatThrownBy(() -> mapper.toAvro(applicant))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Applicant must be between 18 and 25 years of age");
}
```

The single test above covers applicants below and above the required age range.
Grade validation can be tested in a similar manner by verifying grades C, D, and F.

``` java
@Test
@DisplayName("Validation should fail if applicant's grade is lower than B")
void applicantGradeValidation() {
    Applicant applicant = Instancio.of(createValidApplicantModel())
            .generate(all(Grade.class), gen -> gen.oneOf(Grade.C, Grade.D, Grade.F))
            .create();

    assertThatThrownBy(() -> mapper.toAvro(applicant))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Applicant's grade must be either A or B");
}
```

### Implementing schema validation tests

The last test left to implement is to verify the schema.
We want to ensure that if any required field is set to `null`, the service throws the `AvroRuntimeException`.
Essentially, we only want to test one required `null` field at a time.
In order to achieve this goal, we will again use our applicant `Model` as the starting point and then nullify a required field.
We will repeat this process for each required field, as shown in the following method.


``` java linenums="1" hl_lines="17"
@Test
@DisplayName("Should throw AvroRuntimeException if any of the required fields is null")
void shouldThrowAvroRuntimeExceptionIfRequiredDataIsMissing() {
    Selector[] requiredFields = {
            field(Applicant::getFirstName),
            field(Applicant::getLastName),
            field(Address::getStreet),
            field(Address::getCity),
            field(Address::getCountry)
    };

    // Set each of these to null individually, so that only one required field is null at a time
    Arrays.stream(requiredFields).forEach(selector -> {
        // Given
        Applicant applicant = Instancio.of(createValidApplicantModel())
                .set(selector, null)
                .create();

        // Then
        assertThatThrownBy(() -> mapper.toAvro(applicant))
                .as("Expected %s to be required", selector)
                .isInstanceOf(AvroRuntimeException.class);
    });
}
```

Being able to select fields programmatically offers allows us to implement this type of test fairly easily.
Without library support, this type of testing would be tedious to implement.
It would require manually calling setters with a `null` value or implementing similar logic using reflection.
Neither of these options are practical.

## Conclusion

This concludes an overview of models. As we saw, writing tests can get tricky even when the class under test is fairly simple.
Populating objects can be time-consuming. In addition, constructing objects in different states for different test cases
presents its own challenges.

Using a data generator alleviates some of the above challenges.
The random nature of the data allows us to test a wider range of conditions.
This can reduce the number of test methods required to verify different outcomes.
Test methods themselves become simpler. For example, in a lot of cases we can eliminate the need for `@ParameterizedTest`.
In addition, the data setup code is more concise since we no longer need to manually populate objects.
The data setup code itself is easier to maintain and more flexible to change.


## Source code

Source code from this article is available as a Maven project from the
[instancio-samples](https://github.com/instancio/instancio-samples) repository:

``` sh
git clone https://github.com/instancio/instancio-samples.git
cd instancio-samples/instancio-models-sample
mvn package
```
