# aformvalidator

`aformvalidator` is an annotation-driven validation library that generates form validation states
from your data classes and exposes simple error reporting per field via lightweight state objects.

## Overview

- Annotate a data class with `@FormState` to generate a validator type and helpers for checking
  fields and computing error states.
- Each field becomes a `ParamState<T, E>` carrying the current value, a list of validation
  conditions, and derived `error`/`isError` parameters.
- Built-in annotations (e.g. `@NotBlank`, `@Email`, `@Min`, `@Max`, `@Size`, `@ToNumber`,
  `@DependsOn`) provide common validations and map to a sealed `ValidationError` hierarchy.

For a practical real-world example take a look
at [sample-app](./sample-app/src/main/java/io/github/brendonmendicino/aformvalidator/sampleapp)

---

## Installation

To install `aformvalidator` you need to include annotations and the processor in your dependencies

```kotlin
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    implementation("io.github.brendon-mendicino:aformvalidator-core:<version>")
    implementation("io.github.brendon-mendicino:aformvalidator-annotation:<version>")
    implementation("io.github.brendon-mendicino:aformvalidator-processor:<version>")
    ksp("io.github.brendon-mendicino:aformvalidator-processor:<version>")
}
```

## CoreTypes

- ParamState
    - Holds the value, a list of `(T) -> E?` conditions, and a `used` flag to control when `isError`
      should surface.
    - `error` evaluates conditions and returns the first error (or `null`), and `isError` becomes
      true only when the param was interacted with (`used == true`) and an error exists.
    - `update(value)` returns a new state with `used = true`, which is handy for UI flows.
- ValidationError
    - Sealed class of error types used by built‑ins and custom validators: `NotBlank`,
      `Size(min,max)`, `Pattern(regex)`, `Email`, `Min`, `Max`, `MinDouble`, `MaxDouble`,
      `ToNumber(numberClass)`, and `NotNull`.
- Validator and ValidatorCond
    - `@Validator(value = <ValidatorClass>::class, errorType = <E>::class)` ties an annotation to a
      validator that exposes conditions: `List<(T) -> E?>`.
    - Built‑in annotations declare their own validator classes and feed them into `@Validator`.

---

## Code generation

- Mark your model with `@FormState` to generate the corresponding validator wrapper, enabling calls
  like `myForm.toValidator()` in examples shown in built‑ins.
- Generated state exposes one `ParamState` per property with wired conditions derived from applied
  annotations.

---

## Built‑in annotations

List of build-in annotations.

| Annotation       | Key parameters                                              | Description                                                                                                  |
|------------------|-------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------|
| FormState        | —                                                           | Marks a data class so the validator wrapper is generated (enables myForm.toValidator()).                     |
| NotBlank         | —                                                           | Validates that a String is not blank after trim.                                                             |
| Email            | pattern: String (optional)                                  | Validates a String as an email; supports custom regex via pattern.                                           |
| Min              | min: Long                                                   | Ensures a Number is ≥ min; null values pass; repeatable.                                                     |
| Max              | max: Long                                                   | Ensures a Number is ≤ max; null values pass; repeatable.                                                     |
| Size             | min: Int = 0, max: Int = Int.MAX_VALUE                      | Ensures a Collection size is within [min, max]; repeatable.                                                  |
| ToNumber         | numberClass: KClass<out Number>                             | Validates a String can be parsed to the specified Number type; repeatable.                                   |
| DependsOn        | dependencies: Array<String>                                 | Declares that a property depends on other props to keep ParamState.used/validity in sync for derived values. |
| Validator (meta) | value: KClass<out ValidatorCond<...>>, errorType: KClass<*> | Links an annotation to its validator class and the error type it returns.                                    |

Below are built‑ins, their behavior, and the examples embedded in their KDocs.

### NotBlank

Validates that a `String` has at least one non‑whitespace character after trimming.

Example:

```kotlin
@FormState
data class Person(@NotBlank val name: String? = null)

var person = Person().toValidator()
println(person.name.error) // NotBlank
person = person.copy(name = person.name.update("   "))
println(person.name.error) // NotBlank
person = person.copy(name = person.name.update("pippo"))
println(person.name.error) // null
```

### Email

Validates a `String` as an email via regex; you can override the pattern.

Example:

```kotlin
@FormState
data class Person(
    val name: String,
    @Email
    val personal: String,
    // Custom pattern
    @Email(pattern = """\w+@mydomain\.com""")
    val work: String
)
```

### Min

Ensures a numeric value is not less than `min`; nullable numbers pass when `null`.

Example:

```kotlin
@FormState
data class Point(
    @Min(0)
    val x: Int,
    @Min(0)
    val y: Int?,
)

var state = Point(-10, null).toValidator()
println(state.x.error) // Min(min=0)
state = state.copy(x = state.x.update(5))
println(state.errors.firstOrNull()) // null
```

### Size

Validates a collection's size within `[min, max]` bounds.

Example:

```kotlin
data class BoundList(
    @Size(2, 10)
    val list: List<Int>,
)

val empty = BoundList(listOf())
println(empty.list.error) // Size(min=2, max=10)
val withElements = BoundList(listOf(1, 2, 3))
println(withElements.list.error) // null
```

---

## Error mapping

Each built‑in returns one of the `ValidationError` variants, such as `ValidationError.NotBlank`,
`ValidationError.Email`, `ValidationError.Min`, `ValidationError.Max`, `ValidationError.Size`, or
`ValidationError.ToNumber`, which you can surface in UI or map to messages.

---

## Creating your own validators

This library lets you author custom annotations by pairing them with a validator class that emits
`conditions: List<(T) -> E?>`.

1. Implement a validator class
    - Accept the same constructor parameters your annotation will accept, and expose `conditions`.
    - Each condition returns `null` on success or an error value (e.g., `String` or a custom sealed
      type) on failure.

```kotlin
// The constructor parameters are the same that the annotation takes.
class LogMessageValidator(
    val sections: Int,
    val timestamp: String,
    val allowedModules: Array<String>,
    val maxLogLen: Int,
) : ValidatorCond<String, String> {
    override val conditions: List<(String) -> String?> = listOf(
        { input -> if (input.split(" ").size == sections) null else "Section check failed!" },
        { input ->
            runCatching { SimpleDateFormat(timestamp).parse(input.split(" ")[0]) }
                .let { if (it.isSuccess) null else "Timestamp check failed!" }
        },
        { input ->
            if (allowedModules.contains(input.split(" ")[1])) null
            else "Module check failed!"
        },
        { input ->
            if (input.split(" ")[2].length <= maxLogLen) null
            else "Log length check failed!"
        },
    )
}
```

2. Create your annotation and link it with `@Validator`
    - Use the `@Validator(value = YourValidator::class, errorType = YourErrorType::class)`
      meta‑annotation on your custom annotation.

```kotlin
@Validator(
    value = LogMessageValidator::class,
    errorType = String::class,
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class LogMessage(
    val sections: Int = 3,
    val timestamp: String = "YYYY-MM-DD",
    val allowedModules: Array<String> = ["main", "test"],
    val maxLogLen: Int = 100,
)
```

3. Consume in a `@FormState` model
    - Annotate your form field with `@LogMessage(...)`, convert your instance with the generated
      helper, and read `param.isError` or `param.error`.

Design tips:

- Keep each condition focused; short, composable checks are easier to test.
- Choose an error type that fits your rendering pipeline (e.g., a sealed `ValidationError` or plain
  strings).
- Prefer non‑throwing checks; conditions should return error values instead of raising exceptions.

---

## Interaction and UI

- Drive field state with `ParamState.update(...)` to flip `used = true` and enable `isError`
  visibility on user interaction.
- For derived fields, use `@DependsOn` so changes in source properties mark dependents as
  used/validated correctly.



